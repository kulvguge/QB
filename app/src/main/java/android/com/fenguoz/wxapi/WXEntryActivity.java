package android.com.fenguoz.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信客户端回调activity示例
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI wxApi;

    private String TAG = "tag";
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        wxApi = WXAPIFactory.createWXAPI(WXEntryActivity.this, WXConstants.WEIXIN_ID, false);
        wxApi.handleIntent(getIntent(), WXEntryActivity.this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq arg0) {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        wxApi.handleIntent(getIntent(), this);
    }


    /**
     * 回调结果
     **/
    @Override
    public void onResp(BaseResp resp) {

        System.out.println("微信回调" + "resp.errCode:" + resp.errCode + ",resp.errStr:" + resp.errStr);
        Log.i(TAG, "resp.errCode:" + resp.errCode + ",resp.errStr:" + resp.errStr);
        if (ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX == resp.getType()) {
            //分享
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    break;
            }

        } else {
            //登录
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    try {
                        String code = ((SendAuth.Resp) resp).code;

                        // ToastUtils.TextToast(this, "授权登录成功！", 1000 * 2);
                    } catch (Exception e) {
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:

                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:

                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:

                    break;
                default:
                    break;

            }

        }
        finish();
    }

}