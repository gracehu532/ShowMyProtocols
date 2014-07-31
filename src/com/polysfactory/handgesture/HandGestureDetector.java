package com.polysfactory.handgesture;

import org.opencv.core.Rect;

import android.util.Log;

public class HandGestureDetector {

    interface HandGestureListener {
        void onLeftMove();

        void onRightMove();
    }

    enum State {
        LEFT, RIGHT, MIDDLE, LOST
    }

    private static final long TIME_THRESHOLD = 800;
    private static final int FRAME_WIDTH = 320;
    private static final int PADDING = 90;
    private static final int MAX_SEQ_LOST = 2;
    private State lastState = State.LOST; 
    private long lastStateTime;
    private HandGestureListener mHandGestureListener;
    private long lostCount = 0;

    private State currentState(Rect rect) {
        int centerX = rect.x + rect.width / 2; 
     
        if (centerX > FRAME_WIDTH - PADDING) {
        	Log.d(L.TAG, "return state RIGHT");  
            return State.RIGHT; //ORIGINAL
        } else if (centerX < PADDING) {
        	Log.d(L.TAG, "return state LEFT"); 
            return State.LEFT;
        }
        Log.d(L.TAG, "MIDDLE"); 
        return State.MIDDLE; //ORIGINAL	
    }

    public void setHandGestureListener(HandGestureListener handGestureListener) {
        mHandGestureListener = handGestureListener;
    }

    public void handle(Rect rect) {
        if (rect == null) {
            Log.d(L.TAG, "not found");
            lostCount++;
            //Log.i("Grace", "lostCount: " + lostCount);
            if (lostCount > MAX_SEQ_LOST) 
                lastState = State.LOST;
            
            return; 
        }
  
        lostCount = 0;

        State state = currentState(rect);
        long currentTime = System.currentTimeMillis();
        //Log.d(L.TAG, "currentState:" + state.name() + ", lastState:" + lastState.name() + ", time=" + currentTime); 
        switch (state) {
        case MIDDLE:
        case LOST:
            // do nothing
            break;
        case LEFT:
            if ((lastState == State.RIGHT || lastState == State.MIDDLE) && (currentTime - lastStateTime) < TIME_THRESHOLD) {
                // move left
                if (mHandGestureListener != null) {
                	Log.i("Grace", "calling onRightMove NOW");
                    mHandGestureListener.onRightMove(); 
                }
            }
            lastState = State.LEFT;
            lastStateTime = currentTime;
            break;
        case RIGHT:
            if ((lastState == State.LEFT || lastState == State.MIDDLE) && (currentTime - lastStateTime) < TIME_THRESHOLD) {
                // move right
                if (mHandGestureListener != null) {
                	Log.i("Grace", "calling onleftMove NOW");
                	mHandGestureListener.onLeftMove(); 
                }
            }
            lastState = State.RIGHT;
            lastStateTime = currentTime;
            break;
        }
    }
}