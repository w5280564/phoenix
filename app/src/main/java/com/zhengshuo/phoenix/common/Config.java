package com.zhengshuo.phoenix.common;

import java.io.File;

/**
 * 全局配置类
 * @author ouyang
 */
public class Config {

    /**
     * 全局配置
     */
    public static class Setting {

        public static boolean isRedpacketCanWithdraw=true;

        // 开发环境
        public static final boolean DEBUG = false;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  ;

        // 是否输出日志
        public static final boolean IS_LOG = true;


        // 图片保存地址
        public static final String IMG_CACHE = "yuruan" + File.separator

                + "Download";
    }


    /**
     *
     * URL配置
     */
    public static class Link {

        /**
         * 开发环境
         */
        public static String URL_WHOLE = "http://124.70.201.235:8088";




        /**
         * 生产环境
         */
        static {
            if (!Setting.DEBUG) {
                URL_WHOLE = "http://124.70.201.235:8088";
            }
        }

        public static String getWholeUrl() {
            return URL_WHOLE;
        }


    }


    /**
     *
     * 参数配置
     */
    public static class Parameter {

        /**
         * 开发环境
         */

        public static String BUCKET_NAME = "summer";

        public static String TENCENT_FRESH_FACE_APP_ID = "IDA3ldWp";


        /**
         * 生产环境
         */
        static {
            if (!Setting.DEBUG) {
                BUCKET_NAME = "summer";
                TENCENT_FRESH_FACE_APP_ID = "IDA3ldWp";
            }
        }



        public static String getBUCKET_NAME() {
            return BUCKET_NAME;
        }


        public static String getTencentFreshFaceAppId() {
            return TENCENT_FRESH_FACE_APP_ID;
        }

    }

}
