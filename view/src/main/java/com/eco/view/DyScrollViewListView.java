package com.eco.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 1.ListView与ScrollView一起使用的时候会导致 ListView内容显示不全, 此view就是解决显示不全问题的
 * 2.也算是变相的禁止ListView滑动，因为在显示ListView时已经加载出所有内容了
 */
public class DyScrollViewListView extends ListView {
  public DyScrollViewListView(Context context) {
    super(context);
  }

  public DyScrollViewListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DyScrollViewListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
  }
} 