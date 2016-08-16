package com.example.android.booklistingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class BookActivity extends AppCompatActivity {

    ListView listView;
    BookAdapter adapter;

    //For accompanying thumbnail. Have not figured out how to implement.
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);

        listView = (ListView) findViewById(R.id.book_list);
        adapter = new BookAdapter(this);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.empty));

        String url = generateUrl();
        BookAsyncTask task = new BookAsyncTask(url, new AsyncResponse() {
            @Override
            public void processFinish(List<Book> bookList) {
                adapter.clear();
                adapter.addAll(bookList);
                adapter.notifyDataSetChanged();
            }
        });
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            task.execute();
        } else {
            Intent noConnectionIntent = new Intent(BookActivity.this, NoConnectionActivity.class);
            startActivity((noConnectionIntent));
        }

        /**
         * Goes to book url on click.
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }
    private String generateUrl() {
        String search = getIntent().getStringExtra("search");
        String searchURL = "";
        if(search != "") {
            try {
                //Encodes any spaces in search text inputted by user.
                searchURL = "https://www.googleapis.com/books/v1/volumes?q=" + URLEncoder.encode(search, "UTF-8");
                return searchURL;
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError("UTF-8 is unknown");
            }
        } else {
            return searchURL;
        }
    }
}
