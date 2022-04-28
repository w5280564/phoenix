package com.zhengshuo.phoenix.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileHelper {

    private static final String TAG = FileHelper.class.getSimpleName();
    private static final String COMMON_PATH_NAME = "Pictures/Phoenix";


        /**
         * 将图片保存到系统相册
         */
        public static boolean saveImgToAlbum(Context context, String imageFile) {
            LogUtil.d(TAG, "saveImgToAlbum() imageFile = [" + imageFile + "]");
            try {
                ContentResolver localContentResolver = context.getContentResolver();
                File tempFile = new File(imageFile);
                ContentValues localContentValues = getImageContentValues(tempFile, System.currentTimeMillis());
                Uri uri = localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);

                copyFileAfterQ(context, localContentResolver, tempFile, uri);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * 获取图片的ContentValue
         */
        public static ContentValues getImageContentValues(File paramFile, long timestamp) {
            ContentValues localContentValues = new ContentValues();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                localContentValues.put(MediaStore.Images.Media.RELATIVE_PATH, COMMON_PATH_NAME);
            }
            localContentValues.put(MediaStore.Images.Media.TITLE, paramFile.getName());
            localContentValues.put(MediaStore.Images.Media.DISPLAY_NAME, paramFile.getName());
            localContentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            localContentValues.put(MediaStore.Images.Media.DATE_TAKEN, timestamp);
            localContentValues.put(MediaStore.Images.Media.DATE_MODIFIED, timestamp);
            localContentValues.put(MediaStore.Images.Media.DATE_ADDED, timestamp);
            localContentValues.put(MediaStore.Images.Media.ORIENTATION, 0);
            localContentValues.put(MediaStore.Images.Media.DATA, paramFile.getAbsolutePath());
            localContentValues.put(MediaStore.Images.Media.SIZE, paramFile.length());
            return localContentValues;
        }

        /**
         * 将视频保存到系统相册
         */
        public static boolean saveVideoToAlbum(Context context, String videoFile) {
            LogUtil.d(TAG, "saveVideoToAlbum() videoFile = [" + videoFile + "]");
            try {
                ContentResolver localContentResolver = context.getContentResolver();
                File tempFile = new File(videoFile);
                ContentValues localContentValues = getVideoContentValues(tempFile, System.currentTimeMillis());

                Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);

                copyFileAfterQ(context, localContentResolver, tempFile, localUri);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private static void copyFileAfterQ(Context context, ContentResolver localContentResolver, File tempFile, Uri localUri) throws IOException {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.Q) {
                //拷贝文件到相册的uri,android11及以上得这么干，否则不会显示。可以参考ScreenMediaRecorder的save方法
                OutputStream os = localContentResolver.openOutputStream(localUri, "w");
                Files.copy(tempFile.toPath(), os);
                os.close();
                tempFile.deleteOnExit();
            }
        }


        /**
         * 获取视频的contentValue
         */
        public static ContentValues getVideoContentValues(File paramFile, long timestamp) {
            ContentValues localContentValues = new ContentValues();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    localContentValues.put(MediaStore.Video.Media.RELATIVE_PATH, COMMON_PATH_NAME);
            }
            localContentValues.put(MediaStore.Video.Media.TITLE, paramFile.getName());
            localContentValues.put(MediaStore.Video.Media.DISPLAY_NAME, paramFile.getName());
            localContentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            localContentValues.put(MediaStore.Video.Media.DATE_TAKEN, timestamp);
            localContentValues.put(MediaStore.Video.Media.DATE_MODIFIED, timestamp);
            localContentValues.put(MediaStore.Video.Media.DATE_ADDED, timestamp);
            localContentValues.put(MediaStore.Video.Media.DATA, paramFile.getAbsolutePath());
            localContentValues.put(MediaStore.Video.Media.SIZE, paramFile.length());
            return localContentValues;
        }

    private static boolean isSDCardExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    private static String initPath(Context context,String dir) {
        File logDir = isSDCardExist() ? context.getExternalFilesDir(null) : context.getFilesDir();
        String storagePath = logDir.getAbsolutePath() + File.separator + dir;
        File f = new File(storagePath);
        if (!f.exists()) {
            f.mkdir();
        }
        return storagePath;
    }

    public static String saveBitmap(Context context,String dir, Bitmap b) {
        String path = initPath(context,dir);
        long dataTake = System.currentTimeMillis();
        String jpegName = path + File.separator + "picture_" + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return jpegName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
