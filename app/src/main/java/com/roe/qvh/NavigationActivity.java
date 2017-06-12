package com.roe.qvh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MovieLikeFragment.OnFragmentInteractionListener, MoviePendingFragment.OnFragmentInteractionListener,
        MovieSawFragment.OnFragmentInteractionListener, MovieSearchFragment.OnFragmentInteractionListener,
        MoviesInTeahtersFragment.OnFragmentInteractionListener, MovieNextReleasesFragment.OnFragmentInteractionListener,
        MovieLuckySearchFragment.OnFragmentInteractionListener {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.textView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /******************************************************************************************/


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

        /******************************************************************************************/

        ImageButton logout = (ImageButton) findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(NavigationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView textView = (TextView) findViewById(R.id.textView_email_navHeader);
        textView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        /******************************************************************************************/

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
            textView.setText("");

        } else if (id == R.id.nav_luckysearch) {
            fragment = new MovieLuckySearchFragment();
            textView.setText("");

        } else if (id == R.id.nav_theaters) {
            fragment = new MoviesInTeahtersFragment();
            textView.setText("");

        } else if (id == R.id.nav_releases) {
            fragment = new MovieNextReleasesFragment();
            textView.setText("");

        } else if (id == R.id.nav_movie_like) {
            fragment = new MovieLikeFragment();
            textView.setText("");

        } else if (id == R.id.nav_movie_pending) {
            fragment = new MoviePendingFragment();
            textView.setText("");

        } else if (id == R.id.nav_movie_saw) {
            fragment = new MovieSawFragment();
            textView.setText("");

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
