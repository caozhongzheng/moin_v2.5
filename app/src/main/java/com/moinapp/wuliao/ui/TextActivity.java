package com.moinapp.wuliao.ui;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;

/**
 * Created by moying on 16/3/25.
 */
public class TextActivity extends BaseActivity {

    private int currentSize = TypedValue.COMPLEX_UNIT_SP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_layout);

        LinearLayout root = (LinearLayout) findViewById(R.id.layout_root);
        addText(root);


        EditText editText_sp = (EditText) findViewById(R.id.input_sp);
        Button add_sp = (Button) findViewById(R.id.add_sp);
        add_sp.setOnClickListener(v -> {
            int size = Integer.parseInt(editText_sp.getText().toString());
            if (size < 8 || size > 60) {
                showToast("字体大小请在8~60sp之间", 0, 0);
            } else {
                TextView textView = new TextView(TextActivity.this);
                textView.setText(size + getSize() + ":MOIN成为大片主角123ABCabc_-=+");
                textView.setTextSize(currentSize, size);

                root.addView(textView);
            }
        });

        Button exchg = (Button) findViewById(R.id.exchange);
        exchg.setOnClickListener(v -> {
            if (currentSize == TypedValue.COMPLEX_UNIT_SP) {
                currentSize = TypedValue.COMPLEX_UNIT_DIP;
            } else if (currentSize == TypedValue.COMPLEX_UNIT_DIP) {
                currentSize = TypedValue.COMPLEX_UNIT_PX;
            } else if (currentSize == TypedValue.COMPLEX_UNIT_PX) {
                currentSize = TypedValue.COMPLEX_UNIT_SP;
            }
            addText(root);
        });
    }

    private void addText(LinearLayout root) {

        for (int i = 10; i < 40; i++) {
//            View view = LayoutInflater.from(TextActivity.this).inflate(R.layout.activity_text_item, null);
            TextView textView = new TextView(TextActivity.this);
            textView.setSingleLine(true);
            textView.setText(i + getSize() + ":MOIN成为大片主角123ABCabc_-=+");
            textView.setTextSize(currentSize, i);

            root.addView(textView);
        }
    }

    private String getSize() {
        switch (currentSize) {
            case TypedValue.COMPLEX_UNIT_SP:
                return "sp";
            case TypedValue.COMPLEX_UNIT_PX:
                return "px";
            case TypedValue.COMPLEX_UNIT_DIP:
                return "dp";
        }
        return "sp";
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {

    }
}
