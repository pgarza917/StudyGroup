package com.example.studygroup.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygroup.R;
import com.example.studygroup.models.Event;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context mContext;
    private List<Event> mEventsList;

    public EventsAdapter(Context mContext, List<Event> mEventsList) {
        this.mContext = mContext;
        this.mEventsList = mEventsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = mEventsList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return mEventsList.size();
    }

    // Clear all items from Recycler View
    public void clear() {
        mEventsList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items (events)
    public void addAll(List<Event> events) {
        mEventsList.addAll(events);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private TextView mDescriptionTextView;
        private ImageButton mShareImageButton;
        private ImageButton mLocationButton;
        private TextView mLocationTextView;
        private ImageButton mTimeImageButton;
        private TextView mTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.titleTextView);
            mDescriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            mShareImageButton = itemView.findViewById(R.id.shareImageButton);
            mLocationButton = itemView.findViewById(R.id.locationImageButton);
            mLocationTextView = itemView.findViewById(R.id.locationTextView);
            mTimeImageButton = itemView.findViewById(R.id.timeImageButton);
            mTimeTextView = itemView.findViewById(R.id.timeTextView);

        }

        // Method to help bind the data retrieved and stored in an Event object to the views in
        // each item in the Recycler View
        public void bind(Event event) {

            mTitleTextView.setText(event.getTitle());
            mDescriptionTextView.setText(event.getDescription());

            Calendar eventDate = Calendar.getInstance();
            eventDate.setTime(event.getTime());
            String strEventDate = eventDate.get(Calendar.MONTH) + "/" + eventDate.get(Calendar.DAY_OF_MONTH) + "/" + eventDate.get(Calendar.YEAR);
            mTimeTextView.setText(strEventDate);

        }
    }
}
