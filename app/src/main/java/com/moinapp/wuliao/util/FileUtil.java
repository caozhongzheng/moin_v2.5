package com.moinapp.wuliao.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.stickercamera.app.model.PhotoItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件操作工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class FileUtil {
	private static ILogger MyLog = LoggerFactory.getLogger(FileUtil.class.getSimpleName());

	//--- stickDemo start

	private static String    BASE_PATH;
	private static String    STICKER_BASE_PATH;

	private static FileUtil mInstance;

	public static FileUtil getInst() {
		if (mInstance == null) {
			synchronized (FileUtil.class) {
				if (mInstance == null) {
					mInstance = new FileUtil();
				}
			}
		}
		return mInstance;
	}

	public File getExtFile(String path) {
		return new File(BASE_PATH + path);
	}

	/**
	 * 获取文件扩展名
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1).toLowerCase();
			}
		}
		return "";
	}

	/**
	 * 获取文件夹大小
	 * @param file File实例
	 * @return long 单位为K
	 * @throws Exception
	 */
	public long getFolderSize(File file) {
		try {
			long size = 0;
			if (!file.exists()) {
				return size;
			} else if (!file.isDirectory()) {
				return file.length() / 1024;
			}
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
			return size / 1024;
		} catch (Exception e) {
			return 0;
		}
	}


	public String getBasePath(int packageId) {
		return STICKER_BASE_PATH + packageId + "/";
	}

	private String getImageFilePath(int packageId, String imageUrl) {
		String md5Str = MD5.getMD5(imageUrl).replace("-", "mm");
		return getBasePath(packageId) + md5Str;
	}
	//读取assets文件
	public String readFromAsset(String fileName) {
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = AppContext.getApp().getAssets().open(fileName);
			br = new BufferedReader(new InputStreamReader(is));
			String addonStr = "";
			String line = br.readLine();
			while (line != null) {
				addonStr = addonStr + line;
				line = br.readLine();
			}
			return addonStr;
		} catch (Exception e) {
			return null;
		} finally {
			IOUtil.closeStream(br);
			IOUtil.closeStream(is);
		}
	}

	public void removeAddonFolder(int packageId) {
		String filename = getBasePath(packageId);
		File file = new File(filename);
		if (file.exists()) {
			delete(file);
		}
	}

	public void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	public String getPhotoSavedPath() {
		return BASE_PATH + "stickercamera";
	}

	public String getPhotoTempPath() {
		return BASE_PATH + "MOIN";
	}

	public String getSystemPhotoPath() {
//		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
	}

	public String getLastPhotoPath() {
		String path = StickPreference.getInstance().getLastImageFolder();
		if (StringUtil.isNullOrEmpty(path)) {
			return getSystemPhotoPath();
		} else {
			return path;
		}
	}

	private FileUtil() {
		String sdcardState = Environment.getExternalStorageState();
		//如果没SD卡则放缓存
		if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
			BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/Moin/";
		} else {
			BASE_PATH = AppContext.getApp().getCacheDirPath();
		}

		STICKER_BASE_PATH = BASE_PATH + "/stickers/";
	}

	public boolean createFile(File file) {
		try {
			if (!file.getParentFile().exists()) {
				mkdir(file.getParentFile());
			}
			return file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean mkdir(File file) {
		while (!file.getParentFile().exists()) {
			mkdir(file.getParentFile());
		}
		return file.mkdir();
	}

	public boolean writeSimpleString(File file, String string) {
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			fOut.write(string.getBytes());
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		} finally {
			IOUtil.closeStream(fOut);
		}
	}

	public String readSimpleString(File file) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));

			String line = br.readLine();
			if (StringUtils.isNotEmpty(line)) {
				sb.append(line.trim());
				line = br.readLine();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return "";
		} finally {
			IOUtil.closeStream(br);
		}
		return sb.toString();
	}

	//都是相对路径，一一对应
	public boolean copyAssetDirToFiles(Context context, String dirname) {
		try {
			AssetManager assetManager = context.getAssets();
			String[] children = assetManager.list(dirname);
			for (String child : children) {
				child = dirname + '/' + child;
				String[] grandChildren = assetManager.list(child);
				if (0 == grandChildren.length)
					copyAssetFileToFiles(context, child);
				else
					copyAssetDirToFiles(context, child);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	//都是相对路径，一一对应
	public boolean copyAssetFileToFiles(Context context, String filename) {
		return copyAssetFileToFiles(context, filename, getExtFile("/" + filename));
	}

	public boolean copyAssetFileToFiles(Context context, String filename, File of) {
		InputStream is = null;
		FileOutputStream os = null;
		try {
			is = context.getAssets().open(filename);
			createFile(of);
			os = new FileOutputStream(of);

			int readedBytes;
			byte[] buf = new byte[1024];
			while ((readedBytes = is.read(buf)) > 0) {
				os.write(buf, 0, readedBytes);
			}
			os.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			IOUtil.closeStream(is);
			IOUtil.closeStream(os);
		}
	}

	public boolean renameDir(String oldDir, String newDir) {
		File of = new File(oldDir);
		File nf = new File(newDir);
		return of.exists() && !nf.exists() && of.renameTo(nf);
	}

	/**
	 * 移动文件
	 * @param srcFileName 	源文件完整路径
	 * @param destDirName 	目的目录完整路径
	 * @return 文件移动成功返回true，否则返回false
	 */
	public boolean moveFile(String srcFileName, String destDirName) {

		File srcFile = new File(srcFileName);
		if(!srcFile.exists() || !srcFile.isFile())
			return false;

		File destDir = new File(destDirName);
		if (!destDir.exists())
			destDir.mkdirs();

		return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
	}

	/**
	 * 复制单个文件
	 */
	public void copyFile(String oldPath, String newPath) {
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时
				inStream = new FileInputStream(oldPath); //读入原文件
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		} finally {
			IOUtil.closeStream(inStream);
			IOUtil.closeStream(fs);
		}

	}

	public File getCacheDir() {
		return AppContext.context().getCacheDir();
	}


	/**获取DCIM/Camera  path路径下的图片，最新的排在最前【PhotoItem中有排序】*/
	public ArrayList<PhotoItem> findPicsInDir(String path) {
		ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();
		File dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
			//Camera
			listPhotoItems(dir, photos);
		}
		//再找下path目录下的Camera子目录
		File Camera = new File(path + "/Camera");
		if (Camera.exists() && Camera.isDirectory()) {
			listPhotoItems(Camera, photos);
		}
		Collections.sort(photos);
		return photos;
	}

	private void listPhotoItems(File dir, ArrayList<PhotoItem> photos) {
		if (dir == null || photos == null) {
			return;
		}
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				String filePath = pathname.getAbsolutePath();
				return (filePath.endsWith(".png") || filePath.endsWith(".jpg") || filePath
						.endsWith(".jpeg"));
			}
		});
		if (files != null && files.length > 0) {
			for (File file: files) {
				photos.add(new PhotoItem(file.getAbsolutePath(), file.lastModified()));
			}
		}
	}
	//--- stickDemo end
	/**
	 * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @param context
	 * @param fileName
	 * @param content
	 */
	public static void write(Context context, String fileName, String content) {
		if (content == null)
			content = "";

		try {
			FileOutputStream fos = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			fos.write(content.getBytes());

			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文本文件
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String read(Context context, String fileName) {
		try {
			FileInputStream in = context.openFileInput(fileName);
			return readInStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String readInStream(InputStream inStream) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			outStream.close();
			inStream.close();
			return outStream.toString();
		} catch (IOException e) {
			MyLog.e(e);
		}
		return null;
	}

	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName + fileName);
	}

	/**
	 * 向手机写图片
	 * 
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public static boolean writeFile(byte[] buffer, String folder,
			String fileName) {
		boolean writeSucc = false;

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

		String folderPath = "";
		if (sdCardExist) {
			folderPath = Environment.getExternalStorageDirectory()
					+ File.separator + folder + File.separator;
		} else {
			writeSucc = false;
		}

		File fileDir = new File(folderPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File file = new File(folderPath + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(buffer);
			writeSucc = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return writeSucc;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (StringUtils.isEmpty(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return "";
		}
		int point = filePath.lastIndexOf('.');
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
				point);
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		if (StringUtils.isEmpty(fileName))
			return "";

		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * 获取文件大小
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param size
	 *            字节
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取目录文件大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 获取目录文件个数
	 * 
	 * @param dir
	 * @return
	 */
	public long getFileList(File dir) {
		long count = 0;
		File[] files = dir.listFiles();
		count = files.length;
		for (File file : files) {
			if (file.isDirectory()) {
				count = count + getFileList(file);// 递归
				count--;
			}
		}
		return count;
	}

	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) {
			out.write(ch);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkFileExists(String name) {
		boolean status;
		if (!name.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + name);
			status = newPath.exists();
		} else {
			status = false;
		}
		return status;
	}

	/**
	 * 检查路径是否存在
	 * 
	 * @param path
	 * @return
	 */
	public static boolean checkFilePathExists(String path) {
		return new File(path).exists();
	}

	/**
	 * 计算SD卡的剩余空间
	 * 
	 * @return 返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 新建目录
	 * 
	 * @param directoryName
	 * @return
	 */
	public static boolean createDirectory(String directoryName) {
		boolean status;
		if (!directoryName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);
			status = newPath.mkdir();
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 检查是否安装SD卡
	 * 
	 * @return
	 */
	public static boolean checkSaveLocationExists() {
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status;
		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
			status = true;
		} else
			status = false;
		return status;
	}
	
	/**
	 * 检查是否安装外置的SD卡
	 * 
	 * @return
	 */
	public static boolean checkExternalSDExists() {
		
		Map<String, String> evn = System.getenv();
		return evn.containsKey("SECONDARY_STORAGE");
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteDirectory(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();
		
		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + "/"
								+ listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					MyLog.i("DirectoryManager deleteDirectory:" + fileName);
					status = true;
				} catch (Exception e) {
					e.printStackTrace();
					status = false;
				}

			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile()) {
				try {
					MyLog.i("DirectoryManager deleteFile:" + fileName);
					newPath.delete();
					status = true;
				} catch (SecurityException se) {
					se.printStackTrace();
					status = false;
				}
			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除空目录
	 * 
	 * 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
	 * 
	 * @return
	 */
	public static int deleteBlankPath(String path) {
		File f = new File(path);
		if (!f.canWrite()) {
			return 1;
		}
		if (f.list() != null && f.list().length > 0) {
			return 2;
		}
		if (f.delete()) {
			return 0;
		}
		return 3;
	}

	/**
	 * 重命名
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public static boolean reNamePath(String oldName, String newName) {
		File f = new File(oldName);
		return f.renameTo(new File(newName));
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 */
	public static boolean deleteFileWithPath(String filePath) {
		SecurityManager checker = new SecurityManager();
		File f = new File(filePath);
		checker.checkDelete(filePath);
		if (f.isFile()) {
			MyLog.i("DirectoryManager deleteFileWithPath:" + filePath);
			f.delete();
			return true;
		}
		return false;
	}
	
	/**
	 * 清空一个文件夹
	 * @param filePath
	 */
	public static void clearFileWithPath(String filePath) {
		if(StringUtil.isNullOrEmpty(filePath)) {
			return;
		}
		File folder = new File(filePath);
		if(folder == null || !folder.exists() || !folder.isDirectory()) {
			return;
		}
		File[] files = folder.listFiles();
//				FileUtil.listPathFiles(filePath);
		if (files.length == 0) {
			return;
		}
		for (File f : files) {
			if (f.isDirectory()) {
				clearFileWithPath(f.getAbsolutePath());
			} else {
				f.delete();
			}
		}
	}

	/**
	 * 获取SD卡的根目录
	 * 
	 * @return
	 */
	public static String getSDRoot() {
		
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	/**
	 * 获取手机外置SD卡的根目录
	 * 
	 * @return
	 */
	public static String getExternalSDRoot() {
		
		Map<String, String> evn = System.getenv();
		
		return evn.get("SECONDARY_STORAGE");
	}

	/**
	 * 列出root目录下所有子目录
	 * 
	 * @param root
	 * @return 绝对路径
	 */
	public static List<String> listPath(String root) {
		List<String> allDir = new ArrayList<String>();
		SecurityManager checker = new SecurityManager();
		File path = new File(root);
		checker.checkRead(root);
		// 过滤掉以.开始的文件夹
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				if (f.isDirectory() && !f.getName().startsWith(".")) {
					allDir.add(f.getAbsolutePath());
				}
			}
		}
		return allDir;
	}
	
	/**
	 * 获取一个文件夹下的所有文件
	 * @param root
	 * @return
	 */
	public static List<File> listPathFiles(String root) {
		List<File> allDir = new ArrayList<File>();
		SecurityManager checker = new SecurityManager();
		File path = new File(root);
		checker.checkRead(root);
		File[] files = path.listFiles();
		if(files != null && files.length == 0) {
			for (File f : files) {
				if (f.isFile())
					allDir.add(f);
				else
					listPath(f.getAbsolutePath());
			}
		}
		return allDir;
	}

	public enum PathStatus {
		SUCCESS, EXITS, ERROR
	}

	/**
	 * 创建目录
	 * 
	 * @param newPath
	 */
	public static PathStatus createPath(String newPath) {
		File path = new File(newPath);
		if (path.exists()) {
			return PathStatus.EXITS;
		}
		if (path.mkdir()) {
			return PathStatus.SUCCESS;
		} else {
			return PathStatus.ERROR;
		}
	}
	/**
	 * 创建文件夹
	 *
	 * @param path
	 * @return
	 */
	public static boolean createFolder(String path) {
		if (path == null || TextUtils.isEmpty(path))
			return false;
		File file = new File(path);
		File folder = new File(path.substring(0, path.lastIndexOf("/")));
		if (folder.mkdirs() || folder.isDirectory()) {
			if (!file.exists() && file.isDirectory()) {
				file.mkdir();
				return true;
			}
		}
		return false;

	}
	/**
	 * 删除文件夹
	 *
	 * @param path
	 * @return
	 */
	public static boolean removeFolder(String path) {
		if (path == null || TextUtils.isEmpty(path))
			return false;
		File folder = new File(path);
		if (folder.exists() && folder.isDirectory()) {
			folder.delete();
			return true;
		}
		return false;

	}
	/**
	 * 截取路径名
	 * 
	 * @return
	 */
	public static String getPathName(String absolutePath) {
		int start = absolutePath.lastIndexOf(File.separator) + 1;
		int end = absolutePath.length();
		return absolutePath.substring(start, end);
	}
	
	/**
	 * 获取应用程序缓存文件夹下的指定目录
	 * @param context
	 * @param dir
	 * @return
	 */
	public static String getAppCache(Context context, String dir) {
		String savePath = context.getCacheDir().getAbsolutePath() + "/" + dir + "/";
		File savedir = new File(savePath);
		if (!savedir.exists()) {
			savedir.mkdirs();
		}
		savedir = null;
		return savePath;
	}



	/**
	 * 关闭流
	 * @param closeable
	 */
	public static void closeStream(Closeable closeable){
		if (closeable != null)
			try {
				closeable.close();
			} catch (IOException e) {
				MyLog.e(e);
			}
	}


	/**
	 * 删除目录下的所有文件。
	 *
	 * @param dir 目录
	 * @return boolean
	 */
	public static boolean delAllFilesInFolder(String dir) {
		boolean s = false;
		File delfolder = new File(dir);
		File oldFile[] = delfolder.listFiles();
		try {
			for (int i = 0; i < oldFile.length; i++) {
				if (oldFile[i].isDirectory()) {
					delAllFilesInFolder(dir + File.separator + oldFile[i].getName() + "//"); // 递归清空子文件夹
				}
				oldFile[i].delete();
			}
			s = true;
		} catch (Exception e) {
			MyLog.e("FileUtil.delAllFilesInFolder" + e.toString());
		}
		return s;
	}

	public static void unzip(InputStream is, String dir, IListener listener) {
		try {
			if(unzip(is, dir))
				listener.onSuccess(null);
			else
				listener.onErr(null);
		} catch (IOException e) {
			MyLog.e(e);
			listener.onErr(null);
			e.printStackTrace();
		}
	}

	public static boolean unzip(InputStream is, String dir) throws IOException {
		boolean result = false;
		File dest = new File(dir);
		if (!dest.exists()) {
			dest.mkdirs();
		}

		if (!dest.isDirectory()) {
			MyLog.e("Invalid Unzip destination " + dest);
			throw new IOException("Invalid Unzip destination " + dest);
		}
		if (null == is) {
			MyLog.e("InputStream is null" + dest);
			throw new IOException("InputStream is null");
		}

		ZipInputStream zip = new ZipInputStream(is);

		ZipEntry ze;
		while ((ze = zip.getNextEntry()) != null) {
			final String path = dest.getAbsolutePath()
					+ File.separator + ze.getName();
			String zeName = ze.getName();
			char cTail = zeName.charAt(zeName.length() - 1);
			if (cTail == File.separatorChar) {
				File file = new File(path);
				if (!file.exists()) {
					if (!file.mkdirs()) {
						MyLog.e("Unable to create folder " + file);
						throw new IOException("Unable to create folder " + file);
					}
				}
				continue;
			}

			FileOutputStream fout = new FileOutputStream(path);
			byte[] bytes = new byte[1024];
			int c;
			while ((c = zip.read(bytes)) != -1) {
				fout.write(bytes, 0, c);
			}
			zip.closeEntry();
			fout.close();
			result = true;
		}

		return result;
	}

	/**
	 * 写入内容到SD卡中的文本文件
	 */
	public static void writeFile(String str, String fileName)
	{
		try {
			File f = new File(fileName);
			if (f == null) return;
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write(str);
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}