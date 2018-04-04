package android.com.fenguoz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboHandler;

/**
 * 该Activity演示了第三方应用如何发送请求消息给微博客户端。发送的内容包括文字、图片、视频、音乐等。 执行流程： 从本应用->微博->本应用
 */
public class WBShareActivity extends Activity implements IWeiboHandler.Response {

    /**
     * 微博OpenAPI访问入口
     */
    IWeiboAPI mWeiboAPI = null;

    /**
     * 分享图片
     */
    private ImageView mImage;
    public boolean isCancle = false;
    /**
     * 用户点击分享按钮，唤起微博客户端进行分享。
     */
    private ShareWBUtils shareUtils;

    private String mUserId;
    private String shareMsg;

    private String cricle_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shareMsg = getIntent().getStringExtra("shareMsg");
        cricle_type = getIntent().getStringExtra("cricle_type");
        if (shareMsg == null) {
            finish();
            return;
        }
        // 创建微博对外接口实例
        mWeiboAPI = WeiboSDK.createWeiboAPI(this, WBConstants.APP_KEY);
        mWeiboAPI.responseListener(getIntent(), this);


        mWeiboAPI.registerApp();
        shareUtils = new ShareWBUtils(WBShareActivity.this, mWeiboAPI, shareMsg, mImage);
        shareUtils.reqMsg(true, false, false, false, false, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shareMsg == null) {
            finish();
            return;
        }
        //解决 微博分享 点击取消再保存信息，返回次页面 不进入回调 造成页面无法关闭的问题
        if (isCancle) {
            Boolean resp = mWeiboAPI.responseListener(getIntent(), this);
            System.out.println("微博分享对错是:" + resp);
            if (!resp) {
                finish();
            }
        }
        //第一次调用后isCancle变为true
        isCancle = true;

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboAPI.responseListener(intent, this);
    }

    /**
     * 从本应用->微博->本应用 接收响应数据，该方法被调用。 注意：确保{@link #onCreate(Bundle)} 与
     * {@link #onNewIntent(Intent)}中， 调用 mWeiboAPI.responseListener(intent,
     * this)
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        System.out.println("进入分享微博回调");
        switch (baseResp.errCode) {
            case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_OK:
                Toast.makeText(this, "分享成功！！", Toast.LENGTH_LONG).show();
                break;
            case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, "取消分享！！", Toast.LENGTH_LONG).show();
                break;
            case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, baseResp.errMsg + ":失败！！", Toast.LENGTH_LONG).show();
                break;

        }
        finish();
    }


}
