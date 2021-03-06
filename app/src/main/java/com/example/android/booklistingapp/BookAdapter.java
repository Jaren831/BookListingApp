package com.example.android.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jaren Lynch on 8/2/2016.
 */
public class BookAdapter extends ArrayAdapter<Book> {
    public BookAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        Book currentBook = getItem(position);

        //Find the TextView with id author
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        //Display author of the current book
        authorTextView.setText(currentBook.getAuthor());

        //Find the TextView with id title
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        //Display the title of the current book
        titleTextView.setText(currentBook.getTitle());

        TextView snippetTextView = (TextView) listItemView.findViewById(R.id.snippet);
        snippetTextView.setText(currentBook.getSnippet());

        ImageView thumbnailView = (ImageView) listItemView.findViewById(R.id.thumbnail);
        thumbnailView.setImageBitmap(currentBook.getThumbnail());

        return listItemView;
    }
}
