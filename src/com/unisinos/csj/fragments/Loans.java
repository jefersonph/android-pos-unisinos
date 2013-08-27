package com.unisinos.csj.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.unisinos.csj.Login;
import com.unisinos.csj.R;
import com.unisinos.csj.StartActivity;
import com.unisinos.csj.helper.DBHelper;

public class Loans extends SherlockFragment implements ActionBar.TabListener {
	private Fragment mFragment;
	protected Button seusEmprestimos;
	protected Button pedirEmprestimos;
	protected Button perfil;
	protected Button emprestar;
	protected Button sair;
	protected DBHelper dbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.loans);
		pedirEmprestimos = (Button) getSherlockActivity().findViewById(R.id.pedirEmprestado);
		seusEmprestimos = (Button) getSherlockActivity().findViewById(R.id.seusEmprestimos);
		perfil = (Button) getSherlockActivity().findViewById(R.id.perfil);
		emprestar = (Button) getSherlockActivity().findViewById(R.id.emprestar);
		sair = (Button) getSherlockActivity().findViewById(R.id.btnLogout);
		
		dbHelper = new DBHelper(getSherlockActivity());
		
		pedirEmprestimos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager2 = getFragmentManager();
				FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
				FriendsRequestGames fragment2 = new FriendsRequestGames();
				fragmentTransaction2.addToBackStack("xyz");
				fragmentTransaction2.add(android.R.id.content, fragment2);
				fragmentTransaction2.commit();
			}
		});
		
		seusEmprestimos.setOnClickListener( new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager2 = getFragmentManager();
				FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
				YourLoans fragment2 = new YourLoans();
				fragmentTransaction2.addToBackStack("xyz");
				fragmentTransaction2.add(android.R.id.content, fragment2);
				fragmentTransaction2.commit();
			}
		});
		
		perfil.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager2 = getFragmentManager();
				FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
				Account fragment2 = new Account();
				fragmentTransaction2.addToBackStack("xyz");
				fragmentTransaction2.add(android.R.id.content, fragment2);
				fragmentTransaction2.commit();
			}
		});
		
		emprestar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager2 = getFragmentManager();
				FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
				LoanGames fragment2 = new LoanGames();
				fragmentTransaction2.addToBackStack("xyz");
				fragmentTransaction2.add(android.R.id.content, fragment2);
				fragmentTransaction2.commit();
			}
		});
		
		sair.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				
				ProgressDialog dialog = ProgressDialog.show(getSherlockActivity(), null, "Loading", false, true);
				dialog.setCancelable(false);	
				dialog.show();
				
				db.delete("Games", null, null);
				db.delete("Friends", null, null);
				db.delete("Profile", null, null);
				db.delete("Status", null, null);
				db.delete("User", null, null);
				db.delete("Loan", null, null);
				
				db.close();
				
				dialog.dismiss(); 
				Intent intent = new Intent(getSherlockActivity(), Login.class);
				startActivity(intent);
			}
		});
		
		
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mFragment = new Loans();
		ft.add(android.R.id.content, mFragment);
		ft.attach(mFragment);
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(mFragment);
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {}

}
