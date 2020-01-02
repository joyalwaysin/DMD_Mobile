package com.nagainfomob.app.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nagainfomob.app.sql.db.model.CompanyTable;
import com.nagainfomob.app.sql.db.model.ProjectTilesTable;
import com.nagainfomob.app.sql.db.model.ProjectsTable;
import com.nagainfomob.app.sql.db.model.SettingsTable;
import com.nagainfomob.app.sql.db.model.UserTable;

import java.text.MessageFormat;

/**
 * Created by Joy on 13/11/2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String KEY_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS {0} ({1})";
    public static final String KEY_DROP_TABLE = "DROP TABLE IF EXISTS {0}";
    public static final String KEY_ALTER_TABLE = "ALTER TABLE {0} ADD COLUMN {1}";
    private static final int CURRENT_DB_VERSION = 12;
    private static final String DB_NAME = "DMD_DB";
    private Context context;

    //constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, CURRENT_DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserTable(db);
        createSettingsTable(db);
        createProjectsTable(db);
        createProjectTilesTable(db);
        createCompanyTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db, UserTable.TABLE_NAME);
        dropTable(db, SettingsTable.TABLE_NAME);
        dropTable(db, ProjectsTable.TABLE_NAME);
        dropTable(db, ProjectTilesTable.TABLE_NAME);
        dropTable(db, CompanyTable.TABLE_NAME);
    }

    // Table for storing MobiRemit User informations
    private void createUserTable(SQLiteDatabase db) {
        StringBuilder userTableFields = new StringBuilder();
        userTableFields.append(UserTable.Cols.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(UserTable.Cols.USER_ID).append(" TEXT UNIQUE , ")
                .append(UserTable.Cols.USER_NAME).append(" TEXT, ")
                .append(UserTable.Cols.COUNTRY_CODE).append(" TEXT, ")
                .append(UserTable.Cols.MOBILE_NO).append(" TEXT, ")
                .append(UserTable.Cols.EMAIL).append(" TEXT, ")
                .append(UserTable.Cols.USER_TYPE).append(" TEXT, ")
                .append(UserTable.Cols.SUB_TYPE).append(" TEXT, ")
                .append(UserTable.Cols.ACCOUNT_TYPE).append(" TEXT, ")
                .append(UserTable.Cols.IS_ACTIVE).append(" TEXT, ")
                .append(UserTable.Cols.IS_FIRST_LOGIN).append(" TEXT ");
        createTable(db, UserTable.TABLE_NAME, userTableFields.toString());
    }

    // Table for storing Settings
    private void createSettingsTable(SQLiteDatabase db) {
        StringBuilder settingsTableFields = new StringBuilder();
        settingsTableFields
                .append(SettingsTable.Cols.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(SettingsTable.Cols.DEVICE_TYPE).append(" TEXT, ")
                .append(SettingsTable.Cols.DEVICE_TOKEN).append(" TEXT, ")
                .append(SettingsTable.Cols.FCM_TOKEN).append(" TEXT, ")
                .append(SettingsTable.Cols.ACCESS_TOKEN).append(" TEXT, ")
                .append(SettingsTable.Cols.RESOURCE_SERVER).append(" TEXT, ")
                .append(SettingsTable.Cols.LOCK_OUT_TIME).append(" TEXT, ")
                .append(SettingsTable.Cols.ACTIVE_USER).append(" TEXT, ")
                .append(SettingsTable.Cols.UPDATED_AT).append(" TEXT, ")
                .append(SettingsTable.Cols.TIME_STAMP).append(" TEXT ");
        createTable(db, SettingsTable.TABLE_NAME, settingsTableFields.toString());
    }

    // Table for storing Projects
    private void createProjectsTable(SQLiteDatabase db) {
        StringBuilder projectsTableFields = new StringBuilder();
        projectsTableFields
                .append(ProjectsTable.Cols.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ProjectsTable.Cols.NAME).append(" TEXT, ")
                .append(ProjectsTable.Cols.PROJECT_ID).append(" TEXT, ")
                .append(ProjectsTable.Cols.PROJECT_TYPE).append(" TEXT, ")
                .append(ProjectsTable.Cols.CUST_NAME).append(" TEXT, ")
                .append(ProjectsTable.Cols.CUST_MOB).append(" TEXT, ")
                .append(ProjectsTable.Cols.SHARE_ID).append(" TEXT, ")
                .append(ProjectsTable.Cols.ROOM_TYPE).append(" TEXT, ")
                .append(ProjectsTable.Cols.UNIT_ID).append(" TEXT, ")
                .append(ProjectsTable.Cols.UNIT_NAME).append(" TEXT, ")
                .append(ProjectsTable.Cols.UNIT_SCALE).append(" TEXT, ")
                .append(ProjectsTable.Cols.WIDTH).append(" TEXT, ")
                .append(ProjectsTable.Cols.HEIGHT).append(" TEXT, ")
                .append(ProjectsTable.Cols.DEPTH).append(" TEXT, ")
                .append(ProjectsTable.Cols.IMG_ID).append(" TEXT, ")
                .append(ProjectsTable.Cols.IMG_URL).append(" TEXT, ")
                .append(ProjectsTable.Cols.USER_ID).append(" TEXT, ")
                .append(ProjectsTable.Cols.STATUS).append(" TEXT, ")
                .append(ProjectsTable.Cols.TIME_STAMP).append(" TEXT ");
        createTable(db, ProjectsTable.TABLE_NAME, projectsTableFields.toString());
    }

    // Table for storing Projects
    private void createProjectTilesTable(SQLiteDatabase db) {
        StringBuilder fields = new StringBuilder();
        fields
                .append(ProjectTilesTable.Cols.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ProjectTilesTable.Cols.PROJECT_ID).append(" TEXT, ")
                .append(ProjectTilesTable.Cols.WALL_TYPE).append(" TEXT, ")
                .append(ProjectTilesTable.Cols.TILE_ID).append(" TEXT, ")
                .append(ProjectTilesTable.Cols.TILE_COUNT).append(" TEXT, ")
                .append(ProjectTilesTable.Cols.TILE_TYPE).append(" TEXT, ")
                .append(ProjectTilesTable.Cols.SEL_W).append(" TEXT, ")
                .append(ProjectTilesTable.Cols.SEL_H).append(" TEXT ");
        createTable(db, ProjectTilesTable.TABLE_NAME, fields.toString());
    }

    // Table for storing Company informations
    private void createCompanyTable(SQLiteDatabase db) {
        StringBuilder compTableFields = new StringBuilder();
        compTableFields.append(CompanyTable.Cols.ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(CompanyTable.Cols.USER_ID).append(" TEXT UNIQUE , ")
                .append(CompanyTable.Cols.COMP_NAME).append(" TEXT, ")
                .append(CompanyTable.Cols.COMP_DESC).append(" TEXT, ")
                .append(CompanyTable.Cols.COMP_ADDR1).append(" TEXT, ")
                .append(CompanyTable.Cols.COMP_ADDR2).append(" TEXT, ")
                .append(CompanyTable.Cols.COMP_LOGO_URL).append(" TEXT, ")
                .append(CompanyTable.Cols.COMP_NUMBER).append(" TEXT, ")
                .append(CompanyTable.Cols.COMP_COUNTRY_NAME).append(" TEXT, ")
                .append(CompanyTable.Cols.COMP_COUNTRY_CODE).append(" TEXT ");
        createTable(db, CompanyTable.TABLE_NAME, compTableFields.toString());
    }

    public void dropTable(SQLiteDatabase db, String name) {
        String query = MessageFormat
                .format(DatabaseHelper.KEY_DROP_TABLE, name);
        db.execSQL(query);
    }

    public void createTable(SQLiteDatabase db, String name, String fields) {
        String query = MessageFormat.format(DatabaseHelper.KEY_CREATE_TABLE,
                name, fields);
        db.execSQL(query);
    }

    public void updateTable(SQLiteDatabase db, String name, String fields) {
        String query = MessageFormat.format(DatabaseHelper.KEY_ALTER_TABLE,
                name, fields);
        db.execSQL(query);
    }


}
