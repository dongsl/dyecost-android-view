package com.eco.view.disc.stickers.adapter;

import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * 表情盘内容分页
 */
public class StickersAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> contentFragmentList;
    public FragmentManager fm;

    public StickersAdapter(FragmentManager fm, List<Fragment> contentFragmentList) {
        super(fm);
        this.fm = fm;
        this.contentFragmentList = contentFragmentList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return contentFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return contentFragmentList.size();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}
