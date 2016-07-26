package com.willhd.baozhaoqq;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Environment;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by William on 2016/7/6.
 */

public class UserMsg extends Msg{
    private final String TAG = "UserMsg";
    public final String TYPE = "User Msg";

    private final String QQ_JIEXIN = "#接新\n[^#]{1,255}";
    private final String QQ_XUQIU = "#需求\n[^#]{1,255}";
    private final String QQ_RECALLN = "#撤回需求\n[^#]{1,255}";
    private final String QQ_RECALLJ = "#撤回接新\n[^#]{1,255}";
    private final String QQ_FINDJX = "查找接新[^:]{1,255}";
    private final String QQ_FINDXQ = "查找需求[^:]{1,255}";
    private final String QQ_CIRNAME = "圈名[:：][^:]{1,64}";
    private final String QQ_NEWBIE = "[^:]{1,64} 已加入该群";
    private final String QQ_TEACH = "说[^:]{1,255}";
    private final String QQ_ENG = "小饭[a-zA-Z0-9 ,\\.\\?!'-]{1,64}";
    private final String QQ_HELP = "二狗";
    private final String QQ_AD = "[^测^接^需]{0,255}[a-zA-Z0-9]{1,255}\\.[a-zA-Z0-9]{1,255}\\.[a-zA-Z0-9]{1,64}[^测^接^需]{0,255}";
    private final String QQ_XUAN = "[^测^接^需]{0,255}宣[^测^接^需]{0,255}";
    private final String QQ_NUM = "[^测^接^需]{0,255}[0-9]{7,255}[^测^接^需]{0,255}";
    private final String QQ_JINXUAN = "检测到宣传！\n本群禁宣请立即撤回！";
    private final String QQ_SEARCH = "什么是[^`]{1,255}";
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
                    "我是机器人小饭求眼熟～\n\n" + QQ_JXHELP + "\n\n" + QQ_XQHELP +
                    "\n\n请认真阅读以上信息！！！最后祝大家在群里开开心心~";

    private static String searchWord = "";
    private static String question = "";

    /*private Runnable downChat = new Runnable() {
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
    };*/

    public UserMsg (AccessibilityNodeInfo nodeInfo, Robot robot){
        super(nodeInfo, robot);
        Log.d(TAG, "added to User"+nodeInfo.getText().toString());
    }

    /*private void setEnable () {
        String raw = "来嘞来嘞";
        robot.setEnabled(true);
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void setDisable () {
        String raw = "不说了不说了";
        robot.setEnabled(false);
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void setCheck () {
        String raw = "小饭检测名片了";
        robot.setCheckProp(true);
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void setNoCheck () {
        String raw = "小饭不检测名片了";
        robot.setCheckProp(false);
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void sendResponse (AccessibilityNodeInfo info) {
        String raw = robot.getResponses().get(info.getText().toString());
        Log.d(TAG, "Key discovered: " + raw);
        robot.setCheckProp(false);
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void showResponse () {
        String raw = robot.getResponses().toString();
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
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
                raw = "未找到该帮助";
        }
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void shuaPing (int k) {
        if (k > 20) k = 20;
        String raw = "小饭正在刷屏......\n小饭准备刷" +k+"次";
        for (int i = 0; i < k; i++ ) robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void shuo (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replaceFirst("说", "");
        for (int i = 0; i < robot.getBlackList().size(); i++) {
            String check = "[^#]{0,255}" + robot.getBlackList().get(i) + "[^#]{0,255}";
            if (Pattern.compile(check).matcher(raw).matches())
                raw = "经过一系列思想斗争，小饭决定不说";
        }
        Log.d(TAG, "Saying " + raw);
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void search (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replace("什么是", "");
        Log.d(TAG, "Searching " + raw);
        Log.e(TAG, "GET request");
        searchWord = raw;
        new Thread(downData).start();
        raw = raw + "?且容小饭思考一下哈...";
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void ask (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replaceFirst("小饭", "");
        Log.d(TAG, "Asking " + raw);
        Log.e(TAG, "POST request");
        question = raw;
        new Thread(downChat).start();
        raw = "Let me think...";
        Log.e(TAG, "Copied " );
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void searchDone (String raw){
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void askDone (String raw){
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void findXQ () {
        Log.d(TAG, "Checking needs array"+robot.getNeeds().keySet().toString());
        String raw = "所有需求名单共"+robot.getNeeds().size()+"个";
        robot.addToOutput(raw);
        for (String i: robot.getNeeds().keySet()) {
            robot.addToOutput(i + "\n" + robot.getNeeds().get(i));
            Log.d(TAG, "msgLeft added" + i);
        }
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void findJX () {
        Log.d(TAG, "Checking jiexin array"+robot.getJiexins().keySet().toString());
        String raw = "所有接新名单共"+robot.getJiexins().size()+"个";
        robot.addToOutput(raw);
        for (String i: robot.getJiexins().keySet()) {
            robot.addToOutput(i + "\n" + robot.getJiexins().get(i));
            Log.d(TAG, "msgLeft added" + i);
        }
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void searchXQ (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replaceFirst("查找需求", "").toLowerCase();
        Log.d(TAG, "Checking needs array"+robot.getNeeds().keySet().toString());
        robot.addToOutput("小饭正在查找"+raw+"...");
        int r = 0;
        for (String i: robot.getNeeds().keySet()) {
            if (robot.getNeeds().get(i).contains("工种")) {
                if (robot.getNeeds().get(i).split("工种[：:]]")[1].split("\n")[0].contains(raw)) {
                    robot.addToOutput(i + "\n" + robot.getNeeds().get(i));
                    Log.d(TAG, "msgLeft added" + i);
                    r++;
                }
            }
        }
        raw = "小饭查找到需求名单共"+r+"个";
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void searchJX (AccessibilityNodeInfo info) {
        String raw = info.getText().toString().replaceFirst("查找接新", "").toLowerCase();
        Log.d(TAG, "Checking jiexins array"+robot.getJiexins().keySet().toString());
        robot.addToOutput("小饭正在查找"+raw+"...");
        int r = 0;
        for (String i: robot.getJiexins().keySet()) {
            if (robot.getJiexins().get(i).contains("工种")) {
                if (robot.getJiexins().get(i).split("工种[：:]]")[1].split("\n")[0].contains(raw)) {
                    robot.addToOutput(i + "\n" + robot.getJiexins().get(i));
                    Log.d(TAG, "msgLeft added" + i);
                    r++;
                }
            }
        }
        raw = "小饭查找到接新名单共"+r+"个";
        robot.addToOutput(raw);
        Log.d(TAG, "Copied " + raw);
    }
    private void addJiexin (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Jiexin discovered: " + raw);
        if (raw.split("=").length < 2 ||
                (raw.split("圈名[:：]]").length > 2 &&
                        (raw.split("圈名[：:]")[0].contains("*")) || raw.split("圈名[：:]")[1].startsWith("="))) {
            robot.addToOutput("格式错误！请输入接新查看具体格式再试。");
            Log.e(TAG, "Copied Error");
        } else {
            if (!robot.getJiexins().containsKey(raw.split("=")[0].replace("#接新\n", ""))) {
                robot.addToJiexins(raw);
                Log.e(TAG, "Added " + raw);
            }
        }
    }
    private void addXuqiu (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Needs discovered: " + raw);
        if (raw.split("=").length < 2 ||
                (raw.split("圈名[:：]]").length > 2 &&
                        (raw.split("圈名[：:]")[0].contains("*")) || raw.split("圈名[：:]")[1].startsWith("="))) {
            robot.addToOutput("格式错误！请输入需求查看具体格式再试。");
            Log.e(TAG, "Copied Error");
        } else {
            if (!robot.getNeeds().containsKey(raw.split("=")[0].replace("#需求\n", ""))) {
                robot.addToNeeds(raw);
                Log.e(TAG, "Added " + raw);
            }
        }
    }
    private void recallJiexin (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Recall discovered: " + raw);
        if (robot.getJiexins().containsKey(raw.replace("#撤回接新\n", ""))) {
            robot.recallJiexins(raw);
        }

    }
    private void recallXuqiu (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "Recall discovered: " + raw);
        if (robot.getNeeds().containsKey(raw.replace("#撤回需求\n", ""))) {
            robot.recallNeeds(raw);
        }

    }
    private void welcomeNewbie (AccessibilityNodeInfo info) {
        String raw = info.getText().toString();
        Log.e(TAG, "newer discovered: " + raw);
        raw = "欢迎" + raw.replace(" 已加入该群", "") +
                    ".注意！\n本群禁宣" +
                    "本群禁宣" +
                    "本群禁宣\n" +
                    "请新同学先改名片：(工种)xxx \n工种比如：cv、翻唱、后期、美工、填词、策划、导演、编剧等。\n" +
                    "「什么都不会可以改交流或学习哦」\n" +
                    "〖马甲需要帮忙的请艾特管理，告知管理工种和名字〗";
        robot.addToOutput(raw);
    }
    private void foundAd (AccessibilityNodeInfo info) {
        Log.e(TAG, "found Ad"+info.getText().toString());
        robot.addToOutput("检测到宣传，本群禁宣请撤回");
    }


    public void outputResponse () {
        Log.d(TAG, "UserMsg output");

        if (info.getText().toString().equals("二狗你过来一下") && !robot.isEnabled()) setEnable();
        if (info.getText().toString().equals("好了二狗不要再说了") && robot.isEnabled()) setDisable();
        if (info.getText().toString().equals("名片不要检测了") && robot.isCheckProp()) setNoCheck();
        if (info.getText().toString().equals("检测名片") && !robot.isCheckProp()) setCheck();

        //Shuaping
        if (info.getText().toString().equals("二狗刷一下屏")) shuaPing(10);
        //Check Ad
        //if (Pattern.compile(QQ_AD).matcher(info.getText().toString()).matches()) foundAd(info);
        //if (Pattern.compile(QQ_XUAN).matcher(info.getText().toString()).matches()
        //        && !Pattern.compile(QQ_NAME_PAT).matcher(info.getText().toString()).matches()) foundAd(info);
        //if (Pattern.compile(QQ_NUM).matcher(info.getText().toString()).matches()) foundAd(info);
        //Teach
        if (Pattern.compile(QQ_ENG).matcher(info.getText().toString()).matches()) ask(info);
        if (Pattern.compile(QQ_TEACH).matcher(info.getText().toString()).matches()) shuo(info);
        if (Pattern.compile(QQ_SEARCH).matcher(info.getText().toString()).matches()) search(info);
        //show commands
        if (info.getText().toString().equals("~show")) showResponse();
        //show help
        if (info.getText().toString().equals(QQ_HELP) ) showHelp(1);
        if (info.getText().toString().equals("接新")) showHelp(2);
        if (info.getText().toString().equals("需求")) showHelp(3);
        //显示需求
        if (info.getText().toString().equals("找需求")) findXQ();
        if (Pattern.compile(QQ_FINDXQ).matcher(info.getText().toString()).matches()) searchXQ(info);
        //显示接新
        if (info.getText().toString().equals("找接新")) findJX();
        if (Pattern.compile(QQ_FINDJX).matcher(info.getText().toString()).matches()) searchJX(info);

        //send response
        if (robot.getResponses().containsKey(info.getText().toString())) sendResponse(info);
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
        //Welcome
        if (Pattern.compile(QQ_NEWBIE).matcher(info.getText().toString()).matches())
            welcomeNewbie(info);
    }
*/
}
