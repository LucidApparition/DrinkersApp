package com.example.drinkersapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
/*
 * View Page Adapter for Driver UI container
 */
public class DriverViewPageAdapter extends FragmentStateAdapter {

    public DriverViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // opens the right UI based off what navigation item is selected
        switch (position) {
            case 0:
                return new DriverMenuFragment();
            case 1:
                return new DriverAcceptDeclineFragment();
            case 2:
                return new DriverNavigationFragment();
            case 3:
                return new DriverHelpFragment();
            default:
                return new DriverMenuFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
