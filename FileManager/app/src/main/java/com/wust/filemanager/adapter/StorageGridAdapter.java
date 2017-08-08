package com.wust.filemanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wust.filemanager.R;
import com.wust.filemanager.Utils.FileManager;
import com.wust.filemanager.entity.Item;
import com.wust.filemanager.listener.PopButtonOnClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Handler;

/**
 * Created by admin on 2016/4/24.
 */
public class StorageGridAdapter extends BaseAdapter01
{
    private FileManager mFileManager;
    private List<Item> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;



    /*-------------------------------构造、初始化等部分函数，单例模式-------------------------------*/
    //实现单例模式
    private static StorageGridAdapter storageGridAdapter =  new StorageGridAdapter();
    private StorageGridAdapter(){}//私有构造函数
    public static StorageGridAdapter getInstant(){
        return storageGridAdapter;
    }
    //这个实例的初始化,本来可以直接用私有构造函数初始化，但是需要传入参数，所以用这个。
    public  StorageGridAdapter init(Context context)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mFileManager = FileManager.getInstance();
        mDatas = mFileManager.setHomeDir(Environment.getExternalStorageDirectory().getPath());//这里先初始化，后面还要设置数据源的
       // mFileManager.setStorageGridAdapter(StorageGridAdapter.this);//首先将这个适配器保存在filemanager单利类中，以供随时在其他环境中获得。
        return this;
    }



    /*--------------------------------------------------------------------------------------------*/

    /*-------------------------------实现某功能自己添加的函数---------------------------------------*/
    public void setDatas(ArrayList<Item> datas)
    {
        this.mDatas = datas;
        notifyDataSetChanged();
    }
    /*-------------------------------------------------------------------------------------------*/




    /*-------------------------------作为适配器供gridview数据显示的的四个函数------------------------*/
    @Override
    public int getCount()
    {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.gridview_item,parent,false);
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

        if ("DIR".equals(mDatas.get(position).getIcon())) {
            viewHolder.mIcon.setBackgroundColor(Color.parseColor("#FFCC80"));
        }else {viewHolder.mIcon.setBackgroundColor(Color.parseColor("#9E9E9E"));}
        viewHolder.mIcon.setText(mDatas.get(position).getIcon());
        viewHolder.mName.setText(mDatas.get(position).getName());
        viewHolder.mBuildTime.setText(mDatas.get(position).getBuildTime());
        viewHolder.mExtra.setText(mDatas.get(position).getExtra());
        //popupmenu 按钮携带这个item的obj，传递给其监听器获得路径并作相关处理
        viewHolder.mPopmenu.setTag(mDatas.get(position));
        viewHolder.mPopmenu.setOnClickListener(PopButtonOnClickListener.getInstant());//绑定监听器

        return convertView;
    }

    private final class ViewHolder
    {
        TextView mIcon;
        TextView mName;
        TextView mBuildTime;
        TextView mExtra;
        Button   mPopmenu;
    }

    /*-------------------------------------------------------------------------------------------*/
}
