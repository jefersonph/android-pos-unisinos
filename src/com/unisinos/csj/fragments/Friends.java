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
import android.support.v4.app.FragmentManager;
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

public class Friends extends SherlockFragment implements ActionBar.TabListener, OnItemClickListener {
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
		getActivity().setContentView(R.layout.friends);		
		http = new Http();

		
		listView = (ListView) getSherlockActivity().findViewById(R.id.FriendsList);
        listView.setOnItemClickListener(this);
        
        dbHelper = new DBHelper(getSherlockActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = db.query("Status", new String[]{"Friends"}, null, null, null, null, null);
		while(c.moveToNext()){
			if(c.getInt(c.getColumnIndex("Friends")) == 1 ){
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
			new parseDB().execute();			 
		}else{
			new getFriends().execute(gamertag);
		}
		
	
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mFragment = new Friends();
		ft.add(android.R.id.content, mFragment);
		ft.attach(mFragment);
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(mFragment);
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
	
	
	
	private class getFriends extends AsyncTask<String, Void, JSONObject>{

		@Override
		protected JSONObject doInBackground(String... params) {
			
			return http.getJSON("friends", params[0]);
		}
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getSherlockActivity(), null, "Carregando...", false, true);
			dialog.setCancelable(false);
			
		}
		
		protected void onPostExecute(JSONObject result){
			itens = new ArrayList<ItemListView>();
			ContentValues values = new ContentValues();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			try {
				
				JSONArray friends = result.getJSONArray("Friends");
				
				for(int i=0; i < friends.length(); i++){
					JSONObject friend = friends.getJSONObject(i);
					
					Log.i("JSON", "NOME: " + friend.getString("GamerTag")); 

					ItemListView item = new ItemListView(friend.getString("GamerTag"), "GamerScore: " + friend.getString("GamerScore") , friend.getString("LargeGamerTileUrl"));
					
					values.put("GamerTag", friend.getString("GamerTag"));
					values.put("GamerScore", friend.getString("GamerScore"));
					values.put("ImageLarge",friend.getString("LargeGamerTileUrl"));
					
					itens.add(item);
					
					db.insert("Friends", null, values);
					values.clear();
				}
				dialog.dismiss();
				
				adapterListView = new AdapterListView(getSherlockActivity(), itens);
		        listView.setAdapter(adapterListView);
		        listView.setCacheColorHint(Color.TRANSPARENT);
		        
		        values.put("Friends",  1);
		        db.insert("Status", null, values);
		        
		        values.clear();
		        db.close();
		        
				
			} catch (JSONException e) {
				Log.e("Friends", "Error");
				db.close();
				e.printStackTrace();
				
			}
			
		}
		
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        ItemListView item = (ItemListView) adapterListView.getItem(arg2);
        Log.i("TAB", item.getTitle());        
     
        FragmentManager fragmentManager2 = getFragmentManager();
		FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
		FriendAccount fragment2 = new FriendAccount(item.getTitle());
		fragmentTransaction2.addToBackStack("xyz");
		fragmentTransaction2.add(android.R.id.content, fragment2);
		fragmentTransaction2.commit();
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
			
			Cursor c = db.query("Friends", new String[]{"GamerTag", "GamerScore", "ImageLarge"}, null, null, null, null, null);
			while(c.moveToNext()){
				ItemListView item = new ItemListView(c.getString(c.getColumnIndex("GamerTag")), "GamerScore: " + c.getInt(c.getColumnIndex("GamerScore"))  , c.getString(c.getColumnIndex("ImageLarge")));
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
