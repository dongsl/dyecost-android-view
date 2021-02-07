package com.eco.view.disc.stickers.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.eco.basics.handler.ResourcesHandler;
import com.eco.basics.handler.StringHandler;
import com.eco.view.builder.DyImageViewBuilder;
import com.eco.view.builder.DyTextViewBuilder;
import com.eco.view.constants.DyConstants;

import lombok.Data;

@Data
public class Stickers {

  public Stickers() {
  }

  public Stickers(String name) {
    this.name = name;
  }

  private String name;
  private Bitmap bitmap;
  private View view;

  public void setName(String name) {
    this.name = name;
    /*if (StringHandler.isNotBlank(name) && null == bitmap) {
        bitmap = ResourcesHandler.loadEmoji(getName()); //加载本地图片
    }*/
  }

  //获取表情view
  public View getView(Context context, Integer stickersType, float stickersSize, float margin) {
    if (StringHandler.isBlank(name) || null != view) return view;

    if (DyConstants.STICKERS_TYPE.APP_EMOJI.v().equals(stickersType)) { //系统表情
      if (null == bitmap) {
        bitmap = ResourcesHandler.loadBitmap(DyConstants.EMOJI, name, DyConstants.EMOJI_SUFFIX); //加载本地图片
      }
      if (null != bitmap) {
        view = DyImageViewBuilder.init(context).image(bitmap).layout(v -> v.wh((int) stickersSize).marginsX((int) margin)).build(); //表情
      }
    } else if (DyConstants.STICKERS_TYPE.SYS_EMOJI.v().equals(stickersType)) { //文字表情
      view = DyTextViewBuilder.init(context).layout(v -> v.wh((int) stickersSize).marginsX((int) margin).text(name).textSize(stickersSize * 0.8f)).build();
    } else if (DyConstants.STICKERS_TYPE.STICKERS.v().equals(stickersType)) { //表情包

    }
    return view;
  }
}