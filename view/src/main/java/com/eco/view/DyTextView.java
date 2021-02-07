package com.eco.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.Num;
import com.eco.view.builder.DyTextViewBuilder;

import lombok.Getter;

/**
 * DyTextView
 * 绘制顺序: 背景 -> 边框 -> 文字 -> 按下效果
 * 1.使用dy_bg_color_alpha设置背景色的透明度，不会影响到文字透明度
 * 2.textView的dy_down_style不为resize时候，设置dy_down_resize_ratio属性只会对文字有影响
 * -----------------demo-----------------
 * <DyTextView
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"
 * android:text="608"
 * android:textColor="@color/white"
 * android:textSize="@dimen/text_size13"
 * app:dy_bg_color="@color/gray"
 * app:dy_bg_color_alpha="120"
 * app:dy_border_width="1dp"
 * app:dy_down_color="@color/black"
 * app:dy_down_resize_ratio="0.9"
 * app:dy_down_style="resize"
 * />
 */
@SuppressLint("AppCompatCustomView")
public class DyTextView extends TextView implements DyBaseView {

  @Getter
  public DyView dyView;
  public boolean removeLineSpacing; //移除行间距

  public DyTextView(final Context context) {
    this(context, null);
  }

  public DyTextView(final Context context, final AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_text_view);
      removeLineSpacing = typedArray.getBoolean(R.styleable.dy_text_view_dytv_remove_line_spacing, false);
      typedArray.recycle();
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    dyView.initView();
    initText();

    dyView.clearCanvas(canvas);
    dyView.drawShadow(canvas);
    dyView.drawBg(canvas);
    super.onDraw(canvas);
    dyView.drawBorder(canvas);
    dyView.drawDown(canvas);
  }

  public void initText() {
    dyView.openText = Boolean.TRUE;
    dyView.textSize = getTextSize();
    dyView.initText();
    Paint textPaint = this.getPaint();
    textPaint.setStrikeThruText(dyView.textStrikeThru);
    textPaint.setUnderlineText(dyView.textUnderline);
    if (null != dyView.textPaint.getShader()) {
      textPaint.setShader(dyView.textPaint.getShader());
    } else {
      textPaint.setShader(null);
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
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        DyTextViewBuilder.sizePx(this, Num.mForF(getTextSize(), dyView.downResizeRatio));
        break;
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        DyTextViewBuilder.sizePx(this, Num.dForF(getTextSize(), dyView.downResizeRatio));
        break;
    }
    dyView.onTouchEvent(event);
    super.onTouchEvent(event);
    return this.hasOnClickListeners();
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (removeLineSpacing) {
      setIncludeFontPadding(true);
      float textHeight = DensityHandler.getFontHeight(getText().toString(), getTextSize());
      float textOccupyHeight = DensityHandler.getFontHeightForOccupy(getTextSize());
      //文字高度 > 字体高度时, 说明文字超出基线,将高度设置为最大字体高度
      textHeight = textHeight > textOccupyHeight ? DensityHandler.getFontHeightForOccupyMax(getTextSize()) : textOccupyHeight;

      setPadding(getPaddingLeft(), -(int) Math.ceil(DensityHandler.getFontHeightForSpacing(getTextSize())), getPaddingRight(), 0);
      //dyView.maxHeight = dyView.height = textHeight;
      dyView.setHeight(textHeight);
    }
    setMeasuredDimension((int) dyView.maxWidth, (int) dyView.maxHeight);
  }
}