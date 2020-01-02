package com.nagainfomob.app.sql.db.model;

import android.net.Uri;

import com.nagainfomob.app.sql.ContentDescriptor;

/**
 * Created by Joy on 01/06/2017.
 */

public class UserTable {
    public static final String TABLE_NAME = "user";
    public static final String PATH = "user_table";
    public static final int PATH_TOKEN = 1000;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

    public static class Cols {
        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String USER_NAME = "user_name";
        public static final String COUNTRY_CODE = "country_code";
        public static final String MOBILE_NO = "mobile_no";
        public static final String EMAIL = "email";
        public static final String USER_TYPE = "user_type";
        public static final String SUB_TYPE = "subscription_type";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String IS_FIRST_LOGIN = "is_first_login";
        public static final String IS_ACTIVE = "is_active";
    }
}
