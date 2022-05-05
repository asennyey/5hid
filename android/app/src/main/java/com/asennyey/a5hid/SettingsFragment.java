/**
 * @author Aramis Sennyey
 * This class handles creation of settings.
 */

package com.asennyey.a5hid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.asennyey.a5hid.api.AuthenticationController;

public class SettingsFragment extends Fragment {
    public ViewGroup cont;
    Switch theme_switch;

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
        theme_switch= view.findViewById(R.id.theme_switch);
        Button logout = view.findViewById(R.id.logout);
        logout.setVisibility(auth.isLoggedIn() ? View.VISIBLE : View.INVISIBLE);
        logout.setOnClickListener((v)->{
            auth.logout();
            getActivity().invalidateOptionsMenu();
            logout.setVisibility(View.INVISIBLE);
        });
        theme_switch.setOnClickListener((v)->{
            boolean state_in_map_act = ((MapsActivity)getActivity()).get_toggle();
            if (state_in_map_act == true) {
                theme_switch.setChecked(false);
            } else {
                theme_switch.setChecked(true);
            }
            Boolean switchState = theme_switch.isChecked();
            if (switchState == true) {
                theme_switch.setChecked(true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                ((MapsActivity)getActivity()).set_toggle(true);

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                theme_switch.setChecked(false);
                ((MapsActivity)getActivity()).set_toggle(false);
            }
        });
        return view;
    }

    public void log_in_out(View v) {

    }
}