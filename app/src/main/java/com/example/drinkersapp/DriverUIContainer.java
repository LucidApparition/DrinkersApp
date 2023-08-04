package com.example.drinkersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
/*
 * Container for all Driver UI fragments and the bottom navigation bar
 */
public class DriverUIContainer extends AppCompatActivity {
    // bottom navigation bar variables
    ViewPager2 viewPager2;
    DriverViewPageAdapter viewPageAdapter;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_uicontainer);

        // functionality for bottom navigation
        bottomNavigationView = findViewById(R.id.driver_nav);
        viewPager2 = findViewById(R.id.vpDriverViewPager2);
        viewPageAdapter = new DriverViewPageAdapter(this);
        viewPager2.setAdapter(viewPageAdapter);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            // checks which menu item is selected
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case  R.id.home:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.acceptDecline:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.navigate:
                        viewPager2.setCurrentItem(2);
                        break;
                    case R.id.help:
                        viewPager2.setCurrentItem(3);
                        break;
                }
                return false;
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // binds navigation bar items to menu xml file items
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.acceptDecline).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.navigate).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.help).setChecked(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }
}