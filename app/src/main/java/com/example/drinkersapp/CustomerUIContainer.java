package com.example.drinkersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
/*
 * Container for all Customer UI fragments and the bottom navigation bar
 */
public class CustomerUIContainer extends AppCompatActivity {

    // bottom navigation bar variables
    ViewPager2 viewPager2;
    CustomerViewPageAdapter viewPageAdapter;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_uicontainer);

        // functionality for bottom navigation
        bottomNavigationView = findViewById(R.id.customer_nav);
        viewPager2 = findViewById(R.id.vpCustomerViewPager2);
        viewPageAdapter = new CustomerViewPageAdapter(this);
        viewPager2.setAdapter(viewPageAdapter);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            // checks which menu item is selected
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.home:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.compare:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.checkout:
                        viewPager2.setCurrentItem(2);
                        break;
                    case R.id.track:
                        viewPager2.setCurrentItem(3);
                        break;
                    case R.id.account:
                        viewPager2.setCurrentItem(4);
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
                        bottomNavigationView.getMenu().findItem(R.id.compare).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.checkout).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.track).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.account).setChecked(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });

    }
}