package com.eco.view.disc.stickers.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StickersHandler {
  /**
   * 将系统表情转化为字符串
   *
   * @param s
   * @return
   */
  public static String emojiToStr(String s) {
    String context = "";
    //循环遍历字符串，将字符串拆分为一个一个字符
    for (int i = 0, length = s.length(); i < length; i++) {
      char codePoint = s.charAt(i);
      //判断字符是否是emoji表情的字符
      if (isEmojiCharacter(codePoint)) {
        //如果是将以大括号括起来
        String emoji = "{" + Integer.toHexString(codePoint) + "}";
        context = context + emoji;
        continue;
      }
      context = context + codePoint;
    }
    return context;
  }

  /**
   * 是否包含表情
   *
   * @param codePoint
   * @return 如果不包含 返回false,包含 则返回true
   */

  private static boolean isEmojiCharacter(char codePoint) {
    return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
      || (codePoint == 0xD)
      || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
      || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
  }

  /**
   * 是否包含表情
   *
   * @param str
   * @return 如果不包含 返回false,包含 则返回true
   */

  private static boolean isEmoji(String str) {
    for (int i = 0, length = str.length(); i < length; i++) {
      char codePoint = str.charAt(i);

      if (!((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
        || (codePoint == 0xD)
        || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
        || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))) {
        return Boolean.TRUE;
      }

    }
    return Boolean.FALSE;
  }

  /**
   * 将表情描述转换成表情
   *
   * @param str
   * @return
   */
  public static String strToEmoji(String str) {
    String rep = "\\{(.*?)\\}";
    Pattern p = Pattern.compile(rep);
    Matcher m = p.matcher(str);
    while (m.find()) {
      String s1 = m.group();
      String s2 = s1.substring(1, s1.length() - 1);
      String s3;
      try {
        s3 = String.valueOf((char) Integer.parseInt(s2, 16));
        str = str.replace(s1, s3);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return str;
  }
}
