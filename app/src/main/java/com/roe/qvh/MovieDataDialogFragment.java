package com.roe.qvh;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDataDialogFragment extends DialogFragment {

    public MovieDataDialogFragment() {
        // Required empty public constructor
    }

    Movie movie;
    ImageView imageViewBackdrop;
    TableRow row;
    TextView textViewOverview;
    boolean rowEnabled = true;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_movie_data, null);

        builder.setView(rootView);
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // FIRE ZE MISSILES!
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });

        imageViewBackdrop = (ImageView) rootView.findViewById(R.id.imageView_backdrop);
        textViewOverview = (TextView) rootView.findViewById(R.id.textView_overview);

        Button buttonTrailer = (Button) rootView.findViewById(R.id.button_trailer);
        buttonTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchTrailer();
            }
        });

        Button buttonAlquilar = (Button) rootView.findViewById(R.id.button_alquilar);
        buttonAlquilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rentMovie();
            }
        });

        ImageButton buttonCancel = (ImageButton) rootView.findViewById(R.id.button_cancel_dialog);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setInCard();

        row = (TableRow) rootView.findViewById(R.id.rowShopTrailer);
        if (!rowEnabled) {
            //row.setVisibility(View.INVISIBLE);
            rootView.removeView(row);
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        Log.i("Enabled?", rowEnabled+"");
        super.show(manager, tag);
    }

    public void getData(Movie m){
        movie = m;
    }

    public void setInCard() {
        Log.i("peliDialog", movie.toString());

        textViewOverview.setText(movie.getOverview());

        new Thread(new Runnable() {
            @Override
            public void run() {
                String imgUrl = "https://image.tmdb.org/t/p/w342" + movie.getBackdrop_path();
                Picasso.with(getContext()).load(imgUrl).fit().into(imageViewBackdrop);
            }
        }).run();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void watchTrailer() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+movie.getVideo_path()));
        startActivity(intent);
    }

    public void rentMovie() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q="+movie.getTitle()+"&c=movies"));
        startActivity(intent);
    }

    public void disable() {
        rowEnabled = false;
    }

}
