package com.eco.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eco.basics.handler.SystemHandler;
import com.eco.view.DyBadgeView;
import com.eco.view.DyImageView;
import com.eco.view.DyIrregularButtonView;
import com.eco.view.DyProgressBarView;
import com.eco.view.R;
import com.eco.view.handler.ToastHandler;

public class Fragment4 extends Fragment {

  private Context context;
  private TextView pb_start;
  private DyProgressBarView down_time_pb;
  private DyProgressBarView down_time_pb1;
  private DyProgressBarView down_time_pb2;
  private TextView pb_start2;
  private DyProgressBarView exp_pb;
  private DyProgressBarView exp_image_pb;
  private DyProgressBarView loading_pb;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragement_main4, null);

    context = getContext();
    pb_start = view.findViewById(R.id.pb_start);
    down_time_pb = view.findViewById(R.id.down_time_pb);
    down_time_pb1 = view.findViewById(R.id.down_time_pb1);
    down_time_pb2 = view.findViewById(R.id.down_time_pb2);
    pb_start2 = view.findViewById(R.id.pb_start2);
    exp_pb = view.findViewById(R.id.exp_pb);
    exp_image_pb = view.findViewById(R.id.exp_image_pb);
    loading_pb = view.findViewById(R.id.loading_pb);

    init();
    bindEvent();
    return view;
  }

  public void init() {

  }

  public void bindEvent() {
    pb_start.setOnClickListener(v -> {
      exp_pb.reset(0);
      exp_pb.setForGradual(100);
      exp_image_pb.reset(0);
      exp_image_pb.setForGradual(100);
      loading_pb.reset(0);

      SystemHandler.handlerPostDelayed(new Runnable() {
        String[] text = {"加载图片中", "加载数据中", "下载资源中", "加载即将完成", "进入中"};
        int i = 0;

        @Override
        public void run() {
          if (i < 5) {
            loading_pb.setForGradual(text[i], 20);
            i++;
            SystemHandler.handlerPostDelayed(this, 2000);
          }
        }
      }, 10);
    });

    pb_start2.setOnClickListener(v -> {
      down_time_pb.startCountDownTimer(10l, 10l);
      down_time_pb1.reset(0);
      down_time_pb1.setForGradual(100);
      down_time_pb2.startCountDownTimer(0l, 10l);
    });
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