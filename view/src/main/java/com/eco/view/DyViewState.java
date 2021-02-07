package com.eco.view;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * DyView默认参数
 */
public class DyViewState {

  public static final int TRANSLUCENT = 128; //半透明
  //背景色
  public static final int BG_COLOR_ALPHA = 255; //默认背景色透明度(0-255(不透明))
  //边框
  public static final int BORDER_WIDTH = 0;
  public static final int BORDER_COLOR = Color.BLACK;
  //阴影
  public static final int SHADOW_SIZE = 0; //默认阴影大小
  public static final int SHADOW_ALPHA = 255; //默认阴影透明度(0-255(不透明))
  //文字
  public static final int TEXT_COLOR = Color.WHITE;

  /**
   * 形状类型
   */
  public enum SHAPE_TYPE {
    RECTANGLE(1, "矩形"),
    FILLET(2, "圆角"),
    CIRCULAR(3, "圆形");
    private Integer value;
    private String text;

    SHAPE_TYPE(Integer value, String text) {
      this.value = value;
      this.text = text;
    }

    public Integer v() {
      return this.value;
    }

    public String t() {
      return this.text;
    }
  }

  /**
   * 圆角方向
   */
  public enum FILLET_DIRECTION {
    FILLET_LEFT_TOP(1, "左上"),
    FILLET_LEFT_BOTTOM(1 << 1, "左下"), //2
    FILLET_RIGHT_TOP(1 << 2, "右上"), //4
    FILLET_RIGHT_BOTTOM(1 << 3, "右下"); //8

    private Integer value;
    private String text;

    FILLET_DIRECTION(Integer value, String text) {
      this.value = value;
      this.text = text;
    }

    public Integer v() {
      return this.value;
    }

    public String t() {
      return this.text;
    }
  }

  /**
   * 文字位置
   */
  public enum TEXT_LOCATION {
    CENTER(1, "中间"),
    CENTER_HORIZONTAL(1 << 1, "水平剧中"), //2
    CENTER_VERTICAL(1 << 2, "垂直剧中"), //4
    TOP(1 << 3, "上"), //8
    BOTTOM(1 << 4, "下"),
    LEFT(1 << 5, "左"),
    RIGHT(1 << 6, "右"),
    START(1 << 7, "起始位置 - view左上"),
    CENTER_TOP_HALF(1 << 8, "水平居中,top+文字高度的一半(进度条使用,DyView中不进行计算)");

    private Integer value;
    private String text;

    TEXT_LOCATION(Integer value, String text) {
      this.value = value;
      this.text = text;
    }

    public Integer v() {
      return this.value;
    }

    public String t() {
      return this.text;
    }
  }

  /**
   * 颜色渐变方向
   */
  public enum COLOR_GD {
    HORIZONTAL(1, "水平"),
    VERTICAL(2, "垂直"); //8

    private Integer value;
    private String text;

    COLOR_GD(Integer value, String text) {
      this.value = value;
      this.text = text;
    }

    public Integer v() {
      return this.value;
    }

    public String t() {
      return this.text;
    }
  }

  /**
   * 按下类型
   */
  public enum DOWN_TYPE {
    OVERRIDE(1, "在view最顶层覆盖一层"),
    BACKGROUND(2, "替换背景（颜色或背景图，暂时只实现背景色）"),
    RESIZE(3, "缩方view");
    private Integer value;
    private String text;

    DOWN_TYPE(Integer value, String text) {
      this.value = value;
      this.text = text;
    }

    public Integer v() {
      return this.value;
    }

    public String t() {
      return this.text;
    }
  }


  //自定义imageView
  public static class DyIv {
    //图片
    public static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    public static final int COLOR_DRAWABLE_DIMENSION = 2;

    public enum SCALE_TYPE {
      PROPORTIONAL(1, "按照view高宽等比例缩放(根据高宽最小的比例进行缩放，保持图片的原有比例)"),
      SELF(2, "按照view高宽调整图片大小(可能会导致图片变形)"),
      ;
      private Integer value;
      private String text;

      SCALE_TYPE(Integer value, String text) {
        this.value = value;
        this.text = text;
      }

      public Integer v() {
        return this.value;
      }

      public String t() {
        return this.text;
      }
    }
  }

  //自定进度条linerLayout
  public static class DyPbll {
    //形状
    public enum SHAPE_TYPE {
      RECTANGLE_H(1, "水平矩形"),
      RECTANGLE_H_IMAGE(2, "水平矩形+图片"),
      CIRCULAR(3, "圆形"),
      ;
      private Integer value;
      private String text;

      SHAPE_TYPE(Integer value, String text) {
        this.value = value;
        this.text = text;
      }

      public Integer v() {
        return this.value;
      }

      public String t() {
        return this.text;
      }
    }

    //进度条生成方向
    public enum DIRECTION {
      ALONG(1, "左到右，顺时针"),
      INVERSE(2, "右到左，逆时针"),
      ;
      private Integer value;
      private String text;

      DIRECTION(Integer value, String text) {
        this.value = value;
        this.text = text;
      }

      public Integer v() {
        return this.value;
      }

      public String t() {
        return this.text;
      }
    }

    //进度条分割线
    public static final float PB_CUT_RATIO = 0.1f; //进度条分割线比例(10%)
    public static final int PB_CUT_WIDTH = 0; //进度条分割线宽度
    public static final int PB_CUT_COLOR = Color.WHITE; //进度条分割线颜色

    //进度条
    public static final int PB_COLOR = Color.WHITE; //进度条颜色
    public static final int PB_ALPHA = 255; //进度条透明度(0-255(不透明))

    //进度条类型
    public enum PB_RATIO_TYPE {
      PERCENTAGE(1, "百分比(RECTANGLE_* 形状的进度条只能用百分比)"),
      SECONDS(2, "秒,此类型时调用startCountDownTimer后会开启倒计时"),
      ;
      private Integer value;
      private String text;

      PB_RATIO_TYPE(Integer value, String text) {
        this.value = value;
        this.text = text;
      }

      public Integer v() {
        return this.value;
      }

      public String t() {
        return this.text;
      }
    }

    //进度条进度方式
    public enum PB_RATIO_MODE {
      INCR(1, "递增"),
      DECR(2, "递减"),
      ;
      private Integer value;
      private String text;

      PB_RATIO_MODE(Integer value, String text) {
        this.value = value;
        this.text = text;
      }

      public Integer v() {
        return this.value;
      }

      public String t() {
        return this.text;
      }
    }
  }


  //自定义弧形linerLayout
  public static class DyArcll {
    public static final int DIRECTION = 0; //弧形方向，0上，1下
    public static final int ARC_HEIGHT = 0; //弧形高度
  }

  //自定三角形view
  public static class DyTrv {
    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 3;

    public static final int DIRECTION = TOP;
  }

  //提示点(数字气泡,角标)
  public static class DyBv {
    public static final int TEXT_POINT = 0; //数字气泡
    public static final int CORNER_SIGN = 1; //角标
  }

  //多种类view(箭头标签，标题标签等)
  public static class DyMv {

    public enum SHAPE_TYPE {
      ARROW_LABEL(1, "箭头标签"),
      BANNER_I(2, "横幅"),
      BANNER_SILK(3, "锦旗"),
      ;
      private Integer value;
      private String text;

      SHAPE_TYPE(Integer value, String text) {
        this.value = value;
        this.text = text;
      }

      public Integer v() {
        return this.value;
      }

      public String t() {
        return this.text;
      }
    }
  }

  //线条view
  public static class DyLv {
    public static Integer HORIZONTAL = 1;
    public static Integer VERTICAL = 2;
  }

}