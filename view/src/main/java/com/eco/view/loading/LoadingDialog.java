package com.eco.view.loading;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eco.view.R;

public class LoadingDialog {

  private DyLoadingView loadingView;
  private TextView loadingText;
  private Dialog loadingDialog;
  private int color;

  private LoadingDialog(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.layout_loading, null);

    loadingView = view.findViewById(R.id.loading_view);
    loadingText = view.findViewById(R.id.loading_text);
    loadingDialog = new Dialog(context, R.style.loadingDialog);
    loadingDialog.setCancelable(false);
    loadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

    //按键监听
    loadingDialog.setOnKeyListener((dialog, keyCode, event) -> {
      if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
        hide();
        return true;
      }
      return false;
    });
  }

  public static LoadingDialog init(Context context) {
    return new LoadingDialog(context);
  }

  public LoadingDialog style(String indicatorName, int color) {
    loadingView.setIndicator(indicatorName);
    loadingView.setIndicatorColor(color);
    return this;
  }

  public LoadingDialog text(String text) {
    loadingText.setText(text);
    return this;
  }


  public LoadingDialog show() {
    loadingView.smoothToShow();
    loadingDialog.show();
    return this;
  }

  public LoadingDialog hide() {
    loadingDialog.dismiss();
    loadingDialog.hide();
    loadingView.smoothToHide();
    return this;
  }
}