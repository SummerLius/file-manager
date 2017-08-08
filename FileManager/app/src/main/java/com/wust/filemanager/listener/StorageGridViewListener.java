package com.wust.filemanager.listener;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.wust.filemanager.R;
import com.wust.filemanager.Utils.FileManager;
import com.wust.filemanager.Utils.OpenFileUtils;
import com.wust.filemanager.adapter.StorageGridAdapter;
import com.wust.filemanager.entity.Item;

import java.io.File;

/**
 * Created by admin on 2016/4/30.
 */
public class StorageGridViewListener implements AdapterView.OnItemClickListener ,AdapterView.OnItemLongClickListener,AbsListView.OnScrollListener,ViewPager.OnPageChangeListener
{
    private int mFirstVisibleItem;

    @Override   //点击监听器方法
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        StorageGridAdapter adapter = (StorageGridAdapter) parent.getAdapter();
        Item item = (Item) adapter.getItem(position);
        if (!new File(FileManager.getInstance().getCurrentDir()+"/"+item.getName()).canRead()){//首先判断文件是否可读，下面在此基础上在判断
            Toast.makeText(view.getContext(),"文件不可读！",Toast.LENGTH_SHORT).show();
        } else if ("DIR".equals(item.getIcon())){
            adapter.setDatas(FileManager.getInstance().getNextDir(item.getName()));
        } else {
             //如果是文件暂时不做任何处理，此时文件是可读的
            OpenFileUtils.openFile(new File(FileManager.getInstance().getCurrentDir()+"/"+item.getName()));
        }
        //先判断是否可读，可读再判断是否是目录，是就进入目录，不是就对文件进行处理
        //目录为空也正常显示，只是说显示为空白


    }

    @Override  //长按监听器方法
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        return false;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        mFirstVisibleItem = firstVisibleItem;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }

    @Override
    public void onPageSelected(int position)
    {
        FileManager.getInstance().mCurrentViewPagerId = position;
        Menu menu = FileManager.getInstance().mMenu;//注意findItem和getItem的区别
        MenuItem searchItem = menu.findItem(R.id.action_searching);
        MenuItem homeItem = menu.findItem(R.id.action_home);
        MenuItem undoItem = menu.findItem(R.id.action_undo);//undo不再这里联动，dudo随着fab联动

        if(position ==0){
            searchItem.setVisible(true);
            homeItem.setVisible(false);
            FileManager.getInstance().getMainActivity().classifyTv.setTypeface(Typeface.DEFAULT_BOLD);
            FileManager.getInstance().getMainActivity().classifyTv.setTextColor(Color.parseColor("#ffffff"));
            FileManager.getInstance().getMainActivity().storageTv.setTypeface(Typeface.DEFAULT);
            FileManager.getInstance().getMainActivity().storageTv.setTextColor(Color.parseColor("#BDBDBD"));

        }else {
            searchItem.setVisible(false);
            homeItem.setVisible(true);
            FileManager.getInstance().getMainActivity().classifyTv.setTypeface(Typeface.DEFAULT);
            FileManager.getInstance().getMainActivity().classifyTv.setTextColor(Color.parseColor("#BDBDBD"));
            FileManager.getInstance().getMainActivity().storageTv.setTypeface(Typeface.DEFAULT_BOLD);
            FileManager.getInstance().getMainActivity().storageTv.setTextColor(Color.parseColor("#ffffff"));
        }


    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }
}
