package com.nagainfomob.app.sql.db.model;

import android.net.Uri;

import com.nagainfomob.app.sql.ContentDescriptor;

/**
 * Created by Joy on 01/06/2017.
 */

public class ProjectsTable {
    public static final String TABLE_NAME = "projects";
    public static final String PATH = "project_table";
    public static final int PATH_TOKEN = 1002;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

    public static class Cols {
        public static final String ID = "id";
        public static final String NAME = "project_name";
        public static final String PROJECT_ID = "project_id";
        public static final String PROJECT_TYPE = "project_type";
        public static final String CUST_NAME = "customer_name";
        public static final String CUST_MOB = "customer_mobile";
        public static final String SHARE_ID = "share_id";
        public static final String ROOM_TYPE = "room_type";
        public static final String UNIT_ID = "unit_id";
        public static final String UNIT_NAME = "unit_name";
        public static final String UNIT_SCALE = "unit_scale";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String DEPTH = "depth";
        public static final String IMG_ID = "img_id";
        public static final String IMG_URL = "img_url";
        public static final String USER_ID = "user_id";
        public static final String STATUS = "status";
        public static final String TIME_STAMP = "time_stamp";
    }
}