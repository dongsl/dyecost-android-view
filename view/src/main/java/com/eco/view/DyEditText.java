package com.eco.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import lombok.Getter;

/**
 * 自定义editText（退格键，软键盘关闭等）
 * 1.在editText中显示imageSpan时，退格键会存在问题，通过backspaceListener自定义删除事件
 * 2.在打开软键盘后，后退键默认是关闭软键盘，通过keyPreImeListener自定义后退键事件
 * -----------------demo-----------------
 * xml与editText用法相同
 * 退格键监听：
 * view.setBackspaceListener(() -> {
 * view.onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)); //调用系统退格键
 * return true;
 * });
 * 软键盘关闭监听：
 * imContentEdit.setKeyPreImeListener((k, event) -> {
 * if (k == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
 * finish();
 * return true;
 * }
 * return false;
 * });
 */
public class DyEditText extends AppCompatEditText implements DyBaseView {
  @Getter
  public DyView dyView;
  public boolean removeUnderline; //移除下划线

  private DyInputConnection inputConnection = new DyInputConnection(null, true);
  private BackspaceListener backspaceListener; //退格键
  private KeyPreImeListener keyPreImeListener; //软键盘关闭监听，后退按钮

  public DyEditText(Context context) {
    this(context, null);
  }

  public DyEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public DyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr); //android.R.attr.editTextStyle
    init(context, attrs);
  }

  public void init(Context context, AttributeSet attrs) {
    dyView = new DyView(this, context, attrs);
    if (null != attrs) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dy_edit_text);
      removeUnderline = typedArray.getBoolean(R.styleable.dy_edit_text_dyet_remove_underline, true);
      typedArray.recycle();
    }
  }

  @Override
  public void onDraw(Canvas canvas) {
    dyView.initView();
    initEdit();
    dyView.clearCanvas(canvas);
    dyView.drawShadow(canvas);
    dyView.drawBg(canvas);
    super.onDraw(canvas);
    dyView.drawBorder(canvas);
    dyView.drawDown(canvas);
    //dyView.drawPoint(canvas);
  }

  public void initEdit() {
    //防止超出最大行数
    DyEditText view = this;

    view.addTextChangedListener(new TextWatcher() {
      String currentText = "";

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        int lines = view.getLineCount();
        // 限制最大输入行数
        if (lines > view.getMaxLines()) {
          view.setText(currentText);
          view.setSelection(currentText.length());
        } else if (lines <= view.getMaxLines()) {
          currentText = null != s ? s.toString() : "";
        }
      }
    });

    if (removeUnderline) {
      this.setBackgroundColor(ContextCompat.getColor(this.getContext(), R.color.transparent));
    }
  }


  /**
   * 当输入法和EditText建立连接的时候会通过这个方法返回一个InputConnection。
   * 我们需要代理这个方法的父类方法生成的InputConnection并返回我们自己的代理类。
   */
  @Override
  public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    inputConnection.setTarget(super.onCreateInputConnection(outAttrs));
    return inputConnection;
  }

  /**
   * 执行退格键监听
   * 手动调用，不是在软键盘上点击的
   *
   * @return
   */
  public Boolean executeBackspace() {
    if (null != backspaceListener) {
      return backspaceListener.onBackspace();
    }
    return Boolean.FALSE;
  }

  /**
   * 打开软键盘后按下后退键
   *
   * @param keyCode
   * @param event
   * @return
   */
  @Override
  public boolean onKeyPreIme(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
      if (null != keyPreImeListener) { //调用自定义监听
        return keyPreImeListener.onKeyPreIme(keyCode, event); //false：事件将传递给下一个接收器
      }
            /*super.onKeyPreIme(keyCode, event);
            return false;*/
    }
    return super.onKeyPreIme(keyCode, event);
  }

  public void setBackspaceListener(BackspaceListener backspaceListener) {
    this.backspaceListener = backspaceListener;
  }

  public void setKeyPreImeListener(KeyPreImeListener keyPreImeListener) {
    this.keyPreImeListener = keyPreImeListener;
  }

  /**
   * 退格键监听
   */
  public interface BackspaceListener {
    boolean onBackspace(); //true 代表消费了这个事件，不传递给下一个接收器
  }

  /**
   * 软键盘关闭监听，后退按钮
   */
  public interface KeyPreImeListener {
    Boolean onKeyPreIme(int keyCode, KeyEvent event); //true 不传递给下一个接收器
  }

  class DyInputConnection extends InputConnectionWrapper {
    public DyInputConnection(InputConnection target, boolean mutable) {
      super(target, mutable);
    }

    /**
     * 当在软件盘上点击某些按钮（比如退格键，数字键，回车键等
     * <p>
     * 当软键盘删除文本之前，会调用这个方法通知输入框，我们可以重写这个方法并判断是否要拦截这个删除事件。
     * 在谷歌输入法上，点击退格键的时候不会调用{@link #sendKeyEvent(KeyEvent event)}，
     * 而是直接回调这个方法，所以也要在这个方法上做拦截；
     */
    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
      if (backspaceListener != null) {
        if (backspaceListener.onBackspace()) {
          return true;
        }
      }
      return super.deleteSurroundingText(beforeLength, afterLength);
    }

    /**
     * 当在软件盘上点击某些按钮（比如退格键，数字键，回车键等
     * <p>
     * 该方法可能会被触发（取决于输入法的开发者），
     * 所以也可以重写该方法并拦截这些事件，这些事件就不会被分发到输入框了
     */
    @Override
    public boolean sendKeyEvent(KeyEvent event) {
      if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
        if (backspaceListener != null && backspaceListener.onBackspace()) {
          return true;
        }
      }
      return super.sendKeyEvent(event);
    }
  }

  public DyEditText setRemoveUnderline(boolean removeUnderline) {
    if (this.removeUnderline != removeUnderline) {
      this.removeUnderline = removeUnderline;
      invalidate();
    }
    return this;
  }

  /*@Override
  public boolean onTouchEvent(MotionEvent event) {
    dyView.onTouchEvent(event);
    super.onTouchEvent(event);
    return this.hasOnClickListeners();
  }*/

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    dyView.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((int) dyView.maxWidth, (int) dyView.maxHeight);
  }
}