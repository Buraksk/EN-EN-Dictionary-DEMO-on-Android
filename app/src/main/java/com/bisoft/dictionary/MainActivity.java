package com.bisoft.dictionary;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bisoft.dictionary.activity.SettingsActivity;
import com.bisoft.dictionary.fragment.AllWordsFragment;
import com.bisoft.dictionary.fragment.FavouriteFragment;
import com.bisoft.dictionary.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity{
    private DrawerLayout mDrawerLayout;

    public int mState = 1; //where you want to trigger the hide action
    //mState = 1; to hide or mState = 0; to show


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_18dp);
        actionbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.gray)));
        mDrawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        //starting page
        Fragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.content_frame, fragment, "home");
        fragmentTransaction.commit();

        //invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again



        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        // close drawer when item is clicked
                        mDrawerLayout.closeDrawers();

                        Fragment fragment =null;

                        //swap base content
                        switch (menuItem.getItemId()){
                            case R.id.nav_home:
                                fragment = new HomeFragment();
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                        android.R.anim.fade_out);
                                fragmentTransaction.replace(R.id.content_frame, fragment, "home");
                                fragmentTransaction.commit();

                                 mState =1;

                                break;
                            case R.id.nav_favourite:
                                fragment = new FavouriteFragment();
                                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                        android.R.anim.fade_out);
                                fragmentTransaction.replace(R.id.content_frame, fragment, "home");
                                fragmentTransaction.commit();
                                //fragmentTransaction.replace(R.id.content_frame, fragment, "favourite");
                                mState =0;
                                break;
                            case R.id.nav_allWords:
                                fragment = new AllWordsFragment();
                                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                        android.R.anim.fade_out);
                                fragmentTransaction.replace(R.id.content_frame, fragment, "home");
                                fragmentTransaction.commit();
                                //fragmentTransaction.replace(R.id.content_frame, fragment, "favourite");
                                mState =0;
                                break;
                            case R.id.nav_settings:
                                // launch new intent instead of loading fragment
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                mDrawerLayout.closeDrawers();
                                mState =0;
                                break;
                        }
                        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
                        return true;
                    }
                });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        //  drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // state changes

                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(!(item.getItemId()==R.id.add_favourite)) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(Gravity.START);
            } else {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu from xml
        getMenuInflater().inflate(R.menu.menu, menu);

        Log.i("oncreateoptionmenu","girdi");
        if (mState == 1) //1 is true, 0 is false
        {
            //hide only option 2
            //menu.getItem(1).setVisible(false);
            menu.setGroupVisible(R.id.actionbar_element,true);
            //mState=0;

        }else{
            menu.setGroupVisible(R.id.actionbar_element,false);
            //mState=1;
        }
        return true;
    }
}

