package com.moinapp.wuliao.modules.stickercamera.app.camera;

import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsModule;
import com.moinapp.wuliao.commons.moduleframework.IFeature;
import com.moinapp.wuliao.modules.stickercamera.tables.UnUploadCosplayTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiancheng on 15/6/23.
 */
public class StickerCameraModule extends AbsModule {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String MODULE_NAME = "sticker_camera";
    // ===========================================================
    // Fields
    // ===========================================================
    private List<IDataTable> mTables;
    private static ILogger MyLog = LoggerFactory.getLogger(StickerCameraModule.MODULE_NAME);
    // ===========================================================
    // Constructors
    // ===========================================================
    public StickerCameraModule() {
        mTables = new ArrayList<IDataTable>();
        mTables.add(new UnUploadCosplayTable());
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public List<IFeature> getFeatures() {
        return null;
    }

    @Override
    public List<IDataTable> getTables() {
        return mTables;
    }

    @Override
    protected void onEnabled(boolean enabled) {
    }

    public void init() {
    }

    @Override
    public boolean canEnabled() {
        return true;
    }
    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}

