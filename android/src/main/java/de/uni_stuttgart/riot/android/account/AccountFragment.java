package de.uni_stuttgart.riot.android.account;

import android.app.Fragment;
import android.content.ContentProviderClient;
import android.graphics.Paint;
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

/**
 * UI for account creation.
 */
public class AccountFragment extends Fragment implements OnClickListener, ColorPicker.OnColorChangedListener {

    RIOTAccount acc;
    Calendar cal = null;

    EditText editUsername;
    EditText editPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
            String username = editUsername.getText().toString(); // FIXME
            String password = editPassword.getText().toString();

            // TODO check agains backend
            if (password.equals("test")) {
                // edit <server>/iot/api/v1/calendar/<id>/
                // list all<server>/iot/api/v1/calendar/
                Toast.makeText(view.getContext(), "Account wird angelegt", Toast.LENGTH_LONG).show();
                acc = RIOTAccount.getRIOTAccount(getActivity());
                ContentProviderClient client = getActivity().getContentResolver().acquireContentProviderClient(CalendarContract.AUTHORITY);
                cal = new Calendar(acc.getAccount(), client, "RIOT");
            } else {
                Toast.makeText(view.getContext(), "'test' ist das richtig passwort", Toast.LENGTH_LONG).show();
            }

            break;
        case R.id.btn_add_cal_event:
            if (acc == null || cal == null) {
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
        if(cal == null) {
            ContentProviderClient client = getActivity().getContentResolver()
                    .acquireContentProviderClient(CalendarContract.AUTHORITY);
            cal = new Calendar(RIOTAccount.getRIOTAccount(getActivity()).getAccount(), client, "RIOT");
        }
        cal.changeColor(color);


    }
}
