package com.rpolicante.keyboardnumbersample;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.rpolicante.keyboardnumber.KeyboardNumberPicker;
import com.rpolicante.keyboardnumber.KeypadPickerNF;
import com.rpolicante.keyboardnumber.Util.Kp;
import com.rpolicante.keyboardnumber.Util.NfPickerHandler;

import static com.rpolicante.keyboardnumber.Util.Kp.CASH_PAD;
import static com.rpolicante.keyboardnumber.Util.Kp.SIMPLE_PAD;

/**
 * Created by hesk on 5/7/2017.
 */

public class DialogFra extends DialogFragment implements NfPickerHandler {
    private KeypadPickerNF picker;

    @SuppressLint({"InflateParams", "HandlerLeak"})
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle bundle;
        if (savedInstanceState == null) {
            bundle = getArguments();
        } else {
            bundle = savedInstanceState;
        }

        Kp.Builder pickerbuil = new Kp
                .Builder(SIMPLE_PAD)
                .setCurrency("HKD")
                .doNotRetainInstance()
                .setValueToBeCollected(500)
                .overrideMainButton("WAH˚");
        picker = pickerbuil.createNf();
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_f, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Button b = (Button) dialogView.findViewById(R.id.button_panel);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getChildFragmentManager(), "KNum");
            }
        });
        builder.setTitle("DEMO")
                .setView(dialogView)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }


    @Override
    public void onConfirmAction(KeypadPickerNF picker, String value) {
        //   if (container_bill == null || mbill == null || entry_index == -1) return;
        float get_float = Float.parseFloat(value);
    }

    @Override
    public void onCancelAction(KeypadPickerNF picker) {

    }
}
