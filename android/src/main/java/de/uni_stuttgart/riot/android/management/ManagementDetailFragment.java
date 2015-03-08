package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.view.View;

<<<<<<< HEAD
import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.commons.rest.data.Storable;
=======
import de.enpro.android.riot.R;
>>>>>>> RIOT-87:Android:Get things from the server

/**
 * This is an abstract fragment for displaying more information about a list item from the ManagementListFragment.
 *
 * @author Benny
 */
public abstract class ManagementDetailFragment extends ManagementFragment {

    protected long itemId;
    protected Object data;
    protected boolean enableElements;

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
////        inflater.inflate(R.menu.menu_fragment_detail, menu);
////
////        MenuItem logoutMI= menu.add(0,1,0,"Logout");
////        logoutMI.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
////
////        // TODO http://www.grokkingandroid.com/adding-action-items-from-within-fragments/
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
////        // handle item selection
////        switch (item.getItemId()) {
////            case R.id.edit_item:
////                // do s.th.
////                return true;
////            default:
////                return super.onOptionsItemSelected(item);
////        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
////        if (this.enableElements) {
////            // Set save button and its action
////            MenuItem button_save = menu.findItem(R.id.main_button_save);
////            button_save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
////                @Override
////                public boolean onMenuItemClick(MenuItem item) {
////                    setOnSaveClick();
////                    return false;
////                }
////            });
////            button_save.setVisible(true);
////
////            // Set abort button and its action
////            MenuItem button_abort = menu.findItem(R.id.main_button_abort);
////            button_abort.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
////                @Override
////                public boolean onMenuItemClick(MenuItem item) {
////                    setOnAbortClick();
////                    return false;
////                }
////            });
////            button_abort.setVisible(true);
////        } else {
////            // Set edit button and its action
////            MenuItem button_edit = menu.findItem(R.id.main_button_edit);
////            button_edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
////                @Override
////                public boolean onMenuItemClick(MenuItem item) {
////                    setOnEditClick();
////                    return false;
////                }
////            });
////            button_edit.setVisible(true);
////        }
//
//        super.onPrepareOptionsMenu(menu);
//    }


    @Override
    protected void handleArguments(Bundle bundle) {
        this.itemId = 0;
        this.data = null;

        if (bundle.containsKey(BUNDLE_OBJECT_ID)) {
            this.itemId = bundle.getLong(BUNDLE_OBJECT_ID);
        }
    }

    @Override
    protected void displayData() {
        // Prepare menu buttons
        prepareMenuButtons();

        // Get values of the data object
        data = getData();
        if (data == null) {
            // ToDo message or notice on screen that there are no data..
            return;
        }

        // Display the detailed data of the object
        displayDetailData();

        // Disable all items in the layout
        enableItems(false);
    }

    /**
     * Returns a list of all data that will be displayed.
     *
     * @return the list of the specified data type
     */
    protected abstract Object getData();

    /**
     * Set values to the view elements.
     */
    protected abstract void displayDetailData();

    /**
     * Define the action that happens on saving changes.
     */
    protected abstract void saveChanges();

    /**
     * En-/Disable the detail elements in the main view.
     *
     * @param enableElements is true if the elements should be enabled
     */
    protected abstract void enableDetailItems(boolean enableElements);

    /**
     * En-/Disable all elements in the main view.
     *
     * @param enableElements is true if the elements should be enabled
     */
    protected void enableItems(boolean enableElements) {
        this.enableElements = enableElements;

        findViewById(R.id.detail_button_save).setVisibility(enableElements ? View.VISIBLE : View.GONE);
        findViewById(R.id.detail_button_abort).setVisibility(enableElements ? View.VISIBLE : View.GONE);
        findViewById(R.id.detail_button_edit).setVisibility(!enableElements ? View.VISIBLE : View.GONE);
        findViewById(R.id.detail_button_back).setVisibility(!enableElements ? View.VISIBLE : View.GONE);

        enableDetailItems(enableElements);
    }

    /**
     * Define the action that happens on clicking the save button.
     */
    private void setOnSaveClick() {
        // Save changes
        saveChanges();
        enableItems(false);
    }

    /**
     * Define the action that happens on clicking the abort button.
     */
    private void setOnAbortClick() {
        // Reload this activity
        finish();
        startActivity(getIntent());
    }

    /**
     * Define the action that happens on clicking the save button.
     */
    private void setOnEditClick() {
        enableItems(true);
    }

    /**
     * Prepare the menu buttons.
     */
    private void prepareMenuButtons() {

        findViewById(R.id.detail_button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnSaveClick();
            }
        });
        findViewById(R.id.detail_button_abort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnAbortClick();
            }
        });
        findViewById(R.id.detail_button_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnEditClick();
            }
        });
    }
}
