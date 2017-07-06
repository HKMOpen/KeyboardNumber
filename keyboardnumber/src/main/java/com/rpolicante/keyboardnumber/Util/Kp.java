package com.rpolicante.keyboardnumber.Util;

import android.os.Bundle;

import com.rpolicante.keyboardnumber.KeyboardNumberPicker;
import com.rpolicante.keyboardnumber.KeypadPickerNF;

/**
 * Created by hesk on 5/7/2017.
 */

public class Kp {
    public static final int SIMPLE_PAD = 1101;
    public static final int CASH_PAD = 1105;

    public static final String ARG_TAG = Kp.class.getPackage() + ".ARG_TAG";
    public static final String ARG_CURRENCY = Kp.class.getPackage() + ".ARG_CURRENCY";
    public static final String ARG_EXPECT = Kp.class.getPackage() + ".ARG_EXPECT";
    public static final String ARG_SAVE_LABEL = Kp.class.getPackage() + ".ARG_SAVE_LABEL";
    public static final String ARG_VALUE = Kp.class.getPackage() + ".ARG_VALUE";
    public static final String ARG_RETAIN = Kp.class.getPackage() + ".ARG_RETAIN";

    public static class Builder {

        private int tag, collection_expectation;
        private String over_main_button_label, currency;
        private boolean retainInstance = true;

        public Builder(int tag) {
            this.tag = tag;
        }

        public Builder overrideMainButton(String label) {
            over_main_button_label = label;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setValueToBeCollected(int number) {
            collection_expectation = number;
            return this;
        }

        public Builder doNotRetainInstance() {
            retainInstance = false;
            return this;
        }

        public Bundle toBundle() {
            Bundle args = new Bundle();
            args.putInt(ARG_TAG, tag);
            if (over_main_button_label != null) {
                args.putString(ARG_SAVE_LABEL, over_main_button_label);
            }
            if (currency != null) {
                args.putString(ARG_CURRENCY, currency);
            }
            if (collection_expectation > 0) {
                args.putInt(ARG_EXPECT, collection_expectation);
            }
            args.putBoolean(ARG_RETAIN, retainInstance);
            return args;
        }

        public KeypadPickerNF createNf() {
            return KeypadPickerNF.newInstance(toBundle());
        }

        public KeyboardNumberPicker create() {
            return KeyboardNumberPicker.newInstance(toBundle());
        }
    }
}
