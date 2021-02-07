package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.eco.basics.handler.Num;

import lombok.Getter;

/**
 * 多种类view
 * 根据shape设置view形状
 * arrow_label: 箭头(折扣, 指向等使用) 建议比例: w:h=1:0.4
 * banner_i: 横幅(页面顶部横幅) 建议比例: w:h=1:0.2
 * banner_silk: 锦旗 建议比例: w:h=1:1.6
 * 绘制顺序: 背景(自定义形状，多层自定义形状) -> 边框(自定义形状) -> 文字(自定义位置)
 * 注：不使用dyView中的基础绘制功能
 * -----------------demo-----------------
 * <DyMultipleView
 * android:layout_width="350dp"
 * android:layout_height="120dp"
 * app:dy_bg_color="@color/kc_banner_bg"
 * app:dy_border_color_end="@color/end"
 * app:dy_border_color_gd="vertical"
 * app:dy_border_color_start="@color/start"
 * app:dy_border_width="3dp"
 * app:dy_fillet_radius="10dp"
 * app:dy_shape="fillet"
 * app:dy_text="30%"
 * app:dy_text_bold="true"
 * app:dy_text_color="@color/white"
 * app:dy_text_location="center"
 * app:dymv_shape="banner_i" />
 */
public class DyMultipleView extends LinearLayout implements DyBaseView {
  static final String TAG = "MULTIPLE_VIEW";

  @Getter
  public DyView dyView;

  //形状
  public int mpShape;

  public DyMultipleView(Context context) {
    this(context, null);
  }

  public DyMultipleView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyMultipleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setWillNotDraw(false); //设置为false否则不执行onDraw
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_multiple_view);
      //形状
      mpShape = typedArray.getInt(R.styleable.dy_multiple_view_dymv_shape, 0);
      typedArray.recycle();
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    initText();
    drawShape(canvas);
  }

  /**
   * 1.初始化文字
   * 根据形状计算文字大小
   */
  public void initText() {
    dyView.initView();
    if (dyView.openText) {
      if (DyViewState.DyMv.SHAPE_TYPE.ARROW_LABEL.v().equals(mpShape)) { //箭头标签
        dyView.textSize = Num.mForF(dyView.viewRect.height() - dyView.borderWidth * 2, 0.9f);
      } else if (DyViewState.DyMv.SHAPE_TYPE.BANNER_I.v().equals(mpShape)) { //横幅i
        dyView.textSize = Num.mForF(dyView.viewRect.height() - dyView.borderWidth * 2, 0.5f);
      }
      if (DyViewState.DyMv.SHAPE_TYPE.BANNER_I.v().equals(mpShape)) {
        dyView.textRect.bottom = dyView.bgRect.height() - Num.mForF(dyView.bgRect.height(), 0.25f); //重新计算文字位置
      }

      dyView.textPaint.setTextSize(dyView.textSize); //重新设置文字字号
    }
  }

  /**
   * 1.绘制形状
   * 根据形状生成每个部分的path(背景,边框)
   */
  public void drawShape(Canvas canvas) {
    if (DyViewState.DyMv.SHAPE_TYPE.ARROW_LABEL.v().equals(mpShape)) { //箭头标签
      if (dyView.openBg) {
        dyView.drawBg(canvas, initArrowLabel(dyView.bgRect));
      }
      if (dyView.openBorder) {
        dyView.drawBorder(canvas, initArrowLabel(dyView.borderRect));
      }
    } else if (DyViewState.DyMv.SHAPE_TYPE.BANNER_I.v().equals(mpShape)) { //横幅I
      if (dyView.openBg) {
        dyView.drawBg(canvas, initBannerI(dyView.bgRect, dyView.filletRadius, Boolean.TRUE)); //绘制横幅基础样式
        //绘制横幅的阴影块
        Paint paint = new Paint(dyView.bgPaint);
        paint.setColor(Color.BLACK);
        paint.setAlpha(50);
        canvas.drawPath(initBannerIForShadowBlock(dyView.bgRect, dyView.filletRadius, 1), paint);
        Paint paint2 = new Paint(paint);
        paint2.setAlpha(120);
        canvas.drawPath(initBannerIForShadowBlock(dyView.bgRect, dyView.filletRadius, 2), paint2);
      }

      if (dyView.openBorder) {
        dyView.drawBorder(canvas, initBannerI(dyView.borderRect, dyView.borderRadius, Boolean.FALSE));
      }
    } else if (DyViewState.DyMv.SHAPE_TYPE.BANNER_SILK.v().equals(mpShape)) { //锦旗
      if (dyView.openBg) {
        dyView.drawBg(canvas, initBannerSilk(dyView.bgRect, dyView.filletRadius, Boolean.TRUE));
      }
      if (dyView.openBorder) {
        dyView.drawBorder(canvas, initBannerSilk(dyView.borderRect, dyView.borderRadius, Boolean.FALSE));
      }
    }
    dyView.drawText(canvas);
  }

  //初始化"箭头标签"
  public Path initArrowLabel(RectF rect) {
    Path path = new Path();
    float arrowWidth = Num.mForF(rect.width(), 0.15f);
    float arrowHeight = rect.centerY();
    path.moveTo(rect.left, arrowHeight);
    path.lineTo(rect.left + arrowWidth, rect.top);
    path.lineTo(rect.right, rect.top);
    path.lineTo(rect.right - arrowWidth, arrowHeight);
    path.lineTo(rect.right, rect.bottom);
    path.lineTo(rect.left + arrowWidth, rect.bottom);
    path.close();
    return path;
  }

  /**
   * 初始化"横幅i"
   *
   * @param rect
   * @param filletRadius
   * @param close        是否闭合, false: 使用moveTo进行绘制(如过使用lineTo会导致圆角处出现多余线条),在交界处需要将边框部分减去
   * @return
   */
  public Path initBannerI(RectF rect, float filletRadius, boolean close) {
    float topSideWidth = Num.mForF(rect.width(), 0.12f); //上方两边宽度
    float bottomSideWidth = Num.mForF(topSideWidth, 1.5f); //下方两边宽度
    float sideHeight = Num.mForF(rect.height(), 0.25f); //侧边高度
    float arrowWidth = Num.mForF(topSideWidth, 0.5f); //箭头宽度
    float arrowHeight = Num.mForF(rect.height() - sideHeight, 0.5f); //箭头高度
    //dyView.textRect.bottom = rect.height() - sideHeight; //重新计算文字位置

    Path shapePath = new Path();
    if (filletRadius > 0) {
      //左上 - 圆角
      shapePath.moveTo(rect.left + topSideWidth, rect.top + filletRadius);
      shapePath.quadTo(rect.left + topSideWidth, rect.top, rect.left + topSideWidth + filletRadius, rect.top);
      //右上 - 圆角
      shapePath.moveTo(rect.right - topSideWidth - filletRadius, rect.top);
      shapePath.quadTo(rect.right - topSideWidth, rect.top, rect.right - topSideWidth, rect.top + filletRadius);
      //右下 - 圆角
      shapePath.moveTo(rect.right - bottomSideWidth + filletRadius, rect.bottom);
      shapePath.quadTo(rect.right - bottomSideWidth, rect.bottom, rect.right - bottomSideWidth, rect.bottom - filletRadius);
      //左下 - 圆角
      shapePath.moveTo(rect.left + bottomSideWidth, rect.bottom - filletRadius);
      shapePath.quadTo(rect.left + bottomSideWidth, rect.bottom, rect.left + bottomSideWidth - filletRadius, rect.bottom);
    }

    //左上侧边
    shapePath.moveTo(rect.left, rect.top + sideHeight);
    shapePath.lineTo(rect.left + topSideWidth, rect.top + sideHeight);
    //左上侧边 - 圆角
    shapePath.lineTo(rect.left + topSideWidth, rect.top + filletRadius);
    if (close) shapePath.lineTo(rect.left + topSideWidth + filletRadius, rect.top);
    else shapePath.moveTo(rect.left + topSideWidth + filletRadius, rect.top);
    //右上侧边 - 圆角
    shapePath.lineTo(rect.right - topSideWidth - filletRadius, rect.top);
    if (close) shapePath.lineTo(rect.right - topSideWidth, rect.top + filletRadius);
    else shapePath.moveTo(rect.right - topSideWidth, rect.top + filletRadius);
    //右上侧边
    shapePath.lineTo(rect.right - topSideWidth, rect.top + sideHeight);
    shapePath.lineTo(rect.right, rect.top + sideHeight);
    //右边 - 箭头
    shapePath.lineTo(rect.right - arrowWidth, rect.top + sideHeight + arrowHeight);
    shapePath.lineTo(rect.right, rect.bottom);
    //右下侧边 - 圆角
    shapePath.lineTo(rect.right - bottomSideWidth + filletRadius, rect.bottom);
    if (close) shapePath.lineTo(rect.right - bottomSideWidth, rect.bottom - filletRadius);
    else shapePath.moveTo(rect.right - bottomSideWidth, rect.bottom - filletRadius);
    //右下侧边
    shapePath.lineTo(rect.right - bottomSideWidth, rect.bottom - sideHeight);
    shapePath.lineTo(rect.left + bottomSideWidth, rect.bottom - sideHeight);
    //左下侧边 - 圆角
    shapePath.lineTo(rect.left + bottomSideWidth, rect.bottom - filletRadius);
    if (close) shapePath.lineTo(rect.left + bottomSideWidth - filletRadius, rect.bottom);
    else shapePath.moveTo(rect.left + bottomSideWidth - filletRadius, rect.bottom);
    //左边 - 箭头
    shapePath.lineTo(rect.left, rect.bottom);
    shapePath.lineTo(rect.left + arrowWidth, rect.top + sideHeight + arrowHeight);
    shapePath.lineTo(rect.left, rect.top + sideHeight);
    shapePath.lineTo(dyView.borderWidth, rect.top + sideHeight);
    if (close) shapePath.close();
    return shapePath;
  }

  /**
   * 初始化"横幅i - 阴影块"
   * 共两个阴影块
   * 1.横幅底部突出高度 = height*0.25
   * 2.横幅底部突出宽度 = width*0.18
   *
   * @param rect
   * @param filletRadius
   * @param level        阴影块级别，越大阴影越深
   * @return
   */
  public Path initBannerIForShadowBlock(RectF rect, float filletRadius, int level) {
    float borderWidth = dyView.borderWidth;
    float borderWidthHalf = Num.mForF(borderWidth, 0.5f);
    float filletRadiusHalf = Num.mForF(filletRadius, 0.5f);
    rect = new RectF(rect.left, rect.top, rect.right, rect.bottom);

    float topSideWidth = Num.mForF(rect.width(), 0.12f); //上方两边宽度
    float bottomSideWidth = Num.mForF(topSideWidth, 1.5f); //下方两边宽度
    float sideHeight = Num.mForF(rect.height(), 0.25f); //侧边高度
    float bottomSideCurveHeight = Num.mForF(sideHeight, 0.33f); //底部侧边曲线高度
    float shadowHeight = Num.mForF(rect.height() - sideHeight, 0.08f); //阴影高度
    float arrowWidth = Num.mForF(topSideWidth, 0.5f); //箭头宽度
    float arrowHeight = Num.mForF(rect.height() - sideHeight, 0.5f); //箭头高度

    Path path = new Path();
    if (filletRadius > 0) {
      //右下 - 圆角
      path.moveTo(rect.right - bottomSideWidth + filletRadius, rect.bottom);
      path.quadTo(rect.right - bottomSideWidth, rect.bottom, rect.right - bottomSideWidth, rect.bottom - filletRadius);
      //左下 - 圆角
      path.moveTo(rect.left + bottomSideWidth, rect.bottom - filletRadius);
      path.quadTo(rect.left + bottomSideWidth, rect.bottom, rect.left + bottomSideWidth - filletRadius, rect.bottom);
    }
    if (level == 1) {
      if (filletRadius > 0) {
        //右下横幅 - 圆角
        path.moveTo(rect.right - topSideWidth - borderWidthHalf, rect.bottom - sideHeight);
        path.quadTo(rect.right - topSideWidth - borderWidthHalf, rect.bottom - sideHeight - shadowHeight, rect.right - topSideWidth - borderWidthHalf - shadowHeight, rect.bottom - sideHeight - shadowHeight);
        //左下横幅 - 圆角
        path.moveTo(rect.left + topSideWidth + borderWidthHalf, rect.bottom - sideHeight);
        path.quadTo(rect.left + topSideWidth + borderWidthHalf, rect.bottom - sideHeight - shadowHeight, rect.left + topSideWidth + borderWidthHalf + shadowHeight, rect.bottom - sideHeight - shadowHeight);
      }
      //左侧块
      path.moveTo(rect.left, rect.top + sideHeight);
      path.lineTo(rect.left + topSideWidth + borderWidthHalf, rect.top + sideHeight);
      path.lineTo(rect.left + topSideWidth + borderWidthHalf, rect.bottom - sideHeight);
      path.lineTo(rect.left + topSideWidth + borderWidthHalf + shadowHeight, rect.bottom - sideHeight - shadowHeight);
      //右侧块
      path.lineTo(rect.right - topSideWidth - borderWidthHalf - shadowHeight, rect.bottom - sideHeight - shadowHeight);
      path.lineTo(rect.right - topSideWidth - borderWidthHalf, rect.bottom - sideHeight);
      path.lineTo(rect.right - topSideWidth - borderWidthHalf, rect.top + sideHeight);
      path.lineTo(rect.right, rect.top + sideHeight);
      //右侧块 - 箭头
      path.lineTo(rect.right - arrowWidth, rect.top + sideHeight + arrowHeight);
      path.lineTo(rect.right, rect.bottom);
      //右侧块 - 底部
      path.lineTo(rect.right - bottomSideWidth + filletRadius, rect.bottom);
      path.lineTo(rect.right - bottomSideWidth, rect.bottom - filletRadius);
      path.lineTo(rect.right - bottomSideWidth, rect.bottom - sideHeight);
      //左侧块 - 底部
      path.lineTo(rect.left + bottomSideWidth, rect.bottom - sideHeight);
      path.lineTo(rect.left + bottomSideWidth, rect.bottom - filletRadius);
      path.lineTo(rect.left + bottomSideWidth - filletRadius, rect.bottom);
      //左侧块 - 箭头
      path.lineTo(rect.left, rect.bottom);
      path.lineTo(rect.left + arrowWidth, rect.top + sideHeight + arrowHeight);
      path.close();
    } else if (level == 2) {
      if (filletRadius > 0) {
        //左侧块 - 上圆角
        path.moveTo(rect.left + bottomSideWidth - (bottomSideWidth - topSideWidth) + shadowHeight, rect.bottom - sideHeight - borderWidthHalf);
        path.quadTo(rect.left + bottomSideWidth - (bottomSideWidth - topSideWidth), rect.bottom - sideHeight - borderWidthHalf + bottomSideCurveHeight / 2, rect.left + bottomSideWidth - (bottomSideWidth - topSideWidth) + shadowHeight, rect.bottom - sideHeight - borderWidthHalf + bottomSideCurveHeight);
      }
      //左侧块
      path.moveTo(rect.left, rect.bottom);
      //左侧块 - 下圆角
      path.lineTo(rect.left + bottomSideWidth - filletRadius, rect.bottom);
      path.lineTo(rect.left + bottomSideWidth, rect.bottom - filletRadius);
      path.lineTo(rect.left + bottomSideWidth, rect.bottom - sideHeight - borderWidthHalf);
      //左侧块 - 上圆角
      path.lineTo(rect.left + bottomSideWidth - (bottomSideWidth - topSideWidth) + shadowHeight, rect.bottom - sideHeight - borderWidthHalf);
      path.lineTo(rect.left + bottomSideWidth - (bottomSideWidth - topSideWidth) + shadowHeight, rect.bottom - sideHeight - borderWidthHalf + bottomSideCurveHeight);
      //左侧块 - 内角
      path.lineTo(rect.left + bottomSideWidth - shadowHeight, rect.bottom - shadowHeight - bottomSideCurveHeight / 2);
      path.lineTo(rect.left + bottomSideWidth - filletRadius, rect.bottom - shadowHeight);
      path.lineTo(rect.left + shadowHeight, rect.bottom - shadowHeight);
      path.close();

      if (filletRadius > 0) {
        //右侧块 - 上圆角
        path.moveTo(rect.right - bottomSideWidth + (bottomSideWidth - topSideWidth) - shadowHeight, rect.bottom - sideHeight - borderWidthHalf);
        path.quadTo(rect.right - bottomSideWidth + (bottomSideWidth - topSideWidth), rect.bottom - sideHeight - borderWidthHalf + bottomSideCurveHeight / 2, rect.right - bottomSideWidth + (bottomSideWidth - topSideWidth) - shadowHeight, rect.bottom - sideHeight - borderWidthHalf + bottomSideCurveHeight);
      }
      //右侧块
      path.moveTo(rect.right, rect.bottom);
      //右侧块 - 下圆角
      path.lineTo(rect.right - bottomSideWidth + filletRadius, rect.bottom);
      path.lineTo(rect.right - bottomSideWidth, rect.bottom - filletRadius);
      path.lineTo(rect.right - bottomSideWidth, rect.bottom - sideHeight - borderWidthHalf);
      //右侧块 - 上圆角
      path.lineTo(rect.right - bottomSideWidth + (bottomSideWidth - topSideWidth) - shadowHeight, rect.bottom - sideHeight - borderWidthHalf);
      path.lineTo(rect.right - bottomSideWidth + (bottomSideWidth - topSideWidth) - shadowHeight, rect.bottom - sideHeight - borderWidthHalf + bottomSideCurveHeight);
      //右侧块 - 内角
      path.lineTo(rect.right - bottomSideWidth + shadowHeight, rect.bottom - shadowHeight - bottomSideCurveHeight / 2);
      path.lineTo(rect.right - bottomSideWidth + filletRadius, rect.bottom - shadowHeight);
      path.lineTo(rect.right - shadowHeight, rect.bottom - shadowHeight);
      path.close();


    }


    return path;
  }

  /**
   * 初始化"锦旗"
   * 锦旗尾部高度 = h*0.2, 锦旗中的内容尽量在h*0.8的高度内
   *
   * @param rect
   * @param filletRadius
   * @param close        是否闭合, false: 使用moveTo进行绘制(如过使用lineTo会导致圆角处出现多余线条),在交界处需要将边框部分减去
   * @return
   */
  public Path initBannerSilk(RectF rect, float filletRadius, boolean close) {
    float halfWidth = Num.mForF(rect.width(), 0.5f); //一半的宽度
    float silkTailHeight = Num.mForF(rect.height(), 0.2f); //锦旗尾部高度

    Path shapePath = new Path();
    if (filletRadius > 0) {
      //左上 - 圆角
      shapePath.moveTo(rect.left, rect.top + filletRadius);
      shapePath.quadTo(rect.left, rect.top, rect.left + filletRadius, rect.top);
      //右上 - 圆角
      shapePath.moveTo(rect.right - filletRadius, rect.top);
      shapePath.quadTo(rect.right, rect.top, rect.right, rect.top + filletRadius);
    }

    //范围
    shapePath.moveTo(rect.left, rect.bottom - silkTailHeight); //左尾
    shapePath.lineTo(rect.left, rect.top + filletRadius); //左尾 -> 左上圆角
    if (close) shapePath.lineTo(rect.left + filletRadius, rect.top); //左上圆角 -> 左上边圆角
    else shapePath.moveTo(rect.left + filletRadius - dyView.borderWidth, rect.top); //左上圆角 -> 左上边圆角
    shapePath.lineTo(rect.right - filletRadius, rect.top); // -> 左上边圆角 -> 右上边圆角
    if (close) shapePath.lineTo(rect.right, rect.top + filletRadius); //右上边圆角 -> 右上圆角
    else shapePath.moveTo(rect.right, rect.top + filletRadius - dyView.borderWidth); //右上边圆角 -> 右上圆角
    shapePath.lineTo(rect.right, rect.bottom - silkTailHeight); //右上圆角 -> 右尾
    shapePath.lineTo(halfWidth, rect.bottom); //右尾 -> 锦旗尾部
    if (close) shapePath.close();
    else {
      shapePath.lineTo(rect.left, rect.bottom - silkTailHeight); //锦旗尾部 -> 左尾
      shapePath.lineTo(rect.left, rect.bottom - silkTailHeight - dyView.borderWidth); //填充却是部分
    }
    return shapePath;
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((int) dyView.width, (int) dyView.height);
  }
}