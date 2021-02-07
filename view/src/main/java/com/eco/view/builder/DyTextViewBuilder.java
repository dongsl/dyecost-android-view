package com.eco.view.builder;

import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.eco.view.R;
import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.Num;
import com.eco.view.handler.ViewHandler;
import com.eco.view.DyTextView;

/**
 * textView生成器
 * -----------------demo-----------------
 * DyTextViewBuilder
 * .init(context).text().size()
 * .layout(v -> v.wh().margins())
 * .build();
 */
public class DyTextViewBuilder extends DyViewBuilder {
  private DyTextView textView;

  private boolean singleLine; //单行

  private DyTextViewBuilder(Context context) {
    this.context = context;
  }

  public static DyTextViewBuilder init(Context context) {
    DyTextViewBuilder view = new DyTextViewBuilder(context);
    return view;
  }

  public DyTextViewBuilder layout(ViewBuilderLayout viewBuilderLayout) {
    viewBuilderLayout.set(this);
    return this;
  }

  public DyTextViewBuilder singleLine() {
    this.singleLine = Boolean.TRUE;
    return this;
  }

  public static void sizePx(TextView view, float size) {
    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
  }

  public static void sizeDp(TextView view, float size) {
    view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
  }

  /**
   * 计算文字size(px)，通过宽度(px)与默认文字大小计算
   *
   * @param context
   * @param text
   * @param textWidth
   * @return
   */
  public static float initTextSizeForWidth(Context context, String text, float textWidth) {
    float textSize = DensityHandler.getDimenPx(context, R.dimen.text_size16); //默认文字大小
    float resizeRatio = Num.dForF(textWidth, DensityHandler.getWordWidth(text, textSize)); //调整文字大小比例
    return Num.mForF(textSize, resizeRatio);
  }

  public static float initTextSizeForWidth(Context context, int textLength, float textWidth) {
    float textSize = DensityHandler.getDimenPx(context, R.dimen.text_size16); //默认文字大小
    float resizeRatio = Num.dForF(textWidth, DensityHandler.getWordWidth(textLength, textSize)); //调整文字大小比例
    return Num.mForF(textSize, resizeRatio);
  }

  /**
   * 计算文字size(px)，通过高度(px)与默认文字大小计算
   *
   * @param context
   * @param textHeight
   * @return
   */
  public static float initTextSizeForHeight(Context context, float textHeight) {
    float textSize = DensityHandler.getDimenPx(context, R.dimen.text_size16); //默认文字大小
    float resizeRatio = Num.dForF(textHeight, DensityHandler.getFontHeightForLineSpacing(textSize)); //调整文字大小比例
    return Num.mForF(textSize, resizeRatio); //减1防止精度不准
  }

  /**
   * 计算文字size(px)，通过宽高(px)与默认文字大小计算， 使用小的一方防止字体超出view范围
   *
   * @param context
   * @param text
   * @param textWidth  目标宽度
   * @param textHeight 目标高度
   * @return
   */
  public static float initTextSizeForMin(Context context, String text, float textWidth, float textHeight) {
    return Math.min(initTextSizeForWidth(context, text, textWidth), initTextSizeForHeight(context, textHeight));
  }

  public static float initTextSizeForMin(Context context, int textLength, float textWidth, float textHeight) {
    return Math.min(initTextSizeForWidth(context, textLength, textWidth), initTextSizeForHeight(context, textHeight));
  }

  public DyTextView build() {
    textView = new DyTextView(context);
    super.build(textView, textView.getDyView());
    textView.setText(text);
    textView.setTextColor(null == textColor ? ContextCompat.getColor(context, R.color.text) : textColor);
    sizePx(textView, null == textSize ? DensityHandler.getDimenPx(context, R.dimen.text_size16) : textSize);
    textView.setGravity(null == gravity ? Gravity.CENTER : gravity);
    if (null != textBold && textBold) {
      textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    if (singleLine) {
      textView.setMaxLines(1);
      textView.setEllipsize(TextUtils.TruncateAt.END);
    }
    if (null != onClickListener) textView.setOnClickListener(onClickListener);
    return textView;
  }

  /**
   * 表情文本混排view
   *
   * @return
   */
  public DyTextView buildEmoji(CharSequence text) {
    if (null == gravity) gravity = Gravity.LEFT;
    this.text = ViewHandler.emojiTextMixed(context, textSize, text);
    return build();
  }
}