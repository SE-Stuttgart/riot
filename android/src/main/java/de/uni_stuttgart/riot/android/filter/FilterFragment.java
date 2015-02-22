package de.uni_stuttgart.riot.android.filter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.android.notification.NotificationType;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class FilterFragment extends Fragment {

    private RIOTDatabase database;
    private CheckBox check_error, check_appointment, check_warning;

    public FilterFragment(RIOTDatabase database) {
        this.database = database;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        check_error = (CheckBox) view.findViewById(R.id.filter_error_check);
        check_appointment = (CheckBox) view.findViewById(R.id.filter_appointment_check);
        check_warning = (CheckBox) view.findViewById(R.id.filter_warning_check);

        check_error.setChecked(database.getFilterSettings(NotificationType.ERROR));
        check_appointment.setChecked(database.getFilterSettings(NotificationType.APPOINTMENT));
        check_warning.setChecked(database.getFilterSettings(NotificationType.WARNING));

        return view;

    }

}
