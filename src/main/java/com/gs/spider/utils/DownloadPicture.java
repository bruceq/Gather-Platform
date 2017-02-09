package com.gs.spider.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载百度图片
 *
 * @author bruce_q
 * @create 2017-02-03 11:12
 **/


public class DownloadPicture extends BaseProcessor{
    static List<String> urls;
    static List<String> names;

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
    public void setNames(List<String> names) {
        this.names = names;
    }

    /**
     * 下载图片
     * author:bruce_q
     * 2017-2-5 20:47
     *
     * @param urlList
     * @param nameList
     */
    private void downloadPicture(ArrayList<String> urlList,ArrayList<String> nameList,String key) {
        URL url = null;
        for (int i=0;i<urlList.size();i++) {
            try {
                url = new URL(urlList.get(i));
                DataInputStream dataInputStream = new DataInputStream(url.openStream());
                String imageName = i + ".jpg";
                File file=new File("d:\\pic\\"+key);    //设置下载路径
                if(!file.isDirectory()){
                    file.mkdirs();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(new File("d:\\pic\\"+ key +"\\"+ imageName.trim()));
                byte[] buffer = new byte[1024];
                int length;
                while ((length = dataInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
                dataInputStream.close();
                fileOutputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e){
                System.out.println(e+" 图片不存在");
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void process(Page page) {
        List<String> url_list = new ArrayList<>();
        List<String> name_list = new ArrayList<>();
        JSONObject jsonObject = (JSONObject) JSONObject.parse(page.getRawText());
        JSONArray data = (JSONArray) jsonObject.get("imgs");
        for(int i=0;i<data.size();i++){
            String url = (String) data.getJSONObject(i).get("objURL");
            String name = (String) data.getJSONObject(i).get("fromPageTitleEnc");
            if(url!=null){
                url_list.add(url);
                name_list.add(name);
            }
        }
        setUrls(url_list);
        setNames(name_list);
    }

    public static void main(String[] args) {
        //tn:resultjsonavatarnew
        //ie:utf-8 字符编码（ie输入 oe输出）
        //word:美女 搜索关键字
        //pn:60 开始条数
        //rn:30 显示数量
        //z:0 尺寸（0全部尺寸 9特大 3大 2中 1小）
        //width:1024 自定义尺寸-宽
        //height:768 自定义尺寸-高
        //ic:0 颜色(0全部颜色 1红色 2黄色 4绿色 8青色 16蓝色 32紫色 64粉色 128棕色 256橙色 512黑色 1024白色 2048黑白)
        //s:0 3头像图片
        //face:0 1面部特写
        //st:-1 -1全部类型 1卡通画 2简笔画
        //lm:-1 (6动态图片 7静态图片)
        //gsm:3c pn值的十六进制数

        String key = "海贼王";    //百度图片 关键词
        DownloadPicture downloadPicture = new DownloadPicture();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> urlList = new ArrayList<>();
        for(int i=0;i<2;i++){   //控制爬取页数，一页30张图片
            String url = "http://image.baidu.com/search/avatarjson?tn=resultjsonavatarnew&ie=utf-8&word="+key+"&pn="+i*3+"0&rn=30&z=3&ic=0&s=0&face=0&st=-1&lm=-1";
            Spider.create(new DownloadPicture())
                .addUrl(url)
                .run();
            urlList.addAll(urls);
            nameList.addAll(names);
        }
        downloadPicture.downloadPicture(urlList,nameList,key);
    }
}
