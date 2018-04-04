package android.com.fenguoz;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;

public class ShareWBUtils {
	/** 微博OpenAPI访问入口 */
	IWeiboAPI mWeiboAPI = null;

	// UI元素列表
	/** 分享文本 */
	private String mTitle;

	/** 分享图片 */
	private ImageView mImage;

	private WBShareActivity context;

	public ShareWBUtils(WBShareActivity context, IWeiboAPI mWeiboAPI, String mTitle, ImageView mImage) {
		this.mWeiboAPI = mWeiboAPI;
		this.mTitle = mTitle;
		this.mImage = mImage;
		this.context = context;
	}

	public void reqMsg(boolean hasText, boolean hasImage, boolean hasWebpage, boolean hasMusic, boolean hasVedio,
			boolean hasVoice) {

		if (mWeiboAPI.isWeiboAppSupportAPI()) {
			// Toast.makeText(context, "当前微博版本支持SDK分享",
			// Toast.LENGTH_SHORT).show();

			int supportApi = mWeiboAPI.getWeiboAppSupportAPI();
			if (supportApi >= 10351) {
				// Toast.makeText(context, "当前微博版本支持多条消息，Voice消息分享",
				// Toast.LENGTH_SHORT).show();
				reqMultiMsg(hasText, hasImage, hasWebpage, hasMusic, hasVedio, hasVoice);
			} else {
				// Toast.makeText(context, "当前微博版本只支持单条消息分享",
				// Toast.LENGTH_SHORT).show();
				reqSingleMsg(hasText, hasImage, hasWebpage, hasMusic,
						hasVedio/*
								 * , hasVoice
								 */);
			}
		} else {
			// Toast.makeText(context, "当前微博版本不支持SDK分享",
			// Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 注意：当isWeiboAppSupportAPI() >= 10351
	 * 时，支持同时分享多条消息， 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）， 并且支持Voice消息。
	 * 
	 * @param hasText
	 *            分享的内容是否有文本
	 * @param hasImage
	 *            分享的内容是否有图片
	 * @param hasWebpage
	 *            分享的内容是否有网页
	 * @param hasMusic
	 *            分享的内容是否有音乐
	 * @param hasVideo
	 *            分享的内容是否有视频
	 * @param hasVoice
	 *            分享的内容是否有声音
	 */
	private void reqMultiMsg(boolean hasText, boolean hasImage, boolean hasWebpage, boolean hasMusic, boolean hasVideo,
			boolean hasVoice) {

		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		if (hasText) {
			weiboMessage.textObject = getTextObj();
		}

		if (hasImage) {
			weiboMessage.imageObject = getImageObj();
		}

		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest req = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.multiMessage = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboAPI.sendRequest(context, req);
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面。 当isWeiboAppSupportAPI() < 10351 只支持分享单条消息，即
	 * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
	 * 
	 * @param hasText
	 *            分享的内容是否有文本
	 * @param hasImage
	 *            分享的内容是否有图片
	 * @param hasWebpage
	 *            分享的内容是否有网页
	 * @param hasMusic
	 *            分享的内容是否有音乐
	 * @param hasVideo
	 *            分享的内容是否有视频
	 */
	private void reqSingleMsg(boolean hasText, boolean hasImage, boolean hasWebpage, boolean hasMusic,
			boolean hasVideo/*
							 * , boolean hasVoice
							 */) {

		// 1. 初始化微博的分享消息
		// 用户可以分享文本、图片、网页、音乐、视频中的一种
		WeiboMessage weiboMessage = new WeiboMessage();
		if (hasText) {
			weiboMessage.mediaObject = getTextObj();
		}
		if (hasImage) {
			weiboMessage.mediaObject = getImageObj();
		}

		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = weiboMessage;

		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboAPI.sendRequest(context, req);
	}

	/**
	 * 文本消息构造方法。
	 * 
	 * @return 文本消息对象。
	 */
	private TextObject getTextObj() {
		TextObject textObject = new TextObject();
		textObject.text = mTitle.toString();
		return textObject;
	}

	/**
	 * 图片消息构造方法。
	 * 
	 * @return 图片消息对象。
	 */
	private ImageObject getImageObj() {
		ImageObject imageObject = new ImageObject();
		BitmapDrawable bitmapDrawable = (BitmapDrawable) mImage.getDrawable();
		imageObject.setImageObject(bitmapDrawable.getBitmap());
		return imageObject;
	}
}
