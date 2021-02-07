package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import lombok.Getter;

/**
 * DyRelativeLayout
 * 绘制顺序: 阴影 -> 背景 -> 边框 -> 按下事件效果
 * 注：设置android:background不会生效，请使用app:dy_bg_color设置背景色
 * -----------------demo-----------------
 * <RelativeLayout
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * app:dy_bg_color=""
 * app:dy_fillet_radius=""
 * app:dy_shape=""
 * app:dy_bg_color_alpha="">
 * ------任意view------
 * <RelativeLayout/>
 */
public class DyRelativeLayout extends RelativeLayout implements DyBaseView {
  @Getter
  public DyView dyView;

  public DyRelativeLayout(Context context) {
    this(context, null);
  }

  public DyRelativeLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setWillNotDraw(false); //设置为false否则不执行onDraw
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_linear_layout);
      typedArray.recycle();
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    dyView.draw(canvas);
  }

  /**
   * 触摸事件
   *
   * @param event
   * @return
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    dyView.onTouchEvent(event);
    super.onTouchEvent(event);
    return this.hasOnClickListeners();
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((int) dyView.maxWidth, (int) dyView.maxHeight);
  }
}