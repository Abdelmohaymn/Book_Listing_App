package com.example.booklistingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    TextView emptyTV;
    ProgressBar progressBar;
    BookAdapter adapter;
    SearchView searchView;
    String url = "";
    private static final int BOOK_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = findViewById(R.id.search_button);
        searchView = findViewById(R.id.search_view_book);
        emptyTV = findViewById(R.id.empty_text_view);
        progressBar = findViewById(R.id.progress_bar);
        ListView booksListView = findViewById(R.id.books_list_view);

        booksListView.setEmptyView(emptyTV);
        adapter = new BookAdapter(this,new ArrayList<>());
        booksListView.setAdapter(adapter);

        searchView.onActionViewExpanded();
        searchView.setIconified(true);
        searchView.setQueryHint("Enter a book title");


        //"https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10"

        LoaderManager loaderManager = getSupportLoaderManager();

        emptyTV.setVisibility(View.GONE);
        if (!checkConnection()){
            //loaderManager.initLoader(BOOK_LOADER_ID,null,this);
            emptyTV.setVisibility(View.VISIBLE);
            emptyTV.setText("No internet connection.");
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                adapter.clear();
                emptyTV.setVisibility(View.GONE);
                if (checkConnection()){
                    loaderManager.restartLoader(BOOK_LOADER_ID,null,MainActivity.this);
                }else {
                    progressBar.setVisibility(View.GONE);
                    emptyTV.setVisibility(View.VISIBLE);
                    emptyTV.setText("No internet Connection");
                }
            }
        });

        booksListView.setOnItemClickListener((parent, view, position, id) -> {
            Book book = adapter.getItem(position);
            Uri bookUri = Uri.parse(book.getInfoLink());
            Intent webIntent = new Intent(Intent.ACTION_VIEW,bookUri);
            startActivity(webIntent);
        });

    }

    private String makeUrl(String value){
        StringBuilder myUrl = new StringBuilder();
        if (value.contains(" ")){
            value = value.replace(" ","+");
        }
        myUrl.append("https://www.googleapis.com/books/v1/volumes?q=")
        .append(value).append("&maxResults=40");
        return myUrl.toString();
    }

    private Boolean checkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo!=null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }

    }


    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {
        url = makeUrl(searchView.getQuery().toString());
        return new BookLoader(this,url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> books) {
        progressBar.setVisibility(View.GONE);
        adapter.clear();
        emptyTV.setVisibility(View.VISIBLE);
        emptyTV.setText("No books found.");

        if (books != null && !books.isEmpty()){
            adapter.addAll(books);
        }


    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        adapter.clear();
    }
}