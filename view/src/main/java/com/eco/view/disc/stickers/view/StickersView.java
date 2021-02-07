package com.eco.view.disc.stickers.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.eco.view.R;
import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.Num;
import com.eco.view.constants.DyConstants;
import com.eco.view.disc.stickers.adapter.StickersAdapter;
import com.eco.view.disc.stickers.fragment.StickersFragment;
import com.eco.view.disc.stickers.model.Stickers;
import com.eco.view.disc.stickers.model.StickersPageRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 表情盘内容VIEW
 */
public class StickersView extends ViewPager {

  private FragmentManager fm;
  private PageSelectedListener pageSelectedListener; //翻页监听
  private PageScrolledListener pageScrolledListener; //滑动监听
  private StickersClickListener stickersClickListener; //表情点击事件监听
  private List<Fragment> fragmentList = new ArrayList<>();
  private int pageCount = 0; //总页数
  private int currentPage = 1; //当前页，默认第一页
  private List<StickersPageRange> stickersPageRangeList = new ArrayList<>();

  public StickersView(@NonNull Context context) {
    super(context);
  }

  public StickersView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    //滑动监听
    addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // 页面正在滚动时不断调用
        if (null != pageScrolledListener) {
          pageScrolledListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
      }

      @Override
      public void onPageSelected(int position) {
        // 页面选中时调用, 当新页面将被选中时调用，动画不是必需完成的
        currentPage = position + 1;
        if (null != pageSelectedListener) {
          pageSelectedListener.onPageSelected(currentPage); //初始化分页点
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {
        // 页面的滚动状态变化时调用
      }
    });
  }

  public StickersView init(FragmentManager fm) {
    this.fm = fm;
    return this;
  }

  /**
   * 表情点击事件监听
   *
   * @param stickersClickListener
   * @return
   */
  public StickersView addStickersClickListener(StickersClickListener stickersClickListener) {
    this.stickersClickListener = stickersClickListener;
    return this;
  }

  /**
   * 翻页监听
   *
   * @param pageSelectedListener
   * @return
   */
  public StickersView addPageSelectedListener(PageSelectedListener pageSelectedListener) {
    this.pageSelectedListener = pageSelectedListener;
    return this;
  }

  /**
   * 滑动监听
   *
   * @param pageScrolledListener
   * @return
   */
  public StickersView addPageScrolledListener(PageScrolledListener pageScrolledListener) {
    this.pageScrolledListener = pageScrolledListener;
    return this;
  }

  /**
   * 生成表情盘的每页内容
   *
   * @param stickersContentMap         表情盘内容map（一个表情包可能包含多种类型的表情）
   * @param stickersMenuIcSingleLayout 当前表情包对应的菜单图标布局
   * @param stickersViewHeight         表情盘内容布局高度
   * @return
   */
  public void build(Map<Integer, List<Stickers>> stickersContentMap, LinearLayout stickersMenuIcSingleLayout, int stickersViewHeight) {
    int beginPage = pageCount + 1; //起始页，从1开始计算
    Context context = getContext();
    float usedWidth = DensityHandler.getDimensPx(context, R.dimen.layout_margin_left, R.dimen.layout_margin_left); //已用宽度
    float screenWidth = DensityHandler.getScreenWidth(context) - usedWidth; //屏幕剩余可用宽度

    float stickersLayoutMarginX = DensityHandler.getDimenPx(context, R.dimen.layout_margin); //表情盘布局水平间距

    float stickersSize = DensityHandler.getDimenPx(context, R.dimen.emoji_size); //每个表情大小
    float stickersMarginX = DensityHandler.getDimenPx(context, R.dimen.stickers_margin_x); //每个表情水平间距

    int rowNumber = 3; //每页显示行数
    for (Map.Entry<Integer, List<Stickers>> entry : stickersContentMap.entrySet()) {
      int stickersType = entry.getKey(); //表情类型：app表情，系统表情，表情包
      List<Stickers> stickersList = entry.getValue();
      //根据表情类型 设置展示行数和表情大小
      if (!DyConstants.STICKERS_TYPE.APP_EMOJI.v().equals(stickersType) && !DyConstants.STICKERS_TYPE.SYS_EMOJI.v().equals(stickersType)) {
        stickersSize = DensityHandler.getDimenPx(context, R.dimen.emoji_size); //表情大小
        rowNumber = 2;
      }
      int stickersRowLayoutHeight = Num.dForInt(stickersViewHeight, rowNumber); //表情盘每行布局高度 = 表情盘内容布局高度 / 每页显示行数

      float stickerUseWidth = stickersSize + stickersMarginX * 2; //每个表情使用的宽度 = 表情大小 + 表情水平间距 * 2
      int rowViewNumber = Num.dForInt(screenWidth, stickerUseWidth); //每行表情数量 =（屏幕宽度 / 每个表情使用的宽度）
      int pageNumber = rowViewNumber * rowNumber; //每页表情数量 = 表情盘每行数量 * 行数
      float residualWidth = screenWidth - stickerUseWidth * rowViewNumber; //表情盘每行剩余宽度 = 屏幕宽度 - 每个表情使用的宽度 * 表情盘每行数量

      if (residualWidth >= rowViewNumber * 2) { //验证剩余宽度是否能够,把每行的表情都加上水平间距 (最小宽度 = rowViewNumber * 2)
        stickersMarginX += residualWidth / rowViewNumber / 2; //将剩余宽度增加到 每个表情水平间距中 = 每个表情水平间距 + 每个表情新增宽度(表情盘每行剩余宽度 / 表情盘每行数量) / 2(左右两个间距)
      } else { //不满足时 将剩余宽度增加到 表情盘布局中
        stickersLayoutMarginX += residualWidth / 2; //将剩余宽度增加到 表情盘布局左右间距中
      }

      int rowCount = 0; //已生成的行数量
      int position = 1; //当前位置（由于有 backspace 表情 会导致与实际size不符，使用此字段记录实际位置）

      List<List<Stickers>> stickersPageList = new ArrayList<>(); //表情盘每页list
      List<Stickers> stickersRowList = new ArrayList<>(); //表情盘每行list

      for (int i = 0, size = stickersList.size(); i < size; i++) {
        Stickers stickers = stickersList.get(i);
        stickersRowList.add(stickers);

        Boolean isLast = (i == (size - 1)); //是否为最后一位
        int nextPosition = position + 1; //下一个表情位置

        Stickers backspaceStickers = new Stickers(DyConstants.BACKSPACE); //退格键
        if (nextPosition % pageNumber == 0) { //下一个位置 = 当前页中最后一个表情位置时，增加backspace表情
          stickersRowList.add(backspaceStickers);
          position++;
        }

        if (position % rowViewNumber == 0 || isLast) { //表情位置 % 每行表情数量 = 将行布局add到页布局中 并 创建新的一行
          ++rowCount; //行数量

          stickersPageList.add(stickersRowList);
          //生成当前页
          if (rowCount == rowNumber || isLast) { //当前行数量 == 每页行数量时 生成页面 并 创建新的页布局
            for (int j = rowCount; j < rowNumber; j++) { //在创建新的页面时，如果当前行数小于 目标行数则创建出对应的空行
              stickersRowList = new ArrayList<>(); //表情盘每行list
              if (j == rowNumber - 1) {
                stickersRowList.add(backspaceStickers);
              }
              stickersPageList.add(stickersRowList);
            }

            //生成表情每页fragment
            StickersFragment stickersFragment = new StickersFragment(stickersType, stickersPageList);
            stickersFragment.setLayoutParam(stickersLayoutMarginX, stickersRowLayoutHeight, stickersSize, stickersMarginX);
            stickersFragment.setListener(stickersClickListener);
            fragmentList.add(stickersFragment); //将页布局传入到fragment中生成一页

            stickersPageList = new ArrayList<>();
            rowCount = 0; //重置行布局数量
            pageCount++;
          }
          stickersRowList = new ArrayList<>(); //表情盘每行list
        }
        position++;
      }
    }
    stickersMenuIcSingleLayout.setOnClickListener(v -> StickersView.this.setCurrentItem(beginPage - 1)); //设置表情盘菜单图标点击跳转页面
    stickersPageRangeList.add(new StickersPageRange(beginPage, pageCount)); //记录每个表情包的 起始和结束页数
    this.setOffscreenPageLimit(1); //默认加载下标0和1的页面数据，  limit=1 表示当前显示0页 加载1页
    this.setAdapter(new StickersAdapter(fm, fragmentList));
  }

  public PageSelectedListener getPageSelectedListener() {
    return pageSelectedListener;
  }

  public int getPageCount() {
    return pageCount;
  }

  public int getCurrentPage() {
    return currentPage;
  }


  /**
   * 根据当前页获取 表情包范围
   * 每个表情包的起始和结束下标位置
   *
   * @param currentPage
   * @return
   */
  public StickersPageRange getStickersPageRange(int currentPage) {
    for (StickersPageRange stickersPageRange : stickersPageRangeList) {
      if (currentPage >= stickersPageRange.getBegin() && currentPage <= stickersPageRange.getEnd()) {
        return stickersPageRange;
      }
    }
    return null;
  }

  //翻页监听
  public interface PageSelectedListener {
    void onPageSelected(int currentPage);
  }

  //滑动监听
  public interface PageScrolledListener {
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
  }

  //表情点击事件监听
  public interface StickersClickListener {
    void click(int stickersType, String name);
  }

}