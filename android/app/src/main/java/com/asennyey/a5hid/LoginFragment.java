package com.asennyey.a5hid;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.Callback;
import com.asennyey.a5hid.api.Result;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends ApiDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
        super(R.string.login, R.layout.fragment_login, R.string.login_title);
    }

    @Override
    public void onPositiveButtonClick(Dialog root, Callback<Boolean> onAfter) {
        com.asennyey.a5hid.api.objects.write.User user = new com.asennyey.a5hid.api.objects.write.User();
        TextInputLayout usernameLayout = root.findViewById(R.id.user_username_wrapper);
        user.username = usernameLayout.getEditText().getText().toString();
        TextInputLayout passwordLayout = root.findViewById(R.id.user_password_wrapper);
        user.password = passwordLayout.getEditText().getText().toString();
        System.out.println(user);
        ApiController.getInstance().login(
                user,
                (res)->{
                    onAfter.onResult(new Result<>(true));
                },
                (err)->{
                    onAfter.onResult(new Result<>(false));
                }
        );
        getActivity().invalidateOptionsMenu();
    }
}