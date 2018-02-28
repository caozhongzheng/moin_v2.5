package com.moinapp.wuliao.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.base.SystemBarTintManager;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

/**
 * Created by moying on 15/6/1.
 */
public class UiUtils {
    public static ILogger MyLog = LoggerFactory.getLogger("uiutils");

    private static Point point = null;

    public static Point getDisplayWidthPixels(Context context) {
        if (point != null) {
            return point;
        }
        WindowManager wm = ((Activity)context).getWindowManager();
        point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point;
    }

    public static void setStatusBarTint(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(color);
    }

    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, boolean on) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);

            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setStatusBarTintResource(Color.TRANSPARENT);//通知栏所需颜色
        }
    }

    public static void fixListViewHeight(ListView listView) {
        // 如果没有设置数据适配器，则ListView没有子项，返回。
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        if (listAdapter == null) {
            return;
        }
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listViewItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listViewItem.measure(0, 0);
            // 计算所有子项的高度和
            totalHeight += (DisplayUtil.dip2px(AppContext.context(), listViewItem.getMeasuredHeight()));
            // 不过ListView一定要onMeasure
//            MyLog.i(i+"> totalHeightListview-----" + totalHeight);
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // listView.getDividerHeight()获取子项间分隔符的高度
        // params.height设置ListView完全显示需要的高度
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @TargetApi(16)
    public static void fixGridViewHeight(GridView gridView, int screenWidth) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int n = listAdapter.getCount();
        MyLog.i("n-----" + n);
        for (int i = 0; i < listAdapter.getCount() / 3; i++) {
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
            int h = listItem.getMeasuredHeight();
            int w = listItem.getMeasuredWidth();
            MyLog.i(i+"> listItem.getMeasured: " + w+","+h);
//            totalHeight += h;
//            if(h < 200)
                totalHeight += screenWidth / 3;
            MyLog.i(i+"> totalHeightGridview-----" + totalHeight);
        }
        MyLog.i("> gridView.getHorizontalSpacing()-----" + gridView.getHorizontalSpacing());
        MyLog.i("> gridView.getVerticalSpacing()-----" + gridView.getVerticalSpacing());
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight
                + (gridView.getVerticalSpacing() * (listAdapter.getCount()/3 - 1));
        gridView.setLayoutParams(params);

    }
}
