package com.wust.filemanager.listener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wust.filemanager.R;
import com.wust.filemanager.Utils.FileManager;
import com.wust.filemanager.adapter.ClassifyGridHomePageAdapter;
import com.wust.filemanager.adapter.StorageGridAdapter;
import com.wust.filemanager.entity.Item;

import java.io.File;

/**
 * Created by admin on 2016/5/1.
 */
public class PopButtonOnClickListener implements View.OnClickListener ,PopupMenu.OnMenuItemClickListener
{
    /*---------------------------------------------------------------------------------------------*/
    //单例   这个类绑定gridview 每个item的button
    private  static PopButtonOnClickListener popButtonOnClickListener = new PopButtonOnClickListener();
    private PopButtonOnClickListener(){}//私有构造函数

    public static PopButtonOnClickListener getInstant()
    {
        return popButtonOnClickListener;
    }
    /*---------------------------------------------------------------------------------------------*/


    //OnClickListener监听器方法

    private Item mClickItem;
    private Context mContext;
    private int mMenuId;
    private ProgressDialog mProgressDialog;


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_popmenu:
                PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                mClickItem = (Item) v.getTag();
                mContext = v.getContext();
                popupMenu.getMenuInflater().inflate(R.menu.pop_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            case R.id.fab:
                v.setVisibility(View.GONE);//这里执行复制或移动操作后，fab按钮和undo菜单按钮一起消失
                FileManager.getInstance().mMenu.findItem(R.id.action_undo).setVisible(false);
                showProgressDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        final int flag_copy = FileManager.getInstance().copyFile(mClickItem.getPath(),FileManager.getInstance().getCurrentDir());

                        switch (mMenuId)
                        {
                            case R.id.pop_copy:
                                FileManager.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (flag_copy ==0 ){
                                            closeProgressDialog();
                                            Toast.makeText(mContext,"复制成功",Toast.LENGTH_SHORT).show();
                                /*---下面一句很重要，第一是更新目前的ArrayList<item>,第二是手动设置适配器数据更新并提示---*/
                                            StorageGridAdapter.getInstant().setDatas(FileManager.getInstance().getRefreshDir());
                                        }else {
                                            closeProgressDialog();
                                            Toast.makeText(mContext,"复制失败",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                break;

                            case R.id.pop_cut:
                                if(flag_copy == 0){
                                     final int flag_delete = FileManager.getInstance().deleteFile(mClickItem.getPath());
                                    FileManager.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (flag_delete ==0 ){
                                                closeProgressDialog();
                                                Toast.makeText(mContext,"移动成功",Toast.LENGTH_SHORT).show();
                                                //这里cut后要更新分类和存储两个页面，而复制不需要,这里不能直接用mDataSet，因为此时in/out中在外边,mDataSet被赋值为data00
//                                                ClassifyGridHomePageAdapter.getInstance().mDadaSet.remove(mClickItem);
//                                                ClassifyGridHomePageAdapter.getInstance().notifyDataSetChanged();
                                                  ClassifyGridHomePageAdapter.getInstance().mHoldPreviousItems.remove(mClickItem);
                                                  ClassifyGridHomePageAdapter.getInstance().notifyDataSetChanged();
                                                StorageGridAdapter.getInstant().setDatas(FileManager.getInstance().getRefreshDir());
                                            }else {
                                                closeProgressDialog();
                                                Toast.makeText(mContext,"移动失败",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    FileManager.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            closeProgressDialog();
                                            Toast.makeText(mContext,"移动失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                break;
                        }   }
                }).start();
                break;
            default:
                break;
        }

    }

    //OnMenuItemClickListener监听器方法
    @Override
    public boolean onMenuItemClick(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.pop_copy:
                mMenuId = R.id.pop_copy;
                FileManager.getInstance().getFab().setVisibility(View.VISIBLE);
                FileManager.getInstance().mMenu.findItem(R.id.action_undo).setVisible(true);
                return true;
            case R.id.pop_cut:
                mMenuId = R.id.pop_cut;
                FileManager.getInstance().getFab().setVisibility(View.VISIBLE);
                FileManager.getInstance().mMenu.findItem(R.id.action_undo).setVisible(true);
                return true;
            case R.id.pop_delete:
                //删除的时候异步处理，因为如果同步的话，当点下删除item的话，要等删除执行完，这个popupmenu才会退出去，ui上给人感觉不好
                new AlertDialog.Builder(mContext).setTitle("确认删除？")//显示一个dialog确认删除？
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        FileManager.getInstance().deleteFile(mClickItem.getPath());//删除任何地方都可以调用，但是下面视图跟新要分条件
                                        FileManager.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //这里跟新视图要分情况
                                                if(FileManager.getInstance().mCurrentViewPagerId == 0){
                                                    ClassifyGridHomePageAdapter.getInstance().mDadaSet.remove(mClickItem);
                                                    ClassifyGridHomePageAdapter.getInstance().notifyDataSetChanged();
                                                }else {
                                                    //这里是storage页面下删除后更新
                                                    StorageGridAdapter.getInstant().setDatas(FileManager.getInstance().getRefreshDir());//这里删除后，是重新检索当前目录各个文件然后显示
                                                }

                                                Toast.makeText(mContext,mClickItem.getName()+"已经删除",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }).setNegativeButton("取消",null).create().show();
                return true;
            case R.id.pop_details:
                View details_view = LayoutInflater.from(mContext).inflate(R.layout.details_dialog_layout,null);
                final TextView tv_name = (TextView) details_view.findViewById(R.id.details_dialog_name);
                final TextView tv_path = (TextView) details_view.findViewById(R.id.details_dialog_path);
                final TextView tv_extra = (TextView) details_view.findViewById(R.id.details_dialog_extra);
                final TextView tv_buildtime = (TextView) details_view.findViewById(R.id.details_dialog_buildtime);
                final TextView tv_permission = (TextView) details_view.findViewById(R.id.details_dialog_permission);
                final TextView tv_type = (TextView) details_view.findViewById(R.id.details_dialog_type);

                tv_name.setText("名称： "+mClickItem.getName());
                tv_path.setText("路径： "+mClickItem.getPath());
                tv_buildtime.setText("时间： "+mClickItem.getBuildTime());

                tv_permission.setText("权限： "+mClickItem.getPermission());

                if("DIR".equals(mClickItem.getIcon())){
                    tv_type.setText("类型： 文件夹");
                    tv_extra.setText("内容： "+mClickItem.getExtra());
                }else {
                    tv_extra.setText("大小： "+mClickItem.getExtra());
                    tv_type.setText("类型： "+mClickItem.getIcon().toLowerCase()+"文件");
                }
                new AlertDialog.Builder(mContext)
                        .setTitle("详情")
                        .setView(details_view)
                        .setPositiveButton("确认", null).create().show();


                return true;
            case R.id.pop_rename:
                View view = LayoutInflater.from(mContext).inflate(R.layout.rename_dialog_layout,null);
                final EditText editText = (EditText) view.findViewById(R.id.rename_dialog_editview);

                new AlertDialog.Builder(mContext)
                        .setTitle("重命名")
                        .setView(view)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String text = editText.getText().toString();
                                int flag = FileManager.getInstance().renameFile(mClickItem.getPath(),text);
                                if(flag ==0 )
                                {
                                    if(FileManager.getInstance().mCurrentViewPagerId == 0)
                                    {
                                        //这里留个心眼，下面代码不是很优化，可能产生错误
                                        int positon = ClassifyGridHomePageAdapter.getInstance().mDadaSet.indexOf(mClickItem);
                                        String newPath = mClickItem.getPath().substring(0,mClickItem.getPath().lastIndexOf("/")+1)+text+"."+mClickItem.getIcon();
                                        Item item = FileManager.getInstance().wrapSingleItem(newPath);
                                        ClassifyGridHomePageAdapter.getInstance().mDadaSet.remove(positon);
                                        ClassifyGridHomePageAdapter.getInstance().mDadaSet.add(positon,item);
                                        ClassifyGridHomePageAdapter.getInstance().notifyDataSetChanged();

                                    }
                                    else
                                    {
                                        StorageGridAdapter.getInstant().setDatas(FileManager.getInstance().getRefreshDir());
                                    }

                                    Toast.makeText(mContext,"命名成功",Toast.LENGTH_SHORT).show();
                                }else
                                {
                                    Toast.makeText(mContext,"命名失败",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("取消",null)
                        .create()
                        .show();
                return true;
            default:

                return true;
        }

    }


    private void showProgressDialog()
    {
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(mContext);
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
