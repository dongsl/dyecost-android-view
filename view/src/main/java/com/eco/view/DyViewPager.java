package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * DyViewPager
 * 1.在使用viewPager的地方替换为DyNoScrollViewPager就可以
 */
public class DyViewPager extends ViewPager {

  private boolean lrScroll = Boolean.TRUE; //左右滑动

  public DyViewPager(Context context) {
    this(context, null);
  }

  public DyViewPager(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_view_pager);
      lrScroll = typedArray.getBoolean(R.styleable.dy_view_pager_dyvp_lr_scroll, Boolean.TRUE); //左右滑动
      typedArray.recycle();
    } else {

    }
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
  }


  public void setLrScroll(boolean lrScroll) {
    this.lrScroll = lrScroll;
    //invalidate();
  }

  @Override
  public void scrollTo(int x, int y) {
    super.scrollTo(x, y);
  }

  @Override
  public boolean onTouchEvent(MotionEvent arg0) {
    return lrScroll ? super.onTouchEvent(arg0) : false;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent arg0) {
    return lrScroll ? super.onInterceptTouchEvent(arg0) : false;
  }

  @Override
  public void setCurrentItem(int item, boolean smoothScroll) {
    super.setCurrentItem(item, smoothScroll);
  }

  @Override
  public void setCurrentItem(int item) {
    super.setCurrentItem(item);
  }

}
