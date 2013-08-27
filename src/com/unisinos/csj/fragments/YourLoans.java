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
import android.widget.Toast;
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

public class YourLoans extends SherlockFragment implements ActionBar.TabListener, OnItemClickListener {
	private Fragment mFragment;
	private ListView listView;
    private AdapterListView adapterListView;
    private ArrayList<ItemListView> itens;
	protected DBHelper dbHelper;
	protected boolean status = false;
	protected Http http;
	protected int user_id = 0;
	protected int id = 0;
	protected ProgressDialog dialog;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.your_loans);
		
		listView = (ListView) getSherlockActivity().findViewById(R.id.YourLoans);
        listView.setOnItemClickListener(this);
                
        dbHelper = new DBHelper(getSherlockActivity());
        
		
		new getLoans().execute();
    }

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mFragment = new YourLoans();
		ft.add(android.R.id.content, mFragment);
		ft.attach(mFragment);
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(mFragment);
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
		ItemListView item = (ItemListView) adapterListView.getItem(arg2);
        Log.i("TAB", item.getTitle());   
        String game = "";

        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("Loan", new String[]{"ID","game_id","game_to"}, null, null, null, null, null);
		while(c.moveToNext()){
			if(c.getString(c.getColumnIndex("game_id")).equals(item.getTitle())){
				game = c.getString(c.getColumnIndex("game_id"));
				id = c.getInt(c.getColumnIndex("ID"));
			}			
		}
        
		Log.i("TAB game: ", game);
		Log.i("TAB id: ", ""+id);
		
		new deleteLoan().execute(user_id, id);
        
    }
	
	public class getLoans extends AsyncTask<Void, Void, JSONArray>{

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getSherlockActivity(), null, "Carregando...", false, true);
			dialog.setCancelable(false);
			
		}

		@Override
		protected JSONArray doInBackground(Void... params) {
			
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			http = new Http();
			
			
			Cursor c = db.query("User", new String[]{"ID"}, null, null, null, null, null);
			while(c.moveToNext()){
				user_id = c.getInt(c.getColumnIndex("ID"));
			}
			
			Log.i("yourloans id", Integer.toString(user_id));
			
			return http.getLoans(Integer.toString(user_id));
			
		}
		protected void onPostExecute(JSONArray json){
			itens = new ArrayList<ItemListView>();
			ContentValues values = new ContentValues();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			Log.i("json loans", ""+  json.length());
			
			try {
				for(int i=0; i < json.length(); i++){
					JSONObject loan = json.getJSONObject(i);
					
					ItemListView item = new ItemListView(loan.getString("game_id"), "CANCELAR EMPRÉSTIMO: " + loan.getString("to"), getImageGame(loan.getString("game_id")));
					Log.i("item", loan.getString("game_id"));
					itens.add(item);
					
					values.put("ID", loan.getInt("id"));
					values.put("game_id", loan.getString("game_id"));
					values.put("game_to", loan.getString("to"));
					
				}	
				
				
				
				db.insert("Loan", null, values);
				values.clear();
				
				dialog.dismiss();
				
				adapterListView = new AdapterListView(getSherlockActivity(), itens);
		        listView.setAdapter(adapterListView);
		        listView.setCacheColorHint(Color.TRANSPARENT);
				
			}catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	

	public class deleteLoan extends AsyncTask<Integer, Void, String>{
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getSherlockActivity(), null, "Carregando...", false, true);
			dialog.setCancelable(false);
			
		}

		@Override
		protected String doInBackground(Integer... params) {
			http = new Http();
			http.deleteLoan("/users/"+params[0]+"/lends/"+params[1]+"/delete.json");
			return "";
		}
		
		protected void onPostExecute(String result){
			dialog.dismiss();
			Log.i("emprestimo", "Cancelado");
			Toast.makeText(getSherlockActivity(), "Emprestimo deletado.", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	public String getImageGame(String game){
		String gameImage = "";
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = db.query("Games", new String[]{"ImageSmall", "Name"}, null, null, null, null, null);
		while(c.moveToNext()){
			if(c.getString(c.getColumnIndex("Name")).equals(game)){
				gameImage = c.getString(c.getColumnIndex("ImageSmall"));
			}
		}	
		Log.i("img", gameImage);
		
		return gameImage;
		
	}
	
}
