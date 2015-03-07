package de.uni_stuttgart.riot.android;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.account.AndroidUser;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;

/**
 * Created by dirkmb on 2/11/15.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    EditText editUsername;
    EditText editPassword;
    ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = (EditText) this.findViewById(R.id.acc_edit_username);
        editPassword = (EditText) this.findViewById(R.id.acc_edit_password);

        Button b;
        b = (Button) this.findViewById(R.id.btn_add_acc);
        b.setOnClickListener(this);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);

        // check for any existing android account
        Account[] accounts = AndroidUser.getAccounts(this);
        if (accounts == null) {
            //nothing to do, login elements automatically shown
        } else {
            ///verify that the token is valid
            for (Account account : accounts) {
                Log.v("LoginActivity", "Checking login of account" + account);
                new DoCheckLoginToken(this, account).execute();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // do what you want to do when button is clicked
        switch (view.getId()) {
            case R.id.btn_add_acc:
                final String username = editUsername.getText().toString();
                final String password = editPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(view.getContext(), "Please insert username and password.", Toast.LENGTH_LONG).show();
                } else {
                    mDialog.show();
                    Account account = AndroidUser.getAccount(this.getApplicationContext(), username);
                    if (account == null) {
                        account = AndroidUser.createAndroidAccount(username, this.getApplicationContext());
                    }
                    if (account != null) {
                        DoLoginRequest loginRequest = new DoLoginRequest(this, account);
                        loginRequest.execute(username, password);
                    }
                }
                break;
            default:
                throw new IllegalStateException();
        }
    }


    /**
     * @param username
     *            the username which is used as account name, and send back if we got called form the system account manager
     */
    void answerIntent(String username) {
        if (AndroidUser.getAccount(this, username) == null) {
            Bundle extras = this.getIntent().getExtras();
            if (extras != null) {
                AccountAuthenticatorResponse response = extras.getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
                Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.ACCOUNT_TYPE));
                response.onResult(result);
                this.finish();
            }
        } else {
            // TODO account already exists in the system, what to do with the intend?
        }
    }
}

/**
 * The AsycTask for the server communication.
 */
class DoCheckLoginToken extends AsyncTask<String, Integer, Long> {

    LoginActivity loginActivity;
    Account account;

    DoCheckLoginToken(LoginActivity loginActivity, Account account) {
        this.loginActivity = loginActivity;
        this.account = account;
    }

    @Override
    protected Long doInBackground(String[] parameter) {
        RIOTApiClient.getInstance().init(loginActivity, loginActivity.getString(R.string.DEVICE_NAME), account);
        AndroidUser androidUser = new AndroidUser(loginActivity, account.name);
        Long ret = 0L;
        if (androidUser.isLoggedIn()) {
            return 1L;
        }
        return ret;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // setProgressPercent(progress[0]);
    }

    @Override
    protected void onPostExecute(Long result) {
        loginActivity.mDialog.dismiss();
        if (result == 1) {
            Toast.makeText(loginActivity, "Your login was correct.", Toast.LENGTH_LONG).show();
            Intent mainScreen = new Intent(loginActivity, HomeScreen.class);
            //TODO needed some parameters? mainScreen.putExtra("pressedButton", "house");
            loginActivity.startActivity(mainScreen);
        } else {
            Toast.makeText(loginActivity, "Your login was NOT correct.", Toast.LENGTH_LONG).show();
        }
    }
}



/**
 * The AsycTask for the server communication.
 */
class DoLoginRequest extends AsyncTask<String, Integer, Long> {

    LoginActivity loginActivity;
    Account account;

    DoLoginRequest(LoginActivity loginActivity, Account account) {
        this.loginActivity = loginActivity;
        this.account = account;
    }

    @Override
    protected Long doInBackground(String[] parameter) {
        RIOTApiClient.getInstance().init(loginActivity, loginActivity.getString(R.string.DEVICE_NAME), account);
        AndroidUser androidUser = new AndroidUser(loginActivity, account.name);

        Long ret = 0L;
        if (androidUser.isLoggedIn()) {
            return 1L;
        } else if (parameter.length == 2) {
            androidUser.logIn(parameter[0], parameter[1]);
            if (androidUser.isLoggedIn()) {
                ret += 1L;
            }
        }
        return ret;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // setProgressPercent(progress[0]);
    }

    @Override
    protected void onPostExecute(Long result) {
        loginActivity.mDialog.dismiss();
        if (result == 1) {
            Toast.makeText(loginActivity, "Your login was correct.", Toast.LENGTH_LONG).show();
            Intent mainScreen = new Intent(loginActivity, HomeScreen.class);
            //TODO needed some parameters? mainScreen.putExtra("pressedButton", "house");
            loginActivity.startActivity(mainScreen);
        } else {
            Toast.makeText(loginActivity, "Your login was NOT correct.", Toast.LENGTH_LONG).show();
        }
    }
}


