package com.roe.qvh;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MovieLuckySearchFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public MovieLuckySearchFragment() {
        // Required empty public constructor
    }

    static ArrayList<Movie> arrayListMovies = new ArrayList<>();
    static ImageView imageViewPoster;
    static TextView textViewTitle;

    static ImageView imageViewBackdropPath;
    static TextView textViewOverview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_movie_lucky_search, container, false);

        /******************************************************************************************/

        new Thread(new Runnable() {
            @Override
            public void run() {
                discoverMovie();
            }
        }).start();

        textViewTitle = (TextView) rootView.findViewById(R.id.textView_title_cardView);
        imageViewPoster = (ImageView) rootView.findViewById(R.id.imageView_poster_cardView);

        imageViewBackdropPath = (ImageView) rootView.findViewById(R.id.imageView_backdrop);
        textViewOverview = (TextView) rootView.findViewById(R.id.textView_overview);

        Button buttonLike = (Button) rootView.findViewById(R.id.button_like_cardView);
        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFirebase("like");
                arrayListMovies.remove(0);
                setInCard();
            }
        });

        Button buttonPending = (Button) rootView.findViewById(R.id.button_pending_cardView);
        buttonPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFirebase("pending");
                arrayListMovies.remove(0);
                setInCard();
            }
        });

        Button buttonSaw = (Button) rootView.findViewById(R.id.button_watched_cardView);
        buttonSaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFirebase("saw");
                arrayListMovies.remove(0);
                setInCard();
            }
        });

        Button buttonCancel = (Button) rootView.findViewById(R.id.button_cancel_cardView);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayListMovies.remove(0);
                setInCard();
            }
        });

        imageViewPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDataFragment mdf = new MovieDataFragment();
                mdf.getData(arrayListMovies.get(0));
                mdf.show(getFragmentManager(), "abcabc");
            }
        });

        /******************************************************************************************/

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setInCard() {
        Log.i("setInCard", arrayListMovies.get(0).getTitle());

        textViewTitle.setText(arrayListMovies.get(0).getTitle());

        new Thread(new Runnable() {
            @Override
            public void run() {
                String imgUrl = "https://image.tmdb.org/t/p/w342" + arrayListMovies.get(0).getPoster_path();
                Picasso.with(getContext()).load(imgUrl).fit().into(imageViewPoster);
            }
        }).run();
    }

    private void upFirebase(String list){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = database.getReference("users").child(user).child("movies").child(list).child(arrayListMovies.get(0).getId()+"");

        myRef.child("title").setValue(arrayListMovies.get(0).getTitle());
        myRef.child("overview").setValue(arrayListMovies.get(0).getOverview());
        myRef.child("poster_path").setValue(arrayListMovies.get(0).getPoster_path());
        myRef.child("video_path").setValue(arrayListMovies.get(0).getVideo_path());
        myRef.child("backdrop_path").setValue(arrayListMovies.get(0).getBackdrop_path());
    }

    public void discoverMovie() {
        new TMDBService().execute("https://api.themoviedb.org/3/discover/movie?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es&sort_by=popularity.desc&include_adult=false&include_video=false&page=1", "discover");
    }
    public void setTrailerPath() {
        Log.i("traileAAr", "imHere");
        Log.i("arraysize", arrayListMovies.size()+"");
        for (int i=0; i < arrayListMovies.size(); i++) {
            Log.i("trailer", arrayListMovies.get(i).getId()+"");
            new TMDBService().execute("https://api.themoviedb.org/3/movie/"+arrayListMovies.get(i).getId()+"/videos?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es", "video_path", i+"");
        }
        setInCard();
    }

    private void getListDiscover(String s) {

        Movie movie;
        JSONObject jsonObject;
        final JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(s);
            jsonArray = jsonObject.optJSONArray("results");
            for (int i=0; i<jsonArray.length(); i++) {

                movie = new Movie(
                        jsonArray.getJSONObject(i).getInt("id"),
                        jsonArray.getJSONObject(i).getString("title"),
                        jsonArray.getJSONObject(i).getString("poster_path"),
                        //key,
                        null,
                        jsonArray.getJSONObject(i).getString("overview"),
                        jsonArray.getJSONObject(i).getString("backdrop_path")
                );
                //Log.i("movie", movie.toString());
                arrayListMovies.add(movie);
            }

            Log.i("list_Movies_length", ""+arrayListMovies.size());
            for (Movie m: arrayListMovies) {
                Log.i("movie", m.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setTrailerPath();
    }
    private void getMoviePath(String s, int pos) {

        JSONObject jsonObject;
        final JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(s);
            jsonArray = jsonObject.optJSONArray("results");

            arrayListMovies.get(pos).setVideo_path(jsonArray.getJSONObject(0).getString("key"));

            Log.i("list_Movies_length", ""+arrayListMovies.size());
            for (Movie m: arrayListMovies) {
                Log.i("movie", m.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class TMDBService extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            String result = "";
            ArrayList<String> aux = new ArrayList<>();
            aux.add(params[1]);

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = br.readLine()) != null) {
                    result+=line;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i("result", result);
            aux.add(result);

            if (params.length>2) {
                aux.add(params[2]);
            }

            return aux;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            if (s.get(0) == "discover") {
                getListDiscover(s.get(1));

            } else if (s.get(0) == "video_path") {
                Log.i(s.get(2), s.get(1));
                getMoviePath(s.get(1), Integer.parseInt(s.get(2)));
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}