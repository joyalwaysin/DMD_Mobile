package com.nagainfomob.slider;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Text;

import com.nagainfomob.database.DatabaseHandler;
import com.nagainfomob.smartShowroom.AmbienceBedroom;
import com.nagainfomob.smartShowroom.AmbienceDrawingroom;
import com.nagainfomob.smartShowroom.AmbienceLivingRoom01;
import com.nagainfomob.smartShowroom.AmbienceLivingRoom02;
import com.nagainfomob.smartShowroom.AmbienceBathroom;
import com.nagainfomob.smartShowroom.AmbienceKitchen;
import com.nagainfomob.smartShowroom.CustomPatternActivity;
import com.nagainfomob.smartShowroom.EditAmbienceWall;
import com.nagainfomob.smartShowroom.AmbienceExterior;
import com.nagainfomob.smartShowroom.GlobalVariables;
import com.nagainfomob.smartShowroom.PatternLibraryActivity;
import com.nagainfomob.smartShowroom.R;
import com.nagainfomob.smartShowroom.ViewPatternActivity;
import com.nagainfomob.update.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.color;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PatternGridAdapter extends BaseAdapter implements Checkable {

	private Activity activity;
	private ArrayList<HashMap<String, String>> dbResult;
	private LayoutInflater inflater = null;
	private static final String KEY_BRAND = "pro_brand";
	private static final String KEY_NAME = "pro_name";
	private static final String KEY_DIMEN = "pro_dimen";
	private static final String KEY_TYPE = "pro_type";
	public PatternimgNameInterface delegate = null;
	String[] imageUrls;
	// private Bitmap bitmap;
	// private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult,
			PatternLibraryActivity patternLibraryActivity) {
		this.activity = activity;
		this.dbResult = dbResult;
		this.delegate = patternLibraryActivity;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		initimageloader();
		refresh();

	}

	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult) {
		this.activity = activity;
		this.dbResult = dbResult;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		initimageloader();
		refresh();

	}

	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult,
			CustomPatternActivity patternLibraryActivity) {
		this.activity = activity;
		this.dbResult = dbResult;
		this.delegate = patternLibraryActivity;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		initimageloader();
		refresh();

	}

	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult,
			EditAmbienceWall editAmbienceWall) {
		this.activity = activity;
		this.dbResult = dbResult;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		this.delegate = editAmbienceWall;
		initimageloader();
		refresh();

	}

	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult,
			AmbienceLivingRoom01 editAmbienceWall) {
		this.activity = activity;
		this.dbResult = dbResult;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		this.delegate = editAmbienceWall;
		initimageloader();
		refresh();

	}
	
	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult,
			AmbienceLivingRoom02 editAmbienceWall) {
		this.activity = activity;
		this.dbResult = dbResult;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		this.delegate = editAmbienceWall;
		initimageloader();
		refresh();

	}
	
	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult,
			AmbienceExterior editAmbienceWall) {
		this.activity = activity;
		this.dbResult = dbResult;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		this.delegate = editAmbienceWall;
		initimageloader();
		refresh();

	}
	
	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult,
			AmbienceBathroom editAmbienceWall) {
		this.activity = activity;
		this.dbResult = dbResult;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		this.delegate = editAmbienceWall;
		initimageloader();
		refresh();

	}
	
	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult,
			AmbienceKitchen editAmbienceWall) {
		this.activity = activity;
		this.dbResult = dbResult;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		this.delegate = editAmbienceWall;
		initimageloader();
		refresh();

	}
	
	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult,
			AmbienceBedroom editAmbienceWall) {
		this.activity = activity;
		this.dbResult = dbResult;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		this.delegate = editAmbienceWall;
		initimageloader();
		refresh();

	}
	
	public PatternGridAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult,
			AmbienceDrawingroom editAmbienceWall) {
		this.activity = activity;
		this.dbResult = dbResult;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader = new ImageLoader(this.activity.getApplicationContext());
		this.delegate = editAmbienceWall;
		initimageloader();
		refresh();

	}
	
	

	private void initimageloader() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	void refresh() {

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dbResult.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class ViewHolder {
		ImageView imgPattern;
		ProgressBar progressBar;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// // TODO Auto-generated method stub
		// // CheckableLayout l;
		// ViewHolder holder;
		// // if (null == convertView) {
		// // convertView.invalidate();
		// View v = convertView;
		// // v.refreshDrawableState();
		// if (v == null) {
		// holder = new ViewHolder();
		//
		// v = inflater.inflate(R.layout.pattern, parent, false);
		// holder.imgPattern = (ImageView) v.findViewById(R.id.pattern_holder);
		// // imgPattern.setBackgroundColor(Color.RED);
		//
		// v.setTag(holder);
		// // v.setOnClickListener(this);
		//
		// } else {
		// holder = (ViewHolder) v.getTag();
		// }
		String path = GlobalVariables.getPatternRootPath()
				+ dbResult.get(position).get(KEY_BRAND) + "/"
				+ dbResult.get(position).get(KEY_NAME);
		// bitmap = BitmapFactory.decodeFile(path);
		// holder.imgPattern.setImageBitmap(bitmap);

		// /////////////////////////

		CheckableLayout l;
		ImageView i;
		if (convertView == null) {
			i = new ImageView(activity);
			i.setScaleType(ScaleType.FIT_CENTER);
			i.setLayoutParams(new ViewGroup.LayoutParams(160, 200));
			// i.setPadding(10, 10, 10, 10);

			l = new CheckableLayout(activity);
			l.setLayoutParams(new GridView.LayoutParams(
					GridView.LayoutParams.WRAP_CONTENT,
					GridView.LayoutParams.WRAP_CONTENT));
			l.setPadding(5, 5, 5, 5);
			l.addView(i);
			// i.setTag(position);
			// i.setTag(position);
		} else {
			l = (CheckableLayout) convertView;
			i = (ImageView) l.getChildAt(0);
		}

		// i.setImageBitmap(bitmap);
		ImageLoader.getInstance().displayImage("file:///"+path, i, options);

		//
		// // //////////////////////////

		/*
		 * try { imageLoader.DisplayImage(path, holder.imgPattern); } catch
		 * (Exception e) { Log.e("image lazy loader error !", e.getMessage()); }
		 */
		// l.setOnLongClickListener(new OnLongClickListener() {
		//
		// @Override
		// public boolean onLongClick(View v) {
		// // TODO Auto-generated method stub
		// // deletepattern(v,null,null,null,null,null);
		// // String path = GlobalVariables.getPatternRootPath()
		// // + dbResult.get(position).get(KEY_BRAND) + "/"
		// // + dbResult.get(position).get(KEY_NAME);
		// // String tname = dbResult.get(position).get(KEY_NAME);
		// // String tBrand = dbResult.get(position).get(KEY_BRAND);
		// // String tDim = null;
		// // if (dbResult.get(position).get(KEY_DIMEN) != null) {
		// // tDim = dbResult.get(position).get(KEY_DIMEN)
		// // .replaceAll("mm", "");
		// // }
		// // String tType = dbResult.get(position).get(KEY_TYPE);
		// // // showTileDialog(v, path, tname.replace(".jpg", ""), tBrand,
		// // // tDim, tType);
		// // Log.d("TAG", tType+" "+tname+" "+tBrand);
		// //
		// // if (activity.getClass().equals(ViewPatternActivity.class)) {
		// //
		// // deletepattern(v,null,tname,tBrand,null,null);
		// // }
		// if (v.isSelected())
		// v.setSelected(false);
		// else
		// v.setSelected(true);
		// return true;
		//
		// }
		// });

		l.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (activity.getClass().equals(ViewPatternActivity.class)) {
					String path = GlobalVariables.getPatternRootPath()
							+ dbResult.get(position).get(KEY_BRAND) + "/"
							+ dbResult.get(position).get(KEY_NAME);
					String tname = dbResult.get(position).get(KEY_NAME);
					String tBrand = dbResult.get(position).get(KEY_BRAND);
					String tDim = null;
					if (dbResult.get(position).get(KEY_DIMEN) != null) {
						tDim = dbResult.get(position).get(KEY_DIMEN)
								.replaceAll("mm", "");
					}
					String tType = dbResult.get(position).get(KEY_TYPE);
					showTileDialog(v, path, tname.replace(".jpg", ""), tBrand,
							tDim, tType);

				} else if (activity.getClass().equals(
						PatternLibraryActivity.class)) {
					try {
						String path = GlobalVariables.getPatternRootPath()
								+ dbResult.get(position).get(KEY_BRAND) + "/"
								+ dbResult.get(position).get(KEY_NAME);
						String tname = dbResult.get(position).get(KEY_NAME);
						String tBrand = dbResult.get(position).get(KEY_BRAND);
						String tDim = null;
						if (dbResult.get(position).get(KEY_DIMEN) != null) {
							tDim = dbResult.get(position).get(KEY_DIMEN)
									.replaceAll("mm", "");
						}
						String tType = dbResult.get(position).get(KEY_TYPE);
						callDeligate(path, tDim, tBrand, tType);
					} catch (Exception e) {
						Log.e("error", e.getMessage());
					}
				} else if (activity.getClass().equals(
						CustomPatternActivity.class)) {
					String path = GlobalVariables.getPatternRootPath()
							+ dbResult.get(position).get(KEY_BRAND) + "/"
							+ dbResult.get(position).get(KEY_NAME);
					String tname = dbResult.get(position).get(KEY_NAME);
					String tBrand = dbResult.get(position).get(KEY_BRAND);
					String tDim = null;
					if (dbResult.get(position).get(KEY_DIMEN) != null) {
						tDim = dbResult.get(position).get(KEY_DIMEN)
								.replaceAll("mm", "");
					}
					String tType = dbResult.get(position).get(KEY_TYPE);
					callDeligate(path, tDim, tBrand, tType);

				} else if (activity.getClass().equals(EditAmbienceWall.class)) {

					String path = GlobalVariables.getPatternRootPath()
							+ dbResult.get(position).get(KEY_BRAND) + "/"
							+ dbResult.get(position).get(KEY_NAME);
					String tname = dbResult.get(position).get(KEY_NAME);
					String tBrand = dbResult.get(position).get(KEY_BRAND);
					String tDim = null;
					if (dbResult.get(position).get(KEY_DIMEN) != null) {
						tDim = dbResult.get(position).get(KEY_DIMEN)
								.replaceAll("mm", "");
					}
					String tType = dbResult.get(position).get(KEY_TYPE);
					callDeligate(path, tDim, tBrand, tType);

				} else if (activity.getClass()
						.equals(AmbienceLivingRoom01.class)) {

					String path = GlobalVariables.getPatternRootPath()
							+ dbResult.get(position).get(KEY_BRAND) + "/"
							+ dbResult.get(position).get(KEY_NAME);
					String tname = dbResult.get(position).get(KEY_NAME);
					String tBrand = dbResult.get(position).get(KEY_BRAND);
					String tDim = null;
					if (dbResult.get(position).get(KEY_DIMEN) != null) {
						tDim = dbResult.get(position).get(KEY_DIMEN)
								.replaceAll("mm", "");
					}
					String tType = dbResult.get(position).get(KEY_TYPE);
					callDeligate(path, tDim, tBrand, tType);

				} else if (activity.getClass()
						.equals(AmbienceLivingRoom02.class)) {

					String path = GlobalVariables.getPatternRootPath()
							+ dbResult.get(position).get(KEY_BRAND) + "/"
							+ dbResult.get(position).get(KEY_NAME);
					String tname = dbResult.get(position).get(KEY_NAME);
					String tBrand = dbResult.get(position).get(KEY_BRAND);
					String tDim = null;
					if (dbResult.get(position).get(KEY_DIMEN) != null) {
						tDim = dbResult.get(position).get(KEY_DIMEN)
								.replaceAll("mm", "");
					}
					String tType = dbResult.get(position).get(KEY_TYPE);
					callDeligate(path, tDim, tBrand, tType);

				} else if (activity.getClass()
						.equals(AmbienceBathroom.class)) {

					String path = GlobalVariables.getPatternRootPath()
							+ dbResult.get(position).get(KEY_BRAND) + "/"
							+ dbResult.get(position).get(KEY_NAME);
					String tname = dbResult.get(position).get(KEY_NAME);
					String tBrand = dbResult.get(position).get(KEY_BRAND);
					String tDim = null;
					if (dbResult.get(position).get(KEY_DIMEN) != null) {
						tDim = dbResult.get(position).get(KEY_DIMEN)
								.replaceAll("mm", "");
					}
					String tType = dbResult.get(position).get(KEY_TYPE);
					callDeligate(path, tDim, tBrand, tType);

				} else if (activity.getClass()
						.equals(AmbienceExterior.class)) {

					String path = GlobalVariables.getPatternRootPath()
							+ dbResult.get(position).get(KEY_BRAND) + "/"
							+ dbResult.get(position).get(KEY_NAME);
					String tname = dbResult.get(position).get(KEY_NAME);
					String tBrand = dbResult.get(position).get(KEY_BRAND);
					String tDim = null;
					if (dbResult.get(position).get(KEY_DIMEN) != null) {
						tDim = dbResult.get(position).get(KEY_DIMEN)
								.replaceAll("mm", "");
					}
					String tType = dbResult.get(position).get(KEY_TYPE);
					callDeligate(path, tDim, tBrand, tType);

				} else if (activity.getClass()
						.equals(AmbienceKitchen.class)) {

					String path = GlobalVariables.getPatternRootPath()
							+ dbResult.get(position).get(KEY_BRAND) + "/"
							+ dbResult.get(position).get(KEY_NAME);
					String tname = dbResult.get(position).get(KEY_NAME);
					String tBrand = dbResult.get(position).get(KEY_BRAND);
					String tDim = null;
					if (dbResult.get(position).get(KEY_DIMEN) != null) {
						tDim = dbResult.get(position).get(KEY_DIMEN)
								.replaceAll("mm", "");
					}
					String tType = dbResult.get(position).get(KEY_TYPE);
					callDeligate(path, tDim, tBrand, tType);

				} else if (activity.getClass()
						.equals(AmbienceBedroom.class)) {

					String path = GlobalVariables.getPatternRootPath()
							+ dbResult.get(position).get(KEY_BRAND) + "/"
							+ dbResult.get(position).get(KEY_NAME);
					String tname = dbResult.get(position).get(KEY_NAME);
					String tBrand = dbResult.get(position).get(KEY_BRAND);
					String tDim = null;
					if (dbResult.get(position).get(KEY_DIMEN) != null) {
						tDim = dbResult.get(position).get(KEY_DIMEN)
								.replaceAll("mm", "");
					}
					String tType = dbResult.get(position).get(KEY_TYPE);
					callDeligate(path, tDim, tBrand, tType);

				} else if (activity.getClass()
						.equals(AmbienceDrawingroom.class)) {

					String path = GlobalVariables.getPatternRootPath()
							+ dbResult.get(position).get(KEY_BRAND) + "/"
							+ dbResult.get(position).get(KEY_NAME);
					String tname = dbResult.get(position).get(KEY_NAME);
					String tBrand = dbResult.get(position).get(KEY_BRAND);
					String tDim = null;
					if (dbResult.get(position).get(KEY_DIMEN) != null) {
						tDim = dbResult.get(position).get(KEY_DIMEN)
								.replaceAll("mm", "");
					}
					String tType = dbResult.get(position).get(KEY_TYPE);
					callDeligate(path, tDim, tBrand, tType);

				}

			}
		});
		l.refreshDrawableState();
		// v.setTag(holder);
		return l;
	}

	public void callDeligate(String path, String Dim, String brand, String type) {
		this.delegate.patternName(path, Dim, brand, type);
	}

	public void showTileDialog(View v, String spath, String tname,
			String tbrand, String tDimn, String tType) {
		// TODO Auto-generated method stub

		Dialog vDialog = new Dialog(activity);
		vDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		vDialog.setContentView(R.layout.show_pattern_dialog);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(vDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

		ImageView iView = (ImageView) vDialog.findViewById(R.id.show_pattern);
		TextView tName = (TextView) vDialog.findViewById(R.id.tileName);
		TextView tBrand = (TextView) vDialog.findViewById(R.id.brandname);
		TextView tDim = (TextView) vDialog.findViewById(R.id.tDim);
		TextView ttype = (TextView) vDialog.findViewById(R.id.tType);
		tName.setText(tname);
		tBrand.setText(tbrand);
		tDim.setText(tDimn);
		ttype.setText(tType);
		iView.setScaleType(ScaleType.CENTER_INSIDE);
		Bitmap bitmap = BitmapFactory.decodeFile(spath);
		iView.setImageBitmap(bitmap);
		// vDialog.getWindow().setAttributes(lp);
		if (vDialog.isShowing()) {
			vDialog.dismiss();
		}
		vDialog.show();

	}

	// private void deletepattern(View v, String spath, String tname,
	// String tbrand, String tDimn, String tType) {
	// final Dialog delDialog = new Dialog(activity);
	// delDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// delDialog.setContentView(R.layout.delete_pattern);
	// WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	// lp.copyFrom(delDialog.getWindow().getAttributes());
	// lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
	// lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	// Button delbtn = (Button) delDialog.findViewById(R.id.but_delete);
	// Button cancel = (Button) delDialog.findViewById(R.id.but_cancel);
	// final String name = tname;
	// final String brand = tbrand;
	// delbtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if (brand.equalsIgnoreCase("cam click")) {
	// DatabaseHandler db = new DatabaseHandler(activity);
	// db.deletepattern(name.replace(".jpg", ""));
	// activity.recreate();
	// } else {
	// Toast.makeText(activity,
	// "Only Custom Patterns Can be Deleted!!", 1000)
	// .show();
	// }
	// delDialog.dismiss();
	// }
	// });
	//
	// cancel.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// delDialog.cancel();
	//
	// // delDialog.dismiss();
	// }
	// });
	//
	// delDialog.show();
	// }

	public void setSelected(int position) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public boolean onLongClick(View v) {
	// // TODO Auto-generated method stub
	// // int titemId = Integer.valueOf(v.getTag().toString());
	// // String imagePath= dbResult.get(titemId).get(KEY_BRAND) + "/"
	// // + dbResult.get(titemId).get(KEY_NAME);
	// //
	// // String tileSize = dbResult.get(titemId).get(KEY_DIMEN);
	// // tileSize = tileSize.replaceAll("mm", "");
	// //
	// //// Log.d("TaG", titemId+" "+imagePath+" "+tileSize);
	// //
	// //
	// // if (activity.getClass().equals(ViewPatternActivity.class)) {
	// //
	// // deletepattern(v,null,null,null,null,null);
	// // }
	// //
	//
	//
	//
	// return false;
	//
	// }
	public class CheckableLayout extends FrameLayout implements Checkable {
		private boolean mChecked;

		public CheckableLayout(Context context) {
			super(context);
		}

		@SuppressWarnings("deprecation")
		public void setChecked(boolean checked) {
			mChecked = checked;
			setBackgroundDrawable(checked ? new ColorDrawable(Color.BLUE)
					: null);
		}

		public boolean isChecked() {
			return mChecked;
		}

		public void toggle() {
			setChecked(!mChecked);
		}

	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setChecked(boolean checked) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggle() {
		// TODO Auto-generated method stub

	}
}
