package com.asennyey.a5hid;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.AuthenticationController;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEventFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateEventFragment newInstance(String param1, String param2) {
        CreateEventFragment fragment = new CreateEventFragment();
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

        View view = inflater.inflate(R.layout.fragment_create_event, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle(R.string.create_event_title)
                // Add action buttons
                .setPositiveButton(R.string.create_event, (dialog, id) -> {
                    com.asennyey.a5hid.api.objects.write.Event event = new com.asennyey.a5hid.api.objects.write.Event();
                    TextInputLayout layout = view.findViewById(R.id.event_description_wrapper);
                    event.description = layout.getEditText().getText().toString();
                    LocationController.getInstance().getPreciseLocation(cancellationTokenSource, (location)->{
                        event.location = location.result;
                        ApiController.getInstance().createEvent(
                                event,
                                (res)->{
                                    System.out.println(res);
                                },
                                (err)->{
                                    System.out.println(err);
                                }
                        );
                    }, (err)->{
                        System.out.println(err.result);
                    });
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> getDialog().cancel());
        return builder.create();
    }
}