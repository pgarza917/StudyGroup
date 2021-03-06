package com.example.studygroup.eventFeed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.studygroup.R;
import com.example.studygroup.eventCreation.files.FileViewAdapter;
import com.example.studygroup.models.Event;
import com.example.studygroup.models.FileExtended;
import com.example.studygroup.models.Group;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilesFragment extends Fragment {

    public static final String TAG = FilesFragment.class.getSimpleName();

    private RecyclerView mFilesRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mNoFilesTextView;

    private List<FileExtended> mFilesList;
    private FileViewAdapter mFilesAdapter;
    private Event mEvent;
    private Group mGroup;

    public FilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments().containsKey(Event.class.getSimpleName())) {
            mEvent = Parcels.unwrap(getArguments().getParcelable(Event.class.getSimpleName()));
        } else {
            mGroup = Parcels.unwrap(getArguments().getParcelable(Group.class.getSimpleName()));
        }

        mFilesList = new ArrayList<>();
        mFilesAdapter = new FileViewAdapter(getContext(), mFilesList);

        mFilesRecyclerView = view.findViewById(R.id.eventFilesRecyclerView);
        mProgressBar = view.findViewById(R.id.eventFilesProgressBar);
        mNoFilesTextView = view.findViewById(R.id.noFilesTextView);

        mFilesRecyclerView.setAdapter(mFilesAdapter);
        mFilesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<FileExtended> files;
        if(mEvent != null) {
            files = mEvent.getFiles();
        } else {
            files = mGroup.getGroupFiles();
        }

        if(files != null) {
            mFilesList.clear();
            mFilesList.addAll(files);
            mFilesAdapter.notifyDataSetChanged();
        } else {
            mNoFilesTextView.setVisibility(View.VISIBLE);
        }
    }
}