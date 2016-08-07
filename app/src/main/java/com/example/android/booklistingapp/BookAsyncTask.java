package com.example.android.booklistingapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Jaren Lynch on 8/6/2016.
 */
public class BookAsyncTask extends AsyncTask<URL, Void, List<Book>> {

    public final String LOG_TAG = MainActivity.class.getSimpleName();
    private ListView mListView;
    public AsyncResponse delegate = null;
    private String mUrl;

    public BookAsyncTask(String url, AsyncResponse asyncResponse) {
        mUrl = url;
        delegate = asyncResponse;
    }

    @Override
    protected List<Book> doInBackground(URL... urls) {
        // Create URL object
        URL url = createURL(mUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Error with http request", e);
        }

        BookQuery query = new BookQuery(jsonResponse);
        return query.extractBookFromJSON(jsonResponse);
    }

    /**
     * Update the screen with the given earthquake (which was the result of the
     * {@link BookAsyncTask}).
     */
    @Override
    protected void onPostExecute(List<Book> book) {
        if (book == null) {
            return;
        }
        delegate.processFinish(book);

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

}

