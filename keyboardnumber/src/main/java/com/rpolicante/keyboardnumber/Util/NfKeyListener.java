package com.rpolicante.keyboardnumber.Util;

import com.rpolicante.keyboardnumber.KeyboardNumberPicker;
import com.rpolicante.keyboardnumber.KeypadPickerNF;

/**
 * Created by hesk on 5/7/2017.
 */

public interface NfKeyListener {

    void beforeKeyPressed(KeypadPickerNF picker, String oldString, String newDigit);
    void onTextChanged(KeypadPickerNF picker, String oldString, String newString, String digit);
    void afterKeyPressed(KeypadPickerNF picker, String newString);
}
