/**
 * @author Aramis Sennyey
 * This class handles leaderboard display, using the RecyclerViewAdapter to display a list of
 *  users in the leaderboard. Relies on API sorting.
 */
package com.asennyey.a5hid;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.objects.read.LeaderboardUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class LeaderboardFragment extends Fragment {
    private List<LeaderboardUser> users = new ArrayList<>();
    private ApiController api = ApiController.getInstance(null);
    private LeaderboardRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LeaderboardFragment() {
    }

    /**
     * Get the list of users on create.
     * @param savedInstanceState
     */
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

    /**
     * Set the list view up.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new LeaderboardRecyclerViewAdapter(users);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
}