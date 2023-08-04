package com.example.drinkersapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
/*
 * View Page Adapter for Customer UI container
 */
public class CustomerViewPageAdapter extends FragmentStateAdapter {
    public CustomerViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // opens the right UI based off what navigation item is selected
        switch (position){
            case 0: return new CustomerHomeFragment();
            case 1: return new CustomerCompareFragment();
            case 2: return new CustomerCheckoutFragment();
            case 3: return new CustomerOrderTrackingFragment();
            case 4: return new CustomerAccountFragment();
            default: return new CustomerHomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
