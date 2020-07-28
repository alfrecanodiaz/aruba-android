package py.com.aruba.profesionales.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import py.com.aruba.profesionales.R;
import py.com.aruba.profesionales.data.models.Appointment;
import py.com.aruba.profesionales.utils.Constants;
import py.com.aruba.profesionales.utils.Print;
import py.com.aruba.profesionales.utils.SharedPreferencesUtils;
import py.com.aruba.profesionales.utils.Utils;

public class TermsActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.btnAcept)
    Button btnAcept;
    boolean canClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        ButterKnife.bind(this);
        loadWebView();
    }

    /**
     * Cargamos el webview de los términos
     */
    private void loadWebView() {
        String initial_url = "https://aruba.com.py/terminos.php";

        webView.setVerticalScrollBarEnabled(true);
        webView.requestFocus();
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setPadding(0, 0, 0, 0);
        webView.setBackgroundColor(getResources().getColor(R.color.transparent));
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(initial_url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

            }

        });
        webView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // Si está llegando al final, habilitamos el botón
            if ((webView.getScrollY() > webView.getContentHeight() - 100)) {
                canClick = true;
                btnAcept.setBackground(getResources().getDrawable(R.drawable.button_aqua));
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            private ProgressDialog mProgress;

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (mProgress == null) {
                    mProgress = new ProgressDialog(TermsActivity.this);
                    mProgress.show();
                }
                mProgress.setMessage("Cargando " + String.valueOf(progress) + "%");
                if (progress == 100) {
                    mProgress.dismiss();
                    mProgress = null;
                }
            }
        });
    }

    @OnClick({R.id.btnAcept, R.id.rlBackButton,})
    public void onViewClicked(View view) {
        Utils.preventTwoClick(view);
        switch (view.getId()) {
            case R.id.rlBackButton:
                finish();
                break;
            case R.id.btnAcept:
                if (canClick) {
                    SharedPreferencesUtils.setValue(TermsActivity.this, Constants.AGREE_TERMS, true);
                    finish();
                } else {
                    Toast.makeText(TermsActivity.this, "Por favor deslize hasta el final del texto para habilitar el botón.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
