package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class StoryAdapter extends ArrayAdapter<Story> {

    StoryAdapter(Context context, List<Story> stories) {
        super(context, 0, stories);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.story_list_item, parent, false);
        }

        Story currentStory = getItem(position);

        TextView sectionView = listItemView.findViewById(R.id.section_name);
        assert currentStory != null;
        sectionView.setText(currentStory.getSection());

        TextView authorView = listItemView.findViewById(R.id.author_name);
        authorView.setText(currentStory.getAuthor());

        TextView titleView = listItemView.findViewById(R.id.story_title);
        titleView.setText(currentStory.getTitle());

        TextView dateView = listItemView.findViewById(R.id.story_date);
        String formattedDate = currentStory.getDate();
        dateView.setText(formattedDate);

        return listItemView;
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    String time=sdf.format(new Date());
}
