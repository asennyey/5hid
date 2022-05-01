package com.asennyey.a5hid;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.objects.read.User;
import com.asennyey.a5hid.placeholder.PlaceholderContent.PlaceholderItem;
import com.asennyey.a5hid.databinding.FragmentUserBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private ApiController api = ApiController.getInstance();

    public UserRecyclerViewAdapter(List<User> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User item = mValues.get(position);
        holder.mItem = item;
        holder.mIdView.setText(item.firstName + " "+ item.lastName);
        holder.mContentView.setText(item.email);
        holder.addFriend.setOnClickListener((v)->{
            api.addFriend(item.id, (res)->{
                
            }, (err)->{

            });
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public final Button addFriend;
        public User mItem;

        public ViewHolder(FragmentUserBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            addFriend = binding.friendAdd;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}