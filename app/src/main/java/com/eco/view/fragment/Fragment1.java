package com.eco.view.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eco.basics.handler.JsonHandler;
import com.eco.basics.handler.ResourcesHandler;
import com.eco.view.DyEditText;
import com.eco.view.R;
import com.eco.view.constants.DyConstants;
import com.eco.view.disc.extend.ExtendLayout;
import com.eco.view.disc.extend.model.Extend;
import com.eco.view.disc.stickers.StickersLayout;
import com.eco.view.disc.stickers.model.Stickers;
import com.eco.view.handler.ToastHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Fragment1 extends Fragment {

  private DyEditText imContentEdit;
  private ImageView stickersImage;
  private ImageView extendImage;
  private StickersLayout stickersLayout; //表情盘布局VIEW
  private ExtendLayout extendLayout; //扩展盘布局

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragement_main1, null);
    imContentEdit = view.findViewById(R.id.im_content_edit);
    stickersImage = view.findViewById(R.id.stickers_image);
    extendImage = view.findViewById(R.id.extend_image);
    stickersLayout = view.findViewById(R.id.stickers_layout);
    extendLayout = view.findViewById(R.id.extend_layout);

    stickersImage.setOnClickListener(v -> toggle(1));
    extendImage.setOnClickListener(v -> toggle(2));

    //退格键
    imContentEdit.setBackspaceListener(() -> {
      Integer cursorPosition = imContentEdit.getSelectionStart(); //光标位置
      if (cursorPosition <= 0) return true;
      Editable editable = imContentEdit.getText();
      Integer deletePosition = cursorPosition - 1; //删除位置
      if (']' == editable.charAt(deletePosition)) { //匹配是否符合emoji格式
        Integer prefixPosition = editable.toString().substring(0, deletePosition).lastIndexOf("[");
        String stickersName = editable.toString().substring(prefixPosition, cursorPosition); //表情名称
        if (stickersLayout.getOutputStickersMap().containsKey(stickersName)) { //如果与map匹配 则增加删除范围，否则删除一位
          editable.delete(prefixPosition, cursorPosition); //删除表情'[微笑]'
          return true;
        }
      }
      imContentEdit.onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)); //调用系统退格键
      return true;
    });

    initStickers(); //生成表情盘
    initExtend(); //生成扩展盘
    return view;
  }

  private void initStickers() {
    //表情内容，key:表情类型，value:表情内容列表
    Map<Integer, List<Stickers>> stickersContentMap = new LinkedHashMap() {{
      put(DyConstants.STICKERS_TYPE.APP_EMOJI.v(), ResourcesHandler.loadJson("json", "AppEmoji", Stickers.class));
    }};
    Map<Integer, List<Stickers>> stickersContentMap1 = new LinkedHashMap() {{
      put(DyConstants.STICKERS_TYPE.SYS_EMOJI.v(), ResourcesHandler.loadJson("json", "SystemEmoji", Stickers.class));
    }};

    //表情盘map，key:表情盘菜单图标, value:(key:表情类型，value:表情内容列表)
    Map<Bitmap, Map<Integer, List<Stickers>>> stickersMap = new LinkedHashMap<>();
    stickersMap.put(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stickers, new BitmapFactory.Options()), stickersContentMap);
    stickersMap.put(BitmapFactory.decodeResource(getResources(), R.drawable.ic_mind_off, new BitmapFactory.Options()), stickersContentMap1);
    //生成表情盘
    stickersLayout.init(getParentFragmentManager()).targetView(imContentEdit).addStickers(stickersMap).sendClick(v -> ToastHandler.success(getContext(), "发送内容:" + imContentEdit.getText().toString())).build();
  }


  private void initExtend() {
    //生成扩展盘
    List<Extend> extendList = (List<Extend>) ResourcesHandler.loadJson("json", "ImExtend", Extend.class);
    extendLayout.init(getParentFragmentManager()).addExtend(extendList).addExtendClickListener(extendType -> {
      if (extendType == 1) {
        ToastHandler.success(getContext(), "打开照相机");
      } else if (extendType == 2) {
        ToastHandler.success(getContext(), "打开相册");
      } else {
        ToastHandler.success(getContext(), "打开其他");
      }
    }).build();
  }

  private void toggle(Integer discType) {
    if (discType == 1) {
      stickersLayout.setVisibility(View.VISIBLE);
      extendLayout.setVisibility(View.GONE);
    } else if (discType == 2) {
      stickersLayout.setVisibility(View.GONE);
      extendLayout.setVisibility(View.VISIBLE);
    }
  }


}