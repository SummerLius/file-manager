package com.wust.filemanager.Utils;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by admin on 2016/5/1.
 */
public class OpenFileUtils
{
    /**
     * 打开文件
     * @param file
     */
    public static void  openFile(File file){

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
        FileManager.getInstance().getMainActivity().startActivity(intent);

    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     * @param file
     */
    private static String getMIMEType(File file) {

        String type="*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
    /* 获取文件的后缀名*/
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }







   // MIME_MapTable是所有文件的后缀名所对应的MIME类型的一个String数组：

        private final static String[][] MIME_MapTable={
        //{后缀名，MIME类型}
        {".3gp",    "video/3gpp"},
        {".apk",    "application/vnd.android.package-archive"},
        {".asf",    "video/x-ms-asf"},
        {".avi",    "video/x-msvideo"},
        {".bin",    "application/octet-stream"},
        {".bmp",    "image/bmp"},
        {".c",  "text/plain"},
        {".class",  "application/octet-stream"},
        {".conf",   "text/plain"},
        {".cpp",    "text/plain"},
        {".doc",    "application/msword"},
        {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
        {".xls",    "application/vnd.ms-excel"},
        {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
        {".exe",    "application/octet-stream"},
        {".gif",    "image/gif"},
        {".gtar",   "application/x-gtar"},
        {".gz", "application/x-gzip"},
        {".h",  "text/plain"},
        {".htm",    "text/html"},
        {".html",   "text/html"},
        {".jar",    "application/java-archive"},
        {".java",   "text/plain"},
        {".jpeg",   "image/jpeg"},
        {".jpg",    "image/jpeg"},
        {".js", "application/x-javascript"},
        {".log",    "text/plain"},
        {".m3u",    "audio/x-mpegurl"},
        {".m4a",    "audio/mp4a-latm"},
        {".m4b",    "audio/mp4a-latm"},
        {".m4p",    "audio/mp4a-latm"},
        {".m4u",    "video/vnd.mpegurl"},
        {".m4v",    "video/x-m4v"},
        {".mov",    "video/quicktime"},
        {".mp2",    "audio/x-mpeg"},
        {".mp3",    "audio/x-mpeg"},
        {".mp4",    "video/mp4"},
        {".mpc",    "application/vnd.mpohun.certificate"},
        {".mpe",    "video/mpeg"},
        {".mpeg",   "video/mpeg"},
        {".mpg",    "video/mpeg"},
        {".mpg4",   "video/mp4"},
        {".mpga",   "audio/mpeg"},
        {".msg",    "application/vnd.ms-outlook"},
        {".ogg",    "audio/ogg"},
        {".pdf",    "application/pdf"},
        {".png",    "image/png"},
        {".pps",    "application/vnd.ms-powerpoint"},
        {".ppt",    "application/vnd.ms-powerpoint"},
        {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
        {".prop",   "text/plain"},
        {".rc", "text/plain"},
        {".rmvb",   "audio/x-pn-realaudio"},
        {".rtf",    "application/rtf"},
        {".sh", "text/plain"},
        {".tar",    "application/x-tar"},
        {".tgz",    "application/x-compressed"},
        {".txt",    "text/plain"},
        {".wav",    "audio/x-wav"},
        {".wma",    "audio/x-ms-wma"},
        {".wmv",    "audio/x-ms-wmv"},
        {".wps",    "application/vnd.ms-works"},
        {".xml",    "text/plain"},
        {".z",  "application/x-compress"},
        {".zip",    "application/x-zip-compressed"},
        {"",        "*/*"}
};
    //    //android获取一个用于打开HTML文件的intent
//    public static Intent getHtmlFileIntent(File file)
//    {
//        Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.setDataAndType(uri, "text/html");
//        return intent;
//    }
//    //android获取一个用于打开图片文件的intent
//    public static Intent getImageFileIntent(File file)
//    {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "image/*");
//        return intent;
//    }
//    //android获取一个用于打开PDF文件的intent
//    public static Intent getPdfFileIntent(File file)
//    {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "application/pdf");
//        return intent;
//    }
//    //android获取一个用于打开文本文件的intent
//    public static Intent getTextFileIntent(File file)
//    {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "text/plain");
//        return intent;
//    }
//
//    //android获取一个用于打开音频文件的intent
//    public static Intent getAudioFileIntent(File file)
//    {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("oneshot", 0);
//        intent.putExtra("configchange", 0);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "audio/*");
//        return intent;
//    }
//    //android获取一个用于打开视频文件的intent
//    public static Intent getVideoFileIntent(File file)
//    {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("oneshot", 0);
//        intent.putExtra("configchange", 0);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "video/*");
//        return intent;
//    }
//
//
//    //android获取一个用于打开CHM文件的intent
//    public static Intent getChmFileIntent(File file)
//    {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "application/x-chm");
//        return intent;
//    }
//
//
//    //android获取一个用于打开Word文件的intent
//    public static Intent getWordFileIntent(File file)
//    {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "application/msword");
//        return intent;
//    }
//    //android获取一个用于打开Excel文件的intent
//    public static Intent getExcelFileIntent(File file)
//    {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "application/vnd.ms-excel");
//        return intent;
//    }
//    //android获取一个用于打开PPT文件的intent
//    public static Intent getPPTFileIntent(File file)
//    {
//        Intent intent = new Intent("android.intent.action.VIEW");
//        intent.addCategory("android.intent.category.DEFAULT");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.fromFile(file);
//        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//        return intent;
//    }
//    //android获取一个用于打开apk文件的intent
//    public static Intent getApkFileIntent(File file)
//    {
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file),  "application/vnd.android.package-archive");
//        return intent;
//    }

}
