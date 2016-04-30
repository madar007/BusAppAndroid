package com.example.mashfique.mapdemo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FocusFabDialog extends DialogFragment {

    private OnSelectedFocusListener mListener;
    public FocusFabDialog() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] fabOptions = {"Current Location", "East Bank", "West Bank", "St. Paul"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Go to...")
                .setItems(fabOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setMapFocus(fabOptions[which]);
                    }
                });
        return builder.create();
    }

    public interface OnSelectedFocusListener {
        void onSelectedFocus(String location);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectedFocusListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    private void setMapFocus(String location) {
        mListener.onSelectedFocus(location);
    }
}
