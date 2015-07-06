package com.nagainfo.smartShowroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;

public class AlbumAcivity extends Activity{
	static Cursor imageCursor;
	static int columnIndex;
	GridView gridView;
	Intent in;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pics_frm_storage);
		
		gridView=(GridView)findViewById(R.id.gallery_imview);
		
		String[] imgColumnID = {MediaStore.Images.Thumbnails._ID};
	    Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
	    imageCursor = managedQuery(uri, imgColumnID, null, null, 
	                    MediaStore.Images.Thumbnails.IMAGE_ID);
	    columnIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
	    
	    gridView.setAdapter(new ImageAdapter(getApplicationContext()));
	    
	    gridView.setOnItemClickListener(new OnItemClickListener() {

	        public void onItemClick(AdapterView<?> parent, View v, int position,
	                long id) {
	            String[] dataLocation = {MediaStore.Images.Media.DATA};
	            //String []dataLocation = {Environment.getExternalStorageDirectory().getAbsolutePath()};
	            imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,dataLocation, null, null, null);
	            columnIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	            imageCursor.moveToPosition(position);
	            
	            String imgPath = imageCursor.getString(columnIndex);
	            in = new Intent();
	            in.putExtra("id", imgPath);
	            setResult(2,in);
	            finish();
	            
	        }//onItemClickListener
	    });
	    
	}
	public static class ImageAdapter extends BaseAdapter{
	    private Context context;
	    //Album1Activity act = new Album1Activity();
	    public ImageAdapter(Context context){
	        this.context = context;
	    }//ImageAdapter
	    public int getCount() {
	        // TODO Auto-generated method stub
	        return imageCursor.getCount();
	    }//getCount

	    public Object getItem(int position) {
	        // TODO Auto-generated method stub
	        return position;
	    }//getItem

	    public long getItemId(int position) {
	        // TODO Auto-generated method stub
	        return position;
	    }//getItemId

	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView iv;
	        if(convertView == null){
	            iv = new ImageView(context);
	            imageCursor.moveToPosition(position);
	            int imageID = imageCursor.getInt(columnIndex);
	            iv.setImageURI(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
	                            "" +imageID));
	            iv.setScaleType(ImageView.ScaleType.FIT_XY);
	            iv.setPadding(5, 5, 5, 5);
	            iv.setLayoutParams(new GridView.LayoutParams(100, 100));
	        }//if
	        else{
	            iv = (ImageView)convertView;
	        }//else
	        return iv;
	    }//getView

	}//ImageAdapter
}
