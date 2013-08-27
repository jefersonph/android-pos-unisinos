package com.unisinos.csj.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.unisinos.csj.helper.Http;
import com.unisinos.csj.model.ItemListView;

public class FriendGames extends SherlockFragment implements ActionBar.TabListener, OnItemClickListener {
	private Fragment mFragment;
	private ListView listView;
    private AdapterListView adapterListView;
    private ArrayList<ItemListView> itens;
    private Http http;
    protected String gamertag;
	protected ProgressDialog dialog;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.games);
		
		http = new Http();
		
		listView = (ListView) getSherlockActivity().findViewById(R.id.GamesList);
        listView.setOnItemClickListener(this);
        
		new getGames().execute();		
    }

	public FriendGames(String gamertag) {
		super();
		this.gamertag = gamertag;
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mFragment = new FriendGames(gamertag);
		ft.add(android.R.id.content, mFragment);
		ft.attach(mFragment);
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(mFragment);
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
	private class getGames extends AsyncTask<Void, Void, JSONObject>{
		
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getSherlockActivity(), null, "Carregando...", false, true);
			dialog.setCancelable(false);
			
		}
		
		@Override
		protected JSONObject doInBackground(Void... params) {
		
			return http.getJSON("games", gamertag);
		}
		
		protected void onPostExecute(JSONObject result){
			itens = new ArrayList<ItemListView>();
			
			try {
				JSONArray games = result.getJSONArray("Games");
				
				for(int i=0; i < games.length(); i++){
					JSONObject game = games.getJSONObject(i);
					JSONObject boxart = game.getJSONObject("BoxArt");
					
					Log.i("JSON", "NOME: " + game.getString("Name")); 
					ItemListView item = new ItemListView(game.getString("Name"), "PEDIR EMPRESTADO", boxart.getString("Small"));
					itens.add(item);
					

				}
				dialog.dismiss();
				
				adapterListView = new AdapterListView(getSherlockActivity(), itens);
		        listView.setAdapter(adapterListView);
		        listView.setCacheColorHint(Color.TRANSPARENT);
		        		       
			} catch (JSONException e) {
				Log.e("Games", "Error");
				e.printStackTrace();
			}
		}		
			
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        ItemListView item = (ItemListView) adapterListView.getItem(arg2);
        Log.i("TAB", item.getTitle());        
        
        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Pode me emprestar o jogo " + item.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, "Olá, \n Pode me emprestar o jogo " + item.getTitle() + " ?\n Baixe o aplicativo http://bit.ly/compartilheSeuJogo \n Obrigado.");
        startActivity(intent);
    }
	
}
