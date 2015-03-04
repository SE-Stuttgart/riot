package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * This is an abstract fragment for displaying more information about a list item from the ManagementListFragment.
 *
 * @author Benny
 */
public abstract class ManagementDetailFragment extends ManagementFragment {

    protected static final String BUNDLE_ENABLE_ELEMENTS = "bundle_enable_elements";

    protected long itemId;
    protected boolean enableElements;
    protected Storable data;

    @Override
    protected void handleArguments(Bundle args) {
        if (args != null) {
            // Save the value of the parameter, otherwise set a default id
            this.itemId = args.containsKey(BUNDLE_OBJECT_ID) ? args.getLong(BUNDLE_OBJECT_ID) : 0;

            // Save the value of the parameter, otherwise disable all elements
            this.enableElements = args.containsKey(BUNDLE_ENABLE_ELEMENTS) && args.getBoolean(BUNDLE_ENABLE_ELEMENTS);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_fragment_detail, menu);
//
//        MenuItem logoutMI= menu.add(0,1,0,"Logout");
//        logoutMI.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//
//        // TODO http://www.grokkingandroid.com/adding-action-items-from-within-fragments/

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        // handle item selection
//        switch (item.getItemId()) {
//            case R.id.edit_item:
//                // do s.th.
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        if (this.enableElements) {
//            // Set save button and its action
//            MenuItem button_save = menu.findItem(R.id.main_button_save);
//            button_save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    setOnSaveClick();
//                    return false;
//                }
//            });
//            button_save.setVisible(true);
//
//            // Set abort button and its action
//            MenuItem button_abort = menu.findItem(R.id.main_button_abort);
//            button_abort.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    setOnAbortClick();
//                    return false;
//                }
//            });
//            button_abort.setVisible(true);
//        } else {
//            // Set edit button and its action
//            MenuItem button_edit = menu.findItem(R.id.main_button_edit);
//            button_edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    setOnEditClick();
//                    return false;
//                }
//            });
//            button_edit.setVisible(true);
//        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void displayData() {


/* TODO DEL
        // Set values and on click event off the buttons
        Button buttonAbort = (Button) view.findViewById(R.id.detail_abort_button);
        buttonAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnAbortClick(view);
            }
        });
        buttonAbort.setText(getAbortText());

        Button buttonEdit = (Button) view.findViewById(R.id.detail_edit_button);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnEditClick(view);
            }
        });
        buttonEdit.setText(getEditText());
*/

        prepareMenuButtons();

        // Set values of the data objects
        data = getData();
        if (data == null) {
            // ToDo message or notice on screen that there are no data..
            return;
        }

        displayDetailData();
    }

    /**
     * Returns a list of all data that will be displayed.
     *
     * @return the list of the specified data type
     */
    protected abstract Storable getData();

    /**
     * Set values to the view elements.
     */
    protected abstract void displayDetailData();

    /**
     * Define the action that happens on clicking the back button.
     */
    protected abstract void setOnBackClick();

    /**
     * Calls the same fragment for editing.
     *
     * @param edit true if the edit mode is called
     */
    protected abstract void callThisFragment(boolean edit);

    /**
     * Define the action that happens on saving changes.
     */
    protected abstract void saveChanges();

    /**
     * Define the action that happens on clicking the save button.
     */
    private void setOnSaveClick() {
        saveChanges();
        callThisFragment(false);
    }

    /**
     * Define the action that happens on clicking the abort button.
     */
    private void setOnAbortClick() {
        callThisFragment(false);
    }

    /**
     * Define the action that happens on clicking the save button.
     */
    private void setOnEditClick() {
        callThisFragment(true);
    }

    /**
     * Prepare the menu buttons.
     */
    private void prepareMenuButtons() {

        if (this.enableElements) {
            ImageButton buttonSave = (ImageButton) view.findViewById(R.id.detail_save_button);
            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setOnSaveClick();
                }
            });
            buttonSave.setVisibility(View.VISIBLE);

            ImageButton buttonAbort = (ImageButton) view.findViewById(R.id.detail_abort_button);
            buttonAbort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setOnAbortClick();
                }
            });
            buttonAbort.setVisibility(View.VISIBLE);
            return;
        }

        ImageButton buttonEdit = (ImageButton) view.findViewById(R.id.detail_edit_button);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnEditClick();
            }
        });
        buttonEdit.setVisibility(View.VISIBLE);

        ImageButton buttonBack = (ImageButton) view.findViewById(R.id.detail_back_button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnBackClick();
            }
        });
        buttonBack.setVisibility(View.VISIBLE);
    }
}
