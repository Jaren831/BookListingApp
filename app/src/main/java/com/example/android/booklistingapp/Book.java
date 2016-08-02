package com.example.android.booklistingapp;

/**
 * Created by Jaren Lynch on 7/4/2016.
 */
public class Book {

    //Title of book.
    private String mTitle;

    //Author of book.
    private String mAuthor;

    /**
     * @param author is the author of the book.
     * @param title is the title of the book.
     */
    public Book(String author, String title) {
        mTitle = title;
        mAuthor = author;
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
}
