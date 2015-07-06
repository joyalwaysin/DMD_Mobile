package com.nagainfo.smartShowroom;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

public class GetXmlfromAsset {
	public String getXml(String path, Context context) {

		String xmlString = null;
		AssetManager am = context.getAssets();
		try {
			InputStream is = am.open(path);
			int length = is.available();
			byte[] data = new byte[length];
			is.read(data);
			xmlString = new String(data);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return xmlString;
	}
}
