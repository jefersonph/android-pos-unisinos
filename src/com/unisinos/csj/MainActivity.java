package com.unisinos.csj;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.unisinos.csj.fragments.Friends;
import com.unisinos.csj.fragments.Games;
import com.unisinos.csj.fragments.Loans;
import android.os.Bundle;



public class MainActivity extends SherlockFragmentActivity {
	// Declare Tab Variable
	Tab tab;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getSupportActionBar();
		
		actionBar.setDisplayShowHomeEnabled(false);
		
		actionBar.setDisplayShowTitleEnabled(false);
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	
		tab = actionBar.newTab().setTabListener(new Friends());
		tab.setText("Amigos");
		actionBar.addTab(tab);

		tab = actionBar.newTab().setTabListener(new Games());
		tab.setText("Jogos");
		actionBar.addTab(tab);

		tab = actionBar.newTab().setTabListener(new Loans());
		tab.setText("Conta");
		actionBar.addTab(tab);
		
	}
	
	
}
