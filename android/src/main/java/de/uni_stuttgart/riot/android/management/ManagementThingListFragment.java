package de.uni_stuttgart.riot.android.management;

import android.app.Fragment;

import java.util.List;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Fragment that displays all things in a list.
 *
 * @author Benny
 */
public class ManagementThingListFragment extends ManagementListFragment {

    @Override
    protected int getTitleId() {
        return R.string.thing_list;
    }

    @Override
    protected List<Storable> getData() {
        return (List<Storable>) (List<?>) getThings();
    }

    @Override
    protected ManagementListFragment getFragment() {
        return this;
    }

    @Override
    protected Fragment getOnItemClickFragment() {
        return new ManagementThingDetailFragment();
    }

    @Override
    protected boolean isInstanceOf(Storable item) {
        return (item instanceof DummyThing);
    }

    @Override
    protected String getDefaultSubject() {
        return "This is a Thing";
    }

    @Override
    protected String getSubject(Storable item) {
        return ((DummyThing) item).getThingName();
    }

    @Override
    protected String getDefaultDescription() {
        return "This is the description of the thing";
    }

    @Override
    protected String getDescription(Storable item) {
//        Long id = ((DummyThing) item).getId();
//        if (id != null && id != 0) {
//            return id.toString();
//        }
        return null;
    }

    @Override
    protected String getDefaultImageUri() {
        return null; // ToDo loading images asynchronous?
    }

    @Override
    protected String getImageUri(Storable item) {
        return null; // ((DummyThing) item).getImageUri(); // ToDo
    }

    @Override
    protected int getDefaultImageId() {
        return android.R.drawable.ic_menu_gallery;
    }

    @Override
    protected int getImageId(Storable item) {
        return 0; // ((DummyThing) item).getImageId(); // ToDo
    }

    @Override
    protected OnlineState getDefaultOnlineState() {
        return OnlineState.STATUS_OFFLINE;
    }

    @Override
    protected OnlineState getOnlineState(Storable item) {
        return null; // ((DummyThing) item).getOnlineState(); // ToDo
    }
}
