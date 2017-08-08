package com.wust.filemanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.wust.filemanager.R;
import com.wust.filemanager.adapter.ClassifyGridHomePageAdapter;
import com.wust.filemanager.listener.ClassifyViewListener;

/**
 * Created by admin on 2016/4/24.
 */
public class ClassifyFragment extends Fragment
{
    /**
     * 初始numColumns设为3，可以更改为1，看起来就像listview
     */
    private GridView mGridView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.classify_fragment,container,false);

        initView(view);

        initEvent();

        return view;
    }

    private void initView(View view)
    {
        mGridView = (GridView) view.findViewById(R.id.classify_gridview);
        mGridView.setOnItemClickListener(ClassifyViewListener.getInstance());
    }

    private void initEvent()
    {
        mGridView.setAdapter(ClassifyGridHomePageAdapter.getInstance().init(getActivity(),mGridView));

        //这个预先数据处理如果放在MainActivity的oncreate中，在那时候可能主线程Handler机制还没初始化，而任务中有向handler发信息，
        //但是handler还没初始化，所以产生初始化错误
        ClassifyGridHomePageAdapter.getInstance().preLoadDataItems();//预先加载分类页面各个Item数据，开线程
    }
}
