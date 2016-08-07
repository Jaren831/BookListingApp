package com.example.android.booklistingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class BookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);

//        getIntent().getSerializableExtra("search");

        ListView listView = (ListView) findViewById(R.id.book_list);
        BookAdapter adapter = new BookAdapter(this);
        listView.setAdapter(adapter);

        BookAsyncTask task = new BookAsyncTask();
        task(getIntent().getStringExtra("search")).execute();
    }
}
