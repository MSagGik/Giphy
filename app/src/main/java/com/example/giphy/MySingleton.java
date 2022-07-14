package com.example.giphy;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private  static MySingleton instance;
    private RequestQueue requestQueue;
    private  static Context ctx;

    private MySingleton(Context context){
        ctx = context;
        requestQueue = getRequestQueue();
    }

    // Создание одного экземпляра
    public  static  MySingleton getInstance(Context context){
        if(instance == null){
            instance = new MySingleton(context);
        }
        return instance;
    }

    private RequestQueue getRequestQueue() {

        if(requestQueue==null){
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    // Чтобы добавить выбранные объекты в очередь запросов
    public <T> void  addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}
