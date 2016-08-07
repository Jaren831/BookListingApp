package com.example.android.booklistingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText searchText;
    Button searchButton;
    ListView listView;
    ArrayList<Book> books;
    BookAdapter adapter;

    /** URL to query Google Books for book information */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        books = new ArrayList<Book>();
        listView = (ListView) findViewById(R.id.book_list);
        searchText = (EditText) findViewById(R.id.searchText);
        searchButton = (Button) findViewById(R.id.searchButton);
        adapter = new BookAdapter(this);
        listView.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookIntent = new Intent(MainActivity.this, BookActivity.class);
                bookIntent.putExtra("search", searchText.getText().toString());
                startActivity(bookIntent);


//                BookAsyncTask task  = new BookAsyncTask();
//                task.execute();
            }
        });
    }

//    /**
//     * Update the screen to display information from the given {@link Book}.
//     */
//    private void updateUi(List<Book> bookList) {
//        adapter.clear();
//        adapter.addAll(bookList);
//        adapter.notifyDataSetChanged();
//    }

}

