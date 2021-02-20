package com.eco.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eco.view.DyBadgeView;
import com.eco.view.DyImageView;
import com.eco.view.DyIrregularButtonView;
import com.eco.view.R;
import com.eco.view.handler.ToastHandler;

public class Fragment4 extends Fragment {

  private Context context;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragement_main4, null);

    context = getContext();

    init();
    bindEvent();
    return view;
  }

  public void init() {

  }

  public void bindEvent() {

  }

  /**
   * 当前页面是否展示
   *
   * @param isVisibleToUser 显示为true， 不显示为false
   */
  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser) {

    }
  }
}