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
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_login, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle(R.string.login_title)
                // Add action buttons
                .setPositiveButton(R.string.login, (dialog, id) -> {

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> getDialog().cancel());
        return builder.create();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                positiveButton.setEnabled(false);
                com.asennyey.a5hid.api.objects.write.User user = new com.asennyey.a5hid.api.objects.write.User();
                TextInputLayout usernameLayout = d.findViewById(R.id.user_username_wrapper);
                user.username = usernameLayout.getEditText().getText().toString();
                TextInputLayout passwordLayout = d.findViewById(R.id.user_password_wrapper);
                user.password = passwordLayout.getEditText().getText().toString();
                ApiController.getInstance().login(
                        user,
                        (res)->{
                            getActivity().invalidateOptionsMenu();
                            d.dismiss();
                        },
                        (err)->{
                            positiveButton.setEnabled(true);
                        }
                );

                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            });
        }
    }
}