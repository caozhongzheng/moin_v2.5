package com.moinapp.wuliao.bean;


/**
 * 消息实体类
 */
@SuppressWarnings("serial")
public class Messages extends Entity {
	/**
	 * 根据需求, 5大类消息最终在消息列表展示时要分为8种消息,每一条消息都属于这8种种之一,最前面用
	 * 不同的图标表示
	 * 1001. 评论消息 (type=1 action=102)
	 * 1002. 转发转改消息(type=1 action=103 104)
	 * 1003. 赞消息(type=1 action=101)
	 * 1004. @消息(type=1 action=105)
	 * 1005. 关注消息(type=2)
	 * 1006. 表情消息(type=4)
	 * 1007. 贴纸消息(type=3)
	 * 1008. 系统消息(type=9)
	 */
	public final static int MESSAGE_COMMENT = 1001;
	public final static int MESSAGE_FORWARD = 1002;
	public final static int MESSAGE_LIKE = 1003;
	public final static int MESSAGE_AT = 1004;
	public final static int MESSAGE_FOLLOW = 1005;
	public final static int MESSAGE_EMOJI = 1006;
	public final static int MESSAGE_STICKER = 1007;
	public final static int MESSAGE_SYSTEM = 1008;

	/** 消息来源的五大类
	 * type: 1 图片、2 用户、3 贴纸、4 表情、5 标签、6 帖子、9 系统
	 */
	public final static int TYPE_IMAGE = 1;
	public final static int TYPE_USER = 2;
	public final static int TYPE_STICKER = 3;
//	public final static int TYPE_EMOJI = 4;
	public final static int TYPE_TAG = 4;
	public final static int TYPE_POST = 6;
	public final static int TYPE_SYSTEM = 9;

	/**
	 * action：对应不同type，
	 * type为1图片时，101 赞 102 评论 103 转发 104 转改 105 @ 106回复 107评论赞过的图片 108 回复你发的图中的评论 109用户发表了cosplay
	 * type为2用户时 201 注册 202 关注
	 * type为3 贴纸时，301 正常贴纸、302 限时贴纸、303 文字图案、304 特殊符号、305 文字气泡、306 边框
	 */
	public final static int ACTION_LIKE_COSPLAY = 101;
	public final static int ACTION_COMMENT_COSPLAY = 102;
	public final static int ACTION_FORWARD_COSPLAY = 103;
	public final static int ACTION_MODIFY_COSPLAY = 104;
	public final static int ACTION_AT_COSPLAY = 105;
	public final static int ACTION_REPLY_COSPLAY = 106;
	public final static int ACTION_COMMENT_LIKE_COSPLAY = 107;
	public final static int ACTION_REPLY_COMMENT_COSPLAY = 108;
	public final static int ACTION_SUBMIT_COSPLAY = 109;//消息不会用到,动态中会用到

	public final static int ACTION_REGISTER = 201;
	public final static int ACTION_FOLLOW = 202;
	public final static int ACTION_CHAT = 203;

	public final static int ACTION_STICKER_NORMAL = 301;
	public final static int ACTION_STICKER_INTIME = 302;
	public final static int ACTION_STICKER_TEXT = 303;
	public final static int ACTION_STICKER_SPECIAL = 304;
	public final static int ACTION_STICKER_BUBBLE = 305;
	public final static int ACTION_STICKER_FRAME = 306;

	public final static int ACTION_FROM_TAG = 401;
	public final static int ACTION_FOLLOW_TAG = 402;// 关注标签

	/**
	 * 系统启动图片的消息
	 */
	public final static int ACTION_SYSTEM_BOOT_IMAGE = 901;

	/**
	 * 消息的标示id
	 */
	private String messageId;

	/**
	 * 消息的title
	 */
	private String title;

	/**
	 * 对应title内容的显示格式，用代表index的数字分隔。如2 表示从第3个字符开始分为两段；
	 * 2,6 表示从第3个字符和第7个字符开始分为三段。
	 */
	private String style;

	/**
	 * 消息类型
	 */
	private int type;

	/**
	 * 消息的点击动作
	 */
	private int action;

	/**
	 * 客户端显示的消息图标，如关注消息中，显示粉丝的用户头像
	 */
	private String icon;

	/**
	 * 客户端显示的消息图片，如图片消息中，显示转发的图片缩略图
	 */
	private String picture;

	/**
	 * 服务器消息下发的时间戳
	 */
	private long updatedAt;

	/**
	 * 用户ID，当消息类型是图片时，对应操作图片的用户ID；消息类型是用户时，对应用户ID；
	 */
	private String uid;

	/**
	 * 资源ID，当消息类型是图片时，对应操作图片的ID；消息类型是表情时，对表情包资源ID；
	 */
	private String id;

	/**
	 * user对象,仅当action=203是聊天消息时下发
	 */
	private UserInfo user;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getResId() {
		return id;
	}

	public void setResId(String id) {
		this.id = id;
	}

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public boolean isComment() {
		return getAction() == ACTION_COMMENT_COSPLAY ||
				getAction() == ACTION_COMMENT_LIKE_COSPLAY ||
				getAction() == ACTION_REPLY_COSPLAY ||
				getAction() == ACTION_REPLY_COMMENT_COSPLAY;
	}

	@Override
	public String toString() {
		return "Messages{" +
				"messageId='" + messageId + '\'' +
				", title='" + title + '\'' +
				", style='" + style + '\'' +
				", type=" + type +
				", action=" + action +
				", icon='" + icon + '\'' +
				", picture='" + picture + '\'' +
				", updatedAt=" + updatedAt +
				", uid='" + uid + '\'' +
				", id='" + id + '\'' +
				'}';
	}
}
