package cx.via;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;
import java.util.HashSet;

import cx.via.library.R;

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

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (mCustomWebViewClient != null) {
                    return mCustomWebViewClient.shouldOverrideUrlLoading(view, request);
                } else {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onPageStarted(view, url, favicon);
                } else {
                    super.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onPageFinished(view, url);
                } else {
                    super.onPageFinished(view, url);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onLoadResource(view, url);
                } else {
                    super.onLoadResource(view, url);
                }
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onPageCommitVisible(view, url);
                } else {
                    super.onPageCommitVisible(view, url);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (mCustomWebViewClient != null) {
                    return mCustomWebViewClient.shouldInterceptRequest(view, url);
                } else {
                    return super.shouldInterceptRequest(view, url);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (mCustomWebViewClient != null) {
                    return mCustomWebViewClient.shouldInterceptRequest(view, request);
                } else {
                    return super.shouldInterceptRequest(view, request);
                }
            }

            @Override
            public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
                } else {
                    super.onTooManyRedirects(view, cancelMsg, continueMsg);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
                } else {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedError(view, request, error);
                } else {
                    super.onReceivedError(view, request, error);
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedHttpError(view, request, errorResponse);
                } else {
                    super.onReceivedHttpError(view, request, errorResponse);
                }
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onFormResubmission(view, dontResend, resend);
                } else {
                    super.onFormResubmission(view, dontResend, resend);
                }
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.doUpdateVisitedHistory(view, url, isReload);
                } else {
                    super.doUpdateVisitedHistory(view, url, isReload);
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedSslError(view, handler, error);
                } else {
                    super.onReceivedSslError(view, handler, error);
                }
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedClientCertRequest(view, request);
                } else {
                    super.onReceivedClientCertRequest(view, request);
                }
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
                } else {
                    super.onReceivedHttpAuthRequest(view, handler, host, realm);
                }
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                if (mCustomWebViewClient != null) {
                    return mCustomWebViewClient.shouldOverrideKeyEvent(view, event);
                } else {
                    return super.shouldOverrideKeyEvent(view, event);
                }
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onUnhandledKeyEvent(view, event);
                } else {
                    super.onUnhandledKeyEvent(view, event);
                }
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onScaleChanged(view, oldScale, newScale);
                } else {
                    super.onScaleChanged(view, oldScale, newScale);
                }
            }

            @Override
            public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                if (mCustomWebViewClient != null) {
                    mCustomWebViewClient.onReceivedLoginRequest(view, realm, account, args);
                } else {
                    super.onReceivedLoginRequest(view, realm, account, args);
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
