package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.eco.basics.handler.Num;

/**
 * 三角形
 * 可生成上下左右的三角形
 * 绘制顺序: 三角形线条 -> 背景色
 * -----------------demo-----------------
 * <DyTriangleView
 * android:layout_width=""
 * android:layout_height=""
 * app:dy_bg_color=""
 * app:dytrv_direction="" />
 */
public class DyTriangleView extends View {

  private DyView dyView;

  private Integer direction = DyViewState.DyTrv.DIRECTION; //三角形方向

  public DyTriangleView(final Context context) {
    this(context, null);
  }

  public DyTriangleView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyTriangleView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.dy_triangle_view, 0, 0);
      direction = typedArray.getInt(R.styleable.dy_triangle_view_dytrv_direction, DyViewState.DyTrv.DIRECTION);
      typedArray.recycle();
    }
    init();
  }

  private void init() {

  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Path path = new Path();
    switch (direction) {
      case DyViewState.DyTrv.TOP:
        path.moveTo(0, dyView.height);
        path.lineTo(dyView.width, dyView.height);
        path.lineTo(Num.mForF(dyView.width, 0.5f), 0);
        break;
      case DyViewState.DyTrv.BOTTOM:
        path.moveTo(0, 0);
        path.lineTo(Num.mForF(dyView.width, 0.5f), dyView.height);
        path.lineTo(dyView.width, 0);
        break;
      case DyViewState.DyTrv.RIGHT:
        path.moveTo(0, 0);
        path.lineTo(0, dyView.height);
        path.lineTo(dyView.width, Num.mForF(dyView.height, 0.5f));
        break;
      case DyViewState.DyTrv.LEFT:
        path.moveTo(0, Num.mForF(dyView.height, 0.5f));
        path.lineTo(dyView.width, dyView.height);
        path.lineTo(dyView.width, 0);
        break;
      default:
        return;
    }
    path.close();
    dyView.initShape();
    dyView.initBg();
    canvas.drawPath(path, dyView.bgPaint);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((int) dyView.maxWidth, (int) dyView.maxHeight);
  }
    
    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (width == 0 || widthMode != MeasureSpec.EXACTLY) {
            width = DensityHandler.dip2px(context, DEFUALT_WIDTH);
        }
        if (height == 0 || heightMode != MeasureSpec.EXACTLY) {
            height = DensityHandler.dip2px(context, DEFUALT_dyView.height);
        }
        setMeasuredDimension(dyView.width,dyView.height);
    }*/
}