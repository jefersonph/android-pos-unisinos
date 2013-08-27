package com.unisinos.csj.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Http  {
	
	public JSONObject getJSON(String type, String gamertag){
		StringBuilder builder = new StringBuilder();
		
		try{
			String content = "";
			HttpClient http = new DefaultHttpClient();
			HttpGet get = new HttpGet(getDomain(type)+gamertag.replace(" ", "%20"));				
			HttpResponse response = http.execute(get);				
			HttpEntity entity = response.getEntity();
			InputStream  input = entity.getContent();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while((line = reader.readLine()) != null){
				builder.append(line);
			}
			
			content = builder.toString();
			
			JSONObject json = new JSONObject(content);
			
			return json;
			
		}catch (ClientProtocolException e){
			Log.e("HTTP", "ClientProtocolException");
			e.printStackTrace();
		}catch (IOException e){
			Log.e("HTTP", "IOException");
			e.printStackTrace();
		}catch (JSONException e){
			Log.e("HTTP", "JSONException");
			e.printStackTrace();
		}
		
		return null;
			
	}
	
	protected String getDomain(String type){
		if(type.equals("games")){
			return "https://xboxapi.com/games/";
		}else if(type.equals("friends")){
			return "https://xboxapi.com/friends/";
		}else if(type.equals("profile")){
			return "https://xboxapi.com/profile/";
		}else{
			return "";
		}
			
	}
	public Bitmap getBitmap(String src){
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap bm = BitmapFactory.decodeStream(input);
			return bm;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public int postWS(String url){
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://unisinos-jph.rhcloud.com"+url);
		StringBuilder builder = new StringBuilder();
	
		Log.i("URL POST", ""+httppost.getURI());
	
		try {
			String content = "";
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream  input = entity.getContent();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while((line = reader.readLine()) != null){
				builder.append(line);
			}
			
			content = builder.toString();
			
			JSONObject json = new JSONObject(content);
			
			Log.i("CreateUser", json.getString("id"));
			
			return Integer.parseInt(json.getString("id"));
			
		}catch (ClientProtocolException e){
			Log.e("HTTP", "ClientProtocolException");
			e.printStackTrace();
		}catch (IOException e){
			Log.e("HTTP", "IOException");
			e.printStackTrace();
		}catch (JSONException e){
			Log.e("HTTP", "JSONException");
			e.printStackTrace();
		}
		return -1;
	}	
	
	public void deleteLoan(String url){
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://unisinos-jph.rhcloud.com"+url);
		StringBuilder builder = new StringBuilder();
	
		Log.i("URL POST", ""+httppost.getURI());
	
		try {

			HttpResponse response = httpclient.execute(httppost);
			
		}catch (ClientProtocolException e){
			Log.e("HTTP", "ClientProtocolException");
			e.printStackTrace();
		}catch (IOException e){
			Log.e("HTTP", "IOException");
			e.printStackTrace();
		}
	}	
	
	public JSONArray getLoans(String id){
		StringBuilder builder = new StringBuilder();
		
		try{
			String content = "";
			HttpClient http = new DefaultHttpClient();
			HttpGet get = new HttpGet("http://unisinos-jph.rhcloud.com/users/"+id+"/lends.json");				
			HttpResponse response = http.execute(get);				
			HttpEntity entity = response.getEntity();
			InputStream  input = entity.getContent();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while((line = reader.readLine()) != null){
				builder.append(line);
			}
			
			content = builder.toString();
			
			JSONArray json = new JSONArray(content);
			
			return json;
			
		}catch (ClientProtocolException e){
			Log.e("HTTP", "ClientProtocolException");
			e.printStackTrace();
		}catch (IOException e){
			Log.e("HTTP", "IOException");
			e.printStackTrace();
		}catch (JSONException e){
			Log.e("HTTP", "JSONException");
			e.printStackTrace();
		}
		
		return null;
			
	}
}
