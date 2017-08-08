package com.wust.filemanager.listener;

import android.app.ProgressDialog;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.wust.filemanager.Utils.FileManager;
import com.wust.filemanager.Utils.OpenFileUtils;
import com.wust.filemanager.Utils.Search;
import com.wust.filemanager.adapter.ClassifyGridHomePageAdapter;
import com.wust.filemanager.entity.Item;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by admin on 2016/5/2.
 */
public class ClassifyViewListener implements AdapterView.OnItemClickListener
{
    //单例私有
    private static ClassifyViewListener classifyViewListener = new ClassifyViewListener();
    private ClassifyViewListener(){}
    public static ClassifyViewListener getInstance(){return classifyViewListener;}

    private ProgressDialog mProgressDialog = null;
    private int mWhoClick = -1;//这个很重要

    //这里是一个亮点，
    public Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
             switch (msg.what)
             {
                 case 0:
                     if(mProgressDialog != null && mWhoClick ==0) {
                         ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mAudioItems);
                         closeProgressDialog();
                     }
                     break;
                 case 1:
                     if(mProgressDialog != null && mWhoClick ==1) {
                         ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mVedioItems);
                         closeProgressDialog();
                     }
                     break;
                 case 2:
                     if(mProgressDialog != null && mWhoClick ==2) {
                         ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mImgItems);
                         closeProgressDialog();
                     }
                     break;
                 case 3:
                     if(mProgressDialog != null && mWhoClick ==3) {
                         ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mDocItems);
                         closeProgressDialog();
                     }
                     break;
                 case 4:
                     if(mProgressDialog != null && mWhoClick ==4) {
                         ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mApkItems);
                         closeProgressDialog();
                     }
                     break;
                 case 5:
                     if(mProgressDialog != null && mWhoClick ==5) {
                         ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mCompressItems);
                         closeProgressDialog();
                     }
                     break;
             }
        }
    };



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(ClassifyGridHomePageAdapter.getInstance().classifyMode == 0)
        {
            switch (position)
            {
                case 0://"音频":
                    if (ClassifyGridHomePageAdapter.getInstance().isCheckAudio != 0)
                    {
                        ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mAudioItems);
                    } else
                    {
                        showProgressDialog();
                        mWhoClick = 0;

                    }


//                    if(ClassifyGridHomePageAdapter.getInstance().isCheckAudio != 0)
//                    {
//                        ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mAudioItems);
//                    }
//                    else
//                    {
//                        showProgressDialog();
//                        final ArrayList<String> contentPath = new ArrayList<String>();
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Search.getInstance().searchTypeFile(Environment.getExternalStorageDirectory().getPath(),new Search.AudioFilerNameFilter() ,contentPath);
//                                final ArrayList<Item> datas = FileManager.getInstance().wrap(contentPath,ClassifyGridHomePageAdapter.getInstance().mAudioItems);
//                                FileManager.getInstance().getMainActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ClassifyGridHomePageAdapter.getInstance().isCheckAudio = 1;
//                                        ClassifyGridHomePageAdapter.getInstance().goInDateset01(datas);
//                                        closeProgressDialog();
//                                    }
//                                });
//                            }
//                        }).start();
//                    }
            break;

                case 1://"视频":
                    if(ClassifyGridHomePageAdapter.getInstance().isCheckVedio != 0)
                    {
                        ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mVedioItems);
                    }
                    else
                    {
                        showProgressDialog();
                        mWhoClick = 1;
                    }
                    break;
                case 2://"图像":
                    if(ClassifyGridHomePageAdapter.getInstance().isCheckImg != 0)
                    {
                        ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mImgItems);
                    }
                    else
                    {
                        showProgressDialog();
                        mWhoClick = 2;

                    }
                    break;
                case 3://"文档":
                    if(ClassifyGridHomePageAdapter.getInstance().isCheckDoc != 0)
                    {
                        ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mDocItems);
                    }
                    else
                    {
                        showProgressDialog();
                        mWhoClick = 3;
                    }
                    break;
                case 4://"安装包":
                    if(ClassifyGridHomePageAdapter.getInstance().isCheckApk != 0)
                    {
                        ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mApkItems);
                    }
                    else
                    {
                        showProgressDialog();
                        mWhoClick = 4;
                    }
                    break;
                case 5://"压缩包":
                    if(ClassifyGridHomePageAdapter.getInstance().isCheckCompress != 0)
                    {
                        ClassifyGridHomePageAdapter.getInstance().goInDateset01(ClassifyGridHomePageAdapter.getInstance().mCompressItems);
                    }
                    else
                    {
                        showProgressDialog();
                        mWhoClick = 5;
                    }
                    break;
                default:
                    break;
            }
        }
        else //这里的else表示此时 classifyMode为1，此时为 inner
        {
            Item item = (Item) ClassifyGridHomePageAdapter.getInstance().getItem(position);
            if(new File(item.getPath()).canRead()){
                OpenFileUtils.openFile(new File(item.getPath()));
            }else {
                Toast.makeText(view.getContext(),"文件不可读！",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showProgressDialog()
    {
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(FileManager.getInstance().getMainActivity());
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
