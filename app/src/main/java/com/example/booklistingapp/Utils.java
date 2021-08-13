package com.example.booklistingapp;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utils {


    private Utils(){

    }

    public static List<Book> fetchBooksData(String requestUrl){
        List<Book> books ;

        URL url = createUrl(requestUrl);
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e) {

        }
        books = extractBooks(jsonResponse);
        return books;
    }

    private static URL createUrl(String stringUrl){
        if (stringUrl == null){
            return null;
        }
        URL url = null;
        try {
            url = new URL(stringUrl);
        }catch (MalformedURLException e){

        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null){
            return null;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection() ;
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }

        }catch (IOException e){

        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }

        }
        return output.toString();
    }

    private static List<Book> extractBooks(String jsonRes){
        if (TextUtils.isEmpty(jsonRes)){
            return null;
        }

        List<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonObject = new JSONObject(jsonRes);
            JSONArray itemsArray = baseJsonObject.getJSONArray("items");

            for (int i=0; i<itemsArray.length(); i++){
                JSONObject currentBook = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                String title = volumeInfo.getString("title");
                String author = "";

                if (volumeInfo.has("authors")){
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    if (!volumeInfo.isNull("authors")){
                         author = authors.getString(0);
                    }else {
                        author = "Unknown author";
                    }
                }else {
                    author = "Unknown author";
                }

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String imageUrl = imageLinks.getString("smallThumbnail");
                String infoLink = volumeInfo.getString("infoLink");

                Book book = new Book(title,author,imageUrl,infoLink);
                books.add(book);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return books;

    }

}

