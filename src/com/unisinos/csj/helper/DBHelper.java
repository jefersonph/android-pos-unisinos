package com.unisinos.csj.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "csj.db";
	private static final String GAMES_TABLE = "CREATE TABLE IF NOT EXISTS Games (" +  
					" ID INTEGER PRIMARY KEY, " +
					" Name TEXT, " + 
					" Achievements INTEGER, " +
					" Score INTEGER, " +
					" ImageSmall TEXT " +
					")";
	private static final String FRIENDS_TABLE = "CREATE TABLE IF NOT EXISTS Friends (" +  
					" GamerTag TEXT PRIMARY KEY, " +
					" GamerScore INTEGER, " + 
					" ImageLarge TEXT " +
					")";
	private static final String PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS Profile (" +  
					" GamerTag TEXT PRIMARY KEY, " +
					" ImageLarge TEXT, " + 
					" ImageSmall TEXT, " +
					" ImageBody TEXT, " +
					" Gamerscore TEXT, " +
					" Reputation INTEGER, " +
					" RecentGame1 TEXT, " +
					" RecentGame2 TEXT, " +
					" RecentGame3 TEXT " +
					")"; 
	private static final String STATUS_TABLE = " CREATE TABLE IF NOT EXISTS Status (" +
					" Games INTEGER, " +
					" Friends INTEGER, " +
					" Profile INTEGER, " +
					" User INTEGER, " +
					" Loan INTEGER " +
					")";

	private static final String USER_TABLE = " CREATE TABLE IF NOT EXISTS User (" +
					" GamerTag TEXT , " +
					" email TEXT, " +
					" ID INTEGER " +
					")";
			
	private static final String LOAN_TABLE = " CREATE TABLE IF NOT EXISTS Loan (" +
					" game_id TEXT, " +
					" game_to TEXT, " +
					" ID INTEGER " +
					")";
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(GAMES_TABLE);
		db.execSQL(FRIENDS_TABLE);
		db.execSQL(PROFILE_TABLE);
		db.execSQL(STATUS_TABLE);
		db.execSQL(USER_TABLE);		
		db.execSQL(LOAN_TABLE);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 Log.i("version", Integer.toString(oldVersion) + ">" + Integer.toString(newVersion));		
	}
	

}
