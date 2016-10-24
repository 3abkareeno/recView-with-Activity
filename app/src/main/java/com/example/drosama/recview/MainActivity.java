package com.example.drosama.recview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerAdapter recAdapter;
    private static int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Recycler View Initialization
        mRecyclerView = (RecyclerView) findViewById(R.id.movies_grid);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recAdapter = new RecyclerAdapter(this);
        mRecyclerView.setAdapter(recAdapter);

        // Displaying initial set of movies
        popularMovies();

        // Next | Previous Button Handling
        Button buttonPrev = (Button) findViewById(R.id.prev_button);
        if (page == 1){
            buttonPrev.setVisibility(View.GONE);
        }else{
            buttonPrev.setVisibility(View.VISIBLE);
        }
        buttonPrev.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                prevPage();
            }
        });

        Button buttonNext = (Button) findViewById(R.id.next_button);
        buttonNext.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                nextPage();
            }
        });
    }
    private void nextPage(){
        page++;
        new GetMovies(new GetMovies.SetOnSuccess() {
            @Override
            public void onSuccess(Movie[] result) {
                if (result != null) {
                    recAdapter.updateMovies(result);
                }
            }
        },this ).execute("https://api.themoviedb.org/3/movie/popular?api_key=33be9732d89e6e88a6922c96c52332ef&language=en-US&page="+page);
        Button buttonPrev = (Button) findViewById(R.id.prev_button);
        if (page == 1){
            buttonPrev.setVisibility(View.GONE);
        }else{
            buttonPrev.setVisibility(View.VISIBLE);
        }
    }
    private void prevPage(){
        page--;
        new GetMovies(new GetMovies.SetOnSuccess() {
            @Override
            public void onSuccess(Movie[] result) {
                if (result != null) {
                    recAdapter.updateMovies(result);
                }
            }
        }, this).execute("https://api.themoviedb.org/3/movie/popular?api_key=33be9732d89e6e88a6922c96c52332ef&language=en-US&page="+page);
        Button buttonPrev = (Button) findViewById(R.id.prev_button);
        if (page == 1){
            buttonPrev.setVisibility(View.GONE);
        }else{
            buttonPrev.setVisibility(View.VISIBLE);
        }
    }
    private void popularMovies(){
        setTitle("Popular Movies");
        new GetMovies(new GetMovies.SetOnSuccess() {
            @Override
            public void onSuccess(Movie[] result) {
                if (result != null) {
                    recAdapter.updateMovies(result);
                }
            }
        }, this).execute("https://api.themoviedb.org/3/movie/popular?api_key=33be9732d89e6e88a6922c96c52332ef&language=en-US&page="+page);
    }
}
