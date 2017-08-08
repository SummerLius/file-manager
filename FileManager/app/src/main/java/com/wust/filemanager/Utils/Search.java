package com.wust.filemanager.Utils;

import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by admin on 2016/4/24.
 */
public class Search
{
    public void Search(String keyword)
    {
    }

    private static Search mSearch = new Search();
    private Search(){}
    public static Search getInstance(){return mSearch;}


    public void searchFile(String dirPath, String fileName, ArrayList<String> n) {
        File rootDir = new File(dirPath);//在那个目录下搜索
        String[] list = rootDir.list();//返回的只是各个name不是路径

        if(list != null && rootDir.canRead()) {
            int len = list.length;

            for (int i = 0; i < len; i++) {
                File check = new File(dirPath + "/" + list[i]);
                String name = check.getName();

                if(check.isFile() && name.toLowerCase().
                        contains(fileName.toLowerCase())) {
                    n.add(check.getPath());
                }
                else if(check.isDirectory()) {
                    //本来这部分是可以搜索结果包含文件夹，但是我这里至搜索文件
//                    if(name.toLowerCase().contains(fileName.toLowerCase()))
//                        n.add(check.getPath());
//
//                    else if(check.canRead() && !dirPath.equals("/"))
//                        searchFile(check.getAbsolutePath(), fileName, n);
                    if(check.canRead() && !dirPath.equals("/")) {
                        searchFile(check.getAbsolutePath(), fileName, n);
                    }
                }
            }
        }
    }

    public void searchTypeFile(String dirPath,FilenameFilter filter,ArrayList<String> n)
    {
        File rootDir = new File(dirPath);//在那个目录下搜索
        String[] list = rootDir.list(filter);//返回的只是各个name不是路径

        if(list != null && rootDir.canRead()) {
            int len = list.length;

            for (int i = 0; i < len; i++) {
                File subFile = new File(dirPath + "/" + list[i]);
                if(subFile.canRead()){
                    if(subFile.isDirectory()){
                        searchTypeFile(subFile.getAbsolutePath(),filter,n);
                    }else {
                        n.add(subFile.getAbsolutePath());
                    }
                }
            }
        }
    }



    /**
     * 音频文件过滤器
     */
    public final static class AudioFilerNameFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File dir, String filename) {

            if("android".equals(filename.toLowerCase())){
                return false;
            }
            if (filename.endsWith(".mp3")||filename.endsWith(".ogg")||filename.endsWith(".wmv")||filename.endsWith(".rmvb")) {
                return true;
            }
            if(new File(dir.getAbsolutePath()+"/"+filename).isDirectory()) {
                return true;
            }
            return false;
        }
    }


/**
 * 图片文件过滤器
 */
  public final static class  ImgFilerNameFilter implements FilenameFilter
{
    @Override
    public boolean accept(File dir, String filename) {

        File file = new File(dir.getAbsolutePath()+"/"+filename);
        if (filename.endsWith(".jpg")||filename.endsWith(".jpeg")||filename.endsWith(".png")||filename.endsWith(".bmp")||filename.endsWith(".gif")) {
            return true;
        }
        if(file.isDirectory()) {
            if (file.getAbsolutePath().toLowerCase().contains("dcim") ||
                    file.getAbsolutePath().toLowerCase().contains("picture")||
                    file.getAbsolutePath().toLowerCase().contains("image")){
                return true;
            }



        }
        return false;
    }
}
    /**
     * 视频文件过滤器
     */
    public  final  static class VideoFilerNameFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File dir, String filename) {

            if("android".equals(filename.toLowerCase())){
                return false;
            }
            if (filename.endsWith(".mp4")||filename.endsWith(".ogg")||filename.endsWith(".avi")||filename.endsWith(".rmvb")||filename.endsWith(".mkv")||filename.endsWith(".3gp")) {
                return true;
            }
            if(new File(dir.getAbsolutePath()+"/"+filename).isDirectory()) {
                return true;
            }
            return false;
        }
    }
    /**
     * 文档文件过滤器
     */
   public final static class DocumentFilerNameFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File dir, String filename) {

            if("android".equals(filename.toLowerCase())){
                return false;
            }
            if (filename.endsWith(".txt")||filename.endsWith(".doc")||filename.endsWith(".xls")||filename.endsWith(".docx")
                    ||filename.endsWith(".pdf")||filename.endsWith(".wps")||filename.endsWith(".xml")||filename.endsWith(".xlsx")
                    ||filename.endsWith(".ppt")||filename.endsWith(".pptx")) {
                return true;
            }
            if(new File(dir.getAbsolutePath()+"/"+filename).isDirectory()) {
                return true;
            }
            return false;
        }
    }
    /**
     * 安装包文件过滤器
     */
    public final static class AppFilerNameFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File dir, String filename) {

            if("android".equals(filename.toLowerCase())){
                return false;
            }
            if (filename.endsWith(".apk")) {
                return true;
            }
            if(new File(dir.getAbsolutePath()+"/"+filename).isDirectory()) {
                return true;
            }
            return false;
        }
    }
    /**
     * 压缩包文件过滤器
     */
    public final static class CompressFilerNameFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File dir, String filename) {

            if("android".equals(filename.toLowerCase())){
                return false;
            }
            if (filename.endsWith(".zip")||filename.endsWith(".rar")||filename.endsWith(".rar")||filename.endsWith(".7-zip")||filename.endsWith(".iso")) {
                return true;
            }
            if(new File(dir.getAbsolutePath()+"/"+filename).isDirectory()) {
                return true;
            }
            return false;
        }
    }
}


