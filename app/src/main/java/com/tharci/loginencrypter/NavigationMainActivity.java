package com.tharci.loginencrypter;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class NavigationMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);
        setTitle("Login Encrypter");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.nav_main_frameLayout,
                        new ListLoginFragment())
                .commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_listLogin) {
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_main_frameLayout,
                            new ListLoginFragment())
                    .commit();

        } else if (id == R.id.nav_newLogin) {
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_main_frameLayout,
                            new NewLoginFragment())
                    .commit();
        } else if (id == R.id.nav_impExp) {
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_main_frameLayout,
                            new ImpExpFragment())
                    .commit();
        } else if (id == R.id.settings) {
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_main_frameLayout,
                            new SettingsFragment())
                    .commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        DataService.passwordHash = null;
    }


}
