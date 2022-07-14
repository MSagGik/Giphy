package com.example.giphy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DataAdapter.OnItemClickListener {

    private Document doc; // Объект для записи веб страницы с помощью библиотеки Jsoup
    private Thread secThread; // Второстепенный поток (для разгрузки экрана)
    private Runnable runnable; // Место запускания кода
    private ListView listView; // Окно для курса валют
    private CustomArrayAdapter adapter; // Задание C.Arr.Adapter
    private List<ListItemClass> arrayList; // Задание массива курса валют
    private ImageView GifDay;
    private RadioButton BtnUSA;
    private RadioButton BtnEuro;
    private RadioButton BtnUan;
    public static final String API_KEY = "9Ewd195qjvCdOwQ3QI7vwQWWj0eO9sq8";
    public static final String BASE_URL = "https://api.giphy.com/v1/gifs/random?api_key=";
    public static final String URL_MED = "&tag=";
    public static final String URL_TAG1 = "rich";
    public static final String URL_TAG2 = "broke";
    public static final String URL_END = "&rating=g";
    public static String url;
    public static final String url1 = BASE_URL + API_KEY + URL_MED + URL_TAG1 + URL_END;
    public static final String url2 = BASE_URL + API_KEY + URL_MED + URL_TAG2 + URL_END;


    RecyclerView rView;
    ArrayList<DataModel> dataModelArrayList = new ArrayList<>();
    DataAdapter dataAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        Calendar calendar = Calendar.getInstance(); // Индикация даты
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        TextView DataDay = findViewById(R.id.DateDay);
        DataDay.setText(currentDate);

    }

    // метод показа "гиф"
    public void giphyUrl(String... url) {
        rView = findViewById(R.id.recyclerView);
        rView.setLayoutManager(new GridLayoutManager(this, 1)); // количество гиф в строке
        rView.addItemDecoration((new SpaceItemDecoration(0))); // растояние между гифами
        rView.setHasFixedSize(true);

        // Получение данных

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, MainActivity.url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    dataModelArrayList.clear();
                    JSONObject dataArray = response.getJSONObject("data");
                    JSONObject obj = dataArray.getJSONObject("images");
                    JSONObject obj1 = obj.getJSONObject("downsized_medium");
                    String sourceUrl = obj1.getString("url");
                    dataModelArrayList.add(new DataModel(sourceUrl));
                    dataAdapter = new DataAdapter(MainActivity.this, dataModelArrayList);
                    rView.setAdapter(dataAdapter);
                    dataAdapter.setOnItemClickListener(MainActivity.this::onItemClick);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Добавление данных в очередь запросов
        MySingleton.getInstance(this).addToRequestQueue(objectRequest);
    }

    @Override
    public void onItemClick ( int pos) {
        Intent fullView = new Intent(this, FullActivity.class);
        DataModel clickedItem = dataModelArrayList.get(pos);
        fullView.putExtra("imageUrl", clickedItem.getImageUrl());
        startActivity(fullView);
    }

    private void init() {

        listView = findViewById(R.id.listView); // Поиск окна для курса валют
        arrayList = new ArrayList<>(); // Создание пустого массива
        adapter = new CustomArrayAdapter(this, R.layout.list_item_1, arrayList, getLayoutInflater());
        listView.setAdapter(adapter);
        runnable = new Runnable() {
            @Override
            public void run() {
                getWeb();
            }
        };
        secThread = new Thread(runnable); // Создание второстепенного потока
        secThread.start(); // Запуск второстепенного потока
    }

    private void getWeb()// Функция забора страницы и записи её в объект doc (парсинг сервиса "www.banki.ru")
    {
        try {
            doc = Jsoup.connect("https://www.banki.ru/products/currency/cb/").get(); // Веб страница, к которой подключаемся с помощью библиотеки Jsoup
            Elements tables = doc.getElementsByTag("table");// Массив из Jsoup всех таблиц
            Element our_table = tables.get(0); // Создание элемента таблицы
            Elements elements_from_table = our_table.children(); // Дробление таблицы на элементы
            Element currency = elements_from_table.get(1); // Создание участка элемента таблицы
            Elements dollar_elements = currency.children(); // Дробление участка таблицы на состовляющие, где "0" - это доллар

            Log.d("MyLog", "table size:" + dollar_elements.get(0).text()); // Выделение первой состовляющей из выбранного участка таблицы

            ListItemClass items = new ListItemClass(); // Заполнение массива тестовыми значениями

            this.BtnUSA = (RadioButton) this.findViewById(R.id.BtnUSA);
            this.BtnEuro = (RadioButton) this.findViewById(R.id.BtnEuro);
            this.BtnUan = (RadioButton) this.findViewById(R.id.BtnUan);

            // задание в get("k") "k"=0 - Доллар США, "к"=1 - Евро, "к"=16 - Китайский Юань
            this.BtnUSA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.clear(); // Очистка массива
                    secThread = new Thread(runnable); // Создание второстепенного потока
                    secThread.start(); // Запуск второстепенного потока
                    items.setData_1(currency.children().get(0).child(2).text());
                    items.setData_2(" / Рубль РФ");
                    items.setData_3(currency.children().get(0).child(3).text().substring(0, 5));
                    String str4a = currency.children().get(0).child(4).text().substring(0, 5);
                    str4a = str4a.replaceAll(",", ".");
                    float fa = Float.parseFloat(str4a);
                    String qa = String.format("%.2f",fa);
                    qa = qa.replaceAll(",", ".");
                    items.setData_4(qa);
                    arrayList.add(items);
                    if (fa < 0) {
                        url=url1;
                        MainActivity.this.giphyUrl();
                    } else if (fa > 0) {
                        url=url2;
                        MainActivity.this.giphyUrl();
                    }
                }
            });
            this.BtnEuro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.clear(); // Очистка массива
                    secThread = new Thread(runnable); // Создание второстепенного потока
                    secThread.start(); // Запуск второстепенного потока
                    items.setData_1(currency.children().get(1).child(2).text());
                    items.setData_2(" / Рубль РФ");
                    items.setData_3(currency.children().get(1).child(3).text().substring(0, 5));
                    String str4b = currency.children().get(1).child(4).text().substring(0, 5);
                    str4b = str4b.replaceAll(",", ".");
                    float fc = Float.parseFloat(str4b);
                    String qc = String.format("%.2f",fc);
                    qc = qc.replaceAll(",", ".");
                    items.setData_4(qc);
                    arrayList.add(items);
                    if (fc < 0) {
                        url=url1;
                        MainActivity.this.giphyUrl();
                    } else if (fc > 0) {
                        url=url2;
                        MainActivity.this.giphyUrl();
                    }
                }
            });
            this.BtnUan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arrayList.clear(); // Очистка массива
                    secThread = new Thread(runnable); // Создание второстепенного потока
                    secThread.start(); // Запуск второстепенного потока
                    items.setData_1(currency.children().get(16).child(2).text());
                    items.setData_2(" / Рубль РФ");
                    String str3c = currency.children().get(16).child(3).text().substring(0, 5);
                    str3c = str3c.replaceAll(",", ".");
                    float fe = Float.parseFloat(str3c);
                    float ye = fe/10;
                    String qe = String.format("%.2f",ye);
                    qe = qe.replaceAll(",", ".");
                    items.setData_3(qe);
                    String str4c = currency.children().get(16).child(4).text().substring(0, 5);
                    str4c = str4c.replaceAll(",", ".");
                    float ff = Float.parseFloat(str4c);
                    float yf = ff/10;
                    String qf = String.format("%.2f",yf);
                    qf = qf.replaceAll(",", ".");
                    items.setData_4(qf);
                    arrayList.add(items);
                    if (yf < 0) {
                        url=url1;
                        MainActivity.this.giphyUrl();
                    } else if (yf > 0) {
                        url=url2;
                        MainActivity.this.giphyUrl();
                    }
                }
            });

            runOnUiThread(new Runnable() { //запуск на основном потоке, а не на второстепенном иначе адаптер не запустится
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

