package com.nagainfomob.app.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by lalit on 9/13/2016.
 */
public class InputValidation {
    private Context context;

    /**
     * constructor
     *
     * @param context
     */
    public InputValidation(Context context) {
        this.context = context;
    }

    /**
     * method to check InputEditText filled .
     *
     * @param textInputEditText
     * @param textInputLayout
     * @param message
     * @return
     */
    public boolean isInputEditTextFilled(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty()) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }


    /**
     * method to check InputEditText has valid email .
     *
     * @param textInputEditText
     * @param textInputLayout
     * @param message
     * @return
     */
    public boolean isInputEditTextEmail(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String message) {
        String value = textInputEditText.getText().toString().trim();
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextMatches(TextInputEditText textInputEditText1, TextInputEditText textInputEditText2, TextInputLayout textInputLayout, String message) {
        String value1 = textInputEditText1.getText().toString().trim();
        String value2 = textInputEditText2.getText().toString().trim();
        if (!value1.contentEquals(value2)) {
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText2);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextMatches(EditText textEditText1, EditText textEditText2, String message) {
        String value1 = textEditText1.getText().toString().trim();
        String value2 = textEditText2.getText().toString().trim();
        if (!value1.contentEquals(value2)) {
            textEditText2.setError(message);
            textEditText2.setFocusableInTouchMode(true);
            textEditText2.requestFocus();
            hideKeyboardFrom(textEditText2);
            return false;
        }
        return true;
    }

    /**
     * method to Hide keyboard
     *
     * @param view
     */
    private void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public boolean isEmptyText(EditText textEditText, String message) {
        String value = textEditText.getText().toString().trim();
        if (value.isEmpty()) {
            textEditText.setError(message);
            textEditText.setFocusableInTouchMode(true);
            textEditText.requestFocus();
            hideKeyboardFrom(textEditText);
            return false;
        }
        return true;
    }

    public boolean isValidEmail(EditText textEditText, String message) {
        String value = textEditText.getText().toString().trim();
        if (value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            textEditText.setError(message);
            textEditText.setFocusableInTouchMode(true);
            textEditText.requestFocus();
            hideKeyboardFrom(textEditText);
            return false;
        }
        return true;
    }

    public boolean isEmptySpinner(Spinner spinner, String message) {

        int pos =spinner.getSelectedItemPosition();
        if(pos == 0)
        {
            TextView errorText = (TextView)spinner.getSelectedView();
            errorText.setError(message);
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(message);

            spinner.setFocusableInTouchMode(true);
            spinner.requestFocus();
            hideKeyboardFrom(spinner);
            return false;
        }
        return true;
    }

    public boolean lengthValidation(EditText textEditText, String message, int length) {
        String value = textEditText.getText().toString().trim();

        if (value.length() != length) {
            textEditText.setError(message);
            textEditText.setFocusableInTouchMode(true);
            textEditText.requestFocus();
            hideKeyboardFrom(textEditText);
            return false;
        }
        return true;


    }
}
