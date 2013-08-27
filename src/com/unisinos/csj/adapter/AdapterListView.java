package com.unisinos.csj.adapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.unisinos.csj.R;
import com.unisinos.csj.R.id;
import com.unisinos.csj.R.layout;
import com.unisinos.csj.helper.Http;
import com.unisinos.csj.model.ItemListView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterListView extends BaseAdapter {
	
	private LayoutInflater mInflater;
    private ArrayList<ItemListView> itens;
    
    protected Http http;
    
    public AdapterListView(Context context, ArrayList<ItemListView> itens) {
        this.itens = itens;
        mInflater = LayoutInflater.from(context);
    }
	
	@Override
	public int getCount() {
		return itens.size();
	}

	@Override
	public Object getItem(int position) {
		return itens.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ItemSuporte itemHolder;
		if (view == null) {
			view = mInflater.inflate(R.layout.list_row, null);
			itemHolder = new ItemSuporte();
			itemHolder.title = ((TextView) view.findViewById(R.id.listTitle));
			itemHolder.subTitle = ((TextView) view.findViewById(R.id.listSubTitle));
			itemHolder.image = ((ImageView) view.findViewById(R.id.listImage));
			view.setTag(itemHolder);
		} else {
			itemHolder = (ItemSuporte) view.getTag();
		}
		
		ItemListView item = itens.get(position);
		itemHolder.title.setText(item.getTitle());
		itemHolder.subTitle.setText(item.getSubTitle());
		itemHolder.position = position;
		
		new getImage(position, itemHolder).execute(item.getImage());
		
		return view;		

	}
	
	private class ItemSuporte {
		ImageView image;
		TextView subTitle;
		TextView title;
		int position;
	}
	
	public class getImage extends AsyncTask<String, Void, Bitmap>{
		private ItemSuporte holder;
		private int mposition;
		public getImage(int position, ItemSuporte holder){
			this.holder = holder;
			this.mposition = position;
			
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			http = new Http();
			return http.getBitmap(params[0]);
		}
		
		protected void onPostExecute(Bitmap result){
			if(mposition == holder.position)
				holder.image.setImageBitmap(result);
		}
		
		
	}
	
}
