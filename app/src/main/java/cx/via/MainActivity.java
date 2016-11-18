package cx.via;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView mWv;
    private WebView mWv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWv = (WebView) findViewById(R.id.wv);
        mWv2 = (WebView) findViewById(R.id.wv2);
        mWv2.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.equals("via.cx", Uri.parse(url).getHost())) {
                    Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        onClickReload(null);
    }

    public void onClickReload(View view) {
        mWv.loadUrl("file:///android_asset/test.html");
        mWv2.loadUrl("file:///android_asset/test.html");
    }
}
