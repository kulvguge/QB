package android.com.fenguoz;

/**
 * Created by ts on 2018/4/4.
 */

import android.app.Activity;
import android.com.fenguoz.customview.SelectPicPopupWindow;
import android.com.fenguoz.wxapi.WXConstants;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.List;

public class ShareUtils {
    public static String urlDis;
    public static String title;
    public static String content = "";
    public static String log;
    public static IWXAPI wxApi;
    private static Tencent mTencent;
    private static Activity activity;
    private static Bitmap thumb;
    private static SelectPicPopupWindow menuWindow;


    /**
     * @param activity 要调用的activity
     * @param urlDis   分享内容的地址
     * @param title    分享的title
     * @param content  分享的内容
     * @param log      分享各个平台的显示图片
     *                 urlType  1 拼接openid 2 不拼
     */
    @SuppressWarnings("static-access")
    public ShareUtils(Activity activity, String urlDis, String title, String content, String log
                        ) {
        this.activity = activity;
        this.urlDis = urlDis;
        //this.title=title;
        this.content = content;
        this.urlDis = urlDis;
        if (TextUtils.isEmpty(log)) {
            this.log = "http://static.jinvovo.com/images/app/share_app_logo.png";
        } else {
            this.log = log;
        }

        if (TextUtils.isEmpty(title)) {
            title = "金窝窝";
        } else {
            this.title = title;
        }
    }

    public void share() {

        // this.activity =activity;
        wxApi = WXAPIFactory.createWXAPI(activity, WXConstants.WEIXIN_ID, true);
        wxApi.registerApp(WXConstants.WEIXIN_ID);
        mTencent = Tencent.createInstance("1104690443", activity);
        menuWindow = new SelectPicPopupWindow(activity, itemsOnClick);
        // 显示窗口
        final View viewss = LayoutInflater.from(activity).inflate(
                R.layout.shared_popupwindow, null);


        menuWindow.showAtLocation(viewss, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    // 为弹出窗口实现监听类
    final static Bundle params = new Bundle();
    private static View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.dialog_weixin:
                    if (!wxApi.isWXAppInstalled()) {
                        // 提醒用户没有按照微信
                        Toast.makeText(activity, "沒有安裝微信", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    wechatShare(0);
                    //statisticsEvent(activity, areaId);
                    break;
                case R.id.dialog_weixinmoments:
                    if (!wxApi.isWXAppInstalled()) {
                        // 提醒用户没有按照微信
                        Toast.makeText(activity, "沒有安裝微信", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    wechatShare(1);
                    //statisticsEvent(activity, areaId);
                    break;
                case R.id.dialog_sinaweibo:
                    if (!isAvilible("com.sina.weibo")) {
                        Toast.makeText(activity, "未安装新浪微博", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(activity, WBShareActivity.class);
                        // TODO: 2018/4/4  
                        intent.putExtra("shareMsg", "MSG");
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.in_from_right,
                                R.anim.out_to_left);
                    }
                    break;

                case R.id.dialog_qone:
                    params.putInt("req_type",
                            QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
                    params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);// 必填
                    params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);// 选填
                    params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, urlDis);// 必填
                    // 支持传多个imageUrl
                    ArrayList<String> imageUrls = new ArrayList<String>();
                    imageUrls.add(log);
                    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,
                            imageUrls);
                    mTencent.shareToQzone(activity, params, new BaseUiListener());
                    break;
                case R.id.dialog_qq:
                    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
                            QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                    params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                    params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
                    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, urlDis);
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, log);
                    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "秦贝");
                    mTencent.shareToQQ(activity, params, new BaseUiListener());
                    break;
                case R.id.dialog_cancel_lay:
                    break;
                case R.id.pop_layout:
                    break;
                default:
                    break;
            }
            menuWindow.dismiss();
        }
    };


    /**
     * qq分享回调
     */
    private static class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            Toast.makeText(activity, "分享成功！！", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(activity, "分享失败！！", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(activity, "取消分享！！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     *
     * @param flag (0:分享到微信好友，1：分享到微信朋友圈)
     */


    private static void wechatShare(int flag) {
        //System.out.println("点击分享了微信");
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = /*getShareText(*/urlDis/*)*/;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        if (title == null) {
            msg.title = "金窝窝";
            msg.description = "最新活动";
        } else {
            msg.title = title;
            msg.description = content;
        }
        if (thumb == null) {
            thumb = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher_background);
        }
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
                : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);

    }


    /**
     * 通过包名检测系统中是否安装某个应用程序
     *
     * @param packageName ：应用程序的包名(QB:com.tencent.mtt)
     * @return false : 系统中未安装该应用程序
     */

    public static boolean isAvilible(String packageName) {
        PackageManager packageManager = activity.getPackageManager();

        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
}


