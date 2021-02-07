package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import lombok.Getter;

/**
 * DyLinearLayout
 * 绘制顺序: 阴影 -> 背景 -> 边框 -> 按下事件效果
 * 注：设置android:background不会生效，请使用app:dy_bg_color设置背景色
 * -----------------demo-----------------
 * <DyLinearLayout
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * app:dy_bg_color=""
 * app:dy_fillet_radius=""
 * app:dy_shape=""
 * app:dy_bg_color_alpha="">
 * ------任意view------
 * <DyLinearLayout/>
 */
public class DyLinearLayout extends LinearLayout implements DyBaseView {
  @Getter
  public DyView dyView;
  public View[] onTouchEventViews;

  public DyLinearLayout(Context context) {
    this(context, null);
  }

  public DyLinearLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    //Color.parseColor("#303F9F")
    super(context, attrs, defStyleAttr);
    setWillNotDraw(false); //设置为false否则不执行onDraw
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_linear_layout);
      typedArray.recycle();
    }
    //根据点击监听是否存在来设置是否可以点击，如果当前view没有监听并且setClickable=true时，回导致点击不能穿透到父布局上
    //setClickable(hasOnClickListeners());
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
    if(null != onTouchEventViews){
      for(View view : onTouchEventViews){
        view.onTouchEvent(event);
      }
    }
    //requestLayout();
    //setMeasuredDimension((int) (dyView.maxWidth * 0.5), (int) (dyView.maxHeight*0.5));
    super.onTouchEvent(event);
    return this.hasOnClickListeners();
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((int) dyView.maxWidth, (int) dyView.maxHeight);
  }

  /**
   * 指定view一起触发onTouchEvent方法
   * 1.点击布局时布局下的view一起缩放
   * @param views
   */
  public void onTouchEventView(View... views){
    onTouchEventViews = views;
  }
}