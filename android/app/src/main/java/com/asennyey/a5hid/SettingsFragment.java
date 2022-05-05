/**
 * @author Aramis Sennyey
 * This class handles creation of settings.
 */

package com.asennyey.a5hid;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asennyey.a5hid.api.AuthenticationController;

public class SettingsFragment extends Fragment {
    public ViewGroup cont;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        this.cont = container;
        AuthenticationController auth = AuthenticationController.getInstance();
        Button logout = view.findViewById(R.id.logout);
        logout.setVisibility(auth.isLoggedIn() ? View.VISIBLE : View.INVISIBLE);
        logout.setOnClickListener((v)->{
            auth.logout();
            getActivity().invalidateOptionsMenu();
            logout.setVisibility(View.INVISIBLE);
        });
        return view;
    }

    public void log_in_out(View v) {

    }
}