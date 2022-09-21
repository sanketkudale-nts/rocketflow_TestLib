package com.rocketflyer.rocketflow.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.tracki.utils.AestrickSpanAdjuster;

public class AestrickUtils {

    public static void setAsterick(EditText edittext, String hint, String aestringWithSpace)
    {
        SpannableString mString = new SpannableString(hint+aestringWithSpace);
        if(mString.length() > 0) {
            mString.setSpan(new AestrickSpanAdjuster(0.2), mString.length() - 1,
                    mString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        /*edittext.append("\n");
        edittext.append(mString);*/
        edittext.setHint(mString);
    }

    /**
     * This function will apply the a colorful mandatory field aestrick with different type of views mentioned and set as hint or text accordingly
     * @param view View to be modified
     * @param hint Text with black
     * @param aestrickWithSpace text with Custom color
     * @param isText weather to set text or hint with these values
     * @param colorCode color needed for aestrickWithSpace
     */
    public static void setTextViewAsterick(View view, String hint, String aestrickWithSpace, boolean isText, int colorCode)
    {
        SpannableString mString = new SpannableString(hint+aestrickWithSpace);
        if(mString.length() > 0) {
            mString.setSpan(new AestrickSpanAdjuster(0.2), mString.length() - 1,
                    mString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            mString.setSpan(new ForegroundColorSpan(colorCode), mString.length() - 1, mString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        /*txtView.append("\n");
        txtView.append(mString);*/
        //  txtView.setHint(mString);
        if (view instanceof TextView)
        {
            if (isText)
                ((TextView)view).setText(mString);
            else
                ((TextView)view).setHint(mString);
        }
        else if (view instanceof EditText)
        {
            if (isText)
                ((EditText)view).setText(mString);
            else
                ((EditText)view).setHint(mString);
        }
        else if (view instanceof CheckBox)
        {
            if (isText)
                ((CheckBox)view).setText(mString);
            else
                ((CheckBox)view).setHint(mString);
        }
    }
}