package com.roe.qvh;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
        service.execute("https://api.themoviedb.org/3/discover/movie?api_key=bf25f4ac2b3e20d7bde180f92504c75c&language=es&sort_by=popularity.desc&include_adult=false&include_video=false&page=1");

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
