package com.rpolicante.keyboardnumber;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rpolicante.keyboardnumber.Util.NfKeyListener;
import com.rpolicante.keyboardnumber.Util.NfPicker;
import com.rpolicante.keyboardnumber.Util.NfPickerHandler;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.Currency;

import static com.rpolicante.keyboardnumber.Util.Kp.ARG_CURRENCY;
import static com.rpolicante.keyboardnumber.Util.Kp.ARG_EXPECT;
import static com.rpolicante.keyboardnumber.Util.Kp.ARG_RETAIN;
import static com.rpolicante.keyboardnumber.Util.Kp.ARG_SAVE_LABEL;
import static com.rpolicante.keyboardnumber.Util.Kp.ARG_TAG;
import static com.rpolicante.keyboardnumber.Util.Kp.ARG_VALUE;
import static com.rpolicante.keyboardnumber.Util.Kp.CASH_PAD;
import static com.rpolicante.keyboardnumber.Util.Kp.SIMPLE_PAD;

/**
 * Created by hesk on 5/7/2017.
 */

public class KeypadPickerNF extends DialogFragment {

    private TextView display, display_expect;
    private View keyboardNumberView;
    private ImageView backspace;
    private AlertDialog keyboardDialog;
    private String strValue = "", save_label = "", label_currency = "";

    private int theme = R.style.KeyboardNumberTheme;
    private int tag, expectation, difference;

    public static KeypadPickerNF newInstance(Bundle dc) {
        KeypadPickerNF fragment = new KeypadPickerNF();
        fragment.setArguments(dc);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        setRetainInstance(getArguments().getBoolean(ARG_RETAIN));
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
        keyboardDialog = new AlertDialog.Builder(getActivity(), theme)
                .setView(keyboardNumberView)
                .setPositiveButton(!save_label.isEmpty() ? save_label : getString(R.string.knp_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NfPickerHandler handler = getImplementsHandlerListener();
                        if (handler != null) {
                            handler.onConfirmAction(KeypadPickerNF.this, strValue);
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.knp_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NfPickerHandler handler = getImplementsHandlerListener();
                        if (handler != null) {
                            handler.onCancelAction(KeypadPickerNF.this);
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
        TypedArray attributes = getActivity().obtainStyledAttributes(theme, R.styleable.KeyboardNumberPicker);

        int knpDisplayTextColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpDisplayTextColor, ContextCompat.getColor(getActivity(), android.R.color.secondary_text_light));
        int knpDisplayBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpDisplayBackgroundColor, ContextCompat.getColor(getActivity(), android.R.color.transparent));

        int knpsDisplayTextColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpSubDisplayTextColor, ContextCompat.getColor(getActivity(), android.R.color.secondary_text_light));
        int knpsDisplayBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpSubDisplayBackgroundColor, ContextCompat.getColor(getActivity(), android.R.color.transparent));

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

        int knpBackspaceTintColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpBackspaceTintColor, ContextCompat.getColor(getActivity(), android.R.color.secondary_text_light));
        int knpBackspaceBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpBackspaceBackgroundColor, ContextCompat.getColor(getActivity(), android.R.color.transparent));
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

        int knpBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpBackgroundColor, ContextCompat.getColor(getActivity(), android.R.color.background_light));
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

        int knpKeysTextColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpKeysTextColor, ContextCompat.getColor(getActivity(), android.R.color.secondary_text_light));
        int knpKeysBackgroundColor = attributes.getColor(R.styleable.KeyboardNumberPicker_knpKeysBackgroundColor, ContextCompat.getColor(getActivity(), android.R.color.transparent));

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
                NfKeyListener keyListener = getImplementsKeyListener();
                if (keyListener != null) {
                    keyListener.beforeKeyPressed(KeypadPickerNF.this, oldValue, value);
                    keyListener.onTextChanged(KeypadPickerNF.this, oldValue, strValue, value);
                }

                NfPicker formatter = getImplementsFormatterListener();
                if (formatter != null) {
                    strValue = formatter.formatNumber(this, strValue);
                }

                if (keyListener != null) {
                    keyListener.afterKeyPressed(KeypadPickerNF.this, strValue);
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

    private NfPicker getImplementsFormatterListener() {
        final Activity activity = getActivity();
        final Fragment fragment = getParentFragment();
        if (activity instanceof NfPicker) {
            return (NfPicker) activity;
        } else if (fragment instanceof NfPicker) {
            return (NfPicker) fragment;
        } else {
            return null;
        }
    }

    private NfPickerHandler getImplementsHandlerListener() {
        final Activity activity = getActivity();
        final Fragment fragment = getParentFragment();
        if (activity instanceof NfPickerHandler) {
            return (NfPickerHandler) activity;
        } else if (fragment instanceof NfPickerHandler) {
            return (NfPickerHandler) fragment;
        } else {
            return null;
        }
    }

    private NfKeyListener getImplementsKeyListener() {
        final Activity activity = getActivity();
        final Fragment fragment = getParentFragment();
        if (activity instanceof NfKeyListener) {
            return (NfKeyListener) activity;
        } else if (fragment instanceof NfKeyListener) {
            return (NfKeyListener) fragment;
        } else {
            return null;
        }
    }

    //FragmentManager mRetainedChildFragmentManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      /*  if (getRetainInstance()) {
            if (mRetainedChildFragmentManager != null) {
                try {
                    Field childFMField = Fragment.class.getDeclaredField("mChildFragmentManager");
                    childFMField.setAccessible(true);
                    childFMField.set(this, mRetainedChildFragmentManager);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                mRetainedChildFragmentManager = getChildFragmentManager();
            }
        }*/
    }
}
