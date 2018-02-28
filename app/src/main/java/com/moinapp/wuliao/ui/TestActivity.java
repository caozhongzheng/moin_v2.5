package com.moinapp.wuliao.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.DynamicDrawableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.melnykov.fab.ObservableScrollView;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.login.LoginApi;
import com.moinapp.wuliao.modules.login.model.BaseLoginResult;
import com.moinapp.wuliao.modules.mine.MineApi;
import com.moinapp.wuliao.modules.mine.model.GetMyFansResult;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StyledText;
import com.moinapp.wuliao.util.MD5;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.XmlUtils;

import org.apache.http.Header;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import butterknife.OnClick;

/**
 * Created by liujiancheng on 15/9/6.
 */
public class TestActivity extends ActionBarActivity  implements View.OnClickListener,ObservableScrollView.OnScrollChangedListener {
    EditText ed_input, ed_size, ed_color;
    TextView tv_nmodel;
    TextView content;
    ObservableScrollView scrollView;
    Context context;

    private ILogger MyLog = LoggerFactory.getLogger(TestActivity.class.getSimpleName());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake);

        context = TestActivity.this;

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:13683179542"));
        intent.putExtra("sms_body", "老刘你好 " + System.currentTimeMillis());
        startActivity(intent);

        testArrr("http://dev.mo-image.com/cosplay/view?ucid=563b7fc24d5913098b32b6ea&type=2");
        testArrr("http://moinapp//cosplay/view?ucid=563b7fc24d5913098b32b6ea");
        testArrr("http://moinapp//cosplay/view?ucid=563b7fc24d5913098b32b6ea&type=1");
        testArrr("moinapp://cosplay/view?ucid=563b7fc24d5913098b32b6ea&type=1");

        scrollView = (ObservableScrollView) findViewById(R.id.activity_root);
        scrollView.setOnScrollChangedListener(this);
        tv_nmodel = (TextView) findViewById(R.id.tv_nmodel);
        content = (TextView) findViewById(R.id.tv_span);
        ed_input = (EditText) findViewById(R.id.tv_input);
        ed_size = (EditText) findViewById(R.id.tv_size);
        ed_color = (EditText) findViewById(R.id.tv_color);
//        testIndex(" @26  @HAHA  @Java132456  @mei ");
//        testIndex("@26 @HAHA @Java132456 @mei ");
        testHtml();
    }


    private void testHtml() {
        String abc = "<span  style=\"vertical-align: middle; background-color: red; border-radius: 5px;padding: 5px; color:white\">官213方</span>" + "你好,中国,我爱你1234567890林敏骢曾经与谭咏麟、张国荣、梅艳芳、张学友、关淑怡等数十位当红歌手合作。" +
                "亦曾被前亚洲电视总监周梁淑怡重金礼聘过档亚洲电视，与曾志伟主持搞笑节目《开心主流派》，深受观众欢迎；" +
                "他曾与好友曾志伟出版《冇有线电台》系列大碟。此外，他亦曾任香港电台极富争议性的电视节目《头条新闻》的嘉宾主持。";
        content.setText(Html.fromHtml(abc));
    }

    private void testSpann() {
        String abc = "你好,中国,我爱你1234567890林敏骢曾经与谭咏麟、张国荣、梅艳芳、张学友、关淑怡等数十位当红歌手合作。" +
                "亦曾被前亚洲电视总监周梁淑怡重金礼聘过档亚洲电视，与曾志伟主持搞笑节目《开心主流派》，深受观众欢迎；" +
                "他曾与好友曾志伟出版《冇有线电台》系列大碟。此外，他亦曾任香港电台极富争议性的电视节目《头条新闻》的嘉宾主持。";
        SpannableString spanStr = new SpannableString("da"+abc);
        SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
//        ssb.setSpan(new BackgroundColorSpan(Color.parseColor("#f66382")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ssb.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (true) {
            ssb.setSpan(new DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BOTTOM) {
                @Override
                public Drawable getDrawable() {
                    Drawable drawable = getResources().getDrawable(R.drawable.dr_label);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    MyLog.i("dr_label wh=" + drawable.getIntrinsicWidth() + "*" + drawable.getIntrinsicHeight());
                    return drawable;
                }
            }, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        content.setMovementMethod(LinkMovementMethod.getInstance());
        content.setFocusable(true);
        content.setLongClickable(true);
        content.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    protected boolean parseUrl(String url) {
        if (!StringUtil.isNullOrEmpty(url)) {
            Uri uri = Uri.parse(url);

            String resource = null, action = null;
            String[] params = uri.getPath().split("/");
            boolean first = true;
            for (String par : params) {
                if (!StringUtil.isNullOrEmpty(par) && first) {
                    resource = par.toLowerCase();
                    first = false;
                } else if (!first) {
                    action = par.toLowerCase();
                    break;
                }
            }

            MyLog.i("resource=" + resource + ", action=" + action);
            if (StringUtil.isNullOrEmpty(resource) || StringUtil.isNullOrEmpty(action)) {
                return false;
            }

            String[] querys = uri.getQuery().split("&");
            Intent intent = new Intent();
            for (String que : querys) {
                String key = que.substring(0, que.indexOf("="));
                String val = que.substring(que.indexOf("=") + 1);
                MyLog.i("que=" + que + " ,key=" + key + " ,val=" + val);
                intent.putExtra(key, val);
            }
            if (resource.equals("tag")) {
                if (action.equals("view")) {
                    // 查看话题详情
                    if (!StringUtil.isNullOrEmpty(intent.getExtras().getString("tpid"))) {
//                        UIHelper.showTopicDetail(EventsDetailActivity.this, null,
//                                "tp", intent.getExtras().getString("tpid"), 0);
                    }
                }
            }

            return true;
        }

        return false;
    }

    private void testArrr(String url) {
        Uri uri = Uri.parse(url);
        MyLog.i("uri=" + uri);
        String scheme = uri.getScheme();
        MyLog.i("scheme:" + scheme);//znn
        if (uri != null) {
            String host = uri.getHost();
//            String dataString = intent.getDataString();
            String p = uri.getQueryParameter("type");
            String id = uri.getQueryParameter("ucid");
            String path = uri.getPath();
            String path1 = uri.getEncodedPath();
            String queryString = uri.getQuery();
            MyLog.i("host:" + host);//aa.bb
//            MyLog.i("dataString:" + dataString);
            MyLog.i("p:" + p);//12
            MyLog.i("id:" + id);//1
            MyLog.i("path:"+path);// /moin/test
//            MyLog.i("path1:" + path1);// /moin/test
            MyLog.i("queryString:" + queryString);// p=12&id=1

            String resource = null, action = null;
            String[] params = uri.getPath().split("/");
            boolean first = true;
            for (String par : params) {
                MyLog.i("par=" + par);
                if (!StringUtil.isNullOrEmpty(par) && first) {
                    resource = par;
                    first = false;
                } else if (!first) {
                    action = par;
                    break;
                }
            }

            MyLog.i("resource=" + resource);
            MyLog.i("action=" + action);
            String[] querys = uri.getQuery().split("&");
            Intent intent = new Intent();
            for (String que : querys) {
                String key = que.substring(0, que.indexOf("="));
                String val = que.substring(que.indexOf("=") + 1);
                MyLog.i("que=" + que + " ,key="+key + " ,val="+val);
                intent.putExtra(key, val);
            }

            MyLog.i("intent=" + intent.getExtras().toString());

        }

//        for (int i = 0; i < urls.length; i++) {
//            String url = urls[i];
//            MyLog.i("urls["+i+"]=" + urls[i]);
//        }
    }

    /**
     * 制作彩色文字
     */
    private void generateImage(String ts) {
        int x = 5, y = 10;

        long startT = System.currentTimeMillis();
        try {
            String input = ed_input.getText().toString();
            if (StringUtil.isNullOrEmpty(input)) {
                input = "我爱你中国";
            }

            String size = ed_size.getText().toString();
            if (StringUtil.isNullOrEmpty(size)) {
                size = "40";
            }

            String color = ed_color.getText().toString();
            if (!StringUtil.isNullOrEmpty(color)) {
                color = "#" + color;
            } else {
                color = "#60ff20";
            }

            /** 设置这个txt的显示 */
            tv_nmodel.setText(input);
            tv_nmodel.setTextSize(Float.parseFloat(size));
            tv_nmodel.setTextColor(Color.parseColor(color));
            tv_nmodel.setTypeface(Typeface.DEFAULT_BOLD, Typeface.ITALIC);


            /**---------------------------------获取文字的宽高start---------------------------------*/

            TextPaint textPaintnew = tv_nmodel.getPaint();
            int textPaintWidth = (int) textPaintnew.measureText(tv_nmodel.getText().toString());


            Paint.FontMetrics fm = textPaintnew.getFontMetrics();
            int txtHeight = (int) (Math.ceil(fm.bottom - fm.top) * TDevice.getDensity());
            MyLog.i("FontMetrics height=" + txtHeight);
            MyLog.i("getLineHeight height=" + tv_nmodel.getLineHeight());
            MyLog.i("tv_nmodel.getHeight()=" + tv_nmodel.getHeight());

            int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            tv_nmodel.measure(spec, spec);

            // getMeasuredWidth
            int measuredWidth = tv_nmodel.getMeasuredWidth();
            int measuredHeight = tv_nmodel.getMeasuredHeight();
            measuredHeight -= 15;
            MyLog.i("measuredWidth,Height=" + measuredWidth + ", " + measuredHeight);



            ViewTreeObserver obs = tv_nmodel.getViewTreeObserver();
            obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    final int TxtHeight = tv_nmodel.getHeight();
                    MyLog.i("onGlobalLayout height=" + TxtHeight);
                }
            });


            MyLog.i("generate input = " + input
                            + ", tinput=" + tv_nmodel.getText()
                            + ", size=" + size
                            + ", tsize=" + tv_nmodel.getTextSize()
                            + ", color=" + color
                            + ", tcolor=" + tv_nmodel.getCurrentTextColor()
                            + ", width=" + textPaintWidth
                            + ", height=" + txtHeight
            );
            if (txtHeight < 50) {
                txtHeight = 50;
            }
            /**---------------------------------获取文字的宽高end---------------------------------*/

            /**---------------------------------draw start---------------------------------*/

            Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            /**
             * input:要绘制的字符串
             * textPaint(TextPaint 类型)设置了字符串格式及属性的画笔
             * 240为设置画多宽后换行
             * 后面的参数是对齐方式...
             */

            StaticLayout layout = new StaticLayout(tv_nmodel.getText(), textPaintnew, measuredWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            canvas.translate(0,-15);
            layout.draw(canvas);


//            Paint paint = new Paint();
//            paint.setTextSize(Float.parseFloat(size));
//            paint.setTextSize(tv_nmodel.getTextSize());
//            paint.setColor(Color.argb(255, 60, 150, 20));
//            paint.setColor(tv_nmodel.getCurrentTextColor());
//            paint.setColor(Color.parseColor(color));
//            paint.setTypeface(Typeface.DEFAULT_BOLD);
//            canvas.drawText(input, 0, 1.5f * height, paint);
//            String[] ss = new String[]{"我爱你中国"};
//            for (int i = 0; i < ss.length; i++) {
//                y = y + 20;
//            }

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();

            /**---------------------------------draw end---------------------------------*/

            /**---------------------------------save start---------------------------------*/

            String path = Environment.getExternalStorageDirectory() + "/ct/test_" + startT + "_image.png";
            MyLog.i("outpath " + path);
            FileOutputStream os = new FileOutputStream(new File(path));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();

            /**---------------------------------save end---------------------------------*/
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            long endT = System.currentTimeMillis();
            MyLog.i("used " + (endT - startT));
        }
    }

    /** 获取一段文字的长度 */

    private int getLenByPaint(String textString, Paint paint) {
        int textLen = (int) paint.measureText(textString);
        MyLog.i("\n获取一段文字[" + textString + "]的长度 measureText = " + textLen);
        Rect bound = new Rect();
        paint.getTextBounds(textString, 0, textString.length(), bound);
        MyLog.i("获取一段文字[" + textString + "]的长度 getTextBounds = " + (bound.right - bound.left));
        textLen = textLen > (bound.right - bound.left) ? textLen : (bound.right - bound.left);
        MyLog.i("获取一段文字[" + textString + "]的长度 textLen = " + textLen);
        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
        int fontHeight = fmi.descent - fmi.ascent;
        MyLog.i("获取一段文字[" + textString + "]的高度  fontHeight = " + fontHeight);
        return textLen;
    }
    private void testTxtWH(int size) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(3);
        paint.setTextSize(size);
//        paint.setColor(Color.CYAN);
//        canvas.drawRect(targetRect, paint);
        paint.setColor(Color.BLACK);
        String txt1 = "我";
        String txt2 = "我们";
        String txt3 = "一路向";
        String txt4 = "一路向西";

        MyLog.i(size + ", getLenByPaint=[" + getLenByPaint(txt1, paint) + "]");
        MyLog.i(size + ", getLenByPaint=[" + getLenByPaint(txt2, paint) + "]");
        MyLog.i(size + ", getLenByPaint=[" + getLenByPaint(txt3, paint) + "]");
        MyLog.i(size + ", getLenByPaint=[" + getLenByPaint(txt4, paint) + "]");
    }

    private void testIndex(String text) {

            if(StringUtil.isNullOrEmpty(text)) {
                return;
            }
            List<StyledText> sTextsStartList = new ArrayList<>();

        TreeMap<String, String> atUsersMap = new TreeMap<>();

        int sTextLength = -1;
            String temp = text;
            int lengthFront = 0;//记录被找出后前面的字段的长度
            int start = -1;
            int end = -1;
            do
            {
                MyLog.i("temp=[" + temp+"]");
                start = temp.indexOf("@", end);
                end = temp.indexOf(" ", start);
                MyLog.i("start=" + start + ", end=" + end);

                if(start != -1)
                {
//                    start = start + lengthFront;
                    sTextLength = end - start;
                    MyLog.i("start=" + start + ", sTextLength=" + sTextLength);
                    sTextsStartList.add(new StyledText(start, sTextLength, text.substring(start + 1, end)));

                    atUsersMap.put("" + start, text.substring(start+1, end));
//                    lengthFront = start + sTextLength;
//                    MyLog.i("lengthFront=" + lengthFront);
//                temp = text.substring(lengthFront);
                }

            }while(start != -1);

            int i = 0;
            for(StyledText st : sTextsStartList)
            {
                MyLog.i(i+", StyledText=" + st.toString());
//                MyLog.i(i+", StyledText=[" + temp.substring(st.getStart(), st.getEnd()));
//                styledText.setSpan(
//                        new ForegroundColorSpan(Color.parseColor("#FF0000")),
//                        st.getStart(),
//                        st.getEnd(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                i++;
            }

        String del = "mei";
        if(atUsersMap.containsValue(del)) {
            String key = null;
            Iterator iter = atUsersMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
//            Object key = entry.getKey();
//            Object val = entry.getValue();
                if(entry.getValue().equals(del)) {
                    key = (String) entry.getKey();
                }
            }
            if(!StringUtil.isNullOrEmpty(key)) {
                atUsersMap.remove(key);
            }
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        Iterator iter = atUsersMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
//            Object key = entry.getKey();
//            Object val = entry.getValue();
            if(first) {
                sb.append(entry.getValue());
                first = false;
            } else {
                sb.append(",").append(entry.getValue());
            }
        }
//        for(Map.Entry<String, String> entry : atUsersMap.entrySet()) {
//            if(first) {
//                sb.append(entry.getValue());
//                first = false;
//            } else {
//                sb.append(",").append(entry.getValue());
//            }
//        }
        MyLog.i("atUsersMap , StyledText=[" + sb.toString() + "]");

//            return styledText;
    }

    private void testSort() {
        Log.i("sort", "--------原始-----------\n");
        ArrayList<TypeItem> list = new ArrayList<>();
        list.add(new TypeItem(1));
        list.add(new TypeItem(2));
        list.add(new TypeItem(3));
        list.add(new TypeItem(4));
        list.add(new TypeItem(5));
        list.add(new TypeItem(6));

        int end = 0;
        List<TypeItem> tmp = list.subList(0,end);
        Log.i("sort", "---tmp.isnull=" + (null == tmp));
        Log.i("sort", "---tmp.size=" + tmp.size());
        for (int i = 0; i < tmp.size(); i++) {
            Log.i("sort", i+"---"+tmp.get(i).getId());
        }

        Log.i("sort", "---------------------------------");
        end = list.size();
        tmp = list.subList(end, list.size());
        Log.i("sort", "---tmp.isnull="+(null==tmp));
        Log.i("sort", "---tmp.size="+tmp.size());
        for (int i = 0; i < tmp.size(); i++) {
            Log.i("sort", i+"---"+tmp.get(i).getId());
        }
//
//        Collections.sort(list, new Comparator<TypeItem>() {
//            @Override
//            public int compare(TypeItem lhs, TypeItem rhs) {
//                // 排序方式是234561
//                return rhs.getId() - lhs.getId();
//            }
//        });
//        Log.i("sort", "--------排序1-----------\n");
//        for (int i = 0; i < list.size(); i++) {
//            Log.i("sort", i+"---"+list.get(i).getId());
//        }
//
//
//        int end = 0;
//        for (int i = 0; i < list.size(); i++) {
//            if(list.get(i).getId() == 1) {
//                end = i;
//                break;
//            }
//            Log.i("sort", i + "-排序2---" + list.get(i).getId());
//        }
//        List<TypeItem> tmp = list.subList(0,end);
//        Collections.sort(tmp, new Comparator<TypeItem>() {
//            @Override
//            public int compare(TypeItem lhs, TypeItem rhs) {
//                // 排序方式是234561
//                return lhs.getId() - rhs.getId();
//            }
//        });
//
//        ArrayList<TypeItem> list2 = new ArrayList<>();
//        list2.addAll(tmp);
//        list2.addAll(list.subList(end,list.size()));
//        Log.i("sort", "---------排序3-------------end=" + end + "\n");
//        for (int i = 0; i < list2.size(); i++) {
//            Log.i("sort", i+"---"+list2.get(i).getId());
//        }

    }

    int mScrollThreshold = 3;
    int mLastScrollY = 0;

    private int mEmojiFragmentHeight;
    private boolean mEmojiVisible = false;

    long lastTime = 0;
    @Override
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {

        long current = System.currentTimeMillis();
        boolean delta = (current - lastTime) > 100;
//        boolean visible = Math.abs(y - mLastScrollY) < mScrollThreshold;
//        MyLog.i(System.currentTimeMillis()
//                        + " onScrollChanged "
//                      + " delta=" + visible
//                        + ", delta=" + delta
//                        + ", mEmojiVisible=" + mEmojiVisible
//                        + ", y=" + y
//                        + ", oldy=" + oldy
//                        + ", lastY=" + mLastScrollY
//        );
//        if (!visible) {
//            mLastScrollY = y;
//        }

//        if(mEmojiVisible != delta || !mEmojiVisible) {
//            mEmojiVisible = delta;
//            lastTime = current;
//
//            if (mEmojiFragmentHeight == 0) {
//                mEmojiFragmentHeight = getResources().getDimensionPixelSize(R.dimen.pic_comment_fg_height) + 3;
//            }
//
//            MyLog.i("animate  h=" + (mEmojiVisible ? 0 : (float) mEmojiFragmentHeight) + ", oldy=" + oldy);
//        }
//

        if (mEmojiFragmentHeight == 0) {
            mEmojiFragmentHeight = getResources().getDimensionPixelSize(R.dimen.pic_comment_fg_height) + 3;
            MyLog.i("height=" + mEmojiFragmentHeight);
        }
//        MyLog.i("animate  h=" + (mEmojiVisible ? 0 : (float) mEmojiFragmentHeight) + ", oldy=" + oldy);
        if (delta) {
            lastTime = current;
            MyLog.i("animate  h=" + mEmojiFragmentHeight + ", y=" + y + ", oldy=" + oldy);
        }

        if (mTimer != null){
            if (mTimerTask != null){
                mTimerTask.cancel();  //将原任务从队列中移除
            }

            mTimerTask = new MyTimerTask();  // 新建一个任务
            mTimer.schedule(mTimerTask, 100);
        }

//        timer.cancel();
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                MyLog.i("animate  h=000000");
//                timer.cancel();
//            }
//        }, 100);
    }
    Timer timer = null;
    Timer mTimer = new Timer(true);
    private MyTimerTask mTimerTask;
    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            MyLog.i("animate  h=000000");
        }
    }

//    TimerTask task = new TimerTask() {
//        public void run() {
//            MyLog.i("animate  h=000000");
//            timer.cancel();
//        }
//    };


    class TypeItem {
        public int getId() {
            return id;
        }

        private int id;

        public TypeItem(int id) {
            this.id = id;
        }
    }
    @Override
    @OnClick({R.id.button, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button1:
//                getEmoji();
                generateImage("我爱你中国");
                break;
            case R.id.button2:
                sendEmoji();
                break;
            case R.id.button3:
                getContacts();
                break;
            case R.id.button4:
                getSIMContacts();
                break;
            case R.id.button5:
                testTxtWH(29);
                break;
            case R.id.button6:
                testTxtWH(30);
                testTxtWH(45);
                break;
            case R.id.button7:
                testTxtWH(31);
                break;

        }
    }

    /**
     * 得到手机通讯录联系人信息
     * 这个比较全,包含了sim卡的联系人信息*
     */
    private void getContacts() {
        ContentResolver resolver = getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION,
                null, null, null);

        if (phoneCursor != null) {
            int PHONES_NUMBER_INDEX = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            int i = 1;
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);

                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                if (phoneNumber.contains("-")) {
                    phoneNumber = phoneNumber.replaceAll("-", "");
                }
                if (phoneNumber.length() > 11) {
                    phoneNumber = phoneNumber.substring(phoneNumber.length() - 11);
                }
                if (phoneNumber.startsWith("1")) {
                    String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                    MyLog.i(i++ + "-----: " + contactName + ", " + phoneNumber);
                }
            }
            phoneCursor.close();
        }
    }

    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER,
//            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, Phone.CONTACT_ID
    };

    /**得到手机通讯录联系人信息
     * 这个比较全,包含了sim卡的联系人信息**/
    private void getPhoneContacts() {
        ContentResolver resolver = getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            int PHONES_NUMBER_INDEX = phoneCursor.getColumnIndex(Phone.NUMBER);
            int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
//            int PHONES_CONTACT_ID_INDEX = phoneCursor.getColumnIndex(Phone.CONTACT_ID);
//            int PHONES_PHOTO_ID_INDEX = phoneCursor.getColumnIndex(Phone.PHOTO_ID);

            int i = 1;
            MyLog.i("得到手机通讯录联系人信息-----: ");
            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);

                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

//                //得到联系人ID
//                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
//
//                //得到联系人头像ID
//                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
//
//                //得到联系人头像Bitamp
//                Bitmap contactPhoto = null;
//
//                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
//                if(photoid > 0 ) {
//                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
//                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
//                    contactPhoto = BitmapFactory.decodeStream(input);
//                }else {
//                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_favor);
//                }

                MyLog.i(i++ + "-----: " + contactName + ", " + phoneNumber);
            }

            phoneCursor.close();
        }
    }
    /**得到手机SIM卡联系人人信息
     * 这个比较少,只是存储在sim卡内的信息**/
    private void getSIMContacts() {

        try {
            ContentResolver resolver = getContentResolver();
            // 获取Sims卡联系人
            Uri uri = Uri.parse("content://icc/adn");
            Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);

            if (phoneCursor != null) {
                int i = 1;
                MyLog.i("得到手机SIM卡联系人信息-----: ");
                int PHONES_NUMBER_INDEX = phoneCursor.getColumnIndex(Phone.NUMBER);
                int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
                MyLog.i("phoneCursor.getCount-----: " + phoneCursor.getCount());
                String[] columns = phoneCursor.getColumnNames();
                for (int j = 0; j < columns.length; j++) {
                    String column = columns[j];
                    MyLog.i(j+" column--: " + column + ", index= " + phoneCursor.getColumnIndex(column));
                }
//                I/test    ( 3445): 0 column--: name, index= 0 (TestActivity.java:147)
//                I/test    ( 3445): 1 column--: number, index= 1 (TestActivity.java:147)
//                I/test    ( 3445): 2 column--: emails, index= 2 (TestActivity.java:147)
//                I/test    ( 3445): 3 column--: _id, index= 3 (TestActivity.java:147)
//                I/test    ( 3445): 4 column--: anr, index= 4 (TestActivity.java:147)

                while (phoneCursor.moveToNext()) {


                    // 得到手机号码
                    String phoneNumber = phoneCursor.getString(1);
                    // 当手机号码为空的或者为空字段 跳过当前循环
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                    // 得到联系人名称
                    String contactName = phoneCursor.getString(0);

                    //Sim卡中没有联系人头像
                    MyLog.i(i++ + "*****: " + contactName + ", " + phoneNumber);
                }

                phoneCursor.close();
            }
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    class Idols {
        private int result;
        private List<UserInfo> idol;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public List<UserInfo> getIdol() {
            return idol;
        }

        public void setIdol(List<UserInfo> idol) {
            this.idol = idol;
        }
    }

    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//            LoginUserBean loginUserBean = XmlUtils.toBean(LoginUserBean.class, arg2);
            Log.i("ljc","recevied response =" + new String(arg2));
            GetMyFansResult userInfo = XmlUtils.JsontoBean(GetMyFansResult.class, arg2);
            if (userInfo != null) {
                Log.i("ljc", "userinfo =" + userInfo.getFans().get(0).getUsername());
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            AppContext.showToast("网络出错" + arg0);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    };

    private AsyncHttpResponseHandler mHandlerLogin = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            String response = new String(arg2);
            Log.i("ljc", "recevied response =" + response);
            if (!StringUtils.isEmpty(response)) {
                BaseLoginResult result =  new Gson().fromJson(response, BaseLoginResult.class);
                if (result.getResult() == 1) {
                    ClientInfo.setUID(result.getUid());
                    ClientInfo.setPassport(result.getPassport());
                    Log.i("ljc", "set uid =" + result.getUid());
                    Log.i("ljc", "set password =" + result.getPassport());
                }
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            AppContext.showToast("网络出错" + arg0);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }
    };

    //测试登陆
    private void testLogin() {
        LoginApi.login("13683179542", MD5.md5("12345678"), mHandlerLogin);
    }

    //测试注册
    private void testRigister() {
//        LoginApi.registerUser("15601036268", MD5.md5("12345678"), "9905", mHandler);
    }

    //测试电话号码唯一性检查
    private void checkPhone() {
        LoginApi.checkPhone("15601036268", mHandler);
    }

    //得到短信验证码
    private void getSms() {
        LoginApi.getPhoneSms("15601036268", mHandler);
    }

    //通过手机找回密码 ??????????????????????
    private void retrievePasswordByPhone() {
        LoginApi.retrievePasswordByPhone("13683179542", "3956", mHandlerLogin);
    }

    //用户退出登陆
    private void userLogout() {
        LoginApi.userLogout(mHandler);
    }

    //更新密码
    private void updatePassword() {
        MineApi.updatePassword(MD5.md5("12345678"), mHandler);
    }

    //上传头像
    private void uploadAvatar() {
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator +
                "Moin/MOIN/1.jpg";
        File f = new File(path);
        if (f != null && f.exists()) {
            Log.i("ljc","avatar file exists!!");
        }
        MineApi.uploadAvatar(path, mHandler);
    }

    //更新用户信息
    private void updateUserInfo() {
        UserInfo user = new UserInfo();
        user.setUsername("liujiancheng");
        user.setSex("male");
        MineApi.updateUserInfo(user, mHandler);
    }

    //检查用户名的唯一性 ?????????????????
    private void checkUsername() {
        LoginApi.checkUserName("liujiancheng", mHandler);
    }

    //检查用户类型
    private void checkUserType() {
        LoginApi.getUserLoginType(mHandler);
    }

    //用户反馈
    private void userFeedback() {
        LoginApi.userFeedback("吐槽", "zhengwen:fsdfsdfsd", mHandler);
    }

    // 获取用户信息
    private void getUserInfo() {
        MineApi.getUserInfo("555c032d9f8bdff37813cfde", mHandler);
    }

    // 获取我关注的人列表 ????????????????
    private void getMyIdols() {
//        LoginApi.getMyIdols(null,null, mHandler);
    }

    // 获取我的粉丝列表 ?????????????????
//    private void getMyFans() {
//        LoginApi.getMyFans("555c032d9f8bdff37813cfde",null, mHandler);
//    }

    // 关注／取消关注 ?????????????????
    private void FollowUser() {
        MineApi.followUser("555c032d9f8bdff37813cfde", 1, mHandler);
        MineApi.followUser("55ebfcba80ea79fc71492e82", 1, mHandler);
        MineApi.followUser("55ade9be0bdcb6025e2f0f61", 1, mHandler);
        MineApi.followUser("55e5669908204a70286c0f18", 1, mHandler);
    }

    // 获取表情专辑 ?????????????????
    private void getEmoji() {
//        LoginApi.getEmoji("花千骨", "花千骨", "2D", null, mHandler);
    }

    // 发送表情 ?????????????????
    private void sendEmoji() {
//        LoginApi.sendEmoji(1, "555c032d9f8bdff37813cfde", "QQ", mHandler);
    }
}
