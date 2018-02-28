package com.moinapp.wuliao.commons.init;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.init.model.GetBootImageResult;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.IModule;
import com.moinapp.wuliao.commons.moduleframework.ModuleContainer;
import com.moinapp.wuliao.commons.system.SystemFacadeFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.mine.MineModule;
import com.moinapp.wuliao.modules.stickercamera.app.camera.StickerCameraModule;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.XmlUtils;

import org.apache.http.Header;
import org.kymjs.kjframe.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class InitManager {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final ILogger MyLog = LoggerFactory.getLogger("init");
	
    public static final String TABLE_VERSION_PREFIX = "table_version_";
	// ===========================================================
	// Fields
	// ===========================================================
	private ModuleContainer mModules;
	private InitPreference mPreference;
    private Map<IModule, Boolean> mModuleDefaults;
	private Context mContext;
	private static InitManager mInstance;
	// ===========================================================
	// Constructors
	// ===========================================================

	public InitManager() {
	}

	public static synchronized InitManager getInstance() {
		if (mInstance == null) {
			mInstance = new InitManager();
		}

		return mInstance;
	}

	public InitManager(Context context) {
		mContext = context;

		mPreference = InitPreference.getInstance();
		mModules = ModuleContainer.getInstance();	
		mModuleDefaults = new LinkedHashMap<IModule, Boolean>();
	}

	public static synchronized InitManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new InitManager(context);
		}

		return mInstance;
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
    public void registerModules() {
		// 添加含有TABLE等需要初始化信息的MODULE
		addModule(new MineModule(), true); //必须在所有联网之前初始化
		addModule(new StickerCameraModule(), true); //必须在所有联网之前初始化
        overrideModuleDefaults();
    }

	private void overrideModuleDefaults() {
		Map<String, Boolean> overrides = ClientInfo.overrideModuleDefaults();
        if (overrides != null){
        	for (Entry<String, Boolean> entry : overrides.entrySet()){
        		String moduleName = entry.getKey();
        		IModule module = mModules.getModuleByName(moduleName);
        		boolean enabled = entry.getValue();
        		mModuleDefaults.put(module, enabled);
        	}
        }
	}
    
    private void addModule(IModule module, boolean enabled){
        mModuleDefaults.put(module, enabled);
        mModules.addModule(module);
    }
	
    public void init() {
		initModules();

    }
    
    private void registerTables() {
		List<IDataTable> tables = getAllTables();

		for (IDataTable table : tables) {
			String tableName = table.getName();
			mPreference.setInt(TABLE_VERSION_PREFIX + tableName,
					table.getVersion());
		}
	}

	private List<IDataTable> getAllTables() {
		Collection<IModule> modules = ModuleContainer.getInstance().getModules();
		List<IDataTable> tables = new ArrayList<IDataTable>();
		for (IModule module : modules) {
			List<IDataTable> list = module.getTables();
			if (list != null && !list.isEmpty()){	
				tables.addAll(list);
			}
		}
		return tables;
	}

	private void initModules() {
		{
			for (Entry<IModule, Boolean> entry : mModuleDefaults.entrySet()) {
				IModule module = entry.getKey();
				boolean enabled = entry.getValue();
				initModule(module, enabled);
			}
		}

	}

	private void initModule(IModule module, boolean enabled) {
		try {
			long start = SystemFacadeFactory.getSystem().currentTimeMillis();
			module.setEnabled(enabled);
			long end = SystemFacadeFactory.getSystem().currentTimeMillis();
			MyLog.i("Init: module=" + module.getName() + ", enabled=" + enabled
					+ ", time=" + (end - start));
		} catch (Exception e) {
			MyLog.e(e);
		}
    }


	public void upgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {
		List<IDataTable> tables = getAllTables();
		
		for (IDataTable table : tables) {
			String tableName = table.getName();
			int oldTableVersion = 2;//mPreference.getInt(TABLE_VERSION_PREFIX+tableName);
			table.upgrade(db, oldTableVersion, table.getVersion());
		}
	}

	/**
	 * 开机联网获取开机启动画面的接口
	 * @param imageView
	 * @param listener
	 */
	public void getBootImage(ImageView imageView, IListener listener) {
		long lastUpdatedAt = 0l;
		boolean hasMsg = InitPreference.getInstance().isHasBootImageMsg();
		if(!hasMsg) {
			// 上一次获取到的最新启动图的时间
			lastUpdatedAt = InitPreference.getInstance().getBootImageUpdateAt();
			// 首次或者超过7天的话,检查一次
			if (lastUpdatedAt > (System.currentTimeMillis() - StringUtil.ONE_DAY)) {
//			MyLog.i("lastUpdatedAt: 7days before= " + (System.currentTimeMillis() - 7 * StringUtil.ONE_DAY));
//			MyLog.i("lastUpdatedAt: time return = " + lastUpdatedAt);
				return;
			}
//			MyLog.i("lastUpdatedAt: time = " + lastUpdatedAt);
			InitApi.getBootImage(lastUpdatedAt, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					MyLog.i("onSucceed: response = " + new String(responseBody));
					GetBootImageResult result = XmlUtils.JsontoBean(GetBootImageResult.class, responseBody);
					if (result != null && result.getResult() > 0) {
						//todo:这里获取到数据后请保存到 InitPreference 或另行处理 result.getPicture
						if (result.getBootPicture() != null) {
							try {
								InitPreference.getInstance().setBootImageUpdateAt(result.getBootPicture().getUpdatedAt());
								InitPreference.getInstance().setBootImageUrl(result.getBootPicture().getPicture().getUri());
							} catch (Exception e) {
								MyLog.e(e);
							}
						}

						if (listener != null) {
							listener.onSuccess(result.getBootPicture());
						}

					} else {
						if (listener != null) {
							listener.onErr(null);
						}
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					if (listener != null) {
						listener.onErr(null);
					}
				}
			});
		} else {
			if (listener != null) {
				listener.onSuccess(InitPreference.getInstance().getBootImageUrl());
			}
		}
	}

	/**
	 * 是否有新的启动图
	 */
	public boolean hasNewBootImage() {
		boolean hasMsg = InitPreference.getInstance().isHasBootImageMsg();
		if (hasMsg) {
			return true;
		} else {
			long lastUpdatedAt = InitPreference.getInstance().getBootImageUpdateAt();
			if (lastUpdatedAt < (System.currentTimeMillis() - StringUtil.ONE_DAY)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 下载成功后备份启动图
	 */
	public void backupBootImage() {
		File bootFile = new File(BitmapUtil.getBootImagePath());
		File lastFile = new File(BitmapUtil.getLastBootImagePath());
		if (bootFile != null && bootFile.exists()) {
			FileUtils.copyFile(bootFile, lastFile);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
