package com.example.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_item,parent,false);
        }

        Book book = getItem(position);

        TextView bookName = listItemView.findViewById(R.id.book_name);
        TextView bookAuthor = listItemView.findViewById(R.id.book_author);
        ImageView bookImage = listItemView.findViewById(R.id.book_image);

        bookName.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        Picasso.get().load(book.getImageUrl()).into(bookImage);

        return listItemView;
    }
}
