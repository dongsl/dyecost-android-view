package com.eco.view.builder;

import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.view.Gravity;

import com.eco.view.R;
import com.eco.basics.handler.DensityHandler;
import com.eco.view.DyEditText;

/**
 * textView生成器
 * -----------------demo-----------------
 * DyTextViewBuilder
 * .init(context).text().size()
 * .layout(v -> v.wh().margins())
 * .build();
 */
public class DyEditTextBuilder extends DyViewBuilder {
  private DyEditText editText;
  private boolean removeUnderline; //移除下划线

  private DyEditTextBuilder(Context context) {
    this.context = context;
  }

  public static DyEditTextBuilder init(Context context) {
    DyEditTextBuilder view = new DyEditTextBuilder(context);
    return view;
  }

  public DyEditTextBuilder layout(ViewBuilderLayout viewBuilderLayout) {
    viewBuilderLayout.set(this);
    return this;
  }

  public DyEditTextBuilder removeUnderline(boolean removeUnderline) {
    this.removeUnderline = removeUnderline;
    return this;
  }

  public DyEditText build() {
    editText = new DyEditText(context);
    super.build(editText, editText.getDyView());
    editText.setText(text);
    editText.setTextColor(null == textColor ? ContextCompat.getColor(context, R.color.text) : textColor);
    DyTextViewBuilder.sizePx(editText, null == textSize ? DensityHandler.getDimenPx(context, R.dimen.text_size16) : textSize);
    editText.setGravity(null == gravity ? Gravity.CENTER : gravity);
    if (null != textBold && textBold) {
      editText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }
    editText.removeUnderline = removeUnderline;
    editText.invalidate();
    return editText;
  }
}