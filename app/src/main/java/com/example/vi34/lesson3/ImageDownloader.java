package com.example.vi34.lesson3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


/**
 * Created by vi34 on 26.09.14.
 */
public class ImageDownloader {

    public void search(String query) {
        new DownloadImageTask().execute(query);
    }

    private class DownloadImageTask extends AsyncTask<String, Bitmap, Bitmap[]> {
        private int done = 0;

        protected Bitmap[] doInBackground(String... query) {
            Bitmap[] bmp = new Bitmap[10];
            URL url;
            URLConnection connection;
            String line;
            JSONObject json;
            JSONArray resultArray;
            String[] imageLinks = new String[10];
            InputStream input;

            try {
                // need to modify query (check for spaces, and prohibited symbols)
                query[0] = URLEncoder.encode(query[0], "UTF-8");

            } catch (Exception e) {
                Log.e("ASYNC", "encode query problems");
            }
                for (int k = 0; done < 10; k++) {

                    try {


                        url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +
                                "v=1.0&q=" + query[0] + "&imgsz=medium&rsz=8&userip=INSERT-USER-IP" + "&start=" + (k * 8));
                        connection = url.openConnection();
                        connection.addRequestProperty("Referer", "ITMO homework app");
                        StringBuilder builder = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        json = new JSONObject(builder.toString());
                        resultArray = json.getJSONObject("responseData").getJSONArray("results");


                        for (int i = 0; i < 8; i++) {
                            try {

                                json = resultArray.getJSONObject(i);
                                imageLinks[done] = json.getString("url");
                                url = new URL(imageLinks[done]);
                                connection = url.openConnection();
                                connection.setDoInput(true);

                                connection.connect();

                                input = connection.getInputStream();
                                bmp[done] = BitmapFactory.decodeStream(input);
                                publishProgress(bmp[done]);
                                done++;


                            } catch (IOException ie) {
                                Log.e("Error", "Connection problems");

                            } catch (JSONException e) {
                            }

                        }
                    } catch (Exception e)
                    {

                    }

                }
            return bmp;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            SecondActivity.imageView[done].setImageBitmap(values[0]);
            SecondActivity.imageView[done].invalidate();
        }

        protected void onPostExecute(Bitmap[] result) {
            Log.i("ASYNC", "Done");
            SecondActivity.bmp = result;
            //---- Bad solution... fix it later
            for (int i = 0; i < 10; i++) {
                SecondActivity.imageView[i].setImageBitmap(result[i]);
                SecondActivity.imageView[i].invalidate();
            }
            //----
        }
    }
}
