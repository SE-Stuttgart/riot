package de.uni_stuttgart.riot.android.account;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.text.format.DateUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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

class CalendarEntry{}

/**
 * FIXME Provide description FIXME Clean up this class and fix checkstyle errors.
 * CHECKSTYLE:OFF
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

    public boolean CreateEventOnServer(JSONObject entry)
    {
        HttpClient httpClient = createHttpClient();

        HttpPost httpReq = new HttpPost(API_URL+"/api/v1/calendar/");
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
            if(response.getStatusLine().getStatusCode() == 201) {
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

    public JSONObject ReadEventsFromServer()
    {
        JSONObject data = null;
        HttpClient httpClient = createHttpClient();

        HttpGet httpReq = new HttpGet(API_URL+"/api/v1/calendar/");
        httpReq.setHeader("Content-Type", "application/json");
        HttpResponse response = null;
        String result = null;
        try {
            response = httpClient.execute(httpReq);
            HttpEntity entity1 = response.getEntity();
            result = EntityUtils.toString(entity1);
            data = new JSONObject(result);
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
        //return result;
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
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Performing sync for authority " + authority);
        readCalendar(getContext().getContentResolver());

//      TODO need to use an other lib!
//        UserManagementClient cl = new UserManagementClient(URL, "htc");
//        cl.login("Yoda","YodaPW");
//        Client client = ClientBuilder.newClient();
//        cl.get(client.target("api/v1/calendar/"), "");

    }

    public void readCalendar(ContentResolver contentResolver) {
        Cursor cursor;
        // Fetch a list of all calendars synced with the device, their display names and whether the
        cursor = contentResolver.query(Calendars.CONTENT_URI,
                (new String[] { Calendars._ID , Calendars.NAME, Calendars.ACCOUNT_TYPE, Calendars.ACCOUNT_NAME}), null, null, null);

        HashSet<String> calendarIds = new HashSet<String>();
        try
        {
            System.out.println("Found #"+cursor.getCount()+" calendars");
            if(cursor.getCount() > 0)
            {
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    String acc_type = cursor.getString(2);
                    String acc_name = cursor.getString(3);

                    System.out.println("Id: " + _id + " Display Name: " + displayName + " Acc: " + acc_type + " - " + acc_name);
                    calendarIds.add(_id);
                }
            }
        }
        catch(AssertionError ex)
        {
            ex.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        JSONObject online_calendars = ReadEventsFromServer();

        // For each calendar, display all the events from the previous week to the end of next week.
        for (String id : calendarIds) {
            Log.d(TAG,"Get all events from calendar "+id);
            long now = new java.util.Date().getTime();

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");

            ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS * 10000);
            ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 10000);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    //null,
                    new String[]  {CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.ALL_DAY, "eventStatus"}, //projection
                    CalendarContract.Events.CALENDAR_ID + " = ?", //selection
                    new String[] {id}, //selection args
                    CalendarContract.Events.DTSTART+" ASC");
/*
            int i = eventCursor.getColumnCount();
            for (int j = 0; j < eventCursor.getColumnCount(); j++) {
                Log.d("#$$$$$$$$############", "##" + eventCursor.getColumnName(j));
            }
*/

            System.out.println("eventCursor count="+eventCursor.getCount());
            if(eventCursor.getCount()>0)
            {
                if(eventCursor.moveToFirst())
                {
                    do
                    {
                        final String title = eventCursor.getString(0);
                        final java.util.Date begin = new java.util.Date(eventCursor.getLong(1));
                        final java.util.Date end = new java.util.Date(eventCursor.getLong(2));
                        final String desc = eventCursor.getString(3);
                        final String loc = eventCursor.getString(4);
                        final int all_day = eventCursor.getInt(5);
                        final int dirty = eventCursor.getInt(6);

                        System.out.println("Title:"+title+" Begin:"+begin+" - "+end+"Desc: "+desc);

                        JSONObject entry = new JSONObject();
                        try {
                            entry.put("title",title);
                            entry.put("location",loc);
                            entry.put("description",desc);
                            entry.put("startTime",begin);
                            entry.put("endTime",end);
                            entry.put("allDayEvent",all_day);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //TODO check if event is in online events, ifso do an update
                        //TODO need to figure out how to get the dirty flag
                        //for(online_calendars

                        if(CreateEventOnServer(entry) == false) {
                            return;
                        }
                    }
                    while(eventCursor.moveToNext());
                }
            }
            break;
        }
    }


}
