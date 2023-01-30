package com.wadaro.promotor.ui.demo.process;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.wadaro.promotor.R;
import com.wadaro.promotor.api.Config;
import com.wadaro.promotor.api.ErrorCode;
import com.wadaro.promotor.api.PostManager;
import com.wadaro.promotor.module.Utility;
import com.wadaro.promotor.ui.order.InfoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CoordinatorInfoDialog extends Dialog {

    private final LinearLayout lnly_body_00;
    public CoordinatorInfoDialog(@NonNull Context context) {
        super(context, R.style.AppTheme_transparent);

        WindowManager.LayoutParams wlmp = Objects.requireNonNull(getWindow()).getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.demo_dialog_coordinator, null);
        setContentView(view);

        int radius = Utility.dpToPx(context, 5);
        RelativeLayout rvly_body_00 = view.findViewById(R.id.rvly_body_00);
        rvly_body_00.setBackground(Utility.getRectBackground("ffffff",radius));

        lnly_body_00 = view.findViewById(R.id.lnly_body_00);

        view.findViewById(R.id.imvw_close_00).setOnClickListener(v -> dismiss());
    }

    public void show(String mBookingID) {
        super.show();
        lnly_body_00.removeAllViews();
        String param = "?booking_id="+mBookingID;
        PostManager post = new PostManager(getContext(), Config.GET_DEMO_DATA_DETAIL+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                buildData(obj);
            }
        });
    }

    private void buildData(JSONObject obj){
        try {
            JSONObject data     = obj.getJSONObject("data");
            JSONObject booking  = data.getJSONObject("booking");
            JSONObject coordinator = booking.getJSONObject("coordinator");


            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id"));
            Date demoDate = oldFormat.parse(booking.getString("date"));

            if (demoDate != null){
                buildInfo("Tanggal Demo", dateFormat.format(demoDate));
            }
            buildInfo("Koordinator", coordinator.getString("name"));
            buildInfo("Alamat", coordinator.getString("address"));
            buildInfo("No. KTP", coordinator.getString("ktp"));
            buildInfo("No. HP", coordinator.getString("phone"));

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void buildInfo(String key, String value){
        InfoView infoView = new InfoView(getContext(), null);
        if (value.equals("null")){
            infoView.create(key, "-");
        }
        else {
            infoView.create(key, value.trim());
        }
        lnly_body_00.addView(infoView);
    }
}
