package com.josephmpo.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie("The Jungle Book"));
        movies.add(new Movie("Titanic"));
        movies.add(new Movie("The Revenant"));
        movies.add(new Movie("Tarzan"));

        for (int i = 1; i < 20; i++) {
            movies.add(new Movie("Thor "+i));
        }

        MovieAdapter adapter = new MovieAdapter(movies);
        RecyclerView rv = findViewById(R.id.movies_rv);

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
}