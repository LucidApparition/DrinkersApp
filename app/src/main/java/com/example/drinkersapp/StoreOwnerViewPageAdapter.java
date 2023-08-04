package com.example.drinkersapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
/*
 * View Page Adapter for Store Owner UI container
 */
public class StoreOwnerViewPageAdapter extends FragmentStateAdapter {
    public StoreOwnerViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // opens the right UI based off what navigation item is selected
        switch (position) {
            case 0:
                return new StoreOwnerAccountFragment();
            case 1:
                return new StoreOwnerOrdersFragment();
            case 2:
                return new StoreOwnerHistoryFragment();
            case 3:
                return new StoreOwnerSettingsFragment();
            default:
                return new StoreOwnerOrdersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}