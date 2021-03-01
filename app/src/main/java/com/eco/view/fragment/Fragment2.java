package com.eco.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.Num;
import com.eco.view.DySlideButtonView;
import com.eco.view.DyTabPageLayout;
import com.eco.view.DyViewState;
import com.eco.view.R;
import com.eco.view.alert.AlertCallBack;
import com.eco.view.alert.AlertHandler;
import com.eco.view.alert.AlertView;
import com.eco.view.builder.DyLinearLayoutBuilder;
import com.eco.view.builder.DyTextViewBuilder;
import com.eco.view.handler.PopHandler;
import com.eco.view.handler.ToastHandler;
import com.eco.view.handler.ViewHandler;
import com.eco.view.loading.DyLoadingView;
import com.eco.view.loading.LoadingDialog;

public class Fragment2 extends Fragment {

  private Context context;
  //pop, toast
  private Button success_button;
  private Button warning_button;
  private Button error_button;
  private Button pop_button;
  private PopHandler popHandler;
  //alert
  public Button alert_confirm_button;
  public Button alert_bottom_confirm_button;
  public Button alert_msg_button;
  public Button alert_dialog_button;
  public Button alert_edit_button;
  public Button alert_list_button;
  public Button alert_view_button;
  //loading
  public Button loading_default_button;
  public Button loading_style_button;
  public Button loading_style_red_button;

  public DySlideButtonView slide;
  private DyTabPageLayout tab;

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragement_main2, null);

    context = getContext();
    success_button = view.findViewById(R.id.success_button);
    warning_button = view.findViewById(R.id.warning_button);
    error_button = view.findViewById(R.id.error_button);
    pop_button = view.findViewById(R.id.pop_button);
    alert_confirm_button = view.findViewById(R.id.alert_confirm_button);
    alert_bottom_confirm_button = view.findViewById(R.id.alert_bottom_confirm_button);
    alert_msg_button = view.findViewById(R.id.alert_msg_button);
    alert_dialog_button = view.findViewById(R.id.alert_dialog_button);
    alert_edit_button = view.findViewById(R.id.alert_edit_button);
    alert_list_button = view.findViewById(R.id.alert_list_button);
    alert_view_button = view.findViewById(R.id.alert_view_button);
    loading_default_button = view.findViewById(R.id.loading_default_button);
    loading_style_button = view.findViewById(R.id.loading_style_button);
    loading_style_red_button = view.findViewById(R.id.loading_style_red_button);
    slide = view.findViewById(R.id.slide);
    tab = view.findViewById(R.id.tab);
    init();
    bindEvent();
    return view;
  }

  public void init() {
    popHandler = new PopHandler(getActivity());
    popHandler.init(R.layout.pop_plus);
    slide.setChecked(true);

    float screenWidth = DensityHandler.getScreenWidth(context);
    //---商品 - 标签---
    int tabNumber = 3; //标签数量
    float kcDyFillet = 30; //圆角半径
    float tabMargin = 6; //标签间距(标题, 页头, 页脚等)
    //---商品 - 标签 - 标题---
    float tabTitleWidth = Num.dForInt(screenWidth, tabNumber); //标签标题宽度
    float tabTitleHeight = Num.dForInt(tabTitleWidth, tabNumber); //标签标题高度
    int tabTitleFilletDirection = DyViewState.FILLET_DIRECTION.FILLET_LEFT_TOP.v() | DyViewState.FILLET_DIRECTION.FILLET_RIGHT_TOP.v(); //标签圆角方向
    float tabTitleTextSize = DyTextViewBuilder.initTextSizeForHeight(context, Num.mForF(tabTitleHeight, 0.5f)); //标签标题文字大小
    //---商品 - 标签vp---
    float tabVpHeaderHeight = 20; //vp头高度
    float tabVpFooterHeight = 60; //vp页脚高度
    int tabVpFooterFilletDirection = DyViewState.FILLET_DIRECTION.FILLET_LEFT_BOTTOM.v() | DyViewState.FILLET_DIRECTION.FILLET_RIGHT_BOTTOM.v(); //vp页脚圆角方向


    //---商品 - 标签vp---
    //标签vp背景布局
    DyLinearLayoutBuilder tabPageVpBgLayoutBuilder = DyLinearLayoutBuilder.init(context)
        .layout(v -> v
            .wh(ViewHandler.MATCH_PARENT, (int) tabVpHeaderHeight).gravity(Gravity.TOP)
            .paddingX((int) tabMargin).paddingY(null, (int) tabMargin)
            .bgColor(Color.parseColor("#000000"), 128)
        );
    //生成标签vp头布局
    DyLinearLayoutBuilder tabPageVpHeaderLayoutBuilder = DyLinearLayoutBuilder.init(context).layout(v -> v.whm().bgColor(ContextCompat.getColor(context, R.color.bg)));
    View vpHeaderLayout = tabPageVpBgLayoutBuilder.build(tabPageVpHeaderLayoutBuilder.build());
    //生成标签vp页脚布局
    DyLinearLayoutBuilder tabPageVpFooterLayoutBuilder = DyLinearLayoutBuilder.init(context)
        .layout(v -> v.whm()
            .shape(null, DyViewState.SHAPE_TYPE.FILLET.v(), kcDyFillet, tabVpFooterFilletDirection)
            .bgColor(ContextCompat.getColor(context, R.color.bg))
        );
    tabPageVpBgLayoutBuilder.layout(v -> v
        .wh(ViewHandler.MATCH_PARENT, (int) tabVpFooterHeight)
        .paddingY((int) tabMargin, (int) tabMargin)
        .shape(null, DyViewState.SHAPE_TYPE.FILLET.v(), kcDyFillet, tabVpFooterFilletDirection)
    );
    View vpFooterLayout = tabPageVpBgLayoutBuilder.build(tabPageVpFooterLayoutBuilder.build());
    //生成标签布局
    tab
        .init(getParentFragmentManager())
        .setFragments(new FragmentTab1(), new FragmentTab2(), new FragmentTab3())
        .setTitleTexts("热卖", "道具", "皮肤")
        .setTitleTextSize(tabTitleTextSize)
        .setTitleWh(tabTitleWidth, tabTitleHeight)
        .setTitleWeight(1f)
        .setVpHeader(vpHeaderLayout)
        .setVpFooter(vpFooterLayout)
        .setTitleMargins(tabMargin, tabMargin, tabMargin, null)
        .setStyle(v -> v
            .setShape(DyViewState.SHAPE_TYPE.FILLET.v())
            .setFilletRadius(kcDyFillet)
            .setFilletDirection(tabTitleFilletDirection)
        )
        .setTabClickListener((v, position) -> ToastHandler.success(getContext(), "点击" + position + "个标签"))
        .build();

  }

  public void bindEvent() {
    success_button.setOnClickListener(v -> ToastHandler.success(context, "成功"));
    warning_button.setOnClickListener(v -> ToastHandler.warning(context, "警告"));
    error_button.setOnClickListener(v -> ToastHandler.error(context, "失败"));
    pop_button.setOnClickListener(v -> popHandler.show(v));

    alert_confirm_button.setOnClickListener(v -> AlertHandler.confirm(context, "是否下载", new AlertCallBack() {
      @Override
      public void onFirst() {
        ToastHandler.success(context, "下载成功");
      }

      @Override
      public void onCancel() {
        ToastHandler.error(context, "下载失败");
      }
    }));

    alert_bottom_confirm_button.setOnClickListener(v -> AlertHandler.confirmBottom(context, "确认要下载文件吗", "确认下载", new AlertCallBack() {
      @Override
      public void onFirst() {
        ToastHandler.success(context, "下载成功");
      }

      @Override
      public void onCancel() {
        ToastHandler.error(context, "下载失败");
      }
    }));

    alert_msg_button.setOnClickListener(v -> AlertHandler.alertMsgForOne(context, "您的帐号在其它地方登录，您已被迫下线", "重新登录", new AlertCallBack() {
      @Override
      public void onFirst() {
        ToastHandler.success(context, "登录成功");
      }
    }));

    alert_dialog_button.setOnClickListener(v -> {
      View view = LayoutInflater.from(context).inflate(R.layout.layout_alert, null);
      AlertHandler.view(context, view, Boolean.TRUE).show();
    });

    alert_edit_button.setOnClickListener(v -> AlertHandler.editText(context, "昵称", "dyecost", 10, 1, new AlertCallBack() {
      @Override
      public void onFirst(String content) {
        ToastHandler.success(context, "修改成功:" + content);
      }
    }));

    alert_list_button.setOnClickListener(v -> {
      new AlertView.Builder()
          .setContext(context)
          .setStyle(AlertView.Style.ActionSheet)
          .setTitle("列表弹框")
          .setDestructive("按钮1", "按钮2")
          .setOthers("其他按钮1", "其他按钮2", "其他按钮3")
          .setCancelText("确认")
          .setOnItemClickListener((o, position) -> {
            ToastHandler.success(context, o + ":" + position);
          })
          .build()
          .show();
    });

    alert_view_button.setOnClickListener(v -> new AlertView.Builder()
        .setContext(context)
        .setStyle(AlertView.Style.Alert)
        .setTitle("自定义弹框")
        .setMessage("可选择弹出AlertView.Style.*, 设置标题,描述,按钮")
        .setCancelText("确认")
        .build()
        .show());

    loading_default_button.setOnClickListener(v -> LoadingDialog.init(context).text("正在进入房间").show());
    loading_style_button.setOnClickListener(v -> LoadingDialog.init(context).style("SemiCircleSpinIndicator", Color.WHITE).text("正在进入房间").show());
    loading_style_red_button.setOnClickListener(v -> LoadingDialog.init(context).style("SemiCircleSpinIndicator", Color.RED).text("正在进入房间").show());


  }
}