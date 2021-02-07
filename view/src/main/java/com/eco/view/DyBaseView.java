package com.eco.view;


import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * DyView的基础接口, 实现此接口的类均为DyView类型
 * 1.此接口里面可以不定义任何方法，定义方法是为了提示使用，防止view忘记重写父类的方法
 * 2.此接口作用: 方便验证类型, 由于实现类的父类是不同的view所以不能统一验证类型,通过此接口来验证view的类型
 */
public interface DyBaseView {

  void onDraw(Canvas canvas);

  boolean onTouchEvent(MotionEvent event);

  void onMeasure(int widthMeasureSpec, int heightMeasureSpec);
}
