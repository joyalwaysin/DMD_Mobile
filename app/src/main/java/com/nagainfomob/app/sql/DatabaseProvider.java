package com.nagainfomob.app.sql;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.nagainfomob.app.sql.db.model.CompanyTable;
import com.nagainfomob.app.sql.db.model.ProjectTilesTable;
import com.nagainfomob.app.sql.db.model.ProjectsTable;
import com.nagainfomob.app.sql.db.model.SettingsTable;
import com.nagainfomob.app.sql.db.model.UserTable;

/**
 * Created by Joy on 01/06/2017.
 */

public class DatabaseProvider extends ContentProvider {

    private static final String UNKNOWN_URI = "Unknown URI ";
    private DatabaseHelper dbHelper;


    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        dbHelper.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final int token = ContentDescriptor.URI_MATCHER.match(uri);

        Cursor result = null;

        switch (token) {
            case UserTable.PATH_TOKEN: {
                result = doQuery(db, uri, UserTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case SettingsTable.PATH_TOKEN: {
                result = doQuery(db, uri, SettingsTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case ProjectsTable.PATH_TOKEN: {
                result = doQuery(db, uri, ProjectsTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case ProjectTilesTable.PATH_TOKEN: {
                result = doQuery(db, uri, ProjectTilesTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case CompanyTable.PATH_TOKEN: {
                result = doQuery(db, uri, CompanyTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }

        }
        return result;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = ContentDescriptor.URI_MATCHER.match(uri);

        Uri result = null;

        switch (token) {
            case UserTable.PATH_TOKEN: {
                result = doInsert(db, UserTable.TABLE_NAME, UserTable.CONTENT_URI,
                        uri, values);
                break;
            }
            case SettingsTable.PATH_TOKEN: {
                result = doInsert(db, SettingsTable.TABLE_NAME,
                        SettingsTable.CONTENT_URI, uri, values);
                break;
            }
            case ProjectsTable.PATH_TOKEN: {
                result = doInsert(db, ProjectsTable.TABLE_NAME,
                        ProjectsTable.CONTENT_URI, uri, values);
                break;
            }
            case ProjectTilesTable.PATH_TOKEN: {
                result = doInsert(db, ProjectTilesTable.TABLE_NAME,
                        ProjectTilesTable.CONTENT_URI, uri, values);
                break;
            }
            case CompanyTable.PATH_TOKEN: {
                result = doInsert(db, CompanyTable.TABLE_NAME,
                        CompanyTable.CONTENT_URI, uri, values);
                break;
            }
        }
        if (result == null) {
            throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }

        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = ContentDescriptor.URI_MATCHER.match(uri);

        int result = 0;

        switch (token) {
            case UserTable.PATH_TOKEN: {
                result = doDelete(db, uri, UserTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
            case SettingsTable.PATH_TOKEN: {
                result = doDelete(db, uri, SettingsTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
            case ProjectsTable.PATH_TOKEN: {
                result = doDelete(db, uri, ProjectsTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
            case ProjectTilesTable.PATH_TOKEN: {
                result = doDelete(db, uri, ProjectTilesTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
            case CompanyTable.PATH_TOKEN: {
                result = doDelete(db, uri, CompanyTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = ContentDescriptor.URI_MATCHER.match(uri);

        int result = 0;

        switch (token) {
            case UserTable.PATH_TOKEN: {
                result = doUpdate(db, uri, UserTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case SettingsTable.PATH_TOKEN: {
                result = doUpdate(db, uri, SettingsTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case ProjectsTable.PATH_TOKEN: {
                result = doUpdate(db, uri, ProjectsTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case ProjectTilesTable.PATH_TOKEN: {
                result = doUpdate(db, uri, ProjectTilesTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case CompanyTable.PATH_TOKEN: {
                result = doUpdate(db, uri, CompanyTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
        }
        return result;
    }

    private Cursor doQuery(SQLiteDatabase db, Uri uri, String tableName,
                           String[] projection, String selection, String[] selectionArgs,
                           String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tableName);
        Cursor result = builder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    private int doUpdate(SQLiteDatabase db, Uri uri, String tableName,
                         String selection, String[] selectionArgs, ContentValues values) {
        int result = db.update(tableName, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    private int doDelete(SQLiteDatabase db, Uri uri, String tableName,
                         String selection, String[] selectionArgs) {
        int result = db.delete(tableName, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    private Uri doInsert(SQLiteDatabase db, String tableName, Uri contentUri,
                         Uri uri, ContentValues values) {
        try {
            long id = db.insert(tableName, null, values);
            Uri result = contentUri.buildUpon().appendPath(String.valueOf(id))
                    .build();
            getContext().getContentResolver().notifyChange(uri, null);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}