package com.nicholas.httpwrapper;

/**
 * Created by Nicholas on 8/30/2017.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpWrapper extends AsyncTask<Request, Void, String> {

    public OkHttpClient client = new OkHttpClient();
    public String authToken;

    public OkHttpWrapper(String auth) {
        authToken = auth;
    }

    protected void authAsyncGet(String url) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

        this.execute(request);
    }

    protected String doInBackground(Request... requests) {
        try {
            Request request = requests[0];
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.d("Except GET-ing", e.toString());
            return "Exception caught";
        }
    }

    protected void onPostExecute(String response) {
        return;
    }

}
