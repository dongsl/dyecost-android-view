package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.databinding.BindingAdapter;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.eco.basics.handler.Num;
import com.eco.view.handler.ViewHandler;
import com.eco.view.builder.DyLinearLayoutBuilder;

import lombok.Getter;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 提示气泡
 * 1.在指定view上生成角标（例如imageView右上角的小红点）
 * 2.在指定view中生成提示气泡（例如消息列表中的未阅读数量）
 * -----------------demo-----------------
 * 1.BadgeView.initBadgeForCorner(view, text, size, click);
 * <p>
 * 2.<BadgeView
 * android:layout_width="50dp"
 * android:layout_height="26dp"
 * app:badgeText=''
 * app:textSize=''
 * app:onDragStateChangedListener=''
 * app:visibility='' />
 */
public class DyBadgeView extends View implements DyBaseView {

  @Getter
  public DyView dyView;
  private Badge badge;

  //参数
  private int type; //类型
  private OnDragStateChangedListener dragStateChangedListener;

  public DyBadgeView(Context context) {
    this(context, null);
  }

  public DyBadgeView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyBadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_badge_view);
      type = typedArray.getInt(R.styleable.dy_badge_view_dybv_type, DyViewState.DyBv.TEXT_POINT);
      typedArray.recycle();
    } else {
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    //如果存在则移出
    ViewGroup vp = ((ViewGroup) this.getParent());
    for (int i = 0; i < vp.getChildCount(); i++) {
      View viewChild = vp.getChildAt(i);
      if (viewChild instanceof QBadgeView) {
        vp.removeView(viewChild);
      }
    }
    badge = new QBadgeView(getContext()).bindTarget(this);
    initBadge(this, badge, dyView.text, dyView.textSize, null, dragStateChangedListener);
  }

  public DyBadgeView setType(int type) {
    if (this.type != type) {
      this.type = type;
      invalidate();
    }
    return this;
  }

  @BindingAdapter(value = {"dybv_drag_state_changed_listener"})
  public static void setDragStateChangedListener(DyBadgeView view, OnDragStateChangedListener listener) {
    view.setDragStateChangedListener(listener);
  }

  public DyBadgeView setDragStateChangedListener(OnDragStateChangedListener listener) {
    this.dragStateChangedListener = listener;
    return this;
  }

  /**
   * 生成角标
   * 根据目标view的间距与view的大小按比例生成 一个利用间距位置 展示悬浮气泡的布局
   * 1.生成一个id=badge_view_parent_layout的布局，将view移出原来的布局，添加到新布局中
   * 例如：
   * android:layout_marginRight="10dp"
   * android:layout_marginTop="10dp"
   * 使用目标view宽+右间距和高+上间距计算出 气泡布局宽高后 生成LinearLayout，将badge生成在与LinearLayout同级别布局下
   * 根据字体大小计与间距计算 badge实际超出目标view大小的像素，剩余的像素放到新生成LinearLayout布局间距中
   *
   * @param view
   * @param badgeText
   * @param textSize
   * @param listener  拖动红点后回调方法
   */
  public static void initBadgeForCorner(View view, Object badgeText, float textSize, OnDragStateChangedListener listener) {
    ViewGroup currentViewParent = ((ViewGroup) view.getParent()); //获取要添加 角标view 的父级
    Context context = currentViewParent.getContext();
    LinearLayout linearLayout = null;
    Badge badge = null;
    int currentViewParentCount = currentViewParent.getChildCount();
    for (int i = 0; i < currentViewParentCount; i++) {
      View currentViewParentChild = currentViewParent.getChildAt(i);
      if (currentViewParent.getId() == R.id.badge_view_parent_layout) {
        //验证当前view的id与重新生成布局的ID是否相同
        linearLayout = (LinearLayout) currentViewParent;
        ViewGroup badgeLayoutViewParent = (ViewGroup) linearLayout.getParent();
        for (int j = 0; j < badgeLayoutViewParent.getChildCount(); j++) {
          View badgeLayoutViewParentChild = badgeLayoutViewParent.getChildAt(j);
          if (badgeLayoutViewParentChild instanceof QBadgeView) { //找到当前布局下的角标VIEW
            badge = (QBadgeView) badgeLayoutViewParentChild;
            break;
          }
        }
      } else {
        if (currentViewParentChild == view) {
          if (i + 1 < currentViewParentCount) {
            View nextView = currentViewParent.getChildAt(i + 1);

            float verticalMargin = view.getTop(); //目标view距上方间距 垂直间距像素
            float topMargin;
            if (verticalMargin > textSize) {
              //如果垂直间距 大于 字体大小， 则使用字体大小 并 计算出剩余间距赋予布局
              topMargin = verticalMargin - textSize;
              verticalMargin = textSize;
            } else {
              topMargin = 0;
            }

            float horizontalMargin = nextView.getLeft() - view.getRight(); //目标view与右侧view 水平间距像素
            float rightMargin;
            float textSize70 = Num.mForF(textSize, 0.7f);
            if (horizontalMargin > textSize70) {
              //如果水平间距 大于 字体大小， 则使用字体大小 并 计算出剩余间距赋予布局
              rightMargin = horizontalMargin - textSize70;
              horizontalMargin = textSize70;
            } else {
              rightMargin = 0;
            }

            currentViewParent.removeView(view);//在目标view父级布局中移出 目标view（下面重新生成）
            ViewHandler.setLayoutParams(view, 0, 0, 0, 0); //移出目标view的所有间距
            float width = view.getLayoutParams().width + horizontalMargin; //布局宽度（目标VIEW宽度+水平间距）
            float height = view.getLayoutParams().height + verticalMargin; //布局高度（目标VIEW高度+垂直间距）
            linearLayout = DyLinearLayoutBuilder.init(context).orientation(LinearLayout.VERTICAL)
              .layout(v -> v.wh(width, height).margins(0, (int) topMargin, (int) rightMargin, 0).gravity(Gravity.LEFT | Gravity.BOTTOM))
              .build(view); //内容布局
            linearLayout.setId(R.id.badge_view_parent_layout);
            currentViewParent.addView(linearLayout, 0);
            badge = new QBadgeView(context).bindTarget(linearLayout); //将气泡放到布局里
            break;
          }
        }
      }
    }
    if (null == linearLayout || null == badge) return;
    //badgeText = new Random().nextInt(100);

    initBadge(linearLayout, badge, badgeText, textSize, Gravity.END | Gravity.TOP, listener);
  }

  /**
   * 生成提示气泡
   *
   * @param view      父view
   * @param badge     提示气泡view
   * @param badgeText 提示气泡显示内容
   * @param textSize  提示气泡内容字号
   * @param gravity   相对于父view的位置
   * @param listener  点击监听
   */
  public static void initBadge(View view, Badge badge, Object badgeText, float textSize, Integer gravity, OnDragStateChangedListener listener) {
    if (null == badge) {
      badge = new QBadgeView(view.getContext()).bindTarget(view);
    }
    if (null == badgeText) badgeText = "";

    ((View) badge).setVisibility(view.getVisibility());
    //生成提示气泡
    if (badgeText instanceof String) {
      badge.setBadgeText(badgeText.toString());
    } else {
      badge.setBadgeNumber(Integer.valueOf(badgeText.toString()));
      badge.setExactMode(Boolean.FALSE);
    }

    badge.setBadgeTextSize(textSize, false);
    if (null != gravity) {
      badge.setBadgeGravity(gravity); //设置Badge相对于TargetView的位置
    }
    badge.setShowShadow(Boolean.FALSE); //设置是否显示阴影

    //打开拖拽消除模式并设置监听
    if (null != listener) {
      badge.setOnDragStateChangedListener((state, b, targetView) -> {
        if (state == Badge.OnDragStateChangedListener.STATE_SUCCEED) {
          //Toast.makeText(view.getContext(), String.valueOf(1), Toast.LENGTH_SHORT).show();
          listener.onDragStateChanged(state, b, targetView);
        }
      });
    }
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((int) dyView.width, (int) dyView.height);
  }

  public interface OnDragStateChangedListener {
    void onDragStateChanged(int dragState, Badge badge, View targetView);
  }

  /**
   * view可见监听，隐藏或显示时触发(跳转关闭页面或被遮盖)
   *
   * @param visibility
   */
  /*@Override
  protected void onWindowVisibilityChanged(int visibility) {
    super.onWindowVisibilityChanged(visibility);
    if (null != badge) {
      ((View) badge).setVisibility(this.getVisibility());

      if (visibility == View.VISIBLE) {
        System.out.println(badge + ":显示-----------" + this.getVisibility());
      } else if (visibility == INVISIBLE || visibility == GONE) {
        System.out.println(badge + ":隐藏-----------" + this.getVisibility());
      }
    }
  }*/
  @Override
  public void setVisibility(int visibility) {
    super.setVisibility(visibility);
    if (null != badge) {
      ((View) badge).setVisibility(visibility);
    }
  }

}