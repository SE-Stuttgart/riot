package de.uni_stuttgart.riot.android;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowListView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;
import de.uni_stuttgart.riot.notification.Notification;

//@RunWith(RobolectricTestRunner.class)
//@Config(manifest = "../android/AndroidManifest.xml")
public class TestMainActivity {

    @Test
    public void shouldHaveRefillWaterItem() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InterruptedException {

        if (true) {
            return; // FIXME Temporarily disabled
        }

        // Create MainActivity
        Intent intent = new Intent();
        intent.putExtra("pressedButton", "coffeeMachine");
        HomeScreen activity = Robolectric.buildActivity(HomeScreen.class).withIntent(intent).setup().get();

        // Enable ERROR type in the database
        Field filterObjectsField = HomeScreen.class.getDeclaredField("filterObjects");
        filterObjectsField.setAccessible(true);
        RIOTDatabase riotDatabase = new RIOTDatabase(activity);
        SQLiteDatabase db = riotDatabase.getWritableDatabase();
        db.execSQL("INSERT INTO filter (id, type, is_checked) VALUES (1000, 'ERROR', 1)");
        db.close();

        // Update the view
        ShadowActivity a = (ShadowActivity) ShadowExtractor.extract(activity);
        a.clickMenuItem(R.id.action_refresh);

        // Wait a while until the refresh is complete
        Thread.sleep(1000);

        // Check if the items are present.
        ListView list = (ListView) activity.findViewById(R.id.NotificationList);
        Set<String> itemTitles = new HashSet<>();
        Notification notif1 = (Notification) list.getItemAtPosition(0);
        Notification notif2 = (Notification) list.getItemAtPosition(1);
        itemTitles.add(notif1.getTitle());
        itemTitles.add(notif2.getTitle());
        assertTrue(itemTitles.contains("Refill water"));
        assertTrue(itemTitles.contains("Refill beans"));

        ShadowListView l = (ShadowListView) ShadowExtractor.extract(list);
        l.populateItems();

        // Note: There is no reasonable behavior in the listener in MainActivity, currently.
        // This should simply print "click".
        l.clickFirstItemContainingText("Refill water");

    }
}
