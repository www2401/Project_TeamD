package com.example.ilove.teamd;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class TeamD extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BluetoothChatFragment bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_d);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bt = new BluetoothChatFragment();
        Log.v("1", "dsdf");
        setContentView(R.layout.activity_team_d);
        Log.v("2", "das");
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothChatFragment fragment = new BluetoothChatFragment();
            transaction.replace(R.id.content_team_d, fragment);
            transaction.commit();
        }
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_map) {
            Intent page =new Intent(TeamD.this,map.class);
            startActivity(page);
        } else if (id == R.id.nav_graph) {
            Intent page =new Intent(TeamD.this,graph.class);
            startActivity(page);
        } else if (id == R.id.nav_account) {
            Intent page =new Intent(TeamD.this,account.class);
            startActivity(page);
        } else if (id == R.id.nav_registration) {
            Intent page =new Intent(TeamD.this,registration.class);
            startActivity(page);
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "log out", Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.nav_login){
            Intent page =new Intent(TeamD.this,login.class);
            startActivity(page);
        }
        else if (id == R.id.nav_heart) {
            Intent page =new Intent(TeamD.this,heart.class);
            startActivity(page);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
