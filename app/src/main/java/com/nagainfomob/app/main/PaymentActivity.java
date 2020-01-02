package com.nagainfomob.app.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.nagainfomob.app.R;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.model.UserModel;
import com.nagainfomob.app.sql.DatabaseManager;


/**
 * Created by joy on 20/04/18.
 */

public class PaymentActivity extends Activity {

    //private Button button;
    private WebView webView;
    private LinearLayout LoaderView;
    private SessionManager session;

    private String mSuccessUrl = "http://54.88.149.63:3001/v1/payment/success";
    private String mFailedUrl = "http://54.88.149.63:3001/v1/payment/fail";

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        session = new SessionManager(this);

        //Get webview
        webView = (WebView) findViewById(R.id.webView1);

        String url = "";
        url = getIntent().getExtras().getString("pay_url");

        if(!url.equals("")){
            startWebView(url);
        }
    }

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        webView.setWebViewClient(new WebViewClient() {
//            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                /*if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(ShowWebView.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }*/
            }
            public void onPageFinished(WebView view, String url) {
                try{

                    Log.d("PaymentLog", "URL - "+url);

                    if (url.equals(mSuccessUrl)) {

                        UserModel userdata = new UserModel();
                        userdata.setUserId(session.getUserID());
                        userdata.setSubType("2");
                        DatabaseManager.saveUser(PaymentActivity.this, userdata);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(PaymentActivity.this, DashboardActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }, 3000);

                    } else if (url.equals(mFailedUrl)) {

                        /*final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("MESSAGE","fail");
                                setResult(3,intent);
                                finish();
                            }
                        }, 3000);*/

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(PaymentActivity.this, DashboardActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }, 3000);
                    }

                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);

        //Load url in webview
        webView.loadUrl(url);


    }

    // Open previous opened link from history on webview when back button pressed

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

}