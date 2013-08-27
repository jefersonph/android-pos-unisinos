package com.unisinos.csj.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.unisinos.csj.R;
import com.unisinos.csj.adapter.AdapterListView;
import com.unisinos.csj.helper.DBHelper;
import com.unisinos.csj.helper.Http;
import com.unisinos.csj.model.ItemListView;

public class Games extends SherlockFragment implements ActionBar.TabListener, OnItemClickListener {
	private Fragment mFragment;
	private ListView listView;
    private AdapterListView adapterListView;
    private ArrayList<ItemListView> itens;
	protected DBHelper dbHelper;
	protected boolean status = false;
	protected Http http;
	protected String gamertag = "";
	protected ProgressDialog dialog;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.games);
		http = new Http();
		
		listView = (ListView) getSherlockActivity().findViewById(R.id.GamesList);
        listView.setOnItemClickListener(this);
        
        dbHelper = new DBHelper(getSherlockActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = db.query("Status", new String[]{"Games"}, null, null, null, null, null);
		while(c.moveToNext()){
			if(c.getInt(c.getColumnIndex("Games")) == 1 ){
				status = true;
			}			
		}
		Log.i("status", "" + status);
		
		Cursor d = db.query("User", new String[]{"GamerTag"}, null, null, null, null, null);
		while(d.moveToNext()){
			this.gamertag = d.getString(d.getColumnIndex("GamerTag"));
		}
		
		db.close();
		
		if(status){
			new parseDB().execute();			 
		}else{
			new getGames().execute(gamertag);
		}
		
    }

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mFragment = new Games();
		ft.add(android.R.id.content, mFragment);
		ft.attach(mFragment);
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(mFragment);
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
	private class getGames extends AsyncTask<String, Void, JSONObject>{

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getSherlockActivity(), null, "Carregando...", false, true);
			dialog.setCancelable(false);
			
		}
		
		@Override
		protected JSONObject doInBackground(String... params) {

			return http.getJSON("games", params[0]);
		}
		
		protected void onPostExecute(JSONObject result){
			itens = new ArrayList<ItemListView>();
			ContentValues values = new ContentValues();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			try {
				JSONArray games = result.getJSONArray("Games");
				
				for(int i=0; i < games.length(); i++){
					JSONObject game = games.getJSONObject(i);
					JSONObject progress = game.getJSONObject("Progress");
					JSONObject boxart = game.getJSONObject("BoxArt");
					
					Log.i("JSON", "NOME: " + game.getString("Name")); 
					ItemListView item = new ItemListView(game.getString("Name"), "Achievements: " + progress.getInt("Achievements") , boxart.getString("Small"));
					itens.add(item);
					
					values.put("ID", game.getInt("ID"));
					values.put("Name", game.getString("Name"));
					values.put("Achievements", progress.getInt("Achievements"));
					values.put("Score", progress.getInt("Score"));
					values.put("ImageSmall", boxart.getString("Small"));
					
					db.insert("Games", null, values);
					values.clear();
				}
				
				dialog.dismiss();
				
				adapterListView = new AdapterListView(getSherlockActivity(), itens);
		        listView.setAdapter(adapterListView);
		        listView.setCacheColorHint(Color.TRANSPARENT);
		        
		        
		        values.put("Games",  1);
		        db.insert("Status", null, values);
		        
		        values.clear();
		        db.close();
				
			} catch (JSONException e) {
				Log.e("Games", "Error");
				db.close();
				e.printStackTrace();
			}
		}		
			
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        ItemListView item = (ItemListView) adapterListView.getItem(arg2);
        Log.i("TAB", item.getTitle());      
        
    }
	
	public class parseDB extends AsyncTask<Void, Void, ArrayList>{
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getSherlockActivity(), null, "Carregando...", false, true);
			dialog.setCancelable(false);
			
		}
		
		@Override
		protected ArrayList doInBackground(Void... params) {
			
			itens = new ArrayList<ItemListView>();
	     	SQLiteDatabase db = dbHelper.getReadableDatabase();
			
			Cursor c = db.query("Games", new String[]{"ID", "Name", "Achievements", "Score", "ImageSmall"}, null, null, null, null, null);
			while(c.moveToNext()){
				ItemListView item = new ItemListView(c.getString(c.getColumnIndex("Name")), "Achievements: " + c.getInt(c.getColumnIndex("Achievements"))  , c.getString(c.getColumnIndex("ImageSmall")));
				itens.add(item);
			}
			
			return itens;
			
		}
		
		protected void onPostExecute(ArrayList result){
			dialog.dismiss();
			adapterListView = new AdapterListView(getSherlockActivity(), result);
	        listView.setAdapter(adapterListView);
	        listView.setCacheColorHint(Color.TRANSPARENT);
	        
		}
		

	}

}
