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
 * {@link MovieLikeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MovieLikeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public MovieLikeFragment() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_like, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.reciclerViewMovieLike);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        final ArrayList<Movie> movies = new ArrayList<Movie>();

        new Thread(new Runnable() {

            @Override
            public void run() {
                progressDialog = ProgressDialog.show(getContext(), null, "cargando...", true);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference myRef = database.getReference("users").child(user).child("movies")
                        .child("like");
                Log.i("key", myRef.getKey());
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        movies.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Log.i("value", ds.getValue().toString());
                                Movie m = ds.getValue(Movie.class);
                                m.setId(Integer.parseInt(ds.getKey()));
                                Log.i("dsID", m.getId() + "");
                                Log.i("movie", m.toString());
                                movies.add(m);
                            }

                        } else {
                            Log.i("dsExist", "NO hay pelis");
                            Toast.makeText(getContext(), "No has marcado ninguna película como vista", Toast.LENGTH_SHORT).show();
                        }
                        RecyclerViewAdapter adapter = new RecyclerViewAdapter(movies, getActivity(), "like");
                        recyclerView.swapAdapter(adapter, false);

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
