package de.uni_stuttgart.riot.android.management;

/**
 * Created by Benny on 09.01.2015.
 */
public class JSONAsyncTask {//extends AsyncTask<String, String, JSONObject> {
//
//    private OLDManagementFragment OLDManagementFragment;
//    private String url;
//    private ProgressDialog progressDialog;
//
//    /**
//     * Constructor
//     *
//     * @param managementListFragment the calling fragment
//     */
//    public JSONAsyncTask(OLDManagementFragment managementListFragment, String url) {
//        this.OLDManagementFragment = managementListFragment;
//        this.progressDialog = new ProgressDialog(this.OLDManagementFragment.getActivity());
//        this.progressDialog.setMessage(this.OLDManagementFragment.getActivity().getString(R.string.processDialogExecute));
//        this.progressDialog.setIndeterminate(false);
//        this.progressDialog.setCancelable(true);
//        this.url = url;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        this.progressDialog.show();
//    }
//
//    @Override
//    protected JSONObject doInBackground(String... strings) {
//        // ToDo Tmp TEST ->>
//        if (url.startsWith("{")) {
//            try {
//                JSONObject jsonObject = new JSONObject(url);
//                return jsonObject;
//            } catch (JSONException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//        // <<----------------
//
//        JSONParser jsonParser = new JSONParser();
//        // Get JSONObject from URL
//        JSONObject jsonObject = jsonParser.getJSONFromUrl(this.url);
//        return jsonObject;
//    }
//
//    @Override
//    protected void onPostExecute(JSONObject jsonObject) {
//        this.progressDialog.dismiss();
//        this.OLDManagementFragment.doOnPostExecute(jsonObject);
//    }
}
