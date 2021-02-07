package com.eco.view.disc.extend.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.eco.basics.base.DyFragment;
import com.eco.view.R;
import com.eco.basics.binding.EventBindingAdapter;
import com.eco.basics.constants.Time;
import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.ResourcesHandler;
import com.eco.basics.handler.StringHandler;
import com.eco.view.handler.ViewHandler;
import com.eco.view.DyImageView;
import com.eco.view.DyLinearLayout;
import com.eco.view.DyTextView;
import com.eco.view.DyViewState;
import com.eco.view.builder.DyImageViewBuilder;
import com.eco.view.builder.DyLinearLayoutBuilder;
import com.eco.view.builder.DyTextViewBuilder;
import com.eco.view.databinding.FragementExtendBinding;
import com.eco.view.disc.extend.model.Extend;
import com.eco.view.disc.extend.view.ExtendView;


import java.util.List;

/**
 * 扩展盘内容页面
 */
public class ExtendFragment extends DyFragment<FragementExtendBinding> implements View.OnTouchListener {

  private LinearLayout extendLayout;
  private List<List<Extend>> extendPageList;

  private float layoutMargin; //扩展盘布局间距
  private float extendRowLayoutHeight; //扩展盘每行布局可用高度

  private float extendBtnWidth; //每个按钮占用的宽度
  private float extendBtnImageLayoutSize; //每个按钮图片布局的大小
  private float extendBtnImageSize; //每个按钮图片的大小
  private float extendBtnTextSize;
  private float extendBtnTextMarginTop;
  private int textHintColor;
  private ExtendView.ExtendClickListener extendClickListener;

  public ExtendFragment() {
  }

  @SuppressLint("ValidFragment")
  public ExtendFragment(List<List<Extend>> extendPageList) {
    this.extendPageList = extendPageList;
  }

  /**
   * 设置布局像素
   *
   * @param layoutMargin          扩展盘布局间距
   * @param extendRowLayoutHeight 扩展盘每行布局可用高度
   */
  public void setLayoutParam(float layoutMargin, float extendRowLayoutHeight) {
    this.layoutMargin = layoutMargin;
    this.extendRowLayoutHeight = extendRowLayoutHeight;
  }

  public void setBtnLayoutParam(float extendBtnWidth, float extendBtnImageLayoutSize, float extendBtnImageSize, float extendBtnTextSize, float extendBtnTextMarginTop, int textHintColor) {
    this.extendBtnWidth = extendBtnWidth;
    this.extendBtnImageLayoutSize = extendBtnImageLayoutSize;
    this.extendBtnImageSize = extendBtnImageSize;
    this.extendBtnTextSize = extendBtnTextSize;
    this.extendBtnTextMarginTop = extendBtnTextMarginTop;
    this.textHintColor = textHintColor;
  }

  /**
   * 设置监听
   * 点击事件，触摸事件等
   *
   * @param extendClickListener
   */
  public void setListener(ExtendView.ExtendClickListener extendClickListener) {
    this.extendClickListener = extendClickListener;
  }

  @Override
  protected void initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    init(R.layout.fragement_extend); //初始化 + 绑定
  }


  /**
   * 初始化变量（参数，控件，viewModel等）
   */
  @Override
  public void initVariable() {
    //获取参数

    //获取控件
    extendLayout = binding.extendLayout;

    //初始化hander

    //初始化其他
  }

  /**
   * 初始化控件（设置不能在layout中设置的属性，样式，计算像素等）
   */
  @Override
  public void initControl() {
    extendLayout.removeAllViews();

    //生成布局
    DyLinearLayoutBuilder extendRowLayoutBuilder = DyLinearLayoutBuilder.init(context)
      .orientation(LinearLayout.HORIZONTAL)
      .layout(v -> v.wh(ViewHandler.MATCH_PARENT, (int) extendRowLayoutHeight).gravity(Gravity.LEFT)); //扩展每行布局

    DyLinearLayoutBuilder extendBtnLayoutBuilder = DyLinearLayoutBuilder.init(context)
      .orientation(LinearLayout.VERTICAL)
      .layout(v -> v.wh((int) extendBtnWidth, ViewHandler.MATCH_PARENT).gravity(Gravity.CENTER)); //每个按钮的布局

    DyLinearLayoutBuilder extendBtnImageLayoutBuilder = DyLinearLayoutBuilder.init(context).orientation(LinearLayout.VERTICAL)
      .layout(v -> v.wh((int) extendBtnImageLayoutSize).gravity(Gravity.CENTER)
        .shape(null, DyViewState.SHAPE_TYPE.FILLET.v(), DensityHandler.getDimenPx(context, R.dimen.extend_fillet), null)
        .bgColor(ContextCompat.getColor(context, R.color.white))
        .down(DyViewState.DOWN_TYPE.OVERRIDE.v(), ContextCompat.getColor(context, R.color.btn_pressed_default_bg), null, null)); //每个按钮图片的布局
    DyImageViewBuilder extendBtnImageBuilder = DyImageViewBuilder.init(context).layout(v -> (v.wh((int) extendBtnImageSize))); //每个按钮的图标
    DyTextViewBuilder extendBtnTextBuilder = DyTextViewBuilder.init(context).layout(v -> v.textSize(extendBtnTextSize).textColor(textHintColor).marginsY((int) extendBtnTextMarginTop, null)); //每个按钮的文字

    for (List<Extend> extendRowList : extendPageList) {
      LinearLayout extendRowLayout = extendRowLayoutBuilder.build(); //扩展每行布局
      for (Extend extend : extendRowList) {
        DyImageView extendBtnImageView = extendBtnImageBuilder.image(ResourcesHandler.getDrawableId(context, extend.getImage())).build(); //每个按钮的图标
        DyTextView extendBtnTextView = extendBtnTextBuilder.layout(v -> v.text(StringHandler.getValue(context, ResourcesHandler.getStringId(context, extend.getName())))).build(); //每个按钮的文字
        DyLinearLayout extendBtnImageLayout = extendBtnImageLayoutBuilder.build(extendBtnImageView);
        DyLinearLayout extendBtnLayout = extendBtnLayoutBuilder.build(extendBtnImageLayout, extendBtnTextView);
        EventBindingAdapter.clickInterval(extendBtnImageLayout, v -> extendClickListener.click(extend.getType()), Time.DEFAULT_CLICK_MTI); //绑定点击事件
        extendRowLayout.addView(extendBtnLayout);
      }
      extendLayout.addView(extendRowLayout);
    }
  }

  /**
   * 初始化数据（设置固定数据，获取首次打开页面加载的数据）
   */
  @Override
  public void initData() {
  }

  /**
   * 绑定观察者监听(liveData，databinding)
   */
  @Override
  public void bindObserve() {

  }

  /**
   * 绑定事件监听
   */
  @Override
  public void bindEvent() {
  }

  /**
   * 服务器获取数据
   */
  @Override
  public void getData() {
    try {
      //finishLoadData(); //完成加载数据
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    // 拦截触摸事件，防止泄露下去
    view.setOnTouchListener(this);
  }

  //监听点击位置
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    return true;
  }

}