package com.rocketflyer.rocketflow.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;

import com.tracki.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiSelectSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    private List<String> items;
    private boolean[] selected;
    private String defaultText;
    private String label;
    private MultiSpinnerListener listener;

    public MultiSelectSpinner(Context context) {
        super(context);
    }

    public MultiSelectSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSelectSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected[which] = isChecked;
//        checkItemValue();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        StringBuilder spinnerBuilder = new StringBuilder();
        boolean someUnselected = false;
        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                spinnerBuilder.append(items.get(i));
                spinnerBuilder.append(", ");
            } else {
                someUnselected = true;
            }
        }
        String spinnerText;
        if (someUnselected) {
            spinnerText = spinnerBuilder.toString();
            if (spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        } else {
            spinnerText = defaultText;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{spinnerText});
        setAdapter(adapter);
        listener.onItemsSelected(selected);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean performClick() {
//        super.performClick();
//        checkItemValue();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        builder.setTitle(label);
        builder.setMultiChoiceItems(items.toArray(new CharSequence[0]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                (dialog, which) -> dialog.cancel());
        builder.setOnCancelListener(this);
//        TextView textView = (TextView) builder.findViewById(android.R.id.message);
//        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/FONT");
//        textView.setTypeface(face);
        builder.show();
        return true;
    }

    private void checkItemValue() {
        for (boolean b : selected) {
            Log.e("Selected ITems-", "--------------------------->> " + b);
        }
    }

    public void setItems(String label, List<String> items, String allText,
                         MultiSpinnerListener listener) {
        this.label = label;
        this.items = items;
        this.defaultText = allText;
        this.listener = listener;
        selected = new boolean[items.size()];

        if (allText != null) {
            String[] string = allText.split(",");
            for (int i = 0; i < selected.length; i++) {
                for (String s : string) {
                    String item = items.get(i);
                    if (item.equalsIgnoreCase(s)) {
                        selected[i] = true;
                        break;
                    }
                }
            }
        }
        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, new String[]{allText}) {

            @NotNull
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                ((TextView) v).setTypeface(externalFont);

                return v;
            }


            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                ((TextView) v).setTypeface(externalFont);
                v.setBackgroundColor(Color.GREEN);

                return v;
            }
        };
        setAdapter(adapter);
    }

    public interface MultiSpinnerListener {
        void onItemsSelected(boolean[] selected);
    }
}

