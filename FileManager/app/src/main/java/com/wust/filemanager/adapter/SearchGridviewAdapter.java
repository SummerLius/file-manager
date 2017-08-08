package com.wust.filemanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wust.filemanager.R;
import com.wust.filemanager.entity.Item;
import com.wust.filemanager.listener.PopButtonOnClickListener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/4.
 */
public class SearchGridviewAdapter extends BaseAdapter01
{
    private ArrayList<Item> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;

    public SearchGridviewAdapter(Context context, ArrayList<Item> datas)
    {
        mContext = context;
        mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }


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
}
