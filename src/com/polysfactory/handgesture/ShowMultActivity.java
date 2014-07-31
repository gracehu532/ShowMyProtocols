package com.polysfactory.handgesture;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//feel free to delete this file. unnecessary
public class ShowMultActivity extends Activity{
	private MenuItem enable, disable;

	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	}
	
	
	public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(L.TAG, "called onCreateOptionsMenu");
        menu.add(R.string.enable);
        menu.add(R.string.disable);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item){
	if (item == enable){
		Log.i("Grace","Enable multiple cards");
		MainActivity.skip = 1;
		return true;
		}
	
	else if (item == disable) {
		Log.i("Grace","Disable multiple cards");
		MainActivity.skip = 0;
		return true;
	}
	else return false;
	}
	

}
