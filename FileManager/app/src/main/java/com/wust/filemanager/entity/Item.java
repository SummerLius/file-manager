package com.wust.filemanager.entity;

/**
 * Created by admin on 2016/4/30.
 */
public class Item
{
    private String icon;
    private String name;
    private String buildTime;
    private String extra;//如果是文件就是大小，如果是文件夹就是

    private String path;
    private String permission;
    private String belongTo;

    public Item(){}
    public Item(String name){this.name = name;}

    public String getBelongTo()
    {
        return belongTo;
    }

    public void setBelongTo(String belongTo)
    {
        this.belongTo = belongTo;
    }

    public String getPermission()
    {
        return permission;
    }

    public void setPermission(String permission)
    {
        this.permission = permission;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }



    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getBuildTime()
    {
        return buildTime;
    }

    public void setBuildTime(String buildTime)
    {
        this.buildTime = buildTime;
    }

    public String getExtra()
    {
        return extra;
    }

    public void setExtra(String extra)
    {
        this.extra = extra;
    }
}
