package com.example.studygroup.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.studygroup.MainActivity;
import com.example.studygroup.R;
import com.example.studygroup.eventFeed.EventDetailsRootFragment;
import com.example.studygroup.eventFeed.EventsAdapter;
import com.example.studygroup.eventFeed.FeedFragment;
import com.example.studygroup.groups.GroupDetailsRootFragment;
import com.example.studygroup.groups.GroupsAdapter;
import com.example.studygroup.models.Event;
import com.example.studygroup.models.Group;
import com.example.studygroup.profile.ProfileDetailsFragment;
import com.example.studygroup.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    public static final String TAG = SearchFragment.class.getSimpleName();

    private RecyclerView mEventsResultsRecyclerView;
    private RecyclerView mUsersResultsRecyclerView;
    private RecyclerView mGroupsResultsRecyclerView;
    private ProgressBar mProgressBar;

    private EventsAdapter mEventsAdapter;
    private List<Event> mEventsResultList;
    private UserSearchResultAdapter mUsersAdapter;
    private List<ParseUser> mUsersResultsList;
    private GroupsAdapter mGroupsAdapter;
    private List<Group> mGroupsResultList;
    private String mSearchCategory;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Search");
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FeedFragment();
                ((MainActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayoutContainer, fragment)
                        .commit();
            }
        });

        mSearchCategory = "users";

        mEventsResultList = new ArrayList<>();
        mUsersResultsList = new ArrayList<>();
        mGroupsResultList = new ArrayList<>();
        mEventsAdapter = new EventsAdapter(getContext(), mEventsResultList, new EventsAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Event event = mEventsResultList.get(position);

                if(position != RecyclerView.NO_POSITION) {

                    Fragment fragment = new EventDetailsRootFragment();

                    Bundle data = new Bundle();
                    data.putParcelable(Event.class.getSimpleName(), Parcels.wrap(event));
                    data.putInt("position", position);
                    fragment.setArguments(data);

                    Fragment currentFragment = ((MainActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.frameLayoutContainer);
                    if(currentFragment.getClass().getSimpleName().equals(ProfileFragment.TAG)) {
                        fragment.setTargetFragment(currentFragment, ProfileFragment.RC_DETAILS);
                    }

                    ((MainActivity) getContext()).getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.frameLayoutContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        mUsersAdapter = new UserSearchResultAdapter(getContext(), mUsersResultsList, new UserSearchResultAdapter.ResultClickListener() {
            @Override
            public void onResultClicked(int position) {
                // Launch profile details
                ParseUser selectedUser = mUsersResultsList.get(position);

                Fragment fragment = new ProfileDetailsFragment();
                Bundle data = new Bundle();
                data.putParcelable(ParseUser.class.getSimpleName(), Parcels.wrap(selectedUser));
                fragment.setArguments(data);

                ((MainActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.frameLayoutContainer, fragment)
                        .commit();
            }
        });
        mGroupsAdapter = new GroupsAdapter(getContext(), mGroupsResultList, new GroupsAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Group group = mGroupsResultList.get(position);
                Fragment fragment = new GroupDetailsRootFragment();
                Bundle data = new Bundle();
                data.putParcelable(Group.class.getSimpleName(), Parcels.wrap(group));
                fragment.setArguments(data);

                ((MainActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.frameLayoutContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        mProgressBar = view.findViewById(R.id.searchFragmentProgressBar);
        mEventsResultsRecyclerView = view.findViewById(R.id.eventsResultsRecyclerView);
        mUsersResultsRecyclerView = view.findViewById(R.id.usersResultsRecyclerView);
        mGroupsResultsRecyclerView = view.findViewById(R.id.groupsResultsRecyclerView);
        mEventsResultsRecyclerView.setVisibility(View.GONE);
        mUsersResultsRecyclerView.setVisibility(View.VISIBLE);
        mGroupsResultsRecyclerView.setVisibility(View.GONE);

        mEventsResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mEventsResultsRecyclerView.setAdapter(mEventsAdapter);

        mUsersResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsersResultsRecyclerView.setAdapter(mUsersAdapter);

        mGroupsResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mGroupsResultsRecyclerView.setAdapter(mGroupsAdapter);

        DividerItemDecoration eventsDivider = new DividerItemDecoration(mEventsResultsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mEventsResultsRecyclerView.addItemDecoration(eventsDivider);

        DividerItemDecoration usersDivider = new DividerItemDecoration(mUsersResultsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mUsersResultsRecyclerView.addItemDecoration(usersDivider);

        DividerItemDecoration groupsDivider = new DividerItemDecoration(mGroupsResultsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mGroupsResultsRecyclerView.addItemDecoration(groupsDivider);


        final Spinner spinner = view.findViewById(R.id.selectionSpinner);

        List<String> options = new ArrayList<String>();
        options.add("Users");
        options.add("Events");
        options.add("Groups");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position == 0) {
                    mSearchCategory = "users";
                    mEventsResultsRecyclerView.setVisibility(View.GONE);
                    mGroupsResultsRecyclerView.setVisibility(View.GONE);
                    mUsersResultsRecyclerView.setVisibility(View.VISIBLE);
                } else if(position == 1) {
                    mSearchCategory = "events";
                    mUsersResultsRecyclerView.setVisibility(View.GONE);
                    mGroupsResultsRecyclerView.setVisibility(View.GONE);
                    mEventsResultsRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mSearchCategory = "groups";
                    mUsersResultsRecyclerView.setVisibility(View.GONE);
                    mEventsResultsRecyclerView.setVisibility(View.GONE);
                    mGroupsResultsRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                mProgressBar.setVisibility(View.VISIBLE);
                if(mSearchCategory.equals("events")) {
                    searchEvents(query);
                } else if(mSearchCategory.equals("users")) {
                    searchUsers(query);
                } else {
                    searchGroups(query);
                }
                // workaround to avoid issues with some emulators and keyboard devices
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.isEmpty()){
                    if (mSearchCategory.equals("events")) {
                        searchEvents(query);
                    } else if(mSearchCategory.equals("users")) {
                        searchUsers(query);
                    } else {
                        searchGroups(query);
                    }
                 }
                return false;
            }
        });

        searchItem.expandActionView();
        searchView.requestFocus();

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchUsers(String query) {
        ParseQuery<ParseUser> nameQuery = ParseUser.getQuery();
        nameQuery.whereContains("lowerDisplayName", query.toLowerCase());

        ParseQuery<ParseUser> emailQuery = ParseUser.getQuery();
        emailQuery.whereContains("email", query.toLowerCase());

        List<ParseQuery<ParseUser>> userQueries = new ArrayList<ParseQuery<ParseUser>>();
        userQueries.add(nameQuery);
        userQueries.add(emailQuery);

        ParseQuery<ParseUser> usersFullQuery = ParseQuery.or(userQueries);
        usersFullQuery.orderByDescending(Event.KEY_CREATED_AT);
        usersFullQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        usersFullQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error executing user search query: ", e);
                    return;
                }
                Log.i(TAG, "Success searching for users");
                mUsersAdapter.clear();
                mUsersAdapter.addAll(users);
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void searchEvents(String query) {
        ParseQuery<Event> eventsTitleQuery = ParseQuery.getQuery(Event.class);
        eventsTitleQuery.whereContains("title", query);

        ParseQuery<Event> eventsDescriptionQuery = ParseQuery.getQuery(Event.class);
        eventsDescriptionQuery.whereContains("description", query);

        ParseQuery<Event> eventsLocationQuery = ParseQuery.getQuery(Event.class);
        eventsLocationQuery.whereContains("locationName", query);

        List<ParseQuery<Event>> eventsQueries = new ArrayList<ParseQuery<Event>>();
        eventsQueries.add(eventsTitleQuery);
        eventsQueries.add(eventsDescriptionQuery);
        eventsQueries.add(eventsLocationQuery);

        ParseQuery<Event> eventsFullQuery = ParseQuery.or(eventsQueries);
        eventsFullQuery.orderByDescending(Event.KEY_CREATED_AT);
        eventsFullQuery.include(Event.KEY_FILES);
        eventsFullQuery.include(Event.KEY_USERS);

        eventsFullQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error executing event search query: ", e);
                    return;
                }
                Log.i(TAG, "Success searching for events");
                mEventsAdapter.clear();
                mEventsAdapter.addAll(events);
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void searchGroups(String query) {
        ParseQuery<Group> nameQuery = ParseQuery.getQuery(Group.class);
        nameQuery.whereContains("groupName", query);

        ParseQuery<Group> descriptionQuery = ParseQuery.getQuery(Group.class);
        descriptionQuery.whereContains("description", query);

        List<ParseQuery<Group>> queries = new ArrayList<>();
        queries.add(nameQuery);
        queries.add(descriptionQuery);

        ParseQuery<Group> fullQuery = ParseQuery.or(queries);
        fullQuery.include(Event.KEY_FILES);
        fullQuery.include("users");
        fullQuery.include("events");

        fullQuery.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                mGroupsResultList.clear();
                mGroupsResultList.addAll(objects);
                mGroupsAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}