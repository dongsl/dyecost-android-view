package com.eco.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.eco.view.R;
import com.eco.view.fragment.Fragment1;
import com.eco.view.navigation.BottomNavBarView;
import com.eco.view.navigation.BottomNavigationBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private BottomNavigationBuilder bottomNavigationBuilder;
  private BottomNavBarView bottomNavBarView;

  private String[] tabText = {"消息", "联系人", "游戏", "发现", "我"};
  private int[] iconOff = {R.drawable.ic_message_off, R.drawable.ic_contacts_off, R.drawable.ic_mind_off, R.drawable.ic_star_off, R.drawable.ic_my_off}; //未选中icon
  private int[] iconOn = {R.drawable.ic_message_on, R.drawable.ic_contacts_on, R.drawable.ic_mind_on, R.drawable.ic_star_on, R.drawable.ic_my_on}; //选中时icon
  private List<Fragment> fragments = new ArrayList<>(Arrays.asList(new Fragment1(), new Fragment1(), new Fragment1(), new Fragment1()));

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    bottomNavBarView = findViewById(R.id.bottom_nav_bar_view);
    initBottomNavBar();
  }

  public void initBottomNavBar() {
    bottomNavBarView.selectTab(0);
    bottomNavigationBuilder = new BottomNavigationBuilder(bottomNavBarView);
    bottomNavigationBuilder.setFragment(fragments, getSupportFragmentManager()); //初始化页面
    bottomNavigationBuilder.setLayout(tabText, iconOff, iconOn).initStyle().build();
  }


}
