package de.uni_stuttgart.riot.android.management;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import de.enpro.android.riot.R;

/**
 * Created by Benny on 09.01.2015.
 */
public class JSONAsyncTask extends AsyncTask<String, String, JSONObject> {

    private ManagementFragment managementFragment;
    private String url;
    private ProgressDialog progressDialog;

    /**
     * Constructor
     *
     * @param managementListFragment the calling fragment
     */
    public JSONAsyncTask(ManagementFragment managementListFragment, String url) {
        this.managementFragment = managementListFragment;
        this.progressDialog = new ProgressDialog(this.managementFragment.getActivity());
        this.progressDialog.setMessage(this.managementFragment.getActivity().getString(R.string.processDialogExecute));
        this.progressDialog.setIndeterminate(false);
        this.progressDialog.setCancelable(true);
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        // ToDo Tmp TEST ->>
        if (url.startsWith("{")) {
            try {
                JSONObject jsonObject = new JSONObject(url);
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        // <<----------------

        JSONParser jsonParser = new JSONParser();
        // Get JSONObject from URL
        JSONObject jsonObject = jsonParser.getJSONFromUrl(this.url);
        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        this.progressDialog.dismiss();
        this.managementFragment.doOnPostExecute(jsonObject);
    }
}
