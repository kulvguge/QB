package android.com.fenguoz.customview;

import android.com.fenguoz.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class ProgressWebView extends WebView {
    private Context context;
    private ProgressBar progressbar;
    private ReceivedTitleListener lisenter;

    @SuppressWarnings("deprecation")
    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        WebSettings websetting = getSettings();
        // 设置js可用
        websetting.setJavaScriptEnabled(true);
        // 设置获取本地缓存
        websetting.setDomStorageEnabled(true);
        websetting.setDatabaseEnabled(true); // 初始化时缩放
        websetting.setUseWideViewPort(true);// 设置加载进来的页面自适应手机屏幕
        websetting.setLoadWithOverviewMode(true);// 以缩略图模式加载页面
        websetting.setDefaultTextEncodingName("utf-8");
        //websetting.setCacheMode(WebSettings.LOAD_NO_CACHE);// 解决缓存问题
        websetting.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        // 开启 DOM storage API 功能  
        websetting.setDomStorageEnabled(true);
       
        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS
         * ：适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN : 适应屏幕，内容将自动缩放
         */
        websetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);

        websetting.setUseWideViewPort(true);// 启用支持视窗meta标记（可实现双击缩放）
        websetting.setRenderPriority(RenderPriority.HIGH);
        // 启用触控缩放
        websetting.setBuiltInZoomControls(true);
        websetting.setSupportZoom(true);
        // TODO
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 5, 0, 0));
        Drawable draw = getResources().getDrawable(R.drawable.progressbar_color);
        draw.setBounds(progressbar.getProgressDrawable().getBounds());
        progressbar.setProgressDrawable(draw);

        addView(progressbar);
        // 设置将接收各种通知和请求的WebViewClient（在WebView加载所有的链接）
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        setWebChromeClient(new WebChromeClient());
        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                if (url != null && url.startsWith("http://"))
                    ProgressWebView.this.context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (lisenter != null) {
                lisenter.onReceivedTitle(view, title);
            }
            super.onReceivedTitle(view, title);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setLinsener(ReceivedTitleListener lisenter) {
        this.lisenter = lisenter;
    }

    public interface ReceivedTitleListener {
        public void onReceivedTitle(WebView view, String title);
    }

}
