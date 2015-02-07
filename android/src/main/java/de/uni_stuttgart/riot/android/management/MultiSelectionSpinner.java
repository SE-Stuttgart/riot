package de.uni_stuttgart.riot.android.management;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This code is from the website: http://v4all123.blogspot.in/2013/09/spinner-with-multiple-selection-in.html.
 * Date of posting: 2013'09'01.
 * Time of posting: 20:09.
 * Author: Gunaseelan A.
 * ...
 * Modified by Benny on 16.12.2014.
 * This class provides a spinner that allows multiple selection.
 */
public class MultiSelectionSpinner extends Spinner implements
        OnMultiChoiceClickListener {
    String[] items = null;
    boolean[] mSelection = null;

    ArrayAdapter<String> simpleAdapter;

    /**
     * .
     *
     * @param context .
     */
    public MultiSelectionSpinner(Context context) {
        super(context);

        simpleAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simpleAdapter);
    }

    /**
     * .
     *
     * @param context .
     * @param attrs   .
     */
    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        simpleAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simpleAdapter);
    }

    /**
     * .
     *
     * @param dialog    .
     * @param which     .
     * @param isChecked .
     */
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;

            simpleAdapter.clear();
            simpleAdapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(items, mSelection, this);
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    /**
     * .
     *
     * @param items .
     */
    public void setItems(String[] items) {
        this.items = items;
        mSelection = new boolean[this.items.length];
        simpleAdapter.clear();
        simpleAdapter.add(this.items[0]);
        Arrays.fill(mSelection, false);
    }

    /**
     * .
     *
     * @param items .
     */
    public void setItems(List<String> items) {
        setItems(items.toArray(new String[items.size()]));
    }

    /**
     * .
     *
     * @param items .
     */
    public void setItems(Set<String> items) {
        setItems(items.toArray(new String[items.size()]));
    }

    /**
     * .
     *
     * @param selection .
     */
    public void setSelection(String[] selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < items.length; ++j) {
                if (items[j].equals(sel)) {
                    mSelection[j] = true;
                }
            }
        }
        simpleAdapter.clear();
        simpleAdapter.add(buildSelectedItemString());
    }

    /**
     * .
     *
     * @param value .
     */
    public void setSelectionEnabled(boolean value) {
// ToDo
//        setClickable(value);
//        setFocusable(value);
//        getSelectedView().setEnabled(value);
    }

    /**
     * .
     * @param selection .
     */
    public void setSelection(List<String> selection) {
        setSelection(selection.toArray(new String[selection.size()]));
    }

    /**
     * .
     *
     * @param selection .
     */
    public void setSelection(Set<String> selection) {
        setSelection(selection.toArray(new String[selection.size()]));
    }

    /**
     * .
     *
     * @param index .
     */
    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simpleAdapter.clear();
        simpleAdapter.add(buildSelectedItemString());
    }

    /**
     * .
     *
     * @param selectedIndicies .
     */
    public void setSelection(int[] selectedIndicies) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simpleAdapter.clear();
        simpleAdapter.add(buildSelectedItemString());
    }

    /**
     * .
     *
     * @return .
     */
    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < items.length; ++i) {
            if (mSelection[i]) {
                selection.add(items[i]);
            }
        }
        return selection;
    }

    /**
     * .
     *
     * @return .
     */
    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    /**
     * .
     *
     * @return .
     */
    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(items[i]);
            }
        }
        return sb.toString();
    }

    /**
     * Saves the selected elements in an array list.
     *
     * @return the array list that includes the selected elements
     */
    public ArrayList<String> getSelectedItemsAsArrayList() {
        ArrayList<String> arrayList = new ArrayList<String>();

        for (int i = 0; i < items.length; ++i) {
            if (mSelection[i]) {
                arrayList.add(items[i]);
            }
        }
        return arrayList;
    }

    /**
     * .
     *
     * @return .
     */
    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (String element : getSelectedItemsAsArrayList()) {
            if (foundOne) {
                sb.append(", ");
            }
            foundOne = true;
            sb.append(element);
        }
        return sb.toString();
    }
}
