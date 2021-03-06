package com.example.studygroup.messaging;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.studygroup.MainActivity;
import com.example.studygroup.R;
import com.example.studygroup.models.Message;
import com.example.studygroup.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends Fragment {

    public static final String TAG = ConversationFragment.class.getSimpleName();

    private ImageView mToolbarImageView;
    private TextView mToolbarTextView;
    private ImageButton mSendMessageImageButton;
    private EditText mComposeMessageEditText;
    private RecyclerView mConversationMessagesRecyclerView;

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private List<Message> mMessageList;
    private MessageAdapter mMessagesAdapter;

    public ConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar_messages);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MessagesFragment();
                ((MainActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.frameLayoutContainer, fragment)
                        .commit();
            }
        });

        ConversationFragment fragment = (ConversationFragment) getFragmentManager().findFragmentByTag(ConversationFragment.class.getSimpleName());
        fragment.getView().setFocusableInTouchMode(true);
        fragment.getView().requestFocus();
        fragment.getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_BACK) {
                    Fragment fragment = new MessagesFragment();
                    ((MainActivity) getContext()).getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.frameLayoutContainer, fragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });

        mToolbarImageView = view.findViewById(R.id.toolbarProfilePictureImageView);
        mToolbarTextView = view.findViewById(R.id.toolbarUserNameTextView);
        mSendMessageImageButton = view.findViewById(R.id.messageSendButton);
        mComposeMessageEditText = view.findViewById(R.id.composeMessageEditText);
        mConversationMessagesRecyclerView = view.findViewById(R.id.conversationMessagesRecyclerView);

        Bundle data = getArguments();
        String userId = data.getString("userId");

        mSendMessageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mComposeMessageEditText.getText().toString();
                if(!message.isEmpty()) {
                    sendMessage(mFirebaseUser.getUid(), userId, message);
                    mComposeMessageEditText.setText("");
                }
            }
        });

        mConversationMessagesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        mConversationMessagesRecyclerView.setLayoutManager(linearLayoutManager);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        authenticateFirebase(userId);
    }

    private void authenticateFirebase(String userId) {
        if(mFirebaseUser != null) {
            loadUserInfo(userId);
        } else {
            ParseUser currentUser = ParseUser.getCurrentUser();
            String email = currentUser.getEmail();
            String password = currentUser.getObjectId();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Successful login to Firebase");
                        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        loadUserInfo(userId);
                    }
                }
            });
        }

    }

    private void loadUserInfo(String userId) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mToolbarTextView.setText(user.getUsername());
                if(user.getImageUrl().equals("default")) {
                    mToolbarImageView.setVisibility(View.INVISIBLE);
                } else {
                    Glide.with(getContext())
                            .load(user.getImageUrl())
                            .circleCrop()
                            .into(mToolbarImageView);
                }

                readMessages(mFirebaseUser.getUid(), userId, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error with database value event listener ", error.toException());
            }
        });

    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessages(String myId, String userId, String imageUrl) {
        mMessageList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessageList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);

                    if((message.getReceiver().equals(myId) && message.getSender().equals(userId)) ||
                            message.getReceiver().equals(userId) && message.getSender().equals(myId)) {
                        mMessageList.add(message);
                    }
                }

                mMessagesAdapter = new MessageAdapter(getContext(), mMessageList, imageUrl);
                mConversationMessagesRecyclerView.setAdapter(mMessagesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error reading messages from Firebase: ", error.toException());
            }
        });
    }
}