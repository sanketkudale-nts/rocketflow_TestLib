package com.tracki.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.tracki.R;
import com.tracki.data.model.response.config.Navigation;
import com.tracki.ui.custom.AnimatedExpandableListView;
import com.tracki.ui.custom.GlideApp;
import com.tracki.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 14/11/18
 */
public class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private MainActivity _context;
    private List<Navigation> menuList;

    ExpandableListAdapter(MainActivity context, List<Navigation> menuList) {
        this._context = context;
        this.menuList = menuList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return menuList.get(groupPosition).getNestedMenu();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @NonNull
    @Override
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                                 View convertView, @NonNull ViewGroup parent) {
        @SuppressWarnings("unchecked") final List<Navigation> sublist_menus = (ArrayList) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_list_my_account, null);
        }
        TextView txtListChild = convertView.findViewById(R.id.tv);
        txtListChild.setText(sublist_menus.get(childPosition).getTitle());
        final ImageView ivRightArrow = convertView.findViewById(R.id.ivRightArrow);
        if (sublist_menus.get(childPosition).getNestedMenu() != null) {
            ivRightArrow.setVisibility(View.VISIBLE);
        } else {
            ivRightArrow.setVisibility(View.GONE);
        }
        final LinearLayout ll_sublist = convertView.findViewById(R.id.ll_sublist);
        ll_sublist.setVisibility(View.GONE);
        if (sublist_menus.get(childPosition).getActionConfig() != null) {
            convertView.setOnClickListener(view -> {
                if (sublist_menus.get(childPosition).getNestedMenu() == null||sublist_menus.get(childPosition).getNestedMenu().isEmpty()) {
                    _context.setSelectedPosition(groupPosition);
                    _context.showMenuSelected(sublist_menus.get(childPosition));
                } else {
                    if (ll_sublist.getVisibility() == View.GONE) {
                        _context.insideSubListView(sublist_menus.get(childPosition).getNestedMenu(), ll_sublist);
                        CommonUtils.expandText(ll_sublist);
//                        CommonUtils.rotate(0f, 90f, ivRightArrow);
                        CommonUtils.rotateArrowAnother(ivRightArrow, false);
                    } else {
                        CommonUtils.collapseText(ll_sublist);
                        CommonUtils.rotateArrowAnother(ivRightArrow, true);
//                        CommonUtils.rotate(90f, 0f, ivRightArrow);
                    }
                }
            });

        }

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        if (menuList.get(groupPosition).getNestedMenu() != null)
            return menuList.get(groupPosition).getNestedMenu().size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (menuList != null) {
            return menuList.get(groupPosition);
        } else {
            return null;
        }
    }

    @Override
    public int getGroupCount() {
        if (menuList != null) {
            return menuList.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final Navigation groupData = (Navigation) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_accout_menu, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.tv);
        ImageView ivMenuListIcon = convertView.findViewById(R.id.ivMenuListIcon);
        ImageView ivRightArrow = convertView.findViewById(R.id.ivRightArrow);
        lblListHeader.setText(groupData.getTitle());
        String picUrl = null;
        if (groupData.getThumbnailURL() != null && !"".equals(groupData.getThumbnailURL()) &&
                (groupData.getThumbnailURL().startsWith("http://") ||
                        groupData.getThumbnailURL().startsWith("https://"))) {
            picUrl = groupData.getThumbnailURL();
        }
        GlideApp.with(_context)
                .asBitmap()
                .load(((picUrl == null) ? R.drawable.ic_clipboard : picUrl))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_clipboard))
                .error(R.drawable.ic_clipboard)
                .into(ivMenuListIcon);



        if (groupData.getNestedMenu() != null && groupData.getNestedMenu().size() > 0) {
            ivRightArrow.setVisibility(View.VISIBLE);
        } else {
            ivRightArrow.setVisibility(View.GONE);
        }
        if (isExpanded) {
            ivRightArrow.setImageResource(R.drawable.ic_arrow_down_grey_24dp);
        } else {
            ivRightArrow.setImageResource(R.drawable.ic_arrow_right_orrange);
        }
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

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

}
