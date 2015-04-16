package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.ui.PropertyView;
import de.uni_stuttgart.riot.android.ui.UIProducer;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * This is an abstract activity for displaying more information about a list item from the ManagementListFragment.
 *
 * @param <T>
 *            type of the handled object
 * @author Benny
 */
public abstract class ManagementDetailActivity<T> extends ManagementActivity<T> {

    private static int nonGroupId = 0;

    protected T item;
    protected long itemId;
    protected List<ManagementPropertyBinding<?>> boundProperties = new ArrayList<ManagementPropertyBinding<?>>();
    protected LinearLayout mainLayout;
    private SparseArray<List<View>> groupedItems;
    private SparseArray<String> groupNames;

    @Override
    protected void handleArguments(Bundle bundle) {
        this.itemId = 0;

        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_OBJECT_ID)) {
                this.itemId = bundle.getLong(BUNDLE_OBJECT_ID);
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.management_detail;
    }

    @Override
    protected void displayManagementData() {
        // Check if there is an item to display
        if (this.item == null) {
            IM.INSTANCES.getMH().writeErrorMessage("There is no data for displaying!");
            IM.INSTANCES.getMH().showQuickMessage("There is no data for displaying!");
            return;
        }

        // Check if the main layout is available
        if (this.mainLayout == null) {
            this.mainLayout = (LinearLayout) findViewById(R.id.management_linear_layout);
            if (this.mainLayout == null) {
                IM.INSTANCES.getMH().writeErrorMessage("No main layout was found!");
                IM.INSTANCES.getMH().showQuickMessage("No main layout was found!");
                return;
            }
        } else {
            // Clear all children and the group list
            this.mainLayout.removeAllViews();
        }

        // Initialize the list of the grouped items
        if (this.groupedItems == null) {
            this.groupedItems = new SparseArray<List<View>>();
            this.groupNames = new SparseArray<String>();
        } else {
            // Clear all grouped items
            this.groupedItems.clear();
            this.groupNames.clear();
        }

        displayDetailData();
    }

    /**
     * Bind all thing properties with their property listener.
     */
    protected void bindAllManagementProperties() {
        // Start monitoring only if it is not already running
        new Thread(new Runnable() {
            public void run() {
                for (ManagementPropertyBinding<?> binding : boundProperties) {
                    binding.bind();
                }
            }
        }).start();
    }

    /**
     * Unbind all thing properties with their property listener.
     */
    protected void unbindAllManagementProperties() {
        new Thread(new Runnable() {
            public void run() {
                for (ManagementPropertyBinding<?> binding : boundProperties) {
                    binding.unbind();
                }
                boundProperties.clear();
            }
        }).start();
    }

    /**
     * Add the given item to the the wanted group.
     *
     * @param groupId
     *            the unique id of the group
     * @param view
     *            the prepared item
     */
    protected void addPreparedItemToGroup(int groupId, View view) {
        // If the group list for that group id does not exists yet create it
        if (this.groupedItems.get(groupId) == null) {
            this.groupedItems.append(groupId, new ArrayList<View>());
        }

        // Add it to the group list
        if (view != null) {
            this.groupedItems.get(groupId).add(view);
        }
    }

    /**
     * Prepare and add all items in the right group list.
     *
     * @param properties
     *            the items
     */
    protected void prepareAndGroupItems(Collection<Property<?>> properties) {
        for (Property<?> property : properties) {
            // Get group id and prepare the view item
            int groupId = property.getUiHint().getGroupAndOrderID();
            addPreparedItemToGroup(groupId, prepareItemBy(property));
            if (groupId != nonGroupId) {
                saveGroupName(groupId, String.valueOf(groupId));
            }
        }
    }

    /**
     * Save the name of the group.
     *
     * @param groupId
     *            the unique id of the group
     * @param groupName
     *            the name of the group
     */
    protected void saveGroupName(int groupId, String groupName) {
        if (this.groupNames.get(groupId) == null) {
            this.groupNames.append(groupId, groupName);
        }
    }

    /**
     * Add the grouped and prepared items to the main layout
     */
    protected void addGroupedItemsToMainLayout() {
        for (int i = 0; i < this.groupedItems.size(); i++) {
            int key = this.groupedItems.keyAt(i);
            if (key == nonGroupId) {
                // Just add the items to the main layout (because they do not belong to a group)
                for (View view : this.groupedItems.get(key)) {
                    this.mainLayout.addView(view);
                }
            } else {
                // Prepare a group with the grouped items
                this.mainLayout.addView(prepareGroup(this.groupNames.get(key), this.groupedItems.get(key)));
            }
        }
    }

    /**
     * Prepare an item by name and ui hint.
     *
     * @param <V>
     *            The type of the value.
     * @param itemName
     *            is the name that will be displayed above the item
     * @param value
     *            includes the value that will be displayed with this item
     * @param uiHint
     *            is needed for decide which ui element will be built
     * @return the prepared view
     */
    protected <V> View prepareItemBy(String itemName, V value, UIHint uiHint) {
        // Prepare the item
        LinearLayout linearLayoutItem = prepareItemName(itemName);

        @SuppressWarnings("unchecked")
        Class<V> valueType = (Class<V>) value.getClass();
        PropertyView<V> view = UIProducer.producePropertyView(this, uiHint, valueType, itemName);
        linearLayoutItem.addView(view.toView());
        view.setValue(value);
        view.setEnabled(false);

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Prepare an item for the appropriate property type.
     *
     * @param <V>
     *            The type of the values in the view.
     * @param property
     *            for that item the view will be prepared
     * @return the prepared view
     */
    protected <V> View prepareItemBy(Property<V> property) {
        // Prepare the property item
        LinearLayout linearLayoutItem = prepareItemName(property.getName());

        PropertyView<V> view = UIProducer.producePropertyView(this, property.getUiHint(), property.getValueType(), property.getName());
        ManagementPropertyBinding<V> binding = new ManagementPropertyBinding<V>(property, view, this);
        this.boundProperties.add(binding);
        linearLayoutItem.addView(view.toView());
        view.setValue(property.get());

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Prepare the item group.
     *
     * @param groupName
     *            the name of the group
     * @param items
     *            the items of the group
     * @return the prepared item group
     */
    protected View prepareGroup(String groupName, List<View> items) {

        LinearLayout linearLayout = prepareItemName(groupName);

        // Create new layout element
        final RelativeLayout relativeLayoutGroup = new RelativeLayout(getApplicationContext());

        // Add group layout to the prepared layout with the group name
        linearLayout.addView(relativeLayoutGroup);

        // Set an listener to the layout to order the items inside after they know their size
        relativeLayoutGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Sort the items in the group
                orderGroupedItems(relativeLayoutGroup);

                // Unregister the listener
                relativeLayoutGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        // Set linearLayoutGroup attributes
        relativeLayoutGroup.setBackground(getResources().getDrawable(R.drawable.group_border));
        int padding = (int) getResources().getDimension(R.dimen.group_padding);
        relativeLayoutGroup.setPadding(padding, padding, padding, padding);

        // Add sub items to group
        if (items != null) {
            for (View view : items) {
                // Add an id to the view
                view.setId(generateViewId());

                // Add item to the layout
                relativeLayoutGroup.addView(view);
            }
        }

        return linearLayout;
    }

    /**
     * Order the items inside the group layout.
     *
     * @param relativeLayoutGroup
     *            the group
     */
    private void orderGroupedItems(RelativeLayout relativeLayoutGroup) {

        // Get available width and decrement with two times left and right padding of the main layout and two times of the group layout
        int availableWidth = relativeLayoutGroup.getMeasuredWidth(); // - (2 * (int) getDimension(R.dimen.fragment_main_margin));
        int usedWidth = 0;

        int idOfFirstElementInLastRow = 0;
        int idOfFirstElementInRow = 0;
        int idOfPreviousElement = 0;

        for (int i = 0; i < relativeLayoutGroup.getChildCount(); i++) {
            View preparedItem = relativeLayoutGroup.getChildAt(i);

            if (preparedItem != null) {
                // Set layout params
                RelativeLayout.LayoutParams relativeLayoutItemParams = (RelativeLayout.LayoutParams) preparedItem.getLayoutParams();
                preparedItem.setLayoutParams(relativeLayoutItemParams);

                // Check if the element would have enough space
                int itemWidth = preparedItem.getMeasuredWidth();
                if ((usedWidth + itemWidth) < availableWidth) {
                    if (idOfFirstElementInRow == 0) {
                        // This is the very first element that will be added
                        idOfFirstElementInRow = preparedItem.getId();
                        idOfPreviousElement = preparedItem.getId();
                    } else {
                        // All following elements in the same row
                        relativeLayoutItemParams.addRule(RelativeLayout.RIGHT_OF, idOfPreviousElement);
                        if (idOfFirstElementInLastRow != 0) {
                            relativeLayoutItemParams.addRule(RelativeLayout.BELOW, idOfFirstElementInLastRow);
                        }
                        idOfPreviousElement = preparedItem.getId();
                    }
                    usedWidth += itemWidth;
                } else {
                    if (idOfPreviousElement == 0) {
                        // This could be the first element if it is to large for one row
                        idOfFirstElementInRow = preparedItem.getId();
                        idOfPreviousElement = preparedItem.getId();
                    } else {
                        // All first elements in a row
                        relativeLayoutItemParams.addRule(RelativeLayout.BELOW, idOfFirstElementInRow);
                        idOfFirstElementInLastRow = idOfFirstElementInRow;
                        idOfFirstElementInRow = preparedItem.getId();
                        idOfPreviousElement = preparedItem.getId();
                    }
                    usedWidth = itemWidth;
                }
            }
        }
    }

    /**
     * Prepare an item - add a surrounding view with the name of the item.
     *
     * @param itemName
     *            is the name that will be displayed above the item
     * @return a layout with the name of the item and space for the item itself
     */
    protected LinearLayout prepareItemName(String itemName) {
        // Create new layout element
        LinearLayout linearLayoutItem = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams linearLayoutItemParams;

        // Add name of the item
        if (itemName != null) {
            // Create a full line item
            View preparedTextView = prepareTextView(itemName);
            if (preparedTextView != null) {
                linearLayoutItem.addView(preparedTextView);
            }
            // Get layout params
            linearLayoutItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Set linearLayoutItem attributes
            linearLayoutItem.setOrientation(LinearLayout.VERTICAL);

            // Set a padding on the top and the bottom
            linearLayoutItem.setPadding(0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) * 3, 0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) / 2);
        } else {
            // Get layout params - just wrapping the item
            linearLayoutItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Set a padding on the top and the bottom
            linearLayoutItem.setPadding(0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) / 2, 0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) / 2);
        }

        // Set layout params
        linearLayoutItem.setLayoutParams(linearLayoutItemParams);

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Prepare a text view for the item name.
     *
     * @param text
     *            is the text that will be displayed
     * @return the prepared view
     */
    private View prepareTextView(String text) {
        // Create new layout elements
        TextView textView = new TextView(getApplicationContext());

        // Get layout params
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        textView.setLayoutParams(textViewParams);

        // Set the current value
        if (text != null) {
            textView.setText(text);
        } else {
            // Set default values
            textView.setText(getText(R.string.default_item_title));
        }

        return textView;
    }

    /**
     * Display the loaded data.
     */
    protected abstract void displayDetailData();
}
