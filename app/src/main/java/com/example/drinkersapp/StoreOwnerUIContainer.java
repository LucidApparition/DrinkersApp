package com.example.drinkersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
/*
 * Container for all Store Owner UI fragments and the bottom navigation bar
 */
public class StoreOwnerUIContainer extends AppCompatActivity {
    // bottom navigation bar variables
    ViewPager2 viewPager2;
    StoreOwnerViewPageAdapter viewPageAdapter;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_owner_uicontainer);

        // functionality for bottom navigation
        bottomNavigationView = findViewById(R.id.store_nav);
        viewPager2 = findViewById(R.id.vpStoreViewPager2);
        viewPageAdapter = new StoreOwnerViewPageAdapter(this);
        viewPager2.setAdapter(viewPageAdapter);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            // checks which menu item is selected
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.account:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.orders:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.history:
                        viewPager2.setCurrentItem(2);
                        break;
                    case R.id.settings:
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
                        bottomNavigationView.getMenu().findItem(R.id.account).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.orders).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.history).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.settings).setChecked(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });

    }
}