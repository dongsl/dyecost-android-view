package com.eco.view.builder;

import android.content.Context;
import android.view.View;

import com.eco.view.DyLinearLayout;

/**
 * linearLayout生成器
 * 1,只有在build后调用addViews(views)才会有效
 * 2.调用build(views)后可以随时调用addViews
 * -----------------demo-----------------
 * DyLinearLayoutBuilder.init(context)
 * .layout(v -> v.wh().gravity())
 * .build(views)
 */
public class DyLinearLayoutBuilder extends DyViewBuilder {
  private Context context;
  private DyLinearLayout linearLayout;
  private Integer orientation; //水平垂直布局

  private DyLinearLayoutBuilder(Context context) {
    this.context = context;
  }

  public static DyLinearLayoutBuilder init(Context context) {
    DyLinearLayoutBuilder view = new DyLinearLayoutBuilder(context);
    return view;
  }

  public DyLinearLayoutBuilder layout(ViewBuilderLayout viewBuilderLayout) {
    viewBuilderLayout.set(this);
    return this;
  }

  public DyLinearLayoutBuilder orientation(Integer orientation) {
    this.orientation = orientation;
    return this;
  }

  public DyLinearLayoutBuilder click(View.OnClickListener onClickListener) {
    this.onClickListener = onClickListener;
    return this;
  }

  public DyLinearLayoutBuilder addViews(View... vs) {
    if (null == linearLayout) {
      return this;
    }
    if (null != vs && vs.length > 0) {
      for (View v : vs) {
        linearLayout.addView(v);
      }
    }
    return this;
  }

  public DyLinearLayout build(View... vs) {
    linearLayout = new DyLinearLayout(context);
    super.build(linearLayout, linearLayout.getDyView());
    if (null != gravity) linearLayout.setGravity(gravity);
    if (null != orientation) linearLayout.setOrientation(orientation);
    if (null != bgResource) linearLayout.setBackgroundResource(bgResource);
    if (null != onClickListener) linearLayout.setOnClickListener(onClickListener);
    addViews(vs);
    linearLayout.invalidate();
    return linearLayout;
  }
}