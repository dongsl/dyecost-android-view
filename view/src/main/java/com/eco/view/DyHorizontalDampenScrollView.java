package com.eco.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;

import androidx.drawerlayout.widget.DrawerLayout;

/**
 * 水平方向左右向滑动阻尼效果
 * -----------------demo-----------------
 * <HorizontalDampenScrollView
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * android:scrollbars="none">
 * <LinearLayout/>
 * </HorizontalDampenScrollView>
 */
public class DyHorizontalDampenScrollView extends HorizontalScrollView {

  private View inner;
  private float x;
  private Rect normal = new Rect();
  private boolean isCount = false;
  private boolean isMoveing = false;
  private int initLeft;
  private int left;
  private DrawerLayout drawerLayout;

  public DyHorizontalDampenScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  /**
   * Based on the XML generated view work done.
   * The function in the creation of the view of the last call.
   * after all sub-view has been added.
   * Even if the sub-class covered the onFinishInflate method.
   * it should also call the parent class method to make the method to be implemented.
   */
  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    if (getChildCount() > 0) {
      inner = getChildAt(0);
    }
  }

  /**
   * Touch event handling
   **/
  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (inner != null) {
      commOnTouchEvent(ev);
    }
    return super.onTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_UP) {
      if (drawerLayout != null) { //如果存在侧边栏则关闭
        drawerLayout.closeDrawers();
      }
    }
    return super.onInterceptTouchEvent(ev);
  }

  /**
   * Slide event (let the speed of sliding into the original 1/2)
   */
  @Override
  public void fling(int velocityY) {
    super.fling(velocityY / 2);
  }

  /***
   * Touch the event
   *
   * @param ev
   */
  public void commOnTouchEvent(MotionEvent ev) {
    int action = ev.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        break;

      case MotionEvent.ACTION_UP:
        isMoveing = false;
        // Fingers loose
        if (isNeedAnimation()) {
          animation();
        }
        break;
      /**Excluding the first mobile calculation.
       *  because the first time can not know the y coordinates.
       *  in MotionEvent.ACTION_DOWN not get.
       *  because this time is MyScrollView touch event passed to the LIstView child item above.
       *  So from the second calculation But we also have to initialize.
       *  that is. the first time to move the sliding distance to 0.
       *  After the record is accurate on the normal implementation.
       */
      case MotionEvent.ACTION_MOVE:
        final float preX = x;// When the y coordinate is pressed
        float nowX = ev.getX();// Always y-coordinate
        int deltaX = (int) (nowX - preX);// Slide distance
        if (!isCount) {
          deltaX = 0; // Here to 0.
        }
        // When the scroll to the top or the most when it will not scroll, then move the layout.
        isNeedMove();

        if (isMoveing) {
          // Initialize the head rectangle
          if (normal.isEmpty()) {
            // Save the normal layout position
            normal.set(inner.getLeft(), inner.getTop(), inner.getRight(), inner.getBottom());
          }

          // Move the layout
          inner.layout(inner.getLeft() + deltaX / 3, inner.getTop(),
              inner.getRight() + deltaX / 3, inner.getBottom());

          left += (deltaX / 6);
        }

        isCount = true;
        x = nowX;
        break;

      default:
        break;

    }
  }

  /**
   * Retract animation
   */
  public void animation() {
    TranslateAnimation taa = new TranslateAnimation(0, 0, left + 200, initLeft + 200);
    taa.setDuration(200);
    TranslateAnimation ta = null;
    // Turn on moving animation
    ta = new TranslateAnimation(inner.getLeft(), normal.left, 0, 0);
    ta.setDuration(200);
    inner.startAnimation(ta);
    // Set back to the normal layout position
    inner.layout(normal.left, normal.top, normal.right, normal.bottom);
    normal.setEmpty();

    isCount = false;
    x = 0;// Fingers loose to 0..

  }

  // Whether you need to turn on animation
  public boolean isNeedAnimation() {
    return !normal.isEmpty();
  }

  /***
   * Whether you need to move the layout inner.getMeasuredHeight (): get the total height of the control
   *
   * GetHeight (): Get the height of the screen.
   *
   * @return
   */
  public void isNeedMove() {
    int scrollY = getScrollY();
    if (scrollY == 0) {
      isMoveing = true;
    }
  }

  public void setDrawerLayout(DrawerLayout drawerLayout) {
    this.drawerLayout = drawerLayout;
  }
}