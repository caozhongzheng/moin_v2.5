package com.moinapp.wuliao.modules.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.events.EventsManager;
import com.moinapp.wuliao.modules.mission.MissionPreference;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.FolderInfo;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.TextActivity;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.MyAudioPlayer;
import com.moinapp.wuliao.widget.togglebutton.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DebugFragment extends BaseFragment {
	@InjectView(R.id.title_bar)
	CommonTitleBar commonTitleBar;

	@InjectView(R.id.button_textsize)
	Button mTextSize;

	@InjectView(R.id.info)
	TextView mInfo;

	@InjectView(R.id.test_switch)
	ToggleButton mToggleButton;

	@InjectView(R.id.iv_env)
	TextView mTvEnv;

	@InjectView(R.id.listview)
	ListView mListView;

	@InjectView(R.id.test)
	Button mTest;

	private ArrayList<Messages> mMessageList = new ArrayList<Messages>();
	private MessageAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_debug, container,
				false);
		ButterKnife.inject(this, view);
		initView(view);
		initData();
		return view;
	}
	
	@Override
	public void initView(View view) {
		commonTitleBar.setTitleTxt(getString(R.string.debug_title));
		commonTitleBar.setLeftBtnOnclickListener(v -> {
			getActivity().finish();
		});

		StringBuffer info = new StringBuffer();
		info.append("BuildNo:").append(AppConfig.getBuildNo()).append("\n");
		info.append("channel:").append(ClientInfo.getChannelId()).append("\n");
		info.append("VersionName:").append(AppTools.getVersionName(BaseApplication.context())).append("\n");
		info.append("VersionCode:").append(AppTools.getVersionCode(BaseApplication.context())).append("\n");
		info.append("baidu push id:").append(ClientInfo.getPushChannel()).append("\n");
		info.append("uid:").append(ClientInfo.getUID()).append("\n");
		info.append("passport:").append(ClientInfo.getPassport()).append("\n");

		mInfo.setText(info.toString());

		setToggleChanged(mToggleButton, AppConfig.KEY_TEST_ENV_SWICH);
		setToggle(mToggleButton, AppContext.get(AppConfig.KEY_TEST_ENV_SWICH, AppConfig.isDebug()));

		mMessageList = (ArrayList<Messages>)MineManager.getInstance().getMessagesFromDatabase();
		mAdapter = new MessageAdapter();
		mAdapter.setData(mMessageList);
		mListView.setAdapter(mAdapter);

		mTextSize.setOnClickListener(v -> {
			startActivity(new Intent(getActivity(), TextActivity.class));
		});

		mTest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//清除引导图第一次弹出的标记
				MissionPreference.getInstance().setShareGuide(0);
				MissionPreference.getInstance().setCosplayuide(0);
				MissionPreference.getInstance().setFirstCommentGuide(true);
				MissionPreference.getInstance().setFirstLikeGuide(true);
				AppContext.toast(getActivity(), "清除标记成功");
			}
		});
	}

	private void getPostDetail(String postid) {
		DiscoveryManager.getInstance().getCosplay(postid, 0, new IListener() {
					@Override
					public void onSuccess(Object obj) {

					}

					@Override
					public void onErr(Object obj) {

					}

					@Override
					public void onNoNetwork() {

					}
				});
	}

	/**设置开关状态*/
	private void setToggle(ToggleButton tb, boolean value) {
		if (value) {
			tb.setToggleOn();
			mTvEnv.setText("当前环境为测试环境");
		} else {
			tb.setToggleOff();
			mTvEnv.setText("当前环境为正式环境");
		}
	}

	/**设置监听*/
	private void setToggleChanged(ToggleButton tb, final String key) {
		tb.setOnToggleChanged(new ToggleButton.OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				AppContext.set(key, on);
				if (on)
					mTvEnv.setText("当前环境为测试环境");
				else
					mTvEnv.setText("当前环境为正式环境");

				AppContext.toast(getActivity(), "你已经切换环境,请退出重新启动应用");
			}
		});
	}

	private class MessageAdapter extends ListBaseAdapter<Messages> {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView,
							final ViewGroup parent) {
			ViewHolder vh = null;
			if (convertView == null || convertView.getTag() == null) {
				convertView = getLayoutInflater(parent.getContext()).inflate(
						R.layout.list_cell_debug_message, null);
				vh = new ViewHolder(convertView);
				convertView.setTag(vh);

				Messages message = mMessageList.get(position);
				if (message != null) {
					String value = new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date(message.getUpdatedAt()));
					vh.mTvTime.setText(value);
					vh.mTvType.setText(String.valueOf(message.getType()));
					vh.mTvTitle.setText(String.valueOf(message.getTitle()));
				}
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			return convertView;
		}
	}

	static class ViewHolder {
		@InjectView(R.id.tv_message_time)
		TextView mTvTime;
		@InjectView(R.id.tv_message_type)
		TextView mTvType;
		@InjectView(R.id.tv_message_title)
		TextView mTvTitle;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
