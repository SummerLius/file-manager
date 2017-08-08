package com.wust.filemanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.wust.filemanager.R;
import com.wust.filemanager.Utils.FileManager;
import com.wust.filemanager.Utils.Search;
import com.wust.filemanager.entity.Item;
import com.wust.filemanager.listener.ClassifyViewListener;
import com.wust.filemanager.listener.PopButtonOnClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by admin on 2016/4/24.
 */
public class ClassifyGridHomePageAdapter extends BaseAdapter01
{
    public List<Item> mDadaSet;
    private List<Item> mDadaSet00;
    private List<Item> mDadaSet01;
    private Context mContext;
    private LayoutInflater mInflate;
    public int classifyMode = 0;
    public ArrayList<Item> mAudioItems = new ArrayList<Item>();
    public ArrayList<Item> mVedioItems = new ArrayList<Item>();
    public ArrayList<Item> mImgItems = new ArrayList<Item>();
    public ArrayList<Item> mDocItems = new ArrayList<Item>();
    public ArrayList<Item> mApkItems = new ArrayList<Item>();
    public ArrayList<Item> mCompressItems = new ArrayList<Item>();
    public int isCheckAudio = 0;//进入in时，判断之前是否加载过，之前使用mAudioItem.isEmpty()，但是可能本来就没文件，所以每次都会加载
    public int isCheckVedio = 0;
    public int isCheckImg = 0;
    public int isCheckApk = 0;
    public int isCheckDoc = 0;
    public int isCheckCompress = 0;


    public ArrayList<Item> mHoldPreviousItems;
    private GridView mGridView;

    private String[] mClassifyName = new String[]{
            "音频",
            "视频",
            "图像",
            "文档",
            "安装包",
            "压缩包",
            "下载",
            "收藏",
            "其他"};

    /*------------------私有化------------------------------------------------------------------*/
    private static ClassifyGridHomePageAdapter classifyGridHomePageAdapter = new ClassifyGridHomePageAdapter();
    private ClassifyGridHomePageAdapter(){}
    public static ClassifyGridHomePageAdapter getInstance(){return classifyGridHomePageAdapter;}
    /*-----------------------------------------------------------------------------------------*/

    public ClassifyGridHomePageAdapter init(Context context, GridView gridView)
    {

        mContext = context;
        mInflate = LayoutInflater.from(context);
        mGridView = gridView;

        mDadaSet00 = new ArrayList<Item>();

        for(String name : mClassifyName){
            Item item = new Item(name);
            mDadaSet00.add(item);
        }
        mDadaSet = mDadaSet00;//此时就mDataSet01还没有初始化

        return this;
    }


    public void preLoadDataItems()
    {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        //音频
        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                ArrayList<String> contentPath = new ArrayList<String>();
                Search.getInstance().searchTypeFile(Environment.getExternalStorageDirectory().getPath(), new Search.AudioFilerNameFilter(), contentPath);
                FileManager.getInstance().wrap(contentPath, ClassifyGridHomePageAdapter.getInstance().mAudioItems);
                ClassifyGridHomePageAdapter.getInstance().isCheckAudio = 1;
                ClassifyViewListener.getInstance().mHandler.sendEmptyMessage(0);
            }
        });

        //图像
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> contentPath = new ArrayList<String>();
                Search.getInstance().searchTypeFile(Environment.getExternalStorageDirectory().getPath(),new Search.ImgFilerNameFilter() ,contentPath);
                FileManager.getInstance().wrap(contentPath,ClassifyGridHomePageAdapter.getInstance().mImgItems);
                ClassifyGridHomePageAdapter.getInstance().isCheckImg = 1;
                ClassifyViewListener.getInstance().mHandler.sendEmptyMessage(2);
            }
        });

        //视频
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> contentPath = new ArrayList<String>();
                Search.getInstance().searchTypeFile(Environment.getExternalStorageDirectory().getPath(),new Search.VideoFilerNameFilter() ,contentPath);
                FileManager.getInstance().wrap(contentPath,ClassifyGridHomePageAdapter.getInstance().mVedioItems);
                ClassifyGridHomePageAdapter.getInstance().isCheckVedio = 1;
                ClassifyViewListener.getInstance().mHandler.sendEmptyMessage(1);
            }
        });

        //文档
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> contentPath = new ArrayList<String>();
                Search.getInstance().searchTypeFile(Environment.getExternalStorageDirectory().getPath(),new Search.DocumentFilerNameFilter() ,contentPath);
                FileManager.getInstance().wrap(contentPath,ClassifyGridHomePageAdapter.getInstance().mDocItems);
                ClassifyGridHomePageAdapter.getInstance().isCheckDoc = 1;
                ClassifyViewListener.getInstance().mHandler.sendEmptyMessage(3);
            }
        });

        //apk
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> contentPath = new ArrayList<String>();
                Search.getInstance().searchTypeFile(Environment.getExternalStorageDirectory().getPath(),new Search.AppFilerNameFilter() ,contentPath);
                FileManager.getInstance().wrap(contentPath,ClassifyGridHomePageAdapter.getInstance().mApkItems);
                ClassifyGridHomePageAdapter.getInstance().isCheckApk= 1;
                ClassifyViewListener.getInstance().mHandler.sendEmptyMessage(4);
            }
        });

        //压缩
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> contentPath = new ArrayList<String>();
                Search.getInstance().searchTypeFile(Environment.getExternalStorageDirectory().getPath(),new Search.CompressFilerNameFilter() ,contentPath);
                FileManager.getInstance().wrap(contentPath,ClassifyGridHomePageAdapter.getInstance().mCompressItems);
                ClassifyGridHomePageAdapter.getInstance().isCheckCompress = 1;
                ClassifyViewListener.getInstance().mHandler.sendEmptyMessage(5);
            }
        });

        //压缩
        executor.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
        executor.shutdown();
    }
























    /*----------------------下面是两个设置更新视图的方法---------------------------------------------*/
    public void goOutDataset00(){
        classifyMode = 0;
        mDadaSet = mDadaSet00;
        mGridView.setNumColumns(3);
        notifyDataSetChanged();
    }
    public void goInDateset01(ArrayList<Item> datas){
        classifyMode = 1;
        mHoldPreviousItems = datas;//为了popupmenu中的移动功能能正常更新此视图，要能分辨是音频、视频、文档还是什么。
        mDadaSet01 = datas;
        mDadaSet = datas;
        mGridView.setNumColumns(1);
        notifyDataSetChanged();
    }


    /*----------------------------------以下为适配器四个方法---------------------------------------*/
    @Override
    public int getCount()
    {
        return mDadaSet.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mDadaSet.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(classifyMode == 0)//这里进行总的判断
        {

                convertView = mInflate.inflate(R.layout.classify_grid_item_for_homepage,parent,false);
                ((TextView)convertView).setText(mDadaSet.get(position).getName());
                convertView.setTag(0);
            return convertView;
        }
        else
        {
            ViewHolder viewHolder = null;
            if (convertView == null || convertView.getTag().equals(0)){
                convertView = mInflate.inflate(R.layout.gridview_item,parent,false);
                convertView.setTag(1);
                viewHolder = new ViewHolder();

                viewHolder.mIcon = (TextView) convertView.findViewById(R.id.textview_icon);
                viewHolder.mName = (TextView) convertView.findViewById(R.id.textview_name);
                viewHolder.mBuildTime = (TextView) convertView.findViewById(R.id.textview_build_time);
                viewHolder.mExtra = (TextView) convertView.findViewById(R.id.textview_extra);
                viewHolder.mPopmenu = (Button) convertView.findViewById(R.id.button_popmenu);

                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }



            viewHolder.mIcon.setBackgroundColor(Color.parseColor("#9E9E9E"));
            viewHolder.mIcon.setText(mDadaSet.get(position).getIcon());
            viewHolder.mName.setText(mDadaSet.get(position).getName());
            viewHolder.mBuildTime.setText(mDadaSet.get(position).getBuildTime());
            viewHolder.mExtra.setText(mDadaSet.get(position).getExtra());
            //popupmenu 按钮携带这个item的obj，传递给其监听器获得路径并作相关处理,
            viewHolder.mPopmenu.setTag(mDadaSet.get(position));
            viewHolder.mPopmenu.setOnClickListener(PopButtonOnClickListener.getInstant());//绑定监听器，这个监听器含有button的处理代码，动态的
                                                                                          //创建popupMenu并显示出来
            /* popupmenu 动态的和任何位置的按钮绑定 */

            return convertView;
        }
    }


    private final class ViewHolder
    {
        TextView mIcon;
        TextView mName;
        TextView mBuildTime;
        TextView mExtra;
        Button mPopmenu;
    }
}
