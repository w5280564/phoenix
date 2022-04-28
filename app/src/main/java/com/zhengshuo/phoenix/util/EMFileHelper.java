package com.zhengshuo.phoenix.util;

public class EMFileHelper {
//    private static class EMFileHelperInstance {
//        private static final EMFileHelper instance = new EMFileHelper();
//    }
//
//    private Context mContext;
//    private IFilePresenter mHelper;
//
//    private EMFileHelper() {
//        mContext = EMClient.getInstance().getContext();
//        mHelper = new FilePresenterImpl();
//    }
//
//    public static EMFileHelper getInstance() {
//        return EMFileHelperInstance.instance;
//    }
//
//    /**
//     * \~chinese
//     * 设置自定义的FilePresenter
//     * @param presenter
//     *
//     * \~english
//     * Set custom FilePresenter
//     * @param presenter
//     */
//    public void setFileHelper(IFilePresenter presenter) {
//        this.mHelper = presenter;
//    }
//
//    /**
//     * \~chinese
//     * 判断文件是否存在，需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param fileUri   文件的资源标识符（路径）
//     * @return  文件是否存在
//     *
//     * \~english
//     * Whether the file exists, call it after {@link EMClient#init(Context, EMOptions)}
//     * @param fileUri   File's Uri
//     * @return  Whether the file exists
//     */
//    public boolean isFileExist(Uri fileUri) {
//        return mHelper.isFileExist(mContext, fileUri);
//    }
//
//    /**
//     * \~chinese
//     * 判断文件是否存在，需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param stringUri 文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
//     * @return  文件是否存在
//     *
//     * \~english
//     * Whether the file exists, call it after {@link EMClient#init(Context, EMOptions)}
//     * @param stringUri File's path, which may be the absolute path of file or the string from of Uri
//     * @return  Whether the file exists
//     */
//    public boolean isFileExist(String stringUri) {
//        if (TextUtils.isEmpty(stringUri)) {
//            return false;
//        }
//        return isFileExist(Uri.parse(stringUri));
//    }
//
//    /**
//     * \~chinese
//     * 判断文件是否存在
//     * @param context   上下文
//     * @param fileUri   文件资源标识符（路径）
//     * @return  文件是否存在
//     *
//     * \~english
//     * Determine whether the file exists
//     * @param context
//     * @param fileUri   File's Uri
//     * @return  Whether the file exists
//     */
//    public boolean isFileExist(Context context, Uri fileUri) {
//        return mHelper.isFileExist(context, fileUri);
//    }
//
//    /**
//     * \~chinese
//     * 判断文件是否存在
//     * @param context   上下文
//     * @param stringUri   文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
//     * @return  文件是否存在
//     *
//     * \~english
//     * Determine whether the file exists
//     * @param context
//     * @param stringUri   File's path, which may be the absolute path of file or the string from of Uri
//     * @return  Whether the file exists
//     */
//    public boolean isFileExist(Context context, String stringUri) {
//        if (TextUtils.isEmpty(stringUri)) {
//            return false;
//        }
//        return isFileExist(context, Uri.parse(stringUri));
//    }
//
//    /**
//     * \~chinese
//     * 获取文件名称
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param fileUri   文件的资源标识符（路径）
//     * @return  文件名称
//     *
//     * \~english
//     * Get file name
//     * Note：Calls after {@link EMClient#init(Context, EMOptions)}
//     * @param fileUri   File's Uri
//     * @return  File name
//     */
//    public String getFilename(Uri fileUri) {
//        return mHelper.getFilename(mContext, fileUri);
//    }
//
//    /**
//     * \~chinese
//     * 获取文件名称
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param filePath   文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
//     * @return  文件名称
//     *
//     * \~english
//     * Get file name
//     * Note：Calls after {@link EMClient#init(Context, EMOptions)}
//     * @param filePath   File's path, which may be the absolute path of file or the string from of Uri
//     * @return  File name
//     */
//    public String getFilename(String filePath) {
//        if (TextUtils.isEmpty(filePath)) {
//            return "";
//        }
//        return getFilename(Uri.parse(filePath));
//    }
//
//    /**
//     * \~chinese
//     * 获取文件的绝对路径
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param fileUri   文件的资源标识符（路径）
//     * @return  文件路径
//     *
//     * \~english
//     * Get file's absolute path
//     * Note：Calls after {@link EMClient#init(Context, EMOptions)}
//     * @param fileUri   File's Uri
//     * @return  File's absolute path
//     */
//    public String getFilePath(Uri fileUri) {
//        return mHelper.getFilePath(mContext, fileUri);
//    }
//
//    /**
//     * \~chinese
//     * 获取文件的绝对路径
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param filePath   文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
//     * @return  文件的绝对路径
//     *
//     * \~english
//     * Get file's absolute path
//     * Note：Calls after {@link EMClient#init(Context, EMOptions)}
//     * @param filePath   File's path, which may be the absolute path of file or the string from of Uri
//     * @return  File's absolute path
//     */
//    public String getFilePath(String filePath)
//    {
//        if (TextUtils.isEmpty(filePath)) {
//            return filePath;
//        }
//        return getFilePath(Uri.parse(filePath));
//    }
//
//    /**
//     * \~chinese
//     * 获取文件的绝对路径
//     * @param context   上下文
//     * @param fileUri   文件的资源标识符（路径）
//     * @return  文件路径
//     *
//     * \~english
//     * Get file's absolute path
//     * @param context
//     * @param fileUri   File's Uri
//     * @return  File's absolute path
//     */
//    public String getFilePath(Context context, Uri fileUri) {
//        return mHelper.getFilePath(context, fileUri);
//    }
//
//    /**
//     * \~chinese
//     * 获取文件的绝对路径
//     * @param context    上下文
//     * @param filePath   文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
//     * @return  文件的绝对路径
//     *
//     * \~english
//     * Get file's absolute path
//     * @param context
//     * @param filePath   File's path, which may be the absolute path of file or the string from of Uri
//     * @return  File's absolute path
//     */
//    public String getFilePath(Context context, String filePath)
//    {
//        if (TextUtils.isEmpty(filePath)) {
//            return filePath;
//        }
//        return getFilePath(context, Uri.parse(filePath));
//    }
//
//    /**
//     * \~chinese
//     * 获取文件大小
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param fileUri   文件的资源标识符（路径）
//     * @return  文件大小
//     *
//     * \~english
//     * Get file length
//     * Note：Calls after {@link EMClient#init(Context, EMOptions)}
//     * @param fileUri   File's Uri
//     * @return  File length
//     */
//    public long getFileLength(Uri fileUri) {
//        return mHelper.getFileLength(mContext, fileUri);
//    }
//
//    /**
//     * \~chinese
//     * 获取文件大小
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param filePath   文件路径，可能是文件的绝对路径，也可能是资源标识符的字符串形式
//     * @return  文件大小
//     *
//     * \~english
//     * Get file length
//     * Note：Calls after {@link EMClient#init(Context, EMOptions)}
//     * @param filePath   File's path, which may be the absolute path of file or the string from of Uri
//     * @return  File length
//     */
//    public long getFileLength(String filePath) {
//        if (TextUtils.isEmpty(filePath)) {
//            return 0;
//        }
//        return getFileLength(Uri.parse(filePath));
//    }
//
//    /**
//     * \~chinese
//     * 获取文件类型
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param fileUri   文件的资源标识符（路径）
//     * @return  文件类型
//     *
//     * \~english
//     * Get file mime type
//     * Note：Calls after {@link EMClient#init(Context, EMOptions)}
//     * @param fileUri   File's Uri
//     * @return  File mime type
//     */
//    public String getFileMimeType(Uri fileUri) {
//        return mHelper.getFileMimeType(mContext, fileUri);
//    }
//
//    /**
//     * 删除文件（可以获取到绝对路径）
//     * SDk内部调用，不建议外部使用
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param filePath
//     * @return
//     */
//    public boolean deletePrivateFile(String filePath) {
//        if (TextUtils.isEmpty(filePath)) {
//            return false;
//        }
//        if (!isFileExist(filePath)) {
//            return false;
//        }
//        filePath = getFilePath(Uri.parse(filePath));
//        if (!TextUtils.isEmpty(filePath))
//        {
//            File file = new File(filePath);
//            if (file.exists()) {
//                return file.delete();
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 格式化输入到SDK内部的文件的Uri，并输出未Uri
//     * SDk内部调用，不建议外部使用
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param fileUri
//     * @return
//     */
//    public Uri formatInUri(Uri fileUri) {
//        if (fileUri == null) {
//            return null;
//        }
//        if ((VersionUtils.isTargetQ(mContext)) && (UriUtils.uriStartWithContent(fileUri))) {
//            return fileUri;
//        }
//        String path = getFilePath(fileUri);
//        if (!TextUtils.isEmpty(path)) {
//            fileUri = Uri.parse(path);
//        }
//        return fileUri;
//    }
//
//    /**
//     * 格式化输入到SDK的文件，并输出未Uri
//     * SDk内部调用，不建议外部使用
//     * @param file
//     * @return
//     */
//    public Uri formatInUri(File file) {
//        if (file == null) {
//            return null;
//        }
//        return Uri.parse(file.getAbsolutePath());
//    }
//
//    /**
//     * 格式化输入到SDK内部的文件的Uri，并输出未Uri
//     * SDk内部调用，不建议外部使用
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param filePath
//     * @return
//     */
//    public Uri formatInUri(String filePath) {
//        if (TextUtils.isEmpty(filePath)) {
//            return null;
//        }
//        return formatInUri(Uri.parse(filePath));
//    }
//
//    /**
//     * 将格式化后的Uri转为string
//     * SDk内部调用，不建议外部使用
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param uri
//     * @return
//     */
//    public String formatInUriToString(Uri uri) {
//        uri = formatInUri(uri);
//        if (uri == null) {
//            return "";
//        }
//        return uri.toString();
//    }
//
//    /**
//     * 格式化文件，并最终转为Uri的string样式
//     * SDk内部调用，不建议外部使用
//     * @param file
//     * @return
//     */
//    public String formatInUriToString(File file) {
//        Uri fileUri = formatInUri(file);
//        if (fileUri == null) {
//            return "";
//        }
//        return fileUri.toString();
//    }
//
//    /**
//     * 格式化传入的路径，并最终转为Uri的string样式
//     * SDk内部调用，不建议外部使用
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param filePath
//     * @return
//     */
//    public String formatInUriToString(String filePath) {
//        if (TextUtils.isEmpty(filePath)) {
//            return "";
//        }
//        return formatInUriToString(Uri.parse(filePath));
//    }
//
//    /**
//     * 格式化输出的路径，如果可以获取到绝对路径，则优先返回绝对路径
//     * SDk内部调用，不建议外部使用
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param filePath
//     * @return
//     */
//    public String formatOutLocalUrl(String filePath) {
//        if (TextUtils.isEmpty(filePath)) {
//            return filePath;
//        }
//        String path = getFilePath(filePath);
//        if (!TextUtils.isEmpty(path)) {
//            return path;
//        }
//        return filePath;
//    }
//
//    /**
//     * 格式化输出的Uri
//     * SDk内部调用，不建议外部使用
//     * 注：需要在{@link EMClient#init(Context, EMOptions)}后调用
//     * @param filePath
//     * @return
//     */
//    public Uri formatOutUri(String filePath) {
//        if (TextUtils.isEmpty(filePath)) {
//            return null;
//        }
//        Uri fileUri = Uri.parse(filePath);
//        if ((VersionUtils.isTargetQ(mContext)) && (UriUtils.uriStartWithContent(fileUri))) {
//            return fileUri;
//        }
//        String str = getFilePath(fileUri);
//        if (!TextUtils.isEmpty(str)) {
//            fileUri = Uri.fromFile(new File(str));
//        }
//        return fileUri;
//    }
//
//    /**
//     * \~chinese
//     * 将Uri转成string类型
//     * @param fileUri   文件的资源标识符（路径）
//     * @return
//     *
//     * \~english
//     * Convert the URI to a String
//     * @param fileUri   File's Uri
//     * @return
//     */
//    public String uriToString(Uri fileUri) {
//        if (fileUri == null) {
//            return "";
//        }
//        return fileUri.toString();
//    }
//
//    public static class FilePresenterImpl implements IFilePresenter {
//
//        @Override
//        public boolean isFileExist(Context context, Uri fileUri) {
//            return UriUtils.isFileExistByUri(context, fileUri);
//        }
//
//        @Override
//        public String getFilename(Context context, Uri fileUri) {
//            return UriUtils.getFileNameByUri(context, fileUri);
//        }
//
//        @Override
//        public String getFilePath(Context context, Uri fileUri) {
//            return UriUtils.getFilePath(context, fileUri);
//        }
//
//        @Override
//        public long getFileLength(Context context, Uri fileUri) {
//            return UriUtils.getFileLength(context, fileUri);
//        }
//
//        @Override
//        public String getFileMimeType(Context context, Uri fileUri) {
//            return UriUtils.getMimeType(context, fileUri);
//        }
//    }
//
//    /**
//     * \~chinese
//     * 操作文件接口
//     *
//     * \~english
//     * Operation file interface
//     */
//    public interface IFilePresenter {
//
//        /**
//         * \~chinese
//         * 用于判断文件是否存在
//         * @param context   上下文
//         * @param fileUri   文件的资源标识符（路径）
//         * @return 文件是否存在
//         *
//         * \~english
//         * Determine whether file exists
//         * @param context
//         * @param fileUri   File's Uri
//         * @return  Whether file exists
//         */
//        boolean isFileExist(Context context, Uri fileUri);
//
//        /**
//         * \~chinese
//         * 获取文件名
//         * @param context   上下文
//         * @param fileUri   文件的资源标识符（路径）
//         * @return 文件名
//         *
//         * \~english
//         * Get file name
//         * @param context
//         * @param fileUri   File's Uri
//         * @return  File name
//         */
//        String getFilename(Context context, Uri fileUri);
//
//        /**
//         * \~chinese
//         * 获取文件绝对路径
//         * @param context   上下文
//         * @param fileUri   文件的资源标识符（路径）
//         * @return 文件绝对路径
//         *
//         * \~english
//         * Get file's absolute path
//         * @param context
//         * @param fileUri   File's Uri
//         * @return  File's absolute path
//         */
//        String getFilePath(Context context, Uri fileUri);
//
//        /**
//         * \~chinese
//         * 获取文件大小
//         * @param context   上下文
//         * @param fileUri   文件的资源标识符（路径）
//         * @return 文件大小
//         *
//         * \~english
//         * Get file length
//         * @param context
//         * @param fileUri   File's Uri
//         * @return  File length
//         */
//        long getFileLength(Context context, Uri fileUri);
//
//        /**
//         * \~chinese
//         * 获取文件类型
//         * @param context   上下文
//         * @param fileUri   文件的资源标识符（路径）
//         * @return 文件类型
//         *
//         * \~english
//         * Get file mime type
//         * @param context
//         * @param fileUri   File's Uri
//         * @return  File mime type
//         */
//        String getFileMimeType(Context context, Uri fileUri);
//    }
}
