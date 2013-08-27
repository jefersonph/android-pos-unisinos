package com.unisinos.csj.model;

import android.graphics.Bitmap;

public class ItemListView {

	private String title;
	private String subTitle;
	private String image;

	public ItemListView(String title, String subTitle, String image) {
		this.title = title;
		this.subTitle = subTitle;
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	
}
