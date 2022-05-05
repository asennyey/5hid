/**
 * @author Aramis Sennyey
 * This class handles a dialog fragment that would make an API request and uses callbacks to achieve
 *  such behavior.
 */

package com.asennyey.a5hid;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.Callback;
import com.asennyey.a5hid.api.Result;
import com.asennyey.a5hid.viewmodels.EventViewModel;
import com.google.android.gms.common.api.Api;
import com.google.android.material.textfield.TextInputLayout;

public abstract class ApiDialogFragment extends DialogFragment {
    int positiveButtonText;
    int negativeButtonText;
    int dialogLayout;
    int dialogTitle;
    public ApiDialogFragment(int positiveButtonText, int dialogLayout, int dialogTitle){
        this(positiveButtonText, R.string.cancel, dialogLayout, dialogTitle);
    }

    public ApiDialogFragment(int positiveButtonText, int negativeButtonText, int dialogLayout, int dialogTitle){
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.dialogLayout = dialogLayout;
        this.dialogTitle = dialogTitle;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ApiDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ApiDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ApiDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                positiveButton.setEnabled(false);
                this.onPositiveButtonClick(getDialog(), (success)->{
                    positiveButton.setEnabled(true);
                    if(success.result){
                        d.dismiss();
                        listener.onDialogPositiveClick(this);
                    }
                });
            });
            Button negativeButton = (Button) d.getButton(Dialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(v -> {
                negativeButton.setEnabled(false);
                this.onNegativeButtonClick(getDialog(), (success)->{
                    negativeButton.setEnabled(true);
                    if(success.result){
                        d.dismiss();
                        listener.onDialogNegativeClick(this);
                    }
                });
            });
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(this.dialogLayout, null);
        builder.setView(view)
                .setMessage(this.dialogTitle)
                .setPositiveButton(this.positiveButtonText, (dialog, id) -> {
                    // empty because some APi versions ignore if not implemented
                })
                .setNegativeButton(this.negativeButtonText, (dialog, id) -> {
                    // empty because some APi versions ignore if not implemented
                });
        return builder.create();
    }

    public abstract void onPositiveButtonClick(Dialog root, Callback<Boolean> onAfter);

    public void onNegativeButtonClick(Dialog root, Callback<Boolean> onAfter){
        onAfter.onResult(new Result<>(true));
    }
}
