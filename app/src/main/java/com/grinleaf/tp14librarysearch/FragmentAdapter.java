package com.grinleaf.tp14librarysearch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentStateAdapter {

    ArrayList<Fragment> fragments= new ArrayList<>();

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        fragments.add(new Tab1Fragment());
        fragments.add(new Tab2Fragment());
        fragments.add(new Tab3Fragment());
        fragments.add(new Tab4Fragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
