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
    private final String QQ_NAME_PAT = "[^`]{1,255}:";
    private final String QQ_ADD_RESP = "Will:#add [^:]{1,255}::[^:]{1,255}";
    private final String QQ_NEWBIE = "[^`]{1,64} 已加入该群";
    private final String QQ_JIEXIN = "#接新\n[^#]{1,255}";
    private final String QQ_XUQIU = "#需求\n[^#]{1,255}";
    private final String QQ_RECALLN = "#撤回需求\n[^#]{1,255}";
    private final String QQ_RECALLJ = "#撤回接新\n[^#]{1,255}";
    private final String QQ_AD = "[^测^接^需]{0,255}[a-zA-Z0-9]{1,255}\\.[a-zA-Z0-9]{1,255}\\.[a-zA-Z0-9]{1,64}[^测^接^需]{0,255}";
    private final String QQ_XUAN = "[^测^接^需]{0,255}宣[^测^接^需]{0,255}";
    private final String QQ_NUM = "[^测^接^需]{0,255}[0-9]{7,255}[^测^接^需]{0,255}";
    private final String QQ_TEACH = "说[^:]{1,255}";
    private final String QQ_SETTIME = "定时[0-9]{1,64}";
    private final String QQ_BLACKLIST = "黑名单[^:]{1,255}";
    private final String QQ_RBLACKLIST = "移除黑名单[^:]{1,255}";
    private final String QQ_ENG = "小饭[a-zA-Z0-9 ,\\.\\?!'-]{1,64}";
    private final String QQ_COMM = "Will:说[^:]{1,255}";
    private final String QQ_CIRNAME = "圈名[:：][^:]{1,64}";
    //private final String QQ_RECOVER = "Will:[^:]{1,255}";
    private final String QQ_NAMEFORMAT = "[（\\(][^\\(^（^\\)）]{1,64}[）\\)][^\\(^\\)]{1,64}:";
    private final String QQ_HELP = "二狗";
    private final String QQ_SEND = "发送";
    private final String QQ_JINXUAN = "检测到宣传！\n本群禁宣请立即撤回！";
    private final String QQ_SEARCH = "什么是[^`]{1,255}";
    private final String QQ_ADMIN = "饭管理群-[^:]{1,64}:[^`]{1,255}";
    private final String QQ_FINDJX = "查找接新[^:]{1,255}";
    private final String QQ_FINDXQ = "查找需求[^:]{1,255}";

    //private final String QQ_TIME = "[上下]午([0-2])[0-9]:[0-5][0-9]";

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
                    "备注：*\n" +
                    "有偿还是工种互换：*\n" +
                    "工作效率：***\n" +
                    "QQ号码：***\n" +
                    "发布时间：*\n" +
                    "------需要撤回需求请按照以下格式发\n" +
                    "------*符号处填你的信息并删掉*号\n" +
                    "------前面几行不用写！信息由#开始：\n" +
                    "#撤回需求\n" +
                    "圈名：***";
    private final String QQ_HELPS =
            "欢迎来到嘹亮古风交流群大家庭～\n" +
            "我是机器人小饭求眼熟～\n\n" + QQ_JXHELP + "\n\n" + QQ_XQHELP + "\n\n请认真阅读以上信息！！！最后祝大家在群里开开心心~";
    public static long TIME = 300000;
    private static int REC_SIZE = 15;
    private static String searchWord = "";
    private static String question = "";
    private static int msgCount = 0;
    private static Queue<String> msgLeft = new ArrayBlockingQueue<>(500);

    private MessageSenderService s;

    private List<String> names = new ArrayList<>();
    private List<String> blackList = new ArrayList<>();

    private boolean shouldWrite = false;
    private boolean shouldSend = false;
    private boolean pending = false;
    private boolean busy = false;
    private String lastKey = "";
    private boolean enabled = false;
    private boolean lastPaste = false;
    private boolean clearing = false;
    private boolean checkProp = true;
    private long lastTime;

    private List<String> group_names;
    private HashMap<String, String> responses;
    private HashMap<String, String> needs;
    private HashMap<String, String> jiexins;
    private Queue<String> record;

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
            busy = true;
            enabled = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("answer", "来嘞来嘞");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + "Come");
            pending = true;
            shouldWrite = true;
            busy = false;
        }
    }
    private void setDisable () {
        if (!record.contains("不说了不说了")) {
            busy = true;
            enabled = false;
            clearing = false;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("answer", "不说了不说了");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + "Come");
            lastPaste = true;
            pending = true;
            shouldWrite = true;
            busy = false;
        }
    }
    private void setNoCheck () {
        String raw = "小饭不检测名片了";
        if (!record.contains(raw)) {
            busy = true;
            checkProp = false;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("answer", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + "Come");
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord(raw);
    }
    private void setCheck () {
        String raw = "小饭检测名片了";
        if (!record.contains(raw)) {
            busy = true;
            checkProp = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("answer", "小饭检测名片了");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + "Come");
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord(raw);
    }
    private void checkName (AccessibilityNodeInfo info){
        String name = info.getText().toString();
        Log.e(TAG, "Name discovered: "+name);
        if (!names.contains(name) && !pending){
            if (!Pattern.compile(QQ_NAMEFORMAT).matcher(name).matches()) {
                busy = true;
                ClipboardManager mClipboard;
                mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
                ClipData mClip;
                mClip = ClipData.newPlainText("name", "@" + name.replace(":", " ")
                        + "\n少侠马甲格式不对哦!\n请修改群马甲（工种）昵称。");
                mClipboard.setPrimaryClip(mClip);
                Log.e(TAG, "Copied " + name);
                pending = true;
                shouldWrite = true;
                names.add(name);
                busy = false;
            }
        }
    }
    private void welcomeNewbie (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "newer discovered: " + raw);
        if (!record.contains(raw) && !pending) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("Adder", "欢迎" + raw.replace(" 已加入该群", "") +
                    ".注意！\n本群禁宣" +
                    "本群禁宣" +
                    "本群禁宣\n" +
                    "请新同学先改名片：(工种)xxx 工种比如：cv、翻唱、后期、美工、填词、策划、导演、编剧等。\n" +
                    "「什么都不会可以改交流或学习哦」\n" +
                    "〖马甲需要帮忙的请艾特管理，告知管理工种和名字〗");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
    }
    private void sendResponse (AccessibilityNodeInfo info) {
        String raw = responses.get(info.getText().toString());
        Log.e(TAG, "Key discovered: " + raw);
        if (!pending && !record.contains(raw)) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("resp", raw);
            mClipboard.setPrimaryClip(mClip);
            lastKey = info.getText().toString();
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord(raw);
    }
    private void showResponse () {
        String raw = responses.toString();
        if (!pending && !record.contains(raw)) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("list", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
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
        if (!pending && !record.contains(raw)) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("list", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
    }
    private void shuaPing () {
        String raw = "正在刷屏。。。\n停止刷屏请输入命令\n好了二狗不要再说了";
        if (!pending && clearing) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("list", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
    }
    private void shuo (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replaceFirst("说", "");
        for (int i = 0; i < blackList.size(); i++) {
            String check = "[^#]{0,255}" + blackList.get(i) + "[^#]{0,255}";
            if (Pattern.compile(check).matcher(raw).matches())
                raw = "检测到敏感词，小饭不说！";
        }
        lastKey = raw;
        Log.d(TAG, "saying " + raw);
        if (!pending && !record.contains(raw) ) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("shuo", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord(raw);
    }
    private void shuoMaster (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replace("Will:说", "");
        lastKey = raw;
        Log.d(TAG, "saying " + raw);
        if (!pending && !record.contains(raw) ) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("shuo", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord(raw);
    }
    private void setMaster (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replace("Will:", "");
        lastKey = raw;
        Log.e(TAG, "setting " + raw);
        if (!pending && !record.contains(info.getText().toString()) ) {
            busy = true;

            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("setting", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord(info.getText().toString());
    }
    private void shuoAdmin (String cont, int l){
        String raw;
        switch (l) {
            case 0:
                raw = cont.replaceFirst("说", "");
                break;
            case 1:
                blackList.add(cont.replaceFirst("黑名单", ""));
                raw = "已完成";
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
            default:
                raw = "出错";
                break;
        }
        lastKey = raw;
        Log.d(TAG, "saying " + raw);
        if (!pending && !record.contains(raw) ) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("shuo", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord(raw);
    }
    private void search (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replace("什么是", "");
        lastKey = "什么是" + raw;
        Log.d(TAG, "searching " + raw);
        if (!pending && !record.contains(lastKey) ) {
            busy = true;
            Log.e(TAG, "GET request");
            searchWord = raw;
            new Thread(downData).start();
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("search", raw + "?且容小饭思考一下哈...");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " );
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord(lastKey);
    }
    private void ask (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replaceFirst("小饭", "");
        lastKey = info.getText().toString();
        Log.d(TAG, "asking " + raw);
        if (!pending && !record.contains(lastKey) ) {
            busy = true;
            Log.e(TAG, "POST request");
            question = raw;
            new Thread(downChat).start();
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("chat", "Let me think...");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " );
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord(lastKey);
    }
    private void searchDone (String res){
        busy = true;
        ClipboardManager mClipboard;
        mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
        ClipData mClip;
        mClip = ClipData.newPlainText("search", res);
        mClipboard.setPrimaryClip(mClip);
        Log.e(TAG, "Copied " + res);
        pending = true;
        shouldWrite = true;
        busy = false;
    }
    private void askDone (String res){
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("search", res);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + res);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
    private void findXQ () {
        Log.d(TAG, "check narray"+needs.keySet().toString());
        for (String i: needs.keySet()) {
            msgLeft.add(i + "\n" + needs.get(i));
            Log.d(TAG, "msgLeft added"+i);
        }
        if (!pending && !record.contains("需求名单")) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("list", "需求名单");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + "需求名单");
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord("需求名单");
    }
    private void findJX () {
        Log.d(TAG, "check jarray"+jiexins.keySet().toString());
        for (String i: jiexins.keySet()) {
            msgLeft.add(i+ "\n" +jiexins.get(i));
            Log.d(TAG, "msgLeft added"+i);
        }
        if (!pending && !record.contains("接新名单")) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("list", "接新名单");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + "接新名单");
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord("接新名单");
    }
    private void searchXQ (AccessibilityNodeInfo info) {
        Log.d(TAG, "check jarray"+needs.values());
        String raw = info.getText().toString().replaceFirst("查找需求", "");
        if (!record.contains("含有"+raw+"的需求名单"))
            for (String i: needs.keySet()) {
                if (needs.get(i).contains("工种："))
                    if (needs.get(i).split("工种：")[1].split("\n")[0].contains(raw)) {
                        msgLeft.add(i + "\n" + needs.get(i));
                        Log.d(TAG, "msgLeft added" + i);
                    }
            }
        if (!pending && !record.contains("含有"+raw+"的需求名单")) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("list", "含有"+raw+"的需求名单");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + "含有"+raw+"的需求名单");
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord("含有"+raw+"的需求名单");
    }
    private void searchJX (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replaceFirst("查找接新", "");
        Log.d(TAG, "check jarray"+jiexins.values());
        if (!record.contains("含有"+raw+"的接新名单"))
            for (String i: jiexins.keySet()) {
                if (jiexins.get(i).contains("工种："))
                    if (jiexins.get(i).split("工种：")[1].split("\n")[0].contains(raw)) {
                        msgLeft.add(i + "\n" + jiexins.get(i));
                        Log.d(TAG, "msgLeft added" + i);
                    }
            }
        if (!pending && !record.contains("含有"+raw+"的接新名单")) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("list", "含有"+raw+"的接新名单");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + "含有"+raw+"的接新名单");
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord("含有"+raw+"的接新名单");
    }
    private void addResponse (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replace("Will:#add ", "");
        Log.e(TAG, "Adder discovered: " + raw);
        if (!responses.containsKey(raw.split("::")[0]) && !pending) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("Adder", "Command\n\n" + raw.split("::")[0] + "\n\n" + raw.split("::")[1] + "\n\nAdded!");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            responses.put(raw.split("::")[0], raw.split("::")[1]);
            String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/responses.txt";
            try {
                File file = new File(local_file);
                FileOutputStream fos = new FileOutputStream(file, true);
                String ww = raw + "\n";
                fos.write(ww.getBytes());
                fos.close();
                Log.d(TAG, "Added to responses.txt");
            } catch (Exception e) {
                e.printStackTrace();
            }
            busy = false;
        }
    }
    private void addJiexin (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Jiexin discovered: " + raw);
        if (raw.split("=").length < 2) {
            Log.e(TAG, "Copied Error");
        } else {
            if (!jiexins.containsKey(raw.split("=")[0].replace("#接新\n", "")) && !pending) {
                busy = true;
                ClipboardManager mClipboard;
                mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
                ClipData mClip;
                mClip = ClipData.newPlainText("Jiexin", raw.replace("#", "").split("=")[0] + "\nAdded");
                mClipboard.setPrimaryClip(mClip);
                Log.e(TAG, "Copied " + raw);
                pending = true;
                shouldWrite = true;
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
                busy = false;
            }
        }
    }
    private void addXuqiu (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Xuqiu discovered: " + raw);
        if (raw.split("=").length < 2) {
            Log.e(TAG, "Copied Error");
        } else {
            if (!needs.containsKey(raw.split("=")[0].replace("#需求\n", "")) && !pending) {
                busy = true;
                ClipboardManager mClipboard;
                mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
                ClipData mClip;
                mClip = ClipData.newPlainText("Xuqiu", raw.replace("#", "").split("=")[0] + "\nAdded");
                mClipboard.setPrimaryClip(mClip);
                Log.e(TAG, "Copied " + raw);
                pending = true;
                shouldWrite = true;
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
                busy = false;
            }
        }
    }
    private void recallJiexin (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Recall discovered: " + raw);
        if (jiexins.containsKey(raw.replace("#撤回接新\n", "")) && !pending) {
            busy = true;
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
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("recall", raw.replace("#撤回接新\n", "") + "接新撤回成功!");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
    }
    private void recallXuqiu (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Recall discovered: " + raw);
        if (needs.containsKey(raw.replace("#撤回需求\n", "")) && !pending) {
            busy = true;
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
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("recall", raw.replace("#撤回需求\n", "") + "需求撤回成功!");
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
    }
    private void coldNotification () {
        String raw = "已经超过"+ TIME/1000 +"秒没人说话了！\n小饭我来暖一下场哈~\n有人想我吗？";
        if (enabled) {
            busy = true;
            lastTime = System.currentTimeMillis();
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("cold", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
            s.onAccessibilityEvent(AccessibilityEvent.obtain(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED));
        }
    }
    private void foundAd (AccessibilityNodeInfo info) {
        Log.e(TAG, "found Ad"+info.getText().toString());
        String raw = QQ_JINXUAN;
        if (!pending && !record.contains(raw)) {
            busy = true;
            ClipboardManager mClipboard;
            mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
            ClipData mClip;
            mClip = ClipData.newPlainText("cold", raw);
            mClipboard.setPrimaryClip(mClip);
            Log.e(TAG, "Copied " + raw);
            pending = true;
            shouldWrite = true;
            busy = false;
        }
        addToRecord(QQ_JINXUAN);
    }
    private void addToRecord(String s) {
        record.add(s);
        lastTime = System.currentTimeMillis();
        Log.d(TAG, "Queue Added " + s);
        if (record.size() > 15) {
            Log.d(TAG, "Queue Removed " + record.poll());
        }
    }

    public void onReceive (AccessibilityNodeInfo info, MessageSenderService mss){
        this.s = mss;
        Log.d(TAG, "Now " + enabled);

        if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.TextView")) {
                //Reset
                if (info.getText().toString().equals("~reset")){
                    s.resetRobot();
                    Log.e(TAG, "Reset succeed");
                }
                if (!enabled) pending = false;
                //set enabled
                if (Pattern.compile("[0-9]{1,5}").matcher(info.getText().toString()).matches())
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                if (info.getText().toString().equals("二狗你过来一下") && !enabled) setEnable();
                if (info.getText().toString().equals("好了二狗不要再说了") && enabled) setDisable();
                if (info.getText().toString().equals("名片不要检测了") && checkProp) setNoCheck();
                if (info.getText().toString().equals("检测名片") && !checkProp) setCheck();

                Log.d(TAG, "msgLeft size"+ msgLeft.size());
                if (msgLeft.size() == 0) {
                    //if (Pattern.compile(QQ_RECOVER).matcher(info.getText().toString()).matches()
                    //&& !info.getText().toString().equals(lastKey)) setMaster(info);
                    //Check format
                    if (Pattern.compile(QQ_NAME_PAT).matcher(info.getText().toString()).matches() && checkProp)
                        checkName(info);
                    //Welcome
                    if (Pattern.compile(QQ_NEWBIE).matcher(info.getText().toString()).matches())
                        welcomeNewbie(info);
                    //Shuaping
                    if (info.getText().toString().equals("二狗来刷一下屏！")) clearing = true;
                    if (clearing) shuaPing();
                    //Check Ad
                    //if (Pattern.compile(QQ_AD).matcher(info.getText().toString()).matches() && !record.contains(QQ_JINXUAN)) foundAd(info);
                    //if (Pattern.compile(QQ_XUAN).matcher(info.getText().toString()).matches() && !record.contains(QQ_JINXUAN)
                    //        && !Pattern.compile(QQ_NAME_PAT).matcher(info.getText().toString()).matches()) foundAd(info);
                    //if (Pattern.compile(QQ_NUM).matcher(info.getText().toString()).matches() && !record.contains(QQ_JINXUAN)) foundAd(info);
                    //Teach
                    if (Pattern.compile(QQ_ENG).matcher(info.getText().toString()).matches()
                            && !info.getText().toString().equals(lastKey)) ask(info);
                    if (Pattern.compile(QQ_TEACH).matcher(info.getText().toString()).matches()
                            && !info.getText().toString().equals(lastKey)) shuo(info);
                    if (Pattern.compile(QQ_COMM).matcher(info.getText().toString()).matches()
                            && !info.getText().toString().equals(lastKey)) shuoMaster(info);
                    if (Pattern.compile(QQ_SEARCH).matcher(info.getText().toString()).matches()
                            && !info.getText().toString().equals(lastKey)) search(info);
                    //Admin
                    if (Pattern.compile(QQ_ADMIN).matcher(info.getText().toString()).matches()
                            && !info.getText().toString().equals(lastKey)) {
                        String comm = info.getText().toString().split(":")[1];
                        if (Pattern.compile(QQ_TEACH).matcher(comm).matches()) shuoAdmin(comm, 0);
                        if (Pattern.compile(QQ_BLACKLIST).matcher(comm).matches())
                            shuoAdmin(comm, 1);
                        if (Pattern.compile(QQ_SETTIME).matcher(comm).matches()) shuoAdmin(comm, 2);
                        if (Pattern.compile(QQ_RBLACKLIST).matcher(comm).matches())
                            shuoAdmin(comm, 3);
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
                } else {
                    if (!pending) {
                        String raw = msgLeft.poll();
                        busy = true;
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(MessageSenderService.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("list", raw);
                        mClipboard.setPrimaryClip(mClip);
                        Log.e(TAG, "Copied " + raw);
                        pending = true;
                        shouldWrite = true;
                        busy = false;
                    }
                }
            }
        }

        //Add records
        if (info.getClassName() != null && info.getText() != null) {
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
        }


        //Paste from Clipboard
        if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.EditText")
                    && shouldWrite && !busy && (enabled || lastPaste)) {
                busy = true;
                info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                Log.e(TAG, "Pasted");
                shouldSend = true;
                shouldWrite = false;
                busy = false;
            }
        }

        //Click send
        if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.Button")
                    && info.getText().toString().equals(QQ_SEND)
                    && shouldSend && !busy && (enabled || lastPaste)) {
                busy = true;
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.e(TAG, "Sent");
                shouldSend = false;
                pending = false;
                lastPaste = false;
                busy = false;
            }
        }

        //Warm up
        if (System.currentTimeMillis() - lastTime >= TIME) coldNotification();
    }
}
