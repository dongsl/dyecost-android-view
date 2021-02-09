package com.eco.view.disc.stickers.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.eco.view.R;
import com.eco.view.constants.DyConstants;
import com.eco.view.handler.ViewHandler;
import com.eco.view.DyImageView;
import com.eco.view.builder.DyImageViewBuilder;
import com.eco.view.builder.DyLinearLayoutBuilder;
import com.eco.view.disc.stickers.model.Stickers;
import com.eco.view.disc.stickers.view.StickersView;

import java.util.List;

/**
 * 表情盘内容页面
 */
public class StickersFragment extends Fragment implements View.OnTouchListener {

  private Context context;
  private LinearLayout stickersLayout;
  private List<List<Stickers>> stickersPageList;
  private int stickersType; //表情类型

  private float stickersLayoutMarginX; //表情盘布局水平间距
  private float stickersRowLayoutHeight; //表情盘每行布局高度
  private float stickersSize; //表情大小
  private float stickersMarginX; //表情水平间距

  private StickersView.StickersClickListener stickersClickListener;

  public StickersFragment() {
  }


  @SuppressLint("ValidFragment")
  public StickersFragment(int stickersType, List<List<Stickers>> stickersPageList) {
    this.stickersPageList = stickersPageList;
    this.stickersType = stickersType;
  }

  /**
   * 设置布局像素
   *
   * @param stickersLayoutMarginX   表情盘布局水平间距
   * @param stickersRowLayoutHeight 表情盘每行布局高度
   * @param stickersSize            表情大小
   * @param stickersMarginX         表情水平间距
   */
  public void setLayoutParam(float stickersLayoutMarginX, float stickersRowLayoutHeight, float stickersSize, float stickersMarginX) {
    this.stickersLayoutMarginX = stickersLayoutMarginX;
    this.stickersRowLayoutHeight = stickersRowLayoutHeight;
    this.stickersSize = stickersSize;
    this.stickersMarginX = stickersMarginX;

  }

  /**
   * 设置监听
   * 点击事件，触摸事件等
   *
   * @param stickersClickListener
   */
  public void setListener(StickersView.StickersClickListener stickersClickListener) {
    this.stickersClickListener = stickersClickListener;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragement_stickers, null);
    context = getActivity();
    stickersLayout = view.findViewById(R.id.stickers_layout);
    initControl();
    return view;
  }

  /**
   * 初始化表情控件（设置不能在layout中设置的属性，样式，计算像素等）
   */
  public void initControl() {
    stickersLayout.removeAllViews();
    ViewHandler.setLayoutParams(stickersLayout, ViewHandler.MATCH_PARENT, ViewHandler.MATCH_PARENT, null, (int) stickersLayoutMarginX, 0, (int) stickersLayoutMarginX, 0); //重置表情盘 高宽,位置,间距
    //生成布局
    DyLinearLayoutBuilder stickersRowLayoutBuilder = DyLinearLayoutBuilder.init(context).orientation(LinearLayout.HORIZONTAL).layout(v -> v.wh(ViewHandler.MATCH_PARENT, (int) stickersRowLayoutHeight).gravity(Gravity.LEFT | Gravity.CENTER_VERTICAL));
    DyLinearLayoutBuilder backspaceLayoutBuilder = DyLinearLayoutBuilder.init(context).layout(v -> v.wh(ViewHandler.MATCH_PARENT).gravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL));
    for (List<Stickers> stickersRowList : stickersPageList) {
      LinearLayout stickersRowLayout = stickersRowLayoutBuilder.build();  //每行表情布局
      for (Stickers stickers : stickersRowList) {
        if (stickers.getName().equals(DyConstants.BACKSPACE)) { //生成退格键布局
         DyImageView backspaceView = DyImageViewBuilder.init(context)
           .image(R.drawable.dy_ic_backspace)
           .layout(v -> v.wh(stickersSize).marginsX((int) stickersMarginX))
           .click(v -> stickersClickListener.click(stickersType, stickers.getName()))
           .build();
          stickers.setView(backspaceLayoutBuilder.build(backspaceView));
        }
        View stickersView = stickers.getView(context, stickersType, stickersSize, stickersMarginX); //生成表情VIEW
        if (null != stickersView) {
          if (null != stickersClickListener) {
            stickersView.setOnClickListener(v -> stickersClickListener.click(stickersType, stickers.getName())); //绑定点击事件
          }
          stickersRowLayout.addView(stickersView); //将一个表情add到行布局中
        }

      }
      stickersLayout.addView(stickersRowLayout);
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