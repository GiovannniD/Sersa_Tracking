package com.example.sersa_tracking;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class setting {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    public Map<String, String>  tempSavePackage = new HashMap<String, String>();
    public  Map<String, String>  tempSaveCode = new HashMap<String, String>();
    public Map<String, String>  tempSaveScannedCode = new HashMap<String, String>();
    public String Link()
    {
        return "186.1.41.92:10000";
    }



  public  String postwith(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();


        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

        public Map<String,String> JsonToMapString(JSONObject jsonObject) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();
        if(jsonObject != JSONObject.NULL) {
            Iterator<String> keysItr = jsonObject.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                map.put(key, jsonObject.get(key).toString());
            }
        }
        return map;
    }


}

