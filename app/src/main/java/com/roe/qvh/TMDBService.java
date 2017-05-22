package com.roe.qvh;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by r on 7/5/17.
 */

public class TMDBService extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        String result = "";

        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {
                result+=line;
            }

//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(result.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            JSONArray jsonArray = jsonObject.optJSONArray("results");
//            for (int i=0; i<jsonArray.length(); i++) {
//                try {
//                    Log.i("result", jsonArray.getString(i));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
