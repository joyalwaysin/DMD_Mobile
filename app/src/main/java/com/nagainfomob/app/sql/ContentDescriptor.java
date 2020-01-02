package com.nagainfomob.app.sql;

import android.content.UriMatcher;
import android.net.Uri;

import com.nagainfomob.app.sql.db.model.CompanyTable;
import com.nagainfomob.app.sql.db.model.ProjectTilesTable;
import com.nagainfomob.app.sql.db.model.ProjectsTable;
import com.nagainfomob.app.sql.db.model.SettingsTable;
import com.nagainfomob.app.sql.db.model.UserTable;


/**
 * Created by root on 5/2/17.
 */

public class ContentDescriptor {

    public static final String AUTHORITY = "com.nagainfomob.dmd.database";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY, UserTable.PATH, UserTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, SettingsTable.PATH, SettingsTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, ProjectsTable.PATH, ProjectsTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, ProjectTilesTable.PATH, ProjectTilesTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, CompanyTable.PATH, CompanyTable.PATH_TOKEN);

        return matcher;

    }

}