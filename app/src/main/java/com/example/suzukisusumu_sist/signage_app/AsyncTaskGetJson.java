package com.example.suzukisusumu_sist.signage_app;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mizuno on 2016/01/14.
 */
public class AsyncTaskGetJson extends AsyncTask<Void, Void, String> {

    private final static String API_URL = "http://signage.sist.ac.jp/androids/api/";
    private AsyncCallBack asyncCallBack=null;
    private String androidId;

    public AsyncTaskGetJson(AsyncCallBack asyncCallBack,String androidId) {
        this.asyncCallBack = asyncCallBack;
        this.androidId = androidId;
    }

    public interface AsyncCallBack{
        void onPostExecute(String result);
    }

    @Override
    protected String doInBackground(Void... voids) {

        String result = new String();
        ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(API_URL+androidId);
            httpPost.setEntity(new UrlEncodedFormEntity(postData, "UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String data = EntityUtils.toString(responseEntity);

                JSONObject rootObject = new JSONObject(data);

                JSONArray userArray = rootObject.getJSONArray("Videos");
                Log.d("json1_data", userArray.toString());

                for (int n = 0; n < userArray.length(); n++) {
                    // User data
                    JSONObject userObject = userArray.getJSONObject(n);
                    String url = userObject.getString("url");
                    result += url+"\r\n";
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Result",result);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        this.asyncCallBack.onPostExecute(result);
    }
}
