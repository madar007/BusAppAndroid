package com.example.mashfique.mapdemo;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FabDialog extends DialogFragment {


    public FabDialog() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] fabOptions = {"East Bank", "West Bank", "St. Paul", "Fullscreen"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Map options")
                .setItems(fabOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = "You picked: ";
                        text = text.concat(fabOptions[which]);
                        Toast confirmSelect = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                        confirmSelect.show();
                    }
                });
        return builder.create();
    }
}
