package com.wust.filemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wust.filemanager.Utils.FileManager;
import com.wust.filemanager.adapter.BaseAdapter01;
import com.wust.filemanager.adapter.SearchGridviewAdapter;
import com.wust.filemanager.adapter.StorageGridAdapter;
import com.wust.filemanager.listener.PopButtonOnClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by admin on 2016/5/14.
 */
public class AppManageActivity extends AppCompatActivity
{
    private ProgressDialog mProgressDialog;
    private ListView mListView;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appmanage_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(getResources().getDrawable(FileManager.getInstance().mCurrentColorId));

        mListView = (ListView) findViewById(R.id.appmanage_listview);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                AppInfo tmpInfo = (AppInfo) parent.getAdapter().getItem(position);

                Intent intent = getPackageManager().getLaunchIntentForPackage(tmpInfo.packageName);
                try {
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(AppManageActivity.this,"卸载失败",Toast.LENGTH_SHORT).show();
                }

            }
        });






        /*-----------------------------------------------------------------------------------*/
        showProgressDialog();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
               final  ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); //用来存储获取的应用信息数据
                List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

                for(int i=0;i<packages.size();i++) {
                    PackageInfo packageInfo = packages.get(i);
                    AppInfo tmpInfo =new AppInfo();
                    tmpInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                    tmpInfo.packageName = packageInfo.packageName;
                    tmpInfo.versionName = packageInfo.versionName;
                    tmpInfo.versionCode = packageInfo.versionCode;

                    Calendar calendar = Calendar.getInstance();
                    long time = packageInfo.firstInstallTime;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    calendar.setTimeInMillis(time);

                    tmpInfo.firstInstallTime = calendar.getTime().toLocaleString();


                    tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                    //Only display the non-system app info
                    if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0)
                    {
                        appList.add(tmpInfo);//如果非系统应用，则添加至appList
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        mListView.setAdapter(new AppAdapter(AppManageActivity.this, appList));
                        closeProgressDialog();
                    }
                });
            }
        }).start();
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
}

/*下面是其他类*/

 class AppInfo {
     public String appName="";
     public String packageName="";
     public String versionName="";
     public String firstInstallTime="";
     public int versionCode=0;
     public Drawable appIcon=null;
 }

class AppAdapter extends BaseAdapter01 implements View.OnClickListener{

    private Context mContext;
    private List<AppInfo> mDatas;
    private LayoutInflater mInflater;
    public AppAdapter(Context context, List<AppInfo> datas)
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
            convertView = mInflater.inflate(R.layout.app_manage_listview_item,parent,false);
            viewHolder = new ViewHolder();

            viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.textview_icon);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.textview_name);
            viewHolder.mFirstIntallTime = (TextView) convertView.findViewById(R.id.textview_build_time);
            viewHolder.mExtra = (TextView) convertView.findViewById(R.id.textview_extra);

            viewHolder.mUnInstallButton = (Button) convertView.findViewById(R.id.button_popmenu);
            viewHolder.mUnInstallButton.setOnClickListener(this);


            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mIcon.setImageDrawable(mDatas.get(position).appIcon);
        viewHolder.mName.setText(mDatas.get(position).appName);
        viewHolder.mFirstIntallTime.setText(mDatas.get(position).firstInstallTime);
        viewHolder.mExtra.setText(mDatas.get(position).versionName );
        viewHolder.mUnInstallButton.setTag(position);

        return convertView;
    }

    @Override
    public void onClick(View v)
    {
        AppInfo tmpInfo = mDatas.get((Integer) v.getTag());

        Uri uri = Uri.parse("package:"+tmpInfo.packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        v.getContext().startActivity(intent);
    }

    private final class ViewHolder
    {
        ImageView mIcon;
        TextView mName;
        TextView mFirstIntallTime;
        TextView mExtra;
        Button   mUnInstallButton;
    }

}
