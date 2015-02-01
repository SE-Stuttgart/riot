package de.uni_stuttgart.riot.android;

import android.view.MenuItem;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class Filter {

    private int id;
    private MenuItem item;
    private NotificationType type;
    private boolean checked;

    public Filter(int id, MenuItem item, NotificationType type, boolean checked) {
        this.id = id;
        this.item = item;
        this.type = type;
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
