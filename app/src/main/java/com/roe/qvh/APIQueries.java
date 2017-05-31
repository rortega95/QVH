package com.roe.qvh;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by r on 22/5/17.
 */

public class APIQueries {

    public ArrayList getListDiscover() {

        TMDBService service = new TMDBService();
        Movie movie;
        ArrayList<Movie> arrayList = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            String s = service.execute("https://api.themoviedb.org/3/discover/movie?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es&sort_by=popularity.desc&include_adult=false&include_video=false&page=1").get();
            jsonObject = new JSONObject(s);
            jsonArray = jsonObject.optJSONArray("results");
            for (int i=0; i<jsonArray.length(); i++) {
                //Log.i("jsonArray "+i, jsonArray.getJSONObject(i).getString("title"));
                TMDBService service2 = new TMDBService();
                String r = service2.execute("https://api.themoviedb.org/3/movie/"+jsonArray.getJSONObject(i).getInt("id")+"/videos?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es").get();
                JSONObject jsonObject2 = new JSONObject(r);
                JSONArray jsonArray2 = jsonObject2.optJSONArray("results");
                String key;
                if (jsonArray2.length()==0) {
                    key = null;
                } else {
                    key = jsonArray2.getJSONObject(0).getString("key");
                }

                movie = new Movie(
                        jsonArray.getJSONObject(i).getInt("id"),
                        jsonArray.getJSONObject(i).getString("title"),
                        jsonArray.getJSONObject(i).getString("poster_path"),
                        key,
                        jsonArray.getJSONObject(i).getString("overview")
                );
                //Log.i("movie", movie.toString());
                arrayList.add(movie);
            }

            Log.i("list_Movies_length", ""+arrayList.size());
            for (Movie m: arrayList) {
                Log.i("movie", m.toString());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;
    }
}
