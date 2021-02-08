package com.eco.view.navigation;

import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class BottomNavViewPagerAdapter extends FragmentStatePagerAdapter { //FragmentPagerAdapter

  private List<Fragment> fragments;

  public BottomNavViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
    super(fm);
    this.fragments = fragments;
  }


  public void refresh() {
    notifyDataSetChanged();
  }

  public void setData(List<Fragment> data) {
    fragments.clear();
    if (null != data && data.size() > 0) {
      fragments.addAll(data);
    }
    refresh();
  }

  /**
   * 添加数据 并刷新
   *
   * @param data
   */
  public void addData(List<Fragment> data) {
    if (null != data && data.size() > 0) {
      fragments.addAll(data);
    }
    refresh();
  }

  @Override
  public int getItemPosition(Object object) {
    return PagerAdapter.POSITION_NONE;
  }

  @Override
  public Fragment getItem(int position) {
    Fragment fragment = fragments.get(position);
    if (null == fragment) { //如果为null 构建一个空白页面
      fragment = new Fragment();
    }
    return fragment;
  }

  @Override
  public int getCount() {
    return fragments == null ? 0 : fragments.size();
  }

  @Override
  public void restoreState(Parcelable state, ClassLoader loader) {

  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
  }
}
