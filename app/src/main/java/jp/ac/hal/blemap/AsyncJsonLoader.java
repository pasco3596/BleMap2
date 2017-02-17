package jp.ac.hal.blemap;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pasuco on 2016/12/15.
 */
public class AsyncJsonLoader extends AsyncTask<String, Integer, JSONObject> {
    public interface AsyncCallback {
        void preExecute();

        void postExecute(JSONObject result);

        void progressUpdate(int progress);

        void cancel();
    }

    private AsyncCallback mAsyncCallback = null;

    public AsyncJsonLoader(AsyncCallback asyncCallback) {
        mAsyncCallback = asyncCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAsyncCallback.preExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        mAsyncCallback.progressUpdate(progress[0]);
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        mAsyncCallback.postExecute(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mAsyncCallback.cancel();
    }

    @Override
    protected JSONObject doInBackground(String... urlStr) {
        try {
            HttpURLConnection con = null;
            URL url = new URL(urlStr[0]);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            ///////////setいるのかよくわからない
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            //////////////////////////////////

            con.connect();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

           br.close();
            return new JSONObject("{\"items\":"+sb.toString()+"}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}