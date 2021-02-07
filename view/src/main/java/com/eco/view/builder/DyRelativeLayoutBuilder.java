package com.eco.view.builder;

import android.content.Context;
import android.view.View;

import com.eco.view.DyRelativeLayout;

/**
 * relativeLayout生成器
 * 1,只有在build后调用addViews(views)才会有效
 * 2.调用build(views)后可以随时调用addViews
 * -----------------demo-----------------
 * DyRelativeLayoutBuilder.init(context)
 * .layout(v -> v.wh().gravity())
 * .build(views)
 */
public class DyRelativeLayoutBuilder extends DyViewBuilder {
  private Context context;
  private DyRelativeLayout relativeLayout;

  private DyRelativeLayoutBuilder(Context context) {
    this.context = context;
  }

  public static DyRelativeLayoutBuilder init(Context context) {
    DyRelativeLayoutBuilder view = new DyRelativeLayoutBuilder(context);
    return view;
  }

  public DyRelativeLayoutBuilder layout(ViewBuilderLayout viewBuilderLayout) {
    viewBuilderLayout.set(this);
    return this;
  }

  public DyRelativeLayoutBuilder click(View.OnClickListener onClickListener) {
    this.onClickListener = onClickListener;
    return this;
  }

  public DyRelativeLayoutBuilder addViews(View... vs) {
    if (null == relativeLayout) {
      return this;
    }
    if (null != vs && vs.length > 0) {
      for (View v : vs) {
        relativeLayout.addView(v);
      }
    }
    return this;
  }

  public DyRelativeLayout build(View... vs) {
    relativeLayout = new DyRelativeLayout(context);
    super.build(relativeLayout, relativeLayout.getDyView());
    if (null != gravity) relativeLayout.setGravity(gravity);
    if (null != bgResource) relativeLayout.setBackgroundResource(bgResource);
    if (null != onClickListener) relativeLayout.setOnClickListener(onClickListener);
    addViews(vs);
    relativeLayout.invalidate();
    return relativeLayout;
  }
}