package com.rpolicante.keyboardnumber.Util;

import com.rpolicante.keyboardnumber.KeyboardNumberPicker;
import com.rpolicante.keyboardnumber.KeypadPickerNF;

/**
 * Created by hesk on 5/7/2017.
 */

public interface NfPickerHandler {
    void onConfirmAction(KeypadPickerNF picker, String value);

    void onCancelAction(KeypadPickerNF picker);
}
