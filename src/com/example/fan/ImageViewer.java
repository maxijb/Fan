package com.example.fan;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ImageViewer extends Activity implements OnClickListener  {

	protected String[] images;
	protected int index;
	protected String dominio;
	private ImageView img;
	protected static ImageViewer self;
	private ProgressBar progress;
	private int w, h; //screen width and hieght
	private Boolean isLoading;
	private int direction = 1;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_image_viewer);
		getDimensions();
		Bundle b = getIntent().getExtras();
		index = b.getInt("index");
		images = b.getStringArray("images");
		img = (ImageView) findViewById(R.id.img);
		img.setOnClickListener(this);
		gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        img.setOnTouchListener(gestureListener);
		
		progress = (ProgressBar) findViewById(R.id.progress);
		isLoading = true;
		self = this;
		loadImage(index);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		getDimensions();
//		loadImage(index);
		super.onConfigurationChanged(newConfig);
	}
	
	
	public void onClick(View v) {
	     finish();
	}
	
	/** Gets the page's dimensions 
	 *  Es llamada por create y configurationchange
	 * */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void getDimensions() {
		Display display = getWindowManager().getDefaultDisplay();
		try {
			Point size = new Point();
			display.getSize(size);
			w = size.x;
			h = size.y;
		}
		catch (NoSuchMethodError e) { 
			w = display.getWidth();
			h = display.getHeight();
		}
	}
	
	
	
	private void passImage(int delta) {
		direction = delta;
		index += direction;
		if (index < 0) {
			index = 0;
			falseSlide(); 
		}
		else if ( index >= images.length) {
			index = images.length - 1;
			falseSlide();
		}
		else {
			loadImage(index);
		}
	}
	
	private void falseSlide() {
		
	}
	
	private void loadImage(int num) {
		progress.setVisibility(View.VISIBLE);
		TranslateAnimation animation = makeAnimation(false);
		img.startAnimation(animation);
		index = num;
		try {
			Log.i("URL", "http://www.fannumero1.com.ar/image_resizer.php?image=img/ids/"+ images[num]+"&width="+w+"&height="+h);
			URL url = new URL("http://www.fannumero1.com.ar/image_resizer.php?image=img/ids/"+ images[num]+"&width="+w+"&height="+h);
			new DownloadFilesTask().execute(url);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			showImage(false, null);
		}
	}
	
	protected void showImage(Boolean state, Drawable thumb) {
		if (state) {
			progress.setVisibility(View.GONE);
			img.setVisibility(View.VISIBLE);
			img.setImageDrawable(thumb);
			Log.i("imagen", "w" + img.getWidth() + "   h:" + img.getHeight());
			TranslateAnimation animation = makeAnimation(true);
			img.startAnimation(animation);

		}
		else {
			isLoading = false;
		}
	}
	
	/** Genera la transicion
	 * 
	 * @param isArriving   true si la imagen llega, false si se va
	 * @return
	 */
	private TranslateAnimation makeAnimation(Boolean isArriving) {
		int x = (direction > 0) ? w : -w;
		x = isArriving ? x : -x;
		TranslateAnimation trans = new TranslateAnimation(isArriving ? x : 0 
														, isArriving ? 0 : x
														, 0, 0);
	    
		int duration = isArriving ? 600 : 200;
		trans.setDuration(duration);
		
		if (isArriving)
		    trans.setAnimationListener(new Animation.AnimationListener()
		    {
		        public void onAnimationEnd(Animation animation)
		        {
		        	isLoading = false;
		        }
	
				@Override
				public void onAnimationRepeat(Animation arg0) {
				}
	
				@Override
				public void onAnimationStart(Animation arg0) {
				}
		    });
		else
			trans.setAnimationListener(new Animation.AnimationListener()
			{
				public void onAnimationEnd(Animation animation)
				{
					self.hideImage();
				}
				
				@Override
				public void onAnimationRepeat(Animation arg0) {
				}
				
				@Override
				public void onAnimationStart(Animation arg0) {
				}
			});
		
		return trans;
	}
	
	protected void hideImage() {
		img.setVisibility(View.GONE);
	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_image_viewer, menu);
//		return true;
//	}
	
	/** Descarga asincrona
	 *
	 */
	 private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
		 private Drawable thumb_d;
		 
		 protected Long doInBackground(URL... urls) {
	    	 try {
	 		    thumb_d = Drawable.createFromStream(urls[0].openStream(), "src");
	 		}
	 		catch (Exception e) {
	 		    this.cancel(true);
	 		}
	    	return (long) 0;
	     }
	     
	     protected void	 onCancelled() {
	    	 self.showImage(false, null);
	     }

	     protected void onPostExecute(Long result) {
	    	 self.showImage(true, thumb_d);
	     }
	 }
	
	 
	 
	 @Override
	    public boolean onKeyDown( int keyCode, KeyEvent event )
	    {
	        if ( event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT)
	        {
	            // return true to consume press or false to pass it on
	        	passImage(-1);
	        	return true; 
	        }
	        
	        if ( event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT)
	        {
	        	// return true to consume press or false to pass it on
	        	passImage(1);
	        	return true;  
	        } 
	        return super.onKeyDown( keyCode, event ); 
	    }
	   
	 
	 /** Gestos / swipes
	  * 
	  */
	   
	 class MyGestureDetector extends SimpleOnGestureListener {
	        @Override
	        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	            try {
	                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
	                    return false;
	                // right to left swipe
	                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                    self.passImage(1);
	                    return true;
	                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                    self.passImage(-1);
	                    return true;
	                }
	            } catch (Exception e) {
	                // nothing
	            }
	            return false;
	        }

	    }
}
