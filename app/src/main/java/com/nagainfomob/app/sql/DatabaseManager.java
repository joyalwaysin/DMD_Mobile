package com.nagainfomob.app.sql;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.nagainfomob.app.model.CompanyModel;
import com.nagainfomob.app.model.ProjectTilesModel;
import com.nagainfomob.app.model.ProjectsModel;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.model.UserModel;
import com.nagainfomob.app.sql.db.model.CompanyTable;
import com.nagainfomob.app.sql.db.model.ProjectTilesTable;
import com.nagainfomob.app.sql.db.model.ProjectsTable;
import com.nagainfomob.app.sql.db.model.SettingsTable;
import com.nagainfomob.app.sql.db.model.UserTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joy on 01/06/2017.
 */

public class DatabaseManager {

    public static void saveUser(Context context, UserModel user) {
        ContentValues values = getContentValuesUserTable(context, user);
        String condition = UserTable.Cols.USER_ID + "='" + user.getUserId()
                + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UserTable.CONTENT_URI, null, condition,
                null, null);

        if (cursor != null && cursor.getCount() > 0) {
            Log.d("DB: Update User>>> ", "Sucess");
            resolver.update(UserTable.CONTENT_URI, values, condition, null);
        } else {
            Log.d("DB: Insert User>>> ", "Sucess");
            resolver.insert(UserTable.CONTENT_URI, values);

        }

        if (cursor != null) {
            cursor.close();
        }
    }

    public static void addSettingsInfo(Context context, SettingsModel settingsModel) {
        ContentValues values = getContentValuesSettingsTable(context, settingsModel);
        String condition = SettingsTable.Cols.ID + "= " + settingsModel.getId() + "";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(SettingsTable.CONTENT_URI, null, condition,
                null, null);

        if (cursor != null && cursor.getCount() > 0) {
            resolver.update(SettingsTable.CONTENT_URI, values, null, null);
            Log.d("DB: ",  "Settings Updated");
        } else {
            resolver.insert(SettingsTable.CONTENT_URI, values);
            int c = cursor.getColumnCount();
            Log.d("DB: ",  "Settings Inserted");

        }

        if (cursor != null) {
            cursor.close();
        }
    }

    public static void addProjectsInfo(Context context, ProjectsModel projectsModel) {
        ContentValues values = getContentValuesProjectsTable(context, projectsModel);
        String condition = ProjectsTable.Cols.PROJECT_ID + "='" + projectsModel.getProject_id().trim() + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ProjectsTable.CONTENT_URI, null, condition,
                null, null);

        if (cursor != null && cursor.getCount() > 0) {
            resolver.update(ProjectsTable.CONTENT_URI, values, null, null);
            Log.d("DB: ",  "Project Updated_"+ projectsModel.getProject_id());
        } else {
            resolver.insert(ProjectsTable.CONTENT_URI, values);
            int c = cursor.getColumnCount();
            Log.d("DB: ",  "Project Inserted_"+ projectsModel.getProject_id());

        }

        if (cursor != null) {
            cursor.close();
        }
    }

    public static boolean deleteProject(Context context, String projectID) {
        String condition = ProjectsTable.Cols.PROJECT_ID + "= '" + projectID + "'";

        int cursor = context.getContentResolver().delete(
                ProjectsTable.CONTENT_URI,
                condition,
                null);

        if (cursor > 0) {
            return true;
        }
        return false;
    }

    public static void addProjectTilesInfo(Context context, ProjectTilesModel projectTilesModel) {
        ContentValues values = getContentValuesProjectTilesTable(context, projectTilesModel);
        ContentResolver resolver = context.getContentResolver();

        resolver.insert(ProjectTilesTable.CONTENT_URI, values);
        Log.d("DB: ",  "Project Tile Inserted");

    }

    public static void addCompanyInfo(Context context, CompanyModel companyModel) {
        ContentValues values = getContentValuesCompanyTable(context, companyModel);
        String condition = CompanyTable.Cols.USER_ID + "=" + companyModel.getUserId().trim() + "";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(CompanyTable.CONTENT_URI, null, condition,
                null, null);

        if (cursor != null && cursor.getCount() > 0) {
            resolver.update(CompanyTable.CONTENT_URI, values, condition, null);
            Log.d("DB: ",  "Company Updated_"+ companyModel.getUserId());
        } else {
            resolver.insert(CompanyTable.CONTENT_URI, values);
            int c = cursor.getColumnCount();
            Log.d("DB: ",  "Company Inserted_"+ companyModel.getUserId());

        }

        if (cursor != null) {
            cursor.close();
        }
    }

    public static List<UserModel> getUser(Context context, String userId) {
        List<UserModel> user = new ArrayList<UserModel>();
        String condition = UserTable.Cols.USER_ID + "= '" + userId + "'";
        Cursor cursor = context.getContentResolver().query(
                UserTable.CONTENT_URI,
                null,
                condition,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    UserModel userModel = new UserModel();
                    userModel.setUserId(cursor.getString(cursor.getColumnIndex(UserTable.Cols.USER_ID)));
                    userModel.setName(cursor.getString(cursor.getColumnIndex(UserTable.Cols.USER_NAME)));
                    userModel.setEmail(cursor.getString(cursor.getColumnIndex(UserTable.Cols.EMAIL)));
                    userModel.setCountry_code(cursor.getString(cursor.getColumnIndex(UserTable.Cols.COUNTRY_CODE)));
                    userModel.setMobile_no(cursor.getString(cursor.getColumnIndex(UserTable.Cols.MOBILE_NO)));
                    userModel.setUserType(cursor.getString(cursor.getColumnIndex(UserTable.Cols.USER_TYPE)));
                    userModel.setSubType(cursor.getString(cursor.getColumnIndex(UserTable.Cols.SUB_TYPE)));
                    userModel.setAccounType(cursor.getString(cursor.getColumnIndex(UserTable.Cols.ACCOUNT_TYPE)));

                    user.add(userModel);
                } while (cursor.moveToNext());
            }
        }
        return user;
    }

    public static List<SettingsModel> getSettings(Context context) {
        List<SettingsModel> settings = new ArrayList<SettingsModel>();
        String condition = SettingsTable.Cols.ID + "= '1'";
        Cursor cursor = context.getContentResolver().query(
                SettingsTable.CONTENT_URI,
                null,
                condition,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    SettingsModel settingsModel = new SettingsModel();
                    settingsModel.setId(cursor.getInt(cursor.getColumnIndex(SettingsTable.Cols.ID)));
                    settingsModel.setDevice_type(cursor.getString(cursor.getColumnIndex(SettingsTable.Cols.DEVICE_TYPE)));
                    settingsModel.setDevice_token(cursor.getString(cursor.getColumnIndex(SettingsTable.Cols.DEVICE_TOKEN)));
                    settingsModel.setFcm_token(cursor.getString(cursor.getColumnIndex(SettingsTable.Cols.FCM_TOKEN)));
                    settingsModel.setAccessToken(cursor.getString(cursor.getColumnIndex(SettingsTable.Cols.ACCESS_TOKEN)));
                    settingsModel.setAccessToken(cursor.getString(cursor.getColumnIndex(SettingsTable.Cols.ACCESS_TOKEN)));
                    settingsModel.setLock_out_time(cursor.getString(cursor.getColumnIndex(SettingsTable.Cols.LOCK_OUT_TIME)));
                    settingsModel.setActive_user(cursor.getString(cursor.getColumnIndex(SettingsTable.Cols.ACTIVE_USER)));
                    settingsModel.setUpdated_at(cursor.getString(cursor.getColumnIndex(SettingsTable.Cols.UPDATED_AT)));

                    settings.add(settingsModel);
                } while (cursor.moveToNext());
            }
        }
        return settings;
    }

    public static List<ProjectsModel> getProjects(Context context, String uid) {
        List<ProjectsModel> projects = new ArrayList<ProjectsModel>();
//        String condition = null;
        String condition = ProjectsTable.Cols.USER_ID + "=" + uid + "";
        String sort_order = "CAST("+ ProjectsTable.Cols.PROJECT_ID +" AS INTEGER)" + " DESC LIMIT 10";

        Cursor cursor = context.getContentResolver().query(
                ProjectsTable.CONTENT_URI,
                null,
                condition,
                null,
                sort_order);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ProjectsModel projectsModel = new ProjectsModel();
                    projectsModel.setProject_name(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.NAME)));
                    projectsModel.setProject_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.PROJECT_ID)));
                    projectsModel.setProject_type(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.PROJECT_TYPE)));
                    projectsModel.setCust_name(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.CUST_NAME)));
                    projectsModel.setCust_mob(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.CUST_MOB)));
                    projectsModel.setShare_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.SHARE_ID)));
                    projectsModel.setRoom_type(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.ROOM_TYPE)));
                    projectsModel.setUnit_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.UNIT_ID)));
                    projectsModel.setUnit_name(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.UNIT_NAME)));
                    projectsModel.setUnit_scale(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.UNIT_SCALE)));
                    projectsModel.setWidth(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.WIDTH)));
                    projectsModel.setHeight(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.HEIGHT)));
                    projectsModel.setDepth(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.DEPTH)));
                    projectsModel.setImg_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.IMG_ID)));
                    projectsModel.setImg_url(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.IMG_URL)));
                    projectsModel.setUser_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.USER_ID)));
                    projectsModel.setStatus(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.STATUS)));
                    projectsModel.setTime_stamp(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.TIME_STAMP)));

                    projects.add(projectsModel);
                } while (cursor.moveToNext());
            }
        }
        return projects;
    }

    public static List<ProjectsModel> getAllProjects(Context context, String uid) {
        List<ProjectsModel> projects = new ArrayList<ProjectsModel>();
//        String condition = null;
        String condition = ProjectsTable.Cols.USER_ID + "=" + uid + "";
        String sort_order = "CAST("+ ProjectsTable.Cols.PROJECT_ID +" AS INTEGER)" + " DESC";

        Cursor cursor = context.getContentResolver().query(
                ProjectsTable.CONTENT_URI,
                null,
                condition,
                null,
                sort_order);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ProjectsModel projectsModel = new ProjectsModel();
                    projectsModel.setProject_name(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.NAME)));
                    projectsModel.setProject_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.PROJECT_ID)));
                    projectsModel.setProject_type(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.PROJECT_TYPE)));
                    projectsModel.setCust_name(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.CUST_NAME)));
                    projectsModel.setCust_mob(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.CUST_MOB)));
                    projectsModel.setShare_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.SHARE_ID)));
                    projectsModel.setRoom_type(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.ROOM_TYPE)));
                    projectsModel.setUnit_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.UNIT_ID)));
                    projectsModel.setUnit_name(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.UNIT_NAME)));
                    projectsModel.setUnit_scale(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.UNIT_SCALE)));
                    projectsModel.setWidth(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.WIDTH)));
                    projectsModel.setHeight(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.HEIGHT)));
                    projectsModel.setDepth(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.DEPTH)));
                    projectsModel.setImg_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.IMG_ID)));
                    projectsModel.setImg_url(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.IMG_URL)));
                    projectsModel.setUser_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.USER_ID)));
                    projectsModel.setStatus(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.STATUS)));
                    projectsModel.setTime_stamp(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.TIME_STAMP)));

                    projects.add(projectsModel);
                } while (cursor.moveToNext());
            }
        }
        return projects;
    }

    public static List<ProjectsModel> getProject(Context context, String projectID) {
        List<ProjectsModel> projects = new ArrayList<ProjectsModel>();
        String condition = ProjectsTable.Cols.PROJECT_ID + "= '" + projectID + "'";
        Cursor cursor = context.getContentResolver().query(
                ProjectsTable.CONTENT_URI,
                null,
                condition,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ProjectsModel projectsModel = new ProjectsModel();
                    projectsModel.setProject_name(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.NAME)));
                    projectsModel.setProject_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.PROJECT_ID)));
                    projectsModel.setProject_type(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.PROJECT_TYPE)));
                    projectsModel.setCust_name(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.CUST_NAME)));
                    projectsModel.setCust_mob(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.CUST_MOB)));
                    projectsModel.setShare_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.SHARE_ID)));
                    projectsModel.setRoom_type(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.ROOM_TYPE)));
                    projectsModel.setUnit_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.UNIT_ID)));
                    projectsModel.setUnit_name(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.UNIT_NAME)));
                    projectsModel.setUnit_scale(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.UNIT_SCALE)));
                    projectsModel.setWidth(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.WIDTH)));
                    projectsModel.setHeight(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.HEIGHT)));
                    projectsModel.setDepth(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.DEPTH)));
                    projectsModel.setImg_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.IMG_ID)));
                    projectsModel.setImg_url(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.IMG_URL)));
                    projectsModel.setUser_id(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.USER_ID)));
                    projectsModel.setStatus(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.STATUS)));
                    projectsModel.setTime_stamp(cursor.getString(cursor.getColumnIndex(ProjectsTable.Cols.TIME_STAMP)));

                    projects.add(projectsModel);
                } while (cursor.moveToNext());
            }
        }
        return projects;
    }

    public static boolean checkProject(Context context, String projectID) {
        String condition = ProjectsTable.Cols.PROJECT_ID + "= '" + projectID + "'";
        Cursor cursor = context.getContentResolver().query(
                ProjectsTable.CONTENT_URI,
                null,
                condition,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public static List<ProjectTilesModel> getProjectTiles(Context context, String projectID) {
        List<ProjectTilesModel> projects = new ArrayList<ProjectTilesModel>();
        String condition = ProjectTilesTable.Cols.PROJECT_ID + "= '" + projectID + "'";
//                +") GROUP BY ("+ ProjectTilesTable.Cols.WALL_TYPE+", "+ProjectTilesTable.Cols.TILE_ID;

        Cursor cursor = context.getContentResolver().query(
                ProjectTilesTable.CONTENT_URI,
                null,
                condition,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ProjectTilesModel projectTilesModel = new ProjectTilesModel();

                    projectTilesModel.setWall_type(cursor.getString(cursor.getColumnIndex(ProjectTilesTable.Cols.WALL_TYPE)));
                    projectTilesModel.setTile_id(cursor.getString(cursor.getColumnIndex(ProjectTilesTable.Cols.TILE_ID)));
                    projectTilesModel.setTile_count(cursor.getString(cursor.getColumnIndex(ProjectTilesTable.Cols.TILE_COUNT)));
                    projectTilesModel.setTile_type(cursor.getString(cursor.getColumnIndex(ProjectTilesTable.Cols.TILE_TYPE)));
                    projectTilesModel.setSel_w(cursor.getString(cursor.getColumnIndex(ProjectTilesTable.Cols.SEL_W)));
                    projectTilesModel.setSel_h(cursor.getString(cursor.getColumnIndex(ProjectTilesTable.Cols.SEL_H)));

                    projects.add(projectTilesModel);
                } while (cursor.moveToNext());
            }
        }
        return projects;
    }

    public static boolean deleteProjectTiles(Context context, String projectID, String wall) {
        String condition = ProjectTilesTable.Cols.PROJECT_ID + "= '" + projectID + "' AND " +
                ProjectTilesTable.Cols.WALL_TYPE + "= '" + wall + "'";

        int cursor = context.getContentResolver().delete(
                ProjectTilesTable.CONTENT_URI,
                condition,
                null);

        if (cursor > 0) {
            return true;
        }
        return false;
    }

    public static List<CompanyModel> getCompany(Context context, String userId) {
        List<CompanyModel> comp = new ArrayList<CompanyModel>();
        String condition = CompanyTable.Cols.USER_ID + "= '" + userId + "'";
        Cursor cursor = context.getContentResolver().query(
                CompanyTable.CONTENT_URI,
                null,
                condition,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    CompanyModel compModel = new CompanyModel();
                    compModel.setUserId(cursor.getString(cursor.getColumnIndex(CompanyTable.Cols.USER_ID)));
                    compModel.setComp_name(cursor.getString(cursor.getColumnIndex(CompanyTable.Cols.COMP_NAME)));
                    compModel.setComp_desc(cursor.getString(cursor.getColumnIndex(CompanyTable.Cols.COMP_DESC)));
                    compModel.setComp_addr1(cursor.getString(cursor.getColumnIndex(CompanyTable.Cols.COMP_ADDR1)));
                    compModel.setComp_addr2(cursor.getString(cursor.getColumnIndex(CompanyTable.Cols.COMP_ADDR2)));
                    compModel.setComp_logo_url(cursor.getString(cursor.getColumnIndex(CompanyTable.Cols.COMP_LOGO_URL)));
                    compModel.setComp_phone_number(cursor.getString(cursor.getColumnIndex(CompanyTable.Cols.COMP_NUMBER)));
                    compModel.setComp_country_code(cursor.getString(cursor.getColumnIndex(CompanyTable.Cols.COMP_COUNTRY_CODE)));
                    compModel.setComp_country_name(cursor.getString(cursor.getColumnIndex(CompanyTable.Cols.COMP_COUNTRY_NAME)));

                    comp.add(compModel);
                } while (cursor.moveToNext());
            }
        }
        return comp;
    }

    /*public static boolean checkUser(Context context, String uid, String password) {

        Cursor cursor = context.getContentResolver().query(
                UserTable.CONTENT_URI, null,
                UserTable.Cols.USER_ID + " = '" + uid + "' AND "+
                        UserTable.Cols.PASSWORD + "='"+password+"'", null,
                null);

        if (cursor.getCount() >= 1) {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }*/

    public static Boolean getRegisteredUser(Context context, String uid) {
        String password = null;
        Cursor cursor = context.getContentResolver().query(
                UserTable.CONTENT_URI, null,
                UserTable.Cols.USER_ID + " = '" + uid + "'", null,
                null);
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

   /* public static boolean checkOldPasswd(Context context, String passwd) {
        String password = null;
        Cursor cursor = context.getContentResolver().query(
                UserTable.CONTENT_URI, null,
                UserTable.Cols.PASSWORD + " = '" + passwd + "'", null,
                null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }*/

    private static ContentValues getContentValuesUserTable(Context context,
                                                           UserModel user) {
        ContentValues values = new ContentValues();
        if (user.getUserId() != null)
            values.put(UserTable.Cols.USER_ID, user.getUserId());
        if (user.getName() != null)
            values.put(UserTable.Cols.USER_NAME, user.getName());
        if (user.getEmail() != null)
            values.put(UserTable.Cols.EMAIL, user.getEmail());
        if (user.getCountry_code() != null)
            values.put(UserTable.Cols.COUNTRY_CODE, user.getCountry_code());
        if (user.getMobile_no() != null)
            values.put(UserTable.Cols.MOBILE_NO, user.getMobile_no());
        if (user.getUserType() != null)
            values.put(UserTable.Cols.USER_TYPE, user.getUserType());
        if (user.getSubType() != null)
            values.put(UserTable.Cols.SUB_TYPE, user.getSubType());
        if (user.getAccounType() != null)
            values.put(UserTable.Cols.ACCOUNT_TYPE, user.getAccounType());
        if (user.getIs_first_login() != null)
            values.put(UserTable.Cols.IS_FIRST_LOGIN, user.getIs_first_login());
        if (user.getIs_active() != null)
            values.put(UserTable.Cols.IS_ACTIVE, user.getIs_active());

        return values;
    }

    private static ContentValues getContentValuesSettingsTable(Context context,
                                                               SettingsModel settings) {
        ContentValues values = new ContentValues();

        if (settings.getId() != 0)
            values.put(SettingsTable.Cols.ID, settings.getId());
        if (settings.getDevice_type() != null)
            values.put(SettingsTable.Cols.DEVICE_TYPE, settings.getDevice_type());
        if (settings.getDevice_token() != null)
            values.put(SettingsTable.Cols.DEVICE_TOKEN, settings.getDevice_token());
        if (settings.getFcm_token() != null)
            values.put(SettingsTable.Cols.FCM_TOKEN, settings.getFcm_token());
        if (settings.getAccessToken() != null)
            values.put(SettingsTable.Cols.ACCESS_TOKEN, settings.getAccessToken());
        if (settings.getLock_out_time() != null)
            values.put(SettingsTable.Cols.LOCK_OUT_TIME, settings.getLock_out_time());
        if (settings.getTime_stamp() != null)
            values.put(SettingsTable.Cols.TIME_STAMP, settings.getTime_stamp());
        if (settings.getActive_user() != null)
            values.put(SettingsTable.Cols.ACTIVE_USER, settings.getActive_user());
        if (settings.getUpdated_at() != null)
            values.put(SettingsTable.Cols.UPDATED_AT, settings.getUpdated_at());

        return values;
    }

    private static ContentValues getContentValuesProjectsTable(Context context,
                                                               ProjectsModel projects) {
        ContentValues values = new ContentValues();

        if (projects.getId() != 0)
            values.put(ProjectsTable.Cols.ID, projects.getId());
        if (projects.getProject_name() != null)
            values.put(ProjectsTable.Cols.NAME, projects.getProject_name());
        if (projects.getProject_id() != null)
            values.put(ProjectsTable.Cols.PROJECT_ID, projects.getProject_id());
        if (projects.getProject_type() != null)
            values.put(ProjectsTable.Cols.PROJECT_TYPE, projects.getProject_type());
        if (projects.getCust_name() != null)
            values.put(ProjectsTable.Cols.CUST_NAME, projects.getCust_name());
        if (projects.getCust_mob() != null)
            values.put(ProjectsTable.Cols.CUST_MOB, projects.getCust_mob());
        if (projects.getShare_id() != null)
            values.put(ProjectsTable.Cols.SHARE_ID, projects.getShare_id());
        if (projects.getRoom_type() != null)
            values.put(ProjectsTable.Cols.ROOM_TYPE, projects.getRoom_type());
        if (projects.getUnit_id() != null)
            values.put(ProjectsTable.Cols.UNIT_ID, projects.getUnit_id());
        if (projects.getUnit_name() != null)
            values.put(ProjectsTable.Cols.UNIT_NAME, projects.getUnit_name());
        if (projects.getUnit_scale() != null)
            values.put(ProjectsTable.Cols.UNIT_SCALE, projects.getUnit_scale());
        if (projects.getWidth() != null)
            values.put(ProjectsTable.Cols.WIDTH, projects.getWidth());
        if (projects.getHeight() != null)
            values.put(ProjectsTable.Cols.HEIGHT, projects.getHeight());
        if (projects.getDepth() != null)
            values.put(ProjectsTable.Cols.DEPTH, projects.getDepth());
        if (projects.getImg_id() != null)
            values.put(ProjectsTable.Cols.IMG_ID, projects.getImg_id());
        if (projects.getImg_url() != null)
            values.put(ProjectsTable.Cols.IMG_URL, projects.getImg_url());
        if (projects.getUser_id() != null)
            values.put(ProjectsTable.Cols.USER_ID, projects.getUser_id());
        if (projects.getStatus() != null)
            values.put(ProjectsTable.Cols.STATUS, projects.getStatus());
        if (projects.getTime_stamp() != null)
            values.put(ProjectsTable.Cols.TIME_STAMP, projects.getTime_stamp());

        return values;
    }

    private static ContentValues getContentValuesProjectTilesTable(Context context,
                                                                   ProjectTilesModel projectTilesModel) {
        ContentValues values = new ContentValues();

        if (projectTilesModel.getId() != 0)
            values.put(ProjectTilesTable.Cols.ID, projectTilesModel.getId());

        if (projectTilesModel.getProject_id() != null)
            values.put(ProjectTilesTable.Cols.PROJECT_ID, projectTilesModel.getProject_id());

        if (projectTilesModel.getWall_type() != null)
            values.put(ProjectTilesTable.Cols.WALL_TYPE, projectTilesModel.getWall_type());

        if (projectTilesModel.getTile_id() != null)
            values.put(ProjectTilesTable.Cols.TILE_ID, projectTilesModel.getTile_id());

        if (projectTilesModel.getTile_count() != null)
            values.put(ProjectTilesTable.Cols.TILE_COUNT, projectTilesModel.getTile_count());

        if (projectTilesModel.getTile_type() != null)
            values.put(ProjectTilesTable.Cols.TILE_TYPE, projectTilesModel.getTile_type());

        if (projectTilesModel.getSel_w() != null)
            values.put(ProjectTilesTable.Cols.SEL_W, projectTilesModel.getSel_w());

        if (projectTilesModel.getSel_h() != null)
            values.put(ProjectTilesTable.Cols.SEL_H, projectTilesModel.getSel_h());


        return values;
    }

    private static ContentValues getContentValuesCompanyTable(Context context,
                                                              CompanyModel companyModel) {
        ContentValues values = new ContentValues();

        if (companyModel.getId() != 0)
            values.put(CompanyTable.Cols.ID, companyModel.getId());

        if (companyModel.getUserId() != null)
            values.put(CompanyTable.Cols.USER_ID, companyModel.getUserId());

        if (companyModel.getComp_name() != null)
            values.put(CompanyTable.Cols.COMP_NAME, companyModel.getComp_name());

        if (companyModel.getComp_desc() != null)
            values.put(CompanyTable.Cols.COMP_DESC, companyModel.getComp_desc());

        if (companyModel.getComp_addr1() != null)
            values.put(CompanyTable.Cols.COMP_ADDR1, companyModel.getComp_addr1());

        if (companyModel.getComp_addr2() != null)
            values.put(CompanyTable.Cols.COMP_ADDR2, companyModel.getComp_addr2());

        if (companyModel.getComp_logo_url() != null)
            values.put(CompanyTable.Cols.COMP_LOGO_URL, companyModel.getComp_logo_url());

        if (companyModel.getComp_phone_number() != null)
            values.put(CompanyTable.Cols.COMP_NUMBER, companyModel.getComp_phone_number());

        if (companyModel.getComp_country_code() != null)
            values.put(CompanyTable.Cols.COMP_COUNTRY_CODE, companyModel.getComp_country_code());

        if (companyModel.getComp_country_name() != null)
            values.put(CompanyTable.Cols.COMP_COUNTRY_NAME, companyModel.getComp_country_name());


        return values;
    }

    /*public static CursorLoader getDeviceDetailsCursorLoader(Context context, String device_id) {

        Uri str = DeviceTable.CONTENT_URI;

        return new CursorLoader(context, DeviceTable.CONTENT_URI, null, DeviceTable.Cols.DEVICE_ID + "=" + device_id, null, null);
    }*/
}