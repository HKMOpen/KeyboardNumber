package com.rpolicante.keyboardnumber;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;

/**
 * Created by Cooper Card on 09/03/2017.
 */

public class KeyboardNumberPicker extends DialogFragment {
    public static final int SIMPLE_PAD = 1101;
    public static final int CASH_PAD = 1105;
    private static final String ARG_TAG = KeyboardNumberPicker.class.getPackage() + ".ARG_TAG";
    private static final String ARG_CURRENCY = KeyboardNumberPicker.class.getPackage() + ".ARG_CURRENCY";
    private static final String ARG_EXPECT = KeyboardNumberPicker.class.getPackage() + ".ARG_EXPECT";
    private static final String ARG_SAVE_LABEL = KeyboardNumberPicker.class.getPackage() + ".ARG_SAVE_LABEL";
    private static final String ARG_VALUE = KeyboardNumberPicker.class.getPackage() + ".ARG_VALUE";

    private TextView display, display_expect;
    private View keyboardNumberView;
    private ImageView backspace;
    private AlertDialog keyboardDialog;
    private String strValue = "", save_label = "", label_currency = "";

    private int theme = R.style.KeyboardNumberTheme;
    private int tag, expectation, difference;

    private static KeyboardNumberPicker newInstance(Bundle dc) {
        KeyboardNumberPicker fragment = new KeyboardNumberPicker();
        fragment.setArguments(dc);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        setRetainInstance(true);
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            loadArguments(savedInstanceState);
        } else if (null != getArguments()) {
            loadArguments(getArguments());
        }

        setStyle(STYLE_NO_TITLE, theme);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        createDialog();
        setup(savedInstanceState);
        return keyboardDialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_TAG, tag);
        outState.putString(ARG_VALUE, strValue);
        outState.putInt(ARG_EXPECT, expectation);
    }

    private void loadArguments(Bundle bundle) {
        if (bundle.containsKey(ARG_TAG)) {
            tag = bundle.getInt(ARG_TAG);
        }
        if (bundle.containsKey(ARG_SAVE_LABEL)) {
            save_label = bundle.getString(ARG_SAVE_LABEL);
        }
        if (bundle.containsKey(ARG_EXPECT)) {
            expectation = bundle.getInt(ARG_EXPECT);
        }
        if (bundle.containsKey(ARG_CURRENCY)) {
            label_currency = bundle.getString(ARG_CURRENCY);
        }

    }

    private int getLayout() {
        if (tag == CASH_PAD) {
            return R.layout.rpolicante_dialog_picker_double;
        } else if (tag == SIMPLE_PAD) {
            return R.layout.rpolicante_dialog_picker_single;
        } else {
            return R.layout.rpolicante_dialog_picker_single;
        }
    }

    private void createDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        keyboardNumberView = inflater.inflate(getLayout(), null);
        keyboardDialog = new AlertDialog.Builder(getContext(), theme)
                .setView(keyboardNumberView)
                .setPositiveButton(!save_label.isEmpty() ? save_label : getString(R.string.knp_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        KeyboardNumberPickerHandler handler = getImplementsHandlerListener();
                        if (handler != null) {
                            handler.onConfirmAction(KeyboardNumberPicker.this, strValue);
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.knp_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        KeyboardNumberPickerHandler handler = getImplementsHandlerListener();
                        if (handler != null) {
                            handler.onCancelAction(KeyboardNumberPicker.this);
                        }
                        dismiss();
                    }
                })
                .create();
    }

    private String currency_format(int sample) {
        return currency_format(String.valueOf(sample));
    }

    private String currency_format(String sample) {
        NumberFormat format = NumberFormat.getInstance();
        StringBuilder spm = new StringBuilder();
        double amount = Double.parseDouble(sample);
        Currency c = Currency.getInstance(label_currency.toUpperCase());
        format.setCurrency(c);
        spm.append(label_currency.toUpperCase());
        String output = format.format(amount);
        spm.append(" ");
        spm.append(output);
        return spm.toString();
    }

    private void setup(Bundle args) {
        @SuppressLint("Recycle")
        TypedArray attributes = getContext().obtainStyledAttributes(theme, R.styleable.KeyboardNumberPicker);

        int knpDisplayTextColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpDisplayTextColor, ContextCompat.getColor(getContext(), android.R.color.secondary_text_light));
        int knpDisplayBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpDisplayBackgroundColor, ContextCompat.getColor(getContext(), android.R.color.transparent));

        int knpsDisplayTextColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpSubDisplayTextColor, ContextCompat.getColor(getContext(), android.R.color.secondary_text_light));
        int knpsDisplayBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpSubDisplayBackgroundColor, ContextCompat.getColor(getContext(), android.R.color.transparent));

        if (tag == CASH_PAD) {
            display_expect = (TextView) keyboardNumberView.findViewById(R.id.collected_cash);
            display_expect.setBackgroundColor(knpsDisplayBackgroundColor);
            display_expect.setTextColor(knpsDisplayTextColor);
            display_expect.setText(currency_format(expectation));
        }

        display = (TextView) keyboardNumberView.findViewById(R.id.rpolicante_dialog_picker_display);
        display.setBackgroundColor(knpDisplayBackgroundColor);
        display.setTextColor(knpDisplayTextColor);

        if (args != null && args.containsKey(ARG_VALUE)) {
            strValue = args.getString(ARG_VALUE, "");
        }
        display.setText(strValue);

        int knpBackspaceTintColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpBackspaceTintColor, ContextCompat.getColor(getContext(), android.R.color.secondary_text_light));
        int knpBackspaceBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpBackspaceBackgroundColor, ContextCompat.getColor(getContext(), android.R.color.transparent));
        backspace = (ImageView) keyboardNumberView.findViewById(R.id.rpolicante_dialog_picker_backspace);
        backspace.setColorFilter(knpBackspaceTintColor, PorterDuff.Mode.SRC_IN);
        backspace.getRootView().setBackgroundColor(knpBackspaceBackgroundColor);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateText("");
            }
        });
        backspace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                updateText(null);
                return true;
            }
        });

        int knpBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpBackgroundColor, ContextCompat.getColor(getContext(), android.R.color.background_light));
        keyboardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(knpBackgroundColor));

        View.OnClickListener keyListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView) {
                    TextView key = (TextView) v;
                    String value = key.getText().toString();
                    updateText(value);
                }
            }
        };

        int knpKeysTextColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpKeysTextColor, ContextCompat.getColor(getContext(), android.R.color.secondary_text_light));
        int knpKeysBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpKeysBackgroundColor, ContextCompat.getColor(getContext(), android.R.color.transparent));

        GridLayout grid = (GridLayout) keyboardNumberView.findViewById(R.id.rpolicante_dialog_picker_grid);
        int childCount = grid.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (grid.getChildAt(i) instanceof TextView) {
                TextView key = (TextView) grid.getChildAt(i);
                key.setTextColor(knpKeysTextColor);
                key.setBackgroundColor(knpKeysBackgroundColor);
                key.setOnClickListener(keyListener);
            }
        }

        attributes.recycle();

    }

    @Override
    public void onStart() {
        super.onStart();
        onNumberChanged();
    }

    private void updateText(String value) {
        try {
            if (tag == CASH_PAD && value.equalsIgnoreCase(".")) return;
            String oldValue = strValue;
            if (value != null) {
                if (value.isEmpty()) {
                    if (strValue.length() > 0) {
                        strValue = strValue.substring(0, strValue.length() - 1);
                    }
                } else {
                    strValue += value;
                }
            } else {
                strValue = "";
            }

            if (tag == CASH_PAD) {
                if (strValue.isEmpty()) {
                    difference = expectation;
                    display_expect.setText(currency_format(expectation));
                } else {
                    difference = expectation - Integer.parseInt(strValue);
                    display_expect.setText(currency_format(difference));
                }

            } else {
                KeyboardNumberKeyListener keyListener = getImplementsKeyListener();
                if (keyListener != null) {
                    keyListener.beforeKeyPressed(this, oldValue, value);
                    keyListener.onTextChanged(this, oldValue, strValue, value);
                }

                KeyboardNumberFormatter formatter = getImplementsFormatterListener();
                if (formatter != null) {
                    strValue = formatter.formatNumber(this, strValue);
                }

                if (keyListener != null) {
                    keyListener.afterKeyPressed(this, strValue);
                }
            }
            display.setText(strValue);
            onNumberChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onNumberChanged() {
        if (0 == display.getText().length()) {
            keyboardDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        } else {
            keyboardDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
        }
    }

    public void clearKeyboard() {
        updateText(null);
    }

    private KeyboardNumberFormatter getImplementsFormatterListener() {
        final Activity activity = getActivity();
        final Fragment fragment = getParentFragment();
        if (activity instanceof KeyboardNumberFormatter) {
            return (KeyboardNumberFormatter) activity;
        } else if (fragment instanceof KeyboardNumberFormatter) {
            return (KeyboardNumberFormatter) fragment;
        } else {
            return null;
        }
    }

    private KeyboardNumberPickerHandler getImplementsHandlerListener() {
        final Activity activity = getActivity();
        final Fragment fragment = getParentFragment();
        if (activity instanceof KeyboardNumberPickerHandler) {
            return (KeyboardNumberPickerHandler) activity;
        } else if (fragment instanceof KeyboardNumberPickerHandler) {
            return (KeyboardNumberPickerHandler) fragment;
        } else {
            return null;
        }
    }

    private KeyboardNumberKeyListener getImplementsKeyListener() {
        final Activity activity = getActivity();
        final Fragment fragment = getParentFragment();
        if (activity instanceof KeyboardNumberKeyListener) {
            return (KeyboardNumberKeyListener) activity;
        } else if (fragment instanceof KeyboardNumberKeyListener) {
            return (KeyboardNumberKeyListener) fragment;
        } else {
            return null;
        }
    }


    public static class Builder {

        private int tag, collection_expectation;
        private String over_main_button_label, currency;

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

        public KeyboardNumberPicker create() {

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

            return newInstance(args);
        }
    }

}
