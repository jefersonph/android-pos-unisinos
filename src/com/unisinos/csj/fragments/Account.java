package com.unisinos.csj.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.unisinos.csj.Login;
import com.unisinos.csj.R;
import com.unisinos.csj.helper.DBHelper;
import com.unisinos.csj.helper.Http;

public class Account extends SherlockFragment implements ActionBar.TabListener {
	private Fragment mFragment;
	protected DBHelper dbHelper;
	protected boolean status = false;
	protected ImageView profileBody;
	protected TextView profileName;
	protected TextView profileScore;
	protected TextView profileReputation;
	protected TextView profileGame1;
	protected TextView profileGame2;
	protected TextView profileGame3;
	protected Http http;
	protected String gamertag = "";
	protected ProgressDialog dialog;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.account);
				
		profileBody = (ImageView) getSherlockActivity().findViewById(R.id.profileBody);
		profileName = (TextView) getSherlockActivity().findViewById(R.id.profileName);
		profileScore = (TextView) getSherlockActivity().findViewById(R.id.profileScore);
		profileReputation = (TextView) getSherlockActivity().findViewById(R.id.profileReputation);
		profileGame1 = (TextView) getSherlockActivity().findViewById(R.id.profileGame1);
		profileGame2 = (TextView) getSherlockActivity().findViewById(R.id.profileGame2);
		profileGame3 = (TextView) getSherlockActivity().findViewById(R.id.profileGame3);
			
		dbHelper = new DBHelper(getSherlockActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = db.query("Status", new String[]{"Profile"}, null, null, null, null, null);
		while(c.moveToNext()){
			if(c.getInt(c.getColumnIndex("Profile")) == 1 ){
				status = true;
			}			
		}
		
		Cursor d = db.query("User", new String[]{"GamerTag"}, null, null, null, null, null);
		while(d.moveToNext()){
			this.gamertag = d.getString(d.getColumnIndex("GamerTag"));
		}
		
		Log.i("status", "" + status);
		
		db.close();
		
		if(status){
			parseDB();			 
		}else{
			new getProfile().execute(gamertag);
		}
		
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mFragment = new Account();
		ft.add(android.R.id.content, mFragment);
		ft.attach(mFragment);
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(mFragment);
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
	private class getProfile extends AsyncTask<String, Void, JSONObject>{
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getSherlockActivity(), null, "Carregando...", false, true);
			dialog.setCancelable(false);
			
		}
		
		@Override
		protected JSONObject doInBackground(String... params) {
			http = new Http();
			return http.getJSON("profile", params[0]);
		}

		protected void onPostExecute(JSONObject result){
			ContentValues values = new ContentValues();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			List<String> lista = new ArrayList<String>();
			
			dialog.dismiss();
			
			try {
				JSONObject player = result.getJSONObject("Player");
				JSONArray recentGames = result.getJSONArray("RecentGames");
				JSONObject avatar = player.getJSONObject("Avatar");
				
				profileName.setText(player.getString("Gamertag"));
				profileScore.setText("Gamerscore: " + player.getString("Gamerscore"));
				profileReputation.setText("Reputation: " + player.getString("Reputation"));
				
				for(int i=0; i < 3; i++){
					JSONObject recent = recentGames.getJSONObject(i);
					lista.add(recent.getString("Name"));
				}
				
				profileGame1.setText(lista.get(0));
				profileGame2.setText(lista.get(1));
				profileGame3.setText(lista.get(2));
				
				new getImageProfile().execute(gamertag);
				
				values.put("GamerTag", player.getString("Gamertag"));
				values.put("ImageLarge", ""); 
				values.put("ImageSmall", "");
				values.put("ImageBody", avatar.getString("Body"));
				values.put("Gamerscore", player.getInt("Gamerscore"));
				values.put("Reputation", player.getInt("Reputation"));
				values.put("RecentGame1", lista.get(0));
				values.put("RecentGame2", lista.get(1));
				values.put("RecentGame3", lista.get(2));
				
				db.insert("Profile", null, values);
				values.clear();
				
				values.put("Profile",  1);
			    db.insert("Status", null, values);
			        
			    values.clear();
			    db.close();
				
			} catch (JSONException e) {
				Log.e("Profile", "Error");
				db.close();
				e.printStackTrace();
			}
		}		
			
	}
	
	public void parseDB(){
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = db.query("Profile", new String[]{"GamerTag", "ImageLarge", "ImageSmall", "ImageBody", "Gamerscore", "Reputation", "RecentGame1", "RecentGame2", "RecentGame3"}, null, null, null, null, null);
		
		while(c.moveToNext()){
			profileName.setText(c.getString(c.getColumnIndex("GamerTag")));
			profileScore.setText("Gamerscore: " + c.getInt(c.getColumnIndex("Gamerscore")));
			profileReputation.setText("Reputation: " + c.getInt(c.getColumnIndex("Reputation")));
			profileGame1.setText(c.getString(c.getColumnIndex("RecentGame1")));
			profileGame2.setText(c.getString(c.getColumnIndex("RecentGame2")));
			profileGame3.setText(c.getString(c.getColumnIndex("RecentGame3")));
		}
		
		new getImageProfile().execute(gamertag);
		
	}
	
	private class getImageProfile extends AsyncTask<String, Void, Bitmap>{
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getSherlockActivity(), null, "Carregando...", false, true);
			dialog.setCancelable(false);
			
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			http = new Http();
			return http.getBitmap("https://avatar-ssl.xboxlive.com/avatar/" + params[0] + "/avatar-body.png");
		}
		
		protected void onPostExecute(Bitmap result){
			dialog.dismiss();
			profileBody.setImageBitmap(result);
		}
		
	}



}
