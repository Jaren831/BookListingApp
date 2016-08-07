package com.example.android.booklistingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Book> books;
    BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);

        getIntent().getSerializableExtra("search");

        listView = (ListView) findViewById(R.id.book_list);
        adapter = new BookAdapter(this);
        listView.setAdapter(adapter);
        books = new ArrayList<Book>();


        String url = generateUrl();
        BookAsyncTask task = new BookAsyncTask(url, new AsyncResponse() {
            @Override
            public void processFinish(ArrayList<Book> list) {
                books.clear();
                books = list;
                listView.setAdapter(adapter);
            }
        });
        task.execute();
    }
    private String generateUrl() {
        String search = getIntent().getStringExtra("search");
        String keyword = "";
        if(search != "") {
            keyword = search;
            return "https://www.googleapis.com/books/v1/volumes?q=" + keyword;
        } else {
            return keyword;
        }
    }
}
