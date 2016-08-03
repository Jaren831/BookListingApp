package com.example.android.booklistingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText searchText = (EditText) findViewById(R.id.searchText);

//        TextView responseView = (TextView) findViewById(R.id.responseView);
        Button searchButton = (Button) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookActivity.class);
                String search = searchText.getText().toString();
                intent.putExtra("searchText", search);
                startActivity(intent);
            }
        });
    }

}

