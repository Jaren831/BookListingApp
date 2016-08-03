package com.example.android.booklistingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Jaren Lynch on 8/1/2016.
 */
public class BookActivity extends AppCompatActivity{

    /** Tag for the log messages */
    public static final String LOG_TAG = BookActivity.class.getSimpleName();

    //Baseline url for google books api.
    public static final String BOOK_JSON_URL = "https://www.googleapis.com/books/v1/volumes?q=robert";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);

        BookAsyncTask task = new BookAsyncTask();
        task.execute();

        //Create a list of books.
        final ArrayList<Book> books = new ArrayList<Book>();

        //Create adapter for each book in list. Create list items for each book in list.
        BookAdapter adapter = new BookAdapter(this, books);

        //Find the ListView in the view hierarchy.
        ListView listView = (ListView) findViewById(R.id.book_list);

        //Sets list view to the data organized by the adapter.
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book book = books.get(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(book.getUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }
    /**
     * Update the screen to display information from the given {@link Book}.
     */
    private void updateUi(Book book) {
        // Display the book title in the UI
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(book.getTitle());

        // Display the book author in the UI
        TextView authorTextView = (TextView) findViewById(R.id.author);
        authorTextView.setText(book.getAuthor());
    }
    private class BookAsyncTask extends AsyncTask<URL, Void, Book> {
        @Override
        protected Book doInBackground(URL... urls) {
            //Create url object
            URL url = createURL(BOOK_JSON_URL + getIntent().getStringExtra("search"));
            System.out.print(url);

            //Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem receiving the book JSON result.", e);
            }

            //Extract relevant fields from the JSON response is a string from readfromstream
            // --> extractfromjson
            Book book = extractItemFromJson(jsonResponse);

            //Return the object
            return book;
        }
        @Override
        protected void onPostExecute(Book book) {
            if (book == null) {
                return;
            }

            updateUi(book);
        }

        /**
         * Returns new URL object from the given string url.
         */
        private URL createURL(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a string as the response.
         */

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem receiving the book json result.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    //function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the InputStream into a string which contains the whole JSON response from the
         * server
         */
        @NonNull
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                        Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Return a Book object by parsing out information about the
         * first book from the input bookJSON string
         */
        @Nullable
        private Book extractItemFromJson(String inputStream) {
            try {
                JSONObject baseJsonResponse = new JSONObject(inputStream);
                JSONArray items = baseJsonResponse.getJSONArray("items");

                // If there are results in the items array
                if (items.length() > 0) {
                    extractBooks(items);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the book JSON result", e);
            }
            return null;
        }
        public ArrayList<Book> extractBooks(JSONArray item) {

            // Create an empty ArrayList that we can start adding books to
            ArrayList<Book> books = new ArrayList<>();

            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs

            try {
                for (int i = 0; i < item.length(); i++) {
                    JSONObject currentBook = item.getJSONObject(i);
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    String author = volumeInfo.getString("authors");
                    String title = volumeInfo.getString("title");
                    String url = volumeInfo.getString("canonicalVolumeLink");
                    books.add(new Book(author, title, url));
                }
            } catch (JSONException e) {
                //If an e error is thrown, catch exception so app doesn't crash. Print log message
                //with the message from the exception.
                Log.e("extractBooks", "Problem parsing the book JSON results", e);
            }

            //Return list of books.
            return books;

        }
    }
}

