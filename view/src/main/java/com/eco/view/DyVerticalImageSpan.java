package com.eco.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * 垂直居中的ImageSpan（图文view）
 * 1.系统的ImageSpan：内容会在布局的下方显示，通过此view将内容调整为水平居中
 * -----------------demo-----------------
 * BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap); //创建资源
 * bitmapDrawable.setBounds(0, 0, 32, 32); //设置资源大小
 * DyVerticalImageSpan imageSpan = new DyVerticalImageSpan(bitmapDrawable);
 * SpannableString spannableString = new SpannableString(text);
 * spannableString.setSpan(imageSpan, 替换开始位置, 替换结束位置, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
 */
public class DyVerticalImageSpan extends ImageSpan {

  public DyVerticalImageSpan(Drawable drawable) {
    super(drawable);
  }

  @Override
  public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt) {
    Drawable drawable = getDrawable();
    Rect rect = drawable.getBounds();
    if (fontMetricsInt != null) {
      Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
      int fontHeight = fmPaint.bottom - fmPaint.top;
      int drHeight = rect.bottom - rect.top;

      int top = drHeight / 2 - fontHeight / 4;
      int bottom = drHeight / 2 + fontHeight / 4;

      fontMetricsInt.ascent = -bottom;
      fontMetricsInt.top = -bottom;
      fontMetricsInt.bottom = top;
      fontMetricsInt.descent = top;
    }
    return rect.right;
  }

  @Override
  public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
    Drawable drawable = getDrawable();
    canvas.save();
    int transY = (bottom - top - drawable.getBounds().bottom) / 2 + top;
    canvas.translate(x, transY);
    drawable.draw(canvas);
    canvas.restore();
  }
}