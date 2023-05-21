package com.hcmus.project_21424074_21424092_21424094.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hcmus.project_21424074_21424092_21424094.mainFragments.AlbumFragment;
import com.hcmus.project_21424074_21424092_21424094.mainFragments.FavoriteFragment;
import com.hcmus.project_21424074_21424092_21424094.mainFragments.PhotoFragment;
import com.hcmus.project_21424074_21424092_21424094.models.Image;
import com.hcmus.project_21424074_21424092_21424094.utility.GetAllPhotoFromGallery;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<Image> data;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
        data = GetAllPhotoFromGallery.getAllImageFromGallery(context);
    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new PhotoFragment();
            case 1:
                return new AlbumFragment();
            case 2:
                return new FavoriteFragment();
            default:
                return null;
        }
    }
    @Override
    public int getItemCount() {
        return 3;
    }


}
