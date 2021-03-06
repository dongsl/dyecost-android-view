package com.eco.view;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.MotionEvent;

public class ScrollViewPager extends ViewPager {
  public boolean isScroll = Boolean.TRUE; //时候可以滑动

  public ScrollViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ScrollViewPager(Context context) {
    super(context);
  }

  /**
   * 1.dispatchTouchEvent一般情况不做处理
   * ,如果修改了默认的返回值,子孩子都无法收到事件
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return super.dispatchTouchEvent(ev);   // return true;不行
  }

  /**
   * 是否拦截
   * 拦截:会走到自己的onTouchEvent方法里面来
   * 不拦截:事件传递给子孩子
   */
  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    // return false;//不拦截事件,
    // return true;//子类无法处理事件
    if (isScroll) {
      return super.onInterceptTouchEvent(ev);
    } else {
      return false;
    }
  }

  /**
   * 是否消费事件
   * 消费:事件就结束
   * 不消费:往父控件传
   */
  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    //return false;// 不消费,传给父控件
    //return true;// 消费,拦截事件
    //虽然onInterceptTouchEvent中拦截了,
    //但是如果viewpage里面子控件不是viewgroup,还是会调用这个方法.
    if (isScroll) {
      return super.onTouchEvent(ev);
    } else {
      return true;// 可行,消费,拦截事件
    }
  }

  public void setScroll(boolean scroll) {
    isScroll = scroll;
  }
}