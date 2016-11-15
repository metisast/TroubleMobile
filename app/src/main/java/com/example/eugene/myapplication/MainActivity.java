package com.example.eugene.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SERVICE_ACTION = "com.example.eugene.action.CAT";
    final String SERVICE_URL = "http://89.219.23.94:14601/api/task";
    private static final String LOG_TAG = "myLogs";
    private static final int CM_DELETE_ID = 1;

    ArrayList<Map<String, Object>> contactList;
    SimpleAdapter sAdapter;

    ListView lvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Start broadcasting */
        Intent intent = new Intent(SERVICE_ACTION);
        sendBroadcast(intent);

        // определяем список и присваиваем ему адаптер
        lvMain = (ListView) findViewById(R.id.lvMain);
        registerForContextMenu(lvMain);

        new GetContacts().execute();

    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading",Toast.LENGTH_LONG).show();
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
                    contactList = new ArrayList<Map<String, Object>>(tasks.length());
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
                        contactList.add(m);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Log.e(LOG_TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sAdapter = new SimpleAdapter(MainActivity.this,
                    contactList,
                    R.layout.item,
                    new String[]{"room_name", "created_at"}, new int[]{R.id.room_name, R.id.created_at});
            lvMain.setAdapter(sAdapter);
        }
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Задача выполнена");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == CM_DELETE_ID){
            // получаем инфу о пункте списка
            AdapterView.AdapterContextMenuInfo acmi= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // удаляем Map из коллекции, используя позицию пункта в списке
            contactList.remove(acmi.position);
            // уведомляем, что данные изменились
            sAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new GetContacts().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
