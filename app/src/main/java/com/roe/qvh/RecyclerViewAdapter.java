package com.roe.qvh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by r on 31/5/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView poster;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.textView_title_cardView);
            poster = (ImageView) v.findViewById(R.id.imageView_poster_cardView);
        }

    }

    private ArrayList<Movie> list;

    public RecyclerViewAdapter(ArrayList<Movie> list){
        Log.i("sizelist", list.size()+"");
        this.list=list;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_for_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, int position) {
        final Movie m = list.get(position);
        holder.title.setText(m.getTitle());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String imgUrl = "https://image.tmdb.org/t/p/w342" + m.getPoster_path();
                Context context = holder.poster.getContext();
                Picasso.with(context).load(imgUrl).fit().into(holder.poster);
            }
        }).run();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
