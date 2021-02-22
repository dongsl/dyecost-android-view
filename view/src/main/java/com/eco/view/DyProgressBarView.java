package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.databinding.BindingAdapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;

import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.ImageHandler;
import com.eco.basics.handler.Num;
import com.eco.basics.handler.StringHandler;
import com.eco.basics.handler.SystemHandler;
import com.eco.basics.handler.ThreadPoolHandler;
import com.eco.basics.timer.DyCountDownTimerBuilder;
import com.eco.basics.timer.DyCountDownTimerCallBack;
import com.eco.view.builder.DyTextViewBuilder;

import lombok.Getter;

/**
 * 进度条DyProgressBarView
 * 绘制顺序: (背景 -> 进度条分割线 -> 进度条) -> (进度条外圈 -> 背景 -> 进度条内圈边框 -> 进度条外圈进度) -> 边框 -> 进度条文字 -> 图片背景 -> 图片 -> 图片边框
 * 注：
 * 1.初始化时图片优先使用pbImageFilePath,如果为空或路径不存在在使用pbImageResourceId
 * 2.使用setImage设置图片时，如果传入的图片不存在则不会重新绘制
 * 3.图片背景色和边框使用进度条的参数，如果bitmap为空则不会绘制图片
 * 4.在绘制进度时，dypbv_ratio_type=percentage时候会已100做为基础比例与dypbv_ratio_max进行计算
 * 5.进度条分割线0%和100%的位置不会存在(如：每10%一个分割线，一共会绘制9个，10%-90%每隔10%绘制一个)
 * 6.设置text后会在进度条中显示文字并追加百分比
 * 7.部分text_location类型会根据text_size对进度条高度进行动态修改
 * 8.dypbv_shape=rectangle_h_*时text_location才会生效,circular时候文字在中间
 * 9.dypbv_shape=rectangle_h_*进度每次都会绘制100%然后将多余的部分裁剪，如果不绘制100%会导致圆角的角度与边框角度不一致
 * 10.dypbv_ratio_type=seconds时会将ratio*1000,以毫秒单位进行增加进度
 * 11.调用set方法时会根据dypbv_ratio_mode类型对比例进行增加或减少
 * 12.dypbv_direction=ALONG时"横向进度条"是从左向右增加或从右向左减少,"圆形进度条"是从顶部0°开始顺时针增加或从ratio°向0°减少
 * 13.文字大小会根据text+ratio自动设置,如果手动设置的textSize小于自动计算的文字大小,则使用手动设置的
 * 14.dypbv_ratio_mode="desc"时调用startCountDownTimer(A, B)方法时A>0,A<=B才会有效果,"incr"时A<B
 * -----------------demo-----------------
 * <DyProgressBarView
 * android:layout_width="match_parent"
 * android:layout_height="60dp"
 * app:dy_bg_color="@color/black"
 * app:dy_bg_color_alpha="@integer/translucent_alpha"
 * app:dy_border_color="@color/red"
 * app:dy_border_width="@dimen/kc_pb_border_width"
 * app:dy_fillet_radius="@dimen/kc_dy_fillet"
 * app:dy_fixed_text="false"
 * app:dy_shape="fillet"
 * app:dy_text="加载"
 * app:dy_text_bold="true"
 * app:dy_text_color="@color/kcq_reward_target_text"
 * app:dy_text_location="start"
 * app:dypbv_color="@color/kc_pb"
 * app:dypbv_cut_color="@color/black"
 * app:dypbv_cut_width="1dp"
 * app:dypbv_image_resource="@drawable/bg_matching"
 * app:dypbv_ratio="3"
 * app:dypbv_ratio_max="10"
 * app:dypbv_ratio_mode="decr"
 * app:dypbv_ratio_type="percentage"
 * app:dypbv_shape="rectangle_h_image" />
 */
public class DyProgressBarView extends View implements DyBaseView {

  public String TASK_NAME = this.toString();
  @Getter
  public DyView dyView;
  public RectF viewRect;
  private DyCountDownTimerBuilder timerBuilder;
  private long refreshInterval = 10;
  //形状
  private boolean isCircular = Boolean.FALSE;
  private int pbShape = DyViewState.DyPbll.SHAPE_TYPE.RECTANGLE_H.v();
  private int pbDirection = DyViewState.DyPbll.DIRECTION.ALONG.v(); //进度条方向
  //文字
  private String pbText;
  //进度条分割线
  private boolean openPbCut;
  private Paint pbCutPaint;
  @FloatRange(from = 0.0, to = 1.0)
  private float pbCutRatio = DyViewState.DyPbll.PB_CUT_RATIO; //分割线比例(百分比)
  private float pbCutWidth = DyViewState.DyPbll.PB_CUT_WIDTH; //分割线宽度
  private int pbCutColor = DyViewState.DyPbll.PB_CUT_COLOR; //分割线颜色
  //进度条
  private boolean openPb;
  private boolean isRatioSeconds = Boolean.FALSE;
  private boolean isIncr = Boolean.TRUE;
  private RectF pbRect;
  private Paint pbPaint;
  private int pbColor = DyViewState.DyPbll.PB_COLOR; //进度条颜色
  private long pbRatioMax = 100; //进度条最大宽度，默认100%或100毫秒
  private long pbRatio = 0; //进度条当前进度，进度条比例(0%-100% 或 倒计时)
  private int pbRatioType = DyViewState.DyPbll.PB_RATIO_TYPE.PERCENTAGE.v(); //进度条类型(百分比或秒)
  private int pbRatioMode = DyViewState.DyPbll.PB_RATIO_MODE.INCR.v(); //进度条进度方式
  private int pbAlpha = DyViewState.DyPbll.PB_ALPHA; //进度条透明度

  //圆形进度条
  private RectF pbInnerRingBorderRect; //内环边框
  private float pbInnerRingSize; //内环大小
  private RectF pbOuterRingBgRect; //外环背景
  private Paint pbOuterRingBgPaint; //外环背景
  private int pbOuterRingBgColor = DyViewState.DyPbll.PB_COLOR; //外环背景色
  private float pbOuterRingWidth; //外环宽度
  private float pbOuterRingWidthHalf;
  //图片
  private boolean openPbImage;
  //图片边框
  private RectF pbImageBorderRect;
  private Paint pbImageBorderPaint;
  //图片背景
  private RectF pbImageBgRect;
  private Paint pbImageBgPaint;
  //图片
  private RectF pbImageRect;
  private Paint pbImagePaint;
  private String pbImageFilePath; //图片地址(优先级：高)
  private int pbImageResourceId; //图片资源(优先级：低)
  private Bitmap bitmap; //图片

  //监听
  private RatioListener ratioListener;
  private RatioCompleteListener ratioCompleteListener;

  private PbShape shape;

  public DyProgressBarView(Context context) {
    this(context, null);
  }

  public DyProgressBarView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyProgressBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_pb_view);
      //形状
      pbShape = typedArray.getInt(R.styleable.dy_pb_view_dypbv_shape, DyViewState.DyPbll.SHAPE_TYPE.RECTANGLE_H.v()); //调整view大小
      pbDirection = typedArray.getInt(R.styleable.dy_pb_view_dypbv_direction, DyViewState.DyPbll.DIRECTION.ALONG.v()); //进度条方向
      //进度条分割线
      pbCutRatio = typedArray.getFloat(R.styleable.dy_pb_view_dypbv_cut_ratio, DyViewState.DyPbll.PB_CUT_RATIO); //分割线比例(百分比)
      pbCutWidth = typedArray.getDimension(R.styleable.dy_pb_view_dypbv_cut_width, DyViewState.DyPbll.PB_CUT_WIDTH); //分割线宽度
      pbCutColor = typedArray.getColor(R.styleable.dy_pb_view_dypbv_cut_color, DyViewState.DyPbll.PB_CUT_COLOR); //分割线颜色
      //进度条
      pbColor = typedArray.getColor(R.styleable.dy_pb_view_dypbv_color, DyViewState.DyPbll.PB_COLOR); //进度条颜色
      pbRatioMax = typedArray.getInteger(R.styleable.dy_pb_view_dypbv_ratio_max, 100); //进度条比例
      pbRatio = typedArray.getInteger(R.styleable.dy_pb_view_dypbv_ratio, 0); //进度条比例
      pbRatioType = typedArray.getInt(R.styleable.dy_pb_view_dypbv_ratio_type, DyViewState.DyPbll.PB_RATIO_TYPE.PERCENTAGE.v()); //进度条比例
      if (pbRatioType == DyViewState.DyPbll.PB_RATIO_TYPE.PERCENTAGE.v()) {
        pbRatioMode = DyViewState.DyPbll.PB_RATIO_MODE.INCR.v();
      } else {
        pbRatioMode = typedArray.getInt(R.styleable.dy_pb_view_dypbv_ratio_mode, DyViewState.DyPbll.PB_RATIO_MODE.INCR.v()); //进度条比例
      }
      pbAlpha = typedArray.getInteger(R.styleable.dy_pb_view_dypbv_alpha, DyViewState.DyPbll.PB_ALPHA); //进度条透明度
      //圆形进度条
      pbOuterRingBgColor = typedArray.getColor(R.styleable.dy_pb_view_dypbv_outer_ring_bg_color, DyViewState.DyPbll.PB_COLOR); //外环背景色
      pbOuterRingWidth = typedArray.getDimension(R.styleable.dy_pb_view_dypbv_outer_ring_width, 0); //外环宽度
      //图片
      pbImageFilePath = typedArray.getString(R.styleable.dy_pb_view_dypbv_image_file_path); //进度条图片文件路径
      pbImageResourceId = typedArray.getResourceId(R.styleable.dy_pb_view_dypbv_image_resource, 0); //进度条图片资源
      typedArray.recycle();
    } else {

    }

    initOpenDraw();
    if (null == bitmap && StringHandler.isNotEmpty(pbImageFilePath)) {
      bitmap = BitmapFactory.decodeFile(pbImageFilePath);
    }
    if (null == bitmap && pbImageResourceId > 0) {
      bitmap = BitmapFactory.decodeResource(getResources(), pbImageResourceId);
    }
    if (isCircular) {
      dyView.shape = DyViewState.SHAPE_TYPE.CIRCULAR.v();
      shape = new Circular();
    } else {
      shape = new Rectangle();
    }
    if (isRatioSeconds) {
      pbRatioMax *= 1000;
      pbRatio *= 1000;
    }

    /*DyProgressBarView A = this;
    SystemHandler.handlerPostDelayed(() -> A.startCountDownTimer(), 500l);
    SystemHandler.handlerPostDelayed(() -> A.setForGradual("a", 50), 1000);*/
  }

  @Override
  public void onDraw(Canvas canvas) {
    initView();
    shape.draw(canvas);
  }

  public void initView() {
    initOpenDraw();
    dyView.initSystemParam();
    dyView.initShape();
    shape.initView();
  }

  public void initOpenDraw() {
    openPbCut = pbCutRatio > 0 && pbCutWidth > 0 && pbCutColor != Color.TRANSPARENT;
    openPb = pbColor != Color.TRANSPARENT;
    openPbImage = DyViewState.DyPbll.SHAPE_TYPE.RECTANGLE_H_IMAGE.v().equals(pbShape) && null != bitmap;
    isCircular = DyViewState.DyPbll.SHAPE_TYPE.CIRCULAR.v().equals(pbShape);
    isRatioSeconds = DyViewState.DyPbll.PB_RATIO_TYPE.SECONDS.v().equals(pbRatioType);
    isIncr = DyViewState.DyPbll.PB_RATIO_MODE.INCR.v().equals(pbRatioMode);
  }

  private interface PbShape {
    void initView();

    void draw(Canvas canvas);
  }

  private class Rectangle implements PbShape {

    @Override
    public void initView() {
      //根据文字或图片动态缩小进度条高度
      viewRect = new RectF(dyView.viewRect);
      if (dyView.openText) {
        initTextRectangle();//提前调用，需要用到文字大小来计算进度条高度
        float textSize = dyView.textSize;
        int textLocation = dyView.textLocation;
        float top = 0;
        if (openPbImage) {
          top = Num.mForF(dyView.viewRect.height(), 0.5f);
        } else if ((textLocation & DyViewState.TEXT_LOCATION.CENTER_TOP_HALF.v()) != 0) {
          top = viewRect.top + Num.mForF(textSize, 0.5f);
        } else if ((textLocation & DyViewState.TEXT_LOCATION.START.v()) != 0) {
          top = viewRect.top + textSize;
        }
        viewRect.top = top;
      }

      initPbRectangle();
      dyView.initBg(viewRect);
      dyView.initBorder(viewRect);
      initTextRectangle();
      initImage();
    }

    @Override
    public void draw(Canvas canvas) {
      dyView.drawBg(canvas);
      drawPbRectangle(canvas);
      dyView.drawBorder(canvas);
      drawTextRectangle(canvas);
      drawImage(canvas);
    }

    /**
     * 1.初始化矩形进度条
     * 1).进度条分割线
     * 2).进度条
     */
    private void initPbRectangle() {
      //初始化进度条分割线
      if (openPbCut) {
        if (null == pbCutPaint) pbCutPaint = new Paint();
        pbCutPaint.setAntiAlias(true); //防锯齿;
        pbCutPaint.setDither(true); //防抖动;
        pbCutPaint.setStyle(Paint.Style.STROKE);
        pbCutPaint.setColor(pbCutColor);
        pbCutPaint.setStrokeWidth(pbCutWidth);
      }

      //初始化进度条
      if (openPb) {
        if (null == pbRect) pbRect = new RectF();
        if (null == pbPaint) pbPaint = new Paint();
        //进度条范围
        pbRect.set(viewRect); //进度条范围
        if (dyView.borderWidth > 0) dyView.insetBorder(pbRect); //缩小进度条范围
        pbPaint.setAntiAlias(true); //防锯齿;
        pbPaint.setDither(true); //防抖动;
        pbPaint.setStyle(Paint.Style.FILL);
        pbPaint.setColor(pbColor);
        pbPaint.setAlpha(pbAlpha);
      }
    }

    /**
     * 2.初始化矩形进度条文字
     * 1).不设置size时,自动计算文字size
     */
    private void initTextRectangle() {
      if (dyView.openText) {
        String pbTextMax = pbText = dyView.text.toString();
        if (!dyView.isFixedText) {
          pbText = "%s";
          pbTextMax = (DyViewState.DyPbll.PB_RATIO_TYPE.PERCENTAGE.v().equals(pbRatioType) ? pbRatio + "%" : Num.dForInt(pbRatio, 1000)).toString();
          if (null != dyView.text && StringHandler.isNotBlank(dyView.text.toString().trim())) {
            pbText = dyView.text + ":" + pbText;
            pbTextMax = dyView.text + ":" + pbTextMax;
          }
        }
        float textWidth = Num.mForF(openPbImage ? dyView.viewRect.width() - dyView.viewSizeMin : dyView.viewRect.width(), 0.8f);
        float textHeight = Num.mForF(dyView.viewRect.height(), 0.5f);
        float maxTextSize = DyTextViewBuilder.initTextSizeForMin(getContext(), pbTextMax, textWidth, textHeight);
        if (dyView.textSize <= 0 || dyView.textSize > maxTextSize) {
          dyView.textSize = maxTextSize;
        }
        dyView.initText();
      }
    }

    /**
     * 3.初始化图片
     * 1).绘制图片背景色
     * 2).绘制图片
     * 3).绘制图片边框
     */
    private void initImage() {
      if (openPbImage) {
        //初始化图片背景
        if (dyView.bgColor != Color.TRANSPARENT) {
          if (null == pbImageBgRect) pbImageBgRect = new RectF();
          if (null == pbImageBgPaint) pbImageBgPaint = new Paint();
          pbImageBgRect.set(dyView.viewRect.right - dyView.viewSizeMin, dyView.viewRect.top, dyView.viewRect.right, dyView.viewRect.bottom);
          if (dyView.borderWidth > 0) dyView.insetBorder(pbImageBgRect);
          pbImageBgPaint.setAntiAlias(true); //防锯齿;
          pbImageBgPaint.setDither(true); //防抖动;
          pbImageBgPaint.setStyle(Paint.Style.FILL);
          pbImageBgPaint.setColor(dyView.bgColor);
        }

        //初始化图片
        if (null == pbImageRect) pbImageRect = new RectF();
        if (null == pbImagePaint) pbImagePaint = new Paint();
        pbImageRect.set(dyView.viewRect.right - dyView.viewSizeMin, dyView.viewRect.top, dyView.viewRect.right, dyView.viewRect.bottom);

        float imageWidth = pbImageRect.width() - dyView.imageZoomSize;
        float imageHeight = pbImageRect.height() - dyView.imageZoomSize;
        bitmap = ImageHandler.scaleBitmap(bitmap, imageWidth, imageHeight); //创建bitmap并进行缩放

        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();

        //图片范围
        pbImageRect.left += Num.mForF(pbImageRect.width() - imageWidth, 0.5f);
        pbImageRect.right = pbImageRect.left + imageWidth;
        pbImageRect.top += Num.mForF(pbImageRect.height() - imageHeight, 0.5f);
        pbImageRect.bottom = pbImageRect.top + imageHeight;
        pbImagePaint.setAntiAlias(true); //防锯齿;
        pbImagePaint.setDither(true); //防抖动;

        //初始化图片边框
        if (dyView.borderWidth > 0) {
          if (null == pbImageBorderRect) pbImageBorderRect = new RectF();
          if (null == pbImageBorderPaint) pbImageBorderPaint = new Paint();
          pbImageBorderRect.set(pbImageBgRect);
          pbImageBorderPaint.setAntiAlias(true); //防锯齿;
          pbImageBorderPaint.setDither(true); //防抖动;
          pbImageBorderPaint.setStyle(Paint.Style.STROKE);
          pbImageBorderPaint.setStrokeWidth(dyView.borderWidth);
          pbImageBorderPaint.setColor(dyView.borderColor);
        }
      }
    }

    /**
     * 1.绘制矩形进度条
     * 1).进度条分割线
     * 2).进度条
     *
     * @param canvas
     */
    private void drawPbRectangle(Canvas canvas) {
      float pbAvailableWidth = pbImageResourceId > 0 ? dyView.viewRect.width() - dyView.viewSizeMin : dyView.viewRect.width(); //进度条可用宽度（可以填充的宽度）
      float pbWidth = pbAvailableWidth * (Float.valueOf(pbRatio) / pbRatioMax); //进度条宽度

      //绘制进度条分割线, 分割线不包括左右两侧分割条 因为左右两侧通过DyView边框生成
      if (openPbCut) {
        float pbCutSpacing = pbAvailableWidth * pbCutRatio; //分割线间距
        int pbCutNumber = (int) Num.dForF(pbAvailableWidth, pbCutSpacing) - 1; //分割线数量（如果要从0开始，需要+1）（减1=移除最后一个）

        float[] bpCutPts = new float[pbCutNumber * 4];
        int index = 0;
        for (int i = 0; i < pbCutNumber; i++) {
          bpCutPts[index++] = pbCutSpacing * (i + 1);
          bpCutPts[index++] = viewRect.top;
          bpCutPts[index++] = bpCutPts[index - 3];
          bpCutPts[index++] = viewRect.bottom;
        }
        canvas.drawLines(bpCutPts, pbCutPaint);
      }

      //绘制进度条
      if (pbRatio > 0 && openPb) {
        float restWidth = pbAvailableWidth - pbWidth; //剩余宽度 = 可用宽度 - 已用宽度
        if (0 == restWidth) {
          canvas.drawRoundRect(pbRect, dyView.filletRadius, dyView.filletRadius, pbPaint); //圆角背景色
        } else {
          setLayerType(LAYER_TYPE_HARDWARE, null);
          Bitmap bitmap = Bitmap.createBitmap((int) dyView.maxWidth, (int) dyView.maxHeight, Bitmap.Config.ARGB_8888);
          canvas.drawBitmap(bitmap, 0, 0, null); //将原先的Canvas绘制在bitmap上面
          Canvas canvasTemp = new Canvas(bitmap); //以bitmap对象创建一个画布
          canvasTemp.drawRoundRect(pbRect, dyView.filletRadius, dyView.filletRadius, pbPaint); //圆角背景色
          canvasTemp.save(); //保存现在画布的形状
          //设置裁剪区域，设置完成之后Canvas会在裁剪区域绘制
          if (DyViewState.DyPbll.DIRECTION.INVERSE.v().equals(pbDirection)) {
            canvasTemp.clipRect(pbRect.left, pbRect.top, pbRect.right - pbWidth, pbRect.bottom);
          } else {
            canvasTemp.clipRect(pbRect.left + pbWidth, pbRect.top, pbRect.right, pbRect.bottom);
          }
          //Canvas 擦除颜色，由于擦除颜色使用的不是view本身的Canvas，所以不会擦除背景和父布局的view
          canvasTemp.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
          canvasTemp.restore(); //还原到save前的形状
        }
      }

    }

    /**
     * 2.绘制矩形进度条文字
     * 1).text增加百分比参数
     * 2).根据类型指定文字位置
     *
     * @param canvas
     */
    private void drawTextRectangle(Canvas canvas) {
      if (dyView.openText) {
        String text = String.format(pbText, isRatioSeconds ? Num.dForInt(pbRatio, 1000) : pbRatio + "%");
        if ((dyView.textLocation & DyViewState.TEXT_LOCATION.CENTER_TOP_HALF.v()) != 0) {
          float textWidthHalf = Num.mForF(DensityHandler.getWordWidth(text, dyView.textSize), 0.5f);
          dyView.drawText(canvas, viewRect.centerX() - textWidthHalf, DensityHandler.getFontHeight(dyView.textSize), text);
        } else {
          dyView.drawText(canvas, text);
        }
      }
    }

    /**
     * 3.绘制图片
     * 1).绘制图片背景色
     * 2).绘制图片
     * 3).绘制图片边框
     *
     * @param canvas
     */
    private void drawImage(Canvas canvas) {
      if (openPbImage) {
        //绘制图片背景色
        if (dyView.bgColor != Color.TRANSPARENT) {
          canvas.drawRoundRect(pbImageBgRect, dyView.filletRadius, dyView.filletRadius, pbImageBgPaint); //圆角背景色
        }

        //绘制图片
        canvas.drawBitmap(bitmap, pbImageRect.left, pbImageRect.top, pbImagePaint);

        //绘制图片边框
        if (dyView.borderWidth > 0) {
          canvas.drawRoundRect(pbImageBorderRect, dyView.borderRadius, dyView.borderRadius, pbImageBorderPaint); //圆角边框
        }
      }
    }

  }

  private class Circular implements PbShape {

    @Override
    public void initView() {
      dyView.initBg();
      initPbCircular();
      dyView.initBorder();
      initTextCircular();
    }

    @Override
    public void draw(Canvas canvas) {
      dyView.drawBg(canvas);
      drawPbCircular(canvas);
      dyView.drawBorder(canvas);
      drawTextCircular(canvas);
    }


    /**
     * 1.2.初始化圆形进度条
     * 1).进度条分割线
     * 2).进度条
     */
    private void initPbCircular() {
      if (null == pbOuterRingBgRect) pbOuterRingBgRect = new RectF();
      if (null == pbOuterRingBgPaint) pbOuterRingBgPaint = new Paint();
      if (null == pbInnerRingBorderRect) pbInnerRingBorderRect = new RectF();
      pbOuterRingWidth = 0 == pbOuterRingWidth ? Num.mForF(dyView.width, 0.25f) : pbOuterRingWidth;
      pbOuterRingWidthHalf = Num.mForF(pbOuterRingWidth, 0.5f);
      pbInnerRingSize = dyView.width - Num.mForF(pbOuterRingWidth, 2);

      pbOuterRingBgRect.set(dyView.viewRect); //外环背景范围
      if (dyView.borderWidth > 0) dyView.insetBorder(pbOuterRingBgRect); //缩小进度条范围
      //设置内环边框范围
      pbInnerRingBorderRect.set(pbOuterRingBgRect);
      pbInnerRingBorderRect.top += pbOuterRingWidth;
      pbInnerRingBorderRect.bottom -= pbOuterRingWidth;
      pbInnerRingBorderRect.left += pbOuterRingWidth;
      pbInnerRingBorderRect.right -= pbOuterRingWidth;

      //设置外环背景范围
      pbOuterRingBgRect.inset(pbOuterRingWidthHalf, pbOuterRingWidthHalf);
      pbOuterRingBgPaint.setAntiAlias(true); //防锯齿;
      pbOuterRingBgPaint.setDither(true); //防抖动;
      pbOuterRingBgPaint.setStyle(Paint.Style.STROKE);
      pbOuterRingBgPaint.setStrokeWidth(pbOuterRingWidth);
      pbOuterRingBgPaint.setColor(pbOuterRingBgColor);

      //初始化进度条
      if (openPb) {
        if (null == pbRect) pbRect = new RectF();
        if (null == pbPaint) pbPaint = new Paint();
        //进度条范围
        pbRect.set(pbOuterRingBgRect); //进度条范围
        pbPaint.setAntiAlias(true); //防锯齿;
        pbPaint.setDither(true); //防抖动;
        pbPaint.setStyle(Paint.Style.STROKE);
        pbPaint.setStrokeWidth(Num.mForF(pbOuterRingWidth - dyView.borderWidth, 0.65f));
        pbPaint.setColor(pbColor);
        pbPaint.setAlpha(pbAlpha);
      }

    }

    /**
     * 2.2.初始化圆形进度条文字
     * 1).根据内圈size计算文字size
     */
    private void initTextCircular() {
      if (dyView.openText) {
        String pbTextMax = pbText = dyView.text.toString();
        if (!dyView.isFixedText) {
          pbText = "%s";
          pbTextMax = (DyViewState.DyPbll.PB_RATIO_TYPE.PERCENTAGE.v().equals(pbRatioType) ? pbRatio + "%" : Num.dForInt(pbRatio, 1000)).toString();
          if (null != dyView.text && StringHandler.isNotBlank(dyView.text.toString().trim())) {
            pbText = dyView.text + ":" + pbText;
            pbTextMax = dyView.text + ":" + pbTextMax;
          }
        }
        float size = Num.mForF(pbInnerRingSize, 0.8f);
        dyView.textLocation = DyViewState.TEXT_LOCATION.CENTER.v();
        dyView.textSize = DyTextViewBuilder.initTextSizeForMin(getContext(), pbTextMax, size, size);
        dyView.initText();
      }
    }

    /**
     * 1.2.绘制圆形进度条
     * 1).外圈背景
     * 2).外圈进度条
     *
     * @param canvas
     */
    private void drawPbCircular(Canvas canvas) {
      float xCenter = pbOuterRingBgRect.centerX(), yCenter = pbOuterRingBgRect.centerY();
      float ratio = Math.min(xCenter, yCenter);
      canvas.drawRoundRect(pbOuterRingBgRect, ratio, ratio, pbOuterRingBgPaint); //圆角边框
      canvas.drawRoundRect(pbInnerRingBorderRect, ratio, ratio, getDyView().borderPaint); //圆角边框

      float startAngle = 270, sweepAngle = 360;
      sweepAngle = pbRatio >= pbRatioMax ? sweepAngle : Num.mForF(Num.dForF(sweepAngle, pbRatioMax), pbRatio);
      if (DyViewState.DyPbll.DIRECTION.INVERSE.v().equals(pbDirection)) {
        startAngle = 270 - sweepAngle;
      }
      canvas.drawArc(pbRect, startAngle, sweepAngle, false, pbPaint);
    }

    /**
     * 2.2.绘制圆形进度条文字
     *
     * @param canvas
     */
    private void drawTextCircular(Canvas canvas) {
      if (dyView.openText) {
        String text = String.format(pbText, isRatioSeconds ? Num.dForInt(pbRatio, 1000) : pbRatio + "%");
        dyView.drawText(canvas, text);
      }
    }
  }

  /**
   * 设置进度条进度, 可以设置进度条样式和倒计时样式的进度
   * 1.this.pbRatio>100时候不会重新绘制
   * 2.参数pbRatio>100时会按照100进行绘制一次
   *
   * @param ratio
   */
  public synchronized void setPbRatio(long ratio) {
    if (pbRatio == ratio || pbRatio > pbRatioMax || ratio < 0) return;
    pbRatio = ratio;
    if (pbRatio > pbRatioMax) pbRatio = pbRatioMax;
    if (pbRatio < 0) pbRatio = 0;
    SystemHandler.handlerPost(() -> invalidate());

    if (null != ratioListener) ratioListener.progress(pbRatio);
    if (pbRatioMax == pbRatio && null != ratioCompleteListener) { //绘制完成页面时, 进度达到100时候调用完成方法
      ratioCompleteListener.complete();
    }
  }

  /**
   * 启动进度条倒计时
   * 1.incr = pbRatio 到 pbRatioMax(100%)
   * 2.decr = pbRatioMax 到 pbRatio (0%)
   *
   * @param ratio    单位: 秒
   * @param ratioMax 单位: 秒
   */
  public void startCountDownTimer(Long ratio, Long ratioMax) {
    if (!isRatioSeconds) return; //只有比例类型为秒时，才可以使用倒计时
    if (null != timerBuilder) timerBuilder.cancel(); //每次启动前先停止
    if (null != ratio) this.pbRatio = ratio * 1000;
    if (null != ratioMax) this.pbRatioMax = ratioMax * 1000;
    long millisInFuture = isIncr ? pbRatioMax - pbRatio : pbRatio;
    timerBuilder = DyCountDownTimerBuilder.init(getContext())
        .millisInFuture(millisInFuture)
        .countDownInterval(refreshInterval)
        .callBack(new DyCountDownTimerCallBack() {
          @Override
          public void tick(Long millis) {
            setPbRatio(isIncr ? pbRatioMax - millis : millis);
          }

          @Override
          public void finish() {
            setPbRatio(isIncr ? pbRatioMax : 0);
          }
        }).build();
    timerBuilder.start();

  }

  /**
   * 设置进度条样式进度
   *
   * @param pbRatio
   */
  public void set(long pbRatio) {
    if (isRatioSeconds) return;
    setPbRatio(this.pbRatio + (isIncr ? pbRatio : -pbRatio));
  }

  /**
   * 设置进度条样式进度
   * 1.修改文字
   *
   * @param text
   * @param pbRatio
   */
  public void set(String text, long pbRatio) {
    this.dyView.text = text;
    set(pbRatio);
  }

  /**
   * 重置
   *
   * @param pbRatio
   */
  public void reset(long pbRatio) {
    setPbRatio(pbRatio);
  }

  /**
   * 递增进度条进度 - 渐变
   * 最终结果: pbRatio += ratio
   *
   * @param ratio
   */
  public void setForGradual(long ratio) {
    SystemHandler.handlerPost(new Runnable() {
      int i = 0;

      @Override
      public void run() {
        i++;
        set(1);
        if (i < ratio) SystemHandler.handlerPostDelayed(this, refreshInterval);
      }
    });
  }

  /**
   * 递增进度条进度 - 渐变
   * 修改文字, 进度条>100时候不会修改文字
   *
   * @param text
   * @param pbRatio
   */
  public void setForGradual(String text, long pbRatio) {
    this.dyView.text = text;
    setForGradual(pbRatio);
  }


  /**
   * 设置图片
   *
   * @param image 类型：string：file路径，int：资源路径
   */
  public void setImage(Object image) {
    bitmap = null;
    if (image instanceof String) {
      pbImageFilePath = image.toString();
      bitmap = BitmapFactory.decodeFile(pbImageFilePath);
    } else if (image instanceof Integer) {
      pbImageResourceId = (int) image;
      bitmap = BitmapFactory.decodeResource(getResources(), pbImageResourceId);
    }
    if (null != bitmap) {
      invalidate();
    }
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);

    if (isCircular) { //圆形的时候将size改为正方形
      float size = Math.min(dyView.width, dyView.height);
      dyView.setSize(size, size);
    }
    setMeasuredDimension((int) dyView.maxWidth, (int) dyView.maxHeight);
  }

  @BindingAdapter(value = {"dypbv_image_path"})
  public static void loadImage(DyProgressBarView view, String url) {
    ThreadPoolHandler.handlerThread(view.TASK_NAME, () -> {
      try {
        Bitmap bitmap = ImageHandler.getBitmap(view, url);
        view.bitmap = bitmap;
        SystemHandler.handlerPost(() -> view.invalidate());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * 自定义VIEW销毁
   */
  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    ThreadPoolHandler.removeTask(TASK_NAME);
  }

  public void setRatioListener(RatioListener ratioListener) {
    this.ratioListener = ratioListener;
  }

  public void setRatioCompleteListener(RatioCompleteListener ratioCompleteListener) {
    this.ratioCompleteListener = ratioCompleteListener;
  }

  //进度监听
  public interface RatioListener {
    void progress(long ratio);
  }

  //进度完成监听 - 达到100%
  public interface RatioCompleteListener {
    void complete();
  }
}