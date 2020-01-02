package com.nagainfomob.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.model.PatternModel;
import com.nagainfomob.app.model.TileModel;
import com.nagainfomob.app.zipmanagement.Compress;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final String DB_NAME = "dmdDB";
	private static final int DB_VERSION = 7;
	private static final String DB_TABLE = "dmd";
	private static final String DB_PRO = "project";
	private static final String DB_SETTINGS = "settings";
	private static final String DB_PRO_SAVE = "saveproject";
	private static final String DB_PAT_SAVE = "savepattern";
	private static final String DB_PATRNS = "patterns";

	// Table fields
	private static final String KEY_ID = "pro_id";
	private static final String KEY_NAME = "pro_name";
	private static final String KEY_COLOR = "pro_color";
	private static final String KEY_DIMEN = "pro_dimen";
	private static final String KEY_TYPE = "pro_type";
	private static final String KEY_TECH = "tile_tech";
	private static final String KEY_BRAND_ID = "barnd_id";
	private static final String KEY_BRAND = "pro_brand";
	private static final String KEY_COMPANY = "pro_company";
	private static final String KEY_TILE_ID = "tile_id";
	private static final String KEY_CATEGORY_ID = "category_id";
	private static final String KEY_CATEGORY = "tile_category";
	private static final String KEY_TYPE_ID = "type_id";
	private static final String KEY_COLOR_CODE = "color_code";
	private static final String KEY_MODEL_NO = "model_no";
	private static final String KEY_TILE_TYPE = "tile_type";
	private static final String KEY_TILE_PRICE = "tile_price";
	private static final String KEY_IMAGE_URL = "image_url";
	private static final String KEY_IS_ACTIVE = "is_active";
	private static final String KEY_DOWN_STATUS = "download_status";

	//Settings
	public static final String ID = "id";
	public static final String DEVICE_TYPE = "devie_type";
	public static final String DEVICE_TOKEN = "device_token";
	public static final String FCM_TOKEN = "fcm_token";
	public static final String ACTIVE_TOKEN = "active_token";
	public static final String ACTIVE_USER = "active_user";
	public static final String DOWNLOAD_TIME_STAMP = "downoad_time_stamp";

	// Table project fields
	private static final String PROJ_ID = "proj_id";
	private static final String PROJ_NAME = "proj_name";
	private static final String PROJ_UNIT = "proj_unit";
	private static final String PROJ_LENGTH = "proj_len";
	private static final String PROJ_WIDTH = "proj_wid";
	private static final String PROJ_HEIGHT = "proj_hgt";
	private static final String PROJ_C = "proj_c";
	private static final String PROJ_D = "proj_d";

	// Table project save table fields
	private static final String SAVE_ID = "save_id";
	private static final String SAVE_PROJ_NAME = "proj_name";
	private static final String SAVE_WALL = "wall_name";
	private static final String SAVE_PATH = "image_path";

	// Table patter save fields
	private static final String PAT_ID = "pattern_id";
	private static final String PAT_NAME = "pattern_name";
	private static final String PAT_PATH = "pattern_path";

	// Table patters
	private static final String PATRN_ID = "pattern_id";
	private static final String PATRN_NAME = "pattern_name";
	private static final String PATRN_CATEGORY = "pattern_cat";
	private static final String PATRN_CAT_ID = "pattern_cat_id";
	private static final String PATRN_TYPE = "pattern_type";
	private static final String PATRN_TYPE_ID = "pattern_type_id";
	private static final String PATRN_DIMEN = "pattern_dimen";
	private static final String PATRN_PRICE = "pattern_price";
	private static final String PATRN_BRAND = "pattern_brand";
	private static final String PATRN_IS_ACTIVE = "is_active";


	private static Context context1 = null;

	ArrayList<HashMap<String, String>> patternItems = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> dbResult;

	// table create

	public DatabaseHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
		context1 = context;
	}

	//
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String table1 = "CREATE TABLE " + DB_TABLE + " ( " + KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
				+ KEY_COLOR + " TEXT," + KEY_DIMEN + " TEXT," + KEY_TYPE
				+ " TEXT," + KEY_TECH + " TEXT," + KEY_BRAND_ID + " TEXT,"

				+ KEY_TILE_ID + " TEXT," + KEY_CATEGORY_ID + " TEXT," + KEY_CATEGORY + " TEXT,"
				+ KEY_TYPE_ID + " TEXT," + KEY_COLOR_CODE + " TEXT," + KEY_MODEL_NO + " TEXT," + KEY_TILE_TYPE + " TEXT,"
				+ KEY_TILE_PRICE + " TEXT," + KEY_IS_ACTIVE + " TEXT," + KEY_DOWN_STATUS + " TEXT,"
				+ KEY_IMAGE_URL + " TEXT,"

				+ KEY_BRAND + " TEXT," + KEY_COMPANY + " TEXT  )";
		String table2 = "CREATE TABLE " + DB_PRO + " ( " + PROJ_NAME
				+ " TEXT  PRIMARY KEY , " + PROJ_UNIT + " TEXT, " + PROJ_WIDTH
				+ " TEXT, " + PROJ_LENGTH + " TEXT, " + PROJ_HEIGHT + " TEXT ,"
				+ PROJ_C + " TEXT," + PROJ_D + " TEXT, DrawArea TEXT)";
		String table3 = "CREATE TABLE " + DB_PRO_SAVE + " ( " + SAVE_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + SAVE_PROJ_NAME
				+ " TEXT, " + SAVE_WALL + " TEXT, " + SAVE_PATH + " TEXT )";
		String table4 = "CREATE TABLE " + DB_PAT_SAVE + " ( " + PAT_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + PAT_NAME + " TEXT,"
				+ PAT_PATH + " TEXT )";
		String table5 = "CREATE TABLE " + DB_SETTINGS + " ( " + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DEVICE_TYPE + " TEXT,"
				+ " TEXT, " + DEVICE_TOKEN + " TEXT, " + FCM_TOKEN
				+ " TEXT, " + ACTIVE_TOKEN + " TEXT, " + ACTIVE_USER + " TEXT,"
				+ DOWNLOAD_TIME_STAMP + " TEXT )";
		String table6 = "CREATE TABLE " + DB_PATRNS + " ( " + PATRN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + PATRN_NAME
				+ " TEXT, " + PATRN_CATEGORY + " TEXT, " + PATRN_CAT_ID
				+ " TEXT, " + PATRN_TYPE + " TEXT, " + PATRN_TYPE_ID + " TEXT,"
				+ PATRN_DIMEN + " TEXT, " + PATRN_PRICE + " TEXT," + PATRN_BRAND + " TEXT,"
				+ PATRN_IS_ACTIVE + " TEXT )";
		try {
			db.execSQL(table1);
			db.execSQL(table2);
			db.execSQL(table3);
			db.execSQL(table4);
			db.execSQL(table5);
			db.execSQL(table6);

		} catch (Exception e) {
			Log.e( "dmdDB", "create database error  - " + e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
		// db.execSQL("DROP TABLE IF EXISTS " + DB_PAT_SAVE);
		// db.execSQL("DROP TABLE IF EXISTS " + DB_PRO);
		// db.execSQL("DROP TABLE IF EXISTS " + DB_PRO_SAVE);
		if (newVersion == 1) {
			db.execSQL("ALTER TABLE " + DB_PRO + " ADD COLUMN DrawArea TEXT");
		}

		// Create tables again
		onCreate(db);
	}

	public void deletepattern(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
//			db.delete(DB_TABLE, null + "'", null);
			db.delete(DB_TABLE, KEY_NAME + "=" + "'" + name + "'", null);


		} catch (Exception e) {
			Toast.makeText(context1, "delete failed", Toast.LENGTH_LONG).show();
		}
		db.close();

	}

	public String insertRow(String pro_id, String pro_name, String pro_color,
							String pro_dimen, String pro_type, String pro_tech, String pro_brand) {
		String ret_msg;

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_NAME, pro_name);
		values.put(KEY_COLOR, pro_color);
		values.put(KEY_DIMEN, pro_dimen);
		values.put(KEY_TYPE, pro_type);
		values.put(KEY_TECH, pro_tech);
		values.put(KEY_BRAND, pro_brand);

		try {
			db.insert(DB_TABLE, null, values);
			ret_msg = "true";
		} catch (Exception e) {
			Log.e("insertion error", e.getMessage());
			ret_msg = "false";
		}
		db.close();
		return ret_msg;

	}

	public int getCount() {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor c = db.query(DB_TABLE, null, null, null, null, null, null);
		int result = c.getCount();
		c.close();
		return result;

	}

	public String insertRecordNew(TileModel tile) {

		String ret_msg = null;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		String s = tile.getTile_dimen();
		String[] s1 = s.split("x");
		if (s1[0].endsWith("mm") || s1[0].endsWith(".0")) {
			s1[0] = s1[0].substring(0, s1[0].length() - 2);
		}

		if (s1[1].endsWith("mm") || s1[1].endsWith(".0")) {
			s1[1] = s1[1].substring(0, s1[1].length() - 2);
		} else if (s1[1].endsWith("mm~Green")) {
			s1[1] = s1[1].substring(0, s1[1].length() - 8);
		}
		String s2 = s1[0] + " x " + s1[1];
		String tile_dimen = s2;

//		pro_type = pro_type.toUpperCase();

		values.put(KEY_NAME, tile.getTile_name());
		values.put(KEY_COLOR, tile.getTile_color());
		values.put(KEY_DIMEN, tile_dimen);
		values.put(KEY_TYPE, tile.getTile_type());
		values.put(KEY_TECH, "");
		values.put(KEY_BRAND_ID, tile.getBarnd_id());
		values.put(KEY_BRAND, tile.getTile_brand());
		values.put(KEY_COMPANY, tile.getTile_brand());

		values.put(KEY_TILE_ID, tile.getTile_id());
		values.put(KEY_CATEGORY_ID, tile.getCategory_id());
		values.put(KEY_CATEGORY, tile.getTile_category());
		values.put(KEY_TYPE_ID, tile.getType_id());
		values.put(KEY_COLOR_CODE, tile.getColor_code());
		values.put(KEY_MODEL_NO, tile.getModel_no());
		values.put(KEY_TILE_TYPE, tile.getTile_p_type());
		values.put(KEY_TILE_PRICE, tile.getTile_price());
		values.put(KEY_IMAGE_URL, tile.getImage_url());
		values.put(KEY_IS_ACTIVE, tile.getIs_active());
		values.put(KEY_DOWN_STATUS, "0");


		try {

//			db.delete(DB_TABLE, KEY_TILE_ID + "=" + "'" + tile.getTile_id() + "'", null);
			List<TileModel> tiles_d = new ArrayList<TileModel>();

			Cursor cursor = null;
			cursor = db.rawQuery("SELECT * FROM " + DB_TABLE
							+ " WHERE " + KEY_TILE_ID + " ='" + tile.getTile_id() + "'"
					, null);

			if (cursor != null) {
				while (cursor.moveToNext()) {

					TileModel tileModel_d = new TileModel();
					tileModel_d.setDownload_status(cursor.getString(cursor.getColumnIndex(KEY_DOWN_STATUS)));

					tiles_d.add(tileModel_d);
				}
			}

			if(tiles_d.size() > 0){

				values.put(KEY_DOWN_STATUS, tiles_d.get(0).getDownload_status());
				db.update(DB_TABLE, values, KEY_TILE_ID+"="+tile.getTile_id(), null);



				Log.d( "dmdDB", " Tile Updated" + tile.getTile_name());
			}
			else{
				db.insert(DB_TABLE, null, values);

				Log.d( "dmdDB", " Tile Inserted" + tile.getTile_name());
			}

			ret_msg = "true";

		} catch (Exception e) {

			ret_msg = "false";
			Log.d( "dmdDB", " Tile Insert Error  - " + e.getMessage());
		}

		db.close(); // Closing database connection

//		List<TileModel> tiles1 = new ArrayList<TileModel>();
//		tiles1 = getTileDetails("52");
//		Log.d( "dmdDB", " Tile  - " + tiles1.get(0).getIs_active());

		return ret_msg;

	}

	/*public String checkTile(String tileId) {
		String size = "";

		Cursor cursor = null;
		SQLiteDatabase db = this.getWritableDatabase();

		String[] path = tileName.split("/");
		tileName = path[path.length - 1].replace(".jpg", "");

		cursor = db.rawQuery("SELECT " + KEY_DIMEN + " FROM " + DB_TABLE
				+ " WHERE " + KEY_NAME + " ='" + tileName + "'", null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				size = cursor.getString(cursor.getColumnIndex(KEY_DIMEN));
			}
		}

		db.close();
		cursor.close();

//		size = "337 x 261";

		return size;
	}*/

	public String insertRecordCustomNew(TileModel tile) {

		String ret_msg = null;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		String s = tile.getTile_dimen();
		String[] s1 = s.split("x");
		if (s1[0].endsWith("mm") || s1[0].endsWith(".0")) {
			s1[0] = s1[0].substring(0, s1[0].length() - 2);
		}

		if (s1[1].endsWith("mm") || s1[1].endsWith(".0")) {
			s1[1] = s1[1].substring(0, s1[1].length() - 2);
		} else if (s1[1].endsWith("mm~Green")) {
			s1[1] = s1[1].substring(0, s1[1].length() - 8);
		}
		String s2 = s1[0] + " x " + s1[1];
		String tile_dimen = s2;

//		pro_type = pro_type.toUpperCase();

		values.put(KEY_NAME, tile.getTile_name());
		values.put(KEY_COLOR, tile.getTile_color());
		values.put(KEY_DIMEN, tile_dimen);
		values.put(KEY_TYPE, tile.getTile_type());
		values.put(KEY_TECH, "");
		values.put(KEY_BRAND_ID, tile.getBarnd_id());
		values.put(KEY_BRAND, tile.getTile_brand());
		values.put(KEY_COMPANY, tile.getTile_brand());

		values.put(KEY_TILE_ID, tile.getTile_id());
		values.put(KEY_CATEGORY_ID, tile.getCategory_id());
		values.put(KEY_CATEGORY, tile.getTile_category());
		values.put(KEY_TYPE_ID, tile.getType_id());
		values.put(KEY_COLOR_CODE, tile.getColor_code());
		values.put(KEY_MODEL_NO, tile.getModel_no());
		values.put(KEY_TILE_TYPE, tile.getTile_p_type());
		values.put(KEY_TILE_PRICE, tile.getTile_price());
		values.put(KEY_IMAGE_URL, tile.getImage_url());
		values.put(KEY_IS_ACTIVE, tile.getIs_active());
		values.put(KEY_DOWN_STATUS, "1");


		try {
			db.insert(DB_TABLE, null, values);

			ret_msg = "true";
			Log.d( "dmdDB", " Tile Inserted" + tile.getTile_name());
		} catch (Exception e) {

			ret_msg = "false";
			Log.d( "dmdDB", " Tile Insert Error  - " + e.getMessage());
		}
		db.close(); // Closing database connection

		return ret_msg;

	}

	public String insertPattern(PatternModel patternModel) {

		String ret_msg = null;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		String s = patternModel.getPattern_dimen();
		String[] s1 = s.split("x");
		if (s1[0].endsWith("mm") || s1[0].endsWith(".0")) {
			s1[0] = s1[0].substring(0, s1[0].length() - 2);
		}

		if (s1[1].endsWith("mm") || s1[1].endsWith(".0")) {
			s1[1] = s1[1].substring(0, s1[1].length() - 2);
		} else if (s1[1].endsWith("mm~Green")) {
			s1[1] = s1[1].substring(0, s1[1].length() - 8);
		}
		String s2 = s1[0] + " x " + s1[1];
		String pattern_dimen = s2;

		values.put(PATRN_NAME, patternModel.getPattern_name());
		values.put(PATRN_CATEGORY, patternModel.getPattern_category());
		values.put(PATRN_DIMEN, pattern_dimen);
		values.put(PATRN_CAT_ID, patternModel.getCategory_id());
		values.put(PATRN_TYPE, patternModel.getPattern_type());
		values.put(PATRN_TYPE_ID, patternModel.getType_id());
		values.put(PATRN_BRAND, patternModel.getPattern_brand());
		values.put(PATRN_PRICE, patternModel.getPattern_price());
		values.put(PATRN_IS_ACTIVE, "1");

		try {
			db.insert(DB_PATRNS, null, values);

			ret_msg = "true";
			Log.d( "dmdDB", " Pattern Inserted" + patternModel.getPattern_name());
		} catch (Exception e) {

			ret_msg = "false";
			Log.d( "dmdDB", " Pattern Insert Error  - " + e.getMessage());
		}
		db.close(); // Closing database connection
		return ret_msg;

	}

	public String insertRecord(String pro_name, String pro_color,
							   String pro_dimen, String pro_type, String pro_tech,
							   String brand_id, String brand_name, String com_name,
							   String tile_id, String category_id, String category, String type_id,
							   String color_code, String model_no, String price) {
		// TODO Auto-generated method stub
		String ret_msg = null;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		String s = pro_dimen;
		String[] s1 = s.split("x");
		if (s1[0].endsWith("mm") || s1[0].endsWith(".0")) {
			s1[0] = s1[0].substring(0, s1[0].length() - 2);
		}

		if (s1[1].endsWith("mm") || s1[1].endsWith(".0")) {
			s1[1] = s1[1].substring(0, s1[1].length() - 2);
		} else if (s1[1].endsWith("mm~Green")) {
			s1[1] = s1[1].substring(0, s1[1].length() - 8);
		}
		String s2 = s1[0] + " x " + s1[1];
		pro_dimen = s2;

		pro_type = pro_type.toUpperCase();

		values.put(KEY_NAME, pro_name);
		values.put(KEY_COLOR, pro_color);
		values.put(KEY_DIMEN, pro_dimen);
		values.put(KEY_TYPE, pro_type);
		values.put(KEY_TECH, pro_tech);
		values.put(KEY_BRAND_ID, brand_id);
		values.put(KEY_BRAND, brand_name);
		values.put(KEY_COMPANY, com_name);

		values.put(KEY_TILE_ID, tile_id);
		values.put(KEY_CATEGORY_ID, category_id);
		values.put(KEY_CATEGORY, category);
		values.put(KEY_TYPE_ID, type_id);
		values.put(KEY_COLOR_CODE, color_code);
		values.put(KEY_MODEL_NO, model_no);
		values.put(KEY_TILE_PRICE, price);
		values.put(KEY_DOWN_STATUS, "false");

		try {
			// db.insert(DB_TABLE, null, values);
			// db.replace(DB_TABLE, KEY_NAME + "=" + "'"+pro_name+"'", values);
			db.delete(DB_TABLE, KEY_NAME + "=" + "'" + pro_name + "'", null);
			db.insert(DB_TABLE, null, values);

			ret_msg = "true";
			Log.d( "dmdDB", " Tile Inserted" + pro_name);
		} catch (Exception e) {

			ret_msg = "false";
			Log.d( "dmdDB", " Tile Insert Error  - " + e.getMessage());
		}
		db.close(); // Closing database connection
		return ret_msg;

	}

	public String getTileSize(String tileName) {
		String size = "";

		Cursor cursor = null;
		SQLiteDatabase db = this.getWritableDatabase();

		String[] path = tileName.split("/");
		tileName = path[path.length - 1].replace(".jpg", "");

		cursor = db.rawQuery("SELECT " + KEY_DIMEN + " FROM " + DB_TABLE
				+ " WHERE " + KEY_NAME + " ='" + tileName + "'", null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				size = cursor.getString(cursor.getColumnIndex(KEY_DIMEN));
			}
		}

		db.close();
		cursor.close();

//		size = "337 x 261";

		return size;
	}

	public String getPatternSize(String id) {
		String size = "";

		Cursor cursor = null;
		SQLiteDatabase db = this.getWritableDatabase();

		cursor = db.rawQuery("SELECT " + PATRN_DIMEN + " FROM " + DB_PATRNS
				+ " WHERE " + PATRN_ID + " =" + id + "", null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				size = cursor.getString(cursor.getColumnIndex(PATRN_DIMEN));
			}
		}

		db.close();
		cursor.close();

//		size = "337 x 261";

		return size;
	}


	/*public List<TileModel> getPatrnDetails(String pat_id) {
		List<TileModel> tiles = new ArrayList<TileModel>();
		String size = "";

		Cursor cursor = null;
		SQLiteDatabase db = this.getWritableDatabase();

		cursor = db.rawQuery("SELECT * FROM " + DB_PATRNS
						+ " WHERE " + PATRN_ID + " =" + pat_id + ""
				, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				size = cursor.getString(cursor.getColumnIndex(KEY_DIMEN));

				TileModel tileModel = new TileModel();

				tileModel.setTile_price(cursor.getString(cursor.getColumnIndex(PATRN_PRICE)));
				tileModel.setTile_name(cursor.getString(cursor.getColumnIndex(PATRN_NAME)));
				tileModel.setTile_brand(cursor.getString(cursor.getColumnIndex(PATRN_BRAND)));
				tileModel.setTile_dimen(cursor.getString(cursor.getColumnIndex(PATRN_DIMEN)));

				tiles.add(tileModel);
			}
		}

		db.close();
		cursor.close();

		return tiles;
	}*/

	public List<TileModel> getTileDetails(String tile_id, String type) {
		List<TileModel> tiles = new ArrayList<TileModel>();
		String size = "";

		Cursor cursor = null;
		SQLiteDatabase db = this.getWritableDatabase();

		if(type.equals("P")) {
			cursor = db.rawQuery("SELECT * FROM " + DB_PATRNS
							+ " WHERE " + PATRN_ID + " ='" + tile_id + "'"
					, null);
		}
		else{
			cursor = db.rawQuery("SELECT * FROM " + DB_TABLE
							+ " WHERE " + KEY_ID + " =" + tile_id + ""
					, null);
		}

		if (cursor != null) {
			while (cursor.moveToNext()) {

				if(type.equals("P")) {
					TileModel tileModel = new TileModel();

					tileModel.setTile_price(cursor.getString(cursor.getColumnIndex(PATRN_PRICE)));
					tileModel.setTile_name(cursor.getString(cursor.getColumnIndex(PATRN_NAME)));
					tileModel.setTile_brand(cursor.getString(cursor.getColumnIndex(PATRN_BRAND)));
					tileModel.setTile_dimen(cursor.getString(cursor.getColumnIndex(PATRN_DIMEN)));

					tiles.add(tileModel);
				}
				else{

					TileModel tileModel = new TileModel();
					tileModel.setTile_serial_no(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
					tileModel.setTile_name(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
					tileModel.setTile_color(cursor.getString(cursor.getColumnIndex(KEY_COLOR)));
					tileModel.setTile_dimen(cursor.getString(cursor.getColumnIndex(KEY_DIMEN)));
					tileModel.setTile_type(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
					tileModel.setBarnd_id(cursor.getString(cursor.getColumnIndex(KEY_BRAND_ID)));
					tileModel.setTile_brand(cursor.getString(cursor.getColumnIndex(KEY_BRAND)));
					tileModel.setTile_id(cursor.getString(cursor.getColumnIndex(KEY_TILE_ID)));
					tileModel.setCategory_id(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_ID)));
					tileModel.setTile_category(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
					tileModel.setType_id(cursor.getString(cursor.getColumnIndex(KEY_TYPE_ID)));
					tileModel.setColor_code(cursor.getString(cursor.getColumnIndex(KEY_COLOR_CODE)));
					tileModel.setModel_no(cursor.getString(cursor.getColumnIndex(KEY_MODEL_NO)));
					tileModel.setTile_price(cursor.getString(cursor.getColumnIndex(KEY_TILE_PRICE)));
					tileModel.setImage_url(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL)));
					tileModel.setIs_active(cursor.getString(cursor.getColumnIndex(KEY_IS_ACTIVE)));
					tileModel.setDownload_status(cursor.getString(cursor.getColumnIndex(KEY_DOWN_STATUS)));

					tiles.add(tileModel);

				}
			}
		}

		db.close();
		cursor.close();

		return tiles;
	}

	public List<TileModel> getTileDetailsforDownload() {
		List<TileModel> tiles = new ArrayList<TileModel>();

		Cursor cursor = null;
		SQLiteDatabase db = this.getWritableDatabase();

		cursor = db.rawQuery("SELECT * FROM " + DB_TABLE
						+ " WHERE " + KEY_DOWN_STATUS + " ='0'"
				, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {

				TileModel tileModel = new TileModel();
				tileModel.setTile_serial_no(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
				tileModel.setTile_name(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
				tileModel.setTile_color(cursor.getString(cursor.getColumnIndex(KEY_COLOR)));
				tileModel.setTile_dimen(cursor.getString(cursor.getColumnIndex(KEY_DIMEN)));
				tileModel.setTile_type(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
				tileModel.setBarnd_id(cursor.getString(cursor.getColumnIndex(KEY_BRAND_ID)));
				tileModel.setTile_brand(cursor.getString(cursor.getColumnIndex(KEY_BRAND)));
				tileModel.setTile_id(cursor.getString(cursor.getColumnIndex(KEY_TILE_ID)));
				tileModel.setCategory_id(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_ID)));
				tileModel.setTile_category(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
				tileModel.setType_id(cursor.getString(cursor.getColumnIndex(KEY_TYPE_ID)));
				tileModel.setColor_code(cursor.getString(cursor.getColumnIndex(KEY_COLOR_CODE)));
				tileModel.setModel_no(cursor.getString(cursor.getColumnIndex(KEY_MODEL_NO)));
				tileModel.setTile_price(cursor.getString(cursor.getColumnIndex(KEY_TILE_PRICE)));
				tileModel.setImage_url(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL)));
				tileModel.setIs_active(cursor.getString(cursor.getColumnIndex(KEY_IS_ACTIVE)));

				tiles.add(tileModel);
			}
		}

		db.close();
		cursor.close();

		return tiles;
	}

	public ArrayList<HashMap<String, String>> getPatterns() {

		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + DB_PATRNS
							+ " WHERE " + PATRN_IS_ACTIVE + " ='1'"
					, null);

			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(PATRN_ID, cursor.getString(cursor
								.getColumnIndex(PATRN_ID)));
						dbResult.put(PATRN_BRAND, cursor.getString(cursor
								.getColumnIndex(PATRN_BRAND)));
						dbResult.put(
								PATRN_NAME,
								cursor.getString(cursor
										.getColumnIndex(PATRN_NAME)) + ".jpg");
						dbResult.put(PATRN_DIMEN, cursor.getString(cursor
								.getColumnIndex(PATRN_DIMEN)));
						dbResult.put(PATRN_CATEGORY, cursor.getString(cursor
								.getColumnIndex(PATRN_CATEGORY)));
						dbResult.put(PATRN_TYPE, cursor.getString(cursor
								.getColumnIndex(PATRN_TYPE)));
						dbResult.put(PATRN_PRICE, cursor.getString(cursor
								.getColumnIndex(PATRN_PRICE)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}

		return patternItems;

	}


	public ArrayList<HashMap<String, String>> searchPatterns(String keyword) {
		patternItems.clear();
		Cursor cursor = null;

		if (keyword.contains(" ") && !keyword.equals("")) {
			String[] key = keyword.split(" ");

			String s = key[0];

			for (int i = 1; i < key.length; i++) {
				s = s + "%' AND " + PATRN_NAME + " LIKE '%" + key[i];

			}

			keyword = s;
			key = new String[0];
		}

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			cursor = db.rawQuery("SELECT * FROM " + DB_PATRNS + " WHERE "
							+ PATRN_NAME + " LIKE '%" + keyword + "%' AND "
							+ PATRN_IS_ACTIVE + " ='1'"
					, null);

			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(PATRN_BRAND, cursor.getString(cursor
								.getColumnIndex(PATRN_BRAND)));
						dbResult.put(
								PATRN_NAME,
								cursor.getString(cursor
										.getColumnIndex(PATRN_NAME)) + ".jpg");
						dbResult.put(PATRN_DIMEN, cursor.getString(cursor
								.getColumnIndex(PATRN_DIMEN)));
						dbResult.put(PATRN_CATEGORY, cursor.getString(cursor
								.getColumnIndex(PATRN_CATEGORY)));
						dbResult.put(PATRN_TYPE, cursor.getString(cursor
								.getColumnIndex(PATRN_TYPE)));
						dbResult.put(PATRN_PRICE, cursor.getString(cursor
								.getColumnIndex(PATRN_PRICE)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public String updateDownloadStatus(String tile_id) {
		// TODO Auto-generated method stub
		String ret_msg = null;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_DOWN_STATUS, "1");


		try {
			db.update(DB_TABLE, values, KEY_TILE_ID+"="+tile_id, null);

			ret_msg = "true";
		} catch (Exception e) {

			ret_msg = "false";
		}
		db.close(); // Closing database connection
		return ret_msg;

	}

	public String deleteTile(String tile_id) {
		// TODO Auto-generated method stub
		String ret_msg = null;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_IS_ACTIVE, "0");
		values.put(KEY_DOWN_STATUS, "0");

		try {
			db.update(DB_TABLE, values, KEY_ID+"="+tile_id, null);

			ret_msg = "true";
		} catch (Exception e) {

			ret_msg = "false";
		}
		db.close(); // Closing database connection
		return ret_msg;
	}

	public String deleteTileCustom(String tile_id) {
		// TODO Auto-generated method stub
		String ret_msg = null;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_IS_ACTIVE, "0");

		try {
			db.update(DB_TABLE, values, KEY_ID+"="+tile_id, null);

			ret_msg = "true";
		} catch (Exception e) {

			ret_msg = "false";
		}
		db.close(); // Closing database connection
		return ret_msg;
	}

	class LayoutDimensions {
		int x, y, height;
		int width;
		float area;
		String selectedTile;
		int orientation;
		int tilesUsed;
	}

	public String getViewAreaOfProject(String proj_name) {
		Cursor cursor = null;
		SQLiteDatabase db = this.getWritableDatabase();
		String CanvasSize = null;

		cursor = db.rawQuery("SELECT DrawArea FROM " + DB_PRO + " WHERE "
				+ PROJ_NAME + " ='" + proj_name + "'", null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				CanvasSize = cursor
						.getString(cursor.getColumnIndex("DrawArea"));
			}
		}

		// CanvasSize=cursor.getString(cursor.getColumnIndex("DrawArea"));
		db.close();
		cursor.close();
		if (CanvasSize != null) {
			return CanvasSize;
		} else {
			return "0";
		}
	}

	public boolean exportProject(String proj_name, String exportPath) {

		Cursor cursor = null;
		SQLiteDatabase db = this.getWritableDatabase();
		Boolean status = null;

		try {

			cursor = db.rawQuery("SELECT * FROM " + DB_PRO + " WHERE "
					+ PROJ_NAME + " ='" + proj_name + "'", null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {

						final String xmlFile = Environment
								.getExternalStorageDirectory()
								+ "/SmartShowRoom/"
								+ proj_name
								+ "/"
								+ proj_name + ".xml";
						File f = new File(xmlFile);
						if (f.exists()) {
							f.delete();
						}

						FileWriter out = new FileWriter(new File(xmlFile));

						XmlSerializer xmlSerializer = Xml.newSerializer();
						StringWriter writer = new StringWriter();
						xmlSerializer.setOutput(writer);
						xmlSerializer.startDocument("UTF-8", true);

						xmlSerializer.startTag(null, "CreateProjectData");

						xmlSerializer.startTag(null, PROJ_NAME);
						xmlSerializer.text(cursor.getString(cursor
								.getColumnIndex(PROJ_NAME)));
						xmlSerializer.endTag(null, PROJ_NAME);

						xmlSerializer.startTag(null, PROJ_UNIT);
						xmlSerializer.text(cursor.getString(cursor
								.getColumnIndex(PROJ_UNIT)));
						xmlSerializer.endTag(null, PROJ_UNIT);

						xmlSerializer.startTag(null, PROJ_LENGTH);
						xmlSerializer.text(cursor.getString(cursor
								.getColumnIndex(PROJ_LENGTH)));
						xmlSerializer.endTag(null, PROJ_LENGTH);

						xmlSerializer.startTag(null, PROJ_WIDTH);
						xmlSerializer.text(cursor.getString(cursor
								.getColumnIndex(PROJ_WIDTH)));
						xmlSerializer.endTag(null, PROJ_WIDTH);

						xmlSerializer.startTag(null, PROJ_HEIGHT);
						xmlSerializer.text(cursor.getString(cursor
								.getColumnIndex(PROJ_HEIGHT)));
						xmlSerializer.endTag(null, PROJ_HEIGHT);

						xmlSerializer.startTag(null, PROJ_C);
						xmlSerializer.text(cursor.getString(cursor
								.getColumnIndex(PROJ_C)));
						xmlSerializer.endTag(null, PROJ_C);

						xmlSerializer.startTag(null, PROJ_D);
						xmlSerializer.text(cursor.getString(cursor
								.getColumnIndex(PROJ_D)));
						xmlSerializer.endTag(null, PROJ_D);

						xmlSerializer.startTag(null, "ViewArea");
						xmlSerializer.text(GlobalVariables
								.getDrawArea(context1) + "");
						xmlSerializer.endTag(null, "ViewArea");

						xmlSerializer.endTag(null, "CreateProjectData");

						xmlSerializer.endDocument();
						xmlSerializer.flush();
						String dataWrite = writer.toString();
						out.write(dataWrite);
						out.close();

					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();
				status = true;
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), "ERROR!");

		}

		List<File> files = getListFiles(new File(
				Environment.getExternalStorageDirectory() + "/SmartShowroom/"
						+ proj_name));
		String[] list = new String[files.size()];

		for (int i = 0; i < files.size(); i++)
			list[i] = files.get(i).toString();

		try {
			new Compress().zip(list, Environment.getExternalStorageDirectory()
					+ "/SmartShowRoom/Exported Projects/" + proj_name + ".zip");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	private List<File> getListFiles(File parentDir) {
		ArrayList<File> inFiles = new ArrayList<File>();
		File[] files = parentDir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				inFiles.addAll(getListFiles(file));
			} else {
				if (file.getName().endsWith(".xml")
						|| file.getName().endsWith(".png")) {
					inFiles.add(file);
				}
			}
		}
		return inFiles;
	}

	public String insertProject(String proj_name, String proj_unit,
								String proj_length, String proj_width, String proj_height,
								String proj_c, String proj_d, String drawarea) {
		// TODO Auto-generated method stub
		String ret_msg = null;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(PROJ_NAME, proj_name);
		values.put(PROJ_UNIT, proj_unit);
		values.put(PROJ_LENGTH, proj_length);
		values.put(PROJ_WIDTH, proj_width);
		values.put(PROJ_HEIGHT, proj_height);
		values.put(PROJ_C, proj_c);
		values.put(PROJ_D, proj_d);
		values.put("DrawArea", drawarea);

		try {
			db.insert(DB_PRO, null, values);

			ret_msg = "true";
		} catch (Exception e) {

			ret_msg = "false";
		}
		db.close(); // Closing database connection
		return ret_msg;

	}

	public String insertWall(String proj_name, String wall_name,
							 String image_name) {
		// TODO Auto-generated method stub
		String ret_msg = null;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(SAVE_PROJ_NAME, proj_name);
		values.put(SAVE_WALL, wall_name);
		values.put(SAVE_PATH, image_name);

		try {
			db.insert(DB_PRO_SAVE, null, values);

			ret_msg = "true";
		} catch (Exception e) {

			ret_msg = "false";
		}
		db.close(); // Closing database connection

		return ret_msg;

	}

	public ArrayList<HashMap<String, String>> getAllPattern() {

		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + DB_TABLE
							+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
							+ " AND " + KEY_IS_ACTIVE + " ='1'"
					, null);
//			cursor = db.rawQuery("SELECT * FROM " + DB_TABLE, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_ID, cursor.getString(cursor
								.getColumnIndex(KEY_ID)));
						dbResult.put(KEY_BRAND, cursor.getString(cursor
								.getColumnIndex(KEY_BRAND)));
						dbResult.put(
								KEY_NAME,
								cursor.getString(cursor
										.getColumnIndex(KEY_NAME)) + ".jpg");
						dbResult.put(KEY_DIMEN, cursor.getString(cursor
								.getColumnIndex(KEY_DIMEN)));
						dbResult.put(KEY_CATEGORY, cursor.getString(cursor
								.getColumnIndex(KEY_CATEGORY)));
						dbResult.put(KEY_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TYPE)));
						dbResult.put(KEY_TILE_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TILE_TYPE)));
						dbResult.put(KEY_COLOR, cursor.getString(cursor
								.getColumnIndex(KEY_COLOR)));
						dbResult.put(KEY_TILE_PRICE, cursor.getString(cursor
								.getColumnIndex(KEY_TILE_PRICE)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}

		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getAllPatternPaginated(int limit, int offset) {

		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + DB_TABLE
							+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
							+ " AND " + KEY_IS_ACTIVE + " ='1' LIMIT "+ limit + " OFFSET "+ offset
					, null);
//			cursor = db.rawQuery("SELECT * FROM " + DB_TABLE, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_ID, cursor.getString(cursor
								.getColumnIndex(KEY_ID)));
						dbResult.put(KEY_BRAND, cursor.getString(cursor
								.getColumnIndex(KEY_BRAND)));
						dbResult.put(
								KEY_NAME,
								cursor.getString(cursor
										.getColumnIndex(KEY_NAME)) + ".jpg");
						dbResult.put(KEY_DIMEN, cursor.getString(cursor
								.getColumnIndex(KEY_DIMEN)));
						dbResult.put(KEY_CATEGORY, cursor.getString(cursor
								.getColumnIndex(KEY_CATEGORY)));
						dbResult.put(KEY_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TYPE)));
						dbResult.put(KEY_TILE_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TILE_TYPE)));
						dbResult.put(KEY_COLOR, cursor.getString(cursor
								.getColumnIndex(KEY_COLOR)));
						dbResult.put(KEY_TILE_PRICE, cursor.getString(cursor
								.getColumnIndex(KEY_TILE_PRICE)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}

		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getAllPatternCustom(boolean isCustom) {

		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			if(isCustom) {
				cursor = db.rawQuery("SELECT * FROM " + DB_TABLE
								+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='C'"
						, null);
			}
			else{
				cursor = db.rawQuery("SELECT * FROM " + DB_TABLE
								+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='M'"
						, null);
			}
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_ID, cursor.getString(cursor
								.getColumnIndex(KEY_ID)));
						dbResult.put(KEY_BRAND, cursor.getString(cursor
								.getColumnIndex(KEY_BRAND)));
						dbResult.put(
								KEY_NAME,
								cursor.getString(cursor
										.getColumnIndex(KEY_NAME)) + ".jpg");
						dbResult.put(KEY_DIMEN, cursor.getString(cursor
								.getColumnIndex(KEY_DIMEN)));
						dbResult.put(KEY_CATEGORY, cursor.getString(cursor
								.getColumnIndex(KEY_CATEGORY)));
						dbResult.put(KEY_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TYPE)));
						dbResult.put(KEY_TILE_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TILE_TYPE)));
						dbResult.put(KEY_COLOR, cursor.getString(cursor
								.getColumnIndex(KEY_COLOR)));
						dbResult.put(KEY_TILE_PRICE, cursor.getString(cursor
								.getColumnIndex(KEY_TILE_PRICE)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}

		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getAllPatternCustomPaginated(boolean isCustom, int limit, int offset) {

		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			if(isCustom) {
				cursor = db.rawQuery("SELECT * FROM " + DB_TABLE
								+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='C' LIMIT "+ limit + " OFFSET "+ offset
						, null);
			}
			else{
				cursor = db.rawQuery("SELECT * FROM " + DB_TABLE
								+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='M' LIMIT "+ limit + " OFFSET "+ offset
						, null);
			}
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_ID, cursor.getString(cursor
								.getColumnIndex(KEY_ID)));
						dbResult.put(KEY_BRAND, cursor.getString(cursor
								.getColumnIndex(KEY_BRAND)));
						dbResult.put(
								KEY_NAME,
								cursor.getString(cursor
										.getColumnIndex(KEY_NAME)) + ".jpg");
						dbResult.put(KEY_DIMEN, cursor.getString(cursor
								.getColumnIndex(KEY_DIMEN)));
						dbResult.put(KEY_CATEGORY, cursor.getString(cursor
								.getColumnIndex(KEY_CATEGORY)));
						dbResult.put(KEY_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TYPE)));
						dbResult.put(KEY_TILE_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TILE_TYPE)));
						dbResult.put(KEY_COLOR, cursor.getString(cursor
								.getColumnIndex(KEY_COLOR)));
						dbResult.put(KEY_TILE_PRICE, cursor.getString(cursor
								.getColumnIndex(KEY_TILE_PRICE)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}

		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getAllBrand() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT DISTINCT " + KEY_BRAND + " FROM "
							+ DB_TABLE
							+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
							+ " AND " + KEY_IS_ACTIVE + " ='1'"
					, null);

			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_BRAND, cursor.getString(cursor
								.getColumnIndex(KEY_BRAND)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getAllCompany() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT DISTINCT " + KEY_COMPANY + " FROM "
					+ DB_TABLE, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_COMPANY, cursor.getString(cursor
								.getColumnIndex(KEY_COMPANY)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getDistinctCategory() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT DISTINCT " + KEY_CATEGORY + " FROM "
							+ DB_TABLE
							+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
							+ " AND " + KEY_IS_ACTIVE + " ='1'"
					, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_CATEGORY, cursor.getString(cursor
								.getColumnIndex(KEY_CATEGORY)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getDistinctCategoryCustom(boolean isCustom) {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			if(isCustom){
				cursor = db.rawQuery("SELECT DISTINCT " + KEY_CATEGORY + " FROM "
								+ DB_TABLE
								+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='C'"
						, null);
			}
			else{
				cursor = db.rawQuery("SELECT DISTINCT " + KEY_CATEGORY + " FROM "
								+ DB_TABLE
								+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='M'"
						, null);
			}

			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_CATEGORY, cursor.getString(cursor
								.getColumnIndex(KEY_CATEGORY)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getDistinctSize() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT DISTINCT " + KEY_DIMEN + " FROM "
							+ DB_TABLE
							+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
							+ " AND " + KEY_IS_ACTIVE + " ='1'"
					, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_DIMEN, cursor.getString(cursor
								.getColumnIndex(KEY_DIMEN)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getDistinctSizeCustom(boolean isCustom) {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			if(isCustom){
				cursor = db.rawQuery("SELECT DISTINCT " + KEY_DIMEN + " FROM "
								+ DB_TABLE
								+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='C'"
						, null);
			}
			else{
				cursor = db.rawQuery("SELECT DISTINCT " + KEY_DIMEN + " FROM "
								+ DB_TABLE
								+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='M'"
						, null);
			}

			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_DIMEN, cursor.getString(cursor
								.getColumnIndex(KEY_DIMEN)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getDistinctColor() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT DISTINCT " + KEY_COLOR + " FROM "
							+ DB_TABLE
							+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
							+ " AND " + KEY_IS_ACTIVE + " ='1'"
					, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_COLOR, cursor.getString(cursor
								.getColumnIndex(KEY_COLOR)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getDistinctType() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT DISTINCT " + KEY_TYPE + " FROM "
							+ DB_TABLE
							+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
							+ " AND " + KEY_IS_ACTIVE + " ='1'"
					, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TYPE)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getDistinctTypeCustom(boolean isCustom) {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			if(isCustom){
				cursor = db.rawQuery("SELECT DISTINCT " + KEY_TYPE + " FROM "
								+ DB_TABLE
								+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='C'"
						, null);
			}
			else{
				cursor = db.rawQuery("SELECT DISTINCT " + KEY_TYPE + " FROM "
								+ DB_TABLE
								+ " WHERE " + KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='M'"
						, null);
			}

			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TYPE)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> searchkeyword(String keyword, String flag) {
		patternItems.clear();
		Cursor cursor = null;

		if (keyword.contains(" ") && !keyword.equals("")) {
			String[] key = keyword.split(" ");

			String s = key[0];
			// if(key.length==1)
			// {
			// keyword=s;
			// }
			// else
			// {
			for (int i = 1; i < key.length; i++) {
				s = s + "%' AND " + KEY_NAME + " LIKE '%" + key[i];

			}

			keyword = s;
			key = new String[0];
			// }
		}

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			String qry = "SELECT * FROM " + DB_TABLE + " WHERE " + KEY_NAME
					+ " LIKE '% " + keyword + " %'";

			if(flag.equals("C")){
				cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE "
								+ KEY_NAME + " LIKE '%" + keyword + "%' AND "
								+ KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='C'"
						, null);
			}
			else if(flag.equals("M")){
				cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE "
								+ KEY_NAME + " LIKE '%" + keyword + "%' AND "
								+ KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
								+ " AND " + KEY_TILE_TYPE + " ='M'"
						, null);
			}
			else{
				cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE "
								+ KEY_NAME + " LIKE '%" + keyword + "%' AND "
								+ KEY_DOWN_STATUS + " ='1'"
								+ " AND " + KEY_IS_ACTIVE + " ='1'"
						, null);
			}

			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_BRAND, cursor.getString(cursor
								.getColumnIndex(KEY_BRAND)));
						dbResult.put(
								KEY_NAME,
								cursor.getString(cursor
										.getColumnIndex(KEY_NAME)) + ".jpg");
						dbResult.put(KEY_DIMEN, cursor.getString(cursor
								.getColumnIndex(KEY_DIMEN)));
						dbResult.put(KEY_CATEGORY, cursor.getString(cursor
								.getColumnIndex(KEY_CATEGORY)));
						dbResult.put(KEY_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TYPE)));
						dbResult.put(KEY_COLOR, cursor.getString(cursor
								.getColumnIndex(KEY_COLOR)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getResultByFilterNew(String category,
																   String brand, String size, String color, String type, String flag) {
		patternItems.clear();
		String qry = "SELECT * FROM " + DB_TABLE + " WHERE ";
		Cursor cursor = null;
		String andString = " and ";
		String qCategory = KEY_CATEGORY + " in ( " + category + " ) ";
		String qBrand = KEY_BRAND + " in ( " + brand + " ) ";
		String qSize = KEY_DIMEN + " in ( " + size + " ) ";
		String qColor = KEY_COLOR + " in ( " + color + " ) ";
		String qType = KEY_TYPE + " in ( " + type + " ) ";
		if (category != null) {

			qry += qCategory;
		}

		if (brand != null) {
			if (category != null)
				qBrand = andString + qBrand;
			qry += qBrand;
		}
		if (size != null) {
			if (brand != null || category != null)
				qSize = andString + qSize;
			qry += qSize;
		}
		if (color != null) {
			if (brand != null || size != null)
				qColor = andString + qColor;
			qry += qColor;
		}
		if (type != null) {
			if (brand != null || size != null || color != null)
				qType = andString + qType;
			qry += qType;
		}

		if (category == null && brand == null && size == null && color == null
				&& type == null) {
//			qry = qry.replace("WHERE", "");

			if(flag.equals("C")){
				qry +=  KEY_DOWN_STATUS + " ='1'"
						+ " AND " + KEY_IS_ACTIVE + " ='1'"
						+ " AND " + KEY_TILE_TYPE + " ='C'";
			}
			else if(flag.equals("M")){
				qry +=  KEY_DOWN_STATUS + " ='1'"
						+ " AND " + KEY_IS_ACTIVE + " ='1'"
						+ " AND " + KEY_TILE_TYPE + " ='M'";
			}
			else{
				qry +=  KEY_DOWN_STATUS + " ='1'"
						+ " AND " + KEY_IS_ACTIVE + " ='1'";
			}
		}
		else{
			if(flag.equals("C")){
				qry +=  " AND " + KEY_DOWN_STATUS + " ='1'"
						+ " AND " + KEY_IS_ACTIVE + " ='1'"
						+ " AND " + KEY_TILE_TYPE + " ='C'";
			}
			else if(flag.equals("M")){
				qry +=  " AND " + KEY_DOWN_STATUS + " ='1'"
						+ " AND " + KEY_IS_ACTIVE + " ='1'"
						+ " AND " + KEY_TILE_TYPE + " ='M'";
			}
			else{
				qry +=  " AND " + KEY_DOWN_STATUS + " ='1'"
						+ " AND " + KEY_IS_ACTIVE + " ='1'";
			}
		}

		Log.d("Query", "Filter" + qry);

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			cursor = db.rawQuery(qry, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_BRAND, cursor.getString(cursor
								.getColumnIndex(KEY_BRAND)));
						dbResult.put(
								KEY_NAME,
								cursor.getString(cursor
										.getColumnIndex(KEY_NAME)) + ".jpg");
						dbResult.put(KEY_DIMEN, cursor.getString(cursor
								.getColumnIndex(KEY_DIMEN)));
						dbResult.put(KEY_CATEGORY, cursor.getString(cursor
								.getColumnIndex(KEY_CATEGORY)));
						dbResult.put(KEY_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TYPE)));
						dbResult.put(KEY_COLOR, cursor.getString(cursor
								.getColumnIndex(KEY_COLOR)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getResultByFilter(String company,
																String brand, String size, String color, String type) {
		patternItems.clear();
		String qry = "SELECT * FROM " + DB_TABLE + " WHERE ";
		Cursor cursor = null;
		String andString = " and ";
		String qCompany = KEY_COMPANY + " in ( " + company + " ) ";
		String qBrand = KEY_BRAND + " in ( " + brand + " ) ";
		String qSize = KEY_DIMEN + " in ( " + size + " ) ";
		String qColor = KEY_COLOR + " in ( " + color + " ) ";
		String qType = KEY_TYPE + " in ( " + type + " ) ";
		if (company != null) {

			qry += qCompany;
		}

		if (brand != null) {
			if (company != null)
				qBrand = andString + qBrand;
			qry += qBrand;
		}
		if (size != null) {
			if (brand != null || company != null)
				qSize = andString + qSize;
			qry += qSize;
		}
		if (color != null) {
			if (brand != null || size != null)
				qColor = andString + qColor;
			qry += qColor;
		}
		if (type != null) {
			if (brand != null || size != null || color != null)
				qType = andString + qType;
			qry += qType;
		}
		if (company == null && brand == null && size == null && color == null
				&& type == null) {
			qry = qry.replace("WHERE", "");
		}
		/*
		 * else{ qry+=qBrand+qSize+qColor+qType; }
		 */
		System.out.println(qry);

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			cursor = db.rawQuery(qry, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(KEY_ID, cursor.getString(cursor
								.getColumnIndex(KEY_ID)));
						dbResult.put(KEY_BRAND, cursor.getString(cursor
								.getColumnIndex(KEY_BRAND)));
						dbResult.put(
								KEY_NAME,
								cursor.getString(cursor
										.getColumnIndex(KEY_NAME)) + ".jpg");
						dbResult.put(KEY_DIMEN, cursor.getString(cursor
								.getColumnIndex(KEY_DIMEN)));
						dbResult.put(KEY_CATEGORY, cursor.getString(cursor
								.getColumnIndex(KEY_CATEGORY)));
						dbResult.put(KEY_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TYPE)));
						dbResult.put(KEY_TILE_TYPE, cursor.getString(cursor
								.getColumnIndex(KEY_TILE_TYPE)));
						dbResult.put(KEY_COLOR, cursor.getString(cursor
								.getColumnIndex(KEY_COLOR)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public void clearTable() {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			String deleteSQL = "DELETE FROM " + DB_PRO;
			db.execSQL(deleteSQL);
			Toast.makeText(this.context1, "Clear table Complete ",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this.context1, "Error! " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean isProjectExist(String projName) {
		SQLiteDatabase sqldb = this.getWritableDatabase();
		String Query = "Select * from " + DB_PRO + " where " + PROJ_NAME
				+ "=\"" + projName + "\"";
		Cursor cursor = sqldb.rawQuery(Query, null);
		if (cursor.getCount() <= 0) {
			return false;
		}
		return true;

	}

	public ArrayList<HashMap<String, String>> getProject() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + DB_PRO, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(PROJ_NAME, cursor.getString(cursor
								.getColumnIndex(PROJ_NAME)));
						dbResult.put(PROJ_UNIT, cursor.getString(cursor
								.getColumnIndex(PROJ_UNIT)));
						dbResult.put(PROJ_LENGTH, cursor.getString(cursor
								.getColumnIndex(PROJ_LENGTH)));
						dbResult.put(PROJ_WIDTH, cursor.getString(cursor
								.getColumnIndex(PROJ_WIDTH)));
						dbResult.put(PROJ_HEIGHT, cursor.getString(cursor
								.getColumnIndex(PROJ_HEIGHT)));
						dbResult.put(PROJ_C,
								cursor.getString(cursor.getColumnIndex(PROJ_C)));
						dbResult.put(PROJ_D,
								cursor.getString(cursor.getColumnIndex(PROJ_D)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

	public void deleteProject(String pro_name) {

		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(DB_PRO, PROJ_NAME + "=\"" + pro_name + "\"", null);
			deleteFiles(Environment.getExternalStorageDirectory()
					+ "/SmartShowroom/" + pro_name);
			File f = new File(Environment.getExternalStorageDirectory()
					+ "/SmartShowroom/" + pro_name);
			f.delete();
			// File file=new
			// File(Environment.getExternalStorageDirectory()+"/SmartShowroom"+pro_name,
			// pro_name);
			// if(file.exists())
			// {
			// file.delete();
			// Toast.makeText(context1, "delete success",
			// Toast.LENGTH_SHORT).show();
			// }
			// String s=sdpath.toString();
			// String s1=s+"/SmartShowroom/"+pro_name;

		} catch (Exception e) {
			Toast.makeText(context1, "delete failed", Toast.LENGTH_LONG).show();
		}
	}

	public static void deleteFiles(String path) {

		File file = new File(path);

		if (file.exists()) {
			String deleteCmd = "rm -r " + path;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
				Toast.makeText(context1, "delete success", Toast.LENGTH_SHORT)
						.show();
			} catch (IOException e) {
			}
		}
	}

	public static void deleteAmbienceFiles(String path) {

		File file = new File(path);

		if (file.exists()) {
			String deleteCmd = "rm -r " + path;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);
				// Toast.makeText(context1, "delete success",
				// Toast.LENGTH_SHORT)
				// .show();
			} catch (IOException e) {
			}
		}

	}

	public static void deleteallshots(String path) {
		File file = new File(path);

		if (file.exists()) {
			String deleteCmd = "rm -r " + path;
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec(deleteCmd);

			} catch (IOException e) {
			}
		}
	}

	// CustomPattern insertion method
	public String insertCustomPattern(String pName, String pPath) {
		String ret_msg = null;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PAT_NAME, pName);
		values.put(PAT_PATH, pPath);

		try {
			db.insert(DB_PAT_SAVE, null, values);

			ret_msg = "true";
		} catch (Exception e) {

			ret_msg = "false";
		}
		db.close(); // Closing database connection

		return ret_msg;

	}

	public ArrayList<HashMap<String, String>> getCustomePattern() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + DB_PAT_SAVE, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					try {
						HashMap<String, String> dbResult = new HashMap<String, String>();
						dbResult.put(PAT_NAME, cursor.getString(cursor
								.getColumnIndex(PAT_NAME)));
						dbResult.put(PAT_PATH, cursor.getString(cursor
								.getColumnIndex(PAT_PATH)));

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("db", "database fetch error getAllPattern_" + e.getMessage());
		}
		return patternItems;

	}

}