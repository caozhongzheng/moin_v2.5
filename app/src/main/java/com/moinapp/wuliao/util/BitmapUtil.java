package com.moinapp.wuliao.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.keyboard.utils.Utils;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 图形操作类
 * 
 * @author Administrator
 * 
 */
public class BitmapUtil {

	private static ILogger MyLog = LoggerFactory.getLogger("BitmapUtil");

	public static String IMAGELOAD_CACHE = "MOIN/imagecache";

	public static String IMAGE_CACHE = "MOIN/.Cache/";
	public static String BITMAP_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + IMAGE_CACHE;

	public static String CAMERA_FOLDER = "MOIN/Camera/";
	public static String BITMAP_CAMERA = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CAMERA_FOLDER;

	public static String IMAGE_FOLDER = "MOIN/MOIN/";
	public static String BITMAP_DOWNLOAD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + IMAGE_FOLDER;

	public static String EMOJI_FOLDER = "MOIN/Emoji/";
	public static String BITMAP_EMOJI = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + EMOJI_FOLDER;

	public static String STICK_FOLDER = Utils.STICK_FOLDER;
	public static String BITMAP_STICKRES = Utils.BITMAP_STICKRES;

	public static String AUDIO_FOLDER = "MOIN/audio/";
	public static String BITMAP_AUDIO = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AUDIO_FOLDER;

	public static String STICK_PACKAGE_FOLDER = BITMAP_STICKRES + "stickerPackage/";

	public static ImageLoaderConfiguration options;

	static {
		if (AppTools.existsSDCARD()) {
			FileUtil.createFolder(BITMAP_CACHE);
			FileUtil.createFolder(BITMAP_CAMERA);
			FileUtil.createFolder(BITMAP_DOWNLOAD);
			FileUtil.createFolder(BITMAP_EMOJI);
			FileUtil.createFolder(BITMAP_STICKRES);
		}
		options = new ImageLoaderConfiguration.Builder(AppContext.context())
//				.writeDebugLogs()
				.build();
	}
	public static ImageLoaderConfiguration getImageLoaderConfiguration() {
		return options;
	}

	public static DisplayImageOptions getImageLoaderOptionWithDefaultIcon() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.default_img) //设置图片在下载期间显示的图片
		.showImageForEmptyUri(R.drawable.default_icon)
		.cacheInMemory(true)// 是否緩存都內存中
        .cacheOnDisk(true)// 是否緩存到sd卡上
		.build();//设置图片Uri为空或是错误的时候显示的图片
		return options;
	}
	
	public static DisplayImageOptions getImageLoaderOption() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)// 是否緩存都內存中
        .cacheOnDisk(true)// 是否緩存到sd卡上
		.build();//设置图片Uri为空或是错误的时候显示的图片  
		return getImageLoaderOptionWithDefaultIcon();
	}

	/**
	 * Bitmap圆角转化
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap, int roundPx) {

		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.BLACK);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			canvas.drawBitmap(bitmap, src, rect, paint);
			return output;
		} catch (Exception e) {
			return bitmap;
		}
	}

	private Bitmap decodeUriAsBitmap(Context context, Uri uri) {
		if(uri == null) {
			return null;
		}
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	/**
	 * 通过Url获取Bitmap:还不能用，因为HttpUtil.httpGetInputStream方法被删除，用HttpUtil.download
	 * 
	 * @param context
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static Bitmap getBitmapByUrl(Context context, String url) throws Exception {
		Bitmap bitmap = null;
		InputStream inputstream = null;
		try {
//			inputstream = HttpUtil.httpGetInputStream(context, url);
			bitmap = BitmapFactory.decodeStream(inputstream);
		}catch(Exception e){
			
		}

		try {
			if (null != inputstream) {
				inputstream.close();
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 通过绝对地址获取Bitmap
	 * use ImageLoader.getInstance().decodeSampledBitmapFromResource();
	 * 
	 * @param filePath
	 * @return
	 */

	public static String saveBitmapToSDCardString(Context context, Bitmap bitmap, String filePath, int quality) throws IOException {
		if (bitmap != null) {
			String md5_path = MD5.MD5Encode(filePath.getBytes()) + ".jpg";
			File file = new File(BITMAP_DOWNLOAD);
			if (!file.exists())
				file.mkdirs();
			FileOutputStream fos = new FileOutputStream(BITMAP_DOWNLOAD + md5_path);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
			byte[] bytes = stream.toByteArray();
			fos.write(bytes);
			fos.close();

			return BITMAP_DOWNLOAD + md5_path;
		} else {
			return "";
		}

	}

	public static boolean saveUserAvatar(Context context, Bitmap bitmap) {
		boolean result = false;
		if (bitmap != null) {
			if (!AppTools.existsSDCARD()) {
				MyLog.v("SD card is not avaiable/writeable right now.");
				return false;
			}
			FileOutputStream b = null;
			String fileName = BitmapUtil.getAvatarImagePath();
			try {
				b = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
				result = true;
			} catch (FileNotFoundException e) {
				MyLog.e(e);
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					MyLog.e(e);
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidht = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

			//todo, 暂时不回收,以后如果频繁要缩放的话需要再处理回收
			bitmap.recycle();
		}
		return newbmp;
	}

	/**
	 * 放大缩小图片
	 *
	 * @param bitmap
	 * @param scale
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, float scale) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			try {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
				bitmap.recycle();
			} catch (Exception e) {
				MyLog.e(e);
			}
		}
		return newbmp;
	}

	/**
	 * Drawable转Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {

		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	// Drawable转换成InputStream
	public static InputStream Drawable2InputStream(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return Bitmap2InputStream(bitmap);
	}

	/**
	 * Bitma转תDrawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	// 将InputStream转换成Bitmap
	public Bitmap InputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	// 将Bitmap转换成InputStream
	public static InputStream Bitmap2InputStream(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	// Drawable转换成Bitmap
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	// Bitmap转换成Drawable
	public Drawable bitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		Drawable d = (Drawable) bd;
		return d;
	}
	/**
	 * Bitma转InputStream
	 *
	 * @param bitmap
	 * @return
	 */
	public static InputStream bitmapToInputStream(Bitmap bitmap) {
		return bitmapToInputStream(bitmap, Bitmap.CompressFormat.JPEG, 100);
	}
	public static InputStream bitmapToInputStream(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(format, quality, baos);
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
		return sbs;
	}

	public static Uri getAvatarImageUri() {
		Uri uri = Uri.parse("file://" + getAvatarImagePath());
		return uri;
	}

	public static String getAvatarImagePath() {
		String path = BITMAP_CACHE + ClientInfo.getUID() + "_avatar.jpg";
		return path;
	}

	public static Uri getAvatarCropUri() {
		Uri uri = Uri.parse("file://" + getAvatarCropPath());
		return uri;
	}

	public static Uri getCosplayCropUri() {
		Uri uri = Uri.parse("file://" + getCosplayCropPath());
		return uri;
	}

	public static String getAvatarCropPath() {
		return BitmapUtil.BITMAP_CAMERA + ClientInfo.getUID() + "_avatar.jpg";
	}

	public static String getTmpAvatarCameraPath() {
		return BitmapUtil.BITMAP_CAMERA + ClientInfo.getUID() + "_avatar_tmp.jpg";
	}

	public static String getTmpWatermarkImagePath() {
		return BitmapUtil.BITMAP_CACHE + "watermark_tmp.jpg";
	}
	public static String getTmpShareImagePath() {
		return BitmapUtil.BITMAP_CACHE + "share_tmp.jpg";
	}

	public static String getTmpShareAvatarImagePath() {
		return BitmapUtil.BITMAP_CACHE + "share_avatar_tmp.jpg";
	}

	public static String getBootImagePath() {
		return BITMAP_CACHE + "boot.png";
	}

	public static String getLastBootImagePath() {
		return BITMAP_CACHE + "boot_last.png";
	}

	/**addby wufan 得到大咖秀临时图片**/
	public static String getCosplayImagePath() {
		String path = BITMAP_CAMERA + ClientInfo.getUID() + "_cosplayBackGround.jpg";
		return path;
	}

	public static String getCosplayCropPath() {
		return BitmapUtil.BITMAP_CAMERA + ClientInfo.getUID() + "_cosplay_crop.jpg";
	}

	public static String POST_CAPTURE_PREFIX = "Moin.Post.";
	public static String getPostCapturePath() {
		String str = null;
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssS");
		date = new Date();
		str = format.format(date);
		String fileName = BitmapUtil.BITMAP_CAMERA + POST_CAPTURE_PREFIX + str + Constants.JPG_EXTENSION;
		return fileName;
	}

	public static String REPLY_CAPTURE_PREFIX = "Moin.Reply.";
	public static String getReplyCapturePath() {
		String str = null;
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssS");
		date = new Date();
		str = format.format(date);
		String fileName = BitmapUtil.BITMAP_CAMERA + REPLY_CAPTURE_PREFIX + str + Constants.JPG_EXTENSION;
		return fileName;
	}

	//========================以下为bitmap的操作===============================
	/**
	 * 从exif信息获取图片旋转角度
	 *
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 对图片进行压缩选择处理
	 *
	 * @param picPath
	 * @return
	 */
	public static Bitmap compressRotateBitmap(String picPath) {
		Bitmap bitmap = null;
		int degree = readPictureDegree(picPath);
		if (degree == 90) {
			bitmap = featBitmapToSuitable(picPath, 500, 1.8f);
			bitmap = rotate(bitmap, 90);
		} else {
			bitmap = featBitmapToSuitable(picPath, 500, 1.8f);
		}
		return bitmap;
	}

	/**
	 * 转换bitmap为字节数组
	 *
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bitmap) {
		final int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		byte[] image = out.toByteArray();
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;

	}

	/**
	 * 获取合适尺寸的图片 图片的长或高中较大的值要 < suitableSize*factor
	 *
	 * @param path
	 * @param suitableSize
	 * @return
	 */
	public static Bitmap featBitmapToSuitable(String path, int suitableSize,
											  float factor) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			options.inPreferredConfig = Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int bitmap_w = options.outWidth;
			int bitmap_h = options.outHeight;
			int max_edge = bitmap_w > bitmap_h ? bitmap_w : bitmap_h;
			while (max_edge / (float) suitableSize > factor) {
				options.inSampleSize <<= 1;
				max_edge >>= 1;
			}
			return BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
		}
		return bitmap;
	}

	public static Bitmap featBitmap(String path, int width) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			options.inPreferredConfig = Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int bitmap_w = options.outWidth;
			while (bitmap_w / (float) width > 2) {
				options.inSampleSize <<= 1;
				bitmap_w >>= 1;
			}
			return BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
		}
		return bitmap;
	}

	public static Bitmap loadBitmap(String path, int maxSideLen) {
		if (null == path) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;
		options.inSampleSize = Math.max(options.outWidth / maxSideLen, options.outHeight / maxSideLen);
		if (options.inSampleSize < 1) {
			options.inSampleSize = 1;
		}
		options.inPreferredConfig = Config.RGB_565;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			if (bitmap != bitmap) {
				bitmap.recycle();
			}
			return bitmap;
		} catch (OutOfMemoryError e) {
			MyLog.e(e);
		}
		return null;
	}

	public static Bitmap loadBitmap(String path) {
		if (null == path) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		//不对图进行压缩
		options.inSampleSize = 1;
		options.inPreferredConfig = Config.ARGB_8888;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			return bitmap;
		} catch (OutOfMemoryError e) {
			MyLog.e(e);
		}
		return null;
	}

	public static Bitmap loadFromAssets(Activity activity, String name, int sampleSize,Config config) {
		AssetManager asm = activity.getAssets();
		try {
			InputStream is = asm.open(name);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = sampleSize;
			options.inPreferredConfig = config;
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				is.close();
				return bitmap;
			} catch (OutOfMemoryError e) {
				MyLog.e(e);
			}
		} catch (IOException e) {
			MyLog.e(e);
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap decodeByteArrayUnthrow(byte[] data, BitmapFactory.Options opts) {
		try {
			return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} catch (Throwable e) {
			MyLog.e(e);
		}

		return null;
	}

	public static Bitmap rotateAndScale(Bitmap b, int degrees, float maxSideLen) {

		return rotateAndScale(b, degrees, maxSideLen, true);
	}

	// Rotates the bitmap by the specified degree.
	// If a new bitmap is created, the original bitmap is recycled.
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (null != b2 && b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}

	public static Bitmap rotateNotRecycleOriginal(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees);
			try {
				return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}

	public static Bitmap rotateAndScale(Bitmap b, int degrees, float maxSideLen, boolean recycle) {
		if (null == b || degrees == 0 && b.getWidth() <= maxSideLen + 10 && b.getHeight() <= maxSideLen + 10) {
			return b;
		}

		Matrix m = new Matrix();
		if (degrees != 0) {
			m.setRotate(degrees);
		}

		float scale = Math.min(maxSideLen / b.getWidth(), maxSideLen / b.getHeight());
		if (scale < 1) {
			m.postScale(scale, scale);
		}

		try {
			Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
			if (null != b2 && b != b2) {
				if (recycle) {
					b.recycle();
				}
				b = b2;
			}
		} catch (OutOfMemoryError e) {
		}

		return b;
	}

	public static boolean saveBitmap2file(Bitmap bmp, File file, Bitmap.CompressFormat format, int quality) {
		if (file.isFile())
			file.delete();
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			MyLog.e(e);
			return false;
		}

		return bmp.compress(format, quality, stream);
	}

	public static boolean saveBitmap2file(Bitmap bmp, String filename, Bitmap.CompressFormat format, int quality) {
		File file = new File(filename);
		if (file == null) {
			return false;
		}
		if (file.exists())
			file.delete();
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			MyLog.e(e);
			return false;
		}

		return bmp.compress(format, quality, stream);
	}


	/**压缩图片
	 * @param max_quality: 初始的压缩质量，最大是100
	 * @param accept: 可接受的最大大小（单位KB）, accept<=0 表示不压缩
	 */
	public static Bitmap compressImage(Bitmap image, int max_quality, int accept) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, max_quality, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = max_quality;
		if(accept > 0) {
			MyLog.i(baos.toByteArray().length / 1024 + " KB 开始压缩图片 目标是（" + accept + " KB）");
			while (baos.toByteArray().length / 1024 > accept) {    //循环判断如果压缩后图片是否大于32kb,大于继续压缩
				MyLog.i(baos.toByteArray().length / 1024 + " KB 压缩图片还达不到要求（" + accept + " KB）");
				baos.reset();//重置baos即清空baos
				options -= 10;//每次都减少10
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			}
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**给图片加上合成的水印
	 *
	 */
	public static Bitmap setWaterMark(Bitmap baseboard, Bitmap src, Bitmap watermark, Bitmap author, String name, int w, int h, int aw, int boardWidth) {
		Bitmap newb;

//		MyLog.i("底图 " + baseboard.getWidth() + "*" + baseboard.getHeight());
//		MyLog.i("src " + src.getWidth() + "*" + src.getHeight());
//		MyLog.i("作者 " + author.getWidth() + "*" + author.getHeight());
//		MyLog.i("wh= " + w + "*" + h + ", aw=" + aw + ", boardWidth=" + boardWidth);
		//create the new blank bitmap
		newb = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );//创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas( newb );

		cv.save();
		cv.drawColor(Color.WHITE);
		//draw baseboard into
//		cv.drawBitmap(baseboard, 0, 0, null );//在 0，0坐标开始画入baseboard

		//draw src into
		cv.drawBitmap(src, 0, 0, null);//在 0，0坐标开始画入src

		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		int cx = boardWidth * 5 + aw / 2;
		//draw author bg into
		cv.drawCircle(cx, w, aw / 2 + boardWidth, paint);

		cv.restore();

		//draw watermark into
		if (watermark != null) {
			int ww = watermark.getWidth();
			int wh = watermark.getHeight();
			cv.drawBitmap(watermark, w - ww - 36, w - wh - 36, null);
		}

		//draw author into
		Bitmap circleAvatar = getCircleAvatar(author, aw);
		cv.drawBitmap(circleAvatar, boardWidth * 5, w - aw / 2, null);

		//draw name into
		paint.setColor(Color.parseColor("#333333"));
		paint.setStrokeWidth(3);
		paint.setTextSize(DisplayUtil.sp2px(BaseApplication.context(), 14));
		int textLen = 0;
		Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
		int baseline = w + aw / 2 + (int) ((fmi.descent - fmi.ascent) * 2f);
		int start = 0;
		if(!TextUtils.isEmpty(name)) {
			textLen = (int) paint.measureText(name);
			start = cx - textLen / 2;
			start = start < 30 ? 30 : start;
			cv.drawText(name, start, baseline, paint);
		}
		paint.setColor(Color.parseColor("#777777"));
		paint.setTextSize(DisplayUtil.sp2px(BaseApplication.context(), 14));
		cv.drawText("向你分享了这张图片", start + textLen + DisplayUtil.dip2px(BaseApplication.context(), 3), baseline, paint);

		int logTextLen = (int) paint.measureText("from MOIN");
		paint.setTextSize(DisplayUtil.sp2px(BaseApplication.context(), 12));
		paint.setColor(Color.parseColor("#999999"));
		cv.drawText("from MOIN", w - logTextLen, baseline - 2, paint);

		//save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);//保存

		//store
		cv.restore();//存储
		return newb;
	}

	/**保存图片添加水印*/
	public static Bitmap setWaterMark(Context context, Bitmap src, Bitmap watermark) {
		int sw = DisplayUtil.getDisplayWidth(context);
		Bitmap newb = Bitmap.createBitmap( sw, sw, Bitmap.Config.ARGB_8888 );
		Canvas cv = new Canvas( newb );

		cv.save();

		int watermark_w = context.getResources().getDimensionPixelOffset(R.dimen.moin_watermark_width);
		int watermark_h = context.getResources().getDimensionPixelOffset(R.dimen.moin_watermark_height);
		int watermark_m = context.getResources().getDimensionPixelOffset(R.dimen.moin_watermark_margin);
		int left = sw - watermark_w - watermark_m;
		int top = sw - watermark_h - watermark_m;
		cv.drawBitmap(src, 0, 0, null);
		cv.drawBitmap(watermark, left, top, null);

		cv.save(Canvas.ALL_SAVE_FLAG);//保存
		cv.restore();//存储

		return newb;
	}

	private static Bitmap getCircleAvatar(Bitmap author, int w) {

		BitmapShader mBitmapShader = new BitmapShader(author, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		Paint mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);

		Bitmap resultBitmap = Bitmap.createBitmap( w, w, Bitmap.Config.ARGB_8888 );//创建一个新的和SRC长度宽度一样的位图
		Canvas canvas = new Canvas( resultBitmap );

		canvas.drawCircle(w / 2, w / 2, w / 2, mBitmapPaint);

		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return resultBitmap;
	}

	private static Bitmap getCircleAvatar2(Bitmap author, int w) {
		Bitmap resultBitmap = Bitmap.createBitmap( w, w, Bitmap.Config.ARGB_8888 );//创建一个新的和SRC长度宽度一样的位图
		Canvas canvas = new Canvas( resultBitmap );

		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawCircle(w / 2, w / 2, w / 2, paint);
		canvas.drawBitmap(author, 0, 0, paint);

		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return resultBitmap;
	}

	public static String getPostCompressPath(int i) {
		return BITMAP_CAMERA + POST_CAPTURE_PREFIX + "compress_" + i + Constants.COMPRESS_EXTENSION;
	}
	public static String getReplyCompressPath(int i) {
		return BITMAP_CAMERA + REPLY_CAPTURE_PREFIX + "compress_" + i + Constants.COMPRESS_EXTENSION;
	}

	private static final String NET_COSPLAY_FILE = "/original_net_decodecache_";
	public static String getOriginalCosplayFile(String url) {
		String path = FileUtil.getInst().getCacheDir() + NET_COSPLAY_FILE + url;
		return path;
	}

	public static BitmapFactory.Options calculateInSampleSize(BitmapFactory.Options options, int
			reqWidth, int reqHeight) {
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			int heightRatio = Math.round((float) height / (float) reqHeight);
			int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		return options;
	}

	public static Bitmap imageZoom(Bitmap bitmap, double maxSize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
		byte[] b = baos.toByteArray();
		double mid = (double) (b.length / 1024);
		double i = mid / maxSize;
		if (i > 1.0D) {
			bitmap = scaleWithWH(bitmap, (double) bitmap.getWidth() / Math.sqrt(i), (double)
					bitmap.getHeight() / Math.sqrt(i));
		}

		return bitmap;
	}

	public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
		if (w != 0.0D && h != 0.0D && src != null) {
			int width = src.getWidth();
			int height = src.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidth = (float) (w / (double) width);
			float scaleHeight = (float) (h / (double) height);
			matrix.postScale(scaleWidth, scaleHeight);
			return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
		} else {
			return src;
		}
	}

	public static Bitmap scaleWithMatrix(Bitmap src, Matrix scaleMatrix) {
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), scaleMatrix, true);
	}

	public static Bitmap scaleWithXY(Bitmap src, float scaleX, float scaleY) {
		Matrix matrix = new Matrix();
		matrix.postScale(scaleX, scaleY);
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
	}

	public static Bitmap scaleWithXY(Bitmap src, float scaleXY) {
		return scaleWithXY(src, scaleXY, scaleXY);
	}

	public static Bitmap rotate(int angle, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate((float) angle);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
				true);
	}

	public static void doRecycledIfNot(Bitmap bitmap) {
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}

	}
}
