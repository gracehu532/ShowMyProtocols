package com.polysfactory.handgesture;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;

public class VoiceDictationActivity extends Activity{

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override //initializes voice recognition 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Grace","VoiceDictationActivity onCreate() called.");
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); 
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak NEXT, PREVIOUS, or CANCEL.");//you can change text
        startActivityForResult(intent, VoiceDemoConstants.SPEECH_REQUEST);//opens the dictation screen
        Log.i("Grace","VoiceDictationActivity onCreate() end.");
    }
  
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	Log.i("Grace", "in onActResult VOICE DICT");
    	
    	//the spoken text gathered here is accessible in MainActivity for processing
        if (requestCode == VoiceDemoConstants.SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            MainActivity.spokenText = results.get(0);
            Log.i("Grace","spoken text: " + MainActivity.spokenText);   
        }
        Log.i("Grace", "resultCode" + resultCode); 
        Log.i("Grace", "end Voice Dictation act"); 
        VoiceDictationActivity.this.finish();//added sat - brings me back to protocols once i finish dictating
    }
}