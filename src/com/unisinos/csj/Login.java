package com.unisinos.csj;

import com.unisinos.csj.helper.DBHelper;
import com.unisinos.csj.helper.Http;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {
	protected DBHelper dbHelper;
	protected SQLiteDatabase db;
	protected EditText gamertag;
	protected EditText email;
	protected Http http;
	protected ProgressDialog dialog;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getActionBar().hide();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Button entrar = (Button) findViewById(R.id.entrarLogin);
		gamertag = (EditText) findViewById(R.id.gamertag);
		email = (EditText) findViewById(R.id.email);
		
		dbHelper = new DBHelper(this);
		
		
		entrar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("login", "entrar");
				
				ContentValues values = new ContentValues();
				db = dbHelper.getWritableDatabase();
				
				values.put("GamerTag", gamertag.getText().toString());
				values.put("email", email.getText().toString());
				db.insert("User", null, values);
				
				values.clear();
				values.put("User", 1);
				db.insert("Status", null, values);
								
				db.close();
				
				new createUser().execute(gamertag.getText().toString(), email.getText().toString());				
				 
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.start, menu);
		
		return true;
	}

	public class createUser extends AsyncTask<String, Void, Integer>{
		protected String gamertag;
		protected String email;
		@Override
		protected Integer doInBackground(String... params) {
			this.gamertag = params[0];
			this.email = params[1];
					
			http = new Http();
			int result = http.postWS("/users.json?user[gamertag]="+params[0]+"&user[email]="+params[1]);
			
			
			return result;
		}
		
		
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(Login.this, null, "Carregando...", false, true);
			dialog.setCancelable(false);
			
			//super.onPreExecute();
		}



		protected void onPostExecute(Integer id){
			ContentValues values = new ContentValues();
			
			db = dbHelper.getWritableDatabase();
			
			values.put("ID", id);
			
			db.update("User", values, "GamerTag=?", new String[]{gamertag});
			db.close();
			
			dialog.dismiss();
			
			Intent intent = new Intent(Login.this, MainActivity.class);
		    startActivity(intent);			
		}

		
		

	}
}
