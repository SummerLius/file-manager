package com.wust.filemanager;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.wust.filemanager.Utils.FileManager;
import com.wust.filemanager.Utils.OpenFileUtils;
import com.wust.filemanager.Utils.Search;
import com.wust.filemanager.adapter.SearchGridviewAdapter;
import com.wust.filemanager.adapter.StorageGridAdapter;
import com.wust.filemanager.entity.Item;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by admin on 2016/5/2.
 */
public class SearchableActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private ProgressDialog mProgressDialog;

    private ArrayList<String> mSearchPaths;
    private ArrayList<Item> mSearchItems;
    private GridView mSearchGridView;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);//目前里面只有一个textview
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(getResources().getDrawable(FileManager.getInstance().mCurrentColorId));

        mSearchPaths = new ArrayList<String>();
        mSearchItems = new ArrayList<Item>();
        mContext = this;

        mSearchGridView = (GridView) findViewById(R.id.search_gridview);
        mSearchGridView.setOnItemClickListener(this);



        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            /* system会将searchView中的text保存到Intent让后传递到这个activity */
            final String query = intent.getStringExtra(SearchManager.QUERY);
            //格局query关键字，进行搜索

            showProgressDialog();
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    doMySearch(query);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mSearchGridView.setAdapter(new SearchGridviewAdapter(mContext, mSearchItems));
                            closeProgressDialog();
                        }
                    });
                }
            }).start();

        }
    }

    //这个比较耗时间
    private void doMySearch(String query)
    {
        Search.getInstance().searchFile(Environment.getExternalStorageDirectory().getPath(),query,mSearchPaths);
        FileManager.getInstance().wrap(mSearchPaths,mSearchItems);
    }


    private void showProgressDialog()
    {
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("请稍等(￣3￣)...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }
    private void closeProgressDialog()
    {
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        SearchGridviewAdapter adapter = (SearchGridviewAdapter) parent.getAdapter();
        Item item = (Item) adapter.getItem(position);
        if (!new File(item.getPath()).canRead()){
            Toast.makeText(view.getContext(),"文件不可读！",Toast.LENGTH_SHORT).show();
        }else {
            //如果是文件暂时不做任何处理，此时文件是可读的
            OpenFileUtils.openFile(new File(item.getPath()));
        }
    }


    /**
     * Receiving the query
     *
     * Searching your data
     *
     * Presenting the results
     */
}
