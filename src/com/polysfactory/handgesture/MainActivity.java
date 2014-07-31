//Google Glass Team - Wellesley HCI Summer 2014
//Grace Hu and Lily Chen
//credits to thorikawa and pavlonator


package com.polysfactory.handgesture;

import java.io.File;
import java.util.List;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.polysfactory.handgesture.HandGestureDetector.HandGestureListener;


public class MainActivity extends Activity implements CvCameraViewListener2, HandGestureListener {

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private Mat mFrame;
    private NativeBridge mNativeDetector;
    private CameraBridgeViewBase mOpenCvCameraView;
    private ViewGroup mTargetImagePreview;

    private ViewPager mViewPager;
    private ViewGroup mContainer;
    private HandGestureDetector mHandGestureDetector;
    private GestureDetector mGestureDetector; // Gesture detector used to present the options menu.
    private AudioManager mAudioManager; // Audio manager used to play system sound effects. 
    private final Handler mHandler = new Handler();
    
    private static int pos = 0;
    protected static boolean showmultcards = false; //should be a toggle in the menu
    protected static int skip = 0; //change to 1 if showmultcards is true
    protected static int resume = 0; //for resuming after enable/disable
    
    protected static String spokenText = "";// Voice action.
  
    //nod THURS
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener mySensorEventListener;
    private float initialZ = -999.0f;
    private long lastSensorReadTimeStamp = 0L;
    private final long SCAN_PERIOD = 1000L; //milliseconds, originally 500
    
    //bookmark
    protected static int checked = Color.CYAN;
    protected static int bookmark = -1;
    
    /** Listener that displays the options menu when the touchpad is tapped. */
   private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
        	Log.i("Grace", "in onGesture");
        	//opens up menu with Switch Protocol, Capture Marker, and Show Multiple Cards options        	
            if (gesture == Gesture.TAP || gesture == Gesture.LONG_PRESS) {
                mAudioManager.playSoundEffect(Sounds.TAP);
                openOptionsMenu();
                return true;
              //moves to the next step of the protocol
            } else if(gesture == Gesture.SWIPE_RIGHT){
            	Log.i("Grace", "onGesture RIGHT");
            	onRightMove();
            	return true;
            	//moves to the previous step of the protocol
            } else if(gesture == Gesture.SWIPE_LEFT){
            	Log.i("Grace", "onGesture LEFT");
            	onLeftMove();
            	return true;	
            	//bookmark the current step
            }else if(gesture == Gesture.SWIPE_UP){
            	//if current page is already bookmarked, then unbookmark it
            	if (bookmark == mViewPager.getCurrentItem()) {
            		bookmark = -1;
            		mViewPager.setBackgroundColor(Color.GRAY);
            	}
            	else {//otherwise, bookmark current page
            		bookmark = mViewPager.getCurrentItem();
            		mViewPager.setBackgroundColor(Color.CYAN);
            	}
            	Log.i("Lily", "bookmarked: " + bookmark);
            	
            	//saves bookmark even after exiting
            	SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
            	SharedPreferences.Editor editor = sharedPref.edit();

            	editor.putInt(getString(R.string.bookmarked_page), bookmark);
            	editor.commit();
            }
            return false;                          
        }
    };
    
    @Override
  //stuff inherent to gestures
    public boolean onGenericMotionEvent(MotionEvent event) {
    	if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return super.onGenericMotionEvent(event);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//initialize some listeners
    	mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
        
        Log.i("Grace", "called onCreate");
        super.onCreate(savedInstanceState);
        
      //defualt features of program
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.marker_tracking);  
        
        //initialize some camera detection stuff
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCameraIndex(Constants.CAMERA_INDEX);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setMaxFrameSize(320, 192);
        mTargetImagePreview = (ViewGroup) findViewById(R.id.marker_preview_container);
        mContainer = (ViewGroup) findViewById(R.id.container);

        mViewPager = new ViewPager(this);
        mViewPager.setBackgroundColor(Color.GRAY);
        mViewPager.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });
        mViewPager.setAdapter(new SamplePagerAdapter(this));
        
        //open to page where we last left off
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int defaultValue = 0;
        int currentSlide = sharedPref.getInt(getString(R.string.current_slide), defaultValue);
        boolean isEnabled = sharedPref.getBoolean(getString(R.string.is_enabled), false); //for showing multiple cards
        showmultcards = isEnabled;
        if (isEnabled) 
        	skip = 1;
        mViewPager.setCurrentItem(currentSlide); //open to slide we last left on

        
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContainer.addView(mViewPager, 2, lp);

        mHandGestureDetector = new HandGestureDetector();
        mHandGestureDetector.setHandGestureListener(this);
      
        //dealing with opening page where we left off
        mViewPager.setOnPageChangeListener(new OnPageChangeListener(){
        	@Override
            public void onPageScrollStateChanged(int position) {}
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                pos = position;
                SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
            	SharedPreferences.Editor editor = sharedPref.edit();

            	editor.putInt(getString(R.string.current_slide), pos);
            	editor.commit();
            }     
        });
               
       //nod
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
            	
                long now = System.currentTimeMillis();
                float currentZ = event.values[2];
                if (initialZ < -990.0f) {
                    initialZ = currentZ;
                    lastSensorReadTimeStamp = now;
                } else {
                	
                    if ((now - lastSensorReadTimeStamp) > SCAN_PERIOD) {
                    	
                        float diffZ = initialZ - currentZ;
                        float absDiffZ = Math.abs(diffZ);
                        if (absDiffZ > 2 && diffZ > 0) { //if head nods up, open up voice
                        	mSensorManager.unregisterListener(mySensorEventListener); //added WED. stop detection once voice detection starts.
                        	Log.i("Grace", "deactivate sensor");
                        	Log.i("Grace", "openVoiceDictationActivity");
                            openVoiceDictationActivity();
                        }
                    }
                }
            }
        };
        mSensorManager.registerListener(mySensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        
        //ADDED THURS - NOD
        mSensorManager.unregisterListener(mySensorEventListener);
        
      //camera stuff for hand gestures
        if (mNativeDetector != null) {
            mNativeDetector.stop();
            mNativeDetector = null;
        }
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    //gets repeatedly called
    public void onResume() {
    	super.onResume();
    	Log.i("Grace","onResume AGAIN"); 
    	mSensorManager.unregisterListener(mySensorEventListener); //prevent too much nodding detection
    	Log.i("Grace","deact"); 
    	
        setupTargetImagePreview();
        mNativeDetector = new NativeBridge(Marker.HAND.getFilePath(this));//searches for hand movements
        mNativeDetector.start();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.enableView();
        }
            
    	processVoiceAction(); //takes an action depending on user's voice input
    	
        mSensorManager.registerListener(mySensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL); //reactivates sensor after voice dication is over
    }

    private void processVoiceAction()
    {
    	Log.i("Grace", "in processVoiceAction"); 
    	Log.i("Grace", "spokenwords in MAINACT: " + spokenText);
    	
        if(spokenText.equals("next")){//move to next step
        	onRightMove();
        	Log.i("Grace", "moving right MAINACT");
        }else if(spokenText.equals("previous")){//previous step
        	onLeftMove();        	
        	Log.i("Grace", "moving left MAINACT");
        }//otherwise stay in the same step
        	
        spokenText = "";   
    }
    
    public void onDestroy() {
    	Log.i("Grace","onDestroy"); 
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }
    
    //methods default for camera viewing
    public void onCameraViewStarted(int width, int height) {
        mFrame = new Mat();
        mNativeDetector.setSize(width, height, width, height);
    }

    public void onCameraViewStopped() {
        mFrame.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mFrame = inputFrame.rgba();
        if (Constants.FLIP) 
            Core.flip(mFrame, mFrame, 1);

        MatOfRect handRectMat = new MatOfRect();
        if (mNativeDetector != null) {
            mNativeDetector.process(mFrame, handRectMat);
        }
        List<Rect> handRectList = handRectMat.toList(); 
        if (handRectList.size() > 0) {
            mHandGestureDetector.handle(handRectList.get(0));
        } else {
            mHandGestureDetector.handle(null); //causes the not found command. this should be default when no hand motion
        }
        return mFrame;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            // Stop the preview and release the camera.
            // Execute your logic as quickly as possible
            // so the capture happens quickly.
            // TODO
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    //trying to inflate directly from menu.xml
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override//what do when any of the following menu items are chosen
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.capture_marker:
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.putExtra(CaptureActivity.EXTRA_KEY_MARKER, Marker.HAND);
            startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
    	
          //have a case for each set of protocols. Set to the title page of the protocol
    	case R.id.moclo_menu: //the first protocol
    		mHandler.post(new Runnable() {
    			@Override
    			public void run() {
    				mViewPager.setCurrentItem(0, true);//opens directly to card 0, which is the start of the first protocol
    			}
    		});
    		return true;		
    	
    	case R.id.trans_menu: //2nd protocol
    		mHandler.post(new Runnable() {
    			@Override
    			public void run() {
    				mViewPager.setCurrentItem(19, true); //19 is the where the protocol starts
    			}
    		});
    		return true;
    		
    	case R.id.minip_menu://3rd protocol
    		mHandler.post(new Runnable() {
    			@Override
    			public void run() {
    				mViewPager.setCurrentItem(34, true);
    			}
    		});
    		return true;
    		
    	case R.id.broth_menu://4th protocol
    		mHandler.post(new Runnable() {
    			@Override
    			public void run() {
    				mViewPager.setCurrentItem(62, true);
    			}
    		});
    		return true;
    	
    	case R.id.agar_menu: //5th protocol
    		mHandler.post(new Runnable() {
    			@Override
    			public void run() {
    				mViewPager.setCurrentItem(73, true); 
    			}
    		});
    		return true;   
    		
    	case R.id.enable: //when multiple cards feature is enabled
			showmultcards = true;
			skip = 1;
			int pos = mViewPager.getCurrentItem();
			//these are the positions of all the title cards
			if (pos >= 0) resume = 0;
			if (pos >= 19) resume = 19;
			if (pos >= 34) resume = 34;
			if (pos >= 62) resume = 62;
			if (pos >= 73) resume = 73;	
			
			//to save whether show mult was enabled
			SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        	SharedPreferences.Editor editor = sharedPref.edit();

        	editor.putBoolean(getString(R.string.is_enabled), true);
        	editor.commit();
        	
        	//after enabling, it will revert to the title card to avoid error
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mViewPager.setCurrentItem(resume, true); 
			}});

			return true;
		
    	case R.id.disable://disables the multiple cards feature. only shows one step at a time
			showmultcards = false;
			skip = 0;
			int pos1 = mViewPager.getCurrentItem();
			//all the title pages
			if (pos1 >= 0) resume = 0;
			if (pos1 >= 19) resume = 19;
			if (pos1 >= 34) resume = 34;
			if (pos1 >= 62) resume = 62;
			if (pos1 >= 73) resume = 73;	
			
			resume = 0;
			
			//to save whether show mult was disabled
			SharedPreferences sharedPref1 = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        	SharedPreferences.Editor editor1 = sharedPref1.edit();

        	editor1.putBoolean(getString(R.string.is_enabled), false);
        	editor1.commit();
        	
        	//after disabling, it will revert to the title card to avoid error
			mHandler.post(new Runnable() {
				@Override
    			public void run() {
    				mViewPager.setCurrentItem(resume, true); 
    			}});

			return true;

    	default:
    		return false; 
    	}	
    }

    @Override
  //called automatically when startActivity is called
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CAPTURE_IMAGE == requestCode) {
            // TODO reload marker
        }
        
        //voice detection
        if (requestCode == VoiceDemoConstants.SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            MainActivity.spokenText = results.get(0);
            Log.i("Grace","spoken text: " + MainActivity.spokenText);
        }
    }
    
  //set up camera view for hand gestures
    private void setupTargetImagePreview() {
        mTargetImagePreview.removeAllViews();
        for (final Marker marker : Marker.values()) {
            File markerFile = marker.getFile(this);
            if (!markerFile.exists()) {
                IOUtils.copy(this, marker.getDefaultRes(), markerFile);
            }

            Mat mat = Highgui.imread(markerFile.getAbsolutePath());
            Bitmap markerBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, markerBitmap);
            ImageView imageView = new ImageView(this);
            imageView.setMaxWidth(100);
            imageView.setMaxHeight(100);
            int width = 100;
            int height = 100;
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
            imageView.setLayoutParams(parms);
            imageView.setScaleType(ScaleType.CENTER_INSIDE);
            imageView.setImageBitmap(markerBitmap);
            mTargetImagePreview.addView(imageView);
        }
    }

   
  //starts the dictation activity - see VoiceDictationActivity.java
    private void openVoiceDictationActivity()
    {
    	Log.i("Grace", "in openVoiceDictationActivity");
        Intent intent = new Intent(MainActivity.this, VoiceDictationActivity.class);
        startActivity(intent);  
    }
    
    @Override
    //called when movement detected. 
  //called when hand gesture movement detected. - gesturing to the left
    public void onLeftMove() {
        Log.d(L.TAG, "onLeftMove");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	int selected = mViewPager.getCurrentItem();
                if (selected > 0) {
                	//if current page is title card + 1, then only move back one page
                	if (selected == 1 || selected == 20 || selected == 35 || selected == 63 || selected == 74)
                		mViewPager.setCurrentItem(selected - 1, true);
                	//else move either 1 or 2 pages back, depending on value of skip
                	else mViewPager.setCurrentItem(selected - 1 - skip, true);
                	
                	//background
                	if (showmultcards){
                    	if (selected -1 == bookmark || selected - 2 == bookmark) 
                    		mViewPager.setBackgroundColor(Color.CYAN);
                    }
                    else if (selected -1 == bookmark) 
                    	mViewPager.setBackgroundColor(Color.CYAN);
                    else mViewPager.setBackgroundColor(Color.GRAY);
                }
            }
        });
    }

    @Override
    //called when movement is detected //called when movement is detected, hand gesture to the right
    public void onRightMove() {
        Log.d(L.TAG, "onRightMove HERE");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	int selected = mViewPager.getCurrentItem();
            	//if current page is a title card, then only move forward by 1
                if (selected < mViewPager.getAdapter().getCount()) {
                	if (selected == 0 || selected == 19 || selected == 34 || selected == 62 || selected == 73)
                		mViewPager.setCurrentItem(selected + 1, true);
                	//else move either 1 or 2 forward depending on value of skip
                	else mViewPager.setCurrentItem(selected + 1 + skip, true);
                	
                	//background colors
                	if (showmultcards){
                    	if (selected + 1 == bookmark || selected + 2 == bookmark) 
                    		mViewPager.setBackgroundColor(Color.CYAN);
                    }
                    else if (selected +1 == bookmark) 
                    	mViewPager.setBackgroundColor(Color.CYAN);
                    else 
                    	mViewPager.setBackgroundColor(Color.GRAY);
                }
            }
        });
    }
}
