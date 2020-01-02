package com.nagainfomob.app.sql.db.model;

import android.net.Uri;

import com.nagainfomob.app.sql.ContentDescriptor;

/**
 * Created by Joy on 01/06/2017.
 */

public class SettingsTable {
    public static final String TABLE_NAME = "settings";
    public static final String PATH = "settings_table";
    public static final int PATH_TOKEN = 1001;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

    public static class Cols {
        public static final String ID = "id";
        public static final String DEVICE_TYPE = "devie_type";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String FCM_TOKEN = "fcm_token";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String RESOURCE_SERVER = "resource_server";
        public static final String LOCK_OUT_TIME = "lock_out_time";
        public static final String ACTIVE_USER = "active_user";
        public static final String UPDATED_AT = "updated_at";
        public static final String TIME_STAMP = "time_stamp";
    }
}
