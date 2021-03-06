package com.example.android.booklistingapp;

import android.graphics.Bitmap;

/**
 * Created by Jaren Lynch on 7/4/2016.
 */
public class Book {

    //Title of book.
    private String mTitle;

    //Author of book.
    private String mAuthor;

    //Url of book on google books.
    private String mUrl;

    //Thumbnail of book on google books.
    private Bitmap mThumbnail;

    private String mSnippet;

    /**
     * @param author is the author of the book.
     * @param title is the title of the book.
     * @param url is the url of book on google bo
     * @param thumbnail is the url of the picture of the book on google books. oks.
     */
    public Book(String author, String title, String url, Bitmap thumbnail, String snippet) {
        mTitle = title;
        mAuthor = author;
        mUrl = url;
        mThumbnail = thumbnail;
        mSnippet = snippet;
    }

    /**
     * @return the title of the book.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return the author of the book.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * @return the url of the book.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * @return the thumbnail of the book.
    */
    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public String getSnippet() {
        return mSnippet;
    }
}
