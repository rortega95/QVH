package com.roe.qvh;

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
import java.util.Date;

public class MovieLuckySearchFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public MovieLuckySearchFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_movie_lucky_search, container, false);

        progressDialog = ProgressDialog.show(getContext(), null, "cargando...", true);
        getIdMovieFirebase();

        textViewTitle = (TextView) rootView.findViewById(R.id.textView_title_cardView);
        imageViewPoster = (ImageView) rootView.findViewById(R.id.imageView_poster_cardView);

        imageViewBackdropPath = (ImageView) rootView.findViewById(R.id.imageView_backdrop);
        textViewOverview = (TextView) rootView.findViewById(R.id.textView_overview);

        Button buttonLike = (Button) rootView.findViewById(R.id.button_like_cardView);
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

        Button buttonPending = (Button) rootView.findViewById(R.id.button_pending_cardView);
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

        Button buttonSaw = (Button) rootView.findViewById(R.id.button_watched_cardView);
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

        Button buttonCancel = (Button) rootView.findViewById(R.id.button_cancel_cardView);
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

    /**
     * Metodo que carga los datos de la película en la vista de tarjeta
     */
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

    /**
     * Metodo para escribir los datos de una película en la base de datos de firebase
     * @param list
     */
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

    private void getIdMovieFirebase() {
        final ArrayList<String> arrayListAux = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference myRef = database.getReference("users").child(user);

        myRef.child("movies").child("like").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i("dsExist", "hay pelis");
                    Log.i("ds", dataSnapshot.getValue().toString());

                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        arrayListAux.add(ds.getKey());
                        Log.i("moviesAux", ds.getKey());
                    }

                    for (int pos=0; pos < arrayListAux.size(); pos++) {
                        myRef.child("lucky").child("IDs").child(pos+"").setValue(arrayListAux.get(pos));
                    }

                } else {
                    Log.i("dsExist", "NO hay pelis");
                    Toast.makeText(getContext(), "No has marcado ninguna película como pendiente", Toast.LENGTH_SHORT).show();
                }

                myRef.child("lucky").child("totalMoviesLike").setValue(arrayListAux.size());
                Log.i("array", arrayListAux.size()+"");
                for (String s: arrayListAux) {
                    Log.i("arrayAux", s);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        myRef.child("lucky").child("lastSearchID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numX = -1;
                if (!dataSnapshot.exists()) {
                    myRef.child("lucky").child("lastSearchID").setValue(-1);
                } else {
                    int num = Integer.parseInt(dataSnapshot.getValue().toString());
                    Log.i("xxx", num + "");
                    Log.i("xxx", arrayListAux.size() - 1 + "");
                    if (num == arrayListAux.size() - 1 && arrayListAux.size() > 0) {
                        myRef.child("lucky").child("lastSearchID").setValue(0);
                        numX = 0;
                    } else if (arrayListAux.size()==0) {

                    } else {
                        myRef.child("lucky").child("lastSearchID").setValue(num+1);
                        numX = num+1;
                    }
                }

                Log.i("array_num", numX+"");
                myRef.child("lucky").child("totalMoviesLike").setValue(arrayListAux.size());
                Log.i("array", arrayListAux.size()+"");
                for (String s: arrayListAux) {
                    Log.i("arrayAux", s);
                }

                final int finalNumX = numX;
                if (finalNumX != -1) {
                    arrayListMovies = new ArrayList<>();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            discoverMovie("https://api.themoviedb.org/3/movie/" + arrayListAux.get(finalNumX) + "/similar?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es&page=1");
                        }
                    }).start();
                } else {
                    arrayListMovies = new ArrayList<>();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String dateMax;

                            Date date = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                            dateMax = dateFormat.format(date.getTime());

                            discoverMovie("https://api.themoviedb.org/3/discover/movie?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es&region=ES&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&release_date.lte="+dateMax);
                        }
                    }).start();
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Método que manda la petición para descubrir las películas a la clase asíncrona
     * @param s
     */
    public void discoverMovie(String s) {
        new TMDBService().execute(s, "discover");
    }

    /**
     * Método que manda la petición para obtener el tráiler de la película a la clase asíncrona
     */
    public void setTrailerPath() {
        Log.i("traileAAr", "imHere");
        Log.i("arraysize", arrayListMovies.size()+"");
        for (int i=0; i < arrayListMovies.size(); i++) {
            Log.i("trailer", arrayListMovies.get(i).getId()+"");
            new TMDBService().execute("https://api.themoviedb.org/3/movie/"+arrayListMovies.get(i).getId()+"/videos?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es", "video_path", i+"");
        }
        setInCard();
    }

    /**
     * Método que proceso la información del JSON y la guarda como un arraylist de películas
     * @param s
     */
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

    /**
     * Método que obtiene el identificador de una pelicula y llama a otro método para obtener su tráiler
     * @param s
     * @param pos
     */
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