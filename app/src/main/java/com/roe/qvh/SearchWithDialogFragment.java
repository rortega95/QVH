package com.roe.qvh;

import android.app.Dialog;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */


public class SearchWithDialogFragment extends DialogFragment {

    private OkCallback okCallback;

    public SearchWithDialogFragment() {
        // Required empty public constructor
    }

    Spinner spinnerGenres;
    EditText editTextPerson;
    EditText editTextYear;

    DrawerLayout drawerLayout;

    String mQuery = "https://api.themoviedb.org/3/discover/movie?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es&sort_by=popularity.desc&include_adult=false&include_video=false&page=1";
    int personID;
    int genreID;
    int year;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okCallback = (OkCallback) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search_with_dialog, null);

        spinnerGenres = (Spinner) rootView.findViewById(R.id.spinner_genres);
        editTextPerson = (EditText) rootView.findViewById(R.id.editText_people);
        editTextYear = (EditText) rootView.findViewById(R.id.editText_year);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.genres_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenres.setAdapter(adapter);

        builder.setView(rootView);
        builder.setTitle("Descubrir"
        ).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                discover();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });


        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(Gravity.START);
    }

    public void discover() {
        getGenre();
        if (editTextPerson.getText().length()>0) {
            Log.i("SearchPerson", editTextPerson.getText().toString());
            new TMDBService().execute("https://api.themoviedb.org/3/search/person?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es&query="+editTextPerson.getText().toString()+"&page=1&include_adult=false");
        } else {
            if (editTextYear.getText().length()>0) {
                mQuery+="&year="+editTextYear.getText();
            }
            okCallback.url(mQuery);

        }
    }

    public void getGenre() {
        if (!spinnerGenres.getSelectedItem().toString().isEmpty()) {
            switch (spinnerGenres.getSelectedItem().toString()) {
                case "Acción":
                    genreID = 28;
                    break;
                case "Aventura":
                    genreID = 12;
                    break;
                case "Animación":
                    genreID = 16;
                    break;
                case "Comedia":
                    genreID = 35;
                    break;
                case "Crimen":
                    genreID = 80;
                    break;
                case "Documental":
                    genreID = 99;
                    break;
                case "Drama":
                    genreID = 18;
                    break;
                case "Familia":
                    genreID = 10751;
                    break;
                case "Fantasía":
                    genreID = 14;
                    break;
                case "Historia":
                    genreID = 36;
                    break;
                case "Terror":
                    genreID = 27;
                    break;
                case "Música":
                    genreID = 10402;
                    break;
                case "Misterio":
                    genreID = 9648;
                    break;
                case "Romance":
                    genreID = 10749;
                    break;
                case "Ciencia ficción":
                    genreID = 878;
                    break;
                case "película de la televisión":
                    genreID = 10770;
                    break;
                case "Suspense":
                    genreID = 53;
                    break;
                case "Guerra":
                    genreID = 10752;
                    break;
                case "Western":
                    genreID = 37;
                    break;
            }
            mQuery += "&with_genres=" + genreID;
        }
    }

    private class TMDBService extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String result = "";

            try {
                URL url = new URL(params[0].replace(" ", "%20"));
                Log.i("url", url.toString());
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

            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject jsonObject;
            final JSONArray jsonArray;

            try {
                jsonObject = new JSONObject(s);
                jsonArray = jsonObject.optJSONArray("results");
                personID = jsonArray.getJSONObject(0).getInt("id");
                if (personID != 0) {
                    mQuery+="&with_cast="+personID;
                }
                if (editTextYear.getText().length()>0) {
                    mQuery+="&year="+editTextYear.getText();
                }

                Log.i("Person", personID+"");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            okCallback.url(mQuery);
            Log.i("consulta", mQuery);

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
