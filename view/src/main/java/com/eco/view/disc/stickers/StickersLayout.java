package com.eco.view.disc.stickers;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.eco.view.R;
import com.eco.basics.handler.DensityHandler;
import com.eco.view.constants.DyConstants;
import com.eco.view.handler.ViewHandler;
import com.eco.view.DyEditText;
import com.eco.view.builder.DyImageViewBuilder;
import com.eco.view.builder.DyLinearLayoutBuilder;
import com.eco.view.disc.stickers.model.Stickers;
import com.eco.view.disc.stickers.model.StickersPageRange;
import com.eco.view.disc.stickers.view.StickersView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表情盘
 */
public class StickersLayout extends LinearLayout {

  private FragmentManager fm;
  private DyEditText targetEditText; //表情输出view
  private Map<Bitmap, Map<Integer, List<Stickers>>> stickersMap; //表情盘map，key:表情盘菜单图标, value:(key:表情类型，value:表情内容列表)
  private OnClickListener sendClickListener; //发送点击事件监听

  public StickersView stickersView; //表情盘内容view
  private LinearLayout pagePointLayout; //分页点布局
  private LinearLayout addStickersPackageBtn; //添加表情包按钮
  private LinearLayout menuLayout; //菜单列表布局
  private LinearLayout sendBtn; //发送按钮
  private Map<String, Integer> outputStickersMap = new HashMap<>(); //输出过的表情，删除时候对比使用

  private float layoutHeight = 0; //整体布局高度
  private float stickersViewHeight = 0; //表情盘内容布局高度

  public StickersLayout(@NonNull Context context) {
    super(context);
    init(context);
  }

  public StickersLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.layout_stickers, null);
    addView(view);
    stickersView = view.findViewById(R.id.stickers_view);
    pagePointLayout = view.findViewById(R.id.page_point_layout);
    addStickersPackageBtn = view.findViewById(R.id.add_stickers_package_btn);
    menuLayout = view.findViewById(R.id.menu_layout);
    sendBtn = view.findViewById(R.id.send_btn);

    layoutHeight = DensityHandler.getDimenPx(context, R.dimen.disc_height);
    stickersViewHeight = layoutHeight - DensityHandler.getDimensPx(context, R.dimen.disc_page_point_size, R.dimen.disc_menu_height); //盘高度 - (分页点高度 + 菜单高度)
  }

  public StickersLayout init(FragmentManager fm) {
    this.fm = fm;
    return this;
  }

  /**
   * 设置表情目标view（只有emoji,unicode输出，image直接调用发送按钮）
   *
   * @param targetEditText
   * @return
   */
  public StickersLayout targetView(DyEditText targetEditText) {
    this.targetEditText = targetEditText;
    return this;
  }

  /**
   * 设置表情内容
   *
   * @param stickersMap
   * @return
   */
  public StickersLayout addStickers(Map<Bitmap, Map<Integer, List<Stickers>>> stickersMap) {
    this.stickersMap = stickersMap;
    return this;
  }

  /**
   * 发送按钮点击事件
   *
   * @param sendClickListener
   * @return
   */
  public StickersLayout sendClick(OnClickListener sendClickListener) {
    this.sendClickListener = sendClickListener;
    return this;
  }

  /**
   * 生成表情盘
   */
  public void build() {
    Context context = getContext();
    stickersView.getLayoutParams().height = (int) stickersViewHeight;

    stickersView.init(fm) //初始化
      .addStickersClickListener((stickersType, name) -> { //每个表情的点击事件
        int cursorPosition = targetEditText.getSelectionStart();
        Editable editable = targetEditText.getText();
        if (DyConstants.BACKSPACE.equals(name)) { //删除
          targetEditText.executeBackspace();
        } else {
          if (DyConstants.STICKERS_TYPE.APP_EMOJI.v().equals(stickersType)) {
            outputStickersMap.put(name, null);
            editable.insert(cursorPosition, ViewHandler.emojiTextMixed(context, targetEditText.getTextSize(), name));
          } else if (DyConstants.STICKERS_TYPE.SYS_EMOJI.v().equals(stickersType)) {
            editable.insert(cursorPosition, name);
          }
        }
      })
      .addPageSelectedListener(currentPage -> { //翻页监听, 每翻一页就重新生成一次 分页点布局
        StickersPageRange stickersPageRange = stickersView.getStickersPageRange(currentPage);
        if (null == stickersPageRange) return;
        //生成表情分页点布局
        pagePointLayout.removeAllViews();
        for (int i = stickersPageRange.getBegin(); i <= stickersPageRange.getEnd(); i++) {
          int wh = R.dimen.disc_page_point_size, drawable = currentPage == i ? R.drawable.dy_ic_point_on : R.drawable.dy_ic_point_off;

          final int itemIndex = i - 1; //页面的实际索引
          DyImageViewBuilder stickersPagePoint = DyImageViewBuilder.init(context).image(drawable)
            .layout(v -> v.wh((int) DensityHandler.getDimenPx(context, wh)))
            .click(v -> stickersView.setCurrentItem(itemIndex));

          pagePointLayout.addView(stickersPagePoint.build());
        }
      });

    float menuIcSize = DensityHandler.getDimenPx(context, R.dimen.stickers_menu_ic_size); //表情盘菜单图标大小
    float menuIcMargin = DensityHandler.getDimenPx(context, R.dimen.stickers_menu_ic_margin); //表情盘菜单图标间距
    DyLinearLayoutBuilder menuIcLayoutBuilder = DyLinearLayoutBuilder.init(context).layout(v -> v.wh(ViewHandler.WRAP_CONTENT, ViewHandler.MATCH_PARENT).gravity(Gravity.CENTER)); //表情盘单个菜单图标布局
    DyImageViewBuilder menuIcImageBuilder = DyImageViewBuilder.init(context).image(R.drawable.dy_ic_plus_square).layout(v -> v.wh((int) menuIcSize, (int) menuIcSize).marginsX((int) menuIcMargin)); //表情包单个菜单图标
    //生成表情盘菜单和内容
    for (Map.Entry<Bitmap, Map<Integer, List<Stickers>>> stickersEntry : stickersMap.entrySet()) {
      View vlineFineView = View.inflate(context, R.layout.vline_fine, null); //竖杠
      LinearLayout menuIcLayout = menuIcLayoutBuilder.build(menuIcImageBuilder.build(stickersEntry.getKey()), vlineFineView); //表情盘单个菜单图标布局
      menuLayout.addView(menuIcLayout); //设置表情盘菜单图标
      //生成表情盘内容
      stickersView.build(stickersEntry.getValue(), menuIcLayout, (int) stickersViewHeight);
    }
    if (null != stickersView) { //触发翻页监听，生成分页点布局
      stickersView.getPageSelectedListener().onPageSelected(stickersView.getCurrentPage());
    }

    sendBtn.setOnClickListener(sendClickListener); //发送按钮点击事件
  }


  /**
   * 输出过的表情
   *
   * @return
   */
  public Map<String, Integer> getOutputStickersMap() {
    return outputStickersMap;
  }

  //表情盘整体布局高度
  public float getLayoutHeight() {
    return layoutHeight;
  }
}