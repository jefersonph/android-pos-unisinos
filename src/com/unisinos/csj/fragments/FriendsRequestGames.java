package com.unisinos.csj.fragments;

import java.util.ArrayList;

import android.app.ProgressDialog;
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
import com.unisinos.csj.model.ItemListView;

public class FriendsRequestGames extends SherlockFragment implements ActionBar.TabListener, OnItemClickListener {
	private Fragment mFragment;
	private ListView listView;
    private AdapterListView adapterListView;
    private ArrayList<ItemListView> itens;
	protected DBHelper dbHelper;
	protected boolean status = false;
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

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mFragment = new Friends();
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
     
        FragmentManager fragmentManager2 = getFragmentManager();
		FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
		FriendGames fragment2 = new FriendGames(item.getTitle());
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
				ItemListView item = new ItemListView(c.getString(c.getColumnIndex("GamerTag")), "VER JOGOS" , c.getString(c.getColumnIndex("ImageLarge")));
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
