package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.FloatRange;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.ImageHandler;
import com.eco.basics.handler.Num;
import com.eco.basics.handler.StringHandler;
import com.eco.basics.log.Logger;

import java.lang.reflect.Method;

import lombok.SneakyThrows;

/**
 * 自定义view的基础类样式
 * 初始化: 形状(按下效果,viewRect,绘制状态) -> 阴影 -> 背景 -> 文字 -> 边框
 * 绘制: 阴影 -> 背景 -> 文字(工具,不自动绘制需要子类调用) -> 边框 -> 按下效果
 * <p>
 * 形状: 默认,矩形,圆角,圆形
 * dy_shape: 绘制view的范围不包括阴影部分
 * 1.RECTANGLE: 矩形时dy_fillet_radius会设置为0,使用FILLET类型来绘制view
 * 2.FILLET: 根据dy_fillet_radius圆角半径绘制view
 * 3.CIRCULAR: 圆形时dy_fillet_radius不生效，自动计算半径
 * <p>
 * 阴影：阴影会自动增加view的高度(通过height获取view真实高度)
 * dy_shadow_size 阴影大小值大于0时生效
 * dy_shadow_color 默认透明色
 * dy_shadow_alpha 默认不透明
 * <p>
 * 背景色：
 * dy_bg_color 默认透明色
 * dy_bg_alpha 默认不透明
 * <p>
 * 边框：
 * 1.边框生效时不会额外增加view高宽，在图片边缘上绘制
 * 2.形状为CIRCULAR时同时开启阴影，view会变成椭圆形，将view分为三个区域（上中下）,上下为半圆，中间为直线
 * dy_border_width 边框宽度大于0时生效
 * dy_border_color 默认黑色
 * <p>
 * 按下效果：
 * 1.只有resize时downResizeRatio才有效(DyTextView除外)，DyTextView按下时downResizeRatio和downColor可以同时生效
 * 2.drawDown只有在样式为OVERRIDE时才会绘制，其他样式通过各模块的绘制方法进行绘制
 * dy_down_style 默认OVERRIDE，最顶层增加半透明背景色
 * dy_down_color 按下时的背景色或覆盖色
 * dy_down_resize_ratio 重置view大小比例
 * OVERRIDE: dy_down_color不能为空白否则不生效
 * BACKGROUND: dy_down_color不能为空白否则不生效
 * RESIZE: dy_down_resize_ratio=0.5~1.5
 * 例子：点击后切换背景色,触摸抬起后不会还原
 * app:dy_down_color="@color/gray"
 * app:dy_down_style="background"
 * app:dy_open_selected="true"
 * <p>
 * 注：
 * 1.在绘制之前先执行init()，可以每次绘制前执行(可重复执行)，也可在View初始化完成后执行(onLayout中执行)
 * 2.暂时不使用android:***=''传入的参数，例如android:background='color'在初始化view时会清空，使用dy_bg_color设置背景色
 * -----------------demo-----------------
 * DyView dyView = new DyView(view, context, attrs);
 * 默认方式(推荐): 基础-自定义view
 * dyView.draw(canvas);
 * 自定义方式1: 指定样式绘制（调整顺序，方便配置设置内容）
 * dyView.initView();
 * dyView.drawShadow(canvas);
 * dyView.drawBorder(canvas);
 * 自定义方式2: 指定初始化paint和指定样式绘制（调整绘制顺序，过滤不需要的对象，方便设置draw）
 * initSystemParam(); 可选
 * initShape(); //必须调用(此方法会初始化一些必要参数,如viewRect)
 * dyView.initShadow();
 * dyView.drawShadow(canvas);
 * dyView.initBg();
 * dyView.drawBg(canvas);
 * <p>
 * 注1：在子类中修改属性后 必须执行initView()或initPaint()或任意initPaint
 * 注2：自定义绘制时 推荐使用"自定义方式1"
 * 注3：text的绘制需要在子类中进行,此类中只提供text基础样式
 * 注4：注释中优先级为 高或1 时优先执行(如：存在背景色和背景资源时,背景资源生效)
 * 注5：缩放(RESIZE)不支持margin和padding, 同时缩放多view时 使用onTouchEventView设置需要缩放的view
 * 注6：缩放(RESIZE)不会改变view原有大小,在原有大小上进行重新绘制, 如果同时缩放布局view和子view会导致 子view在超出布局范围
 * 注7：dy_fillet_radius可以设置为任意值，如果设置的值超过圆形的半径则会生成圆形(如:想将矩形垂直方向设置为圆形 需要设置setFilletRadius(height*0.5) 或 设置一个特别大的值最终效果和手动设置一样)
 */
public class DyView {
  private final String TAG = "dyView";
  public View view;
  public Context context;

  public float l, t; //view起点
  public float originalWidth, originalHeight, originalMaxWidth, originalMaxHeight; //原始size
  public float width, height, maxWidth, maxHeight, xCenter, yCenter; //maxWidth和maxHeight = view设置的高宽+阴影size, width和height=view设置的高度
  public ViewGroup.MarginLayoutParams params;
  //public int mt, mb, ml, mr, pt, pb, pl, pr;
  //public int originalMt, originalMb, originalMl, originalMr, originalPt, originalPb, originalPl, originalPr;

  public RectF viewRect = new RectF();
  //形状
  public boolean resizeView = Boolean.TRUE; //调整view大小(设置width和height后如果存在阴影,判断是否将阴影高度自动加到w和h中)，true: view高宽 = 高宽+shadowSize，false: view高宽 = 布局中设置高宽
  public int shape = DyViewState.SHAPE_TYPE.RECTANGLE.v(); //view形状，默认view，1:矩形，2:圆角，3:圆形
  public float filletRadius; //圆角半径
  public int viewDirectionMin; //width和height较小的那方(0:width, 1:height)
  public float viewSizeMin; //width和height较小的那方size
  public float imageZoomSize; //图片默认缩放大小(图片 - 角度 - 边框)
  public int filletDirection; //圆角方向: shape=FILLET时生效, 默认四个角都是圆角, 选择后只有对应方向为圆角
  //阴影
  public boolean openShadow; //开启阴影
  public RectF shadowRect;
  public Paint shadowPaint;
  public float shadowSize = DyViewState.SHADOW_SIZE, originalShadowSize = DyViewState.SHADOW_SIZE; //阴影大小 + 原始阴影大小
  public int shadowColor = Color.TRANSPARENT, originalShadowColor = Color.TRANSPARENT; //阴影颜色 + 原始阴影颜色(按下后改变阴影色,抬起后使用此字段还原)
  public int shadowAlpha = DyViewState.SHADOW_ALPHA, originalShadowAlpha = DyViewState.SHADOW_ALPHA; //阴影透明度 + 原始阴影透明度(按下后改变阴影透明度,抬起后使用此字段还原)
  //背景
  public boolean openBg; //开启背景
  public RectF bgRect; //背景矩形范围
  public Paint bgPaint;
  //背景资源(优先级1)
  public int bgResourceId; //, originalBgColor = Color.TRANSPARENT; //背景资源
  public Bitmap bgBitmap, originalBgBitmap; //背景图
  //背景色(优先级3)
  public int bgColor = Color.TRANSPARENT, originalBgColor = Color.TRANSPARENT; //背景色 + 原始背景色(按下后改变背景色,抬起后使用此字段还原)
  public int bgColorAlpha = DyViewState.BG_COLOR_ALPHA, originalBgColorAlpha = DyViewState.BG_COLOR_ALPHA; //背景色透明度 + 原始背景色透明度(按下后改变背景透明度,抬起后使用此字段还原)
  //背景色 - 渐变(优先级2)
  public int bgColorGD = DyViewState.COLOR_GD.HORIZONTAL.v(); //背景色渐变方向
  public int bgColorStart = Color.TRANSPARENT, originalBgColorStart = Color.TRANSPARENT;
  public int bgColorEnd = Color.TRANSPARENT, originalBgColorEnd = Color.TRANSPARENT;
  //文字
  public boolean openText;
  public RectF textRect; //文字矩形范围
  public Paint textPaint;
  public CharSequence text = "";
  public boolean isFixedText = Boolean.FALSE; //固定文字(如：进度条在文字后面动态增加百分比)
  public int textColor; //文字颜色
  public float textSize; //文字字号
  public boolean textBold; //文字加粗
  public boolean textStrikeThru; //文字删除线
  public boolean textUnderline; //文字下划线
  public float textSpacing; //文字间距 - 横向
  public int textLocation = DyViewState.TEXT_LOCATION.CENTER.v(); //文字位置
  //文字 - 渐变(优先级高)
  public int textColorGD = DyViewState.COLOR_GD.HORIZONTAL.v(); //背景色渐变方向
  public int textColorStart = Color.TRANSPARENT;
  public int textColorEnd = Color.TRANSPARENT;

  //边框
  public boolean openBorder; //开启边框
  public RectF borderRect; //边框矩形范围
  public Paint borderPaint;
  public float borderWidth = DyViewState.BORDER_WIDTH; //边框宽度
  public float borderWidthHalf; //边框宽度50%
  public int borderColor = DyViewState.BORDER_COLOR; //边框颜色
  public float borderRadius; //边框半径
  // 边框 - 渐变(优先级高)
  public int borderColorGD = DyViewState.COLOR_GD.HORIZONTAL.v(); //背景色渐变方向
  public int borderColorStart = Color.TRANSPARENT;
  public int borderColorEnd = Color.TRANSPARENT;

  //按下效果
  public boolean openDown; //开启按下效果
  public Paint downPaint;
  public int downStyle; //按下类型
  public int downColor = Color.TRANSPARENT; //按下颜色
  public int downColorAlpha = -1; //按下透明度
  @FloatRange(from = 0.5, to = 1.5) //范围：50%-150%
  public float downResizeRatio = 1; //重置size比例(<1:view缩小, >1:view放大)(例如0.9 = view高宽*90%)
  public boolean openSelected; //开启选中功能, true: 点击抬起后不会恢复为原有样式, false: 恢复原有样式
  public boolean selected; //选中状态, true: 样式为ACTION_DOWN, false: 样式为ACTION_UP，openSelected=true时只能手动将此变量设置为false
  public int radioGroupId; //单选组ID  , null: 点击任意开启选中的view后其他view样式不会恢复到未选中状态, not null: 根据ID查找到布局view,依次向内寻找与ID相等的view恢复到未选中状态
  //按下效果 - 渐变(优先级高)
  public int downColorGD = DyViewState.COLOR_GD.HORIZONTAL.v();
  public int downColorStart = Color.TRANSPARENT;
  public int downColorEnd = Color.TRANSPARENT;

  //触摸状态
  public int touchAction = MotionEvent.INVALID_POINTER_ID;

  public DyView(View view, Context context, AttributeSet attrs) {
    //Color.parseColor("#303F9F")
    this.view = view;
    this.context = context;


    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_view);
      //形状
      resizeView = typedArray.getBoolean(R.styleable.dy_view_dy_resize_view, true);
      shape = typedArray.getInt(R.styleable.dy_view_dy_shape, DyViewState.SHAPE_TYPE.RECTANGLE.v()); //view形状
      filletRadius = typedArray.getDimension(R.styleable.dy_view_dy_fillet_radius, 0); //圆角半径
      filletDirection = typedArray.getInt(R.styleable.dy_view_dy_fillet_direction, 0); //圆角方向
      //阴影
      shadowSize = originalShadowSize = typedArray.getDimension(R.styleable.dy_view_dy_shadow_size, DyViewState.SHADOW_SIZE);
      shadowColor = originalShadowColor = typedArray.getColor(R.styleable.dy_view_dy_shadow_color, Color.TRANSPARENT);
      shadowAlpha = originalShadowAlpha = typedArray.getInteger(R.styleable.dy_view_dy_shadow_alpha, DyViewState.SHADOW_ALPHA);

      //背景图
      bgResourceId = typedArray.getResourceId(R.styleable.dy_view_dy_bg_resource, 0); //进度条图片资源
      //背景色
      bgColor = originalBgColor = typedArray.getColor(R.styleable.dy_view_dy_bg_color, Color.TRANSPARENT); //背景颜色
      bgColorAlpha = originalBgColorAlpha = typedArray.getInteger(R.styleable.dy_view_dy_bg_color_alpha, DyViewState.BG_COLOR_ALPHA);
      //背景色 - 渐变(优先级高)
      bgColorGD = typedArray.getInteger(R.styleable.dy_view_dy_bg_color_gd, DyViewState.COLOR_GD.HORIZONTAL.v());
      bgColorStart = originalBgColorStart = typedArray.getColor(R.styleable.dy_view_dy_bg_color_start, Color.TRANSPARENT);
      bgColorEnd = originalBgColorEnd = typedArray.getColor(R.styleable.dy_view_dy_bg_color_end, Color.TRANSPARENT);


      //文字
      text = typedArray.getString(R.styleable.dy_view_dy_text); //文字
      if (StringHandler.isEmpty(text)) text = "";
      isFixedText = typedArray.getBoolean(R.styleable.dy_view_dy_fixed_text, Boolean.FALSE); //固定文字
      textColor = typedArray.getColor(R.styleable.dy_view_dy_text_color, ContextCompat.getColor(context, R.color.text)); //文字颜色
      textSize = typedArray.getDimension(R.styleable.dy_view_dy_text_size, 0); //文字字号
      textBold = typedArray.getBoolean(R.styleable.dy_view_dy_text_bold, false); //文字加粗
      textStrikeThru = typedArray.getBoolean(R.styleable.dy_view_dy_text_strike_thru, false); //文字删除线
      textUnderline = typedArray.getBoolean(R.styleable.dy_view_dy_text_underline, false); //文字下划线
      textSpacing = typedArray.getDimension(R.styleable.dy_view_dy_text_spacing, 0); //文字间距 - 横向
      textLocation = typedArray.getInteger(R.styleable.dy_view_dy_text_location, DyViewState.TEXT_LOCATION.CENTER.v()); //文字位置
      //文字 - 渐变(优先级高)
      textColorGD = typedArray.getInteger(R.styleable.dy_view_dy_text_color_gd, DyViewState.COLOR_GD.HORIZONTAL.v());
      textColorStart = typedArray.getColor(R.styleable.dy_view_dy_text_color_start, Color.TRANSPARENT);
      textColorEnd = typedArray.getColor(R.styleable.dy_view_dy_text_color_end, Color.TRANSPARENT);

      //边框
      borderWidth = typedArray.getDimension(R.styleable.dy_view_dy_border_width, DyViewState.BORDER_WIDTH);

      borderColor = typedArray.getColor(R.styleable.dy_view_dy_border_color, DyViewState.BORDER_COLOR);
      //边框 - 渐变(优先级高)
      borderColorGD = typedArray.getInteger(R.styleable.dy_view_dy_border_color_gd, DyViewState.COLOR_GD.HORIZONTAL.v());
      borderColorStart = typedArray.getColor(R.styleable.dy_view_dy_border_color_start, Color.TRANSPARENT);
      borderColorEnd = typedArray.getColor(R.styleable.dy_view_dy_border_color_end, Color.TRANSPARENT);

      //按下事件
      downStyle = typedArray.getInt(R.styleable.dy_view_dy_down_style, 0);
      downColor = typedArray.getColor(R.styleable.dy_view_dy_down_color, Color.TRANSPARENT);
      downColorAlpha = typedArray.getInteger(R.styleable.dy_view_dy_down_color_alpha, -1);
      downResizeRatio = typedArray.getFloat(R.styleable.dy_view_dy_down_resize_ratio, 1);
      openSelected = typedArray.getBoolean(R.styleable.dy_view_dy_open_selected, Boolean.FALSE);
      selected = typedArray.getBoolean(R.styleable.dy_view_dy_selected, Boolean.FALSE);
      radioGroupId = typedArray.getInteger(R.styleable.dy_view_dy_radio_group_id, 0);
      //按下效果 - 渐变(优先级高)
      downColorGD = typedArray.getInteger(R.styleable.dy_view_dy_down_color_gd, DyViewState.COLOR_GD.HORIZONTAL.v());
      downColorStart = typedArray.getColor(R.styleable.dy_view_dy_down_color_start, Color.TRANSPARENT);
      downColorEnd = typedArray.getColor(R.styleable.dy_view_dy_down_color_end, Color.TRANSPARENT);
      typedArray.recycle();
    } else {
    }

    if (bgResourceId > 0) {
      originalBgBitmap = drawableToBitmap(context.getResources().getDrawable(bgResourceId));
      //BitmapFactory.decodeResource(view.getResources(), bgResourceId);
    }

    if (borderWidth > 0) {
      borderWidthHalf = Num.mForF(borderWidth, 0.5f);
    } else {
      borderWidthHalf = borderWidth = 0;
    }
    view.setClickable(true);
  }

  public static Bitmap drawableToBitmap(Drawable drawable) {
    // 取 drawable 的长宽
    int w = drawable.getIntrinsicWidth();
    int h = drawable.getIntrinsicHeight();

    // 取 drawable 的颜色格式
    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
      : Bitmap.Config.RGB_565;
    // 建立对应 bitmap
    Bitmap bitmap = Bitmap.createBitmap(w, h, config);
    // 建立对应 bitmap 的画布
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, w, h);
    // 把 drawable 内容画到画布中
    drawable.draw(canvas);
    return bitmap;
  }

  /**
   * 初始化view
   * 如果单独初始化，顺序必须与此方法一致
   */
  public void initView() {
    initSystemParam();
    initShape();
    initShadow();
    initBg();
    initText();
    initBorder();
  }

  /**
   * 绘制view
   * 如果单独绘制，没有重新绘制view需求，可以不调用clearCanvas
   *
   * @param canvas
   */
  public void draw(Canvas canvas) {
    clearCanvas(canvas);
    initView();
    drawShadow(canvas);
    drawBg(canvas);
    drawBorder(canvas);
    drawDown(canvas);
  }


  public void drawPoint(Canvas canvas) {
    //标点
    Paint paint = new Paint();
    paint.setColor(Color.RED);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(10);
    canvas.drawLine(viewRect.centerX(), viewRect.top, viewRect.centerX(), viewRect.bottom, paint);
    canvas.drawLine(viewRect.left, viewRect.centerY(), viewRect.right, viewRect.centerY(), paint);
  }

  /**
   * 1.初始化系统参数并将不需要的系统参数清空
   * 如：android:background = color不使用系统参数，通过app:dy_bg_color = color设置
   * android:***='abc'
   */
  public void initSystemParam() {
    Drawable background = view.getBackground();
    if (null != background) {
      //background包括color和Drawable,这里分开取值
      if (background instanceof ColorDrawable) { //不使用系统参数，如果设置则置空
        /*ColorDrawable colorDrawable = (ColorDrawable) background;
        bgColor = colorDrawable.getColor();*/
        view.setBackgroundColor(Color.TRANSPARENT);
      } else if (background instanceof Drawable) {

      }
    }
  }

  /**
   * 2.初始化形状
   * 根据按下效果生成范围，角度等
   */
  public void initShape() {
    /**
     * 初始化按下效果
     * 根据 downStyle 来判断调用顺序:
     * 🌟: BACKGROUND, RESIZE效果 在对应的模块中生成(默认使用drawBg()设置背景，viewRect设置大小)
     * 🌟: OVERRIDE在drawDown中生成
     */
    if (selected) { //按下 touchAction == MotionEvent.ACTION_DOWN
      if (downStyle == DyViewState.DOWN_TYPE.OVERRIDE.v() && openDown) {
        //初始化
        if (null == downPaint) downPaint = new Paint();
        LinearGradient downColorGradient = initGradient(viewRect, downColorGD, downColorStart, downColorEnd); //生成渐变
        downPaint.setAntiAlias(true); //防锯齿;
        downPaint.setDither(true); //防抖动;
        downPaint.setStyle(Paint.Style.FILL);
        if (null != downColorGradient) {
          downPaint.setShader(downColorGradient);
        } else {
          downPaint.setShader(null);
          downPaint.setColor(downColor);
        }
        downPaint.setAlpha(downColorAlpha == -1 ? DyViewState.TRANSLUCENT : downColorAlpha);
      } else if (downStyle == DyViewState.DOWN_TYPE.BACKGROUND.v()) {
        shadowColor = bgColor = downColor;
        bgColorStart = downColorStart;
        bgColorEnd = downColorEnd;
        if (downColorEnd != Color.TRANSPARENT) {
          shadowColor = downColorEnd;
        }

        if (downColorAlpha >= 0) {
          shadowAlpha = bgColorAlpha = downColorAlpha;
        }
      } else if (downStyle == DyViewState.DOWN_TYPE.RESIZE.v()) {
        shadowSize = Num.mForInt(shadowSize, downResizeRatio);
        width = Num.mForF(width, downResizeRatio);
        maxWidth = Num.mForF(maxWidth, downResizeRatio);
        height = Num.mForF(height, downResizeRatio);
        maxHeight = Num.mForF(maxHeight, downResizeRatio);

        l = Num.mForF(originalMaxWidth - maxWidth, 0.5f);
        t = Num.mForF(originalMaxHeight - maxHeight, 0.5f);
        //缩小后的width和height不是按照居中位置算出来的， 例如:原始值width=100, 缩小后width=90, l=5, width最终值应是+=l,
        width += l;
        height += t;
        maxWidth += l;
        maxHeight += t;
        /*if (null != params) {
          if (mt > 0) {
            System.out.println(mt);
          }
          mt = (int) (mt + mt / 0.1);
          mb = (int) (mb + mb / 0.1);
          ml = originalMl;
          mr = originalMr;
        }*/
      }
    } else { //抬起 if (touchAction == MotionEvent.ACTION_UP)
      if (downStyle == DyViewState.DOWN_TYPE.BACKGROUND.v()) {
        shadowColor = originalShadowColor;
        bgColor = originalBgColor;
        bgColorStart = originalBgColorStart;
        bgColorEnd = originalBgColorEnd;
        shadowAlpha = originalShadowAlpha;
        bgColorAlpha = originalBgColorAlpha;
      } else if (downStyle == DyViewState.DOWN_TYPE.RESIZE.v()) {
        shadowSize = originalShadowSize;
        width = originalWidth;
        height = originalHeight;
        maxWidth = originalMaxWidth;
        maxHeight = originalMaxHeight;
        l = t = 0;

        /*if (null != params) {
          mt = (int) (mt - mt / 0.1);
          mb = (int) (mb - mb / 0.1);
          ml = originalMl;
          mr = originalMr;
        }*/
      }
    }

    //if (null != params) params.setMargins(ml, mt, mr, mb);
    //原始view范围
    viewRect.set(l, t, maxWidth, maxHeight);
    xCenter = viewRect.centerX();//.width() / 2f;
    yCenter = viewRect.centerY();//.height() / 2;

    if (shape == DyViewState.SHAPE_TYPE.CIRCULAR.v()) {
      //filletRadius = Math.min(width / 2f, height / 2f); //圆形半径
      filletRadius = Math.min(xCenter, yCenter); //圆形半径
    } else if (shape == DyViewState.SHAPE_TYPE.RECTANGLE.v()) {
      //RECTANGLE时将圆角半径设置为0，按照圆角的方式进行绘制
      shape = DyViewState.SHAPE_TYPE.FILLET.v();
      filletRadius = 0;
    }

    if (viewRect.width() < viewRect.height()) {
      viewSizeMin = viewRect.width();
      viewDirectionMin = 0;
    } else {
      viewSizeMin = viewRect.height();
      viewDirectionMin = 1;
    }
    imageZoomSize = Num.mForF(filletRadius, 0.8f);// + Num.mForF(borderWidth, 2);
    initOpenDraw(); //在此处验证开启绘制的功能，防止set赋值后没有重新验证
  }

  /**
   * 3.初始化阴影
   */
  public void initShadow() {
    if (openShadow) {
      //初始化
      if (null == shadowRect) shadowRect = new RectF();
      if (null == shadowPaint) shadowPaint = new Paint();
      shadowRect.set(viewRect.left, viewRect.top + shadowSize, viewRect.right, viewRect.bottom); //阴影范围
      insetBorder(shadowRect); //缩小边框范围
      shadowPaint.setAntiAlias(true); //防锯齿;
      shadowPaint.setDither(true); //防抖动;
      shadowPaint.setStyle(Paint.Style.FILL);
      shadowPaint.setColor(shadowColor);
      shadowPaint.setAlpha(shadowAlpha);
    }
  }

  /**
   * 4.初始化背景
   */
  public void initBg() {
    initBg(viewRect);
  }

  public void initBg(RectF viewRect) {
    if (openBg) {
      //初始化
      if (null == bgRect) bgRect = new RectF();
      if (null == bgPaint) bgPaint = new Paint();
      //bgRect.set(0, 0, width, height);
      bgRect.set(viewRect.left, viewRect.top, width, height);
      //缩小后的背景色范围 会覆盖一半的边框宽度(防止圆角出现空白或超出边框，因为相同角度，高宽不同的范围 会导致圆角也不同) 所以要在边框前绘制
      insetBorder(bgRect);

      bgPaint.setAntiAlias(true); //防锯齿;
      bgPaint.setDither(true); //防抖动;
      if (null != originalBgBitmap) {
        bgBitmap = ImageHandler.scaleSelfBitmap(originalBgBitmap, bgRect.width(), bgRect.height()); //缩放图片
      } else {
        //生成渐变
        LinearGradient bgColorGradient = initGradient(bgRect, bgColorGD, bgColorStart, bgColorEnd); //生成渐变
        bgPaint.setStyle(Paint.Style.FILL);
        if (null != bgColorGradient) {
          bgPaint.setShader(bgColorGradient);
        } else {
          bgPaint.setShader(null);
          bgPaint.setColor(bgColor);
        }
        bgPaint.setAlpha(bgColorAlpha);
      }

    }
  }

  /**
   * 5.初始化文字相关
   */
  public void initText() {
    if (openText) {
      if (null == textRect) textRect = new RectF();
      if (null == textPaint) textPaint = new Paint();
      textRect.set(viewRect.left, viewRect.top, width, height);
      insetBorder(textRect);
      LinearGradient textColorGradient = initGradient(textRect, textColorGD, textColorStart, textColorEnd); //生成渐变
      textPaint.setAntiAlias(true); //防锯齿;
      textPaint.setDither(true); //防抖动;
      if (null != textColorGradient) {
        textPaint.setShader(textColorGradient);
      } else {
        textPaint.setShader(null);
        textPaint.setColor(textColor);
      }
      textPaint.setTextSize(textSize);
      textPaint.setFakeBoldText(textBold);
      textPaint.setStrikeThruText(textStrikeThru);
      textPaint.setUnderlineText(textUnderline);
    }
  }

  /**
   * 6.初始化边框
   */
  public void initBorder() {
    initBorder(viewRect);
  }

  public void initBorder(RectF viewRect) {
    if (openBorder) {
      //初始化
      if (null == borderRect) borderRect = new RectF();
      if (null == borderPaint) borderPaint = new Paint();
      borderRect.set(viewRect.left, viewRect.top, maxWidth, maxHeight);
      insetBorder(borderRect); //缩小边框范围（绘制边框时，是在边框宽度中心点开始绘制的，所以要保证rect位置足够）
      if (shape == DyViewState.SHAPE_TYPE.CIRCULAR.v()) { //获取边框角度
        //圆形边框时，使用范围中心点 并缩小边框宽度的一半
        borderRadius = Math.min(borderRect.centerX(), borderRect.centerY()); //边框圆形半径
      } else {
        borderRadius = filletRadius;
      }
      LinearGradient borderColorGradient = initGradient(borderRect, borderColorGD, borderColorStart, borderColorEnd); //生成渐变
      borderPaint.setAntiAlias(true); //防锯齿;
      borderPaint.setDither(true); //防抖动;
      borderPaint.setStyle(Paint.Style.STROKE);
      if (null != borderColorGradient) {
        borderPaint.setShader(borderColorGradient);
      } else {
        borderPaint.setShader(null);
        borderPaint.setColor(borderColor);
      }
      borderPaint.setStrokeWidth(borderWidth);
    }
  }


  /**
   * 1.清空画布
   * 防止调用invalidate()重新绘制时，上一次绘制的内容残留
   *
   * @param canvas
   */
  public void clearCanvas(Canvas canvas) {
    //view.setLayerType(view.LAYER_TYPE_HARDWARE, null);
    //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
  }

  /**
   * 2.绘制阴影
   *
   * @param canvas
   */
  public void drawShadow(Canvas canvas) {
    if (openShadow) {
      //绘制
      if (shape == DyViewState.SHAPE_TYPE.CIRCULAR.v()) { //圆形
        canvas.drawArc(shadowRect, 0, 180, false, shadowPaint); //阴影(以范围中心点开始绘制0,180的弧形),下半圆
        //在原始高度的中间 至 中间+阴影宽度位置绘制，做为角度透明处的填充
        RectF centerShadowRect = new RectF(viewRect.left, height / 2f, viewRect.right, height / 2f + shadowSize);
        canvas.drawRect(centerShadowRect, shadowPaint);
      } else if (shape == DyViewState.SHAPE_TYPE.FILLET.v()) {
        //path=null说明不是圆角或没有设置圆角方向
        Path path = initFilletForPath(shadowRect, filletRadius, Boolean.TRUE); //生成圆角PATH
        //圆角背景色
        if (null != path) {
          canvas.drawPath(path, shadowPaint);
        } else {
          canvas.drawRoundRect(shadowRect, filletRadius, filletRadius, shadowPaint); //阴影(在范围2/1处绘制圆角矩形)
        }
      }
    }
  }

  /**
   * 3.绘制背景
   *
   * @param canvas
   */
  public void drawBg(Canvas canvas) {
    if (openBg) {
      if (null != bgBitmap) {
        if (shape == DyViewState.SHAPE_TYPE.CIRCULAR.v()) {
          canvas.drawBitmap(ImageHandler.initOvalBitmap(bgBitmap), bgRect.left, bgRect.top, bgPaint);
        } else {
          canvas.drawBitmap(ImageHandler.initRoundedCornerBitmap(bgBitmap, filletRadius), bgRect.left, bgRect.top, bgPaint);
        }

      } else {
        //path=null说明不是圆角或没有设置圆角方向
        Path path = initFilletForPath(bgRect, filletRadius, Boolean.TRUE); //生成圆角PATH
        //圆角背景色
        if (null != path) {
          canvas.drawPath(path, bgPaint);
        } else {
          canvas.drawRoundRect(bgRect, filletRadius, filletRadius, bgPaint);
        }
      }
    }
  }

  public void drawBg(Canvas canvas, Path path) {
    if (openBg && null != path) {
      canvas.drawPath(path, bgPaint);
    }
  }

  /**
   * 4.绘制文字相关
   * 1).文字默认绘制在中间位置
   * 2).部分view的文字位置需要在子类中计算,如:CENTER_TOP_HALF在进度条view中计算xy
   * 3).绘制时可设置text,不会改变view原有的text
   * drawText参考: https://blog.csdn.net/industriously/article/details/51009274
   *
   * @param canvas
   */
  public void drawText(Canvas canvas) {
    drawText(canvas, text.toString());
  }

  public void drawText(Canvas canvas, float x, float y) {
    drawText(canvas, x, y, text.toString());
  }

  //计算文字位置
  public void drawText(Canvas canvas, String text) {
    if (openText) {
      float textWidth = DensityHandler.getWordWidth(text, textSize);
      float textHeight = DensityHandler.getFontHeight(text, textSize);
      float textOccupyHeight = DensityHandler.getFontHeightForOccupy(textSize);

      float textWidthHalf = Num.mForF(textWidth, 0.5f);
      float textHeightHalf = Num.mForF(textHeight, 0.5f);
      float useSize = borderWidth + Num.mForF(filletRadius, 0.5f);
      float textExcessHeightHalf = textHeight > textOccupyHeight ? Num.mForF(DensityHandler.getFontHeightForExcess(textSize), 0.5f) : 0;
      Float x = null, y = null;

      //测试 - 定位划线
      /*Paint paint = new Paint();
      paint.setColor(Color.RED);
      paint.setStyle(Paint.Style.STROKE);
      paint.setStrokeWidth(3);
      canvas.drawLine(textRect.centerX(), textRect.top, textRect.centerX(), textRect.bottom, paint);
      canvas.drawLine(textRect.left + useSize, textRect.top, textRect.left + useSize, textRect.bottom, paint);
      canvas.drawLine(textRect.left, textRect.centerY(), textRect.right, textRect.centerY(), paint);*/

      if ((textLocation & DyViewState.TEXT_LOCATION.CENTER.v()) != 0) {
        //textPaint.setTextAlign(Paint.Align.CENTER);
        x = textRect.centerX() - textWidthHalf;
        y = textRect.centerY() + textHeightHalf - textExcessHeightHalf;

        /*paint.setColor(Color.BLACK);
        canvas.drawLine(x, y - textHeight, x + textWidth, y - textHeight, paint);
        paint.setColor(Color.BLUE);
        canvas.drawLine(x, y, x + textWidth, y, paint);*/
      }

      if ((textLocation & DyViewState.TEXT_LOCATION.CENTER_HORIZONTAL.v()) != 0) {
        x = textRect.centerX() - textWidthHalf;
      }

      if ((textLocation & DyViewState.TEXT_LOCATION.CENTER_VERTICAL.v()) != 0) {
        y = textRect.centerY() + textHeightHalf - textExcessHeightHalf;
      }

      if ((textLocation & DyViewState.TEXT_LOCATION.TOP.v()) != 0) {
        y = textHeight + borderWidth;
      }
      if ((textLocation & DyViewState.TEXT_LOCATION.BOTTOM.v()) != 0) {
        y = textRect.bottom - borderWidth - textPaint.descent();
      }
      if ((textLocation & DyViewState.TEXT_LOCATION.LEFT.v()) != 0) {
        x = textRect.left + useSize;
      }
      if ((textLocation & DyViewState.TEXT_LOCATION.RIGHT.v()) != 0) {
        x = textRect.right - textWidth - useSize;
      }
      if ((textLocation & DyViewState.TEXT_LOCATION.START.v()) != 0) {
        x = textRect.left + useSize;
        y = textHeight;
      }

      if (null == x) x = textRect.left + useSize;
      if (null == y) y = textRect.top + textHeight + borderWidth;
      drawText(canvas, x, y, text);
    }
  }

  public void drawText(Canvas canvas, float x, float y, String text) {
    if (openText) {
      if (textSpacing > 0) {
        float fontWidth = DensityHandler.getWordWidth(textSize);
        for (int i = 0; i < text.length(); i++) {
          canvas.drawText(text.charAt(i) + "", x, y, textPaint);
          x += fontWidth + textSpacing;
        }
      } else {
        canvas.drawText(text, x, y, textPaint);
      }
    }
  }

  /**
   * 5.绘制边框
   *
   * @param canvas
   */
  public void drawBorder(Canvas canvas) {
    if (openBorder) {
      //绘制
      if (openShadow && shape == DyViewState.SHAPE_TYPE.CIRCULAR.v()) {
        //圆形, 有阴影时，重置边框大小，圆形边框分为三部分（上半圆，中竖线，下半圆）
        //如果不对阴影大小做处理，会导致边框变成椭圆形，当阴影宽度过大时会小于背景色
        RectF topSemicircle = new RectF(borderRect.left, borderRect.top, borderRect.right, borderRect.bottom - shadowSize); //边框 上半圆
        RectF bottomSemicircle = new RectF(borderRect.left, borderRect.top + shadowSize, borderRect.right, borderRect.bottom); //边框 下半圆
        canvas.drawArc(topSemicircle, 180, 180, false, borderPaint); //边框 上半圆
        canvas.drawArc(bottomSemicircle, 0, 180, false, borderPaint); //边框 下半圆
        //竖线, //0,1 上，2,3左，4,5右，6,7下，将没有边框部分加上竖线
        float[] borderPts = {borderWidthHalf, topSemicircle.centerY(), borderWidthHalf, bottomSemicircle.centerY(), topSemicircle.right, topSemicircle.centerY(), topSemicircle.right, bottomSemicircle.centerY()};
        canvas.drawLines(borderPts, borderPaint);
        return;
      }

      //path=null说明不是圆角或没有设置圆角方向
      Path path = initFilletForPath(borderRect, borderRadius, Boolean.FALSE); //生成圆角PATH
      //圆角背景色
      if (null != path) {
        //borderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, borderPaint);
      } else {
        canvas.drawRoundRect(borderRect, borderRadius, borderRadius, borderPaint); //圆角边框
      }
    }
  }

  public void drawBorder(Canvas canvas, Path path) {
    if (openBorder && null != path) {
      canvas.drawPath(path, borderPaint);
    }
  }

  /**
   * 6.绘制按下效果
   *
   * @param canvas
   */
  public void drawDown(Canvas canvas) {
    if (selected && downStyle == DyViewState.DOWN_TYPE.OVERRIDE.v() && openDown) {
      //绘制
      if (shape == DyViewState.SHAPE_TYPE.CIRCULAR.v()) { //圆形
        canvas.drawCircle(xCenter, yCenter, filletRadius, downPaint);
      } else if (shape == DyViewState.SHAPE_TYPE.FILLET.v()) { //圆角
        //path=null说明不是圆角或没有设置圆角方向
        Path path = initFilletForPath(viewRect, filletRadius, Boolean.TRUE); //生成圆角PATH
        //圆角背景色
        if (null != path) {
          canvas.drawPath(path, downPaint);
        } else {
          canvas.drawRoundRect(viewRect, filletRadius, filletRadius, downPaint);
        }
      }
    }
  }

  //--------------------工具方法--------------------

  /**
   * 设置开启绘制状态
   * 修改属性后调用此方法重新验证是否开启绘制功能
   */
  public void initOpenDraw() {
    text = null == text ? "" : text;
    openBg = bgColor != Color.TRANSPARENT || bgColorStart != Color.TRANSPARENT || bgColorEnd != Color.TRANSPARENT || null != originalBgBitmap;
    openText = StringHandler.isNotEmpty(text.toString());
    openBorder = borderWidth > 0;
    openShadow = shadowSize > 0;
    openDown = downColor != Color.TRANSPARENT || downColorStart != Color.TRANSPARENT || downColorEnd != Color.TRANSPARENT;
  }

  /**
   * 生成渐变
   *
   * @param rect
   * @param direction
   * @param colorStart
   * @param colorEnd
   * @return
   */
  public LinearGradient initGradient(RectF rect, int direction, int colorStart, int colorEnd) {
    boolean isGradient = colorStart != Color.TRANSPARENT || colorEnd != Color.TRANSPARENT;
    if (isGradient) {
      float x1 = 0, y1 = 0;
      if (DyViewState.COLOR_GD.VERTICAL.v().equals(direction)) {
        y1 = rect.bottom;
      } else {
        x1 = rect.right;
      }
      return new LinearGradient(0, 0, x1, y1, new int[]{colorStart, colorEnd}, null, Shader.TileMode.CLAMP);
    }
    return null;
  }


  /**
   * 绘制圆角
   * 1.shape = FILLET 并且设置方向后使用path画出来一个形状
   * 2.默认时四角都为圆角，只有设置后才会根据对应值进行绘制
   *
   * @param rectF
   * @param radius
   * @return
   */
  private Path initFilletForPath(RectF rectF, float radius, boolean isBorder) {
    rectF = new RectF(rectF);
    if (filletDirection == 0 || shape != DyViewState.SHAPE_TYPE.FILLET.v()) {
      return null;
    }
    radius += 1;
    float leftTopRadius = radius, leftBottomRadius = radius, rightTopRadius = radius, rightBottomRadius = radius;
    Path path = new Path();
    //拉出圆角
    if ((filletDirection & DyViewState.FILLET_DIRECTION.FILLET_LEFT_TOP.v()) != 0) {
      //左上圆角
      path.moveTo(rectF.left, rectF.top + leftTopRadius);
      path.quadTo(rectF.left, rectF.top, rectF.left + leftTopRadius, rectF.top);
    } else {
      leftTopRadius = 0;
    }

    if ((filletDirection & DyViewState.FILLET_DIRECTION.FILLET_LEFT_BOTTOM.v()) != 0) {
      //左下圆角
      path.moveTo(rectF.left + leftBottomRadius, rectF.bottom);
      path.quadTo(rectF.left, rectF.bottom, rectF.left, rectF.bottom - leftBottomRadius);
    } else {
      leftBottomRadius = 0;
    }

    if ((filletDirection & DyViewState.FILLET_DIRECTION.FILLET_RIGHT_TOP.v()) != 0) {
      //右上圆角
      path.moveTo(rectF.right - rightTopRadius, rectF.top);
      path.quadTo(rectF.right, rectF.top, rectF.right, rectF.top + rightTopRadius);
    } else {
      rightTopRadius = 0;
    }

    if ((filletDirection & DyViewState.FILLET_DIRECTION.FILLET_RIGHT_BOTTOM.v()) != 0) {
      //右下圆角
      path.moveTo(rectF.right, rectF.bottom - rightBottomRadius);
      path.quadTo(rectF.right, rectF.bottom, rectF.right - rightBottomRadius, rectF.bottom);
    } else {
      rightBottomRadius = 0;
    }

    if (isBorder) {
      /**
       * 形成封闭图形
       *
       *         1 ----- 2
       *  -------------------------
       *  |  1-2             2-1  |
       *  |1-1                 2-2|
       * 1|                       |2
       * ||                       ||
       * ||                       ||
       * 4|                       |3
       *  |4-2                 3-1|
       *  |  4-1             3-2  |
       *  -------------------------
       *         4 ----- 3
       */
      //1
      path.moveTo(rectF.left, rectF.top + leftTopRadius);
      path.lineTo(rectF.left + leftTopRadius, rectF.top);
      //2
      path.lineTo(rectF.right - rightTopRadius, rectF.top);
      path.lineTo(rectF.right, rectF.top + rightTopRadius);
      //3
      path.lineTo(rectF.right, rectF.bottom - rightBottomRadius);
      path.lineTo(rectF.right - rightBottomRadius, rectF.bottom);
      //4
      path.lineTo(rectF.left + leftBottomRadius, rectF.bottom);
      path.lineTo(rectF.left, rectF.bottom - leftBottomRadius);
      path.close();
    } else {
      //绘制图形 - 顺时针绘制, 边框使用不是封闭图形,只有线条
      //上边
      path.moveTo(rectF.left + leftTopRadius, rectF.top);
      path.lineTo(rectF.right - rightTopRadius, rectF.top);
      //右边
      path.moveTo(rectF.right, rectF.top + rightTopRadius);
      path.lineTo(rectF.right, rectF.bottom - rightBottomRadius);
      //下边
      path.moveTo(rectF.right - rightBottomRadius, rectF.bottom);
      path.lineTo(rectF.left + leftBottomRadius, rectF.bottom);
      //左边
      path.moveTo(rectF.left, rectF.bottom - leftBottomRadius);
      path.lineTo(rectF.left, rectF.top + leftTopRadius);
    }

    return path;
  }

  /**
   * 根据变宽宽度缩小范围
   * 缩小使用边框厚度的50%，绘制时会在边框厚度50%处开始绘制
   * 如：边框厚度为6，会在3左右两侧开始绘制
   *
   * @param rectF
   */
  public void insetBorder(RectF rectF) {
    if (borderWidth > 0) {
      rectF.inset(borderWidthHalf, borderWidthHalf);
    }
  }

  public void insetBorderY(RectF rectF) {
    if (borderWidth > 0) {
      rectF.inset(0, borderWidthHalf);
    }
  }

  /**
   * 单选选中切换
   * 1.openSelected=true时候按下view后会触发此方法
   * 2.找到ID为radioGroupId的父view, 将父view下与radioGroupId相等的view全都变为未选中
   * 3.允许为不同类型的view在一个父view中进行选中切换，全部都要实现getDyView方法
   */
  public void radio() {
    try {
      if (radioGroupId == 0) return;
      ViewGroup parentViewGroup = null;

      //循环向上寻找 id=radioGroupId的布局
      ViewParent viewParent = view.getParent();
      while (null != viewParent) {
        if (viewParent instanceof ViewGroup) {
          if (null != ((ViewGroup) viewParent).findViewById(radioGroupId)) {
            parentViewGroup = (ViewGroup) viewParent;
            break;
          }
        }
        viewParent = viewParent.getParent();
      }
      if (null == parentViewGroup) {
        Logger.e(TAG, "radioGroupId不存在");
        return;
      }
      noSelected(parentViewGroup);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 对viewGroup下的所有view进行选中恢复
   * view类型为ViewGroup时候进行递归，将所有view都验证一变
   *
   * @param viewGroup
   */
  @SneakyThrows
  private void noSelected(ViewGroup viewGroup) {
    int viewCount = viewGroup.getChildCount();
    for (int i = 0; i < viewCount; i++) {
      View view = viewGroup.getChildAt(i);
      if (view instanceof ViewGroup) {
        noSelected((ViewGroup) view);
      }

      Method method = view.getClass().getMethod("getDyView");
      if (null == method) continue; //不存在getDyView方法
      DyView dyView = (DyView) method.invoke(view);
      if (!dyView.openSelected || dyView.radioGroupId != radioGroupId) { //未开启选中功能 或 view的id与当前点击的view不符
        continue;
      }
      if (this == dyView) {
        dyView.setSelected(Boolean.TRUE);
      } else {
        dyView.setSelected(Boolean.FALSE);
      }
    }
  }

  //--------------------SET--------------------

  /**
   * 是否动态调整view大小
   */
  public DyView setResizeView(boolean resizeView) {
    if (this.resizeView != resizeView) {
      this.resizeView = resizeView;
      view.invalidate();
    }
    return this;
  }

  public DyView setShape(int shape) {
    if (this.shape != shape) {
      this.shape = shape;
      view.invalidate();
    }
    return this;
  }

  public DyView setFilletRadius(float filletRadius) {
    if (this.filletRadius != filletRadius) {
      this.filletRadius = filletRadius;
      view.invalidate();
    }
    return this;
  }

  public DyView setFilletDirection(int filletDirection) {
    if (this.filletDirection != filletDirection) {
      this.filletDirection = filletDirection;
      view.invalidate();
    }
    return this;
  }

  /**
   * 阴影
   */
  public DyView setShadowSize(float shadowSize) {
    if (this.shadowSize != shadowSize) {
      this.shadowSize = this.originalShadowSize = shadowSize;
      view.invalidate();
    }
    return this;
  }

  public DyView setShadowColor(int shadowColor) {
    if (this.shadowColor != shadowColor) {
      this.shadowColor = this.originalShadowColor = shadowColor;
      view.invalidate();
    }
    return this;
  }

  public DyView setShadowAlpha(int shadowAlpha) {
    if (this.shadowAlpha != shadowAlpha) {
      this.shadowAlpha = this.originalShadowAlpha = shadowAlpha;
      view.invalidate();
    }
    return this;
  }

  /**
   * 背景色
   */
  public DyView setBgColor(int bgColor) {
    if (this.bgColor != bgColor) {
      this.bgColor = this.originalBgColor = bgColor;
      view.invalidate();
    }
    return this;
  }

  public DyView setBgColorAlpha(int bgColorAlpha) {
    if (this.bgColorAlpha != bgColorAlpha) {
      this.bgColorAlpha = this.originalBgColorAlpha = bgColorAlpha;
      view.invalidate();
    }
    return this;
  }

  /**
   * 文字
   */
  public DyView setText(String text) {
    if (!this.text.equals(text)) {
      this.text = text;
      view.invalidate();
    }
    return this;
  }

  @BindingAdapter(value = {"dy_text"})
  public static void setText(DyBadgeView view, String text) {
    view.getDyView().setText(text);
  }

  @BindingAdapter(value = {"dy_text"})
  public static void setText(DyMultipleView view, String text) {
    view.getDyView().setText(text);
  }

  public DyView setTextColor(int textColor) {
    if (this.textColor != textColor) {
      this.textColor = textColor;
      view.invalidate();
    }
    return this;
  }

  public DyView setTextColorGradient(Integer textColorGD, Integer textColorStart, Integer textColorEnd) {
    if (null == textColorGD) textColorGD = DyViewState.COLOR_GD.HORIZONTAL.v();
    if (null == textColorStart) textColorStart = Color.TRANSPARENT;
    if (null == textColorEnd) textColorEnd = Color.TRANSPARENT;

    if (this.textColorGD != textColorGD || this.textColorStart != textColorStart || this.textColorEnd != textColorEnd) {
      this.textColorGD = textColorGD;
      this.textColorStart = textColorStart;
      this.textColorEnd = textColorEnd;
      view.invalidate();
    }
    return this;
  }

  public DyView setTextSize(float textSize) { //px
    if (this.textSize != textSize) {
      this.textSize = textSize;
      view.invalidate();
    }
    return this;
  }

  public DyView setTextBold(boolean textBold) {
    if (this.textBold != textBold) {
      this.textBold = textBold;
      view.invalidate();
    }
    return this;
  }

  public DyView setTextStrikeThru(boolean textStrikeThru) {
    if (this.textStrikeThru != textStrikeThru) {
      this.textStrikeThru = textStrikeThru;
      view.invalidate();
    }
    return this;
  }

  public DyView setTextUnderline(boolean textUnderline) {
    if (this.textUnderline != textUnderline) {
      this.textUnderline = textUnderline;
      view.invalidate();
    }
    return this;
  }

  public DyView setTextSpacing(float textSpacing) {
    if (this.textSpacing != textSpacing) {
      this.textSpacing = textSpacing;
      view.invalidate();
    }
    return this;
  }

  /**
   * 边框
   */
  public DyView setBorderColor(int borderColor) {
    if (this.borderColor != borderColor) {
      this.borderColor = borderColor;
      view.invalidate();
    }
    return this;
  }

  public DyView setBorderWidth(float borderWidth) {
    if (this.borderWidth != borderWidth) {
      this.borderWidth = borderWidth;
      if (this.borderWidth > 0) {
        borderWidthHalf = Num.mForF(this.borderWidth, 0.5f);
      } else {
        borderWidthHalf = this.borderWidth = 0;
      }
      view.invalidate();
    }
    return this;
  }

  /**
   * 按下
   */
  public DyView setDownStyle(int downStyle) {
    if (this.downStyle != downStyle) {
      this.downStyle = downStyle;
      view.invalidate();
    }
    return this;
  }

  public DyView setDownColor(int downColor) {
    if (this.downColor != downColor) {
      this.downColor = downColor;
      view.invalidate();
    }
    return this;
  }

  public DyView setDownColorAlpha(int downColorAlpha) {
    if (this.downColorAlpha != downColorAlpha) {
      this.downColorAlpha = downColorAlpha;
      view.invalidate();
    }
    return this;
  }

  public DyView setDownResizeRatio(float downResizeRatio) {
    if (this.downResizeRatio != downResizeRatio) {
      this.downResizeRatio = downResizeRatio;
      view.invalidate();
    }
    return this;
  }

  public DyView setOpenSelected(boolean openSelected) {
    if (this.openSelected != openSelected) {
      this.openSelected = openSelected;
      view.invalidate();
    }
    return this;
  }

  public DyView setSelected(boolean selected) {
    if (this.selected != selected) {
      this.selected = selected;
      view.invalidate();
    }
    return this;
  }


  public DyView setRadioGroupId(int radioGroupId) {
    if (this.radioGroupId != radioGroupId) {
      this.radioGroupId = radioGroupId;
      view.invalidate();
    }
    return this;
  }


  public boolean onTouchEvent(MotionEvent event) {
    touchAction = event.getAction();
    switch (touchAction) {
      case MotionEvent.ACTION_DOWN:
        if (openSelected) {
          radio();
        }
        selected = Boolean.TRUE;
        if (downStyle > 0) {
          view.invalidate();
        }
        break;
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        if (openSelected) break;
        selected = Boolean.FALSE;
        if (downStyle > 0) {
          view.invalidate();
        }
        break;
    }
    return true;
  }

  /**
   * 设置view大小
   * <p>
   * 高或宽等于wrap_content时需要根据不同类型的view计算高宽
   * 如果是LinearLayout,RelativeLayout时根据子view计算出高宽
   * 高或宽等于match_parent或指定大小时不需要手动计算getSize就可以获得正确高宽
   *
   * @param widthMeasureSpec
   * @param heightMeasureSpec
   */
  public void onMeasure(Integer widthMeasureSpec, Integer heightMeasureSpec) {
    /*if (view.getParent() instanceof ViewPager || view.getParent() instanceof DyTabPageLayout || view instanceof ViewPager || view instanceof DyTabPageLayout) {
      params = (ViewPager.MarginLayoutParams) view.getLayoutParams();
    } else {
      params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    }
    if (null != params) {
      originalMt = mt = params.topMargin;
      originalMb = mb = params.bottomMargin;
      originalMl = ml = params.leftMargin;
      originalMr = mr = params.rightMargin;
      originalPt = pt = view.getPaddingTop();
      originalPb = pb = view.getPaddingBottom();
      originalPl = pl = view.getPaddingLeft();
      originalPr = pr = view.getPaddingRight();
    }*/
    //float xPadding = pl + pr;
    //float yPadding = pt + pb;
    int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
    int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
    int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
    int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

    if (widthMode == View.MeasureSpec.EXACTLY) {
      width = widthSize;
    } else {
      width = view.getMeasuredWidth(); //获取默认view宽度，此时view.getLayoutParams().width不管是否设置都不会获取到值，只有在布局初始化完成后才会获取到(onLayout)
    }

    if (heightMode == View.MeasureSpec.EXACTLY) {
      height = heightSize;
    } else {
      height = view.getMeasuredHeight();
    }
    setSize(width, height);
    //if(null != params) params.setMargins(ml, mt, mr, mb);
  }

  /**
   * 设置view大小
   * 有些自定义view可能会动态调整大小
   * 如：进度条形状为圆形时，需要将高宽设置为相同的大小，在自定义view中调整完后调用此方法
   * 如果不调用会导致，DyView中绘制的样式与自定义view中绘制的不一致
   *
   * @param width
   * @param height
   */
  public void setSize(float width, float height) {
    setWidth(width);
    setHeight(height);
  }

  public void setWidth(float width) {
    this.width = width;
    maxWidth = width;
    originalWidth = width;
    originalMaxWidth = maxWidth;
  }

  public void setHeight(float height) {
    this.height = height;
    if (resizeView) {
      maxHeight = this.height + shadowSize;
    } else {
      maxHeight = this.height;
      this.height -= shadowSize;
    }
    originalHeight = this.height;
    originalMaxHeight = maxHeight;

  }

  //设置基础布局信息
  public interface DyViewStyle {
    DyView set(DyView v);
  }

}