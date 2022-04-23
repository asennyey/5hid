package com.asennyey.a5hid;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.objects.read.LeaderboardUser;
import com.asennyey.a5hid.placeholder.PlaceholderContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class leaderboard_list_fragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_LEADERBOARD = "leaderboard";
    // TODO: Customize parameters
    private List<LeaderboardUser> users = new ArrayList<>();
    private ApiController api = ApiController.getInstance(null);
    private MyItemRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public leaderboard_list_fragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static leaderboard_list_fragment newInstance(List<LeaderboardUser> users) {
        leaderboard_list_fragment fragment = new leaderboard_list_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api.getLeaderboard((page)->{
            adapter.notifyItemRangeRemoved(0, users.size());
            users.clear();
            users.addAll(page.result.records);
            adapter.notifyItemRangeInserted(0, page.result.records.size());
        }, (err)->{

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard_list_fragment_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MyItemRecyclerViewAdapter(users);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
}