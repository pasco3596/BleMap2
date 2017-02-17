package jp.ac.hal.blemap;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pasuco on 2017/02/15.
 */

public class MyAsycLoader extends AsyncTask<String, Integer, Void> {
    public interface MyAsyncCallback {
        void preExecute();

        void postExecute(Void result);

        void progressUpdate(int progress);

        void cancel();
    }

    @Override
    protected Void doInBackground(String... urlStr) {
        try {
            HttpURLConnection con = null;
            URL url = new URL(urlStr[0]);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            ///////////setいるのかよくわからない
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setDoInput(true);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private MyAsyncCallback myAsyncCallback = null;

    public MyAsycLoader(MyAsyncCallback myAsyncCallback) {
        this.myAsyncCallback = myAsyncCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        myAsyncCallback.preExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        myAsyncCallback.progressUpdate(progress[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        myAsyncCallback.postExecute(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        myAsyncCallback.cancel();
    }

}
