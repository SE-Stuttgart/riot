package de.uni_stuttgart.riot.android.account;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.text.format.DateUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
class MySSLSocketFactory extends SSLSocketFactory {
    SSLContext sslContext = SSLContext.getInstance("TLS");

    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);

        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sslContext.init(null, new TrustManager[] { tm }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}

class MyCalendarEventEntry {
    MyCalendarEventEntry() {
    }

    MyCalendarEventEntry(Cursor eventCursor) {
        id = eventCursor.getLong(0);
        title = eventCursor.getString(1);
        begin = new java.util.Date(eventCursor.getLong(2));
        end = new java.util.Date(eventCursor.getLong(3));
        desc = eventCursor.getString(4);
        loc = eventCursor.getString(5);
        all_day = eventCursor.getInt(6);
        dirty = eventCursor.getInt(7);
        sync_id = eventCursor.getInt(8);
        deleted = eventCursor.getInt(9) == 1;
        System.out.println("Title:" + title + " Begin:" + begin + " - " + end + " Desc: " + desc + " dirty:" + dirty + " SID:" + sync_id + " [" + id + "]");
    }

    long id;
    String title;
    java.util.Date begin;
    java.util.Date end;
    String desc;
    String loc;
    int all_day;
    int dirty;
    long sync_id;
    boolean deleted;

    JSONObject getJSON() {
        JSONObject entry = new JSONObject();
        try {
            entry.put("title", title);
            entry.put("location", loc);
            entry.put("description", desc);
            entry.put("startTime", begin);
            entry.put("endTime", end);
            entry.put("allDayEvent", all_day);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entry;
    }
}

/**
 * FIXME Provide description FIXME Clean up this class and fix checkstyle errors. CHECKSTYLE:OFF
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SyncAdapter";
    private static final String API_URL = "https://belgrad.informatik.uni-stuttgart.de:8181/riot";
    private boolean bool_added = false;
    private String my_calendar_id = new String();

    public SyncAdapter(Context context) {
        super(context, true);
        Log.v(TAG, "SyncAdapter created");
    }

    public void CreateEventOnServer(ContentResolver contentResolver, MyCalendarEventEntry entry) {
        entry.sync_id = DoCreateEventOnServer(entry.getJSON());
        if (entry.sync_id > 0) {
            // TODO mark undirty and set id
            updateEventSyncID(contentResolver, entry.id, entry.sync_id);
            Log.v(TAG, "Event [" + entry.id + "] synced with: " + entry.sync_id);
        }
    }

    public int DoCreateEventOnServer(JSONObject entry) {

        HttpClient httpClient = createHttpClient();

        HttpPost httpReq = new HttpPost(API_URL + "/api/v1/calendar/");
        httpReq.setHeader("Content-Type", "application/json");
        HttpResponse response = null;
        String result = null;
        try {
            StringEntity entity = new StringEntity(entry.toString());
            httpReq.setEntity(entity);
            response = httpClient.execute(httpReq);
            HttpEntity entity1 = response.getEntity();
            result = EntityUtils.toString(entity1);

            Log.v(TAG, result);
            if (response.getStatusLine().getStatusCode() == 201) {
                return (new JSONObject(result)).getInt("id");
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public void UpdateEventOnServer(ContentResolver contentResolver, MyCalendarEventEntry entry) {
        if (DoUpdateEventOnServer(entry)) {
            // TODO mark undirty and set id
            updateEventSyncID(contentResolver, entry.id, entry.sync_id);
            Log.v(TAG, "Event [" + entry.id + "] synced with: " + entry.sync_id);
        }
    }

    boolean DoUpdateEventOnServer(MyCalendarEventEntry entry) {
        HttpClient httpClient = createHttpClient();

        try {
            HttpPut httpReq = new HttpPut(API_URL + "/api/v1/calendar/" + entry.sync_id);
            httpReq.setHeader("Content-Type", "application/json");
            HttpResponse response;

            StringEntity entity = new StringEntity(entry.getJSON().toString());
            httpReq.setEntity(entity);
            response = httpClient.execute(httpReq);

            Log.v(TAG, response.toString());
            if (response.getStatusLine().getStatusCode() == 204) {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void DeleteEventOnServer(ContentResolver contentResolver, MyCalendarEventEntry entry) {
        if (DoDeleteEventOnServer(entry)) {
            // TODO mark undirty and set id
            updateEventSyncID(contentResolver, entry.id, entry.sync_id);
            Log.v(TAG, "Event [" + entry.id + "] deleted (" + entry.sync_id + ")");
        }
    }

    boolean DoDeleteEventOnServer(MyCalendarEventEntry entry) {
        HttpClient httpClient = createHttpClient();

        try {
            HttpDelete httpReq = new HttpDelete(API_URL + "/api/v1/calendar/" + entry.sync_id);
            httpReq.setHeader("Content-Type", "application/json");
            HttpResponse response;

            response = httpClient.execute(httpReq);

            Log.v(TAG, response.toString());
            if (response.getStatusLine().getStatusCode() == 204) {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public JSONArray ReadEventsFromServer() {
        JSONArray data = null;
        HttpClient httpClient = createHttpClient();

        HttpGet httpReq = new HttpGet(API_URL + "/api/v1/calendar/");
        httpReq.setHeader("Content-Type", "application/json");
        HttpResponse response = null;
        String result = null;
        try {
            response = httpClient.execute(httpReq);
            HttpEntity entity1 = response.getEntity();
            result = EntityUtils.toString(entity1);
            data = new JSONArray(result);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // return result;
        Log.v(TAG, data.toString());
        return data;
    }

    public HttpClient createHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Performing sync for authority " + authority);
        syncCalendar(getContext().getContentResolver());

        // TODO need to use an other lib!
        // UserManagementClient cl = new UserManagementClient(URL, "htc");
        // cl.login("Yoda","YodaPW");
        // Client client = ClientBuilder.newClient();
        // cl.get(client.target("api/v1/calendar/"), "");

    }

    HashSet<String> getCalendarIds(ContentResolver contentResolver) {
        // Fetch a list of all calendars synced with the device, their display names and whether the
        Cursor cursor = contentResolver.query(Calendars.CONTENT_URI, (new String[] { Calendars._ID, Calendars.NAME, Calendars.ACCOUNT_TYPE, Calendars.ACCOUNT_NAME }), null, null, null);

        HashSet<String> calendarIds = new HashSet<String>();
        try {
            System.out.println("Found #" + cursor.getCount() + " calendars");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    String acc_type = cursor.getString(2);
                    String acc_name = cursor.getString(3);

                    System.out.println("Id: " + _id + " Display Name: " + displayName + " Acc: " + acc_type + " - " + acc_name);
                    calendarIds.add(_id);
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return calendarIds;
    }

    Cursor getEvents(ContentResolver contentResolver, long calendarId) {
        Uri url = CalendarContract.Events.CONTENT_URI.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").appendQueryParameter(Calendars.ACCOUNT_NAME, RIOTAccount.AUTHORITY).appendQueryParameter(Calendars.ACCOUNT_TYPE, RIOTAccount.ACCOUNT_TYPE).build();

        Cursor eventCursor = contentResolver.query(url, new String[] { CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.ALL_DAY, CalendarContract.Events.DIRTY, CalendarContract.Events._SYNC_ID, CalendarContract.Events.DELETED }, // projection
                CalendarContract.Events.CALENDAR_ID + " = ?", // selection
                new String[] { Long.toString(calendarId) }, // selection args
                CalendarContract.Events.DTSTART + " ASC");
        return eventCursor;
    }

    public boolean updateEventSyncID(ContentResolver contentResolver, long event_id, long sync_id) {
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events._SYNC_ID, sync_id);
        cv.put(CalendarContract.Events.DIRTY, 0);
        // cv.put(CalendarContract.Events.TITLE, "fuuuu");
        String cs[] = new String[] { Long.toString(event_id) };

        Uri calendarsURI = CalendarContract.Events.CONTENT_URI.buildUpon().appendQueryParameter(Calendars.ACCOUNT_NAME, RIOTAccount.AUTHORITY).appendQueryParameter(Calendars.ACCOUNT_TYPE, RIOTAccount.ACCOUNT_TYPE).appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").build();

        int ret = contentResolver.update(calendarsURI, cv, CalendarContract.Events._ID + " = ? ", cs);

        Log.v(TAG, "Event id(" + event_id + ") updated sid:" + sync_id + " done:" + ret);
        return ret >= 1;
    }

    public void syncCalendar(ContentResolver contentResolver) {
        // JSONArray online_calendars = ReadEventsFromServer();

        // For each calendar, display all the events from the previous week to the end of next week.
        for (String id : getCalendarIds(contentResolver)) {
            Log.d(TAG, "Get all events from calendar " + id);

            Cursor eventCursor = getEvents(contentResolver, Long.valueOf(id));

            System.out.println("eventCursor count=" + eventCursor.getCount());
            for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                MyCalendarEventEntry evt = new MyCalendarEventEntry(eventCursor);

                if (evt.sync_id == 0 && evt.dirty == 1) {
                    Log.v(TAG, "Create on Server");
                    CreateEventOnServer(contentResolver, evt);
                } else if (evt.sync_id > 0 && evt.dirty == 1) {
                    Log.v(TAG, "Update on Server");
                    UpdateEventOnServer(contentResolver, evt);
                } else if (evt.sync_id > 0 && evt.deleted) {
                    Log.v(TAG, "Delete on Server");
                    DeleteEventOnServer(contentResolver, evt);
                } else {
                    Log.v(TAG, "Nothing to update");
                }
            }
        }
    }

}
