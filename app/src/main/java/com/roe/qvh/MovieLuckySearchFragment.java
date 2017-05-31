package com.roe.qvh;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieLuckySearchFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public MovieLuckySearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_movie_lucky_search, container, false);

        /******************************************************************************************/

//        Button buttonLike = (Button) rootView.findViewById(R.id.button_like_cardView);
//        Button buttonWatched = (Button) rootView.findViewById(R.id.button_watched_cardView);
//        Button buttonPending = (Button) rootView.findViewById(R.id.button_pending_cardView);
//        Button buttonCancel = (Button) rootView.findViewById(R.id.button_cancel_cardView);

        final ArrayList<Movie> arrayList;
        arrayList = new APIQueries().getListDiscover();

        final ImageView imageViewPoster = (ImageView) rootView.findViewById(R.id.imageView_poster_cardView);
        final TextView textViewTitle = (TextView) rootView.findViewById(R.id.textView_title_cardView);

        String imgUrl = "https://image.tmdb.org/t/p/w342"+arrayList.get(0).getPoster_path();

        Log.i("title0", arrayList.get(0).getTitle());
        Log.i("poster0", imgUrl);

        Picasso.with(getContext()).load(imgUrl).fit().into(imageViewPoster);
        textViewTitle.setText(arrayList.get(0).getTitle());

        Button buttonLike = (Button) rootView.findViewById(R.id.button_like_cardView);
        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.remove(0);
                String imgUrl = "https://image.tmdb.org/t/p/w342"+arrayList.get(0).getPoster_path();

                Log.i("title0", arrayList.get(0).getTitle());
                Log.i("poster0", imgUrl);

                Picasso.with(getContext()).load(imgUrl).fit().into(imageViewPoster);
                textViewTitle.setText(arrayList.get(0).getTitle());
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}