package com.example.android.booklistingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.List;

public class BookActivity extends AppCompatActivity {

    ListView listView;
    BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);

        listView = (ListView) findViewById(R.id.book_list);
        adapter = new BookAdapter(this);
        listView.setAdapter(adapter);


        String url = generateUrl();
        BookAsyncTask task = new BookAsyncTask(url, new AsyncResponse() {
            @Override
            public void processFinish(List<Book> bookList) {
                adapter.clear();
                adapter.addAll(bookList);
                adapter.notifyDataSetChanged();
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
