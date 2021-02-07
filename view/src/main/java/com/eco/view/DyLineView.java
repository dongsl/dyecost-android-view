package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 线view
 * horizontal:水平线
 * vertical:垂直线
 * 后期增加：波浪线, 斜线等
 * 绘制顺序: 线条 -> 颜色
 * -----------------demo-----------------
 * <DyTriangleView
 * android:layout_width=""
 * android:layout_height=""
 * app:dy_bg_color=""
 * app:dytrv_direction="" />
 */
public class DyLineView extends View {

  private DyView dyView;

  private Integer shape = DyViewState.DyLv.VERTICAL;

  public DyLineView(final Context context) {
    this(context, null);
  }

  public DyLineView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyLineView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.dy_line_view, 0, 0);
      shape = typedArray.getInt(R.styleable.dy_line_view_dylv_shape, DyViewState.DyLv.VERTICAL);
      typedArray.recycle();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    dyView.draw(canvas);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((int) dyView.maxWidth, (int) dyView.maxHeight);
  }
}