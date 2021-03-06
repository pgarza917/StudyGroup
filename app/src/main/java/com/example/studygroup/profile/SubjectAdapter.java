package com.example.studygroup.profile;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studygroup.MainActivity;
import com.example.studygroup.R;
import com.example.studygroup.models.Subject;

import java.util.HashMap;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    public static final String TAG = SubjectAdapter.class.getSimpleName();

    private List<Subject> mSubjectsList;
    private Context mContext;
    private OnClickListener mClickListener;
    private HashMap<String, Integer> mColorMap;

    public SubjectAdapter(Context mContext, List<Subject> mSubjectsList, OnClickListener listener) {
        this.mSubjectsList = mSubjectsList;
        this.mContext = mContext;
        this.mClickListener = listener;
        mColorMap = MainActivity.createColorMap();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject subject = mSubjectsList.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return mSubjectsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mSubjectNameTextView;
        private String mSubjectName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mSubjectNameTextView = itemView.findViewById(R.id.subjectNameTextView);
            itemView.setOnClickListener(this);
        }

        public void bind(Subject subject) {
            mSubjectName = subject.getSubjectName();
            mSubjectNameTextView.setText(mSubjectName);

            Integer color = mColorMap.get(mSubjectName);
            Drawable background = ContextCompat.getDrawable(mContext, R.drawable.subject_tag);
            background.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, color), PorterDuff.Mode.SRC_IN));
            mSubjectNameTextView.setBackground(background);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onClick(getAdapterPosition());
        }
    }

    public interface OnClickListener {
        void onClick(int position);
    }
}
