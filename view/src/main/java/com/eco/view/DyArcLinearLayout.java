package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * 弧形布局 上弧形或下弧形
 * 绘制顺序: 背景 -> 弧形 -> 弧形边框
 * -----------------demo-----------------
 * <DyArcLinearLayout
 * android:layout_width="match_parent"
 * android:layout_height="200dp"
 * android:gravity="center_horizontal|center_vertical"
 * app:dyav_arc_direction="top"
 * app:dy_bg_color="@color/kci_bottom_bg"
 * app:dy_border_color="@color/kci_bottom_border"
 * app:dy_border_width="@dimen/kci_arc_border"
 * app:dyav_arc_height="30dp">
 * ------任意view------
 * <DyArcLinearLayout/>
 */
public class DyArcLinearLayout extends LinearLayout implements DyBaseView {

  private DyView dyView;
  //弧形
  private int direction = DyViewState.DyArcll.DIRECTION; //方向
  private float arcHeight = DyViewState.DyArcll.ARC_HEIGHT; //弧形高度

  public DyArcLinearLayout(Context context) {
    this(context, null);
  }

  public DyArcLinearLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyArcLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    //Color.parseColor("#303F9F")
    super(context, attrs, defStyleAttr);
    setWillNotDraw(false); //LinearLayout需要设置为false否则不执行onDraw
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_arc_view);
      direction = typedArray.getInt(R.styleable.dy_arc_view_dyav_arc_direction, 0);
      arcHeight = typedArray.getDimension(R.styleable.dy_arc_view_dyav_arc_height, 0);
      typedArray.recycle();
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    dyView.initView();

    float top, bottom;
    float yArcStart, yArcEnd;

    //yArcEnd：弧线最终位置，由于拉伸后的弧线实际高度为arcHeight/2，所以要将控制点Y的值设置为arcHeight的2倍（向上：从arcHeight位置开始拉动到-arcHeight位置停止，实际绘制到的位置是0）
    switch (direction) {
      case 0:
        top = arcHeight;
        bottom = dyView.height;
        yArcStart = top + 1;
        yArcEnd = dyView.borderWidth - top;
        break;
      case 1:
        top = 0;
        bottom = dyView.height - arcHeight;
        yArcStart = bottom;
        yArcEnd = dyView.height + arcHeight - dyView.borderWidth;
        break;
      default: //如果方向错误则使用默认方式绘制
        super.onDraw(canvas);
        return;
    }
    if (null != dyView.bgRect) {
      dyView.bgRect.set(0, top, dyView.width, bottom); //重置view绘制范围
      dyView.drawBg(canvas); //绘制背景
    }

    // 18 * 11
    //       9,0
    //        |
    //        |
    //0,2     |     18,2
    //        |
    //        |
    //--------|--------
    //        |
    //        |
    //        |
    //        |
    //        |
    //0，2为起始点， 18，2为结束点， 9，0为控制点 向控制点绘制贝塞尔曲线
    /**
     * 绘制贝塞尔曲线
     * point1 = 起始点， point2 = 控制点， point3 = 结束点
     * point1 到 point3 连接到一起是一条直线
     * point2 为 拉动直线的点
     * moveTo = point1, quadTo(0,1) = paint2, quadTo(2,3) = paint3
     */
    Path path = new Path();
    path.moveTo(0, yArcStart); //设置起始点
    path.quadTo(dyView.width / 2f, yArcEnd, dyView.width, yArcStart); //设置 控制点和结束点
    canvas.drawPath(path, dyView.bgPaint); //绘制贝塞尔曲线
    dyView.initBorder();
    canvas.drawPath(path, dyView.borderPaint); //绘制边框，在原有的弧线上覆盖borderWidth宽度的边框，不会改变整体高度
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((int) dyView.maxWidth, (int) dyView.maxHeight);
  }
}