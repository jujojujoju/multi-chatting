package com.example.joju.client;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends ArrayAdapter<UserProfile>{
    private List<UserProfile> userProfileList = new ArrayList<UserProfile>();
    private Context context;

    @Override
    public void add(UserProfile object) {
        userProfileList.add(object);
        super.add(object);
    }

    public UserListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.userProfileList.size();
    }

    public UserProfile getItem(int index) {
        return this.userProfileList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        UserProfile userProfile = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        row = inflater.inflate(R.layout.userprofile, parent, false);
        TextView nameText = (TextView) row.findViewById(R.id.name);
        nameText.setText(userProfile.getName());
        TextView statusText = (TextView) row.findViewById(R.id.status);
        statusText.setText(userProfile.getStatus());
        return row;
    }
}
