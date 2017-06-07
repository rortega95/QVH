package com.roe.qvh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoviesInTeahtersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MoviesInTeahtersFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public MoviesInTeahtersFragment() {
        // Required empty public constructor
    }

    static ArrayList<Movie> arrayListMovies = new ArrayList<>();

    ImageView imageViewPoster;
    TextView textViewTitle;

    int pos = 0;

    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_movies_in_teahters, container, false);

        /******************************************************************************************/

        progressDialog = new ProgressDialog(getActivity());
        progressDialog = ProgressDialog.show(getContext(), null, "cargando...", true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                discoverMovie();
            }
        }).start();

        textViewTitle = (TextView) rootView.findViewById(R.id.textView_title_cardTeahters);
        imageViewPoster = (ImageView) rootView.findViewById(R.id.imageView_poster_cardTeahters);

        //textViewOverview = (TextView) rootView.findViewById(R.id.textView_overview);

        ImageButton buttonNextMovie = (ImageButton) rootView.findViewById(R.id.button_nextMovie);
        buttonNextMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos==arrayListMovies.size()-1) {

                } else {
                    pos++;
                    setInCard();
                }

            }
        });

        ImageButton buttonBackMovie = (ImageButton) rootView.findViewById(R.id.button_backMovie);
        buttonBackMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos==0) {

                } else {
                    pos--;
                    setInCard();
                }
            }
        });

        Button buttonLike = (Button) rootView.findViewById(R.id.button_like_cardTeahters);
        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFirebase("like");
            }
        });

        Button buttonPending = (Button) rootView.findViewById(R.id.button_pending_cardTeahters);
        buttonPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFirebase("pending");
            }
        });

        Button buttonSaw = (Button) rootView.findViewById(R.id.button_watched_cardTeahters);
        buttonSaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upFirebase("saw");
            }
        });

        Button buttonTrailer = (Button) rootView.findViewById(R.id.button_trailer_cardTeahters);
        buttonTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+arrayListMovies.get(pos).getVideo_path()));
                startActivity(intent);
            }
        });

        Button buttonInfo = (Button) rootView.findViewById(R.id.button_info_cardTeahters);
        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDataFragment mdf = new MovieDataFragment();
                mdf.disable();
                mdf.getData(arrayListMovies.get(pos));
                mdf.show(getFragmentManager(), "");
            }
        });

        /******************************************************************************************/

        return rootView;
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

    public void discoverMovie() {
        String dateMin;
        String dateMax;

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        dateMax = dateFormat.format(date.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -2);
        dateMin = dateFormat.format(calendar.getTime());

//        new TMDBService().execute("https://api.themoviedb.org/3/movie/now_playing?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es&page=1&region=ES", "discover");
        new TMDBService().execute("https://api.themoviedb.org/3/discover/movie?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es&region=ES&sort_by=vote_count.desc&include_adult=false&include_video=false&page=1&release_date.gte="+dateMin+"&release_date.lte="+dateMax+"&vote_count.gte=100", "discover");

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
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

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
                progressDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
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
}
