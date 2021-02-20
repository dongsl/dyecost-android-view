package com.eco.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.eco.view.R;
import com.eco.view.fragment.Fragment1;
import com.eco.view.fragment.Fragment2;
import com.eco.view.fragment.Fragment3;
import com.eco.view.fragment.Fragment4;
import com.eco.view.fragment.Fragment5;
import com.eco.view.navigation.NavigationBarView;
import com.eco.view.navigation.NavigationBarBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private NavigationBarBuilder navigationBarBuilder;
  private NavigationBarView bottomNavBarView;

  private String[] tabText = {"消息", "控件", "dy-view1", "dy-view2", "阻尼效果"};
  private int[] iconOff = {R.drawable.ic_message_off, R.drawable.ic_contacts_off, R.drawable.ic_mind_off, R.drawable.ic_star_off, R.drawable.ic_my_off}; //未选中icon
  private int[] iconOn = {R.drawable.ic_message_on, R.drawable.ic_contacts_on,R.drawable.ic_mind_on, R.drawable.ic_star_on, R.drawable.ic_my_on}; //选中时icon
  private List<Fragment> fragments = new ArrayList<>(Arrays.asList(new Fragment1(), new Fragment2(), new Fragment3(), new Fragment4(), new Fragment5()));

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    bottomNavBarView = findViewById(R.id.bottom_nav_bar_view);
    initBottomNavBar();
  }

  public void initBottomNavBar() {
    navigationBarBuilder = new NavigationBarBuilder(bottomNavBarView).initStyle();
    navigationBarBuilder.setFragment(fragments, getSupportFragmentManager()); //初始化页面
    navigationBarBuilder.setLayout(tabText, iconOff, iconOn).build();
    bottomNavBarView.selectTab(0, true, false);
  }
}