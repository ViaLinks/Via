package cx.via.webview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.walfud.library.R;

import java.net.URISyntaxException;
import java.util.HashSet;

/**
 * Created by duanshenglian on 2016/11/17.
 */

public class ViaWebView extends WebView {
    private WebViewClient mCustomWebViewClient;
    private final HashSet<String> mOutJumpDomains = new HashSet<>();

    public ViaWebView(Context context) {
        this(context, null);
    }

    public ViaWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViaWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ViaWebView);
            if (typedArray.hasValue(R.styleable.ViaWebView_via_jumpOutDomains)) {
                String jumpOutDomainsStr = typedArray.getString(R.styleable.ViaWebView_via_jumpOutDomains);
                if (!TextUtils.isEmpty(jumpOutDomainsStr)) {
                    String[] jumpOutDomainsArr = jumpOutDomainsStr.split("\\|");
                    for (String domain : jumpOutDomainsArr) {
                        mOutJumpDomains.add(domain);
                    }
                }
            }
        }

        super.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (TextUtils.isEmpty(url)) {
                    return super.shouldOverrideUrlLoading(view, url);
                } else if (url.startsWith("intent:")) {
                    return handleIntentUri(getContext(), url);
                } else if (mOutJumpDomains.contains(uri.getHost())) {
                    tryToJumpOut(url);
                    return true;
                } else if (mCustomWebViewClient != null) {
                    return mCustomWebViewClient.shouldOverrideUrlLoading(view, url);
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        mCustomWebViewClient = client;
    }

    /**
     * 处理intent协议的url
     *
     * @param context 上下文
     * @param url     intent协议的url
     * @return url是不是被消费
     */
    public boolean handleIntentUri(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url) || !url.startsWith("intent:")) {
            return false;
        }
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            PackageManager pm = context.getPackageManager();
            if (intent.resolveActivity(pm) != null) {
                //本地安装了应用，就启动
                context.startActivity(intent);
                return true;
            } else if (!TextUtils.isEmpty(intent.getPackage())) {
                //本地未安装应用，intent请求中包含报名，就到应用市场搜索该安装包
                Uri marketUri = Uri.parse(String.format("market://search?q=pname:%s", intent.getPackage()));
                Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
                if (marketIntent.resolveActivity(pm) != null) {
                    ////安装了应用市场，就去搜索
                    context.startActivity(marketIntent);
                    return true;
                } else {
                    ////没安装应用市场
                    String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                    return tryToJumpOut(fallbackUrl);
                }
            } else {
                //intent请求中不包含包名
                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                return tryToJumpOut(fallbackUrl);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 尝试跳转到外部处理
     *
     * @param url
     * @return 外部是否处理成功
     */
    private boolean tryToJumpOut(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getContext().startActivity(viewIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
