package com.unisinos.csj.fragments;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import com.unisinos.csj.R;
import com.unisinos.csj.helper.Http;

public class FriendAccount extends SherlockFragment implements ActionBar.TabListener {
	private Fragment mFragment;
	protected ImageView profileBody;
	protected TextView profileName;
	protected TextView profileScore;
	protected TextView profileReputation;
	protected TextView profileGame1;
	protected TextView profileGame2;
	protected TextView profileGame3;
	protected String gamertag;
	protected Http http;
	protected ProgressDialog dialog;

	
	public FriendAccount(String gamertag) {
		super();
		this.gamertag = gamertag;
	}

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
		
		new getProfile().execute();
		
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mFragment = new FriendAccount(gamertag);
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
			Http http = new Http();
			return http.getJSON("profile", gamertag);
		}

		protected void onPostExecute(JSONObject result){

			
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
				
				new getImageProfile().execute(gamertag.replace(" ", "%20"));
				
				
			} catch (JSONException e) {
				Log.e("Profile", "Error");
				e.printStackTrace();
			}
		}		
			
	}
	
	private class getImageProfile extends AsyncTask<String, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(String... params) {
			http = new Http();
			return http.getBitmap("https://avatar-ssl.xboxlive.com/avatar/" + params[0] + "/avatar-body.png");
		}
		
		protected void onPostExecute(Bitmap result){
			profileBody.setImageBitmap(result);
		}
		
	}

	
}
