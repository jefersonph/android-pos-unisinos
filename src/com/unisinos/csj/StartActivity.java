package com.unisinos.csj;

import com.unisinos.csj.helper.DBHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

public class StartActivity extends Activity {
	protected DBHelper dbHelper;
	protected SQLiteDatabase db;
	protected boolean status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().hide();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		ProgressDialog dialog = ProgressDialog.show(StartActivity.this, null, "Loading", false, true);
		dialog.setCancelable(false);		
		
		dbHelper = new DBHelper(this);
		db = dbHelper.getReadableDatabase();
		
		Cursor c = db.query("Status", new String[]{"User"}, null, null, null, null, null);
		while(c.moveToNext()){
			if(c.getInt(c.getColumnIndex("User")) == 1 ){
				status = true;
			}			
		}
		Log.i("status", "" + status);
		
		db.close();
		
		if(status){
			dialog.dismiss();
			Intent intent = new Intent(StartActivity.this, MainActivity.class);
		    startActivity(intent);			 
		}else{
			dialog.dismiss(); 
			Intent intent = new Intent(StartActivity.this, Login.class);
			startActivity(intent);
		}
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

}
