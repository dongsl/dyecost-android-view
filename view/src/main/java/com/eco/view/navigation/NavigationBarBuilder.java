/**
 * 导航设置
 * this.defaultSetting()  //恢复默认配置、可用于重绘导航栏
 * .titleItems(tabText)      //  Tab文字集合  只传文字则只显示文字
 * .normalIconItems(normalIcon)   //  Tab未选中图标集合
 * .selectIconItems(selectIcon)   //  Tab选中图标集合
 * .fragmentList(fragments)       //  fragment集合
 * .fragmentManager(getSupportFragmentManager())
 * .iconSize(20)     //Tab图标大小
 * .tabTextSize(10)   //Tab文字大小
 * .tabTextTop(2)     //Tab文字距Tab图标的距离
 * .normalTextColor(Color.parseColor("#666666"))   //Tab未选中时字体颜色
 * .selectTextColor(Color.parseColor("#333333"))   //Tab选中时字体颜色
 * .scaleType(ImageView.ScaleType.CENTER_INSIDE)  //同 ImageView的ScaleType
 * .navigationBackground(Color.parseColor("#80000000"))   //导航栏背景色
 * .setOnTabClickListener(new EasyNavigationBar.OnTabClickListener() {
 *
 * @Override public boolean onTabSelectEvent(View view, int position) {
 * //Tab点击事件  return true 页面不会切换
 * <p>
 * return false;
 * }
 * @Override public boolean onTabReSelectEvent(View view, int position) {
 * //Tab重复点击事件
 * return false;
 * }
 * })
 * .smoothScroll(false)  //点击Tab  Viewpager切换是否有动画
 * .canScroll(true)    //Viewpager能否左右滑动
 * .mode(EasyNavigationBar.NavigationMode.MODE_ADD)   //默认MODE_NORMAL 普通模式  //MODE_ADD 带加号模式
 * .centerTextStr("发现")
 * .centerImageRes(R.mipmap.add_image)
 * .centerIconSize(36)    //中间加号图片的大小
 * .centerLayoutHeight(100)   //包含加号的布局高度 背景透明  所以加号看起来突出一块
 * .navigationHeight(60)  //导航栏高度
 * .lineHeight(10)         //分割线高度  默认1px
 * .lineColor(Color.parseColor("#ff0000"))
 * .centerLayoutRule(EasyNavigationBar.RULE_BOTTOM) //RULE_CENTER 加号居中addLayoutHeight调节位置 EasyNavigationBar.RULE_BOTTOM 加号在导航栏靠下
 * .centerLayoutBottomMargin(10)   //加号到底部的距离
 * .hasPadding(true)    //true ViewPager布局在导航栏之上 false有重叠
 * .hintPointLeft(-3)  //调节提示红点的位置hintPointLeft hintPointTop（看文档说明）
 * .hintPointTop(-3)
 * .hintPointSize(6)    //提示红点的大小
 * .msgPointLeft(-10)  //调节数字消息的位置msgPointLeft msgPointTop（看文档说明）
 * .msgPointTop(-10)
 * .msgPointTextSize(9)  //数字消息中字体大小
 * .msgPointSize(18)    //数字消息红色背景的大小
 * .centerAlignBottom(true)  //加号是否同Tab文字底部对齐  RULE_BOTTOM时有效；
 * .centerTextTopMargin(50)  //加号文字距离加号图片的距离
 * .centerTextSize(15)      //加号文字大小
 * .centerNormalTextColor(Color.parseColor("#ff0000"))    //加号文字未选中时字体颜色
 * .centerSelectTextColor(Color.parseColor("#00ff00"))    //加号文字选中时字体颜色
 * .setMsgPointColor(Color.BLUE) //数字消息、红点背景颜色
 * .setMsgPointMoreRadius(5) //消息99+角度半径
 * .setMsgPointMoreWidth(50)  //消息99+宽度
 * .setMsgPointMoreHeight(40)  //消息99+高度
 * .textSizeType(EasyNavigationBar.TextSizeType.TYPE_DP)  //字体单位 建议使用DP  可切换SP
 * .setOnTabLoadListener(new EasyNavigationBar.OnTabLoadListener() { //Tab加载完毕回调
 * @Override public void onTabLoadCompleteEvent() {
 * navigationBar.setMsgPointCount(0, 7);
 * navigationBar.setMsgPointCount(1, 109);
 * navigationBar.setHintPoint(4, true);
 * }
 * })
 * //.setupWithViewPager() ViewPager或ViewPager2
 * .build();
 * <p>
 * ------------------ 红点或数字消息提示
 * //数字消息大于99显示99+ 小于等于0不显示，取消显示则可以navigationBarView.setMsgPointCount(2, 0)
 * navigationBarView.setMsgPointCount(2, 109);
 * navigationBarView.setMsgPointCount(0, 5);
 * //红点提示 第二个参数控制是否显示
 * navigationBarView.setHintPoint(3, true);
 * //清除第四个红点提示
 * navigationBarView.clearHintPoint(3);
 * //清除第一个数字消息
 * navigationBarView.clearMsgPoint(0);
 * <p>
 * ------------------ 半开放式登录、点击“我的”Tab、不切换页面、需要进行登录操作
 * .onTabClickListener(new EasyNavigitionBar.OnTabClickListener() {
 * @Override public boolean onTabClickEvent(View view, int position) {
 * if (position == 3) {
 * Toast.makeText(AddActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
 * return true;
 * }
 * return false;
 * }
 * })
 * ------------------ 加号突出
 * addLayoutRule= RULE_BOTTOM时、addAlignBottom才有效
 * <p>
 * ------------------ 加号旋转
 * <p>
 * EasyNavigitionBar.MODE_ADD模式下
 * 可使用 navigationBarView.getAddImage()获取到加号直接进行操作；
 * <p>
 * EasyNavigitionBar.MODE_ADD_VIEW模式下
 * 可使用navigationBarView.getCustomAddView()对你添加的view进行操作；
 * <p>
 * navigationBarView.getCustomAddView().animate().rotation(180).setDuration(400);
 * <p>
 * <p>
 * 点击第一个页面中的按钮、跳转到第二个页面并执行其中的方法（部分人可选择无视）
 * <p>
 * ------------------ 第二个页面添加方法供Activity调用
 * <p>
 * //提示消息
 * public void showToast(String str) {
 * Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
 * }
 * 第一个页面中调用代码
 * <p>
 * //跳转第二个页面
 * ((AddActivity) getActivity()).getNavigitionBar().selectTab(1);
 * //调用第二个页面的方法
 * ((SecondFragment) (((AddActivity) getActivity()).getNavigitionBar().getAdapter().getItem(1))).showToast("嘻嘻哈哈嗝");
 */
package com.eco.view.navigation;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.daimajia.androidanimations.library.Techniques;

import java.util.List;

public class NavigationBarBuilder {
  private NavigationBarView navigationBarView;

  public NavigationBarBuilder(NavigationBarView navigationBarView) {
    this.navigationBarView = navigationBarView;
  }

  /**
   * 设置选项卡 标题图标
   *
   * @param tabText 标题
   * @param iconOff 未选中图标
   * @param iconOn  选中图标
   * @return
   */
  public NavigationBarBuilder setLayout(String[] tabText, int[] iconOff, int[] iconOn) {
    navigationBarView.titleItems(tabText)   //必传  Tab文字集合
        .normalIconItems(iconOff)   //必传  Tab未选中图标集合
        .selectIconItems(iconOn);   //必传  Tab选中图标集合
    return this;
  }

  /**
   * 设置切换的fragments
   *
   * @param fragments
   * @param fragmentManager
   * @return
   */
  public NavigationBarBuilder setFragment(List<Fragment> fragments, FragmentManager fragmentManager) {
    navigationBarView.fragmentList(fragments)       //必传  fragment集合
        .fragmentManager(fragmentManager); //必传
    return this;
  }

  /**
   * 设置属性
   *
   * @return
   */
  public NavigationBarBuilder initStyle() {
    navigationBarView
        .defaultSetting()
        .iconSize(20)      //Tab图标大小
        .centerIconSize(26)    //中间加号图片的大小
        .tabTextTop(2)     //Tab文字距Tab图标的距离
        .tabTextSize(10)     //Tab文字距Tab图标的距离
        .anim(Techniques.Pulse)  //点击Tab时的动画
        .hintPointLeft(-5) //红点居左位置
        .hintPointTop(-10) //红点居上位置
        .hintPointSize(8)  //提示红点的大小
        .msgPointLeft(-5) //调节数字消息的位置msgPointLeft msgPointTop（看文档说明）
        .msgPointTop(-10)  //数字居上位置
        .msgPointTextSize(8) //数字消息中字体大小
        //.msgPointSize(12)    //数字消息红色背景的大小
        .smoothScroll(false)  //点击Tab  Viewpager切换是否有动画
        .canScroll(true)    //Viewpager能否左右滑动
        .mode(NavigationBarView.MODE_NORMAL)
        .navigationHeight(50)
    ;
    return this;
  }

  /**
   * 编译生效
   */
  public void build() {
    navigationBarView.build();
  }

}
