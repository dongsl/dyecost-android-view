package com.eco.view.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eco.basics.handler.DensityHandler;
import com.eco.view.R;

import java.util.ArrayList;
import java.util.List;

public class BottomNavBarView extends LinearLayout {


  private RelativeLayout addContainerLayout;
  private int tabCount = 0; //Tab数量

  private RelativeLayout bottomNavContainerLayout; //容器布局
  private LinearLayout bottomNavTabLayout; //底部导航标签列表布局

  //分割线
  private View lineView;

  //红点集合
  private List<View> hintPointList = new ArrayList<>();

  //消息数量集合
  private List<TextView> msgPointList = new ArrayList<>();

  //底部Image集合
  private List<ImageView> imageViewList = new ArrayList<>();

  //底部Text集合
  private List<TextView> textViewList = new ArrayList<>();

  //底部TabLayout（除中间加号）
  private List<View> tabList = new ArrayList<>();

  private BottomNavViewPager bottomNavViewPager;
  //private GestureDetector detector;

  private ViewGroup addViewLayout;


  //文字集合
  private String[] titleItems;
  //未选择 图片集合
  private int[] normalIconItems;
  //已选择 图片集合
  private int[] selectIconItems;
  //fragment集合
  private List<Fragment> fragmentList = new ArrayList<>();

  private FragmentManager fragmentManager;

  //Tab点击动画效果
  private Techniques anim;
  //ViewPager切换动画
  private boolean smoothScroll = false;
  //图标大小
  private int iconSize = 20;

  //提示红点大小
  private float hintPointSize = 6;
  //提示红点距Tab图标右侧的距离
  private float hintPointLeft = -3;
  //提示红点距图标顶部的距离
  private float hintPointTop = -3;

  private BottomNavBarView.OnTabClickListener onTabClickListener;
  //消息红点字体大小
  private float msgPointTextSize = 9;
  //消息红点大小
  private float msgPointSize = 18;
  //消息红点距Tab图标右侧的距离   默认为Tab图标的一半
  private float msgPointLeft = -10;
  //消息红点距图标顶部的距离  默认为Tab图标的一半
  private float msgPointTop = -10;
  //Tab文字距Tab图标的距离
  private float tabTextTop = 2;
  //Tab文字大小
  private float tabTextSize = 12;
  //未选中Tab字体颜色
  private int normalTextColor = Color.parseColor("#666666");
  //选中字体颜色
  private int selectTextColor = Color.parseColor("#333333");
  //分割线高度
  private float lineHeight = 1;
  //分割线颜色
  private int lineColor = Color.parseColor("#f7f7f7");

  private int navigationBackground = Color.parseColor("#ffffff");
  private float navigationHeight = 60;

  private ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_INSIDE;

  private boolean canScroll;
  private BottomNavViewPagerAdapter adapter;


  //Add
  private BottomNavBarView.OnAddClickListener onAddClickListener;
  private float addIconSize = 36;
  private float addLayoutHeight = navigationHeight;
  public static final int MODE_NORMAL = 0;
  public static final int MODE_ADD = 1;
  public static final int MODE_ADD_VIEW = 2;

  private float addLayoutBottom = 10;

  //RULE_CENTER 居中只需调节addLayoutHeight 默认和navigationHeight相等 此时addLayoutBottom属性无效
  //RULE_BOTTOM addLayoutHeight属性无效、自适应、只需调节addLayoutBottom距底部的距离
  private int addLayoutRule = RULE_BOTTOM;

  public static final int RULE_cENTER = 0;
  public static final int RULE_BOTTOM = 1;

  //true  ViewPager在Navigation上面
  //false  ViewPager和Navigation重叠
  private boolean hasPadding = true;


  //1、普通的Tab 2、中间带按钮（如加号）3、
  private int mode;

  //true 点击加号切换fragment
  //false 点击加号不切换fragment进行其他操作（跳转界面等）
  private boolean addAsFragment = false;
  //自定义加号view
  private View customAddView;
  private float addTextSize;
  //加号文字未选中颜色（默认同其他tab）
  private int addNormalTextColor;
  //加号文字选中颜色（默认同其他tab）
  private int addSelectTextColor;
  //加号文字距离顶部加号的距离
  private float addTextTopMargin = 3;
  //是否和其他tab文字底部对齐
  private boolean addAlignBottom = true;
  private ImageView addImage;
  private View empty_line;


  public BottomNavBarView(Context context) {
    super(context);

    initViews(context, null);
  }

  public BottomNavBarView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);

    initViews(context, attrs);
  }

  //初始化容器
  private void initViews(Context context, AttributeSet attrs) {
    bottomNavContainerLayout = (RelativeLayout) View.inflate(context, R.layout.layout_bottom_nav_container, null);
    addViewLayout = bottomNavContainerLayout.findViewById(R.id.add_view_ll);
    addContainerLayout = bottomNavContainerLayout.findViewById(R.id.add_rl);
    empty_line = bottomNavContainerLayout.findViewById(R.id.empty_line);
    bottomNavTabLayout = bottomNavContainerLayout.findViewById(R.id.bottom_nav_tab_layout);
    bottomNavViewPager = bottomNavContainerLayout.findViewById(R.id.bottom_nav_view_pager);
    lineView = bottomNavContainerLayout.findViewById(R.id.common_horizontal_line);
    lineView.setTag(-100);
    empty_line.setTag(-100);
    bottomNavTabLayout.setTag(-100);

    toDp();

    TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.bottom_nav_bar_view);
    parseStyle(attributes);

    addView(bottomNavContainerLayout);
  }

  private void parseStyle(TypedArray attributes) {
    if (attributes != null) {
      navigationHeight = attributes.getDimension(R.styleable.bottom_nav_bar_view_nav_height, navigationHeight);
      navigationBackground = attributes.getColor(R.styleable.bottom_nav_bar_view_nav_background, navigationBackground);

      tabTextSize = attributes.getDimension(R.styleable.bottom_nav_bar_view_tab_text_size, tabTextSize);
      tabTextTop = attributes.getDimension(R.styleable.bottom_nav_bar_view_tab_text_top, tabTextTop);
      iconSize = (int) attributes.getDimension(R.styleable.bottom_nav_bar_view_tab_icon_size, iconSize);
      hintPointSize = attributes.getDimension(R.styleable.bottom_nav_bar_view_hint_point_size, hintPointSize);
      msgPointSize = attributes.getDimension(R.styleable.bottom_nav_bar_view_msg_point_size, msgPointSize);
      hintPointLeft = attributes.getDimension(R.styleable.bottom_nav_bar_view_hint_point_left, hintPointLeft);
      msgPointTop = attributes.getDimension(R.styleable.bottom_nav_bar_view_msg_point_top, -iconSize / 2);
      hintPointTop = attributes.getDimension(R.styleable.bottom_nav_bar_view_hint_point_top, hintPointTop);

      msgPointLeft = attributes.getDimension(R.styleable.bottom_nav_bar_view_msg_point_left, -iconSize / 2);
      msgPointTextSize = attributes.getDimension(R.styleable.bottom_nav_bar_view_msg_point_text_size, msgPointTextSize);
      addIconSize = attributes.getDimension(R.styleable.bottom_nav_bar_view_add_icon_size, addIconSize);
      addLayoutBottom = attributes.getDimension(R.styleable.bottom_nav_bar_view_add_layout_bottom, addLayoutBottom);

      //加号属性
      addSelectTextColor = attributes.getColor(R.styleable.bottom_nav_bar_view_add_select_text_color, addSelectTextColor);
      addNormalTextColor = attributes.getColor(R.styleable.bottom_nav_bar_view_add_normal_text_color, addNormalTextColor);
      addTextSize = attributes.getDimension(R.styleable.bottom_nav_bar_view_add_text_size, addTextSize);
      addTextTopMargin = attributes.getDimension(R.styleable.bottom_nav_bar_view_add_text_top_margin, addTextTopMargin);
      addAlignBottom = attributes.getBoolean(R.styleable.bottom_nav_bar_view_add_align_bottom, addAlignBottom);


      lineHeight = attributes.getDimension(R.styleable.bottom_nav_bar_view_line_height, lineHeight);
      lineColor = attributes.getColor(R.styleable.bottom_nav_bar_view_line_color, lineColor);


      addLayoutHeight = attributes.getDimension(R.styleable.bottom_nav_bar_view_add_layout_height, navigationHeight + lineHeight);

      normalTextColor = attributes.getColor(R.styleable.bottom_nav_bar_view_tab_normal_color, normalTextColor);
      selectTextColor = attributes.getColor(R.styleable.bottom_nav_bar_view_tab_select_color, selectTextColor);

      int type = attributes.getInt(R.styleable.bottom_nav_bar_view_scale_type, 0);
      if (type == 0) {
        scaleType = ImageView.ScaleType.CENTER_INSIDE;
      } else if (type == 1) {
        scaleType = ImageView.ScaleType.CENTER_CROP;
      } else if (type == 2) {
        scaleType = ImageView.ScaleType.CENTER;
      } else if (type == 3) {
        scaleType = ImageView.ScaleType.FIT_CENTER;
      } else if (type == 4) {
        scaleType = ImageView.ScaleType.FIT_END;
      } else if (type == 5) {
        scaleType = ImageView.ScaleType.FIT_START;
      } else if (type == 6) {
        scaleType = ImageView.ScaleType.FIT_XY;
      } else if (type == 7) {
        scaleType = ImageView.ScaleType.MATRIX;
      }

      addLayoutRule = attributes.getInt(R.styleable.bottom_nav_bar_view_add_layout_rule, addLayoutRule);
      hasPadding = attributes.getBoolean(R.styleable.bottom_nav_bar_view_has_padding, hasPadding);

      addAsFragment = attributes.getBoolean(R.styleable.bottom_nav_bar_view_add_as_fragment, addAsFragment);

      attributes.recycle();
    }
  }

  //将dp、sp转换成px
  private void toDp() {
    navigationHeight = DensityHandler.dip2px(getContext(), navigationHeight);
    iconSize = (int) DensityHandler.dip2px(getContext(), iconSize);
    hintPointSize = DensityHandler.dip2px(getContext(), hintPointSize);
    hintPointTop = DensityHandler.dip2px(getContext(), hintPointTop);
    hintPointLeft = DensityHandler.dip2px(getContext(), hintPointLeft);

    msgPointLeft = DensityHandler.dip2px(getContext(), msgPointLeft);
    msgPointTop = DensityHandler.dip2px(getContext(), msgPointTop);
    msgPointSize = DensityHandler.dip2px(getContext(), msgPointSize);
    msgPointTextSize = DensityHandler.sp2px(getContext(), msgPointTextSize);

    tabTextTop = DensityHandler.dip2px(getContext(), tabTextTop);
    tabTextSize = DensityHandler.sp2px(getContext(), tabTextSize);

    //Add
    addIconSize = DensityHandler.dip2px(getContext(), addIconSize);
    addLayoutHeight = DensityHandler.dip2px(getContext(), addLayoutHeight);
    addLayoutBottom = DensityHandler.dip2px(getContext(), addLayoutBottom);
    addTextSize = DensityHandler.sp2px(getContext(), addTextSize);
    addTextTopMargin = DensityHandler.dip2px(getContext(), addTextTopMargin);
  }


  @SuppressLint("ClickableViewAccessibility")
  public void build() {

    if (addLayoutHeight < navigationHeight + lineHeight)
      addLayoutHeight = navigationHeight + lineHeight;

    if (addLayoutRule == RULE_cENTER) {
      RelativeLayout.LayoutParams addLayoutParams = (RelativeLayout.LayoutParams) addContainerLayout.getLayoutParams();
      addLayoutParams.height = (int) addLayoutHeight;
      addContainerLayout.setLayoutParams(addLayoutParams);
    } else if (addLayoutRule == RULE_BOTTOM) {
               /* RelativeLayout.LayoutParams addLayoutParams = (RelativeLayout.LayoutParams) addContainerLayout.getLayoutParams();
                if ((addIconSize + addIconBottom) > (navigationHeight + 1))
                    addLayoutParams.height = (int) (addIconSize + addIconBottom);
                else
                    addLayoutParams.height = (int) (navigationHeight + 1);
                addContainerLayout.setLayoutParams(addLayoutParams);*/
    }


    bottomNavTabLayout.setBackgroundColor(navigationBackground);
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bottomNavTabLayout.getLayoutParams();
    params.height = (int) navigationHeight;
    bottomNavTabLayout.setLayoutParams(params);


    if (hasPadding) {
      bottomNavViewPager.setPadding(0, 0, 0, (int) (navigationHeight + lineHeight));
    }

    RelativeLayout.LayoutParams lineParams = (RelativeLayout.LayoutParams) lineView.getLayoutParams();
    lineParams.height = (int) lineHeight;
    lineView.setBackgroundColor(lineColor);
    lineView.setLayoutParams(lineParams);

    //若没有设置中间添加的文字字体大小、颜色、则同其他Tab一样
    if (addTextSize == 0) {
      addTextSize = tabTextSize;
    }
    if (addNormalTextColor == 0) {
      addNormalTextColor = normalTextColor;
    }
    if (addSelectTextColor == 0) {
      addSelectTextColor = selectTextColor;
    }

    if (mode == MODE_NORMAL) {
      buildNavigation();
    } else if (mode == MODE_ADD) {
      buildAddNavigation();
    } else if (mode == MODE_ADD_VIEW) {
      buildAddViewNavigation();
    }

    bottomNavViewPager.setCanScroll(canScroll);
  }

  public void buildNavigation() {
    if ((titleItems.length != normalIconItems.length) || (titleItems.length != selectIconItems.length) || (normalIconItems.length != selectIconItems.length)) {
      Log.e("dongsl_bottom_nav", "请传入相同数量的Tab文字集合、未选中图标集合、选中图标集合");
      return;
    }

    tabCount = titleItems.length;

    removeNavigationAllView();

    setViewPagerAdapter();

    for (int i = 0; i < tabCount; i++) {
      View itemView = View.inflate(getContext(), R.layout.layout_bottom_nav_tab, null); //底部导航标签布局
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
      params.width = DensityHandler.getScreenWidth(getContext()) / tabCount;

      itemView.setLayoutParams(params);
      itemView.setId(i);

      TextView text = itemView.findViewById(R.id.tab_text_tv);
      ImageView icon = itemView.findViewById(R.id.tab_icon_iv);
      icon.setScaleType(scaleType);
      LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
      iconParams.width = iconSize;
      iconParams.height = iconSize;
      icon.setLayoutParams(iconParams);

      View hintPoint = itemView.findViewById(R.id.red_point);

      //提示红点
      RelativeLayout.LayoutParams hintPointParams = (RelativeLayout.LayoutParams) hintPoint.getLayoutParams();
      hintPointParams.bottomMargin = (int) hintPointTop;
      hintPointParams.width = (int) hintPointSize;
      hintPointParams.height = (int) hintPointSize;
      hintPointParams.leftMargin = (int) hintPointLeft;
      hintPoint.setLayoutParams(hintPointParams);

      //消息红点
      TextView msgPoint = itemView.findViewById(R.id.msg_point_tv);

      //msgPoint.setTextSize(TypedValue.COMPLEX_UNIT_PX, msgPointTextSize));
      msgPoint.setTextSize(TypedValue.COMPLEX_UNIT_PX, msgPointTextSize);
      RelativeLayout.LayoutParams msgPointParams = (RelativeLayout.LayoutParams) msgPoint.getLayoutParams();
      msgPointParams.bottomMargin = (int) msgPointTop;
      msgPointParams.width = (int) msgPointSize;
      msgPointParams.height = (int) msgPointSize;
      msgPointParams.leftMargin = (int) msgPointLeft;
      msgPoint.setLayoutParams(msgPointParams);


      hintPointList.add(hintPoint);
      msgPointList.add(msgPoint);

      imageViewList.add(icon);
      textViewList.add(text);

      itemView.setOnClickListener(view -> {
        if (null == onTabClickListener || (!onTabClickListener.onTabClickEvent(view, view.getId()) && null != fragmentList.get(view.getId()))) {
          bottomNavViewPager.setCurrentItem(view.getId(), smoothScroll);
        }
      });

      LayoutParams textParams = (LayoutParams) text.getLayoutParams();
      textParams.topMargin = (int) tabTextTop;
      text.setLayoutParams(textParams);
      text.setText(titleItems[i]);
      text.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);

      tabList.add(itemView);
      bottomNavTabLayout.addView(itemView);
    }
    select(0, false);
  }

  //滑动翻页
  private void setViewPagerAdapter() {
    if (null == adapter) {
      adapter = new BottomNavViewPagerAdapter(fragmentManager, fragmentList);
      bottomNavViewPager.setAdapter(adapter);
    } else {
      adapter.setData(fragmentList);
    }

    bottomNavViewPager.setOffscreenPageLimit(10);
    bottomNavViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override
      public void onPageSelected(int position) {
        //if (null != fragmentList.get(position)) { //在没有控制好 fram等于null时 是否允许左右滑动时 不验证
        select(position, true);
        //}
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  //构建中间带按钮的navigation
  public void buildAddNavigation() {

    if ((titleItems.length != normalIconItems.length) || (titleItems.length != selectIconItems.length) || (normalIconItems.length != selectIconItems.length)) {
      Log.e("dongsl_bottom_nav", "请传入相同数量的Tab文字集合、未选中图标集合、选中图标集合");
      return;
    }
    tabCount = titleItems.length;
    if (tabCount % 2 == 0) {
      Log.e("dongsl_bottom_nav", "MODE_ADD模式下请传入奇奇奇奇奇奇奇奇奇奇奇数数量的Tab文字集合、未选中图标集合、选中图标集合");
      return;
    }

    if (addAsFragment) {
      if (fragmentList.size() < tabCount) {
        Log.e("dongsl_bottom_nav", "MODE_ADD模式下/addAsFragment=true时Fragment的数量应和传入tab集合数量相等");
        return;
      }
    } else {
      if (fragmentList.size() < tabCount - 1) {
        Log.e("dongsl_bottom_nav", "MODE_ADD模式下/addAsFragment=false时Fragment的数量应比传入tab集合数量少一个");
        return;
      }
    }

    removeNavigationAllView();

    setViewPagerAdapter();

    for (int i = 0; i < tabCount; i++) {

      if (i == tabCount / 2) {
        RelativeLayout addItemView = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams addItemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addItemParams.width = DensityHandler.getScreenWidth(getContext()) / tabCount;
        addItemView.setLayoutParams(addItemParams);
        bottomNavTabLayout.addView(addItemView);

        final LinearLayout addLinear = new LinearLayout(getContext());
        addLinear.setOrientation(VERTICAL);
        addLinear.setGravity(Gravity.CENTER);
        final RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addImage = new ImageView(getContext());
        LayoutParams imageParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageParams.width = (int) addIconSize;
        imageParams.height = (int) addIconSize;
        addImage.setLayoutParams(imageParams);

        TextView addText = new TextView(getContext());
        addText.setTextSize(TypedValue.COMPLEX_UNIT_PX, addTextSize);
        LayoutParams addTextParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addTextParams.topMargin = (int) addTextTopMargin;
        if (TextUtils.isEmpty(titleItems[i])) {
          addText.setVisibility(GONE);
        } else {
          addText.setVisibility(VISIBLE);
        }
        addText.setLayoutParams(addTextParams);
        addText.setText(titleItems[i]);

        if (addLayoutRule == RULE_cENTER) {
          linearParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (addLayoutRule == RULE_BOTTOM) {
          linearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
          linearParams.addRule(RelativeLayout.ABOVE, R.id.empty_line);
          if (addAlignBottom) {
            if (textViewList != null && textViewList.size() > 0) {
              textViewList.get(0).post(() -> {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) empty_line.getLayoutParams();
                params.height = (int) ((navigationHeight - textViewList.get(0).getHeight() - iconSize - tabTextTop) / 2);
                empty_line.setLayoutParams(params);
                //linearParams.bottomMargin = (int) ((navigationHeight - textViewList.get(0).getHeight() - iconSize - tabTextTop) / 2);
              });

            }
          } else {
            linearParams.bottomMargin = (int) addLayoutBottom;
          }
        }

        addImage.setId(i);
        addImage.setImageResource(normalIconItems[i]);
        addImage.setOnClickListener(view -> {
          if (onTabClickListener != null) {
            if (!onTabClickListener.onTabClickEvent(view, view.getId()) && addAsFragment) {
              bottomNavViewPager.setCurrentItem(view.getId(), smoothScroll);
            }
          } else if (addAsFragment) {
            bottomNavViewPager.setCurrentItem(view.getId(), smoothScroll);
          }
          if (onAddClickListener != null) {
            onAddClickListener.OnAddClickEvent(view);
          }
        });

        imageViewList.add(addImage);
        textViewList.add(addText);


        addLinear.addView(addImage);
        addLinear.addView(addText);

        tabList.add(addLinear);

        addContainerLayout.addView(addLinear, linearParams);
      } else {
        int index = i;

        View itemView = View.inflate(getContext(), R.layout.layout_bottom_nav_tab, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.width = DensityHandler.getScreenWidth(getContext()) / tabCount;

        itemView.setLayoutParams(params);
        itemView.setId(index);

        TextView text = itemView.findViewById(R.id.tab_text_tv);
        ImageView icon = itemView.findViewById(R.id.tab_icon_iv);
        icon.setScaleType(scaleType);
        LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
        iconParams.width = iconSize;
        iconParams.height = iconSize;
        icon.setLayoutParams(iconParams);

        imageViewList.add(icon);
        textViewList.add(text);
        itemView.setOnClickListener(view -> {
          int tabPosition = view.getId();
          if (null == onTabClickListener || (!onTabClickListener.onTabClickEvent(view, tabPosition))) {
            if (tabPosition > tabCount / 2 && !addAsFragment) {
              tabPosition -= 1;
            }
            if (null != fragmentList.get(tabPosition)) {
              bottomNavViewPager.setCurrentItem(tabPosition, smoothScroll);
            }
          }
        });

        LayoutParams textParams = (LayoutParams) text.getLayoutParams();
        textParams.topMargin = (int) tabTextTop;
        text.setLayoutParams(textParams);
        text.setText(titleItems[index]);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);


        View hintPoint = itemView.findViewById(R.id.red_point);

        //提示红点
        RelativeLayout.LayoutParams hintPointParams = (RelativeLayout.LayoutParams) hintPoint.getLayoutParams();
        hintPointParams.bottomMargin = (int) hintPointTop;
        hintPointParams.width = (int) hintPointSize;
        hintPointParams.height = (int) hintPointSize;
        hintPointParams.leftMargin = (int) hintPointLeft;
        hintPoint.setLayoutParams(hintPointParams);

        //消息红点
        TextView msgPoint = itemView.findViewById(R.id.msg_point_tv);
        msgPoint.setTextSize(TypedValue.COMPLEX_UNIT_PX, msgPointTextSize);
        RelativeLayout.LayoutParams msgPointParams = (RelativeLayout.LayoutParams) msgPoint.getLayoutParams();
        msgPointParams.bottomMargin = (int) msgPointTop;
        msgPointParams.width = (int) msgPointSize;
        msgPointParams.height = (int) msgPointSize;
        msgPointParams.leftMargin = (int) msgPointLeft;
        msgPoint.setLayoutParams(msgPointParams);


        hintPointList.add(hintPoint);
        msgPointList.add(msgPoint);


        tabList.add(itemView);
        bottomNavTabLayout.addView(itemView);
      }
    }
    select(0, false);
  }


  private void removeNavigationAllView() {

    for (int i = 0; i < addContainerLayout.getChildCount(); i++) {
      if (addContainerLayout.getChildAt(i).getTag() == null) {
        addContainerLayout.removeViewAt(i);
      }
    }

    msgPointList.clear();
    hintPointList.clear();
    imageViewList.clear();
    textViewList.clear();
    tabList.clear();

    bottomNavTabLayout.removeAllViews();
  }

  private void addTabView(final int index) {
    View itemView = View.inflate(getContext(), R.layout.layout_bottom_nav_tab, null);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    params.width = DensityHandler.getScreenWidth(getContext()) / tabCount;

    itemView.setLayoutParams(params);
    itemView.setId(index);

    TextView text = itemView.findViewById(R.id.tab_text_tv);
    ImageView icon = itemView.findViewById(R.id.tab_icon_iv);
    icon.setScaleType(scaleType);
    LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
    iconParams.width = iconSize;
    iconParams.height = iconSize;
    icon.setLayoutParams(iconParams);

    imageViewList.add(icon);
    textViewList.add(text);

    itemView.setOnClickListener(view -> {
      Integer tabPosition = null;
      if (mode == MODE_ADD) {
        tabPosition = view.getId();
        if (tabPosition > tabCount / 2 && !addAsFragment) {
          tabPosition -= 1;
        }
      } else if (mode == MODE_ADD_VIEW) {
        tabPosition = index;
        if (index > tabCount / 2 && !addAsFragment) {
          tabPosition = view.getId();
        }
      }
      if (null != tabPosition) {
        if (null == onTabClickListener || (!onTabClickListener.onTabClickEvent(view, tabPosition) && null != fragmentList.get(tabPosition))) {
          bottomNavViewPager.setCurrentItem(tabPosition, smoothScroll);
        }
      }
    });

            /*if (mode == MODE_ADD) {
                itemView.setOnClickListener(view -> {
                    int tabPosition = view.getId();
                    if (null == onTabClickListener || (!onTabClickListener.onTabClickEvent(view, tabPosition) && null != fragmentList.get(tabPosition))) {
                        if (tabPosition > tabCount / 2 && !addAsFragment) {
                            tabPosition -= 1;
                        }
                        bottomNavViewPager.setCurrentItem(tabPosition, smoothScroll);
                    }
                });
            } else if (mode == MODE_ADD_VIEW) {
                itemView.setOnClickListener(view -> {
                    int tabPosition = index;
                    if (null == onTabClickListener || (!onTabClickListener.onTabClickEvent(view, tabPosition) && null != fragmentList.get(tabPosition))) {
                        if (index > tabCount / 2 && !addAsFragment) {
                            tabPosition = view.getId();
                        }
                        bottomNavViewPager.setCurrentItem(tabPosition, smoothScroll);
                    }
                });
            }*/

    LayoutParams textParams = (LayoutParams) text.getLayoutParams();
    textParams.topMargin = (int) tabTextTop;
    text.setLayoutParams(textParams);
    text.setText(titleItems[index]);
    text.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);


    View hintPoint = itemView.findViewById(R.id.red_point);

    //提示红点
    RelativeLayout.LayoutParams hintPointParams = (RelativeLayout.LayoutParams) hintPoint.getLayoutParams();
    hintPointParams.bottomMargin = (int) hintPointTop;
    hintPointParams.width = (int) hintPointSize;
    hintPointParams.height = (int) hintPointSize;
    hintPointParams.leftMargin = (int) hintPointLeft;
    hintPoint.setLayoutParams(hintPointParams);

    //消息红点
    TextView msgPoint = itemView.findViewById(R.id.msg_point_tv);
    msgPoint.setTextSize(TypedValue.COMPLEX_UNIT_PX, msgPointTextSize);
    RelativeLayout.LayoutParams msgPointParams = (RelativeLayout.LayoutParams) msgPoint.getLayoutParams();
    msgPointParams.bottomMargin = (int) msgPointTop;
    msgPointParams.width = (int) msgPointSize;
    msgPointParams.height = (int) msgPointSize;
    msgPointParams.leftMargin = (int) msgPointLeft;
    msgPoint.setLayoutParams(msgPointParams);


    hintPointList.add(hintPoint);
    msgPointList.add(msgPoint);


    tabList.add(itemView);
    bottomNavTabLayout.addView(itemView);
  }


  //自定义中间按钮
  public void buildAddViewNavigation() {

    if ((titleItems.length != normalIconItems.length) || (titleItems.length != selectIconItems.length) || (normalIconItems.length != selectIconItems.length)) {
      Log.e("dongsl_bottom_nav", "请传入相同数量的Tab文字集合、未选中图标集合、选中图标集合");
      return;
    }
    tabCount = titleItems.length + 1;
    if (tabCount % 2 == 0) {
      Log.e("dongsl_bottom_nav", "MODE_ADD_VIEW模式下请传入偶数量的Tab文字集合、未选中图标集合、选中图标集合");
      return;
    }
    if (addAsFragment) {
      if (fragmentList.size() < tabCount) {
        Log.e("dongsl_bottom_nav", "MODE_ADD_VIEW模式下/addAsFragment=true时Fragment的数量应比传入tab集合数量多一个");
        return;
      }
    } else {
      if (fragmentList.size() < tabCount - 1) {
        Log.e("dongsl_bottom_nav", "MODE_ADD_VIEW模式下/addAsFragment=false时,Fragment的数量应和传入tab集合数量相等");
        return;
      }
    }

    removeNavigationAllView();

    setViewPagerAdapter();

    for (int i = 0; i < tabCount; i++) {

      if (i == tabCount / 2) {
        RelativeLayout addItemView = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams addItemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addItemParams.width = DensityHandler.getScreenWidth(getContext()) / tabCount;
        addItemView.setLayoutParams(addItemParams);
        bottomNavTabLayout.addView(addItemView);

        final RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //linearParams.width = DensityHandler.getScreenWidth(getContext()) / tabCount;

        if (addLayoutRule == RULE_cENTER) {
          linearParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (addLayoutRule == RULE_BOTTOM) {
          linearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
          if (addAlignBottom) {
            linearParams.addRule(RelativeLayout.ABOVE, R.id.empty_line);
            if (textViewList != null && textViewList.size() > 0) {
              textViewList.get(0).post(() -> linearParams.bottomMargin = (int) ((navigationHeight - textViewList.get(0).getHeight() - iconSize - tabTextTop) / 2));

            }
          } else {
            linearParams.addRule(RelativeLayout.ABOVE, R.id.empty_line);
            linearParams.bottomMargin = (int) addLayoutBottom;
          }
        }
        customAddView.setId(i);
        customAddView.setOnClickListener(view -> {
          if (onTabClickListener != null) {
            if (!onTabClickListener.onTabClickEvent(view, view.getId())) {
              if (addAsFragment) {
                bottomNavViewPager.setCurrentItem(view.getId(), smoothScroll);
              }
            }
          } else {
            if (addAsFragment)
              bottomNavViewPager.setCurrentItem(view.getId(), smoothScroll);
          }
          if (onAddClickListener != null)
            onAddClickListener.OnAddClickEvent(view);
        });

        addContainerLayout.addView(customAddView, linearParams);
      } else {
        int index;

        if (i > tabCount / 2) {
          index = i - 1;
        } else {
          index = i;
        }

        View itemView = View.inflate(getContext(), R.layout.layout_bottom_nav_tab, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.width = DensityHandler.getScreenWidth(getContext()) / tabCount;

        itemView.setLayoutParams(params);
        itemView.setId(index);

        TextView text = itemView.findViewById(R.id.tab_text_tv);
        ImageView icon = itemView.findViewById(R.id.tab_icon_iv);
        icon.setScaleType(scaleType);
        LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
        iconParams.width = iconSize;
        iconParams.height = iconSize;
        icon.setLayoutParams(iconParams);

        imageViewList.add(icon);
        textViewList.add(text);
        final int finalI = i;
        itemView.setOnClickListener(view -> {
          int tabPosition = finalI;
          if (null == onTabClickListener || (!onTabClickListener.onTabClickEvent(view, tabPosition))) {
            if (finalI > tabCount / 2 && !addAsFragment) {
              tabPosition = view.getId();
            }
            if (null != fragmentList.get(tabPosition)) {
              bottomNavViewPager.setCurrentItem(tabPosition, smoothScroll);
            }
          }
        });

        LayoutParams textParams = (LayoutParams) text.getLayoutParams();
        textParams.topMargin = (int) tabTextTop;
        text.setLayoutParams(textParams);
        text.setText(titleItems[index]);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);

        View hintPoint = itemView.findViewById(R.id.red_point);

        //提示红点
        RelativeLayout.LayoutParams hintPointParams = (RelativeLayout.LayoutParams) hintPoint.getLayoutParams();
        hintPointParams.bottomMargin = (int) hintPointTop;
        hintPointParams.width = (int) hintPointSize;
        hintPointParams.height = (int) hintPointSize;
        hintPointParams.leftMargin = (int) hintPointLeft;
        hintPoint.setLayoutParams(hintPointParams);

        //消息红点
        TextView msgPoint = itemView.findViewById(R.id.msg_point_tv);
        msgPoint.setTextSize(TypedValue.COMPLEX_UNIT_PX, msgPointTextSize);
        RelativeLayout.LayoutParams msgPointParams = (RelativeLayout.LayoutParams) msgPoint.getLayoutParams();
        msgPointParams.bottomMargin = (int) msgPointTop;
        msgPointParams.width = (int) msgPointSize;
        msgPointParams.height = (int) msgPointSize;
        msgPointParams.leftMargin = (int) msgPointLeft;
        msgPoint.setLayoutParams(msgPointParams);

        hintPointList.add(hintPoint);
        msgPointList.add(msgPoint);

        tabList.add(itemView);
        bottomNavTabLayout.addView(itemView);
      }
    }

    select(0, false);
  }


  public BottomNavViewPager getBottomNavViewPager() {
    return bottomNavViewPager;
  }


  public void setAddViewLayout(View addViewLayout) {
    FrameLayout.LayoutParams addParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    this.addViewLayout.addView(addViewLayout, addParams);
  }

  public ViewGroup getAddViewLayout() {
    return addViewLayout;
  }

  /**
   * tab图标、文字变换
   *
   * @param position
   */
  private void select(int position, boolean showAnim) {
    YoYo.AnimationComposer animationComposer = YoYo.with(anim.getAnimator()).duration(300);
    if (mode == MODE_NORMAL) {
      for (int i = 0; i < tabCount; i++) {
        if (i == position) {
          if (anim != null && showAnim) {
            animationComposer.playOn(tabList.get(i));
          }
          imageViewList.get(i).setImageResource(selectIconItems[i]);
          textViewList.get(i).setTextColor(selectTextColor);
        } else {
          imageViewList.get(i).setImageResource(normalIconItems[i]);
          textViewList.get(i).setTextColor(normalTextColor);
        }
      }
    } else if (mode == MODE_ADD) {
      if (addAsFragment) {
        for (int i = 0; i < tabCount; i++) {
          if (i == position) {
            if (anim != null && showAnim && (position != tabCount / 2)) {
              animationComposer.playOn(tabList.get(i));
            }
            if (i == tabCount / 2) {
              textViewList.get(i).setTextColor(addSelectTextColor);
            } else {
              textViewList.get(i).setTextColor(selectTextColor);
            }

            imageViewList.get(i).setImageResource(selectIconItems[i]);
          } else {
            imageViewList.get(i).setImageResource(normalIconItems[i]);
            if (i == tabCount / 2) {
              textViewList.get(i).setTextColor(addNormalTextColor);
            } else {
              textViewList.get(i).setTextColor(normalTextColor);
            }
          }
        }
      } else {
        if ((position > ((tabCount - 2) / 2))) {
          position = position + 1;
        }
        for (int i = 0; i < tabCount; i++) {
          if (i == position) {
            if (anim != null && showAnim && (i != tabCount / 2)) {
              YoYo.with(Techniques.Pulse).duration(300).playOn(tabList.get(i));
            }

            imageViewList.get(i).setImageResource(selectIconItems[i]);
            if (i == tabCount / 2) {
              textViewList.get(i).setTextColor(addSelectTextColor);
            } else {
              textViewList.get(i).setTextColor(selectTextColor);
            }
          } else {
            imageViewList.get(i).setImageResource(normalIconItems[i]);
            if (i == tabCount / 2) {
              textViewList.get(i).setTextColor(addNormalTextColor);
            } else {
              textViewList.get(i).setTextColor(normalTextColor);
            }
          }
        }
      }
    } else if (mode == MODE_ADD_VIEW) {
      int realPosition;
      if (addAsFragment) {
        for (int i = 0; i < tabCount; i++) {
          if (i == tabCount / 2) {
            continue;
          } else if (i > tabCount / 2) {
            realPosition = i - 1;
          } else {
            realPosition = i;
          }
          if (i == position) {
            if (anim != null && showAnim) {
              animationComposer.playOn(tabList.get(realPosition));
            }
            imageViewList.get(realPosition).setImageResource(selectIconItems[realPosition]);
            textViewList.get(realPosition).setTextColor(selectTextColor);
          } else {
            imageViewList.get(realPosition).setImageResource(normalIconItems[realPosition]);
            textViewList.get(realPosition).setTextColor(normalTextColor);
          }
        }
      } else {
        for (int i = 0; i < tabCount - 1; i++) {
          if (i == position) {
            if (anim != null && showAnim) {
              animationComposer.playOn(tabList.get(i));
            }
            imageViewList.get(i).setImageResource(selectIconItems[i]);
            textViewList.get(i).setTextColor(selectTextColor);
          } else {
            imageViewList.get(i).setImageResource(normalIconItems[i]);
            textViewList.get(i).setTextColor(normalTextColor);
          }
        }
      }
    }
  }


  public void selectTab(int position) {
    getBottomNavViewPager().setCurrentItem(position, smoothScroll);
  }


  /**
   * 设置是否显示小红点
   *
   * @param position 第几个tab
   * @param isShow   是否显示
   */
  public void setHintPoint(int position, boolean isShow) {
    if (hintPointList == null || hintPointList.size() < (position + 1))
      return;
    if (isShow) {
      hintPointList.get(position).setVisibility(VISIBLE);
    } else {
      hintPointList.get(position).setVisibility(GONE);
    }
  }


  /**
   * 设置消息数量
   *
   * @param position 第几个tab
   * @param count    显示的数量  99个以上显示99+  少于1则不显示
   */
  public void setMsgPointCount(int position, int count) {
    if (msgPointList == null || msgPointList.size() < (position + 1))
      return;
    if (count > 99) {
      msgPointList.get(position).setText("99+");
      msgPointList.get(position).setVisibility(VISIBLE);
    } else if (count < 1) {
      msgPointList.get(position).setVisibility(GONE);
    } else {
      msgPointList.get(position).setText(count + "");
      msgPointList.get(position).setVisibility(VISIBLE);
    }
  }

  /**
   * 清除数字消息
   *
   * @param position
   */
  public void clearMsgPoint(int position) {
    if (msgPointList == null || msgPointList.size() < (position + 1))
      return;
    msgPointList.get(position).setVisibility(GONE);
  }

  /**
   * 清除提示红点
   *
   * @param position
   */
  public void clearHintPoint(int position) {
    if (hintPointList == null || hintPointList.size() < (position + 1))
      return;
    hintPointList.get(position).setVisibility(GONE);
  }

  /**
   * 清空所有提示红点
   */
  public void clearAllHintPoint() {
    for (int i = 0; i < hintPointList.size(); i++) {
      hintPointList.get(i).setVisibility(GONE);
    }
  }

  /**
   * 清空所有消息红点
   */
  public void clearAllMsgPoint() {
    for (int i = 0; i < msgPointList.size(); i++) {
      msgPointList.get(i).setVisibility(GONE);
    }
  }

  public interface OnTabClickListener {
    boolean onTabClickEvent(View view, int position); //true: 不会继续后面操作，false: 还会切换到指定页面
  }


  public interface OnAddClickListener {
    boolean OnAddClickEvent(View view);
  }


  public BottomNavBarView addLayoutHeight(int addLayoutHeight) {
    this.addLayoutHeight = DensityHandler.dip2px(getContext(), addLayoutHeight);
    return this;
  }

  public BottomNavBarView scaleType(ImageView.ScaleType scaleType) {
    this.scaleType = scaleType;
    return this;
  }


  public BottomNavBarView mode(int mode) {
    this.mode = mode;
    return this;
  }

  public BottomNavBarView hasPadding(boolean hasPadding) {
    this.hasPadding = hasPadding;
    return this;
  }

  public BottomNavBarView addIconSize(int addIconSize) {
    this.addIconSize = DensityHandler.dip2px(getContext(), addIconSize);
    return this;
  }

     /*   public EasyNavigationBar onAddClickListener(EasyNavigationBar.OnAddClickListener onAddClickListener) {
            this.onAddClickListener = onAddClickListener;
            return this;
        }*/


  public BottomNavBarView navigationBackground(int navigationBackground) {
    this.navigationBackground = navigationBackground;
    return this;
  }

  public BottomNavBarView navigationHeight(int navigationHeight) {
    this.navigationHeight = DensityHandler.dip2px(getContext(), navigationHeight);
    return this;
  }

  public BottomNavBarView normalTextColor(int normalTextColor) {
    this.normalTextColor = normalTextColor;
    return this;
  }

  public BottomNavBarView selectTextColor(int selectTextColor) {
    this.selectTextColor = selectTextColor;
    return this;
  }

  public BottomNavBarView lineHeight(int lineHeight) {
    this.lineHeight = lineHeight;
    return this;
  }

  public BottomNavBarView lineColor(int lineColor) {
    this.lineColor = lineColor;
    return this;
  }

  public BottomNavBarView tabTextSize(int tabTextSize) {
    this.tabTextSize = DensityHandler.sp2px(getContext(), tabTextSize);
    return this;
  }

  public BottomNavBarView tabTextTop(int tabTextTop) {
    this.tabTextTop = DensityHandler.dip2px(getContext(), tabTextTop);
    return this;
  }

  public BottomNavBarView msgPointTextSize(int msgPointTextSize) {
    this.msgPointTextSize = DensityHandler.sp2px(getContext(), msgPointTextSize);
    return this;
  }

  public BottomNavBarView msgPointSize(int msgPointSize) {
    this.msgPointSize = DensityHandler.dip2px(getContext(), msgPointSize);
    return this;
  }

  public BottomNavBarView msgPointLeft(int msgPointLeft) {
    this.msgPointLeft = DensityHandler.dip2px(getContext(), msgPointLeft);
    return this;
  }

  public BottomNavBarView msgPointTop(int msgPointTop) {
    this.msgPointTop = DensityHandler.dip2px(getContext(), msgPointTop);
    return this;
  }


  public BottomNavBarView hintPointSize(int hintPointSize) {
    this.hintPointSize = DensityHandler.dip2px(getContext(), hintPointSize);
    return this;
  }

  public BottomNavBarView hintPointLeft(int hintPointLeft) {
    this.hintPointLeft = DensityHandler.dip2px(getContext(), hintPointLeft);
    return this;
  }

  public BottomNavBarView hintPointTop(int hintPointTop) {
    this.hintPointTop = DensityHandler.dip2px(getContext(), hintPointTop);
    return this;
  }


  public BottomNavBarView titleItems(String[] titleItems) {
    this.titleItems = titleItems;
    return this;
  }

  public BottomNavBarView normalIconItems(int[] normalIconItems) {
    this.normalIconItems = normalIconItems;
    return this;
  }

  public BottomNavBarView selectIconItems(int[] selectIconItems) {
    this.selectIconItems = selectIconItems;
    return this;
  }

  public BottomNavBarView fragmentList(List<Fragment> fragmentList) {
    this.fragmentList = fragmentList;
    return this;
  }

  public BottomNavBarView fragmentManager(FragmentManager fragmentManager) {
    this.fragmentManager = fragmentManager;
    return this;
  }

  public BottomNavBarView anim(Techniques anim) {
    this.anim = anim;
    return this;
  }

  public BottomNavBarView addLayoutRule(int addLayoutRule) {
    this.addLayoutRule = addLayoutRule;
    return this;
  }

  public BottomNavBarView canScroll(boolean canScroll) {
    this.canScroll = canScroll;
    return this;
  }

  public BottomNavBarView smoothScroll(boolean smoothScroll) {
    this.smoothScroll = smoothScroll;
    return this;
  }

  public BottomNavBarView onTabClickListener(BottomNavBarView.OnTabClickListener onTabClickListener) {
    this.onTabClickListener = onTabClickListener;
    return this;
  }


  public BottomNavBarView iconSize(int iconSize) {
    this.iconSize = (int) DensityHandler.dip2px(getContext(), iconSize);
    return this;
  }

  public BottomNavBarView addLayoutBottom(int addLayoutBottom) {
    this.addLayoutBottom = DensityHandler.dip2px(getContext(), addLayoutBottom);
    return this;
  }

  public BottomNavBarView addAsFragment(boolean addAsFragment) {
    this.addAsFragment = addAsFragment;
    return this;
  }

  public BottomNavBarView addCustomView(View customAddView) {
    this.customAddView = customAddView;
    return this;
  }

  public BottomNavBarView addTextSize(int addTextSize) {
    this.addTextSize = DensityHandler.sp2px(getContext(), addTextSize);
    return this;
  }

  public BottomNavBarView addNormalTextColor(int addNormalTextColor) {
    this.addNormalTextColor = addNormalTextColor;
    return this;
  }

  public BottomNavBarView addSelectTextColor(int addSelectTextColor) {
    this.addSelectTextColor = addSelectTextColor;
    return this;
  }

  public BottomNavBarView addTextTopMargin(int addTextTopMargin) {
    this.addTextTopMargin = DensityHandler.dip2px(getContext(), addTextTopMargin);
    return this;
  }

  public BottomNavBarView addAlignBottom(boolean addAlignBottom) {
    this.addAlignBottom = addAlignBottom;
    return this;
  }

  public String[] getTitleItems() {
    return titleItems;
  }

  public int[] getNormalIconItems() {
    return normalIconItems;
  }

  public int[] getSelectIconItems() {
    return selectIconItems;
  }

  public List<Fragment> getFragmentList() {
    return fragmentList;
  }

  public FragmentManager getFragmentManager() {
    return fragmentManager;
  }

  public Techniques getAnim() {
    return anim;
  }

  public boolean isSmoothScroll() {
    return smoothScroll;
  }

  public BottomNavBarView.OnTabClickListener getOnTabClickListener() {
    return onTabClickListener;
  }

  public int getIconSize() {
    return iconSize;
  }


  public float getHintPointSize() {
    return hintPointSize;
  }

  public float getHintPointLeft() {
    return hintPointLeft;
  }

  public float getHintPointTop() {
    return hintPointTop;
  }


  public float getMsgPointTextSize() {
    return msgPointTextSize;
  }

  public float getMsgPointSize() {
    return msgPointSize;
  }

  public float getMsgPointLeft() {
    return msgPointLeft;
  }

  public float getMsgPointTop() {
    return msgPointTop;
  }

  public float getTabTextTop() {
    return tabTextTop;
  }

  public float getTabTextSize() {
    return tabTextSize;
  }

  public int getNormalTextColor() {
    return normalTextColor;
  }

  public int getSelectTextColor() {
    return selectTextColor;
  }

  public float getLineHeight() {
    return lineHeight;
  }

  public int getLineColor() {
    return lineColor;
  }

  public BottomNavBarView.OnAddClickListener getOnAddClickListener() {
    return onAddClickListener;
  }

  public float getAddIconSize() {
    return addIconSize;
  }

  public float getAddLayoutHeight() {
    return addLayoutHeight;
  }

  public int getNavigationBackground() {
    return navigationBackground;
  }

  public float getNavigationHeight() {
    return navigationHeight;
  }

  public boolean isCanScroll() {
    return canScroll;
  }

  public BottomNavViewPagerAdapter getAdapter() {
    return adapter;
  }


  public ImageView.ScaleType getScaleType() {
    return scaleType;
  }

  public int getMode() {
    return mode;
  }

  public LinearLayout getBottomNavTabLayout() {
    return bottomNavTabLayout;
  }

  public RelativeLayout getBottomNavContainerLayout() {
    return bottomNavContainerLayout;
  }

  public View getLineView() {
    return lineView;
  }

  public ViewGroup getAddLayout() {
    return addViewLayout;
  }

  public float getAddLayoutBottom() {
    return addLayoutBottom;
  }

  public int getAddLayoutRule() {
    return addLayoutRule;
  }

  public RelativeLayout getAddContainerLayout() {
    return addContainerLayout;
  }

  public boolean isHasPadding() {
    return hasPadding;
  }

  public boolean isAddAsFragment() {
    return addAsFragment;
  }

  public View getCustomAddView() {
    return customAddView;
  }

  public float getAddTextSize() {
    return addTextSize;
  }

  public int getAddNormalTextColor() {
    return addNormalTextColor;
  }

  public int getAddSelectTextColor() {
    return addSelectTextColor;
  }

  public float getAddTextTopMargin() {
    return addTextTopMargin;
  }

  public boolean isAddAlignBottom() {
    return addAlignBottom;
  }

  public ImageView getAddImage() {
    return addImage;
  }

  // --addIconBottom
}
