package com.eco.view.disc.extend;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.eco.view.R;
import com.eco.basics.handler.DensityHandler;
import com.eco.view.builder.DyImageViewBuilder;
import com.eco.view.disc.extend.model.Extend;
import com.eco.view.disc.extend.view.ExtendView;

import java.util.List;

/**
 * 扩展盘
 */
public class ExtendLayout extends LinearLayout {

  private FragmentManager fm;
  private List<Extend> extendList; //扩展盘列表
  private ExtendView.ExtendClickListener extendClickListener; //点击事件监听

  public ExtendView extendView; //扩展盘View
  private LinearLayout pagePointLayout; //分页点布局

  private float layoutHeight; //整体布局高度
  private float extendViewHeight; //扩展盘内容布局高度

  public ExtendLayout(@NonNull Context context) {
    super(context);
    init(context);
  }

  public ExtendLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.layout_extend, null);
    addView(view);
    extendView = view.findViewById(R.id.extend_view);
    pagePointLayout = view.findViewById(R.id.page_point_layout);

    layoutHeight = DensityHandler.getDimenPx(context, R.dimen.disc_height);
    extendViewHeight = layoutHeight - DensityHandler.getDimensPx(context, R.dimen.disc_page_point_size, R.dimen.layout_margin_top, R.dimen.layout_margin_bottom); //盘高度 - 分页点高度
  }

  public ExtendLayout init(FragmentManager fm) {
    this.fm = fm;
    return this;
  }

  /**
   * 设置扩展盘内容
   *
   * @param extendList
   * @return
   */
  public ExtendLayout addExtend(List<Extend> extendList) {
    this.extendList = extendList;
    return this;
  }

  /**
   * 扩展按钮点击事件监听
   *
   * @param extendClickListener
   * @return
   */
  public ExtendLayout addExtendClickListener(ExtendView.ExtendClickListener extendClickListener) {
    this.extendClickListener = extendClickListener;
    return this;
  }

  /**
   * 生成表情盘
   */
  public void build() {
    Context context = getContext();
    extendView.getLayoutParams().height = (int) extendViewHeight;

    extendView.init(fm) //初始化
      .addExtendClickListener(extendClickListener)
      .addPageSelectedListener(currentPage -> { //翻页监听, 每翻一页就重新生成一次 分页点布局
        //生成表情分页点布局
        pagePointLayout.removeAllViews();
        for (int i = 0; i < extendView.getPageCount(); i++) {
          int wh = R.dimen.disc_page_point_size, drawable = currentPage == i + 1 ? R.drawable.dy_ic_point_on : R.drawable.dy_ic_point_off;
          final int itemIndex = i; //页面的实际索引
          DyImageViewBuilder pagePoint = DyImageViewBuilder.init(context).image(drawable)
            .layout(v -> v.wh((int) DensityHandler.getDimenPx(context, wh)))
            .click(v -> extendView.setCurrentItem(itemIndex));
          pagePointLayout.addView(pagePoint.build());
        }
      });

    //生成扩展盘
    extendView.build(extendList, extendViewHeight);

    if (null != extendView) { //触发翻页监听，生成分页点布局
      extendView.getPageSelectedListener().onPageSelected(extendView.getCurrentPage());
    }
  }
}