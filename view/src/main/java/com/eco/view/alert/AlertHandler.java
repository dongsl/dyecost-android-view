package com.eco.view.alert;


import android.content.Context;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.eco.basics.binding.EventBindingAdapter;
import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.Num;
import com.eco.basics.handler.StringHandler;
import com.eco.basics.handler.SystemHandler;
import com.eco.view.DyEditText;
import com.eco.view.DyViewState;
import com.eco.view.R;
import com.eco.view.builder.DyEditTextBuilder;
import com.eco.view.builder.DyLinearLayoutBuilder;
import com.eco.view.handler.ViewHandler;

public class AlertHandler {

  public static void confirm(Context context, String title, AlertCallBack alertCallBack) {
    confirm(context, title, null, Boolean.TRUE, alertCallBack);
  }

  public static void confirm(Context context, String title, String message, AlertCallBack alertCallBack) {
    confirm(context, title, message, Boolean.TRUE, alertCallBack);
  }

  public static void confirm(Context context, String title, String message, boolean showCancel, AlertCallBack alertCallBack) {
    new AlertView.Builder()
        .setContext(context)
        .setStyle(AlertView.Style.Alert)
        .setTitle(title)
        .setMessage(message)
        .setCancelText(showCancel ? StringHandler.getValue(context, R.string.cancel) : null)
        .setDestructive(StringHandler.getValue(context, R.string.confirm))
        .setOnItemClickListener(onItemClickListener(alertCallBack))
        .setOnDismissListener(onDismissListener(alertCallBack))
        .build()
        .show();
  }

  /**
   * 弹出消息框单按钮
   *
   * @param context
   * @param message
   * @param btnName
   * @param alertCallBack
   */
  public static void alertMsgForOne(Context context, String message, String btnName, AlertCallBack alertCallBack) {
    new AlertView.Builder()
        .setContext(context)
        .setStyle(AlertView.Style.Alert)
        .setMessage("\n" + message)
        .setDestructive(btnName)
        .setOnItemClickListener(onItemClickListener(alertCallBack))
        .build()
        .show();
  }

  public static void confirmBottom(Context context, String title, String btnName, AlertCallBack alertCallBack) {
    bottom(context, title, null, new String[]{btnName}, null, Boolean.TRUE, alertCallBack);
  }

  public static void bottom(Context context, String title, String destructive, AlertCallBack alertCallBack) {
    bottom(context, title, null, new String[]{destructive}, null, Boolean.TRUE, alertCallBack);
  }

  public static void bottom(Context context, String title, String[] destructive, AlertCallBack alertCallBack) {
    bottom(context, title, null, destructive, null, Boolean.TRUE, alertCallBack);
  }

  public static void bottom(Context context, String title, String[] destructive, String[] others, Boolean isCencel, AlertCallBack alertCallBack) {
    bottom(context, title, null, destructive, others, isCencel, alertCallBack);
  }

  public static void bottom(Context context, String title, String message, String[] destructive, String[] others, Boolean isCencel, AlertCallBack alertCallBack) {
    new AlertView.Builder()
        .setContext(context)
        .setStyle(AlertView.Style.ActionSheet)
        .setTitle(title)
        .setMessage(message)
        .setCancelText(isCencel ? StringHandler.getValue(context, R.string.cancel) : null)
        .setDestructive(destructive)
        .setOthers(others)
        .setOnItemClickListener(onItemClickListener(alertCallBack))
        .setOnDismissListener(onDismissListener(alertCallBack))
        .build()
        .show();
  }

  /**
   * 弹出dialog窗口
   *
   * @param context
   * @param view
   * @param isCancelable
   * @return
   */
  public static AlertView view(Context context, View view, boolean isCancelable) {
    AlertView alertView = new AlertView.Builder()
        .setContext(context)
        .setStyle(AlertView.Style.AlertView)
        .build();
    alertView.addView(view).setCancelable(isCancelable);
    return alertView;
  }

  /**
   * 弹出编辑框
   *
   * @param context
   * @param title         弹出标题
   * @param content       编辑框内容
   * @param inMaxLength   最大输入长度
   * @param inMaxLine     最大数据行数
   * @param alertCallBack
   */
  public static void editText(Context context, String title, String content, Integer inMaxLength, Integer inMaxLine, AlertCallBack alertCallBack) {
    inMaxLength = null == inMaxLength ? Num.getInt(context, R.integer.in_default_max_length) : inMaxLength; //最大字数
    inMaxLine = null == inMaxLine ? Num.getInt(context, R.integer.in_default_max_line) : inMaxLine; //最大行数


    DyEditText editText = DyEditTextBuilder.init(context)
        .layout(v -> v
            .wh(ViewHandler.MATCH_PARENT, ViewHandler.WRAP_CONTENT)
            .margins(DensityHandler.getDimenPxInt(context, R.dimen.alert_edit_margin))
            .padding(DensityHandler.getDimenPxInt(context, R.dimen.alert_edit_padding))
            .shape(null, DyViewState.SHAPE_TYPE.FILLET.v(), DensityHandler.getDimenPx(context, R.dimen.alert_edit_fillet), null)
            .border(DensityHandler.getDimenPx(context, R.dimen.alert_edit_border), ContextCompat.getColor(context, R.color.alert_edit_border))
            .gravity(Gravity.TOP | Gravity.LEFT)
            .bgColor(ContextCompat.getColor(context, R.color.transparent))
            .text(content)
        ).removeUnderline(Boolean.TRUE).build();

    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inMaxLength)});
    editText.setMaxLines(inMaxLine);
    if (1 == inMaxLine) {
      editText.setSingleLine(Boolean.TRUE);
    } else if (inMaxLine >= 3) {
      editText.setMinLines(3);
    }

    editText.setFocusable(Boolean.TRUE);
    editText.setFocusableInTouchMode(Boolean.TRUE);
    //editText.requestFocus();
    SystemHandler.showSoftKeyboard(editText);
    new AlertView.Builder()
        .setContext(context)
        .setStyle(AlertView.Style.Alert)
        .setTitle(title)
        .setCancelText(StringHandler.getValue(context, R.string.cancel))
        .setOthers(StringHandler.getValue(context, R.string.complete))
        .setOnItemClickListener(onItemClickListener(alertCallBack, editText))
        .setOnDismissListener(onDismissListener(alertCallBack))
        .build()
        .addView(DyLinearLayoutBuilder.init(context).orientation(LinearLayout.VERTICAL).layout(v -> v.whm().gravity(Gravity.CENTER)).build(editText))
        .setCancelable(true)
        .show();
  }

  private static AlertView.OnItemClickListener onItemClickListener(AlertCallBack alertCallBack) {
    return onItemClickListener(alertCallBack, null);
  }

  private static AlertView.OnItemClickListener onItemClickListener(AlertCallBack alertCallBack, EditText editText) {
    return (o, position) -> {
      EventBindingAdapter.eventInterval(alertCallBack.getClass() + "onClick", () -> alertCallBack.onClick(position));
      if (position == AlertView.CANCELPOSITION) {
        EventBindingAdapter.eventInterval(alertCallBack.getClass() + "onCancel", () -> alertCallBack.onCancel());
      } else if (0 == position) {
        EventBindingAdapter.eventInterval(alertCallBack.getClass() + "onFirst", () -> {
          if (null == editText) {
            alertCallBack.onFirst();
          } else {
            alertCallBack.onFirst(editText.getText().toString());
          }
        });
      } else if (1 == position) {
        EventBindingAdapter.eventInterval(alertCallBack.getClass() + "onSecond", () -> alertCallBack.onSecond());
      }
    };
  }

  public static AlertView.OnDismissListener onDismissListener(AlertCallBack alertCallBack) {
    return o -> alertCallBack.onDismiss();
  }
}
