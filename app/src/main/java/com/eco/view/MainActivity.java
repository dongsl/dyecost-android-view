package com.eco.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.eco.view.alert.AlertCallBack;
import com.eco.view.alert.AlertHandler;
import com.eco.view.alert.AlertView;
import com.eco.view.handler.PopHandler;
import com.eco.view.handler.ToastHandler;

public class MainActivity extends AppCompatActivity {

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    context = this;
    success_button = findViewById(R.id.success_button);
    warning_button = findViewById(R.id.warning_button);
    error_button = findViewById(R.id.error_button);
    pop_button = findViewById(R.id.pop_button);
    alert_confirm_button = findViewById(R.id.alert_confirm_button);
    alert_bottom_confirm_button = findViewById(R.id.alert_bottom_confirm_button);
    alert_msg_button = findViewById(R.id.alert_msg_button);
    alert_dialog_button = findViewById(R.id.alert_dialog_button);
    alert_edit_button = findViewById(R.id.alert_edit_button);
    alert_list_button = findViewById(R.id.alert_list_button);
    alert_view_button = findViewById(R.id.alert_view_button);
    init();
    bindEvent();
  }

  public void init() {
    popHandler = new PopHandler(this);
    popHandler.init(R.layout.pop_plus);
  }

  public void bindEvent() {
    success_button.setOnClickListener(v -> ToastHandler.success(this, "成功"));
    warning_button.setOnClickListener(v -> ToastHandler.warning(this, "警告"));
    error_button.setOnClickListener(v -> ToastHandler.error(this, "失败"));
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


  }

}
