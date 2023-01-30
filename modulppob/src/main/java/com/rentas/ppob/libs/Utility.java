    package com.rentas.ppob.libs;

    import android.Manifest;
    import android.animation.ValueAnimator;
    import android.annotation.SuppressLint;
    import android.app.Activity;
    import android.app.ActivityManager;
    import android.app.KeyguardManager;
    import android.content.ClipData;
    import android.content.ClipboardManager;
    import android.content.Context;
    import android.content.pm.PackageManager;
    import android.content.res.Resources;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.graphics.Typeface;
    import android.graphics.drawable.Drawable;
    import android.graphics.drawable.GradientDrawable;
    import android.graphics.drawable.ShapeDrawable;
    import android.graphics.drawable.shapes.OvalShape;
    import android.graphics.drawable.shapes.RoundRectShape;
    import android.os.Build;
    import android.text.Spannable;
    import android.text.SpannableString;
    import android.text.style.StyleSpan;
    import android.util.DisplayMetrics;
    import android.util.Log;
    import android.util.TypedValue;
    import android.view.Gravity;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.inputmethod.InputMethodManager;
    import android.widget.EditText;
    import android.widget.Toast;

    import androidx.core.app.ActivityCompat;
    import androidx.core.content.res.ResourcesCompat;

    import com.google.firebase.firestore.FirebaseFirestore;
    import com.rentas.ppob.R;
    import com.rentas.ppob.component.DynamicDialog;

    import java.io.IOException;
    import java.io.InputStream;
    import java.text.DateFormat;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;
    import java.util.Objects;
    import java.util.TimeZone;

    public class Utility {

        public static float dpToPx(Context context, float valueInDp) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
        }
        public static float dpToPx(Context context, double valueInDp) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) valueInDp, metrics);
        }


        public static int dpToPx(Context context, int dp) {
            float density = context.getResources()
                    .getDisplayMetrics()
                    .density;
            return Math.round((float) dp * density);
        }

        public static int getPixelValue(Context context, int dimenId) {
            Resources resources = context.getResources();
            return (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dimenId,
                    resources.getDisplayMetrics()
            );
        }


        public static void hideKeyboard(Activity activity) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        public static void showKeyboard(Activity activity, EditText editText){
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }

        public static SpannableString BoldText(Context mContext, String pText, int start, int end){
            SpannableString content = new SpannableString(pText);
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            content.setSpan(boldSpan, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return content;
        }
        public static SpannableString NormalText(String pText){
            SpannableString content = new SpannableString(pText);
            StyleSpan boldSpan = new StyleSpan(Typeface.NORMAL);
            content.setSpan(boldSpan, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return content;
        }
        public static SpannableString BoldText(Context pContext, String pText, int start, int end, int resfont){
            SpannableString content = new SpannableString(pText);
            Typeface font =  ResourcesCompat.getFont(pContext,resfont);
            content.setSpan(new CustomTypefaceSpan("", font), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            return content;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public static Drawable getShapeLine(Activity activity, int size, int radius, int lineColor, int background){
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable drawable = activity.getDrawable(R.drawable.default_line);
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            if (gradientDrawable != null){
                gradientDrawable.setCornerRadius(Utility.dpToPx(activity, radius));
                gradientDrawable.setColor(background);
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawable.setStroke(Utility.dpToPx(activity, size), lineColor);
            }
            return drawable;
        }



        public static void startAnim(final View view, final int start, final int end){
            ValueAnimator anim = ValueAnimator.ofInt(start, end);
            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = val;
                view.setLayoutParams(layoutParams);
            });
            anim.setDuration(500);
            anim.start();
        }
        public static void startAnim(final View view, final int start, final int end, int duration){
            ValueAnimator anim = ValueAnimator.ofInt(start, end);
            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = val;
                view.setLayoutParams(layoutParams);
            });
            anim.setDuration(duration);
            anim.start();
        }

        public static void startRoted(final View view, final boolean start){
            if(start){
                view.animate().rotation(180).setDuration(500).start();
            }
            else {
                view.animate().rotation(0).setDuration(500).start();
            }
        }

        public static void startRoted(final View view, int start){
            view.animate().rotation(start).setDuration(500).start();
        }


        public static void copyToClip(Context context, String text, String message){
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("rekening", text);
            clipboard.setPrimaryClip(clip);
            if (!message.isEmpty()){
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }


        public static boolean isRunning(Context ctx) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

                for (ActivityManager.RunningTaskInfo task : tasks) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                            return true;
                    }
                }

                return false;
            }
            else {
                return false;
            }

        }

        public static ShapeDrawable getOvalBackground(String color_code){
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            shapeDrawable.getPaint().setColor(Color.parseColor("#"+color_code));
            return shapeDrawable;
        }

        public static ShapeDrawable getOvalBackground(int color){
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            shapeDrawable.getPaint().setColor(color);
            return shapeDrawable;
        }

        public static ShapeDrawable getRectBackground(String color_code, int radius){
            RoundRectShape roundRectShape = new RoundRectShape(new float[]{
                    radius, radius, radius, radius,
                    radius, radius, radius, radius}, null, null);

            ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
            shapeDrawable.getPaint().setColor(Color.parseColor("#"+color_code));
            return shapeDrawable;
        }

        public static ShapeDrawable getRectBackground(int color, int radius){
            RoundRectShape roundRectShape = new RoundRectShape(new float[]{
                    radius, radius, radius, radius,
                    radius, radius, radius, radius}, null, null);

            ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
            shapeDrawable.getPaint().setColor(color);
            return shapeDrawable;
        }
        public static ShapeDrawable getRectBackground(String color_code, int leftTop, int rightTop, int leftBottom, int rightBottom){
            RoundRectShape roundRectShape = new RoundRectShape(new float[]{
                    leftTop, leftTop, rightTop, rightTop,
                    leftBottom, leftBottom, rightBottom ,rightBottom}, null, null);

            ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
            shapeDrawable.getPaint().setColor(Color.parseColor("#"+color_code));
            return shapeDrawable;
        }
        public static ShapeDrawable getRectBackground(int color_code, int leftTop, int rightTop, int leftBottom, int rightBottom){
            RoundRectShape roundRectShape = new RoundRectShape(new float[]{
                    leftTop, leftTop, rightTop, rightTop,
                    leftBottom, leftBottom, rightBottom ,rightBottom}, null, null);

            ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
            shapeDrawable.getPaint().setColor(color_code);
            return shapeDrawable;
        }

        public static Drawable getRectBackground(int color_code, int linesize, int linecolor, int leftTop, int rightTop, int leftBottom, int rightBottom){
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.RECTANGLE);
            bg.setCornerRadii(new float[] { leftTop, rightTop, leftBottom, rightBottom, 0, 0, 0, 0 });
            bg.setColor(color_code);
            bg.setStroke(linesize ,linecolor);
            return bg;
        }

        public static ShapeDrawable getLineBackground(int stroke, String color_code, int leftTop, int rightTop, int leftBottom, int rightBottom){

            RoundRectShape roundRectShape = new RoundRectShape(new float[]{
                    leftTop, leftTop, rightTop, rightTop,
                    leftBottom, leftBottom, rightBottom ,rightBottom}, null, null);

            ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
            shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
            shapeDrawable.getPaint().setStrokeWidth(stroke);
            shapeDrawable.getPaint().setColor(Color.parseColor("#"+color_code));
            return shapeDrawable;
        }



        public static String UpperAfterSpace(String text){
            text = text.toLowerCase().replaceAll(" +", " ");
            String[] x = text.split(" ");
            StringBuilder sb = new StringBuilder();
            if (x.length > 1){
                for (String x1 : x) {
                    String s1 = x1.substring(0, 1).toUpperCase();
                    String textCapitalized = s1 + x1.substring(1);
                    sb.append(textCapitalized).append(" ");
                }
            }
            else {
                if (text.length() > 1){
                    String s1 = text.substring(0, 1).toUpperCase();
                    sb.append(s1);
                    String s2 = text.substring(1);
                    sb.append(s2);
                }
                else {
                    sb.append(text.toUpperCase());
                }
            }

            return sb.toString();
        }

        public static String UpperFirst(String text){
            text = text.toLowerCase();
            StringBuilder sb = new StringBuilder();
            String s1 = text.substring(0, 1).toUpperCase();
            String textCapitalized = s1 + text.substring(1);
            sb.append(textCapitalized);
            return sb.toString();
        }

        public static Calendar getTimeServer(Date date){
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();

            try {
                TimeZone tz = TimeZone.getTimeZone("GMT+07:00");
                readDate.setTimeZone(tz);
                Date mDate = readDate.parse(readDate.format(date));
                Log.d("Utility", readDate.format(mDate));
                String sDate = readDate.format(mDate);
                String h = sDate.split(" ")[1].split(":")[0];
                String m = sDate.split(" ")[1].split(":")[1];
                String s = sDate.split(" ")[1].split(":")[2];
                calendar.setTime(mDate);
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h));
                calendar.set(Calendar.MINUTE, Integer.parseInt(m));
                calendar.set(Calendar.SECOND, Integer.parseInt(s));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return calendar;
        }

        public static Calendar getCurTimeServer(){
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();

            try {
                TimeZone tz = TimeZone.getTimeZone("GMT+07:00");
                readDate.setTimeZone(tz);
                Date mDate = readDate.parse(readDate.format(new Date()));
                Log.d("Utility", readDate.format(mDate));
                String sDate = readDate.format(mDate);
                String h = sDate.split(" ")[1].split(":")[0];
                String m = sDate.split(" ")[1].split(":")[1];
                String s = sDate.split(" ")[1].split(":")[2];
                calendar.setTime(mDate);
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h));
                calendar.set(Calendar.MINUTE, Integer.parseInt(m));
                calendar.set(Calendar.SECOND, Integer.parseInt(s));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return calendar;
        }

        public static Calendar getLocalTime(Date date){
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();

            try {
                String dateStr = readDate.format(date);
                readDate.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
                Date date2 = readDate.parse(dateStr);
                readDate.setTimeZone(TimeZone.getDefault());
                String mDate = readDate.format(date2);
                Log.d("Utility","TIME "+ mDate);

                String h = mDate.split(" ")[1].split(":")[0];
                String m = mDate.split(" ")[1].split(":")[1];
                String s = mDate.split(" ")[1].split(":")[2];
                calendar.setTime(date2);
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h));
                calendar.set(Calendar.MINUTE, Integer.parseInt(m));
                calendar.set(Calendar.SECOND, Integer.parseInt(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return calendar;
        }

        public static void showToastError(Context context, String message){
            MyToast myToast = new MyToast(context, null);
            myToast.setIcon(R.drawable.ic_clear, Color.RED);
            myToast.setMessage(message);
            myToast.show(Toast.LENGTH_LONG, Gravity.CENTER, 0,0);
        }
        public static void showToastSuccess(Context context, String message){
            MyToast myToast = new MyToast(context, null);
            myToast.setIcon(R.drawable.ic_check, Color.GREEN);
            myToast.setMessage(message);
            myToast.show(Toast.LENGTH_LONG, Gravity.CENTER, 0,0);
        }

        public static void showFailedDialog(Context context, String message){
            DynamicDialog dialog = new DynamicDialog(context);
            dialog.showError("Gagal",message);
        }

        public static boolean checkPermission(Activity activity, String[] permissions){
            return hasPermissions(activity, permissions);
        }
        public static boolean hasPermission(Activity activity, String[] permissions){
            if(!hasPermissions(activity, permissions)){
                ActivityCompat.requestPermissions(Objects.requireNonNull(activity), permissions, 32);
                return false;
            }
            else {
                return true;
            }
        }

        private static boolean hasPermissions(Context context, String... permissions) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("Utility","permission "+ permission +" false ");
                        return false;
                    }
                    Log.d("Utility","permission "+ permission +" true ");
                }
            }
            return true;
        }

        public static String getJsonFromAssets(Context context, String fileName) {
            String jsonString;
            try {
                InputStream is = context.getAssets().open(fileName);

                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                jsonString = new String(buffer, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return jsonString;
        }

        public static String getDateString(String pDate){
            pDate = pDate.replace(".000Z","");
            return pDate.replace("T"," ").split("\\.")[0];
        }

        public static String getDateString(Date pDate, String format){
            DateFormat format1 = new SimpleDateFormat(format,new Locale("id"));
            return format1.format(pDate);
        }

        public static Date getDate(String pDate){
            pDate = pDate.replace(".000Z","");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("id"));
            try {
                return format.parse(pDate.replace("T"," ").split("\\.")[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new Date();
        }


        public static Date getDate(String pDate, String formatCfg){
            DateFormat format = new SimpleDateFormat(formatCfg, new Locale("id"));
            try {
                return format.parse(pDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new Date();
        }

        public static Boolean checkBiometricSupport(Activity activity) {
            KeyguardManager keyguardManager = (KeyguardManager)activity.getSystemService(Context.KEYGUARD_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!keyguardManager.isDeviceSecure()) {
                    Utility.showFailedDialog(activity,"Fingerprint belum diaktifkan di pengaturan. Silahkan aktifkan terlebih dahulu");
                    return false;
                }
            }
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_BIOMETRIC)!= PackageManager.PERMISSION_GRANTED) {
                Utility.showFailedDialog(activity,"Permission Fingerprint belum di aktifkan");
                return false;
            }
            if (!IsSupportFingerPrint(activity)) {
                Utility.showFailedDialog(activity,"Fingerprint not supported");
                return false;
            }
            return  true;

        }

        public static boolean IsSupportFingerPrint(Activity activity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
            }
            else {
                return false;
            }
        }

        public static void SendLogLogin(String api, String param, String response, String agentCode){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String,String> data = new HashMap<>();
            data.put("API",api);
            data.put("parameter",param);
            data.put("response",response);
            data.put("agent_code",agentCode);
            data.put("date",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

            DateFormat format = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
            api = api.replace("/","-");
            String doc = format.format(new Date())+"_"+api+"_"+agentCode;
            Log.d("Utility","LOGIN_LOG "+ doc);
            db.collection("LOGIN_LOG").document(doc)
                    .set(data)
                    .addOnSuccessListener(documentReference -> Log.d("TAG", "DocumentSnapshot added"))
                    .addOnFailureListener(e1 -> Log.w("TAG", "Error adding document", e1));
        }



    }