package com.nagainfomob.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nagainfomob.app.R;
import com.nagainfomob.app.fragments.LibraryFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joy on 23/04/18.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private LibraryFragment _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private List<String> arrayTiles = new ArrayList<>();

    public ExpandableListAdapter(LibraryFragment context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (/*convertView == null && */!this.arrayTiles.contains(groupPosition+"X"+childPosition)) {

            LayoutInflater infalInflater = (LayoutInflater) this._context.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);

            Log.d("DMD_Log", "URL from server - " + groupPosition+"X"+childPosition);

            CheckBox txtListChild = (CheckBox) convertView
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);

            txtListChild.setTextColor(_context.getResources().getColor(R.color.itemTitleColorLight));
            txtListChild.setTag(groupPosition+"X"+childPosition);
            txtListChild.setOnCheckedChangeListener(DownListListener);

            this.arrayTiles.add(groupPosition+"X"+childPosition);

        }

        /*TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);*/

        return convertView;
    }

    CompoundButton.OnCheckedChangeListener DownListListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    String t = buttonView.getTag().toString();
                    _context.setTiesForDownload(t, true);
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }

            }
            if (!isChecked) {
                try {
                    String t = buttonView.getTag().toString();
                    _context.setTiesForDownload(t, false);
                } catch (Exception e) {
                    Log.e("Filter", e.getMessage());
                }
            }

        }

    };

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) _context.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
//        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}