package de.enpro.android_riot.account;

import android.app.Fragment;
import android.content.ContentProviderClient;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import de.enpro.android.riot.R;

public class AccountFragment extends Fragment implements OnClickListener {

    RIOTAccount acc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.account_fragment, container, false);
//        String menu = getArguments().getString("Menu");

        Button b;
        b = (Button)view.findViewById(R.id.btn_add_acc);
        b.setOnClickListener(this);

        b = (Button)view.findViewById(R.id.btn_add_cal_event);
        b.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        //do what you want to do when button is clicked
        switch (v.getId()) {
            case R.id.btn_add_acc:
                acc = RIOTAccount.getRIOTAccount(getActivity());


                break;
            case R.id.btn_add_cal_event:
                acc = RIOTAccount.getRIOTAccount(getActivity());
                ContentProviderClient client = getActivity().getContentResolver().acquireContentProviderClient(CalendarContract.AUTHORITY);
                Calendar cal = new Calendar(acc.account, client, "Test");
                cal.AddEvent("Dummy");
                break;
        }
    }

}