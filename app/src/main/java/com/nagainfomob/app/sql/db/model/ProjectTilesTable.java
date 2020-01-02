package com.nagainfomob.app.sql.db.model;

import android.net.Uri;

import com.nagainfomob.app.sql.ContentDescriptor;


/**
 * Created by Joy on 01/06/2017.
 */

public class ProjectTilesTable {
    public static final String TABLE_NAME = "project_tiles";
    public static final String PATH = "project_tiles";
    public static final int PATH_TOKEN = 1003;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

    public static class Cols {
        public static final String ID = "id";
        public static final String PROJECT_ID = "project_id";
        public static final String WALL_TYPE = "wall_type";
        public static final String TILE_ID = "tile_id";
        public static final String TILE_COUNT = "tile_count";
        public static final String TILE_TYPE = "tile_type";
        public static final String SEL_W = "sel_w";
        public static final String SEL_H = "sel_h";
    }
}
