package de.uni_stuttgart.riot.android.account;

import com.sun.org.apache.xpath.internal.operations.And;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.uni_stuttgart.riot.android.ColorPicker;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.MainActivity;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;

/**
 * UI for account creation.
 */
public class AccountFragment extends Fragment implements OnClickListener, ColorPicker.OnColorChangedListener {

    AndroidUser androidUser;
    Calendar cal = null;

    EditText editUsername;
    EditText editPassword;

    private class DoLoginRequest extends AsyncTask<String, Integer, Long> {

        private String username;
        private String password;
        Context mContext;

        DoLoginRequest(String username, String password, Context mContext)
        {
            this.username = username;
            this.password = password;
            this.mContext = mContext;
        }

        protected Long doInBackground(String[] parameter) {
            if(androidUser.logIn(username, password)) {
                return 1L;
            }
            return 0L;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            if(result == 1) {
                Toast.makeText(mContext, "Your login was correct.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, "Your login was NOT correct.", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Initialize the API client. Initialization is not allowed in the main thread.
        final Context ctx = getActivity();
        new Thread() {
            @Override
            public void run() {
                RIOTApiClient.getInstance().init(ctx, "androidApp"); // TODO device name
                androidUser = new AndroidUser(ctx);
            }
        }.start();

        View view = inflater.inflate(R.layout.fragment_account, container, false);
        // String menu = getArguments().getString("Menu");

        editUsername = (EditText) view.findViewById(R.id.acc_edit_username);
        editPassword = (EditText) view.findViewById(R.id.acc_edit_password);

        Button b;
        b = (Button) view.findViewById(R.id.btn_add_acc);
        b.setOnClickListener(this);

        b = (Button) view.findViewById(R.id.btn_add_cal_event);
        b.setOnClickListener(this);

        b = (Button) view.findViewById(R.id.btn_change_color);
        b.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        // do what you want to do when button is clicked
        switch (view.getId()) {
        case R.id.btn_add_acc:
            final String username = editUsername.getText().toString();
            final String password = editPassword.getText().toString();
            if(username.isEmpty() || password.isEmpty())
            {
                Toast.makeText(view.getContext(), "Please insert username and password.", Toast.LENGTH_LONG).show();
            } else {
                new DoLoginRequest(username, password, getActivity()).execute();
            }
            break;

        case R.id.btn_add_cal_event:
            if (cal == null) {
                Toast.makeText(view.getContext(), "Bitte erst einen Account anelgen", Toast.LENGTH_LONG).show();
            } else {
                if (cal.addEvent("Dummy") > 0) {
                    Toast.makeText(view.getContext(), "Kalender event hinzugefügt", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(), "Error beim hinzugefügen", Toast.LENGTH_LONG).show();
                }
            }
            break;
        case R.id.btn_change_color:
            Paint mPaint = new Paint();
            new ColorPicker(getActivity(), this, mPaint.getColor()).show();
            break;
        default:
            throw new IllegalStateException();
        }
    }

    @Override
    public void colorChanged(int color) {
        if (cal == null) {
//            ContentProviderClient client = getActivity().getContentResolver().acquireContentProviderClient(CalendarContract.AUTHORITY);
//            cal = new Calendar(AndroidUser.getAccount(getContext()), client, "RIOT");
        }
        cal.changeColor(color);
    }

    void AnswerIntent(String username)
    {
        if(androidUser.getAccount(getActivity())==null) {
            boolean accountCreated = androidUser.CreateAndroidAccount(username, getActivity());
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                if (accountCreated) {  //Pass the new account back to the account manager
                    AccountAuthenticatorResponse response = extras
                            .getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
                    Bundle result = new Bundle();
                    result.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                    result.putString(AccountManager.KEY_ACCOUNT_TYPE,
                            getString(R.string.ACCOUNT_TYPE));
                    response.onResult(result);
                }
                getActivity().finish();
            }
        }
        else {
            // TODO account already exists in the system, what to do with the intend?
        }
    }
}
