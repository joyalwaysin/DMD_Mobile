package com.nagainfomob.database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import com.nagainfomob.smartShowroom.GlobalVariables;
import com.nagainfomob.zipmanagement.Compress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final String DB_NAME = "smartDB";
	private static final int DB_VERSION = 17;
	private static final String DB_TABLE = "smartShowroom3D";
	private static final String DB_PRO = "project";
	private static final String DB_PRO_SAVE = "saveproject";
	private static final String DB_PAT_SAVE = "savepattern";

	// Table fields
	private static final String KEY_ID = "pro_id";
	private static final String KEY_NAME = "pro_name";
	private static final String KEY_COLOR = "pro_color";
	private static final String KEY_DIMEN = "pro_dimen";
	private static final String KEY_TYPE = "pro_type";
	private static final String KEY_TECH = "pro_tech";
	private static final String KEY_BRAND_ID = "barnd_id";
	private static final String KEY_BRAND = "pro_brand";
	private static final String KEY_COMPANY = "pro_company";

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
		try {
			db.execSQL(table1);
			db.execSQL(table2);
			db.execSQL(table3);
			db.execSQL(table4);

		} catch (Exception e) {
			Log.e("create database error", e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
		// db.execSQL("DROP TABLE IF EXISTS " + DB_PAT_SAVE);
		// db.execSQL("DROP TABLE IF EXISTS " + DB_PRO);
		// db.execSQL("DROP TABLE IF EXISTS " + DB_PRO_SAVE);
		if (newVersion == 17) {
			db.execSQL("ALTER TABLE " + DB_PRO + " ADD COLUMN DrawArea TEXT");
		}

		// Create tables again
		onCreate(db);
	}

	public void deletepattern(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.delete(DB_TABLE, KEY_NAME + "=" + "'" + name + "'", null);
			// deleteFiles(Environment.getExternalStorageDirectory()+"/SmartShowroom/"+pro_name);
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

	public String insertRecord(String pro_name, String pro_color,
			String pro_dimen, String pro_type, String pro_tech,
			String brand_id, String brand_name, String com_name) {
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
		try {
			// db.insert(DB_TABLE, null, values);
			// db.replace(DB_TABLE, KEY_NAME + "=" + "'"+pro_name+"'", values);
			db.delete(DB_TABLE, KEY_NAME + "=" + "'" + pro_name + "'", null);
			db.insert(DB_TABLE, null, values);

			ret_msg = "true";
		} catch (Exception e) {

			ret_msg = "false";
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

		return size;
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

						xmlSerializer.startTag(null, "ProjectData");

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

						xmlSerializer.endTag(null, "ProjectData");

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
			cursor = db.rawQuery("SELECT * FROM " + DB_TABLE, null);
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

						patternItems.add(dbResult);
					} catch (Exception e) {
						Log.e(e.getMessage(), "ERROR!");
					}
				}
				db.close();
				cursor.close();

			}

		} catch (Exception e) {
			Log.e("database fetch error getAllPattern", e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getAllBrand() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT DISTINCT " + KEY_BRAND + " FROM "
					+ DB_TABLE, null);
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
			Log.e("database fetch error getAllPattern", e.getMessage());
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
			Log.e("database fetch error getAllPattern", e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getDistinctSize() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT DISTINCT " + KEY_DIMEN + " FROM "
					+ DB_TABLE, null);
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
			Log.e("database fetch error getAllPattern", e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getDistinctColor() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT DISTINCT " + KEY_COLOR + " FROM "
					+ DB_TABLE, null);
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
			Log.e("database fetch error getAllPattern", e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> getDistinctType() {
		patternItems.clear();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT DISTINCT " + KEY_TYPE + " FROM "
					+ DB_TABLE, null);
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
			Log.e("database fetch error getAllPattern", e.getMessage());
		}
		return patternItems;

	}

	public ArrayList<HashMap<String, String>> searchkeyword(String keyword) {
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
			// Log.d("TAG", "SELECT * FROM " + DB_TABLE + " WHERE " + KEY_NAME
			// + " LIKE '%" + keyword + "%'");
			cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE "
					+ KEY_NAME + " LIKE '%" + keyword + "%'", null);
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
			Log.e("database fetch error getAllPattern", e.getMessage());
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
						dbResult.put(KEY_BRAND, cursor.getString(cursor
								.getColumnIndex(KEY_BRAND)));
						dbResult.put(
								KEY_NAME,
								cursor.getString(cursor
										.getColumnIndex(KEY_NAME)) + ".jpg");
						dbResult.put(KEY_DIMEN, cursor.getString(cursor
								.getColumnIndex(KEY_DIMEN)));
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
			Log.e("database fetch error getAllPattern", e.getMessage());
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
			Log.e("database fetch error getAllPattern", e.getMessage());
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
			Log.e("database fetch error getCustomPattern", e.getMessage());
		}
		return patternItems;

	}

}
