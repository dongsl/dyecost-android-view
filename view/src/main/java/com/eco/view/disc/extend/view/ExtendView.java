package com.eco.view.disc.extend.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;

import com.eco.view.R;
import com.eco.basics.handler.DensityHandler;
import com.eco.basics.handler.Num;
import com.eco.view.disc.extend.adapter.ExtendAdapter;
import com.eco.view.disc.extend.fragment.ExtendFragment;
import com.eco.view.disc.extend.model.Extend;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展盘内容VIEW
 */
public class ExtendView extends ViewPager {

    private FragmentManager fm;
    private PageSelectedListener pageSelectedListener; //翻页监听
    private PageScrolledListener pageScrolledListener; //滑动监听
    private ExtendClickListener extendClickListener; //扩展按钮点击事件监听
    private List<Fragment> fragmentList = new ArrayList<>();
    private int pageCount = 0; //总页数
    private int currentPage = 1; //当前页，默认第一页

    private int rowNumber = 2; //每页显示几行
    private int rowViewNumber = 4; //每行view数量

    public ExtendView(@NonNull Context context) {
        super(context);
    }

    public ExtendView(@NonNull Context context, @Nullable AttributeSet attrs) {
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

    public ExtendView init(FragmentManager fm) {
        this.fm = fm;
        return this;
    }

    /**
     * 扩展按钮点击事件监听
     *
     * @param extendClickListener
     * @return
     */
    public ExtendView addExtendClickListener(ExtendClickListener extendClickListener) {
        this.extendClickListener = extendClickListener;
        return this;
    }

    /**
     * 翻页监听
     *
     * @param pageSelectedListener
     * @return
     */
    public ExtendView addPageSelectedListener(PageSelectedListener pageSelectedListener) {
        this.pageSelectedListener = pageSelectedListener;
        return this;
    }

    /**
     * 滑动监听
     *
     * @param pageScrolledListener
     * @return
     */
    public ExtendView addPageScrolledListener(PageScrolledListener pageScrolledListener) {
        this.pageScrolledListener = pageScrolledListener;
        return this;
    }

    /**
     * 生成扩展盘的每页内容
     *
     * @param extendList
     * @param extendViewHeight 扩展盘内容布局高度
     * @return
     */
    public void build(List<Extend> extendList, float extendViewHeight) {
        Context context = getContext();
        int pageNumber = rowNumber * rowViewNumber; //每页数量

        float screenWidth = DensityHandler.getScreenWidth(context); //页面宽度
        float layoutMargin = DensityHandler.getDimenPx(context, R.dimen.layout_margin);

        //扩展盘布局可用宽度 = 页面宽度 - 左右间距
        float extendLayoutWidth = screenWidth - layoutMargin * 2;
        //扩展盘每行布局可用高度 = 扩展盘可用高度(view高度 / 行数)， 必须：扩展盘每行布局可用高度 >= extendBtnSize + extendBtnTextSize + extendBtnTextMarginTop
        int extendRowLayoutHeight = Num.dForInt(extendViewHeight, rowNumber);
        float extendBtnWidth = extendLayoutWidth / rowViewNumber; //每个按钮占用的宽度 = 可用宽度 / 按钮个数

        //按钮布局大小上限值 = 扩展盘每行布局可用高度 或 每个按钮占用的宽度(使用较小的一方)
        float extendBtnSizeLimit = extendBtnWidth > extendRowLayoutHeight ? extendRowLayoutHeight : extendBtnWidth;
        extendBtnSizeLimit = Num.mForInt(extendBtnSizeLimit, 0.75); //最多使用75%，剩余部分空白当作间距

        float extendBtnTextSize = DensityHandler.getDimenPx(context, R.dimen.text_size12); //按钮名称字号
        float extendBtnTextMarginTop = DensityHandler.getDimenPx(context, R.dimen.extend_btn_margin_top); //按钮名称居上间距
        int textHintColor = ContextCompat.getColor(context, R.color.text_hint);


        //每个按钮图片布局的大小 = 按钮布局大小上限值 - 按钮名称字号 - 按钮名称居上间距
        float extendBtnImageLayoutSize = extendBtnSizeLimit - extendBtnTextSize - extendBtnTextMarginTop;
        float extendBtnImageSize = Num.mForF(extendBtnImageLayoutSize, 0.5f); //每个按钮图片的大小()

        List<List<Extend>> extendPageList = new ArrayList<>(); //扩展盘每页list
        List<Extend> extendRowList = new ArrayList<>(); //扩展盘每行list
        for (int i = 0, size = extendList.size(); i < size; i++) {
            extendRowList.add(extendList.get(i));

            int position = i + 1;
            boolean isLast = (position == size); //是否为最后一位

            if (position % rowViewNumber == 0 || isLast) {
                extendPageList.add(extendRowList);
                extendRowList = new ArrayList<>();
            }
            if (position % pageNumber == 0 || isLast) {
                //生成表情每页fragment
                ExtendFragment extendFragment = new ExtendFragment(extendPageList);
                extendFragment.setLayoutParam(layoutMargin, extendRowLayoutHeight);
                extendFragment.setBtnLayoutParam(extendBtnWidth, extendBtnImageLayoutSize, extendBtnImageSize, extendBtnTextSize, extendBtnTextMarginTop, textHintColor);

                extendFragment.setListener(extendClickListener);
                fragmentList.add(extendFragment); //将页布局传入到fragme
                extendPageList = new ArrayList<>();
                pageCount++;
            }
        }

        this.setOffscreenPageLimit(1); //默认加载下标0和1的页面数据，  limit=1 表示当前显示0页 加载1页
        this.setAdapter(new ExtendAdapter(fm, fragmentList));
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

    //翻页监听
    public interface PageSelectedListener {
        void onPageSelected(int currentPage);
    }

    //滑动监听
    public interface PageScrolledListener {
        void onPageScrolled(int position, float positionOffset, float positionOffsetPixels);
    }

    //扩展按钮点击事件监听
    public interface ExtendClickListener {
        void click(int extendType);
    }

}