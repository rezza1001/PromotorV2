package com.wadaro.promotor;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.wadaro.promotor.base.MyActivity;
import com.wadaro.promotor.component.EditextForm;

import java.util.Objects;

/**
 * Created by Mochamad Rezza Gumilang on 19/Jun/2021.
 * Class Info :
 */

public class DbugActivity extends MyActivity {

    private LinearLayout lnly_filter_00;

    @Override
    protected int setLayout() {
        return R.layout.activity_dbug;
    }

    @Override
    protected void initLayout() {
        lnly_filter_00 = findViewById(R.id.lnly_filter_00);
    }

    @Override
    protected void initData() {
        addFilter();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.mrly_close_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_add_00).setOnClickListener(v -> addFilter());
        findViewById(R.id.bbtn_find_00).setOnClickListener(v -> findData());
        findViewById(R.id.bbtn_remove_00).setOnClickListener(v -> {
            if (lnly_filter_00.getChildCount() > 0){
                int position = lnly_filter_00.getChildCount();
                lnly_filter_00.removeViewAt(position-1);
            }
        });
    }

    private void addFilter(){
        LinearLayout lnly = new LinearLayout(mActivity, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lnly.setOrientation(LinearLayout.HORIZONTAL);
        lnly.setWeightSum(10);
        lnly.setLayoutParams(lp);
        lnly_filter_00.addView(lnly);

        EditextForm formKey = new EditextForm(mActivity, null);
        formKey.setHint("Parameter "+lnly_filter_00.getChildCount());
        LinearLayout.LayoutParams lpForm1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5);
        lpForm1.setMarginEnd(10);
        lnly.addView(formKey, lpForm1);

        EditextForm formVal = new EditextForm(mActivity, null);
        formVal.setHint("Value "+lnly_filter_00.getChildCount());
        LinearLayout.LayoutParams lpForm2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5);
        lpForm2.setMarginStart(10);
        lnly.addView(formVal, lpForm2);
    }


    private void findData(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference reference = database.collection("DBUG");
        for (int i=0; i<lnly_filter_00.getChildCount(); i++){
            LinearLayout lnly = (LinearLayout) lnly_filter_00.getChildAt(i);
            EditextForm key = (EditextForm) lnly.getChildAt(0);
            EditextForm value = (EditextForm) lnly.getChildAt(1);
            reference.whereEqualTo(key.getValue(), value.getValue());
        }
        reference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d("TAGRZ", document.toString());
                        }
                    } else {
                        Log.d("TAGRZ", "Error");
                    }
                });

    }
}
