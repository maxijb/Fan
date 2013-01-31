package com.example.fan;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

public class FanMain extends Activity {

	private WebView web;
	private ImageView splash;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_fan_main);
        splash = (ImageView) findViewById(R.id.imageView1);
        
        web = (WebView) findViewById(R.id.webView1);
        web.setWebViewClient(new WebViewClient());
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web.addJavascriptInterface(new WebAppInterface(this), "Android");
        web.loadUrl("file:///android_asset/www/index2.html");
        
    }

    
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event )
    {
        if ( event.getKeyCode() == KeyEvent.KEYCODE_SEARCH )
        {
            // Catch the search 
            // do something
            // return true to consume press or false to pass it on
        	web.loadUrl("javascript:pressBuscar()");
        	return true;
        }
        
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
        	web.loadUrl("javascript:handleBackButton()");
        	return true;
        }
        return super.onKeyDown( keyCode, event ); 
    }
    
    
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_fan_main, menu);
//        return true;
//    }
    
    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();  
        }
        
        public void hideSplash() {
        	runOnUiThread(new Runnable() {  
        	     public void run() {
        	    	 //stuff that updates ui 
        	    	 splash.setVisibility(View.GONE);
        	    	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        	    }
        	});
        }
        
        public void backButtonPress() {
        	finish();
        }
        
        
        /** Muestar **/
        public void receiveImages(String imgs, String index) {
        	Intent i = new Intent(FanMain.this, ImageViewer.class);
        	i.putExtra("index", Integer.parseInt(index));
        	i.putExtra("images", imgs.split("\\*")); 
        	startActivity(i);
        }
        
        public void playVideo(String videoId) { 
        	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId)));
        }
    }
    
}
