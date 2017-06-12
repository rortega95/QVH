package com.roe.qvh;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by r on 31/5/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static ArrayList<Movie> list;
    private static Context context;
    private static String back;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView poster;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.textView_title_cardView);
            poster = (ImageView) v.findViewById(R.id.imageView_poster_cardView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("tocado", getAdapterPosition() + "");
                    MovieDataDialogFragment mdf = new MovieDataDialogFragment();
                    FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                    mdf.getData(list.get(getAdapterPosition()));
                    mdf.show(manager, "abcabc");
                }

            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference myRef = database.getReference("users").child(user).child("movies");

                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    switch (back) {
                        case "like":
                            builder.setTitle("Modificar")
                                    .setItems(new CharSequence[]{"Marcar como vista", "Marcar como pendiente"}, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    Log.i("back", back);
                                                    myRef.child(back).child(String.valueOf(list.get(getAdapterPosition()).getId())).removeValue();
                                                    myRef.child("saw").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("title").setValue(list.get(getAdapterPosition()).getTitle());
                                                    myRef.child("saw").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("overview").setValue(list.get(getAdapterPosition()).getOverview());
                                                    myRef.child("saw").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("poster_path").setValue(list.get(getAdapterPosition()).getPoster_path());
                                                    myRef.child("saw").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("video_path").setValue(list.get(getAdapterPosition()).getVideo_path());
                                                    myRef.child("saw").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("backdrop_path").setValue(list.get(getAdapterPosition()).getBackdrop_path());
                                                    break;
                                                case 1:
                                                    Log.i("back", back);
                                                    myRef.child(back).child(String.valueOf(list.get(getAdapterPosition()).getId())).removeValue();
                                                    myRef.child("pending").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("title").setValue(list.get(getAdapterPosition()).getTitle());
                                                    myRef.child("pending").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("overview").setValue(list.get(getAdapterPosition()).getOverview());
                                                    myRef.child("pending").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("poster_path").setValue(list.get(getAdapterPosition()).getPoster_path());
                                                    myRef.child("pending").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("video_path").setValue(list.get(getAdapterPosition()).getVideo_path());
                                                    myRef.child("pending").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("backdrop_path").setValue(list.get(getAdapterPosition()).getBackdrop_path());
                                                    break;
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            myRef.child(back).child(String.valueOf(list.get(getAdapterPosition()).getId())).removeValue();
                                        }
                                    });
                            break;

                        case "saw":
                            builder.setTitle("Modificar")
                                    .setItems(new CharSequence[]{"Marcar como favorita", "Marcar como pendiente"}, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    Log.i("back", back);
                                                    myRef.child(back).child(String.valueOf(list.get(getAdapterPosition()).getId())).removeValue();
                                                    myRef.child("like").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("title").setValue(list.get(getAdapterPosition()).getTitle());
                                                    myRef.child("like").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("overview").setValue(list.get(getAdapterPosition()).getOverview());
                                                    myRef.child("like").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("poster_path").setValue(list.get(getAdapterPosition()).getPoster_path());
                                                    myRef.child("like").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("video_path").setValue(list.get(getAdapterPosition()).getVideo_path());
                                                    myRef.child("like").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("backdrop_path").setValue(list.get(getAdapterPosition()).getBackdrop_path());
                                                    break;
                                                case 1:
                                                    Log.i("back", back);
                                                    myRef.child(back).child(String.valueOf(list.get(getAdapterPosition()).getId())).removeValue();
                                                    myRef.child("pending").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("title").setValue(list.get(getAdapterPosition()).getTitle());
                                                    myRef.child("pending").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("overview").setValue(list.get(getAdapterPosition()).getOverview());
                                                    myRef.child("pending").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("poster_path").setValue(list.get(getAdapterPosition()).getPoster_path());
                                                    myRef.child("pending").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("video_path").setValue(list.get(getAdapterPosition()).getVideo_path());
                                                    myRef.child("pending").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("backdrop_path").setValue(list.get(getAdapterPosition()).getBackdrop_path());
                                                    break;
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            myRef.child(back).child(String.valueOf(list.get(getAdapterPosition()).getId())).removeValue();
                                        }
                                    });
                                break;

                        case "pending":
                            builder.setTitle("Modificar")
                                    .setItems(new CharSequence[]{"Marcar como vista", "Marcar como favorita"}, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    Log.i("back", back);
                                                    myRef.child(back).child(String.valueOf(list.get(getAdapterPosition()).getId())).removeValue();
                                                    myRef.child("saw").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("title").setValue(list.get(getAdapterPosition()).getTitle());
                                                    myRef.child("saw").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("overview").setValue(list.get(getAdapterPosition()).getOverview());
                                                    myRef.child("saw").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("poster_path").setValue(list.get(getAdapterPosition()).getPoster_path());
                                                    myRef.child("saw").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("video_path").setValue(list.get(getAdapterPosition()).getVideo_path());
                                                    myRef.child("saw").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("backdrop_path").setValue(list.get(getAdapterPosition()).getBackdrop_path());
                                                    break;
                                                case 1:
                                                    Log.i("back", back);
                                                    myRef.child(back).child(String.valueOf(list.get(getAdapterPosition()).getId())).removeValue();
                                                    myRef.child("like").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("title").setValue(list.get(getAdapterPosition()).getTitle());
                                                    myRef.child("like").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("overview").setValue(list.get(getAdapterPosition()).getOverview());
                                                    myRef.child("like").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("poster_path").setValue(list.get(getAdapterPosition()).getPoster_path());
                                                    myRef.child("like").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("video_path").setValue(list.get(getAdapterPosition()).getVideo_path());
                                                    myRef.child("like").child(String.valueOf(list.get(getAdapterPosition()).getId())).child("backdrop_path").setValue(list.get(getAdapterPosition()).getBackdrop_path());
                                                    break;
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            myRef.child(back).child(String.valueOf(list.get(getAdapterPosition()).getId())).removeValue();
                                        }
                                    });
                                break;
                    }
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return false;
                }
            });
        }
    }

    public RecyclerViewAdapter(ArrayList<Movie> list, Context context, String back) {
        Log.i("sizelist", list.size() + "");
        this.list = list;
        this.context = context;
        this.back = back;
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
