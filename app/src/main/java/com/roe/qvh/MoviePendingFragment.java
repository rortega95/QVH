package com.roe.qvh;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoviePendingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MoviePendingFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public MoviePendingFragment() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_pending, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.reciclerViewMoviePending);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        final ArrayList<Movie> movies = new ArrayList<Movie>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                progressDialog = ProgressDialog.show(getContext(), null, "cargando...", true);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference myRef = database.getReference("users").child(user).child("movies").child("pending");
                Log.i("key", myRef.getKey());
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.i("dsExist", "hay pelis");
                            Log.i("ds", dataSnapshot.getValue().toString());
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                Log.i("value", ds.getValue().toString());
                                Movie m = ds.getValue(Movie.class);
                                Log.i("movie", m.toString());
                                movies.add(m);
                            }
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(movies, getActivity());
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.i("dsExist", "NO hay pelis");
                            Toast.makeText(getContext(), "No has marcado ninguna película como pendiente", Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).run();

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
}
