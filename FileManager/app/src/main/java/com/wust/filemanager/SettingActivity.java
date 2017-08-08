package com.wust.filemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.wust.filemanager.Utils.FileManager;
import com.wust.filemanager.adapter.StorageGridAdapter;

/**
 * Created by admin on 2016/4/25.
 */
public class SettingActivity extends AppCompatActivity
{
    private CheckBox mCheckBox;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(getResources().getDrawable(FileManager.getInstance().mCurrentColorId));

        mCheckBox = (CheckBox) findViewById(R.id.settings_hide);

        mCheckBox.setChecked(FileManager.getInstance().getShowHiddenFile());

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked) {
                    FileManager.getInstance().setShowHiddenFile(true);
                    StorageGridAdapter.getInstant().setDatas(FileManager.getInstance().getRefreshDir());
                }else {
                    FileManager.getInstance().setShowHiddenFile(false);
                    StorageGridAdapter.getInstant().setDatas(FileManager.getInstance().getRefreshDir());
                }
            }
        });





    }
}
