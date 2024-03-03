package com.example.beachbluenoser;

import android.view.View;

public class PasswordTransformationMethod  extends android.text.method.PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }
    //Transform password to customized symbols
    private class PasswordCharSequence implements CharSequence {

        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source;
        }

        public char charAt(int index) {
            return '*';
        }

        public int length() {
            return mSource.length();
        }

        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
}
