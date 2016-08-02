package com.example.android.booklistingapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jaren Lynch on 8/1/2016.
 */
public class BookActivity extends AppCompatActivity{

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    //Baseline url for google books api.
    final static String API_KEY = "key=AIzaSyC2NqsEr1TAJ87RGYOv00QAEDYqLOOM";

    private static final String BOOK_JSON_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);

        final ArrayList<Book> books = QueryBooks.extractBooks();
        // Kick off an {@link AsyncTask} to perform the network request
        BookAsyncTask task = new BookAsyncTask();
        task.execute();
    }

    private static ArrayList<Book> extractBooks() {
        ArrayList<Book> books = new ArrayList<>();
        try {
            JSONObject jsonRootObject = new JSONObject(BOOK_JSON_URL);
        };
    }


}

