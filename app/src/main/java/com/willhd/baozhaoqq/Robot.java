package com.willhd.baozhaoqq;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

import javax.crypto.AEADBadTagException;


/**
 * Created by william on 5/26/16.
 */
public class Robot{
    private final String TAG = "Robot";

    private final String QQ_ADD_RESP = "Will:#add [^:]{1,255}::[^:]{1,255}";
    private final String QQ_COMM = "Will:说[^:]{1,255}";
    //private final String QQ_RECOVER = "Will:[^:]{1,255}";

    private final String QQ_NAME_PAT = "[^`]{1,255}:";
    private final String QQ_NAMEFORMAT = "[【（\\(\\{｛].+[】）\\)\\}｝].+:";
    private final String QQ_NEWBIE = "[^`]{1,64} 已加入该群";
    private final String QQ_INVITE = "[^`]{1,64} 邀请 [^`]{1,64} 加入了本群";

    private final String QQ_JIEXIN = "#接新\n[^#]{1,255}";
    private final String QQ_XUQIU = "#需求\n[^#]{1,255}";
    private final String QQ_RECALLJ = "#撤回接新\n[^#]{1,255}";
    private final String QQ_RECALLN = "#撤回需求\n[^#]{1,255}";
    private final String QQ_FINDJX = "查找接新[^:]{1,255}";
    private final String QQ_FINDXQ = "查找需求[^:]{1,255}";
    private final String QQ_CIRNAME = "圈名[:：][^:]{1,64}";

    private final String QQ_AD = "[^测^接^需]{0,255}[a-zA-Z0-9]{1,255}\\.[a-zA-Z0-9]{1,255}\\.[a-zA-Z0-9]{1,64}[^测^接^需]{0,255}";
    private final String QQ_XUAN = "[^测^接^需]{0,255}宣[^测^接^需]{0,255}";
    private final String QQ_NUM = "[^测^接^需]{0,255}[0-9]{7,255}[^测^接^需]{0,255}";
    private final String QQ_JINXUAN = "检测到宣传！\n本群禁宣请立即撤回！";

    private final String QQ_ADMIN = "饭管理群-[^:]{1,64}:[^`]{1,255}";
    private final String QQ_TEACH = "说[^:]{1,255}";
    private final String QQ_SETTIME = "定时[0-9]{1,64}";
    private final String QQ_BLACKLIST = "黑名单[^:]{1,255}";
    private final String QQ_RBLACKLIST = "移除黑名单[^:]{1,255}";
    private final String QQ_JUMP = "跳去[^:]{1,64}";

    private final String QQ_ENG = "小饭[a-zA-Z0-9 ,\\.\\?!'-]{1,64}";
    private final String QQ_SEARCH = "什么是[^`]{1,255}";

    private final String QQ_HELP = "二狗";
    private final String QQ_SEND = "发送";
    private final String QQ_GROUP = "-[^:]{1,64}:[^`]{1,255}";

    private final String QQ_JXHELP =
            "------找接新可输入以下两条命令\n" +
            "------*号处填写工种并删掉*号！\n" +
            "找接新\n" +
            "查找接新***\n" +
            "------接新请按以下格式填写\n" +
            "------*号处填写个人信息并删掉*号！\n" +
            "------记得加那个等号在你的圈名之后加！\n" +
            "------前面几行不用写！信息由#开始：\n" +
            "#接新\n" +
            "圈名：***=\n" +
            "所接工种：***\n" +
            "备注：***\n" +
            "有偿还是工种互换：***\n" +
            "工作效率：***\n" +
            "QQ号码：***\n" +
            "发布时间：***\n" +
            "------需要撤回接新请按照以下格式发\n" +
            "------*符号处填你的信息并删掉*号\n" +
            "------前面几行不用写！信息由#开始：\n" +
            "#撤回接新\n" +
            "圈名：***";
    private final String QQ_XQHELP =
            "------找需求可输入以下两条命令\n" +
            "------*号处填写工种并删掉*号！\n" +
            "找需求\n" +
            "查找需求***\n" +
            "------需求请按以下格式填写\n" +
            "------*号处填写个人信息并删掉*号！\n" +
            "------记得加那个等号在你的圈名之后加！\n" +
            "------前面几行不用写！信息由#开始：\n" +
            "#需求\n" +
            "圈名：***=\n" +
            "所需工种：***\n" +
            "备注：***\n" +
            "有偿还是工种互换：***\n" +
            "工作效率：***\n" +
            "QQ号码：***\n" +
            "发布时间：***\n" +
            "------需要撤回需求请按照以下格式发\n" +
            "------*符号处填你的信息并删掉*号\n" +
            "------前面几行不用写！信息由#开始：\n" +
            "#撤回需求\n" +
            "圈名：***";
    private final String QQ_HELPS =
            "欢迎来到嘹亮古风交流群大家庭～\n" +
            "我是机器人小饭求眼熟～\n\n" + QQ_JXHELP + "\n\n" + QQ_XQHELP + "\n\n请认真阅读以上信息！！！最后祝大家在群里开开心心~";
    public static long TIME = 1800000;
    private static int REC_SIZE = 15;
    private static String searchWord = "";
    private static String question = "";
    private static String pendingGroup = "";
    private static String uploadingData = "";
    private static String listType = "";
    private static Queue<String> msgLeft = new ArrayBlockingQueue<>(500);

    private MessageSenderService s;


    private List<String> names = new ArrayList<>();
    private List<String> blackList = new ArrayList<>();

    private boolean shouldWrite = false;
    private boolean shouldSend = false;
    private boolean pending = false;
    private String lastKey = "";
    private boolean enabled = true;
    private boolean lastPaste = false;
    private boolean clearing = false;
    private boolean checkProp = true;
    private boolean entertain = false;
    private long lastTime;

    private List<String> group_names;
    private HashMap<String, String> responses;
    private HashMap<String, String> needs;
    private HashMap<String, String> jiexins;
    private Queue<String> record;
    private Queue<String> nodeids;

    /*private Runnable getList = new Runnable() {
        @Override
        public void run() {
            String result1 = "I can't answer it.";
            try {
                URL url = new URL("119.60.2.38:5000");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(6*1000);
                connection.setUseCaches(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() != 200)
                    throw new IOException("request failed");
                InputStream is = connection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line + "\n");
                }
                Log.e(TAG, "Get word: " + searchWord);
                Log.e(TAG, "Get sb: " + sb.toString());
                try {
                    result0 = sb.toString().split("<hr/>")[1].split("<br/>")[0];
                    if (result0.startsWith("<div")) result0 = result0.split("</div>")[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e(TAG, "Get sb2: " + sb.toString().split("<hr/>")[1].split("<br/>")[1]);
                    if (sb.toString().split("<hr/>")[1].split("<br/>")[1] == null ||
                            sb.toString().split("<hr/>")[1].split("<br/>")[1].trim().equals(""))
                        result0 = "哇的一下就哭了இAஇ\n小饭居然不知道" + searchWord + "是什么...";
                    else
                        result0 = "小饭觉得你找的是这个:\n" + sb.toString().split("<hr/>")[1].split("<br/>")[1];
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                result0 = "哇的一下就哭了இAஇ\n小饭居然不知道" + searchWord + "是什么...";
            } catch (IOException e2) {
                Log.e(TAG, "Get fail" + e2.toString());
                result0 = "哇的一下就哭了இAஇ\n小饭居然出错了！\n快带着错误信息：\n" + e2.toString() +"\n找我主人";
            }
            searchDone(result0);
        }
    };*/

    private Runnable downChat = new Runnable() {
        @Override
        public void run() {
            String result1 = "I can't answer it.";
            try {
                URL url = new URL("https://kakko.pandorabots.com/pandora/talk?botid=f326d0be8e345a13&amp;skin=chat");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(6*1000);
                connection.setUseCaches(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                PrintWriter pw = new PrintWriter(connection.getOutputStream());
                pw.print("botcust2=90d4108dfe017b14&message="+question);
                pw.flush();
                pw.close();
                InputStream is = connection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line + "\n");
                }
                Log.e(TAG, "Get word: "+ searchWord);
                Log.e(TAG, "Get sb: "+ sb.toString());
                try {
                    result1 = sb.toString().split("<B>Mitsuku:</B>")[1].split("<br>")[0].replace("Mitsuku", "小饭").replace("Will", "");
                } catch (ArrayIndexOutOfBoundsException e) {
                    result1 = "哇的一下就哭了இAஇ\n小饭居然不知道怎么回答...";
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                result1 = "哇的一下就哭了இAஇ\n小饭居然不知道怎么回答...";
            } catch (Exception e2) {
                Log.e(TAG, "Get fail" + e2.toString());
                result1 = "哇的一下就哭了இAஇ\n小饭居然出错了！\n快带着错误信息：" + e2.toString() +"\n找我主人";
            }
            askDone(result1);
        }
    };

    private void initRecord(){
        for (int i = 0; i < REC_SIZE; i++) {
            record.add("init" + i);
        }
    }

    private Runnable downData = new Runnable() {
        @Override
        public void run() {
            String result0 = "搜索！";
            try {
                URL url = new URL("http://hudong.cn/home/hudong/search.wml?word=" +
                        URLEncoder.encode(searchWord, "UTF-8") + "&type=dict");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(6 * 1000);
                connection.setUseCaches(true);
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() != 200)
                    throw new IOException("request failed");
                InputStream is = connection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line + "\n");
                }
                Log.e(TAG, "Get word: " + searchWord);
                Log.e(TAG, "Get sb: " + sb.toString());
                try {
                    result0 = sb.toString().split("<hr/>")[1].split("<br/>")[0];
                    if (result0.startsWith("<div")) result0 = result0.split("</div>")[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e(TAG, "Get sb2: " + sb.toString().split("<hr/>")[1].split("<br/>")[1]);
                    if (sb.toString().split("<hr/>")[1].split("<br/>")[1] == null ||
                            sb.toString().split("<hr/>")[1].split("<br/>")[1].trim().equals(""))
                        result0 = "哇的一下就哭了இAஇ\n小饭居然不知道" + searchWord + "是什么...";
                    else
                        result0 = "小饭觉得你找的是这个:\n" + sb.toString().split("<hr/>")[1].split("<br/>")[1];
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                result0 = "哇的一下就哭了இAஇ\n小饭居然不知道" + searchWord + "是什么...";
            } catch (IOException e2) {
                Log.e(TAG, "Get fail" + e2.toString());
                result0 = "哇的一下就哭了இAஇ\n小饭居然出错了！\n快带着错误信息：\n" + e2.toString() +"\n找我主人";
            }
            searchDone(result0);
        }
    };

    public Robot () {
        group_names = new ArrayList<>();
        responses = new HashMap<>();
        needs = new HashMap<>();
        jiexins = new HashMap<>();
        record = new ArrayBlockingQueue<>(30);
        s = new MessageSenderService();
        nodeids = new ArrayBlockingQueue<String>(100);
        initRecord();
        lastTime = System.currentTimeMillis();
        blackList.add("开饭");

        Log.e(TAG, "Start creating");
        String local_file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/QQrobot";
        File f = new File(local_file);
        if(!f.exists()||!f.isDirectory()){
            f.mkdirs();
        }

        Log.e(TAG, "Start creating 0");
        File f0 = new File(f.getAbsolutePath(), "/groups.txt");
        if(f0.exists()) {
            try {
                FileInputStream in_g = new FileInputStream(f0);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_g));
                String temp;
                temp = reader.readLine();
                while ((temp != null) && (!temp.equals(""))) {
                    group_names.add(temp);
                    temp = reader.readLine();
                }
                Log.d(TAG, "File0 inputted");
            } catch (Exception e1) {
                Log.e(TAG, "input Error");
                e1.printStackTrace();
            }
        } else {
            try {
                Log.e(TAG, f0.getPath());
                if (!f0.createNewFile()) {
                    System.out.println("File already exists");
                } else {
                    System.out.println("File created");
                }
            } catch (IOException ex) {
                Log.e(TAG, "create Error");
                ex.printStackTrace();
            }
        }

        Log.e(TAG, "Start creating 1");
        File f1 = new File(f.getAbsolutePath(), "/responses.txt");
        if(f1.exists()) {
            try {
                FileInputStream in_r = new FileInputStream(f1);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_r));
                String[] temp;
                temp = reader.readLine().split("::");
                while ((temp[0] != null) && (!temp[0].equals(""))) {
                    responses.put(temp[0], temp[1]);
                    temp = reader.readLine().split("::");
                }
                Log.d(TAG, "File1 inputted");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }else{
            try {
                if(!f1.createNewFile()) {
                    System.out.println("File already exists");
                }else {
                    System.out.println("File created");
                }
            } catch (IOException ex) {
                Log.e(TAG, "create Error");
                ex.printStackTrace();
            }
        }

        Log.e(TAG, "Start creating 2");
        File f2 = new File(f.getAbsolutePath(), "/jiexins.txt");
        if(f2.exists()) {
            try {
                FileInputStream in_j = new FileInputStream(f2);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_j));
                String[] temp;
                temp = reader.readLine().replace("--", "\n").split("=");
                while ((temp[0] != null) && (!temp[0].equals(""))) {
                    jiexins.put(temp[0], temp[1]);
                    temp = reader.readLine().replace("--", "\n").split("=");
                }
                Log.d(TAG, "File2 inputted");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }else{
            try {
                if(!f2.createNewFile()) {
                    System.out.println("File already exists");
                }else {
                    System.out.println("File created");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Log.e(TAG, "Start creating 3");
        File f3 = new File(f.getAbsolutePath(), "/needs.txt");
        if(f3.exists()) {
            try {
                FileInputStream in_n = new FileInputStream(f3);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_n));
                String[] temp;
                temp = reader.readLine().replace("--", "\n").split("=");
                while ((temp[0] != null) && (!temp[0].equals(""))) {
                    needs.put(temp[0], temp[1]);
                    temp = reader.readLine().replace("--", "\n").split("=");
                }
                Log.d(TAG, "File2 inputted");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }else{
            try {
                if(!f3.createNewFile()) {
                    System.out.println("File already exists");
                }else {
                    System.out.println("File created");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void setEnable () {
        if (!record.contains("来嘞来嘞")) {
            enabled = true;
            msgLeft.add("来嘞来嘞");
            Log.e(TAG, "status Enabled");
        }
    }
    private void setDisable () {
        if (!record.contains("不说了不说了")) {
            enabled = false;
            clearing = false;
            msgLeft.add("不说了不说了");
            Log.e(TAG, "status Disabled");
        }
    }
    private void setNoCheck () {
        String raw = "小饭不检测名片了";
        if (!record.contains(raw)) {
            checkProp = false;
            msgLeft.add(raw);
            Log.e(TAG, "status Not checking name");
        }
        addToRecord(raw);
    }
    private void setCheck () {
        String raw = "小饭检测名片了";
        if (!record.contains(raw)) {
            checkProp = true;
            msgLeft.add(raw);
            Log.e(TAG, "status Checking name");
        }
        addToRecord(raw);
    }
    private void setNoEnt () {
        String raw = "好吧小饭不玩了啦";
        if (!record.contains(raw)) {
            entertain = false;
            msgLeft.add(raw);
            Log.e(TAG, "status Entertainment off");
        }
        addToRecord(raw);
    }
    private void setEnt () {
        String raw = "大家一起来玩吧";
        if (!record.contains(raw)) {
            entertain = true;
            msgLeft.add(raw);
            Log.e(TAG, "status Entertainment on");
        }
        addToRecord(raw);
    }

    private void checkName (AccessibilityNodeInfo info){
        String name = info.getText().toString();
        Log.e(TAG, "Name discovered: "+name);
        //if (!names.contains(name)){
            if (!Pattern.compile(QQ_NAMEFORMAT).matcher(name).matches()) {
                msgLeft.add("@" + name.replace(":", " ")
                        + "\n少侠马甲格式不对哦!请修改群马甲:\n（工种）昵称");
                Log.e(TAG, "event Name format error: "+ name.replace(":", ""));
                //names.add(name);
            }
        //}
    }
    private void welcomeNewbie (AccessibilityNodeInfo info, int t) {
        String raw = info.getText().toString();
        Log.e(TAG, "newer discovered: " + raw);
        if (t == 0) {
            msgLeft.add("欢迎" + raw.replace(" 已加入该群", "。") +
                    "注意！\n本群禁宣" +
                    "本群禁宣" +
                    "本群禁宣\n" +
                    "请新同学先改名片：(工种)xxx\n" +
                    "工种比如：cv、翻唱、后期、美工、填词、策划、导演、编剧等。\n" +
                    "「什么都不会可以改交流或学习哦」\n" +
                    "〖马甲需要帮忙的请艾特管理，告知管理工种和名字〗");
            Log.e(TAG, "event Newbies found:" + raw);
        }
        if (t == 1) {
            msgLeft.add("欢迎" + raw.split("邀请")[1].replace(" 加入了本群", "。") +
                    "注意！\n本群禁宣" +
                    "本群禁宣" +
                    "本群禁宣\n" +
                    "请新同学先改名片：(工种)xxx\n" +
                    "工种比如：cv、翻唱、后期、美工、填词、策划、导演、编剧等。\n" +
                    "「什么都不会可以改交流或学习哦」\n" +
                    "〖马甲需要帮忙的请艾特管理，告知管理工种和名字〗");
            Log.e(TAG, "event Newbies found:" + raw);
        }
    }
    private void sendResponse (AccessibilityNodeInfo info) {
        String raw = responses.get(info.getText().toString());
        Log.e(TAG, "Key discovered: " + raw);
        if (!record.contains(raw)) {
            if (lastKey.equals(raw)){
                msgLeft.add("小饭不想重复回答இAஇ\n免得别人以为我是机器人");
                lastKey = "";
            }else {
                msgLeft.add(raw);
                lastKey = raw;
            }
            Log.e(TAG, "event Keyword found：" + raw);
        }
        addToRecord(raw);
    }
    private void showResponse () {
        String raw = responses.toString();
        if (!record.contains(raw)) {
            msgLeft.add(raw);
            Log.e(TAG, "event Send Responses");
        }
    }
    private void showHelp (int k){
        String raw;
        switch (k) {
            case 1:
                raw = QQ_HELPS;
                break;
            case 2:
                raw = QQ_JXHELP;
                break;
            case 3:
                raw = QQ_XQHELP;
                break;
            default:
                raw = "Invalid comm";
        }
        if (!record.contains(raw)) {
            msgLeft.add(raw);
            Log.e(TAG, "event Help "+k);
        }
    }
    private void shuaPing () {
        String raw = "正在刷屏。。。\n停止刷屏请输入命令\n好了二狗不要再说了";
        if (!pending && clearing) {
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("list", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
        }
    }
    private void shuo (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replaceFirst("说", "");
        for (int i = 0; i < blackList.size(); i++) {
            String check = "[^#]{0,255}" + blackList.get(i) + "[^#]{0,255}";
            if (Pattern.compile(check).matcher(raw).matches())
                raw = "经过一系列思想斗争，小饭决定不说！";
        }
        //lastKey = raw;
        Log.d(TAG, "saying " + raw);
        if (!record.contains(raw)) {
            msgLeft.add(raw);
            Log.e(TAG, "event Saying:" + raw);
        }
        addToRecord(raw);
    }
    private void shuoMaster (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replace("Will:说", "");
        //lastKey = raw;
        Log.d(TAG, "saying " + raw);
        if (!record.contains(raw)) {
            msgLeft.add(raw);
            Log.e(TAG, "event Master saying:" + raw);
        }
        addToRecord(raw);
    }
    private void shuoAdmin (String cont, int l){
        String raw;
        switch (l) {
            case 0:
                raw = cont.replaceFirst("说", "");
                break;
            case 1:
                blackList.add(cont.replaceFirst("黑名单", ""));
                raw = "已设定关键字"+cont.replaceFirst("黑名单", "");
                break;
            case 2:
                raw = cont.replaceFirst("定时", "");
                TIME = Long.parseLong(raw);
                raw = cont.replaceFirst("定时", "冷场间隔设为") + "毫秒";
                break;
            case 3:
                blackList.remove(cont.replaceFirst("移除黑名单", ""));
                raw = "已完成操作";
                break;
            case 4:
                pendingGroup = cont.replaceFirst("跳去", "");
                raw = "小饭已准备好穿梭！目标"+pendingGroup;
                break;
            default:
                raw = "出错";
                break;
        }
        //lastKey = raw;
        Log.d(TAG, "saying " + raw);
        if (!record.contains(raw) ) {
            msgLeft.add(raw);
            Log.e(TAG, "event Admin " + raw);
        }
        addToRecord(raw);
    }
    private void search (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replace("什么是", "");
        //lastKey = "什么是" + raw;
        Log.d(TAG, "searching " + raw);
        if (!record.contains(info.getText().toString()) ) {
            Log.e(TAG, "GET request");
            searchWord = raw;
            new Thread(downData).start();
            msgLeft.add(raw + "?且容小饭思考一下哈...");
            Log.e(TAG, " event Search "+ raw);
        }
        addToRecord(info.getText().toString());
    }
    private void ask (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replaceFirst("小饭", "");
        //lastKey = info.getText().toString();
        Log.d(TAG, "asking " + raw);
        if (!record.contains(info.getText().toString()) ) {
                        Log.e(TAG, "POST request");
            question = raw;
            new Thread(downChat).start();
            msgLeft.add("Let me think...");
            Log.e(TAG, "event Asking" + raw);
        }
        addToRecord(info.getText().toString());
    }
    private void searchDone (String res){
        msgLeft.add(res);
        Log.e(TAG, "event Search done " + res);
    }
    private void askDone (String res){
        msgLeft.add(res);
        Log.e(TAG, "event Ask done " + res);
    }
    private void findXQ () {
        Log.d(TAG, "event check narray"+needs.keySet().toString());
        for (String i: needs.keySet()) {
            msgLeft.add(i + "\n" + needs.get(i));
            Log.d(TAG, "msgLeft added"+i);
        }
        msgLeft.add("查找到需求"+needs.size()+"个");
    }
    private void findJX () {
        Log.d(TAG, "event check jarray"+jiexins.keySet().toString());
        for (String i: jiexins.keySet()) {
            msgLeft.add(i+ "\n" +jiexins.get(i));
            Log.d(TAG, "msgLeft added"+i);
        }
        msgLeft.add("查找到接新"+jiexins.size()+"个");
    }
    private void searchXQ (AccessibilityNodeInfo info) {
        Log.d(TAG, "event check narray" + needs.values());
        String raw = info.getText().toString().replaceFirst("查找需求", "");
        if (!record.contains("含有" + raw + "的需求名单")) {
            msgLeft.add("含有" + raw + "的需求名单");
            int c = 0;
            for (String i : needs.keySet()) {
                if (needs.get(i).contains("工种："))
                    if (needs.get(i).split("工种：")[1].split("\n")[0].contains(raw)) {
                        msgLeft.add(i + "\n" + needs.get(i));
                        Log.d(TAG, "msgLeft added" + i);
                        c++;
                    }
            }
            msgLeft.add("查找到含有"+raw+"的需求"+c+"个");
        }
        addToRecord("含有" + raw + "的需求名单");
    }
    private void searchJX (AccessibilityNodeInfo info) {
        Log.d(TAG, "event check jarray" + jiexins.values());
        String raw = info.getText().toString().replaceFirst("查找接新", "");
        if (!record.contains("含有" + raw + "的接新名单")) {
            msgLeft.add("含有" + raw + "的接新名单");
            int c = 0;
            for (String i : jiexins.keySet()) {
                if (jiexins.get(i).contains("工种："))
                    if (jiexins.get(i).split("工种：")[1].split("\n")[0].contains(raw)) {
                        msgLeft.add(i + "\n" + jiexins.get(i));
                        Log.d(TAG, "msgLeft added" + i);
                        c++;
                    }
            }
            msgLeft.add("查找到含有"+raw+"的接新"+c+"个");
        }
        addToRecord("含有" + raw + "的接新名单");
    }
    private void addResponse (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replace("Will:#add ", "");
        Log.e(TAG, "event Adder discovered: " + raw);
        if (!responses.containsKey(raw.split("::")[0])) {
            msgLeft.add("已添加命令 "+raw.split("::")[0]);
            responses.put(raw.split("::")[0], raw.split("::")[1]);
            String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/responses.txt";
            try {
                File file = new File(local_file);
                FileOutputStream fos = new FileOutputStream(file, true);
                String ww = raw + "\n";
                fos.write(ww.getBytes());
                fos.close();
                Log.d(TAG, "event Added to responses.txt");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void addJiexin (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "event Jiexin discovered: " + raw);
        if (raw.split("=").length < 2 || raw.contains("*")) {
            Log.e(TAG, "Copied Error");
            msgLeft.add("格式错误，请检查");
        } else {
            if (!jiexins.containsKey(raw.split("=")[0].replace("#接新\n", ""))) {
                msgLeft.add(raw.replace("#", "").split("=")[0] + "\n已添加");

                jiexins.put(raw.split("=")[0].replace("#接新\n", ""), raw.split("=")[1]);
                String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/jiexins.txt";
                try {
                    File file = new File(local_file);
                    FileOutputStream fos = new FileOutputStream(file, true);
                    String ww = raw.replace("#接新\n", "").replace("\n", "--") + "\n";
                    fos.write(ww.getBytes());
                    fos.close();
                    Log.d(TAG, "Added to jiexins.txt");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void addXuqiu (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Xuqiu discovered: " + raw);
        if (raw.split("=").length < 2|| raw.contains("*")) {
            Log.e(TAG, "Copied Error");
            msgLeft.add("格式错误，请检查");
        } else {
            if (!needs.containsKey(raw.split("=")[0].replace("#需求\n", "")) && !pending) {
                msgLeft.add(raw.replace("#", "").split("=")[0] + "\n已添加");
                needs.put(raw.split("=")[0].replace("#需求\n", ""), raw.split("=")[1]);
                String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/needs.txt";
                try {
                    File file = new File(local_file);
                    FileOutputStream fos = new FileOutputStream(file, true);
                    String ww = raw.replace("#需求\n", "").replace("\n", "--") + "\n";
                    fos.write(ww.getBytes());
                    fos.close();
                    Log.d(TAG, "Added to needs.txt");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void recallJiexin (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Recall discovered: " + raw);
        if (jiexins.containsKey(raw.replace("#撤回接新\n", "")) && !pending) {
                        jiexins.remove(raw.replace("#撤回接新\n", ""));
            String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/jiexins.txt";
            try {
                File file = new File(local_file);
                File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/tempjiexin.txt");

                FileInputStream in_g = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_g));
                PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
                String temp;
                temp = reader.readLine();
                while ((temp != null) && (!temp.equals(""))) {
                    if (!temp.matches(raw.replace("#撤回接新\n", "") + "[^#]{1,255}")) {
                        writer.println(temp);
                        Log.e(TAG, "Write " + temp);
                    }
                    temp = reader.readLine();
                }

                boolean result = tempFile.renameTo(file);

                writer.close();
                reader.close();
                Log.d(TAG, "removed in jiexins.txt " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            msgLeft.add(raw.replace("#撤回接新\n", "") + "接新撤回成功!");
        }
    }
    private void recallXuqiu (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Recall discovered: " + raw);
        if (needs.containsKey(raw.replace("#撤回需求\n", "")) && !pending) {
                        needs.remove(raw.replace("#撤回需求\n", ""));
            String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/needs.txt";
            try {
                File file = new File(local_file);
                File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/tempneed.txt");

                FileInputStream in_g = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_g));
                PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
                String temp;
                temp = reader.readLine();
                while ((temp != null) && (!temp.equals(""))) {
                    if (!temp.matches(raw.replace("#撤回需求\n", "") + "[^#]{1,255}")) {
                        writer.println(temp);
                        Log.e(TAG, "Write " + temp);
                    }
                    temp = reader.readLine();
                }

                boolean result = tempFile.renameTo(file);

                writer.close();
                reader.close();
                Log.e(TAG, "removed in needs.txt " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            msgLeft.add(raw.replace("#撤回需求\n", "") + "需求撤回成功!");
        }
    }
    private void coldNotification () {
        String raw = "已经超过"+ TIME/1000 +"秒没什么人说话了！\n小饭我来暖一下场哈~\n有人想我吗？";
        if (enabled) {
            lastTime = System.currentTimeMillis();
            msgLeft.add(raw);
            //s.onAccessibilityEvent(AccessibilityEvent.obtain(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED));
        }
    }
    private void foundAd (AccessibilityNodeInfo info) {
        Log.e(TAG, "found Ad"+info.getText().toString());
        String raw = QQ_JINXUAN;
        if (!record.contains(raw)) {
            msgLeft.add(raw);
        }
        addToRecord(QQ_JINXUAN);
    }
    private void addToRecord(String s) {
        /*record.add(s);
        lastTime = System.currentTimeMillis();
        Log.d(TAG, "Queue Added " + s);
        if (record.size() > 15) {
            Log.d(TAG, "Queue Removed " + record.poll());
        }*/
    }
    private void addToInfos(String s) {
        nodeids.add(s);
        lastTime = System.currentTimeMillis();
        Log.d(TAG, "Queue Added " + s);
        if (nodeids.size() > 50) {
            Log.d(TAG, "Queue Removed " + nodeids.poll());
        }
    }

    public void onReceive (AccessibilityNodeInfo info, MessageSenderService mss){
        this.s = mss;

        if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.TextView")) {
                String parentInfo = "";
                for(int i = 0; i < info.getParent().getChildCount(); i++)
                    if(info.getParent().getChild(i).getText() != null)parentInfo += info.getParent().getChild(i).getText().toString();
                parentInfo += info.getText().toString();
                if (nodeids.contains(parentInfo)){
                    Log.d(TAG, "Queue already exist. returned "+parentInfo+info.getText().toString());
                    return;
                }
                addToInfos(parentInfo);

                Log.i(TAG, "Queue nodeInfo id:" + parentInfo);

                Log.d(TAG, "status Now " + enabled);
                Log.i(TAG, "node info: "+ info.toString().split("@")[1].split(";")[0]+info.getText().toString());
                Log.i(TAG, "parent: "+info.getParent().toString().split("@")[1].split(";")[0]);
                for(int i = 0; i < info.getParent().getChildCount(); i++) {
                    Log.i(TAG, "parent's child no."+i+":" + info.getParent().getChild(i).toString().split("@")[1].split(";")[0]);
                }
                //Reset
                if (info.getText().toString().equals("~reset")){
                    s.resetRobot();
                    Log.e(TAG, "Reset succeed");
                }
                if (!enabled) pending = false;
                //set enabled
                if (Pattern.compile("[0-9]{1,5}").matcher(info.getText().toString()).matches())
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                if (Pattern.compile(QQ_ADMIN).matcher(info.getText().toString()).matches()) {
                    String comm = info.getText().toString().split(":")[1];
                    if (comm.equals("二狗你过来一下") && !enabled) setEnable();
                    if (comm.equals("好了二狗不要再说了") && enabled) setDisable();
                    if (comm.equals("名片不要检测了") && checkProp) setNoCheck();
                    if (comm.equals("检测名片") && !checkProp) setCheck();
                    if (comm.equals("小饭我要玩") && !entertain) setEnt();
                    if (comm.equals("不要再玩了") && entertain) setNoEnt();
                }

                Log.d(TAG, "msgLeft size"+ msgLeft.size());
                if (enabled) {
                    //Check format
                    if (Pattern.compile(QQ_NAME_PAT).matcher(info.getText().toString()).matches() && checkProp)
                        checkName(info);
                    //Welcome
                    if (Pattern.compile(QQ_NEWBIE).matcher(info.getText().toString()).matches())
                        welcomeNewbie(info, 0);
                    if (Pattern.compile(QQ_INVITE).matcher(info.getText().toString()).matches())
                        welcomeNewbie(info, 1);
                    //Shuaping
                    if (info.getText().toString().equals("二狗来刷一下屏！")) clearing = true;
                    if (clearing) shuaPing();
                    //Check Ad
                    //if (Pattern.compile(QQ_AD).matcher(info.getText().toString()).matches() && !record.contains(QQ_JINXUAN)) foundAd(info);
                    //if (Pattern.compile(QQ_XUAN).matcher(info.getText().toString()).matches() && !record.contains(QQ_JINXUAN)
                    //        && !Pattern.compile(QQ_NAME_PAT).matcher(info.getText().toString()).matches()) foundAd(info);
                    //if (Pattern.compile(QQ_NUM).matcher(info.getText().toString()).matches() && !record.contains(QQ_JINXUAN)) foundAd(info);
                    //Teach
                    if (entertain) {
                        if (Pattern.compile(QQ_ENG).matcher(info.getText().toString()).matches()
                                && !info.getText().toString().equals(lastKey)) ask(info);
                        if (Pattern.compile(QQ_TEACH).matcher(info.getText().toString()).matches()
                                && !info.getText().toString().equals(lastKey)) shuo(info);
                        if (Pattern.compile(QQ_SEARCH).matcher(info.getText().toString()).matches()
                                && !info.getText().toString().equals(lastKey)) search(info);
                    } else {
                        if (Pattern.compile(QQ_ENG).matcher(info.getText().toString()).matches()
                                && !info.getText().toString().equals(lastKey)) msgLeft.add("管理大大并没有开启小饭这个功能இAஇ");
                        if (Pattern.compile(QQ_TEACH).matcher(info.getText().toString()).matches()
                                && !info.getText().toString().equals(lastKey)) msgLeft.add("管理大大并没有开启小饭这个功能இAஇ");
                        if (Pattern.compile(QQ_SEARCH).matcher(info.getText().toString()).matches()
                                && !info.getText().toString().equals(lastKey)) msgLeft.add("管理大大并没有开启小饭这个功能இAஇ");
                    }
                    if (Pattern.compile(QQ_COMM).matcher(info.getText().toString()).matches()
                            && !info.getText().toString().equals(lastKey)) shuoMaster(info);
                    //Admin
                    if (Pattern.compile(QQ_ADMIN).matcher(info.getText().toString()).matches()
                            && !info.getText().toString().equals(lastKey)) {
                        String comm = info.getText().toString().split(":")[1];
                        if(entertain) {
                            if (Pattern.compile(QQ_TEACH).matcher(comm).matches())
                                shuoAdmin(comm, 0);
                            if (Pattern.compile(QQ_BLACKLIST).matcher(comm).matches())
                                shuoAdmin(comm, 1);
                            if (Pattern.compile(QQ_RBLACKLIST).matcher(comm).matches())
                                shuoAdmin(comm, 3);
                        }
                        if (Pattern.compile(QQ_SETTIME).matcher(comm).matches()) shuoAdmin(comm, 2);
                        if (Pattern.compile(QQ_JUMP).matcher(comm).matches()) shuoAdmin(comm, 4);
                    }


                    //show commands
                    if (info.getText().toString().equals("~show") && !record.contains("~show"))
                        showResponse();
                    //show help
                    if (info.getText().toString().equals(QQ_HELP) && !record.contains(QQ_HELP))
                        showHelp(1);
                    if (info.getText().toString().equals("接新") && !record.contains("接新"))
                        showHelp(2);
                    if (info.getText().toString().equals("需求") && !record.contains("需求"))
                        showHelp(3);
                    //显示需求
                    if (info.getText().toString().equals("找需求") && !record.contains("找需求"))
                        findXQ();
                    if (Pattern.compile(QQ_FINDXQ).matcher(info.getText().toString()).matches())
                        searchXQ(info);
                    //显示接新
                    if (info.getText().toString().equals("找接新") && !record.contains("找接新"))
                        findJX();
                    if (Pattern.compile(QQ_FINDJX).matcher(info.getText().toString()).matches())
                        searchJX(info);
                    //Add response
                    if (Pattern.compile(QQ_ADD_RESP).matcher(info.getText().toString()).matches())
                        addResponse(info);
                    //send response
                    if (responses.containsKey(info.getText().toString())
                            && !info.getText().toString().equals(lastKey)) sendResponse(info);
                    //Jiexin
                    if (Pattern.compile(QQ_JIEXIN).matcher(info.getText().toString()).matches())
                        addJiexin(info);
                    //Xuqiu
                    if (Pattern.compile(QQ_XUQIU).matcher(info.getText().toString()).matches())
                        addXuqiu(info);
                    //Recall Xuqiu
                    if (Pattern.compile(QQ_RECALLN).matcher(info.getText().toString()).matches())
                        recallXuqiu(info);
                    //Recall Jiexin
                    if (Pattern.compile(QQ_RECALLJ).matcher(info.getText().toString()).matches())
                        recallJiexin(info);
                    if (Pattern.compile(pendingGroup.replace("(", "\\(").replace(")", "\\)")+QQ_GROUP).matcher(info.getText().toString()).matches()
                            || info.getText().toString().contains(":小饭我需要你")) {
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        pendingGroup = "";
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("list","小饭我穿越过来啦！");
                        mClipboard.setPrimaryClip(mClip);
                        Log.e(TAG, "Copied " + "穿越"+info.getText().toString());
                        pending = true;
                        shouldWrite = true;
                    }
                }
                if (!pending && msgLeft.size() != 0) {
                    String raw = msgLeft.poll();
                    ClipboardManager mClipboard;
                    mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
                    ClipData mClip;
                    mClip = ClipData.newPlainText("list", raw);
                    mClipboard.setPrimaryClip(mClip);
                    Log.e(TAG, "Copied " + raw);
                    pending = true;
                    shouldWrite = true;
                }
            }
        }

        //Add records
        /*if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.TextView")
                    && !record.contains(info.getText().toString())) {
                record.add(info.getText().toString());
                lastTime = System.currentTimeMillis();
                Log.d(TAG, "Time renewed to" + lastTime);
                Log.d(TAG, "Queue Added " + info.getText().toString());
                if (record.size() > REC_SIZE) {
                    Log.d(TAG, "Queue Removed " + record.poll());
                }
                if (names.size() > REC_SIZE) {
                    names.remove(0);
                }
            }
        }*/


        //Paste from Clipboard
        if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.EditText")
                    && shouldWrite && (enabled || lastPaste)) {
                                info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                Log.e(TAG, "Pasted");
                shouldSend = true;
                shouldWrite = false;
                            }
        }

        //Click send
        if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.Button")
                    && info.getText().toString().equals(QQ_SEND)
                    && shouldSend && (enabled || lastPaste)) {
                                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.e(TAG, "Sent");
                shouldSend = false;
                pending = false;
                lastPaste = false;
                            }
        }

        //Warm up
        if (System.currentTimeMillis() - lastTime >= TIME) coldNotification();
    }
}
