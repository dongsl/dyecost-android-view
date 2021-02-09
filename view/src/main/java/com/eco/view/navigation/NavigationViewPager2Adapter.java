package com.eco.view.navigation;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class NavigationViewPager2Adapter extends FragmentStateAdapter {

  private List<Fragment> fragments;


  public NavigationViewPager2Adapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
    super(fragmentActivity);
    this.fragments = fragments;
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    Fragment fragment = fragments.get(position);
    if (null == fragment) { //如果为null 构建一个空白页面
      fragment = new Fragment();
    }
    return fragment;
  }

  @Override
  public int getItemCount() {
    return fragments == null ? 0 : fragments.size();
  }
}
