package com.nagainfomob.app.sql.db.model;

import android.net.Uri;

import com.nagainfomob.app.sql.ContentDescriptor;

/**
 * Created by Joy on 01/06/2017.
 */

public class CompanyTable {
    public static final String TABLE_NAME = "company";
    public static final String PATH = "company_table";
    public static final int PATH_TOKEN = 1004;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

    public static class Cols {
        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String COMP_NAME = "comp_name";
        public static final String COMP_DESC = "comp_desc";
        public static final String COMP_NUMBER = "comp_phone_number";
        public static final String COMP_LOGO_URL = "comp_logo_url";
        public static final String COMP_ADDR1 = "comp_addr1";
        public static final String COMP_ADDR2 = "comp_addr2";
        public static final String COMP_COUNTRY_CODE = "comp_country_code";
        public static final String COMP_COUNTRY_NAME = "comp_country_name";
    }
}
