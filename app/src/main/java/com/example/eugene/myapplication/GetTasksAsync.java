package com.example.eugene.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eugene on 11/15/16.
 */

public class GetTasksAsync extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = "myLogs";

    private final String SERVICE_URL;
    private ArrayList<Map<String, Object>> tasksList;
    private ListCallback listCallback;

    public GetTasksAsync(String service_url, final ListCallback _listCallback) {
        SERVICE_URL = service_url;
        listCallback = _listCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Toast.makeText((Context) listCallback, "Json Data is downloading", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String jsonStr = null;
        try {
            jsonStr = sh.makeServiceCall(SERVICE_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "Response from url: " + jsonStr);
        if(jsonStr != null){
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray tasks = jsonObject.getJSONArray("tasks");

                // looping through All Contacts
                tasksList = new ArrayList<Map<String, Object>>(tasks.length());
                Map<String, Object> m;
                for (int i = 0; i < tasks.length(); i++) {
                    JSONObject t = tasks.getJSONObject(i);
                    String id = t.getString("id");
                    String room_name = t.getString("room_name");
                    String created_at = t.getString("created_at");

                    // adding each child node to HashMap key => value
                    m = new HashMap<String, Object>();
                    m.put("id", id);
                    m.put("room_name", room_name);
                    m.put("created_at", created_at);

                    // adding contact to contact list
                    tasksList.add(m);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Log.e(LOG_TAG, "Couldn't get json from server.");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        /* Передаем наш новый лист */
        if(listCallback!=null)
            listCallback.createListAdapter(tasksList);

    }

    public interface ListCallback{
        public void createListAdapter(ArrayList<Map<String, Object>> tasksList);
    }

}
