package com.nagainfomob.app.model;

/**
 * Created by Joy on 01/06/2017.
 */
public class ProjectTilesModel {

    private int id;
    private String project_id;
    private String wall_type;
    private String tile_id;
    private String tile_count;
    private String tile_type;
    private String sel_w;
    private String sel_h;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getProject_id() {
        return project_id;
    }
    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getWall_type() {
        return wall_type;
    }
    public void setWall_type(String wall_type) {
        this.wall_type = wall_type;
    }

    public String getTile_id() {
        return tile_id;
    }
    public void setTile_id(String tile_id) {
        this.tile_id = tile_id;
    }

    public String getTile_count() {
        return tile_count;
    }
    public void setTile_count(String tile_count) {
        this.tile_count = tile_count;
    }

    public String getTile_type() {
        return tile_type;
    }
    public void setTile_type(String tile_type) {
        this.tile_type = tile_type;
    }

    public String getSel_w() {
        return sel_w;
    }
    public void setSel_w(String sel_w) {
        this.sel_w = sel_w;
    }

    public String getSel_h() {
        return sel_h;
    }
    public void setSel_h(String sel_h) {
        this.sel_h = sel_h;
    }


}
