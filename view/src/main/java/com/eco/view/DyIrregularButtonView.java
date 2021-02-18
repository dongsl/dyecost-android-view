package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.ImageHandler;
import com.eco.basics.handler.Num;
import com.eco.basics.handler.StringHandler;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * 不规则按钮
 * 使用PATH通过线条连接出不规则形状的按钮
 * 绘制顺序: 线条 -> 记录位置(获取点击位置) -> 文字
 * 注：不使用dyView中的基础绘制功能
 * -----------------demo-----------------
 * <DyIrregularButtonView
 * android:layout_width="280dp"
 * android:layout_height="150dp"
 * app:dy_fillet_radius=""
 * app:dy_text_spacing=""
 * app:dy_text_size=""
 * app:dy_text_color=""
 * app:dyibv_btn_text_1=""
 * app:dyibv_btn_text_2=""
 * app:dyibv_btn_text_3="" />
 */
public class DyIrregularButtonView extends View implements DyBaseView {
  static final String TAG = "IB_VIEW";

  @Getter
  public DyView dyView;

  //形状
  private List<Region> regionList; //按钮边缘集合

  //文字
  private String btnText1 = "", btnText2 = "", btnText3 = "";

  //按钮按下
  private ClickListener clickListener;
  private Integer btnDownIndex = -1; //按钮按下位置
  private float btnDownZoomRatio = 0.9f; //按钮按下缩放比例

  //图片色调
  private ColorMatrix btnDownMatrix = new ColorMatrix(); //图片色彩修改（亮度，饱和度，颜色等）
  private ColorMatrix btnDownLuminosityMatrix = new ColorMatrix(); //图片亮度
  private float btnDownLuminosity = 1.1f; //按钮按下图片亮度


  public DyIrregularButtonView(Context context) {
    this(context, null);
  }

  public DyIrregularButtonView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyIrregularButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_ib_view);

      btnText1 = typedArray.getString(R.styleable.dy_ib_view_dyibv_btn_text_1);
      if (StringHandler.isEmpty(btnText1)) btnText1 = "";
      btnText2 = typedArray.getString(R.styleable.dy_ib_view_dyibv_btn_text_2);
      if (StringHandler.isEmpty(btnText2)) btnText2 = "";
      btnText3 = typedArray.getString(R.styleable.dy_ib_view_dyibv_btn_text_3);
      if (StringHandler.isEmpty(btnText3)) btnText3 = "";
      typedArray.recycle();
    } else {

    }
    init();
  }

  public void init() {
    //设置图片亮度
    btnDownLuminosityMatrix.setScale(btnDownLuminosity, btnDownLuminosity, btnDownLuminosity, 1);
    btnDownMatrix.postConcat(btnDownLuminosityMatrix);
  }

  /**
   * 生成图案
   * 1A代表图一开始的位置
   * |----------------------------|
   * |1111111111122222222222222222|
   * |1           2A             2|
   * |1             2     btn2   2|
   * |1     btn1      2          2|
   * |1               1A32       2|
   * |1            13       32   2|
   * |1       13               322|
   * |1  13                      3|
   * |13A       btn3             3|
   * |3                          3|
   * |3                          3|
   * |3333333333333333333333333333|
   * |----------------------------|
   */
  @Override
  public void onDraw(Canvas canvas) {
    //动态设置圆角半径
    float size = Math.min(dyView.width, dyView.height);
    float maxFilletRadius = Num.mForF(size, 0.2f);
    if (dyView.filletRadius > maxFilletRadius) {
      dyView.filletRadius = maxFilletRadius;
    }
    float filletRadius = dyView.filletRadius;

    //绘制不规则线条
    float xCenter = Num.mForF(dyView.width, 0.5f);
    float yCenter = Num.mForF(dyView.height, 0.5f);
    float x2_1 = Num.mForF(dyView.width, 0.5f); //起笔点X
    float y3_2 = Num.part(dyView.height, 3, 2); //起笔点Y
    float xJunction = x2_1 + Num.mForF(x2_1, 0.2f); //接合点
    float yJunction = Num.mForF(y3_2, 0.5f); //接合点
    //初始化每个按钮的变量
    float x2_1_btn1 = x2_1, x2_1_btn2 = x2_1, x2_1_btn3 = x2_1;
    float y3_2_btn1 = y3_2, y3_2_btn2 = y3_2, y3_2_btn3 = y3_2;
    float xJunctionBtn1 = xJunction, xJunctionBtn2 = xJunction, xJunctionBtn3 = xJunction;
    float yJunctionBtn1 = yJunction, yJunctionBtn2 = yJunction, yJunctionBtn3 = yJunction;

    //动态设置字号
    String textLengthMax = btnText1.length() > btnText2.length() ? btnText1 : btnText2; //最长文字
    textLengthMax = textLengthMax.length() > btnText3.length() ? textLengthMax : btnText3;
    float textWidth = btnTextWidthHalf(textLengthMax) * 2;
    if (textWidth > x2_1) {
      float textSizeZoomRatio = (x2_1 / textWidth);
      dyView.textSize = Num.mForF(dyView.textSize, textSizeZoomRatio);
      dyView.textSpacing = Num.mForF(dyView.textSize, 0.5f);
    }

    //按钮的绘制样式
    Paint bgPaint1 = new Paint();
    bgPaint1.setAntiAlias(true); //抗锯齿
    bgPaint1.setDither(true); //防抖动
    Paint bgPaint2 = new Paint(bgPaint1), bgPaint3 = new Paint(bgPaint1);

    //按钮对应文字的绘制养殖
    Paint btnTextPaint1 = new Paint();
    btnTextPaint1.setAntiAlias(true); //抗锯齿
    btnTextPaint1.setDither(true); //防抖动
    btnTextPaint1.setColor(dyView.textColor);
    btnTextPaint1.setTextSize(dyView.textSize);
    Paint btnTextPaint2 = new Paint(btnTextPaint1), btnTextPaint3 = new Paint(btnTextPaint1);

    //初始化按钮下缩放变量
    if (0 == btnDownIndex) {
      bgPaint1.setColorFilter(new ColorMatrixColorFilter(btnDownMatrix));
      btnTextPaint1.setFakeBoldText(true);
      btnTextPaint1.setColor(Color.WHITE);
      btnTextPaint1.setTextSize(Num.mForF(dyView.textSize, btnDownZoomRatio));
      x2_1_btn1 = x2_1 * btnDownZoomRatio;
      y3_2_btn1 = y3_2 * btnDownZoomRatio;
      xJunctionBtn1 = xJunction * btnDownZoomRatio;
      yJunctionBtn1 = yJunction * btnDownZoomRatio;
    } else if (1 == btnDownIndex) {
      bgPaint2.setColorFilter(new ColorMatrixColorFilter(btnDownMatrix));
      btnTextPaint2.setFakeBoldText(true);
      btnTextPaint2.setColor(Color.WHITE);
      btnTextPaint2.setTextSize(Num.mForF(dyView.textSize, btnDownZoomRatio));
      float xZoomSpacing = x2_1 * (1 - btnDownZoomRatio);
      x2_1_btn2 = x2_1 + xZoomSpacing;
      y3_2_btn2 = y3_2 * btnDownZoomRatio;
      xJunctionBtn2 = xJunction + xZoomSpacing;
      yJunctionBtn2 = yJunction * btnDownZoomRatio;
    } else if (2 == btnDownIndex) {
      bgPaint3.setColorFilter(new ColorMatrixColorFilter(btnDownMatrix));
      btnTextPaint3.setFakeBoldText(true);
      btnTextPaint3.setColor(Color.WHITE);
      btnTextPaint3.setTextSize(Num.mForF(dyView.textSize, btnDownZoomRatio));
      float yZoomSpacing = y3_2 * (1 - btnDownZoomRatio);
      y3_2_btn3 = y3_2 + yZoomSpacing;
      yJunctionBtn3 = yJunction + yZoomSpacing;
    }

    //上图1
    //记录位置使用
    regionList = new ArrayList<>();
    Region totalRegion = new Region(0, 0, (int) dyView.width, (int) dyView.height);
    Region region;

    bgPaint1.setShader(new BitmapShader(ImageHandler.scaleBitmap(this.getResources(), R.drawable.dy_bg_blue_gradient, xJunction, xJunction), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)); //设置背景图
    Path path = new Path();
    path.moveTo(xJunctionBtn1, yJunctionBtn1);
    path.lineTo(0, y3_2_btn1);
    path.lineTo(0, filletRadius);
    path.lineTo(filletRadius, 0); //圆角
    path.lineTo(x2_1_btn1, 0);
    path.close();
    //bgPaint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)); //清空
    canvas.drawPath(path, bgPaint1);
    //记录范围
    region = new Region();
    region.setPath(path, totalRegion);
    regionList.add(region);
    //拉出圆角，左上
    path = new Path();
    path.moveTo(0, filletRadius + 1);
    path.quadTo(0, 0, filletRadius + 1, 0);
    canvas.drawPath(path, bgPaint1);

    //上图2
    bgPaint2.setAntiAlias(true); //抗锯齿
    bgPaint2.setDither(true); //防抖动
    bgPaint2.setShader(new BitmapShader(ImageHandler.scaleBitmap(this.getResources(), R.drawable.dy_bg_pink_gradient, y3_2, y3_2), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)); //设置背景图
    path = new Path();
    path.moveTo(x2_1_btn2, 0);//起点，后面的路径会从该点开始画
    path.lineTo(dyView.width - filletRadius, 0); //当前点（上次操作结束的点）会连接该点, 如果没有进行过操作则默认点为坐标原点。
    path.lineTo(dyView.width, filletRadius); //圆角
    path.lineTo(dyView.width, y3_2_btn2);
    path.lineTo(xJunctionBtn2, yJunctionBtn2);
    path.close(); // 闭合路径，即将当前点和起点连在一起，注：如果连接了最后一个点和第一个点仍然无法形成封闭图形，则close什么也不做
    canvas.drawPath(path, bgPaint2);
    //记录范围
    region = new Region();
    region.setPath(path, totalRegion);
    regionList.add(region);
    //拉出圆角，右上
    path = new Path();
    path.moveTo(dyView.width - filletRadius - 1, 0);
    path.quadTo(dyView.width, 0, dyView.width, filletRadius + 1);
    canvas.drawPath(path, bgPaint2);

    //上图3
    bgPaint3.setAntiAlias(true); //抗锯齿
    bgPaint3.setDither(true); //防抖动
    bgPaint3.setShader(new BitmapShader(ImageHandler.scaleBitmap(this.getResources(), R.drawable.dy_bg_yellow_gradient, dyView.width, dyView.width), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)); //设置背景图
    path = new Path();
    path.moveTo(0, y3_2_btn3);
    path.lineTo(0, dyView.height - filletRadius);
    path.lineTo(filletRadius, dyView.height); //圆角
    path.lineTo(dyView.width - filletRadius, dyView.height);
    path.lineTo(dyView.width, dyView.height - filletRadius); //圆角
    path.lineTo(dyView.width, y3_2_btn3);
    path.lineTo(xJunction, yJunctionBtn3);
    path.close();
    canvas.drawPath(path, bgPaint3);
    //记录范围
    region = new Region();
    region.setPath(path, totalRegion);
    regionList.add(region);
    //拉出圆角，左下,右下
    path = new Path();
    path.moveTo(0, dyView.height - filletRadius - 1);
    path.quadTo(0, dyView.height, filletRadius + 1, dyView.height);
    path.moveTo(dyView.width - filletRadius - 1, dyView.height);
    path.quadTo(dyView.width, dyView.height, dyView.width, dyView.height - filletRadius - 1);
    canvas.drawPath(path, bgPaint3);

    //绘制文字
    float fontWidth = DensityHandler.getWordWidth(dyView.textSize);
    float fontHeight = DensityHandler.getFontHeight(dyView.textSize);
    float horizontalTextSpacing = fontWidth + dyView.textSpacing;
    //绘制按钮1文字
    float xBtn1 = Num.mForF(x2_1_btn1, 0.5f) - btnTextWidthHalf(btnText1);
    float yBtn1 = Num.mForF(y3_2_btn1, 0.5f);
    for (int i = 0; i < btnText1.length(); i++) {
      canvas.drawText(btnText1.charAt(i) + "", xBtn1, yBtn1, btnTextPaint1);
      xBtn1 += horizontalTextSpacing * (0 == btnDownIndex ? btnDownZoomRatio : 1); //缩小间隔
    }
    //绘制按钮2文字
    float xBtn2 = xJunctionBtn2 + Num.mForF(dyView.width - xJunctionBtn2, 0.6f) - fontWidth;
    float yBtn2 = filletRadius;
    for (int i = 0; i < btnText2.length(); i++) {
      canvas.drawText(btnText2.charAt(i) + "", xBtn2, yBtn2, btnTextPaint2);
      if ((i + 1) % 2 == 0) {
        xBtn2 += fontWidth + 5;
        yBtn2 = filletRadius;
      } else {
        yBtn2 += fontHeight + 5;
      }
    }
    //绘制按钮3文字
    float xBtn3 = xCenter - btnTextWidthHalf(btnText3);
    float yBtn3 = y3_2_btn3 + fontHeight;
    for (int i = 0; i < btnText3.length(); i++) {
      canvas.drawText(btnText3.charAt(i) + "", xBtn3, yBtn3, btnTextPaint3);
      xBtn3 += horizontalTextSpacing * (2 == btnDownIndex ? btnDownZoomRatio : 1); //缩小间隔
    }
  }

  /**
   * 触摸事件
   *
   * @param event
   * @return
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (CollectionUtils.isEmpty(regionList)) return true;
    double x = event.getX();
    double y = event.getY();
    int action = event.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        for (int i = 0, size = regionList.size(); i < size; i++) {
          Region region = regionList.get(i);
          if (region.contains((int) x, (int) y)) {
            btnDownIndex = i;
            /*switch (i) {
              case 0:
                Log.i(TAG, "BTN1");
                break;
              case 1:
                Log.i(TAG, "BTN2");
                break;
              case 2:
                Log.i(TAG, "BTN3");
                break;
            }*/
            invalidate();
          }
        }
        break;
      case MotionEvent.ACTION_UP:
        for (int i = 0, size = regionList.size(); i < size; i++) {
          Region region = regionList.get(i);
          if (region.contains((int) x, (int) y)) {
            if (null != btnDownIndex && btnDownIndex == i) {
              switch (i) {
                case 0:
                  //Log.i(TAG, "BTN1");
                  if(null != clickListener) clickListener.oneClick();
                  break;
                case 1:
                  //Log.i(TAG, "BTN2");
                  if(null != clickListener) clickListener.twoClick();
                  break;
                case 2:
                  //Log.i(TAG, "BTN3");
                  if(null != clickListener) clickListener.threeClick();
                  break;
              }
            }
          }
        }
        btnDownIndex = -1;
        invalidate();
        break;
    }
    return true;
  }

  /**
   * 获取按钮文字宽度的中间位置
   * (文字宽度 + 每个文字之间的间距）) / 2
   *
   * @param words
   * @return
   */
  public float btnTextWidthHalf(String words) {
    return (DensityHandler.getWordWidth(words, dyView.textSize) + dyView.textSpacing * (words.length() - 1)) / 2;
  }

  public interface ClickListener {
    void oneClick();

    void twoClick();

    void threeClick();
  }

  public void setClickListener(ClickListener clickListener) {
    this.clickListener = clickListener;
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((int) dyView.width, (int) dyView.height);
  }

}