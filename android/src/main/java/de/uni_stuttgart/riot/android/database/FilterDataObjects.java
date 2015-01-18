package de.uni_stuttgart.riot.android.database;

import de.uni_stuttgart.riot.android.Filter;
import de.uni_stuttgart.riot.android.MainActivity;
import de.uni_stuttgart.riot.android.NotificationType;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class FilterDataObjects {

	private RIOTDatabase database;

	public FilterDataObjects(MainActivity mainActivity) {
		database = new RIOTDatabase(mainActivity);
	}

	public RIOTDatabase getDatabase() {
		return database;
	}

	/**
	 * We set the filter settings into the database.
	 * 
	 * @param filter
	 */
	public void setFilter(Filter filter) {

		if (filter.getItem().isChecked() == false) {
			filter.getItem().setChecked(true);

			database.updateFilterSetting(filter);

		} else {
			filter.getItem().setChecked(false);

			database.updateFilterSetting(filter);
		}
	}

	/**
	 * Get the checked value for a filter
	 * 
	 * @param type
	 *            The type of the notification
	 * @return true = ischecked, false = unchecked
	 */
	public boolean getFilterStatus(NotificationType type) {
		return database.getFilterSettings(type);		
	}

}
