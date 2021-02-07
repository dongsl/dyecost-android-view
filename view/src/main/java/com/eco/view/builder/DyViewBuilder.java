package com.eco.view.builder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import com.eco.view.handler.ViewHandler;
import com.eco.view.DyView;
import com.eco.view.DyViewState;

import lombok.Getter;

/**
 * view生成器父类
 * 1.设置view的属性
 * 2.调用build后会根据设置的属性生成view
 * 3.基础样式使用layout()配置
 * 主要目标：
 * 1.简化java代码中创建view的代码量，更加易读
 * 2.不调用子类中的build()前不会创建view对象
 */
@Getter
public class DyViewBuilder {
  public Integer w = ViewHandler.WRAP_CONTENT, h = ViewHandler.WRAP_CONTENT; //大小
  protected Context context;
  protected LinearLayout.LayoutParams params; //not null: 直接将params放到view中,不会使用wh和m*，null: 生成一个新的params不会赋值给此变量，场景：生成view时候 有一个公用的params,然后传入到builder中
  protected float weight; //大小
  protected Integer ml = 0, mt = 0, mr = 0, mb = 0; //外间距 不算入height中
  protected Integer pl = 0, pt = 0, pr = 0, pb = 0; //内间距 算入height中
  protected Integer gravity; //位置布局
  protected Integer bgResource; //背景资源
  protected View.OnClickListener onClickListener;
  //自定义View属性
  //形状
  protected Boolean resizeView; //调整view大小
  protected Integer shape; //view形状
  protected Float filletRadius; //圆角半径
  protected Integer filletDirection; //圆角方向
  //阴影
  protected Float shadowSize; //阴影大小
  protected Integer shadowColor; //阴影颜色
  protected Integer shadowAlpha; //阴影透明度
  //背景色
  protected Integer bgColor; //背景颜色
  protected Integer bgColorAlpha; //背景色透明度
  //边框
  protected Float borderWidth; //边框宽度
  protected Integer borderColor; //边框颜色
  //文字
  protected CharSequence text;//内容;//内容
  protected Float textSize; //字体大小
  protected Integer textColor; //文字颜色
  protected Boolean textBold; //文字加粗
  //文字 - 渐变
  protected Integer textColorGD = DyViewState.COLOR_GD.HORIZONTAL.v(); //背景色渐变方向
  protected Integer textColorStart = Color.TRANSPARENT;
  protected Integer textColorEnd = Color.TRANSPARENT;
  //按下事件
  protected Integer downStyle; //按下类型
  protected Integer downColor; //按下颜色
  protected Integer downColorAlpha; //按下透明度
  protected Float downResizeRatio; //重置size比例(<1:view缩小, >1:view放大)(例如0.9 = view高宽*90%)
  protected Boolean openSelected; //开启选中功能, true: 点击抬起后不会恢复为原有样式, false: 恢复原有样式
  protected Boolean selected; //选中状态, true: 样式为ACTION_DOWN, false: 样式为ACTION_UP，openSelected=true时只能手动将此变量设置为false
  protected Integer radioGroupId; //单选组ID, null: 点击任意开启选中的view后其他view样式不会恢复到未选中状态, not null: 在最外层布局中依次向下对view进行验证，id相等的view恢复到未选中状态

  //设置基础布局信息
  public interface ViewBuilderLayout {
    DyViewBuilder set(DyViewBuilder v);
  }

  public DyViewBuilder weight(float weight) {
    this.weight = weight;
    return this;
  }

  public DyViewBuilder wh(Integer wh) {
    wh(wh, wh);
    return this;
  }

  public DyViewBuilder wh(Float wh) {
    wh(wh, wh);
    return this;
  }

  public DyViewBuilder wh(Integer w, Integer h) {
    if (null != w) this.w = w;
    if (null != h) this.h = h;
    return this;
  }

  public DyViewBuilder wh(Float w, Float h) {
    if (null != w) this.w = w.intValue();
    if (null != h) this.h = h.intValue();
    return this;
  }

  public DyViewBuilder whm() {
    h = w = ViewHandler.MATCH_PARENT;
    return this;
  }


  public DyViewBuilder margins(Integer m) {
    margins(m, m, m, m);
    return this;
  }

  public DyViewBuilder margins(Integer ml, Integer mt, Integer mr, Integer mb) {
    if (null != ml) this.ml = ml;
    if (null != mt) this.mt = mt;
    if (null != mr) this.mr = mr;
    if (null != mb) this.mb = mb;
    //params.margins(ml, mt, mr, mb);
    return this;
  }

  public DyViewBuilder marginsX(Integer x) {
    return marginsX(x, x);
  }

  public DyViewBuilder marginsX(Integer ml, Integer mr) {
    return margins(ml, null, mr, null);
  }

  public DyViewBuilder marginsY(Integer y) {
    return marginsY(y, y);
  }

  public DyViewBuilder marginsY(Integer mt, Integer mb) {
    return margins(null, mt, null, mb);
  }


  public DyViewBuilder padding(Integer pl, Integer pt, Integer pr, Integer pb) {
    if (null != pl) this.pl = pl;
    if (null != pt) this.pt = pt;
    if (null != pr) this.pr = pr;
    if (null != pb) this.pb = pb;
    return this;
  }

  public DyViewBuilder padding(Integer p) {
    return padding(p, p, p, p);
  }

  public DyViewBuilder paddingX(Integer x) {
    return paddingX(x, x);
  }

  public DyViewBuilder paddingX(Integer pl, Integer pr) {
    padding(pl, null, pr, null);
    return this;
  }

  public DyViewBuilder paddingY(Integer y) {
    return paddingY(y, y);
  }

  public DyViewBuilder paddingY(Integer pt, Integer pb) {
    return padding(null, pt, null, pb);
  }

  public DyViewBuilder bgColor(int bgColor) {
    this.bgColor = bgColor;
    return this;
  }

  public DyViewBuilder bgResource(Integer bgResource) {
    this.bgResource = bgResource;
    return this;
  }

  public DyViewBuilder gravity(Integer gravity) {
    this.gravity = gravity;
    return this;
  }

  public DyViewBuilder param(LinearLayout.LayoutParams params) {
    this.params = params;
    return this;
  }

  public DyViewBuilder shape(Boolean resizeView, Integer shape, Float filletRadius, Integer filletDirection) {
    this.resizeView = resizeView;
    this.shape = shape;
    this.filletRadius = filletRadius;
    this.filletDirection = filletDirection;
    return this;
  }

  public DyViewBuilder shadow(Float shadowSize, Integer shadowColor, Integer shadowAlpha) {
    this.shadowSize = shadowSize;
    this.shadowColor = shadowColor;
    this.shadowAlpha = shadowAlpha;
    return this;
  }

  public DyViewBuilder bgColor(Integer bgColor, Integer bgColorAlpha) {
    this.bgColor = bgColor;
    this.bgColorAlpha = bgColorAlpha;
    return this;
  }

  public DyViewBuilder border(Float borderWidth, Integer borderColor) {
    this.borderWidth = borderWidth;
    this.borderColor = borderColor;
    return this;
  }

  public DyViewBuilder text(CharSequence text, Float textSize, Integer textColor, Boolean textBold) {
    this.text = text;
    this.textSize = textSize;
    this.textColor = textColor;
    this.textBold = textBold;
    return this;
  }

  public DyViewBuilder text(CharSequence text) {
    this.text = text;
    return this;
  }

  public DyViewBuilder textSize(Float textSize) {
    this.textSize = textSize;
    return this;
  }

  public DyViewBuilder textColor(Integer textColor) {
    this.textColor = textColor;
    return this;
  }

  public DyViewBuilder textBold(boolean textBold) {
    this.textBold = textBold;
    return this;
  }

  public DyViewBuilder textColorGradient(Integer textColorGD, Integer textColorStart, Integer textColorEnd) {
    this.textColorGD = textColorGD;
    this.textColorStart = textColorStart;
    this.textColorEnd = textColorEnd;
    return this;
  }

  public DyViewBuilder down(Integer downStyle, Integer downColor, Integer downColorAlpha, Float downResizeRatio) {
    this.downStyle = downStyle;
    this.downColor = downColor;
    this.downColorAlpha = downColorAlpha;
    this.downResizeRatio = downResizeRatio;
    return this;
  }

  public DyViewBuilder selected(Boolean openSelected, Boolean selected, Integer radioGroupId) {
    this.openSelected = openSelected;
    this.selected = selected;
    this.radioGroupId = radioGroupId;
    return this;
  }

  /**
   * 需要设置DyView属性的必须调用此方法
   *
   * @param dyView
   * @return
   */
  protected DyViewBuilder build(View view, DyView dyView) {
    //设置布局
    view.setPadding(pl, pt, pr, pb);
    if (null == params) {
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h, weight);
      params.setMargins(ml, mt, mr, mb);
      view.setLayoutParams(params);
    } else {
      view.setLayoutParams(params);
    }

    //形状
    if (null != resizeView) dyView.resizeView = resizeView;
    if (null != shape) dyView.shape = shape;
    if (null != filletRadius) dyView.filletRadius = filletRadius;
    if (null != filletDirection) dyView.filletDirection = filletDirection;
    //阴影
    if (null != shadowSize) dyView.shadowSize = shadowSize;
    if (null != shadowColor) dyView.shadowColor = shadowColor;
    if (null != shadowAlpha) dyView.shadowAlpha = shadowAlpha;
    //背景色
    if (null != bgColor) dyView.bgColor = bgColor;
    if (null != bgColorAlpha) dyView.bgColorAlpha = bgColorAlpha;
    //边框
    if (null != borderWidth) dyView.borderWidth = borderWidth;
    if (null != borderColor) dyView.borderColor = borderColor;
    //文字
    dyView.text = text;
    if (null != textSize) dyView.textSize = textSize;
    if (null != textColor) dyView.textColor = textColor;
    if (null != textBold) dyView.textBold = textBold;
    //文字渐变
    if (null != textColorGD) dyView.textColorGD = textColorGD;
    if (null != textColorStart) dyView.textColorStart = textColorStart;
    if (null != textColorEnd) dyView.textColorEnd = textColorEnd;
    //按下事件
    if (null != downStyle) dyView.downStyle = downStyle;
    if (null != downColor) dyView.downColor = downColor;
    if (null != downColorAlpha) dyView.downColorAlpha = downColorAlpha;
    if (null != downResizeRatio) dyView.downResizeRatio = downResizeRatio;
    if (null != openSelected) dyView.openSelected = openSelected;
    if (null != selected) dyView.selected = selected;
    if (null != radioGroupId) dyView.radioGroupId = radioGroupId;
    //view.invalidate();
    return this;
  }
}