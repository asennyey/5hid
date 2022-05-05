/**
 * @author Aramis Sennyey
 * This class is a list of all users that a person can add or remove as friends.
 */
package com.asennyey.a5hid;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.objects.read.User;
import com.asennyey.a5hid.placeholder.PlaceholderContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 */
public class UserFragment extends Fragment {
    private List<User> users = new ArrayList<>();
    private ApiController api = ApiController.getInstance(null);
    private UserRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    /**
     * Return the list of all elements generated from the cursor.
     * @return list of all contacts.
     */
    private Set<String> getEmails(){
        Set<String> emailList = new ArraySet<>();
        ContentResolver resolver = this.getActivity().getContentResolver();
        Cursor cursor = resolver.query( ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            Cursor emailCur = resolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[]{id}, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA));
                emailList.add(email); // Here you will get list of email.
            }
            emailCur.close();
        } cursor.close();
        return emailList;
    }

    private void getData(){
        api.getFriendableUsers((page)->{
            adapter.emails = getEmails();
            adapter.notifyItemRangeRemoved(0, users.size());
            System.out.println(page.result.records);
            users.clear();
            users.addAll(page.result.records);
            adapter.notifyItemRangeInserted(0, page.result.records.size());
        }, (err)->{

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new UserRecyclerViewAdapter(users);
            adapter.setOnClickListener((item)->{
                getData();
            });
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
}