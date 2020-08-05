package com.example.studygroup.messaging;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.studygroup.messaging.MessageListFragment;

public class MessagesViewPagerAdapter extends FragmentStateAdapter {

    public static final int MESSAGES_ITEM_SIZE = 2;

    public MessagesViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MessageListFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return MESSAGES_ITEM_SIZE;
    }
}