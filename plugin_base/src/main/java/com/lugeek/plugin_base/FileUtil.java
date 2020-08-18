package com.lugeek.plugin_base;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.lugeek.plugin_base.replugin.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {

    /**
     * 复制assets资源到本地
     * @param context 上下文
     * @param assetsPath assets资源名
     * @param savePath 需要拷贝的本地路径
     * @return 保存好的本地路径
     */
    public static String copyFilesFromAssets(Context context, String assetsPath, String savePath) {
        try {
            InputStream is = context.getAssets().open(assetsPath);
            if (!savePath.endsWith("/")){
                savePath += "/";
            }
            FileOutputStream fos = new FileOutputStream(new File(savePath + assetsPath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
        } catch (Exception e) {
           return null;
        }
        return savePath + assetsPath;
    }

    /**
     * 获取应用专属缓存目录
     * android 4.4及以上系统不需要申请SD卡读写权限
     * 因此也不用考虑6.0系统动态申请SD卡读写权限问题，切随应用被卸载后自动清空 不会污染用户存储空间
     *
     * @param context 上下文
     * @param type    文件夹类型 可以为空，为空则返回API得到的一级目录
     * @return 缓存文件夹 如果没有SD卡或SD卡有问题则返回内存缓存目录，否则优先返回SD卡缓存目录
     */
    public static File getCacheDirectory(Context context, String type) {
        File appCacheDir = getExternalCacheDirectory(context, type);
        if (appCacheDir == null) {
            appCacheDir = getInternalCacheDirectory(context, type);
        }

        if (appCacheDir == null) {
            //Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is mobile phone unknown exception !");
        } else {
            if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                //Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is make directory fail !");
            }
        }
        return appCacheDir;
    }

    /**
     * 获取SD卡缓存目录
     *
     * @param context 上下文
     * @param type    文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
     *                否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
     *                {@link Environment#DIRECTORY_MUSIC},
     *                {@link Environment#DIRECTORY_PODCASTS},
     *                {@link Environment#DIRECTORY_RINGTONES},
     *                {@link Environment#DIRECTORY_ALARMS},
     *                {@link Environment#DIRECTORY_NOTIFICATIONS},
     *                {@link Environment#DIRECTORY_PICTURES}, or
     *                {@link Environment#DIRECTORY_MOVIES}.or 自定义文件夹名称
     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
     */
    public static File getExternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (TextUtils.isEmpty(type)) {
                appCacheDir = context.getExternalCacheDir();
            } else {
                appCacheDir = context.getExternalFilesDir(type);
            }

            if (appCacheDir == null) {// 有些手机需要通过自定义目录
                appCacheDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/cache/" + type);
            }

            if (appCacheDir == null) {
                //Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is sdCard unknown exception !");
            } else {
                if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                    //Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is make directory fail !");
                }
            }
        } else {
            //Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is sdCard nonexistence or sdCard mount fail !");
        }
        return appCacheDir;
    }

    /**
     * 获取内存缓存目录
     *
     * @param type 子目录，可以为空，为空直接返回一级目录
     * @return 缓存目录文件夹 或 null（创建目录文件失败）
     * 注：该方法获取的目录是能供当前应用自己使用，外部应用没有读写权限，如 系统相机应用
     */
    public static File getInternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (TextUtils.isEmpty(type)) {
            appCacheDir = context.getCacheDir();// /data/data/app_package_name/cache
        } else {
            appCacheDir = new File(context.getFilesDir(), type);// /data/data/app_package_name/files/type
        }

        if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
            //Log.e("getInternalDirectory","getInternalDirectory fail ,the reason is make directory fail !");
        }
        return appCacheDir;
    }

    /**
     * so库动态加载，和so库插件化不同
     * so库动态加载有两个好处，1. 包体积减小 2. so库代码动态化
     * 将so文件获取之后，拷贝到私有目录，然后再根据加载顺序，通过System.load，将绝对路径的so文件加载进去。
     * 在使用的时候，不需要再调用System.loadLibrary了，so已经加载完成了。
     * Q1：dlopen failed is 32-bit instead of 64-bit
     * 因为手机是64位的，但是宿主App没有加载过任何so库，默认要加载64位的，但是这个so是32位的，所以1让宿主App加载一个空的32位so，2把这个so改成64位。
     * Q2：so有依赖时，要注意加载顺序。
     * @param dexPath apk地址，如果仅仅是so库动态化，就不需要从apk里面减压出so库了
     * @param context 上下文
     * @param resources 资源
     */
    private void loadSoFiles(String dexPath, Context context, Resources resources) {

        List<String> soPaths = new ArrayList<>(); // 指定so文件加载顺序，有些情况下，so文件会有依赖关系。
        try {
            InputStream is = resources.getAssets().open("plugin_so_paths.txt");
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bf.readLine()) != null) {
                soPaths.add(line);
            }
            is.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        if (soPaths.isEmpty()) {
            return;
        }
        File toDir = context.getDir("libs", Context.MODE_PRIVATE);
        List<ZipEntry> foundEntries = new ArrayList<>();
        File apkFile = new File(dexPath);
        ZipFile zipFile = null;
        try {
            zipFile = new SafeZipFile(apkFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (soPaths.contains(entry.getName())) {
                    foundEntries.add(entry);
                }
            }
            if (foundEntries.isEmpty()) {
                throw new RuntimeException("未找到需要加载的so文件：" + soPaths.toString());
            }

            for (ZipEntry entry : foundEntries) {
                InputStream inputStream = null;
                try {
                    inputStream = zipFile.getInputStream(entry);
                    String soPath = entry.getName();
                    String soName = soPath.substring(soPath.lastIndexOf("/") + 1);
                    BufferedOutputStream output = null;
                    try {
                        File outputFile = new File(toDir, soName);
                        if (outputFile.exists()) {
                            outputFile.delete();
                        }
                        output = new BufferedOutputStream(
                                new FileOutputStream(outputFile));
                        BufferedInputStream input = new BufferedInputStream(inputStream);
                        byte b[] = new byte[8192];
                        int n;
                        while ((n = input.read(b, 0, 8192)) >= 0) {
                            output.write(b, 0, n);
                        }
                    } finally {
                        if (output != null) {
                            output.close();
                        }
                    }
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        } catch (Exception e) {

        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException e) {
            }
        }

        File[] currentOrderSoFiles = new File[soPaths.size()];
        for (int i = 0; i < soPaths.size(); i++) {
            String soPath = soPaths.get(i);
            String soName = soPath.substring(soPath.lastIndexOf("/") + 1);
            currentOrderSoFiles[i] = new File(toDir, soName);
        }
        for (int i = 0; i < currentOrderSoFiles.length; i++) {
            File soFile = currentOrderSoFiles[i];
            if (soFile.exists()) {
                try {
                    System.load(soFile.getAbsolutePath());
                    Log.i("PluginLoader", "so完成: " + soFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Deletes a file. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     * (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param file file or directory to delete, must not be {@code null}
     * @throws NullPointerException  if the directory is {@code null}
     * @throws FileNotFoundException if the file was not found
     * @throws IOException           in case deletion is unsuccessful
     */
    public static void forceDelete(final File file) throws IOException {

        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                final String message =
                        "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    /**
     * Deletes a directory recursively.
     *
     * @param directory directory to delete
     * @throws IOException              in case deletion is unsuccessful
     * @throws IllegalArgumentException if {@code directory} does not exist or is not a directory
     */
    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        // 大部分不需要处理Symlink的情况
//        if (!isSymlink(directory)) {
        cleanDirectory(directory);
//        }

        if (!directory.delete()) {
            final String message =
                    "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    /**
     * Cleans a directory without deleting it.
     *
     * @param directory directory to clean
     * @throws IOException              in case cleaning is unsuccessful
     * @throws IllegalArgumentException if {@code directory} does not exist or is not a directory
     */
    public static void cleanDirectory(final File directory) throws IOException {
        final File[] files = verifiedListFiles(directory);

        IOException exception = null;
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    /**
     * Lists files in a directory, asserting that the supplied directory satisfies exists and is a directory
     * @param directory The directory to list
     * @return The files in the directory, never null.
     * @throws IOException if an I/O error occurs
     */
    private static File[] verifiedListFiles(File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }
        return files;
    }

    /**
     * Copies bytes from an {@link InputStream} <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     * The {@code source} stream is closed.
     * See {@link #copyToFile(InputStream, File)} for a method that does not close the input stream.
     *
     * @param source      the <code>InputStream</code> to copy bytes from, must not be {@code null}, will be closed
     * @param destination the non-directory <code>File</code> to write bytes to
     *                    (possibly overwriting), must not be {@code null}
     * @throws IOException if <code>destination</code> is a directory
     * @throws IOException if <code>destination</code> cannot be written
     * @throws IOException if <code>destination</code> needs creating but can't be
     * @throws IOException if an IO error occurs during copying
     */
    public static void copyInputStreamToFile(final InputStream source, final File destination) throws IOException {
        try {
            copyToFile(source, destination);
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Copies bytes from an {@link InputStream} <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     * The {@code source} stream is left open, e.g. for use with {@link java.util.zip.ZipInputStream ZipInputStream}.
     * See {@link #copyInputStreamToFile(InputStream, File)} for a method that closes the input stream.
     *
     * @param source      the <code>InputStream</code> to copy bytes from, must not be {@code null}
     * @param destination the non-directory <code>File</code> to write bytes to
     *                    (possibly overwriting), must not be {@code null}
     * @throws IOException if <code>destination</code> is a directory
     * @throws IOException if <code>destination</code> cannot be written
     * @throws IOException if <code>destination</code> needs creating but can't be
     * @throws IOException if an IO error occurs during copying
     */
    public static void copyToFile(final InputStream source, final File destination) throws IOException {
        final FileOutputStream output = openOutputStream(destination);
        try {
            IOUtils.copy(source, output);
            output.close(); // don't swallow close Exception if copy completes normally
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * The parent directory will be created if it does not exist.
     * The file will be created if it does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be written to.
     * An exception is thrown if the parent directory cannot be created.
     *
     * @param file the file to open for output, must not be {@code null}
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be written to
     * @throws IOException if a parent directory needs creating but that fails
     */
    public static FileOutputStream openOutputStream(final File file) throws IOException {
        return openOutputStream(file, false);
    }

    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * The parent directory will be created if it does not exist.
     * The file will be created if it does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be written to.
     * An exception is thrown if the parent directory cannot be created.
     *
     * @param file   the file to open for output, must not be {@code null}
     * @param append if {@code true}, then bytes will be added to the
     *               end of the file rather than overwriting
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be written to
     * @throws IOException if a parent directory needs creating but that fails
     */
    public static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            final File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }
}
