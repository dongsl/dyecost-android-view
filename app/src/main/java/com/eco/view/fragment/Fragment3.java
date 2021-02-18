package com.eco.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.DragAndDropPermissions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eco.view.DyBadgeView;
import com.eco.view.DyIrregularButtonView;
import com.eco.view.R;
import com.eco.view.handler.ToastHandler;

import q.rorbin.badgeview.Badge;

public class Fragment3 extends Fragment {

  private Context context;
  private DyIrregularButtonView ib_view;
  private DyBadgeView badge_view;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragement_main3, null);

    context = getContext();
    ib_view = view.findViewById(R.id.ib_view);
    badge_view = view.findViewById(R.id.badge_view);
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
  }
}