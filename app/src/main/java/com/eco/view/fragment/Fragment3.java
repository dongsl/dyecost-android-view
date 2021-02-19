package com.eco.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.DragAndDropPermissions;
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

import q.rorbin.badgeview.Badge;

public class Fragment3 extends Fragment {

  private Context context;
  private DyIrregularButtonView ib_view;
  private DyBadgeView badge_view;
  private ImageView badge_corner_view;
  private DyImageView shop_view;
  private DyImageView rucksack_view;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragement_main3, null);

    context = getContext();
    ib_view = view.findViewById(R.id.ib_view);
    badge_view = view.findViewById(R.id.badge_view);
    badge_corner_view = view.findViewById(R.id.badge_corner_view);
    shop_view = view.findViewById(R.id.shop_view);
    rucksack_view = view.findViewById(R.id.rucksack_view);
    init();
    bindEvent();
    return view;
  }

  public void init() {

  }

  public void bindEvent() {
    ib_view.setClickListener(new DyIrregularButtonView.ClickListener() {
      @Override
      public void oneClick() {
        ToastHandler.success(getContext(), "按钮1");
      }

      @Override
      public void twoClick() {
        ToastHandler.success(getContext(), "按钮2");
      }

      @Override
      public void threeClick() {
        ToastHandler.success(getContext(), "按钮3");
      }
    });

    badge_view.setDragStateChangedListener((dragState, badge, targetView) -> ToastHandler.success(getContext(), "已阅"));
    shop_view.setOnClickListener(v -> ToastHandler.success(getContext(), "打开商城"));
    rucksack_view.setOnClickListener(v -> ToastHandler.success(getContext(), "打开背包"));
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
      DyBadgeView.initBadgeForCorner(badge_corner_view, 10, 20, (dragState, badge, targetView) -> ToastHandler.success(getContext(), "角标已阅"));
    }
  }
}