package com.example.android.booklistingapp;

import android.os.AsyncTask;
import android.util.Log;

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
 * Created by Jaren Lynch on 8/2/2016.
 */
public class QueryBooks {

    public static final String BOOK_JSON_URL = "https://www.googleapis.com/books/v1/volumes?q=robert";

    public static final String LOG_TAG = BookActivity.class.getSimpleName();


    private QueryBooks() {
    }
    private class BookAsyncTask extends AsyncTask<URL, Void, Book> {
        @Override
        protected Book doInBackground(URL... urls) {
            //Create url object
            URL url = createURL(BOOK_JSON_URL + "robert");

            //Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem receiving the book JSON result.", e);
            }

            //Extract relevant fields from the JSON response
            Book book = extractItemFromJson(jsonResponse);

            //Return the object
            return book;
        }
        @Override
        protected void onPostExecute(Book book) {
            if (book == null) {
                return;
            }
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
         * first book from teh input bookJSON string
         */
        private Book extractItemFromJson(String bookJSON) {
            try {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);
                JSONArray itemArray = baseJsonResponse.getJSONArray("items");

                // If there are results in the items array
                if (itemArray.length() > 0) {
                    //Extract out the first item
                    JSONObject firstItem = itemArray.getJSONObject(0);
                    JSONObject volumeInfo = firstItem.getJSONObject("volumeInfo");

                    //Extract out the author, title, and url
                    String title = volumeInfo.getString("title");
                    String author = volumeInfo.getString("authors");
                    String url = volumeInfo.getString("canonicalVolumeLink");

                    //Create a new book object
                    extractBooks(itemArray);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the book JSON result", e);
            }
            return null;
        }
    }

    public static ArrayList<Book> extractBooks(JSONArray item) {

        // Create an empty ArrayList that we can start adding books to
        ArrayList<Book> books = new ArrayList<>();


        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs

        try {
            for (int i = 0; i < item.length(); i++) {
                JSONObject currentBook = item.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                String author = volumeInfo.optString("author");
                String title = volumeInfo.optString("title");
                String url = volumeInfo.optString("canonicalVolumeLink");
                books.add(new Book(author, title, url));

            }
        } catch (JSONException e) {
            //If an e error is thrown, catch exception so app doesn't crash. Print log message
            //with the message from the exception.
            Log.e("QueryBooks", "Problem parsing the book JSON results", e);
        }

        //Return list of books.
        return books;

    }

}
