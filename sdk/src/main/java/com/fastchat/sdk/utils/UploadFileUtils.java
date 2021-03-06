package com.fastchat.sdk.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.fastchat.sdk.SDKConstant;
import com.fastchat.sdk.manager.HTPreferenceManager;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by zhouzhuo on 12/3/15.
 */
public class UploadFileUtils {

    private OSS oss;
    private String testObject;
    private String uploadFilePath;
    private UploadCallBack uploadCallBack;
    String endpoint="";
    String accessKeyId="";
    String accessKeySecret="";
    String bucket="";
    String baseOssUrl="";


    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 201:
                    if(uploadCallBack!=null){
                        uploadCallBack.onSuccess(null,null);
                    }
                    break;
                case 200:
                    if(uploadCallBack!=null){
                        uploadCallBack.onProgress(null,0,0);
                    }
                    break;
                case 202:
                    if(uploadCallBack!=null){
                        uploadCallBack.onFailure(null,null,null);
                    }
                    break;

            }
        }
    };
    public UploadFileUtils(Context context, String testObject, String uploadFilePath) {

        if(SDKConstant.IS_LIMITLESS){
            endpoint= HTPreferenceManager.getInstance().getEndpoint();
            accessKeyId=HTPreferenceManager.getInstance().getAccessKeyId();
            accessKeySecret=HTPreferenceManager.getInstance().getAccessKeySecret();
            bucket=HTPreferenceManager.getInstance().getBucket();
            baseOssUrl=HTPreferenceManager.getInstance().getOssBaseUrl();
        }else {
            endpoint=SDKConstant.endpoint;
            accessKeyId=SDKConstant.accessKeyId;
            accessKeySecret=SDKConstant.accessKeySecret;
            bucket=SDKConstant.bucket;
            baseOssUrl=SDKConstant.baseOssUrl;
        }
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // ?????????????????????15???
        conf.setSocketTimeout(15 * 1000); // socket???????????????15???
        conf.setMaxConcurrentRequest(5); // ??????????????????????????????5???
        conf.setMaxErrorRetry(2); // ????????????????????????????????????2???
        OSSLog.enableLog();
        oss = new OSSClient(context, endpoint, credentialProvider, conf);
        this.testObject = testObject;
        this.uploadFilePath = uploadFilePath;
    }
    public  String getOssBaseUrl(){
        return   baseOssUrl;
    }
    // ???????????????????????????????????????????????????
    public void putObjectFromLocalFile() {
        // ??????????????????
        PutObjectRequest put = new PutObjectRequest(bucket, testObject, uploadFilePath);

        try {
            PutObjectResult putResult = oss.putObject(put);

            Log.d("PutObject", "UploadSuccess");

            Log.d("ETag", putResult.getETag());
            Log.d("RequestId", putResult.getRequestId());
        } catch (ClientException e) {
            // ??????????????????????????????
            e.printStackTrace();
        } catch (ServiceException e) {
            // ????????????
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        }
    }

    // ??????????????????????????????????????????????????????
    public void asyncUploadFile(final UploadCallBack uploadCallBack) {

        // ??????????????????
        PutObjectRequest put = new PutObjectRequest(bucket, testObject, uploadFilePath);

        // ???????????????????????????????????????
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                uploadCallBack.onProgress(request,currentSize,totalSize);
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                uploadCallBack.onSuccess(request,result);
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                uploadCallBack.onFailure(request,clientExcepion,serviceException);
                // ????????????
                if (clientExcepion != null) {
                    // ??????????????????????????????
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // ????????????
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }


    // ??????????????????????????????????????????????????????,???????????????
    public void uploadFile(  UploadCallBack uploadCallBack) {
           this.uploadCallBack=uploadCallBack;
        // ??????????????????
        PutObjectRequest put = new PutObjectRequest(bucket, testObject, uploadFilePath);

        // ???????????????????????????????????????
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                //uploadCallBack.onProgress(request,currentSize,totalSize);


                    Message message=handler.obtainMessage();
                    message.what=200;
                    handler.sendMessage(message);



                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Message message=handler.obtainMessage();
                message.what=201;
                handler.sendMessage(message);
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                Message message=handler.obtainMessage();
                message.what=202;
                handler.sendMessage(message);
                // ????????????
                if (clientExcepion != null) {
                    // ??????????????????????????????
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // ????????????
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }


    // ?????????????????????????????????????????????????????????
    public void putObjectFromByteArray() {
        // ???????????????????????????
        byte[] uploadData = new byte[100 * 1024];
        new Random().nextBytes(uploadData);

        // ??????????????????
        PutObjectRequest put = new PutObjectRequest(bucket, testObject, uploadData);

        try {
            PutObjectResult putResult = oss.putObject(put);

            Log.d("PutObject", "UploadSuccess");

            Log.d("ETag", putResult.getETag());
            Log.d("RequestId", putResult.getRequestId());
        } catch (ClientException e) {
            // ??????????????????????????????
            e.printStackTrace();
        } catch (ServiceException e) {
            // ????????????
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        }
    }

    // ???????????????ContentType??????????????????????????????meta??????
    public void putObjectWithMetadataSetting() {
        // ??????????????????
        PutObjectRequest put = new PutObjectRequest(bucket, testObject, uploadFilePath);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream");
        metadata.addUserMetadata("x-oss-meta-name1", "value1");

        put.setMetadata(metadata);

        // ???????????????????????????????????????
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // ????????????
                if (clientExcepion != null) {
                    // ??????????????????????????????
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // ????????????
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    // ????????????????????????server??????
    public void asyncPutObjectWithServerCallback() {
        // ??????????????????
        final PutObjectRequest put = new PutObjectRequest(bucket, testObject, uploadFilePath);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream");

        put.setMetadata(metadata);

        put.setCallbackParam(new HashMap<String, String>() {
            {
                put("callbackUrl", "110.75.82.106/mbaas/callback");
                put("callbackBody", "test");
            }
        });

        // ???????????????????????????????????????
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                // ???????????????servercallback????????????????????????
                String serverCallbackReturnJson = result.getServerCallbackReturnBody();

                Log.d("servercallback", serverCallbackReturnJson);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // ????????????
                if (clientExcepion != null) {
                    // ??????????????????????????????
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // ????????????
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }




  public   interface UploadCallBack {

        void onProgress(PutObjectRequest request, long currentSize, long totalSize);

        void onSuccess(PutObjectRequest request, PutObjectResult result);

        void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException);


    }

}
