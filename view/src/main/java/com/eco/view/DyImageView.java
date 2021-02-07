package com.eco.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.ImageHandler;
import com.eco.basics.handler.Num;
import com.eco.view.builder.DyTextViewBuilder;

import lombok.Getter;

/**
 * DyImageView
 * 绘制顺序: 阴影 -> 背景 -> 文字 -> 图片 -> 边框
 * <p>
 * 图片：
 * 1.通过imageView获取图片资源，会根view的高宽重置bitmap大小
 * 2.dyiv_zoom_ratio 缩放比例，默认1，范围0～1（100 * 0.5 = 缩小50%）
 * 文字：
 * dy_text 文字不为空时生效，图片默认缩小50%，文字显示在图片下方
 * dy_text_color 默认白色
 * dy_text_size 默认12dp,文字宽度大于图片宽度时会自动重置文字字号
 * dy_text_bold 默认不加粗
 * <p>
 * 推荐样式：头像,按钮,立体view等
 * 1.图文并茂的按钮，例如：矩形带圆角的按钮 图片在上文字在下
 * 2.带背景色的图片或文字
 * 3.立体按钮：通过设置阴影实现立体效果
 * <p>
 * 注意：
 * 1.android:src 必填
 * 2.如果需要缩放图片推荐使用正方形，长方形时图片必须也是长方形否则缩放时图片会被拉伸（根据width,height缩放的）
 * 3.不支持padding，如果要使图片变小 使用dyiv_zoom_ratio
 * -----------------demo-----------------
 * 圆形头像：
 * <DyImageView
 * android:layout_width="36dp"
 * android:layout_height="36dp"
 * android:scaleType="centerCrop"
 * app:dy_border_width="2dp"
 * app:dy_shape="circular"
 * android:src="@drawable/avatar" />
 * 图文按钮：
 * <DyImageView
 * android:layout_width="36dp"
 * android:layout_height="36dp"
 * android:src="@drawable/btn"
 * app:dy_bg_color="#158EEC"
 * app:dy_border_width="2dp"
 * app:dy_fillet_radius="6dp"
 * app:dy_shadow_color="#23379C"
 * app:dy_shadow_size="5dp"
 * app:dy_shape="fillet"
 * app:dy_text="文字"
 * app:dy_text_bold="true"
 * app:dy_text_color="#ffffff" />
 * 图片按钮：
 * <DyImageView
 * android:layout_width="36dp"
 * android:layout_height="36dp"
 * android:src="@drawable/btn"
 * app:dy_bg_color="#158EEC"
 * app:dy_border_width="2dp"
 * app:dy_fillet_radius="6dp"
 * app:dyiv_zoom_ratio="0.5"
 * app:dy_shadow_color="#23379C"
 * app:dy_shadow_size="5dp"
 * app:dy_shape="fillet"/>
 */
@SuppressLint("AppCompatCustomView")
public class DyImageView extends ImageView implements DyBaseView {

  @Getter
  public DyView dyView;

  //图片
  private final RectF imageRect = new RectF(); //图片矩形范围
  private Paint bitmapPaint = new Paint();
  private Bitmap originalBitmap; //原始图片（重置图片大小时,使用原始图片）
  private Bitmap bitmap; //图片
  private BitmapShader bitmapShader; //图片形状
  //形状
  @FloatRange(from = 0.0, to = 1.0)
  public float zoomRatio = 1; //缩放比例(范围：0～1) - 优先级低
  public boolean autoZoom; //自动缩放 - 优先级高
  public int scaleType = DyViewState.DyIv.SCALE_TYPE.PROPORTIONAL.v();

  public DyImageView(Context context) {
    this(context, null);
  }

  public DyImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_image_view, defStyle, 0);
      //形状
      zoomRatio = typedArray.getFloat(R.styleable.dy_image_view_dyiv_zoom_ratio, 1f); //缩放比例
      autoZoom = typedArray.getBoolean(R.styleable.dy_image_view_dyiv_auto_zoom, false); //自动缩放
      scaleType = typedArray.getInteger(R.styleable.dy_image_view_dyiv_scale_type, DyViewState.DyIv.SCALE_TYPE.PROPORTIONAL.v()); //自动缩放
      typedArray.recycle();
    } else {

    }
    init();
  }

  private void init() {
    //super.setScaleType(ScaleType.FIT_START); //只有在使用 super.onDraw(canvas);时 才会生效
    //setLayerType(LAYER_TYPE_SOFTWARE, null); //禁用硬件加速，阴影硬件加速支持drawText，其他draw**开启硬件加速后失效

    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        setOutlineProvider(new OutlineProvider());
    }*/
  }

  @Override
  public void onDraw(Canvas canvas) {
    try {
      if (originalBitmap == null) {
        super.onDraw(canvas);
        return;
      }

      dyView.initView();
      initImage();

      dyView.clearCanvas(canvas);
      dyView.drawShadow(canvas);
      dyView.drawBg(canvas);
      drawImage(canvas);
      dyView.drawBorder(canvas);
      dyView.drawDown(canvas);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 初始化图片
   */
  private void initImage() {
    if (dyView.openText && zoomRatio == 1) {  //开启文字绘制时，将图片缩放50%
      zoomRatio = 0.5f;
    }

    imageRect.set(dyView.viewRect.left, dyView.viewRect.top, dyView.width, dyView.height);
    if (dyView.borderWidth > 0) dyView.insetBorder(imageRect);
    //缩放图片，图片四边去除边框宽度
    float imageWidth = imageRect.width();
    float imageHeight = imageRect.height();
    if (autoZoom) {
      imageWidth -= dyView.imageZoomSize;
      imageHeight -= dyView.imageZoomSize;
    } else {
      imageWidth *= zoomRatio;
      imageHeight *= zoomRatio;
    }
    if (dyView.shape == DyViewState.SHAPE_TYPE.CIRCULAR.v()) { //圆形时将图片缩放成正方形
      bitmap = ImageHandler.scaleBitmapForSquare(originalBitmap, imageWidth, imageHeight); //缩放图片 - 缩放成正方形
    } else {
      if (DyViewState.DyIv.SCALE_TYPE.SELF.v().equals(scaleType)) {
        bitmap = ImageHandler.scaleSelfBitmap(originalBitmap, imageWidth, imageHeight); //缩放图片 - 根据高宽进行缩放
      } else {
        bitmap = ImageHandler.scaleBitmap(originalBitmap, imageWidth, imageHeight); //缩放图片 - 按照比例进行缩放
      }
    }
    imageWidth = bitmap.getWidth();
    imageHeight = bitmap.getHeight();

    //初始化图片资源范围,  居中
    imageRect.left += Num.mForF(imageRect.width() - imageWidth, 0.5f);
    imageRect.right = imageRect.left + imageWidth;
    imageRect.top += Num.mForF(imageRect.height() - imageHeight, 0.5f);
    imageRect.bottom = imageRect.top + imageHeight;

    //文字
    if (dyView.openText) {
      //字号为0时, 自动计算文字大小
      float textWidth = DensityHandler.getWordWidth(dyView.text.toString(), dyView.textSize);
      //重置文字字号，防止文字超出图片宽度
      if (0 == dyView.textSize || textWidth > bitmap.getWidth()) {
        dyView.textSize = DyTextViewBuilder.initTextSizeForWidth(getContext(), dyView.text.toString(), bitmap.getWidth());
        dyView.textPaint.setTextSize(dyView.textSize);
      }
    }
  }

  /**
   * 绘制图片
   *
   * @param canvas
   */
  private void drawImage(Canvas canvas) {
    float imageRectTop = imageRect.top;
    //绘制文字
    if (dyView.openText) {
      //计算文字位置，如果文字长度大于分为宽度则从left处开始绘制，如果小于则计算出中间位置
      float textLeft = imageRect.left;
      float textWidth = DensityHandler.getWordWidth(dyView.text.toString(), dyView.textSize);

      if (textWidth < imageRect.right) {//图片宽度 大于 文字宽度时候，计算文字起始位置，保证文字居中显示
        //文字起始位置：图片左边框位置 + (图片右边框位置 - 图片左边框位置 - 文字宽度) / 2
        textLeft = imageRect.left + (imageRect.right - imageRect.left - 1 - textWidth) / 2;
      }

      float fontHeight = DensityHandler.getFontHeight(dyView.textSize);
      imageRectTop -= fontHeight / 2; //存在文字时计算图片+文字的居中位置
      dyView.drawText(canvas, textLeft, imageRectTop + bitmap.getHeight() + fontHeight);
    }

    //绘制图片
    if (autoZoom || zoomRatio < 1) { //缩放过的图片不使用任何效果
      canvas.drawBitmap(bitmap, imageRect.left, imageRectTop, bitmapPaint); //默认图片
      //如需要缩放后的图片也有角度(圆角)时 使用此代码
      //canvas.drawBitmap(ImageHandler.initFilletBitmap(bitmap, dyView.getFilletRadius(), dyView.getFilletRadius()), imageRect.left, imageRectTop, bitmapPaint);
    } else if (dyView.shape == DyViewState.SHAPE_TYPE.CIRCULAR.v()) { //圆形
      //canvas.drawCircle(imageRect.centerX(), imageRect.centerY(), dyView.filletRadius, bitmapPaint); //圆形图片
      canvas.drawBitmap(ImageHandler.initOvalBitmap(bitmap), imageRect.left, imageRect.top, bitmapPaint);
    } else if (dyView.shape == DyViewState.SHAPE_TYPE.FILLET.v()) {
      canvas.drawBitmap(ImageHandler.initRoundedCornerBitmap(bitmap, dyView.filletRadius), imageRect.left, imageRect.top, bitmapPaint);
      //canvas.drawRoundRect(imageRect, dyView.filletRadius, dyView.filletRadius, bitmapPaint); //圆角图片
    }


    //显示图片各个位置的点
    /*Paint pa = new Paint();
    pa.setColor(Color.WHITE);
    pa.setStrokeWidth(10);
    canvas.drawPoint(imageRect.left, imageRect.top, pa);
    canvas.drawPoint(imageRect.left, imageRect.bottom, pa);
    canvas.drawPoint(imageRect.right, imageRect.top, pa);
    canvas.drawPoint(imageRect.right, imageRect.bottom, pa);
    canvas.drawPoint(imageRect.centerX(), imageRect.centerY(), pa);*/
  }


  /**
   * 生成bitmap
   * 在setImage***中调用此方法，可以随时设置
   */
  private void initBitmap() {
    try {
      Drawable drawable = getDrawable();
      if (drawable != null) {
        if (drawable instanceof BitmapDrawable) {
          originalBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
          if (drawable instanceof ColorDrawable) {
            originalBitmap = Bitmap.createBitmap(DyViewState.DyIv.COLOR_DRAWABLE_DIMENSION, DyViewState.DyIv.COLOR_DRAWABLE_DIMENSION, DyViewState.DyIv.BITMAP_CONFIG);
          } else {
            originalBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), DyViewState.DyIv.BITMAP_CONFIG);
          }
          Canvas canvas = new Canvas(originalBitmap);
          drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
          drawable.draw(canvas);
        }
      }

    } catch (Exception e) {
      originalBitmap = null;
      e.printStackTrace();
    }
    //invalidate();
  }

  /**
   * 缩放
   *
   * @param zoomRatio
   * @return
   */
  public DyImageView setZoomRatio(float zoomRatio) {
    if (this.zoomRatio != zoomRatio) {
      this.zoomRatio = zoomRatio;
      invalidate();
    }
    return this;
  }

  public DyImageView setAutoZoom(boolean autoZoom) {
    if (this.autoZoom != autoZoom) {
      this.autoZoom = autoZoom;
      invalidate();
    }
    return this;
  }

  /**
   * 设置图片颜色
   *
   * @param cf
   */
  @Override
  public void setColorFilter(ColorFilter cf) {
    bitmapPaint.setColorFilter(cf);
    invalidate();
  }

  @Override
  public void setImageBitmap(Bitmap bm) {
    super.setImageBitmap(bm);
    initBitmap();
  }

  @Override
  public void setImageDrawable(Drawable drawable) {
    super.setImageDrawable(drawable);
    initBitmap();
  }

  @Override
  public void setImageResource(@DrawableRes int resId) {
    super.setImageResource(resId);
    initBitmap();
  }

  @Override
  public void setImageURI(Uri uri) {
    super.setImageURI(uri);
    initBitmap();
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

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private class OutlineProvider extends ViewOutlineProvider {

    @Override
    public void getOutline(View view, Outline outline) {
      Rect bounds = new Rect();
      dyView.borderRect.roundOut(bounds);
      outline.setRoundRect(bounds, bounds.width() / 2.0f);
    }
  }
}