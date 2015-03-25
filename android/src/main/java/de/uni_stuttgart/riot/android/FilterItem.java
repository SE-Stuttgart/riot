package de.uni_stuttgart.riot.android;

import android.view.MenuItem;
import de.uni_stuttgart.riot.notification.NotificationSeverity;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class FilterItem {

    private int id;
    private MenuItem item;
    private NotificationSeverity severity;
    private boolean checked;

    public FilterItem(int id, MenuItem item, NotificationSeverity severity, boolean checked) {
        this.id = id;
        this.item = item;
        this.severity = severity;
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MenuItem getItem() {
        return item;
    }

    public void setItem(MenuItem item) {
        this.item = item;
    }

    public NotificationSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(NotificationSeverity severity) {
        this.severity = severity;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
