package de.uni_stuttgart.riot.android.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

import de.uni_stuttgart.riot.android.Callback;
import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.communication.AndroidConnectionProvider;
import de.uni_stuttgart.riot.android.communication.ServerConnection;
import de.uni_stuttgart.riot.clientlibrary.ConnectionInformation;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.clientlibrary.UnauthenticatedException;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.AuthenticationResponse;

/**
 * The activity that shows the login form (and, if required, the network settings) and sends the login data to the backend to make
 * connections.
 *
 * @author Dirk Braunschweiger
 * @author Philipp KEck
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    /**
     * Constant for intents to pass the user name.
     */
    public static final String KEY_USERNAME = "username";

    /**
     * Constant for intents to pass a boolean that indicates if the connection settings should be shown initially.
     */
    public static final String KEY_SHOW_CONNECTION_SETTINGS = "showConnectionSettings";

    /**
     * Constant for intents to pass another intent that is used to redirect back to the source activity after the login succeeded.
     */
    public static final String KEY_REDIRECT_INTENT = "redirectIntent";

    private static final int MIN_PORT = 80;
    private static final int MAX_PORT = 65535;
    private static final String TAG = "LoginActivity";

    EditText editUsername;
    EditText editPassword;
    CheckBox chkConnectionSettings;
    LinearLayout pnlConnectionSettings;
    EditText editHost;
    EditText editPort;
    EditText editPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = (EditText) this.findViewById(R.id.acc_edit_username);
        editPassword = (EditText) this.findViewById(R.id.acc_edit_password);
        chkConnectionSettings = (CheckBox) this.findViewById(R.id.acc_chk_connection_settings);
        pnlConnectionSettings = (LinearLayout) this.findViewById(R.id.acc_pnl_connection_settings);
        editHost = (EditText) this.findViewById(R.id.acc_edit_host);
        editPort = (EditText) this.findViewById(R.id.acc_edit_port);
        editPath = (EditText) this.findViewById(R.id.acc_edit_path);

        if (getIntent().hasExtra(KEY_USERNAME)) {
            editUsername.setText(getIntent().getStringExtra(KEY_USERNAME));
        }

        chkConnectionSettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton button, boolean checked) {
                pnlConnectionSettings.setVisibility(checked ? View.VISIBLE : View.GONE);
                if (checked) {
                    ConnectionInformation information = AndroidConnectionProvider.getInstance().loadConnectionInformation(LoginActivity.this);
                    editHost.setText(information.getHost());
                    editPort.setText(Integer.toString(information.getPort()));
                    editPath.setText(information.getRestRootPath());
                }
            }
        });
        chkConnectionSettings.setChecked(getIntent().getBooleanExtra(KEY_SHOW_CONNECTION_SETTINGS, false));

        Button b;
        b = (Button) this.findViewById(R.id.btn_add_acc);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!applyConnectionSettings()) {
                    return;
                }

                final String username = editUsername.getText().toString();
                final String password = editPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, R.string.login_missing_credentials, Toast.LENGTH_LONG).show();
                } else {
                    login(username, password, new Callback<AuthenticationResponse>() {
                        public void callback(AuthenticationResponse response) {
                            Account account = saveAccountAsCurrent(response);
                            if (getIntent().hasExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)) {
                                Intent intent = new Intent();
                                intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
                                intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AuthConstants.ACCOUNT_TYPE);
                                intent.putExtra(AccountManager.KEY_AUTHTOKEN, response.getAccessToken());
                                setAccountAuthenticatorResult(intent.getExtras());
                                setResult(RESULT_OK, intent);
                            } else if (getIntent().hasExtra(KEY_REDIRECT_INTENT)) {
                                Intent intent = (Intent) getIntent().getParcelableExtra(KEY_REDIRECT_INTENT);
                                if (intent.getAction().equals(Intent.ACTION_MAIN)) {
                                    intent.setFlags(0);
                                }
                                startActivity(intent);
                            }
                            finish();
                        }
                    });
                }
            }
        });

    }

    /**
     * Applies the connection settings that the user entered (if they were visible).
     *
     * @return True if successful or no-op, false if the process must be aborted.
     */
    private boolean applyConnectionSettings() {
        if (!chkConnectionSettings.isChecked()) {
            return true;
        }
        final String host = editHost.getText().toString();
        final String port = editPort.getText().toString();
        final String path = editPath.getText().toString();
        if (host.isEmpty() || port.isEmpty() || path.isEmpty()) {
            Toast.makeText(LoginActivity.this, R.string.login_missing_settings, Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            int portNumber = Integer.parseInt(port);
            if (portNumber > MAX_PORT || portNumber < MIN_PORT) {
                Toast.makeText(LoginActivity.this, R.string.login_invalid_port, Toast.LENGTH_LONG).show();
                return false;
            }

            AndroidConnectionProvider.getInstance().setNewConnectionInformation(this, host, portNumber, path);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(LoginActivity.this, R.string.login_invalid_port, Toast.LENGTH_LONG).show();
            return false;
        }

    }

    /**
     * Performs a login for the given user on the server and calls the callback if successful (shows an error toast otherwise). Displays a
     * progress dialog during the login process.
     *
     * @param username
     *            The username.
     * @param password
     *            The password (will not be stored).
     * @param callback
     *            The callback to call when the login was successful.
     */
    private void login(final String username, final String password, final Callback<AuthenticationResponse> callback) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
        dialog.setCancelable(false);
        dialog.show();

        new ServerConnection<AuthenticationResponse>() {
            protected Context getContext() {
                return LoginActivity.this;
            }

            protected AuthenticationResponse executeRequest(ServerConnector serverConnector) throws IOException, RequestException {
                return serverConnector.executeLoginRequest(username, password);
            }

            protected void handleNetworkError(IOException e) {
                dialog.dismiss();
                Log.e(TAG, "Login failed", e);
                chkConnectionSettings.setChecked(true);
                Toast.makeText(LoginActivity.this, R.string.login_network_error, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void handleAuthenticationError(UnauthenticatedException e) {
                dialog.dismiss();
                Log.e(TAG, "Login failed", e);
                Toast.makeText(LoginActivity.this, R.string.login_incorrect, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(AuthenticationResponse result) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, R.string.login_correct, Toast.LENGTH_LONG).show();
                callback.callback(result);
            }
        }.execute();
    }

    /**
     * Ensures that the (possibly newly created) account that corresponds to the credentials in the response is activated in the connection
     * provider.
     *
     * @param response
     *            The authentication response containing username and tokens.
     * @param account
     *            The account that is now used.
     */
    private Account saveAccountAsCurrent(AuthenticationResponse response) {
        Account account = AccountAuthenticator.getOrCreateAccount(getApplicationContext(), response.getUser().getUsername());
        AndroidConnectionProvider.getInstance().saveNewAccountInformation(account, response.getAccessToken(), response.getRefreshToken(), response.getUser());
        return account;
    }


    @Override
    public void onBackPressed() {
        // do nothing
    }

}
