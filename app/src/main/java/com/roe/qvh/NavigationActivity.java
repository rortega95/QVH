package com.roe.qvh;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MovieLikeFragment.OnFragmentInteractionListener, MoviePendingFragment.OnFragmentInteractionListener,
        MovieSawFragment.OnFragmentInteractionListener, MovieSearchFragment.OnFragmentInteractionListener,
        MoviesInTeahtersFragment.OnFragmentInteractionListener, MovieNextReleasesFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /******************************************************************************************/

        TMDBService service = new TMDBService();
        Movie movie;
        ArrayList<Movie> arrayList = new ArrayList<>();

        try {
            String s = service.execute("https://api.themoviedb.org/3/discover/movie?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es&sort_by=popularity.desc&include_adult=false&include_video=false&page=1").get();
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.optJSONArray("results");
            for (int i=0; i<jsonArray.length(); i++) {
                //Log.i("jsonArray "+i, jsonArray.getJSONObject(i).getString("title"));
                TMDBService service2 = new TMDBService();
                String r = service2.execute("https://api.themoviedb.org/3/movie/"+jsonArray.getJSONObject(i).getInt("id")+"/videos?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es").get();
                JSONObject jsonObject2 = new JSONObject(r);
                JSONArray jsonArray2 = jsonObject2.optJSONArray("results");
                String key;
                if (jsonArray2.length()==0) {
                    key = null;
                } else {
                    key = jsonArray2.getJSONObject(0).getString("key");
                }

                movie = new Movie(
                        jsonArray.getJSONObject(i).getInt("id"),
                        jsonArray.getJSONObject(i).getString("title"),
                        jsonArray.getJSONObject(i).getString("poster_path"),
                        key,
                        jsonArray.getJSONObject(i).getString("overview")
                        );
                //Log.i("movie", movie.toString());
                arrayList.add(movie);
            }

            Log.i("list_Movies_length", ""+arrayList.size());
            for (Movie m: arrayList) {
                Log.i("movie", m.toString());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /******************************************************************************************/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_search) {
            fragment = new MovieSearchFragment();

        } else if (id == R.id.nav_theaters) {
            fragment = new MoviesInTeahtersFragment();

        } else if (id == R.id.nav_releases) {
            fragment = new MovieNextReleasesFragment();

        } else if (id == R.id.nav_movie_like) {
            fragment = new MovieLikeFragment();

        } else if (id == R.id.nav_movie_pending) {
            fragment = new MoviePendingFragment();

        } else if (id == R.id.nav_movie_saw) {
            fragment = new MovieSawFragment();

        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
        getSupportActionBar().setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
