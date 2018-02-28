package com.moinapp.wuliao.modules.mine;

import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsModule;
import com.moinapp.wuliao.commons.moduleframework.IFeature;
import com.moinapp.wuliao.modules.mine.tables.ChatTable;
import com.moinapp.wuliao.modules.mine.tables.PushMessageTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiancheng on 15/6/23.
 */
public class MineModule extends AbsModule {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String MODULE_NAME = "mine";
    // ===========================================================
    // Fields
    // ===========================================================
    private List<IDataTable> mTables;
    private static ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);
    // ===========================================================
    // Constructors
    // ===========================================================
    public MineModule() {
        mTables = new ArrayList<IDataTable>();
        mTables.add(new PushMessageTable());
        mTables.add(new ChatTable());
        MineManager.getInstance().init();
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

