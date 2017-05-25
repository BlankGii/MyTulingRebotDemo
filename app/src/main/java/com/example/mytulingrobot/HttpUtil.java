package com.example.mytulingrobot;

import com.example.mytulingrobot.bean.ChatMessage;
import com.example.mytulingrobot.bean.CommonException;
import com.example.mytulingrobot.bean.Result;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by gcy on 2017/1/13 0013.
 */
public class HttpUtil {
    private static String API_KEY = "534dc342ad15885dffc10d7b5f813451";
    private static String URL = "http://www.tuling123.com/openapi/api";

    /**
     * 发送一个消息，并得到返回的消息
     *
     * @param msg
     * @return
     */
    public static ChatMessage sendMsg(String msg) {
        ChatMessage message = new ChatMessage();
        String url = setParams(msg);
        String res = doGet(url);
        Gson gson = new Gson();
        Result result = gson.fromJson(res, Result.class);

        if (result.getCode() > 400000 || result.getText() == null
                || result.getText().trim().equals("")) {
            message.setMsg("该功能等待开发...");
        } else {
            message.setMsg(result.getText());
        }
        message.setType(com.example.mytulingrobot.bean.ChatMessage.Type.INPUT);
        message.setDate(new Date());

        return message;
    }

    /**
     * 拼接Url
     *
     * @param msg
     * @return
     */
    private static String setParams(String msg) {
        try {
            msg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return URL + "?key=" + API_KEY + "&info=" + msg;
    }

    /**
     * Get请求，获得返回数据
     *
     * @param urlStr
     * @return
     */
    private static String doGet(String urlStr) {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); //允许输入流，即允许下载
            conn.setDoOutput(true); //允许输出流，即允许上传
            conn.setUseCaches(false); //不使用缓冲
            conn.setRequestMethod("GET"); //使用get请求
            is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine = "";
            String resultData = "";
            while ((inputLine = bufferReader.readLine()) != null) {
                resultData += inputLine;
            }
            return resultData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException("服务器连接错误！");
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }

    }
}
