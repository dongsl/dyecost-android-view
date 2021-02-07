package com.eco.view.handler;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.LinearLayout;

import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.ResourcesHandler;
import com.eco.basics.handler.StringHandler;
import com.eco.view.R;
import com.eco.view.DyVerticalImageSpan;
import com.eco.view.constants.DyConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewHandler {

  public final static int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
  public final static int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;


  public static void setLayoutParams(View v, Integer ml, Integer mt, Integer mr, Integer mb) {
    setLayoutParams(v, v.getWidth(), v.getHeight(), null, ml, mt, mr, mb);
  }

  public static void setLayoutParams(View v, Integer width, Integer height, Integer gravity, Integer ml, Integer mt, Integer mr, Integer mb) {
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(null == width ? WRAP_CONTENT : width, null == height ? WRAP_CONTENT : height);
    params.setMargins(ml, mt, mr, mb);
    if (null != gravity) params.gravity = gravity;
    v.setLayoutParams(params);
  }

  /**
   * 生成表情文字混排
   *
   * @param context
   * @param size
   * @param text
   * @return
   */
  public static SpannableString emojiTextMixed(Context context, Float size, CharSequence text) {
    if (StringHandler.isEmpty(text)) text = "";
    size = null == size ? DensityHandler.getDimenPx(context, R.dimen.text_size16) : size;
    SpannableString spannableString = new SpannableString(text);
    float fontHeight = DensityHandler.getFontHeightForLineSpacing(size); //文字高度
    float emojiSize = fontHeight; //图片大小

    Pattern r = Pattern.compile("\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+]"); //匹配规则："[任意字符]"
    Matcher m = r.matcher(text);
    while (m.find()) {
      BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), ResourcesHandler.loadBitmap(DyConstants.EMOJI, m.group(), DyConstants.EMOJI_SUFFIX)); //创建资源
      bitmapDrawable.setBounds(0, 0, (int) emojiSize, (int) emojiSize); //设置资源大小
      DyVerticalImageSpan imageSpan = new DyVerticalImageSpan(bitmapDrawable);
      spannableString.setSpan(imageSpan, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return spannableString;
  }
}
