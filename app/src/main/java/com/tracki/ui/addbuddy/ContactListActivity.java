package com.tracki.ui.addbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.tracki.R;
import com.tracki.data.database.DatabaseClient;
import com.tracki.databinding.ActivityContactListBinding;
import com.tracki.utils.AppConstants;

import java.util.List;

public class ContactListActivity extends AppCompatActivity implements ContactAdapter.CallBack {
    private ContactAdapter mAdapter;
    private EditText etSearch;
    private TextView tvClear;
    private ImageView ivBack;
    private ContentLoadingProgressBar contentProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityContactListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_list);
        etSearch = findViewById(R.id.etSearch);
        tvClear = findViewById(R.id.tvClear);
        ivBack = findViewById(R.id.ivBack);
        contentProgressBar = findViewById(R.id.contentProgressBar);
        mAdapter = new ContactAdapter(this);
        binding.setAdapter(mAdapter);
        DatabaseClient.Companion.getInstance(this).getAppDatabase().contactsDataDao().getAllContact().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                if(contacts!=null && !contacts.isEmpty()){
                    contentProgressBar.setVisibility(View.GONE);
                    mAdapter.addData(contacts);
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 2)
                    mAdapter.getFilter().filter(charSequence);
                else
                    mAdapter.getFilter().filter("");


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
            }
        });

    }


    @Override
    public void onItemClick(Contact contact) {
//        if(getIntent()!=null){
//            if(getIntent().hasExtra(AppConstants.Extra.FROM)){
//                if(getIntent().getStringExtra(AppConstants.Extra.FROM).equals("dashboard")){
//
//
//                }else {
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("result", contact);
//                    setResult(Activity.RESULT_OK, returnIntent);
//                    finish();
//                }
//            }else{
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result", contact);
//                setResult(Activity.RESULT_OK, returnIntent);
//                finish();
//            }
//
//        }else{
//            Intent returnIntent = new Intent();
//            returnIntent.putExtra("result", contact);
//            setResult(Activity.RESULT_OK, returnIntent);
//            finish();
//        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", contact);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();



    }
}
