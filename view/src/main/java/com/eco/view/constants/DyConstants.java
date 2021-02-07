package com.eco.view.constants;

public interface DyConstants {
  String EMOJI = "emoji"; //表情图片位置
  String EMOJI_SUFFIX = "png";
  String JSON = "json"; //json文件位置
  String BACKSPACE = "BACKSPACE"; //退格

  /**
   * 未阅读数量类型
   */
  enum STICKERS_TYPE {
    APP_EMOJI(1, "应用表情符号"),
    SYS_EMOJI(2, "系统表情符号"),
    STICKERS(3, "表情包");

    private Integer value;
    private String text;

    STICKERS_TYPE(Integer value, String text) {
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
