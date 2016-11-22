package com.example.eugene.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GetTasksAsync.ListCallback{

    public static final String SERVICE_ACTION = "com.example.eugene.action.CAT";
    final String SERVICE_URL = "http://89.219.23.94:14601/api/task";
    private static final String LOG_TAG = "myLogs";
    private static final int CM_DELETE_ID = 1;

    ArrayList<Map<String, Object>> tasksList;
    SimpleAdapter sAdapter;

    ListView lvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Запускаем приложение в фоновом режиме */
        Intent intent = new Intent(SERVICE_ACTION);
        sendBroadcast(intent);

        // определяем список и присваиваем ему адаптер
        lvMain = (ListView) findViewById(R.id.lvMain);
        registerForContextMenu(lvMain);

        // Запускаем таск, который проверяет HTTP соединение
        // Преобразуем JSON и создаем список
        GetTasksAsync async = new GetTasksAsync(SERVICE_URL, this);
        async.execute();

    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Задача выполнена");
    }

    /* Функционал контексного меню */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Удаляем список
        if(item.getItemId() == CM_DELETE_ID){
            // получаем инфу о пункте списка
            AdapterView.AdapterContextMenuInfo acmi= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            /* Получаем идентификатор выбранного пункта (Кастыль ужасный) */
            String obj = sAdapter.getItem(acmi.position).toString();
            String str = obj.substring(1, obj.length()-1);
            String[] strArr = str.split(", ");
            String[] id = strArr[strArr.length-1].split("=");
            Log.d(LOG_TAG, id[1]);

            /* Отправляем данные на серер, что задача выполнена */
            PostTaskAsync postAsync = new PostTaskAsync(SERVICE_URL, id[1]);
            postAsync.execute();

            // удаляем Map из коллекции, используя позицию пункта в списке
            tasksList.remove(acmi.position);
            // уведомляем, что данные изменились
            sAdapter.notifyDataSetChanged();

            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Запускаем заново таск после восстановления приложения
        // Запускаем таск, который проверяет HTTP соединение
        // Преобразуем JSON и создаем список
        GetTasksAsync async = new GetTasksAsync(SERVICE_URL, this);
        async.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* Выполнение таска и рендер списка */
    @Override
    public void createListAdapter(ArrayList<Map<String, Object>> lists) {
        /* Передаем новый лист в Activity */
        tasksList = lists;
        sAdapter = new SimpleAdapter(MainActivity.this,
                tasksList,
                R.layout.item,
                new String[]{"room_name", "created_at"}, new int[]{R.id.room_name, R.id.created_at});
        lvMain.setAdapter(sAdapter);
    }
}
