package com.eco.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eco.view.R;
import com.eco.view.alert.AlertCallBack;
import com.eco.view.alert.AlertHandler;
import com.eco.view.alert.AlertView;
import com.eco.view.handler.PopHandler;
import com.eco.view.handler.ToastHandler;
import com.eco.view.loading.DyLoadingView;
import com.eco.view.loading.LoadingDialog;

public class Fragment2 extends Fragment {

  private Context context;
  //pop, toast
  private Button success_button;
  private Button warning_button;
  private Button error_button;
  private Button pop_button;
  private PopHandler popHandler;
  //alert
  public Button alert_confirm_button;
  public Button alert_bottom_confirm_button;
  public Button alert_msg_button;
  public Button alert_dialog_button;
  public Button alert_edit_button;
  public Button alert_list_button;
  public Button alert_view_button;
  //loading
  public Button loading_default_button;
  public Button loading_style_button;
  public Button loading_style_red_button;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragement_main2, null);

    context = getContext();
    success_button = view.findViewById(R.id.success_button);
    warning_button = view.findViewById(R.id.warning_button);
    error_button = view.findViewById(R.id.error_button);
    pop_button = view.findViewById(R.id.pop_button);
    alert_confirm_button = view.findViewById(R.id.alert_confirm_button);
    alert_bottom_confirm_button = view.findViewById(R.id.alert_bottom_confirm_button);
    alert_msg_button = view.findViewById(R.id.alert_msg_button);
    alert_dialog_button = view.findViewById(R.id.alert_dialog_button);
    alert_edit_button = view.findViewById(R.id.alert_edit_button);
    alert_list_button = view.findViewById(R.id.alert_list_button);
    alert_view_button = view.findViewById(R.id.alert_view_button);
    loading_default_button = view.findViewById(R.id.loading_default_button);
    loading_style_button = view.findViewById(R.id.loading_style_button);
    loading_style_red_button = view.findViewById(R.id.loading_style_red_button);
    init();
    bindEvent();
    return view;
  }

  public void init() {
    popHandler = new PopHandler(getActivity());
    popHandler.init(R.layout.pop_plus);
  }

  public void bindEvent() {
    success_button.setOnClickListener(v -> ToastHandler.success(context, "成功"));
    warning_button.setOnClickListener(v -> ToastHandler.warning(context, "警告"));
    error_button.setOnClickListener(v -> ToastHandler.error(context, "失败"));
    pop_button.setOnClickListener(v -> popHandler.show(v));

    alert_confirm_button.setOnClickListener(v -> AlertHandler.confirm(context, "是否下载", new AlertCallBack() {
      @Override
      public void onFirst() {
        ToastHandler.success(context, "下载成功");
      }

      @Override
      public void onCancel() {
        ToastHandler.error(context, "下载失败");
      }
    }));

    alert_bottom_confirm_button.setOnClickListener(v -> AlertHandler.confirmBottom(context, "确认要下载文件吗", "确认下载", new AlertCallBack() {
      @Override
      public void onFirst() {
        ToastHandler.success(context, "下载成功");
      }

      @Override
      public void onCancel() {
        ToastHandler.error(context, "下载失败");
      }
    }));

    alert_msg_button.setOnClickListener(v -> AlertHandler.alertMsgForOne(context, "您的帐号在其它地方登录，您已被迫下线", "重新登录", new AlertCallBack() {
      @Override
      public void onFirst() {
        ToastHandler.success(context, "登录成功");
      }
    }));

    alert_dialog_button.setOnClickListener(v -> {
      View view = LayoutInflater.from(context).inflate(R.layout.layout_alert, null);
      AlertHandler.view(context, view, Boolean.TRUE).show();
    });

    alert_edit_button.setOnClickListener(v -> AlertHandler.editText(context, "昵称", "dyecost", 10, 1, new AlertCallBack() {
      @Override
      public void onFirst(String content) {
        ToastHandler.success(context, "修改成功:" + content);
      }
    }));

    alert_list_button.setOnClickListener(v -> {
      new AlertView.Builder()
          .setContext(context)
          .setStyle(AlertView.Style.ActionSheet)
          .setTitle("列表弹框")
          .setDestructive("按钮1", "按钮2")
          .setOthers("其他按钮1", "其他按钮2", "其他按钮3")
          .setCancelText("确认")
          .setOnItemClickListener((o, position) -> {
            ToastHandler.success(context, o + ":" + position);
          })
          .build()
          .show();
    });

    alert_view_button.setOnClickListener(v -> new AlertView.Builder()
        .setContext(context)
        .setStyle(AlertView.Style.Alert)
        .setTitle("自定义弹框")
        .setMessage("可选择弹出AlertView.Style.*, 设置标题,描述,按钮")
        .setCancelText("确认")
        .build()
        .show());

    loading_default_button.setOnClickListener(v -> LoadingDialog.init(context).text("正在进入房间").show());
    loading_style_button.setOnClickListener(v -> LoadingDialog.init(context).style("SemiCircleSpinIndicator", Color.WHITE).text("正在进入房间").show());
    loading_style_red_button.setOnClickListener(v -> LoadingDialog.init(context).style("SemiCircleSpinIndicator", Color.RED).text("正在进入房间").show());


  }
}