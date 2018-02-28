package com.moinapp.wuliao.ui.imageselect;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.post.PostConstants;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.ImagePreviewActivity;
import com.moinapp.wuliao.util.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import butterknife.InjectView;

/** 发帖时用选择图片,支持多选
 * Created by moying on 15/6/11.
 */
public class SelectMultiPhotoActivity extends BaseActivity implements ListImageDirPopupWindow.OnImageDirSelected {

    private ILogger MyLog = LoggerFactory.getLogger("smp");

    public static final String KEY_SELECTED = "selected";
    public static final String KEY_IS_CHANGED = "ischange";

    @InjectView(R.id.title_layout)
    CommonTitleBar mTitleBar;
    private ProgressDialog mProgressDialog;

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;
    private List<File> mImgFiles;//排序用

    /**
     * 已选择的图片
     */
    private ArrayList<String> mSelectImgs;

    @InjectView(R.id.id_gridView)
    GridView mGirdView;
    private MyMultiImageAdapter mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFolder> mImageFloders = new ArrayList<ImageFolder>();

    @InjectView(R.id.id_bottom_ly)
    RelativeLayout mBottomLy;

    @InjectView(R.id.id_choose_dir)
    TextView mChooseDir;
    @InjectView(R.id.id_total_count)
    TextView mImageCount;
    @InjectView(R.id.id_select_count)
    TextView mSelectCount;
    @InjectView(R.id.id_preview_imgs)
    TextView mPreview;
    int totalCount = 0;
    boolean changed = false;

    private int mScreenHeight;

    private int mPhotoMax = PostConstants.POST_PIC_MAX_SIZE;

    private Uri fileUri;
    private String lastPhotoDir = FileUtil.getInst().getLastPhotoPath();
    private String systemPhotoDir = FileUtil.getInst().getSystemPhotoPath();
    private String systemPhotoDir1 = systemPhotoDir + CAMERA;
    private File cameraFile = new File(systemPhotoDir1);
    private static final String CAMERA = "/Camera";

    private ListImageDirPopupWindow mListImageDirPopupWindow;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_photo_post;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

//        mPhotoMax = getResources().getInteger(R.integer.post_photo_max_count);

        mSelectImgs = getIntent().getStringArrayListExtra(KEY_SELECTED);
        MyLog.i("onCreate mSelectImgs= " + mSelectImgs);
        if(mSelectImgs == null) {
            mSelectImgs = new ArrayList<>();
        }

        if (mSelectImgs.size() > 0 && mSelectImgs.get(mSelectImgs.size() - 1).equals("")) {
            mSelectImgs.remove(mSelectImgs.size() - 1);
        }
    }

    /**
     * 初始化View
     */
    @Override
    public void initView() {
        MyLog.i("initView mSelectImgs= " + mSelectImgs);
        if(mSelectImgs != null && mSelectImgs.size() > 0) {
            mSelectCount.setText(mSelectImgs.size() + "/" + mPhotoMax);
        }
        else {
            mSelectCount.setText("0/" + mPhotoMax);
        }

        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mChooseDir.setOnClickListener(chooseDirListener);
        mImageCount.setOnClickListener(chooseDirListener);
//        mSelectCount.setOnClickListener(selectOkListener);
        mTitleBar.setRightBtnOnclickListener(selectOkListener);
        mPreview.setOnClickListener(view -> {
            if (mSelectImgs != null && mSelectImgs.size() > 0) {
                ImagePreviewActivity.showImagePrivew(SelectMultiPhotoActivity.this, 0, mSelectImgs.toArray(new String[mSelectImgs.size()]));
            }
            else {
                AppContext.showToast(R.string.no_preview);
            }
        });
    }

    @Override
    public void initData() {
        getImages();
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            AppContext.showToast(R.string.label_no_sdcard);
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getContentResolver();

                // 只查询jpeg,webp和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png", "image/webp"},
                        MediaStore.Images.Media.DATE_MODIFIED);

//                Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

//                    Log.e("TAG", path);
                    // 拿到第一张图片的路径
//                    if (firstImage == null)
//                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) {
                        continue;
                    }

                    // 默认第一个选中的目录是相册目录, 因为由于android版本的差异
                    // 暂时无法精确得知系统相册的目录(有的是/DCIM/ 大部分是/DCIM/Camera/)
                    // 这里统一把/DCIM/及子目录/Camera下面的图片都当作/DCIM 目录的来处理
                    // todo 终极方案是能得到系统相册的准确路径
                    if (path.contains(lastPhotoDir)
                            && firstImage == null) {
                        firstImage = path;
                        MyLog.i("system DCIM =" + firstImage);
                        fileUri = Uri.fromFile(new File(firstImage));
//                        mImgDir = new File(firstImage).getParentFile();
                        mImgDir = new File(lastPhotoDir);
                    }

                    // 处理/DCIM目录和/Camera子目录
                    String dirPath = parentFile.getAbsolutePath();
                    if (dirPath.equalsIgnoreCase(systemPhotoDir) || dirPath.equalsIgnoreCase(systemPhotoDir1)) {
                        dirPath = systemPhotoDir;
                    }

                    ImageFolder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFolder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    String[] filelist = listFiles(new File(dirPath));
                    int picSize;
                    if (filelist != null) {
                        picSize = filelist.length;
                        totalCount += picSize;
                    } else {
                        picSize = 0;
                    }

                    //处理/DCIM子目录目录
                    if (dirPath.equalsIgnoreCase(systemPhotoDir)) {
                        String[] cameralist = listFiles(cameraFile);
                        if (cameralist != null) {
                            picSize += cameralist.length;
                        }
                    }

                    imageFloder.setCount(picSize);
                    MyLog.i("扫描完了:" + imageFloder.toString());
                    if (picSize > 0) {
                        mImageFloders.add(imageFloder);
                    }

                    if (picSize > mPicsSize) {
//                        mPicsSize = picSize;
//                        mImgDir = parentFile;
                    }
                }

                // 如果上次选择的文件夹中无文件时,确定一个新的文件夹[优先用系统文件夹]
                if (firstImage == null) {
                    if (mDirPaths.contains(systemPhotoDir)) {
                        for (ImageFolder imageFolder : mImageFloders) {
                            if (systemPhotoDir.equals(imageFolder.getDir())) {
                                fileUri = Uri.fromFile(new File(imageFolder.getFirstImagePath()));
                                mImgDir = new File(systemPhotoDir);
                            }
                        }

                    } else {
                        fileUri = Uri.fromFile(new File(mImageFloders.get(0).getFirstImagePath()));
                        mImgDir = new File(mImageFloders.get(0).getDir());
                    }
                }

                mCursor.close();

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    private String[] listFiles(File dir) {
        if (dir == null || !dir.exists()) return null;
        return dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(".jpg")
                        || filename.endsWith(".jpeg")
                        || filename.endsWith(".png")
                        || filename.endsWith(".webp");
            }
        });
    }

    private File[] getFileList(File dir) {
        if (dir == null || !dir.exists()) {
            MyLog.e("dir == null return null");
            return null;
        }
        return dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                String tmp = filename.toLowerCase();
                return tmp.endsWith(".jpg") || tmp.endsWith(".png")/* || tmp.endsWith(".gif")*/
                        || tmp.endsWith(".jpeg") || tmp.endsWith(".webp");
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x119:
                    AppContext.showToast(String.format(getResources().getString(R.string.select_photo_warning), mPhotoMax));
                    break;

                case 0x110:
                    mProgressDialog.dismiss();
                    // 为View绑定数据
                    data2View();
                    // 初始化展示文件夹的popupWindw
                    initListDirPopupWindw();
                    break;
                default:
                    break;
            }
        }
    };

    private OnClickListener chooseDirListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mListImageDirPopupWindow
                    .setAnimationStyle(R.style.popwin_anim_style);
            mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

            // 设置背景颜色变暗
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = .3f;
            getWindow().setAttributes(lp);
        }
    };

    private OnClickListener selectOkListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            selectOK();
        }
    };

    private void selectOK() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(KEY_SELECTED, MyMultiImageAdapter.getSelectedImage());
        intent.putExtra(KEY_IS_CHANGED, changed);
        setResult(0, intent);
        finish();
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgDir == null) {
            AppContext.showToast(R.string.no_photo);
            return;
        }
        List<File> fileList = null;
        File[] files = getFileList(mImgDir);
        if (files == null) {
            fileList = new ArrayList<>();
        } else {
            fileList = Arrays.asList(files);
        }

        // 处理/DCIM/Camera子目录
        if (mImgDir.getAbsolutePath().equalsIgnoreCase(systemPhotoDir)
                || mImgDir.getAbsolutePath().equalsIgnoreCase(systemPhotoDir)) {
            List<File> cameraFileList = Arrays.asList(getFileList(cameraFile));
            if (cameraFileList != null && cameraFileList.size() > 0) {
                mImgFiles = new ArrayList<File>();
                for (File file : fileList) {
                    mImgFiles.add(file);
                }
                for (File file1 : cameraFileList) {
                    if (!isFileInList(mImgFiles, file1))
                        mImgFiles.add(file1);
                }
            } else {
                mImgFiles = fileList;
            }
        } else {
            mImgFiles = fileList;
        }
        getPathsFromFiles();

        setAdapter();
    }


    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        View popUpView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.imgloader_list_dir, null);
        if (popUpView != null) {
            popUpView.findViewById(R.id.ly_header).setVisibility(View.GONE);
        }
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, popUpView);

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    @Override
    public void selected(ImageFolder floder) {

        mImgDir = new File(floder.getDir());
        // 记住上次选择的相册文件夹
        StickPreference.getInstance().setLastImageFolder(floder.getDir());

        List<File> fileList = null;
        File[] files = getFileList(mImgDir);
        if (files != null) {
            fileList = Arrays.asList(files);
        }

        // 处理/DCIM/Camera子目录
        if (mImgDir.getAbsolutePath().equalsIgnoreCase(systemPhotoDir)
                || mImgDir.getAbsolutePath().equalsIgnoreCase(systemPhotoDir)) {
            files = getFileList(cameraFile);
            if (files != null) {
                List<File> cameraFileList = Arrays.asList(files);
                if (cameraFileList != null && cameraFileList.size() > 0) {
                    mImgFiles = new ArrayList<File>();
                    for (File file : fileList) {
                        mImgFiles.add(file);
                    }
                    for (File file1 : cameraFileList) {
                        if (!isFileInList(mImgFiles, file1))
                            mImgFiles.add(file1);
                    }
                } else {
                    mImgFiles = fileList;
                }
            }
        } else {
            mImgFiles = fileList;
        }
        getPathsFromFiles();

        setAdapter();

        mChooseDir.setText(floder.getName().substring(1));
        mImageCount.setText(floder.getCount() + "张");
        mListImageDirPopupWindow.dismiss();
    }

    private boolean isFileInList(List<File> list, File file) {
        if(list == null || list.size() == 0 || file == null || !file.exists()) {
            return false;
        }
        for (File f : list) {
            if (f == null) continue;
            if (f.getAbsolutePath().equalsIgnoreCase(file.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    private void getPathsFromFiles() {
        Collections.sort(mImgFiles, new FileComparator());
        if (mImgs == null) {
            mImgs = new ArrayList<>();
        } else {
            mImgs.clear();
        }
        for (File photo : mImgFiles) {
            mImgs.add(photo.getAbsolutePath());
        }
    }

    //最后修改的照片在前
    private class FileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() == rhs.lastModified()) {
                return 0;
            }
            if (lhs.lastModified() < rhs.lastModified()) {
                return 1;
            } else {
                return -1;
            }
        }

    }

    private void setAdapter() {
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MyMultiImageAdapter(getApplicationContext(), mImgs,
                R.layout.imgloader_grid_item, mImgDir.getAbsolutePath(), mHandler);
        MyMultiImageAdapter.setSelectedImage(mSelectImgs);
        mAdapter.setTextCallback(new MyMultiImageAdapter.TextCallback() {
            @Override
            public void onListen(int count) {
                mSelectCount.setText(count + "/" + mPhotoMax);
//                mSelectCount.setClickable(true);
                changed = true;
            }
        });
        mGirdView.setAdapter(mAdapter);
        if (mImgs.size() > 0) {
            String s = mImgs.get(0);
            s = s.substring(0, s.lastIndexOf("/"));
            s = s.substring(s.lastIndexOf("/") + 1
            );

            mChooseDir.setText(s);
        }
        mImageCount.setText(mImgs.size() + "张");
    }

    private void clearSelected() {
        if(mSelectImgs.size() > 0) {
            mSelectImgs.clear();
            changed = true;
            setAdapter();
            mSelectCount.setText("0/" + mPhotoMax);
        }
    }

}
