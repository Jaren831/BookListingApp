package com.example.android.booklistingapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    EditText searchText;
    Button searchButton;
    ListView listView;
    ArrayList<Book> books;
    BookAdapter adapter;

    /** URL to query Google Books for book information */
    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    public final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        books = new ArrayList<Book>();
        listView = (ListView) findViewById(R.id.book_list);
        searchText = (EditText) findViewById(R.id.searchText);
        searchButton = (Button) findViewById(R.id.searchButton);
        adapter = new BookAdapter(this, books);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookAsyncTask task  = new BookAsyncTask();
                task.execute();
            }
        });
    }

    /**
     * Update the screen to display information from the given {@link Book}.
     */
    private void updateUi(Book book) {
        // Display the book author in the UI
        TextView authorTextView = (TextView) findViewById(R.id.author);
        authorTextView.setText(book.getAuthor());

        // Display the book title in the UI
        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(book.getTitle());
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, Book> {

        /** Tag for the log messages */

        @Override
        protected Book doInBackground(URL... urls) {
            // Create URL object
            URL url = createURL(BOOK_REQUEST_URL + searchText);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e){
                Log.e(LOG_TAG, "Error with http request", e);
            }

            return extractBookFromJSON(jsonResponse);
        }

        /**
         * Update the screen with the given earthquake (which was the result of the
         * {@link BookAsyncTask}).
         */
        @Override
        protected void onPostExecute(Book book) {
            if (book == null) {
                return;
            }
            updateUi(book);
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createURL(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception){
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try  {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();
                if(urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG,"Error response code : " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem receiving the book JSON result", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return  jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
        private Book extractBookFromJSON(String bookJSON) {

            try {
                JSONObject baseJsonObject = new JSONObject(bookJSON);
                JSONArray itemJsonArray = baseJsonObject.optJSONArray("items");

                for (int i = 0; i < itemJsonArray.length(); i++) {
                    JSONObject arrayJsonObject = itemJsonArray.optJSONObject(i);
                    JSONObject volumeInfoJsonObject = arrayJsonObject.optJSONObject("volumeInfo");

                    // Extract out the title, author, and url values
                    String title = volumeInfoJsonObject.optString("title");
                    String authors = "";
                    String url = volumeInfoJsonObject.optString("canonicalVolumeLink");
                    JSONArray authorsJsonArray = volumeInfoJsonObject.optJSONArray("authors");
                    for (int j = 0; j < authorsJsonArray.length(); j++) {
                        authors += authorsJsonArray.optString(j) + " ";
                    }
                    books.add(new Book(authors, title, url));
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            }

            return null;
        }
    }
}

