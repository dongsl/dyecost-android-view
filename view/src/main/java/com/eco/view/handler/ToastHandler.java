package com.eco.view.handler;

import android.content.Context;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ToastHandler {
  private static Toast lastToast;

  public static void info(Context context, String content, Integer gravity) {
    show(gravity, Toasty.info(context, content, Toast.LENGTH_LONG, false));
  }

  public static void normal(Context context, String content) {
    normal(context, content, null);
  }

  public static void normal(Context context, String content, Integer gravity) {
    show(gravity, Toasty.normal(context, content));
  }

  public static void success(Context context, String content) {
    success(context, content, null);
  }

  public static void success(Context context, String content, Integer gravity) {
    show(gravity, Toasty.success(context, content, Toast.LENGTH_LONG, true));
  }

  public static void warning(Context context, String content) {
    warning(context, content, null);
  }

  public static void warning(Context context, String content, Integer gravity) {
    show(gravity, Toasty.warning(context, content, Toast.LENGTH_LONG, true));
  }

  public static void error(Context context, String content) {
    error(context, content, null);
  }

  public static void error(Context context, String content, Integer gravity) {
    show(gravity, Toasty.error(context, content, Toast.LENGTH_LONG, true));
  }

  public static void custom(Context context, String content, Integer gravity) {
    //show(gravity, Toasty.custom(context, content, Toast.LENGTH_SHORT, true));
  }

  public static void show(Integer gravity, Toast toast) {
    if(null != lastToast) lastToast.cancel();
    lastToast = toast;
    if (null != gravity) {
      lastToast.setGravity(gravity, 50, 120);
    }
    lastToast.show();
  }

}
