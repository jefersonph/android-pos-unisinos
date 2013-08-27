package com.unisinos.csj.fragments;

import java.util.ArrayList;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.unisinos.csj.R;
import com.unisinos.csj.adapter.AdapterListView;
import com.unisinos.csj.helper.DBHelper;
import com.unisinos.csj.helper.Http;
import com.unisinos.csj.model.ItemListView;

public class LoanGameToFriend extends SherlockFragment implements ActionBar.TabListener, OnItemClickListener {
	private Fragment mFragment;
	private ListView listView;
    private AdapterListView adapterListView;
    private ArrayList<ItemListView> itens;
	protected DBHelper dbHelper;
	protected Http http;
	protected int id;
	protected String game;
	protected ProgressDialog dialog;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.friends);
		
		listView = (ListView) getSherlockActivity().findViewById(R.id.FriendsList);
        listView.setOnItemClickListener(this);
        
        dbHelper = new DBHelper(getSherlockActivity());
        
        new parseDB().execute();			 
		
    }
	
	public LoanGameToFriend(String game) {
		super();
		this.game = game;
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mFragment = new LoanGameToFriend(game);
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
        
        new loanGame().execute(item.getTitle(), game);
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
				ItemListView item = new ItemListView(c.getString(c.getColumnIndex("GamerTag")), "EMPRESTAR", c.getString(c.getColumnIndex("ImageLarge")));
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
	
	public class loanGame extends AsyncTask<String, Void, Integer>{
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getSherlockActivity(), null, "Carregando...", false, true);
			dialog.setCancelable(false);
			
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor d = db.query("User", new String[]{"ID"}, null, null, null, null, null);
			while(d.moveToNext()){
				id = d.getInt(d.getColumnIndex("ID"));
			}
			
			http = new Http();
			return http.postWS("/users/"+id+"/lends.json?lend[to]="+params[0].replace(" ", "%20")+"&lend[game_id]="+params[1]);
			
		}
		
		protected void onPostExecute(Integer result){
			dialog.dismiss();
			Log.i("emprestimo", "id: " + result);
			Toast.makeText(getSherlockActivity(), "Emprestimo efetuado.", Toast.LENGTH_SHORT).show();
		}
		
	}

}
