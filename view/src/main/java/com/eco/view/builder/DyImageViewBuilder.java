package com.eco.view.builder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.eco.basics.handler.ImageHandler;
import com.eco.view.DyImageView;

/**
 * imageView生成器
 * 1.可以在build(image)时设置图片内容
 * 2.使用buildFillet(radius)生成圆角view
 * -----------------demo-----------------
 * DyImageViewBuilder.init(context).image()
 * .layout(v -> v.wh())
 * .buildFillet();
 */
public class DyImageViewBuilder extends DyViewBuilder {
  private DyImageView imageView;
  private DyImageView.ScaleType scaleType;
  private Object image; //图片地址（资源ID，资源，url）
  private Float zoomRatio;
  private Boolean autoZoom;

  private DyImageViewBuilder(Context context) {
    this.context = context;
  }

  public static DyImageViewBuilder init(Context context) {
    DyImageViewBuilder view = new DyImageViewBuilder(context);
    return view;
  }

  public DyImageViewBuilder layout(ViewBuilderLayout viewBuilderLayout) {
    viewBuilderLayout.set(this);
    return this;
  }

  public DyImageViewBuilder scaleType(ImageView.ScaleType scaleType) {
    this.scaleType = scaleType;
    return this;
  }

  public DyImageViewBuilder image(Object image) {
    this.image = image;
    return this;
  }

  public DyImageViewBuilder click(View.OnClickListener onClickListener) {
    this.onClickListener = onClickListener;
    return this;
  }

  public DyImageViewBuilder zoom(Float zoomRatio, Boolean autoZoom) {
    this.zoomRatio = zoomRatio;
    this.autoZoom = autoZoom;
    return this;
  }

  public DyImageView build(Object image) {
    image(image);
    return build();
  }

  public DyImageView build() {
    return buildFillet(null);
  }

  public DyImageView buildFillet(Float filletRadius) {
    imageView = new DyImageView(context);
    super.build(imageView, imageView.getDyView());
    if (null != image) {
      if (null == filletRadius) {
        ImageHandler.loadImage(imageView, image);
      } else {
        ImageHandler.loadImageFillet(imageView, image, filletRadius);
      }
    }
    if (null != scaleType) imageView.setScaleType(scaleType);
    if (null != onClickListener) imageView.setOnClickListener(onClickListener);
    if (null != zoomRatio) imageView.zoomRatio = zoomRatio;
    if (null != autoZoom) imageView.autoZoom = autoZoom;
    imageView.invalidate();
    return imageView;
  }
}
