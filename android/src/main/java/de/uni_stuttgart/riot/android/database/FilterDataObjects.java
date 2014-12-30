package de.uni_stuttgart.riot.android.database;

import de.uni_stuttgart.riot.android.Filter;
import de.uni_stuttgart.riot.android.MainActivity;
import de.uni_stuttgart.riot.android.NotificationType;
import android.content.Context;
import android.view.MenuItem;

/**
 * 
 * @author Florian
 *
 */
public class FilterDataObjects {
	private FilterDatabase filterDatabase;

	public FilterDataObjects(MainActivity mainActivity) {
		filterDatabase = new FilterDatabase(mainActivity);
	}

	/**
	 * We set the filter settings into the database.
	 * @param filter
	 */
	public void setFilter(Filter filter) {

		boolean isChecked = false;

		if (filter.getItem().isChecked() == false) {
			filter.getItem().setChecked(true);

			isChecked = !isChecked;
			filterDatabase.updateFilterSetting(filter.getItem().getTitle(),
					filter.getType(), isChecked);

		} else {
			filter.getItem().setChecked(false);

			filterDatabase.updateFilterSetting(filter.getItem().getTitle(),
					filter.getType(), isChecked);
		}

	}

	/**
	 * Get the checked value for a filter
	 * @param type The type of the notification
	 * @return true = ischecked, false = unchecked
	 */
	public boolean getFilterStatus(NotificationType type) {
		return filterDatabase.getFilterSettings(type);
	}

}
