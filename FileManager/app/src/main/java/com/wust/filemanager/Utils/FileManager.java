package com.wust.filemanager.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.widget.GridView;

import com.wust.filemanager.MainActivity;
import com.wust.filemanager.R;
import com.wust.filemanager.adapter.StorageGridAdapter;
import com.wust.filemanager.entity.Item;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Objects;
import java.util.Stack;

/**
 * Created by admin on 2016/4/30.
 */
public class FileManager
{
    private static final FileManager fileManger = new FileManager();
    private static final int BUFFER = 2048;
    private static final int SORT_NONE = 0;
    private static final int SORT_ALPHA = 1;
    private static final int SORT_TYPE = 2;
    private static final int SORT_SIZE = 3;

    private boolean mShowHiddenFiles = false;
    private int mSortType = 0;
    private Stack<String> mPathStack;     //顶层始终为当前目录
    private ArrayList<String> mDirContent;//当前目录下的内容，各个文件
    private ArrayList<Item> mDirContentItem;

    private MainActivity mMainActivity;//保存主activity
    private FloatingActionButton mFab;//保存复制粘贴的按钮

    public int mCurrentViewPagerId;
    public Menu mMenu;//保留activity中的menu
    public int mCurrentColorId = R.color.colorPrimaryDark;
    private int[] mColors = new int[]{
            R.color.c1,
            R.color.c2,
            R.color.c3,
            R.color.c4,
            R.color.c5,
            R.color.c6,
            R.color.c7,
            R.color.c8,
            R.color.c9
    };








    /**
     *  constructor
     */
    private FileManager()
    {
        mPathStack = new Stack<String>();
        mDirContent = new ArrayList<String>();//保存当前目录下的各个文件及目录名字，不是完整路径
        mDirContentItem = new ArrayList<Item>();//保存要返回个适配器的当前目录下各个子文件、目录的item信息

        //设置当前为sdcard路径
        mPathStack.push("/");
        mPathStack.push(mPathStack.peek() + "sdcard");//peek为去除栈顶的内容，不删除
    }

    public static FileManager getInstance()
    {
        return fileManger;
    }

    /**
     * 通过peek，返回当前的路径
     * @return
     */
    public String getCurrentDir() {
        return  mPathStack.peek();
    }

    /**
     * 首页有一个按钮可以直接，回到根目录，这个方法会放在 “settings” 页面来设置
     * @param name
     * @return
     */
    public ArrayList<Item> setHomeDir(String name)
    {
        mPathStack.clear();
        mPathStack.push("/");
        mPathStack.push(name);

        //直接返回一个目录内容列表，这个函数是给mDirContent赋值，然后返回它
        return populate_list();
    }

    /**
     * 进入子目录,目的：更改mPathStack的顶层
     * @param name
     * @return
     */
    public ArrayList<Item> getNextDir(String name)
    {

        mPathStack.push(mPathStack.peek()+"/"+name);
        return populate_list();
    }

    /**
     * 返回父目录
     * @return
     */
    public ArrayList<Item> getPreviousDir()
    {
        mPathStack.pop();
        return populate_list();
    }

    public ArrayList<Item> getRefreshDir()
    {
        return populate_list();
    }




    public void setShowHiddenFile(boolean choice) {
        mShowHiddenFiles = choice;
    }
    public boolean getShowHiddenFile(){return  mShowHiddenFiles;}

    public void setSortType(int type) {
        mSortType = type;
    }




    /*------------------------分类比较器---------------------------------------------------------*/
    //这里Comparator alph = ..出的Comparator不能写成Comparator<String> 暂时不知道为什么
    private  final Comparator alph = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            return lhs.toLowerCase().compareTo(rhs.toLowerCase());//前者位于后者之前，返回负整数、反之正整数
        }
    };

    private  final Comparator size = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            String dir =  mPathStack.peek();
            Long first = new File(dir + "/" +lhs).length();//length()方法只能比较文件的大小
            Long second = new File(dir + "/" + rhs).length();//而不能比较文件夹的大小
            return first.compareTo(second);
        }
    };

    private static final Comparator type = new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            String ext1 = null;//根据扩展名来分类
            String ext2 = null;
            int ret;
            try {
                //subString（begin，end），参数都是索引值，从begin开始，到end-1
                ext1 = lhs.substring(lhs.lastIndexOf(".") + 1, lhs.length()).toLowerCase();
                ext2 = rhs.substring(rhs.lastIndexOf(".") + 1, rhs.length()).toLowerCase();
            } catch (IndexOutOfBoundsException e) { //分文件和文件夹
                return 0;
            }

            ret = ext1.compareTo(ext2);
            if(ret == 0) {
                return lhs.toLowerCase().compareTo(rhs.toLowerCase());
            }
            return ret;
        }
    };
    /*-----------------------------------------------------------------------------------*/

    /**
     * 这个类，提供首页GridView的文件lists，返回一个ArrayList，含有该路径的各个文件名
     * @return
     */
    private ArrayList<Item> populate_list()
    {
        //先清空
        if( !mDirContent.isEmpty()) {
            mDirContent.clear();
        }

        //始终以stack的顶端为当前目录，然后获取该目录下的list
        File file = new File(mPathStack.peek());

        if (file.exists() && file.canRead()) {//在文件存在，可读的条件下，在目录为空的时候，也是在这里面，只不过返回的ArrayList<Item>内容为空但是这个实例还是有的
            String[] list = file.list();
            int len = list.length;

            /* 将list数组中的文件名添加到mDirContent中，并且根据设置，判断是否添加隐藏文件 */
            for (int i = 0; i < len; i++) {
                if (mShowHiddenFiles){     //显示隐藏就全部添加，没有就判断一下在添加
                    mDirContent.add(list[i]);
                }else {
                    if (list[i].charAt(0) != '.') { mDirContent.add(list[i]);}
                }
            }
            //此时文件mDirContent就已经设置好了，下面就根据排序选择来再排列
            switch (mSortType) {
                case SORT_NONE:
                    //不做任何处理
                    break;
                case SORT_ALPHA:
                    Object[] temp_alpha = mDirContent.toArray();
                    mDirContent.clear();

                    Arrays.sort(temp_alpha,alph);

                    for (Object a: temp_alpha) {
                        mDirContent.add((String)a);
                    }
                    break;
                case SORT_SIZE:
                    int index = 0;
                    Object[] temp_size = mDirContent.toArray();
                    String dir = mPathStack.peek();

                    Arrays.sort(temp_size,size);//这里初步排序，下面还分文件和文件夹排序

                    mDirContent.clear();
                    for(Object a : temp_size) {
                        if(new File(dir + "/" + (String)a).isDirectory()) {
                            mDirContent.add(index++,(String)a);//文件夹放在前面，当index有文件时，当前及后面全部后移
                        } else {
                            mDirContent.add((String)a);
                        }
                    }
                    break;
                case SORT_TYPE:
                    int type_index = 0;
                    Object[] temp_type = mDirContent.toArray();
                    String current = mPathStack.peek();

                    Arrays.sort(temp_type,type);//初步排序，根据后缀名,后面分文件和文件夹排序

                    mDirContent.clear();
                    for (Object a : temp_type){
                        if(new File(current + "/" +(String)a).isDirectory()) {
                            mDirContent.add(type_index++, (String) a);
                        } else {
                            mDirContent.add((String) a);
                        }
                    }
                    break;
                default:
                    break;
            }

        }else {
            mDirContent.add("Emtpy");//如果该目录不存在或者不可读
        }
        return wrap();
    }
    /*--------------------------以下方法包装成ArrayList<Item>------------------------------------------*/
    //这几个方法传入的都是路径
    private String getBuildTiem(String path)
    {
        File file = new File(path);
        Calendar calendar = Calendar.getInstance();
        long time = file.lastModified();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar.setTimeInMillis(time);

        return calendar.getTime().toLocaleString();
    }
    private String getIconFileType(String str)
    {
        String ext = null;
        if (new File(str).isDirectory()) {
            ext = "DIR";
        } else if (!str.trim().contains("."))
        {
            ext = "";
        }else if (str.lastIndexOf(".") < str.length()-1  && str.lastIndexOf(".") >= str.length()-5){
            ext= str.substring(str.lastIndexOf(".") + 1, str.length()).toUpperCase();
        }else {
            ext = "";
        }
         return ext;
    }

    /**
     * gridview Item 最右边额外信息的显示，如果是目录，就显示有多少个子文件（还要检查是否有权限）
     *               如果是文件，就显示文件有多大
     * @param
     * @return
     */
    private String getExtra(String path)
    {
        File file = new File(path);
        if (file.isDirectory()){

            return file.canRead() ? file.list().length + " 文件" : "0"; //如果不能文件没有读权限，所以就不能调用file.list()方法
        } else {
            long len = file.length();//long型的字节数
            if (len < 1024) return len+"Byte";
            if (len < 1024*1024) return  len/1024+"KB";
            if (len <1024*1024*1024) return len/1024/1024+"M";
            return len/1024/1024/1024 + "GB";
//            return file.length() > 1000 ? file.length()/1024 +"" : file.length() +"";
        }
    }

    //这个函数暂时有问题
    private String getPermission(String path)
    {
        StringBuilder sb = new StringBuilder();
        File file = new File(path);
        if(file.canRead()){
            sb.append("可读-");
        }else {
            sb.append("不可读-");
        }
        if(file.canWrite()){
            sb.append("可写-");
        }else {
            sb.append("不可写-");
        }
        if(file.canExecute()){
            sb.append("可执行");
        }else {
            sb.append("不可执行");
        }
        return sb.toString();
    }

    private ArrayList<Item> wrap()
    {
        mDirContentItem.clear();
        for(String name : mDirContent){
            Item item = new Item();
            item.setBuildTime(getBuildTiem(mPathStack.peek() +"/" +name));
            item.setIcon(getIconFileType(mPathStack.peek() +"/" +name));
            item.setName(name);
            item.setExtra(getExtra(mPathStack.peek() +"/" +name));
            item.setPath(mPathStack.peek()+"/"+name);
            item.setPermission(getPermission(mPathStack.peek()+"/"+name));
            mDirContentItem.add(item);
        }
        return  mDirContentItem;
    }

    //这里传入的参数只是文件，不是目录，全是完整路径
    public ArrayList<Item> wrap(ArrayList<String> searchFilesPaths, ArrayList<Item> items)
    {
        if(searchFilesPaths == null){return items;}

        for(String filePath : searchFilesPaths)
        {
            Item item = new Item();
            item.setBuildTime(getBuildTiem(filePath));
            item.setIcon(getIconFileType(filePath));
            item.setName(filePath.substring(filePath.lastIndexOf("/")+1,filePath.length()));
            item.setExtra(getExtra(filePath));
            item.setPath(filePath);
            item.setPermission(getPermission(filePath));
            items.add(item);

        }
        return items;
    }

    public Item wrapSingleItem(String filePath)
    {
        Item item = new Item();
        item.setBuildTime(getBuildTiem(filePath));
        item.setIcon(getIconFileType(filePath));
        item.setName(filePath.substring(filePath.lastIndexOf("/")+1,filePath.length()));
        item.setExtra(getExtra(filePath));
        item.setPath(filePath);
        item.setPermission(getPermission(filePath));
        return item;
    }
    /*-------------------------------------------------------------------------------------------*/

    /*-------------------------保存gridview、adapter等，新加的功能的方法-------------------------------------------*/
    /**
     *  这里保存 storage gridview 的适配器，因为FileManager是静态单例类，所以这里可以随时在任何环境得到这个适配器
     *
     *  目前只有在MainActivity中的onBackPress()方法中用到，应为要获得apdatet
     */
//    private StorageGridAdapter mStorageGridAdapter;
//    public void setStorageGridAdapter(StorageGridAdapter s)
//    {
//        mStorageGridAdapter = s;
//    }
//    public StorageGridAdapter getStorageGridAdapter()
//    {
//        return mStorageGridAdapter;
//    }
//
//    private GridView mStorageGridView;
//    public void setStorageGridView(GridView gridview)
//    {
//        mStorageGridView = gridview;
//    }
//    public GridView getStorageGridView()
//    {
//        return mStorageGridView;
//    }
    public MainActivity getMainActivity()
    {
        return mMainActivity;
    }public void setMainActivity(MainActivity MainActivity)
    {
        this.mMainActivity = MainActivity;
    }

    public FloatingActionButton getFab()
    {
        return mFab;
    }

    public void setFab(FloatingActionButton mFab)
    {
        this.mFab = mFab;
    }

    public void changeColor(){mCurrentColorId = mColors[Math.round((float)(Math.random()*8))];}


    /*-------------------------------------------------------------------------------------------*/

    /*----------------------------一下是重命名、删除、复制、剪切、详情等功能------------------------*/

    //删除
    public int deleteFile(String path)
    {
        File target = new File(path);
        //这里一定要判断存在，因为可能同时利用另外一个软件删除，这样判断就有问题了
        if(target.exists() && target.isFile() && target.canWrite()){
            target.delete();
            return 0;
        }else if(target.exists() && target.isDirectory() && target.canRead()) {
            String[] subFileNames = target.list();
            if(subFileNames != null && subFileNames.length ==0) {
                target.delete();
            }else if (subFileNames != null && subFileNames.length > 0) {
                for (int i = 0; i < subFileNames.length; i++) {
                    File tempFile = new File(target.getAbsolutePath() + "/" +subFileNames[i]);
                    if(tempFile.isDirectory()) {
                        deleteFile(tempFile.getAbsolutePath());
                    }else if(tempFile.isFile()){
                        tempFile.delete();
                    }
                }
            }
            if(target.exists()){       //---file.delete(),只能删除文件和空目录，
                if(target.delete()){
                    return 0;
                }
            }
        }
        return -1;
    }
    //重命名
    public int renameFile(String path, String newName)
    {
        File target = new File(path);
        String ext = "";
        File dest;

        if(target.isFile()) {
            if(path.substring(path.lastIndexOf("/"),path.length()).contains(".")){//这里一定要加判断
                ext = path.substring(path.lastIndexOf("."),path.length());
            }else {
                ext = "";
            }

        }
        if(newName.length() < 1){
            return -1;
        }
        String temp = path.substring(0,path.lastIndexOf("/"));

        dest = new File(temp+"/"+newName+ext);
        if(target.renameTo(dest)) {
            return 0;
        }else {
            return -1;
        }
    }

    //复制到指定目录，只是复制功能，没有把旧的删除
    //path为target文件，dirPath为要复制到的位置
    //返回0成功，返回-1失败
    public int copyFile(String path, String dirPath) {
        File targetFile = new File(path);
        File dirFile = new File(dirPath);
        byte[] data = new byte[BUFFER];
        int read = 0;

        if(targetFile.isFile() && dirFile.isDirectory() && dirFile.canWrite()){
            String file_name = path.substring(path.lastIndexOf("/"), path.length());
            File cp_file = new File(dirPath + file_name);

            try {
                BufferedOutputStream o_stream = new BufferedOutputStream(
                        new FileOutputStream(cp_file));
                BufferedInputStream i_stream = new BufferedInputStream(
                        new FileInputStream(targetFile));

                while((read = i_stream.read(data, 0, BUFFER)) != -1)
                    o_stream.write(data, 0, read);

                o_stream.flush();
                i_stream.close();
                o_stream.close();

            } catch (FileNotFoundException e) {
                Log.e("FileNotFoundException", e.getMessage());
                return -1;

            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
                return -1;
            }

        }else if(targetFile.isDirectory() && dirFile.isDirectory() && dirFile.canWrite()) {
            String files[] = targetFile.list();
            String dir = dirPath + path.substring(path.lastIndexOf("/"), path.length());
            int len = files.length;

            if(!new File(dir).mkdir())
                return -1;

            for(int i = 0; i < len; i++)
                copyFile(path + "/" + files[i], dir);

        } else if(!dirFile.canWrite())
            return -1;

        return 0;
    }

}
