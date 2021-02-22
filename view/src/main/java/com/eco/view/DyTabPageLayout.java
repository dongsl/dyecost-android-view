package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.eco.basics.binding.EventBindingAdapter;
import com.eco.basics.handler.DensityHandler;
import com.eco.basics.log.Logger;
import com.eco.view.builder.DyLinearLayoutBuilder;
import com.eco.view.builder.DyTextViewBuilder;

import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

/**
 * 标签布局
 */
public class DyTabPageLayout extends LinearLayout implements DyBaseView {

  private final String TAG = "DyTabPageLayout";

  @Getter
  public DyView dyView;

  //参数
  private boolean lrScroll = Boolean.TRUE; //左右滑动
  private int currentItem; //当前页 从0开始
  private int loadPageLimit; //加载页数限制
  private Float titleWidth; //标题宽度
  private Float titleHeight; //标题高度
  private Float titleWeight; //标题weight
  private float titleTextSize; //文字字号
  private float titleMarginsTop; //标题间距
  private float titleMarginsBottom; //标题间距
  private float titleMarginsLeft; //标题间距
  private float titleMarginsRight; //标题间距

  private TabClickListener tabClickListener; //标签点击事件

  //生成
  private FragmentManager fm;
  private LinearLayout tabPageTitleLayout;
  private DyViewPager tabPageViewPager;
  private LinearLayout tabPageVpHeaderLayout; //viewPager头布局
  private LinearLayout tabPageVpFooterLayout; //viewPager页脚布局

  private List<Fragment> fragmentList;
  private String[] titleTexts; //标签标题
  private View tabPageVpHeaderView; //viewPager头view
  private View tabPageVpFooterView; //viewPager页脚view


  public DyTabPageLayout(Context context) {
    this(context, null);
  }

  public DyTabPageLayout(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DyTabPageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    dyView = new DyView(this, context, attrs);
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.layout_tab_page, null);
    addView(view);
    tabPageTitleLayout = view.findViewById(R.id.tab_page_title_layout);
    tabPageViewPager = view.findViewById(R.id.tab_page_view_pager);
    tabPageVpHeaderLayout = view.findViewById(R.id.tab_page_vp_header_layout);
    tabPageVpFooterLayout = view.findViewById(R.id.tab_page_vp_footer_layout);

    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_tab_page_view);
      lrScroll = typedArray.getBoolean(R.styleable.dy_tab_page_view_dytpv_lr_scroll, Boolean.TRUE); //左右滑动
      currentItem = typedArray.getInteger(R.styleable.dy_tab_page_view_dytpv_current_item, 0); //当前页 从0开始
      loadPageLimit = typedArray.getInteger(R.styleable.dy_tab_page_view_dytpv_load_page_limit, 0); //加载页数限制
      titleTextSize = typedArray.getDimension(R.styleable.dy_tab_page_view_dytpv_title_text_size, DensityHandler.getDimenPx(context, R.dimen.text_size12)); //文字字号
      titleMarginsTop = typedArray.getDimension(R.styleable.dy_tab_page_view_dytpv_title_margins_top, 0); //标题间距
      titleMarginsBottom = typedArray.getDimension(R.styleable.dy_tab_page_view_dytpv_title_margins_bottom, 0); //标题间距
      titleMarginsLeft = typedArray.getDimension(R.styleable.dy_tab_page_view_dytpv_title_margins_left, 0); //标题间距
      titleMarginsRight = typedArray.getDimension(R.styleable.dy_tab_page_view_dytpv_title_margins_right, 0); //标题间距
      typedArray.recycle();
    } else {
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
  }

  public DyTabPageLayout init(FragmentManager fm) {
    this.fm = fm;
    return this;
  }

  /**
   * 生成标签页
   */
  public void build() {
    if (null == titleTexts || titleTexts.length == 0) {
      Logger.e(TAG, "标签标题view为空");
      return;
    }
    Context context = getContext();
    dyView.setRadioGroupId(R.id.tab_page_title_layout);

    //生成标签标题布局
    DyLinearLayoutBuilder tabTitleLayoutBuilder = DyLinearLayoutBuilder.init(context)
      .layout(v -> v
        .wh(titleWidth, titleHeight).weight(titleWeight).gravity(Gravity.BOTTOM)
        .shape(null, dyView.shape, dyView.filletRadius, dyView.filletDirection)
        .bgColor(dyView.bgColor, dyView.bgColorAlpha)
      );
    //生成标签标题
    DyTextViewBuilder tabTitleTextViewBuilder = DyTextViewBuilder.init(context)
      .layout(v -> v
        .whm()
        .textColor(ContextCompat.getColor(context, R.color.white))
        .textSize(titleTextSize).textBold(Boolean.TRUE)
        .margins((int) titleMarginsLeft, (int) titleMarginsTop, (int) titleMarginsRight, (int) titleMarginsBottom)
        .gravity(Gravity.CENTER)
        .shape(null, dyView.shape, dyView.filletRadius, dyView.filletDirection)
        .down(dyView.downStyle, dyView.downColor, dyView.downColorAlpha, null)
        .selected(dyView.openSelected, dyView.selected, dyView.radioGroupId)
      );

    //添加标签标题
    tabPageTitleLayout.removeAllViews();
    int index = 0;
    for (String tabTitle : titleTexts) {
      DyTextView tabTitleTextView = tabTitleTextViewBuilder.layout(v -> v.text(tabTitle)).build();
      if (index == 0) tabTitleTextView.getDyView().setSelected(Boolean.TRUE);

      int finalIndex = index;
      EventBindingAdapter.clickInterval(tabTitleTextView, v -> {
        currentItem = finalIndex;
        tabPageViewPager.setCurrentItem(currentItem);
        if (null != tabClickListener) {
          tabClickListener.click(v, currentItem);
        }
      }, 0l); //绑定点击事件

      tabPageTitleLayout.addView(tabTitleLayoutBuilder.build(tabTitleTextView));
      index++;
    }
    //添加vp头
    tabPageVpHeaderLayout.removeAllViews();
    if (null != tabPageVpHeaderView) tabPageVpHeaderLayout.addView(tabPageVpHeaderView);
    //添加vp页脚
    tabPageVpFooterLayout.removeAllViews();
    if (null != tabPageVpFooterView) tabPageVpFooterLayout.addView(tabPageVpFooterView);

    //设置vp
    tabPageViewPager.setLrScroll(lrScroll);
    if (CollectionUtils.isNotEmpty(fragmentList)) {
      ShopAdapter adapter = new ShopAdapter(fm, fragmentList);
      tabPageViewPager.setAdapter(adapter);
      tabPageViewPager.setCurrentItem(currentItem);
      tabPageViewPager.setOffscreenPageLimit(loadPageLimit);
    }
  }


  public DyTabPageLayout setStyle(DyView.DyViewStyle dyViewStyle) {
    dyViewStyle.set(dyView);
    return this;
  }


  public DyTabPageLayout setTitleTexts(String... titleTexts) {
    this.titleTexts = titleTexts;
    return this;
  }

  /**
   *
   * @param titleTextSize px
   * @return
   */
  public DyTabPageLayout setTitleTextSize(float titleTextSize) {
    this.titleTextSize = titleTextSize;
    return this;
  }

  public DyTabPageLayout setTitleWh(Float w, Float h) {
    if (null != w) this.titleWidth = w;
    if (null != h) this.titleHeight = h;
    return this;
  }

  public DyTabPageLayout setTitleWeight(Float weight) {
    this.titleWeight = weight;
    return this;
  }


  public DyTabPageLayout setLrScroll(boolean lrScroll) {
    this.lrScroll = lrScroll;
    return this;
  }

  public DyTabPageLayout setCurrentItem(int currentItem) {
    this.currentItem = currentItem;
    return this;
  }

  public DyTabPageLayout setLoadPageLimit(int loadPageLimit) {
    this.loadPageLimit = loadPageLimit;
    return this;
  }

  public DyTabPageLayout setFragments(Fragment... fragments) {
    if (null == fragments || fragments.length == 0) return this;
    this.fragmentList = Arrays.asList(fragments);
    return this;
  }

  public DyTabPageLayout setVpHeader(View tabPageVpHeaderView) {
    this.tabPageVpHeaderView = tabPageVpHeaderView;
    return this;
  }

  public DyTabPageLayout setVpFooter(View tabPageVpFooterView) {
    this.tabPageVpFooterView = tabPageVpFooterView;
    return this;
  }

  public DyTabPageLayout setTitleMargins(Float ml, Float mt, Float mr, Float mb) {
    if (null != ml) this.titleMarginsLeft = ml;
    if (null != mt) this.titleMarginsTop = mt;
    if (null != mr) this.titleMarginsRight = mr;
    if (null != mb) this.titleMarginsBottom = mb;
    return this;
  }

  public DyTabPageLayout setTabClickListener(TabClickListener tabClickListener) {
    this.tabClickListener = tabClickListener;
    return this;
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((int) dyView.maxWidth, (int) dyView.maxHeight);
  }

  public class ShopAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> contentFragmentList;
    public FragmentManager fm;

    public ShopAdapter(FragmentManager fm, List<Fragment> contentFragmentList) {
      super(fm);
      this.fm = fm;
      this.contentFragmentList = contentFragmentList;
      notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
      return PagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
      return contentFragmentList.get(position);
    }

    @Override
    public int getCount() {
      return contentFragmentList.size();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
  }

  //标签点击事件
  public interface TabClickListener {
    void click(View v, int position);
  }

}