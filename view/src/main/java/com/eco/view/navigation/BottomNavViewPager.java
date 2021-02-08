package com.eco.view.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class BottomNavViewPager extends ViewPager {

  private boolean isCanScroll = true;

  public BottomNavViewPager(Context context) {
    super(context);
  }

  public BottomNavViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setCanScroll(boolean isCanScroll) {
    this.isCanScroll = isCanScroll;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
        /*try {
            float x1 = 0, x2;
            Boolean leftCanSlide = pageCanSlideListener.leftCanSlide();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //将按下时的坐标存储
                    x1 = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    x2 = event.getX();
                    float motionValue = x2 - x1;
                    if (motionValue < 0 && !leftCanSlide) {//禁止左滑
                        return true;
                    }
                    //当手指离开的时候
                    if (x1 - x2 > 50) {
                        System.out.println("向右滑");
                        return true;
                        //pageCanSlideListener.currentCanSlide(false);
                    } else if (x2 - x1 > 50) {
                        System.out.println("向左滑");
                        //pageCanSlideListener.currentCanSlide(true);
                    }

                    //x1 = event.getX();//手指移动时，再把当前的坐标作为下一次的‘上次坐标’，解决上述问题, 先做在右出出现多拉出来一块时，用此方式

                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/

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
