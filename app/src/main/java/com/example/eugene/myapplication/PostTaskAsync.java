package com.example.eugene.myapplication;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;

/**
 * Created by eugene on 11/19/16.
 */

public class PostTaskAsync extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = "myLogs";
    private final String SERVICE_URL;
    private final String task_id;

    public PostTaskAsync(String service_url, String _task_id) {
        SERVICE_URL = service_url;
        this.task_id = _task_id;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(Void... voids) {
        PostTaskHandler post = new PostTaskHandler();
        //String json = post.deleteTask("206");
        try {
            String response = post.deleteTask(SERVICE_URL, "task_id=" + task_id);
            Log.d(LOG_TAG, response);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
