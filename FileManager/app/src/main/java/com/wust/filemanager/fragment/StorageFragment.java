package com.wust.filemanager.fragment;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.wust.filemanager.R;
import com.wust.filemanager.Utils.FileManager;
import com.wust.filemanager.adapter.StorageGridAdapter;
import com.wust.filemanager.listener.StorageGridViewListener;

/**
 * Created by admin on 2016/4/24.
 */
public class StorageFragment extends Fragment
{
    /**
     * 初始numColumns设置为1，看起来和list一样
     */
    private ListView mListview;
    private StorageGridAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.storage_fragment,container,false);

        initView(view);

        initEvent();

        return view;

    }

    private void initView(View view)
    {

        mListview = (ListView) view.findViewById(R.id.storage_gridview);
//        TextView header = (TextView) LayoutInflater.from(view.getContext()).inflate(R.layout.listview_header,null);
//        header.setText(Environment.getExternalStorageDirectory().getPath());
//        mListview.addHeaderView(header);
        mAdapter = StorageGridAdapter.getInstant().init(getActivity());//这里必须这么些，先初始化。

    }

    private void initEvent()
    {
        mListview.setAdapter(mAdapter);

        //FileManager.getInstance().setStorageGridView(mGridView);//当前的现实的gridview保存在fileManager中
        StorageGridViewListener listeners = new StorageGridViewListener();
        mListview.setOnItemClickListener(listeners);
        mListview.setOnScrollListener(listeners);
    }
}
