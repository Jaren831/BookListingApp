package com.example.android.booklistingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaren Lynch on 8/6/2016.
 */
public class BookQuery {

    public final String LOG_TAG = MainActivity.class.getSimpleName();

    String snippet;

    String mJSONResponse;

    public BookQuery(String jsonResponse) {
        mJSONResponse = jsonResponse;
    }
    public List<Book> extractBookFromJSON(String bookJSON) {

        List<Book> bookResult = new ArrayList<>();

        try {
            JSONObject baseJsonObject = new JSONObject(bookJSON);
            JSONArray itemJsonArray = baseJsonObject.getJSONArray("items");

            for (int i = 0; i < itemJsonArray.length(); i++) {
                JSONObject arrayJsonObject = itemJsonArray.getJSONObject(i);
                JSONObject volumeInfoJsonObject = arrayJsonObject.getJSONObject("volumeInfo");
                JSONObject imageInfoJsonObject = volumeInfoJsonObject.getJSONObject("imageLinks");
                JSONObject searchInfoJsonObject = arrayJsonObject.getJSONObject("searchInfo");
                // Extract out the title, author, and url values
                String title = volumeInfoJsonObject.optString("title");
                String authors = "";
                String url = volumeInfoJsonObject.optString("canonicalVolumeLink");
                String snippet = searchInfoJsonObject.optString("textSnippet");
                snippet = snippet.replace("&quot;", "'");
                snippet = snippet.replace("&#39;", "'");


                //Have not figured out how to implement below.
                String thumbnail = imageInfoJsonObject.optString("smallThumbnail");

                if (volumeInfoJsonObject.has("authors")) {

                    JSONArray authorsJsonArray = volumeInfoJsonObject.getJSONArray("authors");
                    for (int j = 0; j < authorsJsonArray.length(); j++) {
                        authors += authorsJsonArray.optString(j) + " ";
                    }

                }
                bookResult.add(new Book(authors, title, url, getBitmapFromURL(thumbnail), snippet));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }
        return bookResult;
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
