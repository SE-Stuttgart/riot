package de.uni_stuttgart.riot.android.users;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.enpro.android.riot.R;

/**
 * Created by Benny on 16.12.2014.
 */
public class UsersFragment extends Fragment {

    // Attributes for saving and managing the list view items
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container,
                false);

        arrayList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        ((ListView) view.findViewById(R.id.users_listView)).setAdapter(arrayAdapter);

        for(Integer i=0; i<20; i++) {
            arrayAdapter.add("User " + i.toString());
        }

        return view;
    }
}
