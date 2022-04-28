package com.fastchat.sdk.logcollect.logcat;

import android.content.Context;
import android.os.Environment;

import com.fastchat.sdk.logcollect.utils.FileSizeUtil;
import com.fastchat.sdk.manager.HTPreferenceManager;
import com.fastchat.sdk.service.MessageService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * @Description: log日志打印
 * @Author: ouyang
 * @CreateDate: 2022/3/29
 */
public class LogcatHelper {

    private static final String LOGCAT_FNAME = "yuruan_log";
    private static final String LOGCAT_NAME = ".log";
    private final String LOG_NAME_FORMAT = "user_%s";
    private static final double MAX_SIZE = 20;

    private static LogcatHelper mInstance;
    private LogDumper mLogDumper;
    private File file;

    public static LogcatHelper getInstance() {
        if (mInstance == null)
            synchronized (LogcatHelper.class) {
                if (mInstance == null)
                    mInstance = new LogcatHelper();
            }
        return mInstance;
    }

    private LogcatHelper() {
    }


    public String getFileName() {
        String userName = HTPreferenceManager.getInstance().getUser().getUsername();
        return String.format(LOG_NAME_FORMAT, userName) + LOGCAT_NAME;
    }


    public void logDelete(boolean isStart) {
        stop();
        file.delete();
        if (isStart)
            start();
    }

    public void init(Context context) {
        File logDir = isSDCardExist() ? context.getExternalFilesDir(null) : context.getFilesDir();
        file = new File(logDir.getAbsolutePath() + File.separator + LOGCAT_FNAME, getFileName());
    }

    private boolean isSDCardExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(android.os.Process.myPid()), file);
        if (mLogDumper.getState() == Thread.State.NEW) {
            mLogDumper.start();
        }

    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private String pid;
        private File file;

        private boolean mRunning = true;
        private FileOutputStream output;
        private BufferedReader mReader;

        public LogDumper(String pid, File file) {
            this.pid = pid;
            this.file = file;
            initStart();
        }

        private void initStart() {
            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息


            try {
                String cmds = "logcat  | grep \"(" + pid + ")\"";//打印所有日志信息
                Process logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                output = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                String line;
                while (mRunning && mReader != null && (line = mReader.readLine()) != null) {
                    if (line.length() == 0) continue;
                    double size = FileSizeUtil.getFileOrFilesSize(file, FileSizeUtil.SIZETYPE_MB);
                    if (size > MAX_SIZE) {
                        LogcatHelper.this.stop();
                        file.delete();
                        LogcatHelper.this.start();
                    }
                    if (output != null && line.contains(pid) && (line.contains(MessageService.TAG_SEND) || line.contains(MessageService.TAG_RECEIVE))) {
                        output.write((line + "\n").getBytes());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mReader != null) {
                    try {
                        mReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}