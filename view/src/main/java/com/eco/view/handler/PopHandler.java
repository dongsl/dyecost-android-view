package com.eco.view.handler;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.eco.anim.animator.DyAnimator;
import com.eco.anim.animator.DyAnimatorBuilder;

/**
 * 弹框
 */
public class PopHandler {

  private View popView;
  private DyAnimator dyAnimator;
  private PopupWindow popupWindow;
  private Activity activity;
  private float bgAlpha = 1f;
  private boolean bright = false;
  private static final float START_ALPHA = 0.8f;
  private static final float END_ALPHA = 1f;

  public PopHandler(Activity activity) {
    this.activity = activity;
  }

  public void init(int viewId) {
    init(viewId, com.eco.basics.R.style.pop_plus, 300l);
  }

  public void init(int viewId, Long duration) {
    init(viewId, com.eco.basics.R.style.pop_plus, duration);
  }

  /**
   * @param viewId   弹出来的layout
   * @param styleId  弹出和隐藏的样式
   * @param duration
   */
  public void init(int viewId, int styleId, Long duration) {
    popupWindow = new PopupWindow(activity);
    //弹框显示和隐藏动画
    dyAnimator = DyAnimatorBuilder.animator()
        .param(v -> v
            .duration(duration)
            .updateListener((progress, value) -> {
              //此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
              bgAlpha = bright ? progress : (START_ALPHA + END_ALPHA - progress);
              backgroundAlpha(bgAlpha);
            }).endListener(() -> bright = !bright)
        ).buildValue(START_ALPHA, END_ALPHA);

    // 设置布局文件
    popupWindow.setContentView(LayoutInflater.from(activity).inflate(viewId, null));
    // 为了避免部分机型不显示，我们需要重新设置一下宽高
    popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
    popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
    popupWindow.setFocusable(true); //false可能会导致 点击外侧不消失， true时点击任何位置 都不会传递到副页面上
    // 设置pop可点击，为false点击事件无效，默认为true
    popupWindow.setTouchable(true);
    // 设置pop关闭监听，用于改变背景透明度
    popupWindow.setOnDismissListener(() -> dyAnimator.start());
    // 设置pop透明效果
    popupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
    // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
    popupWindow.setOutsideTouchable(true);
    // 设置pop出入动画
    popupWindow.setAnimationStyle(styleId);
    popView = popupWindow.getContentView();
  }

  public View show(View view) {
    dyAnimator.start();
    popupWindow.showAsDropDown(view, -100, 0); //显示弹框，相对于 + 号正下面，同时可以设置偏移量
    return popView;
  }

  public void hide() {
    // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
    popupWindow.dismiss();
  }

  /**
   * 此方法用于改变背景的透明度，从而达到“变暗”“变亮”的效果
   */
  private void backgroundAlpha(float bgAlpha) {
    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
    // 0.0-1.0
    lp.alpha = bgAlpha;
    activity.getWindow().setAttributes(lp);
    // everything behind this window will be dimmed.
    // 此方法用来设置浮动层，防止部分手机变暗无效
    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
  }
}
