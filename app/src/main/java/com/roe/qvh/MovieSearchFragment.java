package com.roe.qvh;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MovieSearchFragment extends Fragment implements OkCallback {

    private OnFragmentInteractionListener mListener;

    public MovieSearchFragment() {
        // Required empty public constructor
    }

    ArrayList<Movie> arrayListMovies = new ArrayList<>();
    ImageView imageViewPoster;
    TextView textViewTitle;

    ImageView imageViewBackdropPath;
    TextView textViewOverview;

    ProgressDialog progressDialog;

    int pos = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_search, container, false);

        SearchWithDialogFragment swdf = new SearchWithDialogFragment();
        swdf.setTargetFragment(this, 0);
        swdf.show(getFragmentManager(), "acab");

        textViewTitle = (TextView) view.findViewById(R.id.textView_title_cardView);
        imageViewPoster = (ImageView) view.findViewById(R.id.imageView_poster_cardView);

        imageViewBackdropPath = (ImageView) view.findViewById(R.id.imageView_backdrop);
        textViewOverview = (TextView) view.findViewById(R.id.textView_overview);

        Button buttonLike = (Button) view.findViewById(R.id.button_like_cardView);
        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFirebase("like");
                if (pos < arrayListMovies.size()-1) {
                    pos++;
                    setInCard();
                }
            }
        });

        Button buttonPending = (Button) view.findViewById(R.id.button_pending_cardView);
        buttonPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFirebase("pending");
                if (pos < arrayListMovies.size()-1) {
                    pos++;
                    setInCard();
                }
            }
        });

        Button buttonSaw = (Button) view.findViewById(R.id.button_watched_cardView);
        buttonSaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFirebase("saw");
                if (pos < arrayListMovies.size()-1) {
                    pos++;
                    setInCard();
                }
            }
        });

        Button buttonCancel = (Button) view.findViewById(R.id.button_cancel_cardView);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos < arrayListMovies.size()-1) {
                    pos++;
                    setInCard();
                }
            }
        });

        imageViewPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDialog();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public String url(String s) {
        Log.i("Callback", s);
        progressDialog = ProgressDialog.show(getContext(), null, "cargando...", true);
        discoverMovie(s);
        return null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setInCard() {
        Log.i("setInCard", arrayListMovies.get(pos).getTitle());

        textViewTitle.setText(arrayListMovies.get(pos).getTitle());

        new Thread(new Runnable() {
            @Override
            public void run() {
                String imgUrl = "https://image.tmdb.org/t/p/w342" + arrayListMovies.get(pos).getPoster_path();
                Picasso.with(getContext()).load(imgUrl).fit().into(imageViewPoster);
            }
        }).run();
    }

    private void upFirebase(String list){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference myRef = database.getReference("users").child(user).child("movies").child(list).child(arrayListMovies.get(pos).getId()+"");

        myRef.child("title").setValue(arrayListMovies.get(pos).getTitle());
        myRef.child("overview").setValue(arrayListMovies.get(pos).getOverview());
        myRef.child("poster_path").setValue(arrayListMovies.get(pos).getPoster_path());
        myRef.child("video_path").setValue(arrayListMovies.get(pos).getVideo_path());
        myRef.child("backdrop_path").setValue(arrayListMovies.get(pos).getBackdrop_path());
    }

    public void discoverMovie(String s) {

        new TMDBService().execute(s, "discover");
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

        if (arrayListMovies.size()>0) {
            setTrailerPath();
        } else {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "No se encontraron resultados", Toast.LENGTH_LONG).show();
            SearchWithDialogFragment swdf = new SearchWithDialogFragment();
            swdf.setTargetFragment(this, 0);
            swdf.show(getFragmentManager(), "acab");

        }

    }
    private void getMoviePath(String s, int pos) {

        JSONObject jsonObject;
        final JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(s);
            jsonArray = jsonObject.optJSONArray("results");

            Log.i("posMovie", pos+"");
            if (jsonArray.length()==0) {
                arrayListMovies.get(pos).setVideo_path(null);
            } else {
                arrayListMovies.get(pos).setVideo_path(jsonArray.getJSONObject(0).getString("key"));
            }

            Log.i("list_Movies_length", ""+arrayListMovies.size());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();
    }

    public void runDialog() {
        MovieDataDialogFragment mdf = new MovieDataDialogFragment();
        mdf.getData(arrayListMovies.get(pos));
        mdf.show(getFragmentManager(), "abcabc");
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
            Log.i("vacio", s.get(1));
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


