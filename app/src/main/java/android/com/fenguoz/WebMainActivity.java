package android.com.fenguoz;

import android.Manifest;
import android.app.Activity;
import android.com.fenguoz.customview.ProgressWebView;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by TS on 2018/4/2.
 */

public class WebMainActivity extends Activity implements View.OnClickListener {
    private ProgressWebView mWebView;
    private String currentUrl = "http://www.fenguoz.com";
    private boolean hasNext = false;//是否有下一页
    private boolean canBack = false;//是否有上一页
    private long time = -1l;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mWebView = findViewById(R.id.webview);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.fresh).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        findViewById(R.id.more).setOnClickListener(this);
        findViewById(R.id.cut_screen).setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);
        init();
    }

    /**
     * webView初始化信息
     */
    private void init() {
        // 兼容http和https混淆访问
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 启用触控缩放
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setSupportZoom(false);
        // 设置js可用
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "jmallJSObject");
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setGeolocationEnabled(true);

        mWebView.getSettings().setDomStorageEnabled(true);
//        mWebView.setWebChromeClient(new WebChromeClient() {
//
//            // 配置权限（同样在WebChromeClient中实现）
//            @Override
//            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
//                callback.invoke(origin, true, false);
//                super.onGeolocationPermissionsShowPrompt(origin, callback);
//                // System.out.println("进入 没有定位权限");
////                // 启用数据库
////                mWebView.getSettings().setDatabaseEnabled(true);
////                // 设置定位的数据库路径
////                String dir = WebMainActivity.this.getApplicationContext().getDir("database", Context.MODE_PRIVATE)
////                        .getPath();
////                mWebView.getSettings().setGeolocationDatabasePath(dir);
////                mWebView.getSettings().setGeolocationEnabled(true);
////                mWebView.getSettings().setDomStorageEnabled(true);
////                if (Build.VERSION.SDK_INT >= 23) {
////                    int checkPermission = ContextCompat.checkSelfPermission(WebMainActivity.this,
////                            Manifest.permission.ACCESS_COARSE_LOCATION);
////                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
////                        ActivityCompat.requestPermissions(WebMainActivity.this,
////                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
////                        ActivityCompat.requestPermissions(WebMainActivity.this,
////                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
////                        // System.out.println("进入定位这里");
////                        mWebView.getSettings().setGeolocationEnabled(true);
////
////                        mWebView.getSettings().setDomStorageEnabled(true);
////
////                    }
////                }
//            }
//
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//
//            }
//        });
        // mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.loadUrl(currentUrl);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (canBack) {
                    mWebView.goBack();
                } else {
                    if (time > 0 && System.currentTimeMillis() - time < 2000) {
                        finish();
                    } else {
                        time = System.currentTimeMillis();
                        Toast.makeText(this, "再次点击退出APP", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.fresh:
                if (mWebView != null)
                    mWebView.reload();
                break;
            case R.id.cut_screen:
                if (Build.VERSION.SDK_INT >= 23) {
                    int per = ContextCompat.checkSelfPermission(WebMainActivity.this, Manifest.permission
                            .WRITE_EXTERNAL_STORAGE);
                    if (per == PackageManager.PERMISSION_GRANTED) {
                        cutScreen();
                    } else {
                        ActivityCompat.requestPermissions(WebMainActivity.this, new String[]{
                                Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE
                        }, 1);
                    }
                } else {
                    cutScreen();
                }

                break;
            case R.id.more:

                break;
            case R.id.share:
                ShareUtils shareUtils = new ShareUtils(WebMainActivity.this, "http:www.baidu.com", "qingbei",
                        "qingbei", "");
                shareUtils.share();
                break;
            case R.id.next:
                if (hasNext) {
                    mWebView.goForward();
                } else {
                    Toast.makeText(this, "没有下一页了!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    final class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            new AtTheFirstTime().setCookie(ShopWebViewActivity.this, currentUrl);
            super.onPageStarted(view, url, favicon);
            Log.i("WebView", "onPageStart:" + url);
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            CookieManager cookieManager = CookieManager.getInstance();
            String CookieStr = cookieManager.getCookie(url);
            Log.i("WebView", "onPageFinished" + url + "==" + CookieStr);
            if (mWebView.canGoForward()) {
                hasNext = true;
            } else {
                hasNext = false;
            }
            if (mWebView.canGoBack()) {
                canBack = true;
            } else {
                canBack = false;
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.i("WebView", "onReceivedError" + failingUrl);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.i("WebView", "onReceivedSslError" + handler.toString() + " _ " + error.toString());
            handler.proceed();
        }
    }

    class InJavaScriptLocalObj {
        //cookie过时
        @JavascriptInterface
        public void loginOut() {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack(); // goBack()表示返回WebView的上一页面
            } else {
                if (time > 0 && System.currentTimeMillis() - time < 2000) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    time = System.currentTimeMillis();
                    Toast.makeText(this, "再次点击退出APP", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    /**
     * 截屏
     */
    private void cutScreen() {

        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
        if (bitmap != null) {
            try {
                // 获取内置SD卡路径
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                // 图片文件路径
                String filePath = sdCardPath + File.separator + "screenshot.png";
                File file = new File(filePath);
                FileOutputStream os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
                //通知相册图片有更新
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        bitmap, file.getPath(), null);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                sendBroadcast(intent);
                bitmap.recycle();
                Toast.makeText(this, "截屏已经保存", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.i("Exception", e.toString());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cutScreen();
        } else {
            Toast.makeText(this, "请开启存储权限", Toast.LENGTH_SHORT).show();
        }
    }
}
