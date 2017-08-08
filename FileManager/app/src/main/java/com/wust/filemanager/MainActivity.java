package com.wust.filemanager;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
//import android.widget.SearchView;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wust.filemanager.Utils.FileManager;
import com.wust.filemanager.adapter.ClassifyGridHomePageAdapter;
import com.wust.filemanager.adapter.MainPagerAdaper;
import com.wust.filemanager.adapter.StorageGridAdapter;
import com.wust.filemanager.fragment.ClassifyFragment;
import com.wust.filemanager.fragment.StorageFragment;
import com.wust.filemanager.listener.PopButtonOnClickListener;
import com.wust.filemanager.listener.StorageGridViewListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    /**
     * 主页viewpager，含有两个fragment
     */
    private ViewPager mViewpager;

    public TextView classifyTv;
    public TextView storageTv;

    /**
     *  用于fragment的管理
     *  用法：fragmentManager是activity和fragment的唯一类，fragment的增加是不需要获得activity的content layout来通过addView()添加
     *       每次fragment的增删都是同过fm开启事务操作的
     *  获取：fm不能通过new获得，必须activity.getFragmentManager()
     *
     *  适配器：FragmentPageAdapter 只需要重写两个方法就可以，getCount、getItem，构造函数传入fm，和List<fragment>
     */
    private FragmentManager fm;
    private List<Fragment> fragmentLists;

    private LinearLayout mNav;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);//使用toolbar的前提是当前页面使用无actionbar主题android:theme="@style/AppTheme.NoActionBar"

        //进来就设置toolbar颜色
        FileManager.getInstance().mCurrentColorId = getSharedPreferences("data",MODE_PRIVATE).getInt("colorId", FileManager.getInstance().mCurrentColorId);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(FileManager.getInstance().mCurrentColorId));

        initView();

        iniEvent();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(PopButtonOnClickListener.getInstant());
        FileManager.getInstance().setFab(fab);
        fab.setVisibility(View.GONE);


                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //以下initview 和 initevent 是我写的两个方法
    private void initView()
    {

        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mViewpager.addOnPageChangeListener(new StorageGridViewListener());
        fm = getSupportFragmentManager();

        //下面是标题和viewpager的联动
        classifyTv = (TextView) findViewById(R.id.classify_textview);
        storageTv = (TextView) findViewById(R.id.storage_textview);
        classifyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewpager.setCurrentItem(0);
            }
        });
        storageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewpager.setCurrentItem(1);
            }
        });
        classifyTv.setTypeface(Typeface.DEFAULT_BOLD);
        classifyTv.setTextColor(Color.parseColor("#ffffff"));
        storageTv.setTypeface(Typeface.DEFAULT);
        storageTv.setTextColor(Color.parseColor("#BDBDBD"));

        mNav = (LinearLayout) findViewById(R.id.nav);
        mNav.setBackgroundColor(getResources().getColor(FileManager.getInstance().mCurrentColorId));
    }

    private void iniEvent()
    {
        FileManager.getInstance().setMainActivity(MainActivity.this);//将activity引用保存在filemanager，因为在popmenu监听类中要执行Activity.runOnUiThread()
        fragmentLists = new ArrayList<Fragment>();
        ClassifyFragment classifyFragment = new ClassifyFragment();
        StorageFragment storageFragment = new StorageFragment();
        fragmentLists.add(classifyFragment);
        fragmentLists.add(storageFragment);
        mViewpager.setAdapter(new MainPagerAdaper(fm,fragmentLists));


    }

    /**
     * 重写后退按键方法
     *
     * 流程：1、先判断抽屉界面是否在前端
     *      2、判断，目录是否退到根"/"目录，是就交给上层super.onBackPressed()，否就将目录路径退到父目录，
     *         将FileManager中的Stack<string>.pop()一层
     *
     */
    private int backCount = -1;
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else if(FileManager.getInstance().mCurrentViewPagerId ==0 ){//处理当pagerID,即在分类页面时，做相关操作，不影响storage页面

            //此时当分类页面在inner里面的时候，做处理
            if(ClassifyGridHomePageAdapter.getInstance().classifyMode == 1){
                ClassifyGridHomePageAdapter.getInstance().goOutDataset00();
            }else if(backCount == -1){backCount++;Toast.makeText(this,"真的残忍的退出么?(・ω・)",Toast.LENGTH_SHORT).show();}else {super.onBackPressed();}

        }else if ("/".equals(FileManager.getInstance().getCurrentDir()))
        {
            if(backCount == -1){
                backCount++;
                Toast.makeText(this,"真的残忍的退出么？(・ω・)",Toast.LENGTH_SHORT).show();
                }else {super.onBackPressed();}
        } else
        {
            //在适配器的构造器中就放到filemanager中了
            StorageGridAdapter.getInstant().setDatas(FileManager.getInstance().getPreviousDir());
        }
    }

    /**
     * 这里可以写不少东西，为什么不用TouchEvent()等等，写一些时间分发机制
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        backCount = -1;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //这部分分page页面来显示action menu item
        getMenuInflater().inflate(R.menu.main, menu);
        FileManager.getInstance().mMenu = menu;
        MenuItem homeItem = menu.findItem(R.id.action_home);
        MenuItem undoItem = menu.findItem(R.id.action_undo);
        homeItem.setVisible(false);
        undoItem.setVisible(false);

        //下面是 searchview的配置
        MenuItem searchItem = menu.findItem(R.id.action_searching);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) searchItem.getActionView();//这两个加载view的方法都可以
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);                        //这里第二个参数必须是全名
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName("com.wust.filemanager","com.wust.filemanager.SearchableActivity")));
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(true);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               // Toast.makeText(MainActivity.this,"submit",Toast.LENGTH_SHORT).show();

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
               // Toast.makeText(MainActivity.this,"change",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
//        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
//            @Override
//            public boolean onSuggestionSelect(int position) {
//                return false;
//            }
//            @Override
//            public boolean onSuggestionClick(int position) {
//                return false;
//            }
//        });

        /*
         * ##：首先searchView的配置：
         *     >首先searchView.setSearchableInfo(SearchableInfo),这个参数是system根据我们配置searchable xml文件创建的一个实例
         *      具体的代码就是上面所写的。
         *     >
         * ##：下面是searchable xml和activity的配置：
         *
         * 当我们执行一个searchview的时候，system会创建一个Intent，查询到的数据就存储在里面。然后system会
         * 用这个intent打开一个Activity（你准备处理显示这个intent数据的searchable activty），为了让你的
         * 程序有这种功能，你必须设置下面的一些选项：
         * 1、 实现一个 searchable00 xml configuration。
         *     一个xml文件配置search dialog或searchview。包含着一些search box的voice searh，search suggestion，
         *     hint text等的特性设置...
         *
         * 2、 实现一个searchable Activity。
         *     接受查询的关键字、并根据关键字查询数据、然后显示你的数据。
         *
         * 3、 一个 search 接口，也就是ui 。
         *     the search dialog：搜索框。默认情况下，对话框是隐藏的，但是当你调用 onSearchRequested()，也就是
         *                        当用户按下搜素按钮时，会在屏幕的最顶端显示出来。
         *     a SearchView widget:使用这个searchview，你可以把它当作一个view组件放在界面的任何地方。不过，tongchang
         *                         作为放在app bar作为一个action button。
         *
         *（1）、创建一个Searchable xml配置文件
         *       -首先习惯上命名为： searchable0000.xml,必须放在 res/xml 工程目录下。（system会根据这个配置文件
         *       生成一个 SearchableInfo 的实例对象，不必用户创建，我们要做的就是配置这个文件）
         *       -root element 标签必须是<searchable00>, 属性 android:label="application name" 是唯一要求必须指明的属性。
         *
         *（2）、创建一个Searchable Activity
         *       当用户用searchView执行搜索时，system会自动打开这个activity，还会将保存数据的Intent（action：ACTIONz_SEARCH）
         *       传递给这个activity。activity通过Intent的QUERY属性字段得到要检索的关键字。
         *       声明一个Searchable Activity：
         *       因为searchView在其他Activity中，所以为了system能从MainActivity自动转到searchActivity中，你必须在androidmanifest
         *       文件中声明你这个searchable activity。
         *       A：指明Intent的Action：ACTION_SEARCH
         *       B：指明配置好的Searchable XML文件
         *       <activity android:name=".SearchableActivity"
         *                 android:launchMode="singleTop">
         *           <intent-filter>
         *               < action android:name="android.intent.action.SEARCH" />//这里不需要<category>应为这里system
         *           </intent-filter>                                           //会根据它的组件名字和action精准的找到searchable activity
         *           <meta-data android:name="android.app.searchable00"   //必须这样写
          *                     android:resource="@xml/searchable00"/>    //必须这样写
         *       </activity>
         *（3）、接下来 接受查询关键字、根据关键字查询数据、显示数据的操作的笔记在Searchable Activity写。
         *
         *（4）、要成功跳转，searchview.setOnQueryTextListener(new ..);中设置的两个方法，只能return false。
         *
         *
         * ##： 上面两个是searchview和searchable activity的两个配置
         *      那么下面是 Suggestions 的配置。
         * 1、 add recent query suggestions:
         *
         * 2、
         * */

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
      switch (item.getItemId())
      {
          case R.id.action_undo:
              FileManager.getInstance().getFab().setVisibility(View.GONE);//撤销复制和移动操作
              item.setVisible(false);
              return true;
          case R.id.action_searching:
              return true;
          case R.id.action_home:
              StorageGridAdapter.getInstant().setDatas(FileManager.getInstance().setHomeDir(Environment.getExternalStorageDirectory().getPath()));
              return true;
          case R.id.action_changecolor:
              FileManager.getInstance().changeColor();
              getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(FileManager.getInstance().mCurrentColorId));
              mNav.setBackgroundColor(getResources().getColor(FileManager.getInstance().mCurrentColorId));
              SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
              editor.putInt("colorId",FileManager.getInstance().mCurrentColorId);
              editor.commit();

          default:
              return super.onOptionsItemSelected(item);
      }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera)
        {
            Intent intent = new Intent(MainActivity.this,AppManageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery)
        {

        } else if (id == R.id.nav_slideshow)
        {

        } else if (id == R.id.nav_manage)
        {
            Intent intent = new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share)
        {

        } else if (id == R.id.nav_send)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();

    }

}


/**
 * toolbar:
 *     api21 版本5.0.1，更多的是通过spport v7包来导入并和AppCompatActivity配合使用，使用的时候当前Activity必须设置隐藏actionbar，并在其
 *     activity中设置在：setSupportActionBar(toolbar)。
 *
 *     增加按钮：toolbar和Activity联合，按钮的增加只需要在menu xml文件当中增加一个item即可，具体加载进Activity是通过activity的方法：
 *     onCreateOptionMenu(munu)中加载menu的xml文件，在xml属性中showAsAction="always"表示不显示在menu overflow中，"ifRoom"表示有空间就显示在
 *     boolbar上，没有就显示在overflow中，"never"表示永远显示在overflow中
 *
 *     响应事件：在方法onOptionsItemSelected(MenuItem item)中，通过switch和item.getItemId()来相应各个menu按钮的事件
 *
 *
 * 通过toolbar，很方便的点击toolbar的up action从Child Activity跳回到 Main Activity：
 *     1.首先是在androidmanifest中对child Activity的属性的声明：android:parentActivityName=".MainActivity"，这个在android 4.1 api16之上是
 *     支持的，对于4.0及以下可以通过设置标签<meta_data/>，具体为  <meta-data android:name="android.support.PARENT_ACTIVITY"
 *     android:value="com.example.myfirstapp.MainActivity" />。
 *     2.在child Activity中获取并设置
 *             setContentView(R.layout.setting_activity);//当然layout中也要放置一个toolbar
 *             Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
 *             setSupportActionBar(toolbar);
 *             ActionBar ab = getSupportActionBar();
 *     3.配置响应：ab.setDisplayHomeAsUpEnabled(true);//设置这一项，才最终起作用：child activity中的toolbar产生一个up action的按钮
 *     4.这个up action不需要我们写onOptionsItemSelected()中的响应事件
 *
 *
 * toolbar上面的action view及provider：
 *     1.在menu的xml属性中主要设置两个：app:showAsAction="ifRoom|collapseActionView" //这里或者"never|collapseActionView"
 *                                    app:actionViewClass="android.support.v7.widget.SearchView"
 *                                    //app:actionLayout=""
 *     2.对action view进行获得并设置事件监听处理在方法onCreateOptionsMenu(),见上面代码。
 *
 *
 *
 *
 *
 *
 *setOnSystemUiVisibilityChangeListenersetOnSystemUiVisibilityChangeListener
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
