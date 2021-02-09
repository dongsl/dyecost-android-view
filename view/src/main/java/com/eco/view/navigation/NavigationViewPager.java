package com.eco.view.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class NavigationViewPager extends ViewPager {

  private boolean isCanScroll = true;

  public NavigationViewPager(Context context) {
    super(context);
  }

  public NavigationViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setCanScroll(boolean isCanScroll) {
    this.isCanScroll = isCanScroll;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    return isCanScroll && super.dispatchTouchEvent(event);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return isCanScroll && super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return isCanScroll && super.onTouchEvent(event);
  }
}
