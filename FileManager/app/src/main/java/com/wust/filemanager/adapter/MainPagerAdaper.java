package com.wust.filemanager.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by admin on 2016/4/24.
 */
public class MainPagerAdaper extends FragmentPagerAdapter
{
    private List<Fragment> mData;
    public MainPagerAdaper(FragmentManager fm, List<Fragment> data)
    {
        super(fm);
        mData = data;
    }

    @Override
    public Fragment getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }
}
