/**
 * @author Aramis Sennyey
 * This class handles user creation of an event through a modal, currently input of a description
 *  with inferred location.
 */
package com.asennyey.a5hid;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.AuthenticationController;
import com.asennyey.a5hid.api.Callback;
import com.asennyey.a5hid.api.Result;
import com.asennyey.a5hid.viewmodels.EventViewModel;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEventFragment extends ApiDialogFragment {

    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    public CreateEventFragment() {
        // Required empty public constructor
        super(R.string.create_event, R.layout.fragment_create_event, R.string.create_event_title);
    }

    /**
     * On positive button click handle submitting to the API.
     * @param root
     * @param onAfter
     */
    @Override
    public void onPositiveButtonClick(Dialog root, Callback<Boolean> onAfter) {
        com.asennyey.a5hid.api.objects.write.Event event = new com.asennyey.a5hid.api.objects.write.Event();
        TextInputLayout layout = getDialog().findViewById(R.id.event_description_wrapper);
        event.description = layout.getEditText().getText().toString();
        LocationController.getInstance().getPreciseLocation(cancellationTokenSource, (location)->{
            event.location = location.result;
            ApiController.getInstance().createEvent(
                    event,
                    (res)->{
                        onAfter.onResult(new Result<>(true));
                    },
                    (err)->{
                        onAfter.onResult(new Result<>(false));
                    }
            );
        }, (err)->{
            System.out.println(err.result);
            onAfter.onResult(new Result<>(false));
        });
    }
}