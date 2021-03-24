package com.qtt.thebarber.Common;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.qtt.thebarber.Model.Barber;
import com.qtt.thebarber.Model.BarberService;
import com.qtt.thebarber.Model.BookingInformation;
import com.qtt.thebarber.Model.MyNotification;
import com.qtt.thebarber.Model.MyToken;
import com.qtt.thebarber.Model.Salon;
import com.qtt.thebarber.Model.User;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.LayoutRatingDialogBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class Common {
    public static final String IS_LOGIN = "isLogin";
    public static final String KEY_ENABLE_BUTTON_NEXT = "keyEnableButtonNext";
    public static final String KEY_SALON_STORED = "keySalonStored";
    public static final String KEY_LOAD_BARBERS_DONE = "keyLoadBarbersDone";
    public static final String KEY_BARBERS_STORED = "keyBarbersStored";
    public static final String KEY_STEP = "keyStep";
    public static final String KEY_BARBER_SELECTED = "keyBarberSelected";
    public static final int TIME_SLOT_TOTAL = 20;
    public static final String KEY_DISPLAY_TIME_SLOT = "keyDisplayTimeSlot/";
    public static final String KEY_DISABLE = "disable";
    public static final String KEY_TIME_SLOT = "keyTimeSlot";
    public static final String KEY_CONFIRM_BOOKING = "keyConfirmBooking";
    public static final String EVENT_URI_SAVE = "eventUriSave";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static final String LOGGED_KEY = "loggedKey";
    public static final String RATING_INFORMATION_KEY = "ratingInformationKey";
    public static final String RATING_STATE_KEY = "ratingStateKey";
    public static final String RATING_SALON_ID = "ratingSalonId";
    public static final String RATING_SALON_NAME = "ratingSalonName";
    public static final String RATING_BARBER_ID = "ratingBarberId";
    public static final Integer MAX_NOTI_PER_LOAD = 10;
    public static User currentUser;
    public static Salon currentSalon = null;
    public static int currentStep = 0;
    public static String cityName = "";
    public static Barber currentBarber = null;
    public static int currentTimeSlot = -1;
    public static Calendar bookingDate = Calendar.getInstance();
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
    public static BookingInformation currentBookingInfo = null;
    public static String currentBookingId = "";
    public static MyNotification currentNotification;
    public static BarberService selectedService = null;
    public static Integer PRICE_STANDARD_RANK = 200;
    public static Integer PRICE_BRONZE_RANK = 400;
    public static Integer PRICE_SILVER_RANK = 600;
    public static Integer PRICE_GOLD_RANK = 1000;

    public static String STANDARD_RANK = "Standard Rank";
    public static String BRONZE_RANK = "Bronze Rank";
    public static String SILVER_RANK = "Silver Rank";
    public static String GOLD_RANK = "Gold Rank";
    public static String PLATINUM_RANK = "Platinum Rank";

    public static String getRank(Integer money) {
        if (PRICE_STANDARD_RANK < money && money<= PRICE_BRONZE_RANK) {
            return BRONZE_RANK;
        } else if (PRICE_BRONZE_RANK < money && money<= PRICE_SILVER_RANK) {
            return SILVER_RANK;
        } else if (PRICE_SILVER_RANK < money && money<= PRICE_GOLD_RANK) {
            return GOLD_RANK;
        } else if (PRICE_GOLD_RANK < money) {
            return PLATINUM_RANK;
        }

        return STANDARD_RANK;
    }

    public static String convertTimeSlotToString(int position) {
        switch (position) {
            case 0:
                return "9:00 - 9:30";
            case 1:
                return "9:30 - 10:00";
            case 2:
                return "10:00 - 10:30";
            case 3:
                return "10:30 - 11:00";
            case 4:
                return "11:00 - 11:30";
            case 5:
                return "11:30 - 12:00";
            case 6:
                return "12:00 - 12:30";
            case 7:
                return "12:30 - 13:00";
            case 8:
                return "13:00 - 13:30";
            case 9:
                return "13:30 - 14:00";
            case 10:
                return "14:00 - 14:30";
            case 11:
                return "14:30 - 15:00";
            case 12:
                return "15:00 - 15:30";
            case 13:
                return "15:30 - 16:00";
            case 14:
                return "16:00 - 16:30";
            case 15:
                return "16:30 - 17:00";
            case 16:
                return "17:00 - 17:30";
            case 17:
                return "17:30 - 18:00";
            case 18:
                return "18:00 - 18:30";
            case 19:
                return "18:30 - 19:00";
            case 20:
                return "19:00 - 19:30";
            default:
                return "Closed";
        }
    }

    public static String convertTimeStampToStringKey(Timestamp timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        Date date = timestamp.toDate();
        return simpleDateFormat.format(date);
    }

    public static String formatShoppingItemName(String name) {
        return name.length() > 13 ? new StringBuilder(name.substring(0, 10)).append("...").toString() : name;
    }

    public static void updateToken(Context context, final String s) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            MyToken myToken = new MyToken();
            myToken.setToken(s);
            myToken.setTokenType(TOKEN_TYPE.CLIENT);
            myToken.setUserPhone(user.getPhoneNumber());

            FirebaseFirestore.getInstance()
                    .collection("Tokens")
                    .document(user.getPhoneNumber())
                    .set(myToken)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
        } else {
            Paper.init(context);
            String localUser = Paper.book().read(Common.LOGGED_KEY);

            if (localUser != null) {
                if (!TextUtils.isEmpty(localUser)) {
                    MyToken myToken = new MyToken();
                    myToken.setToken(s);
                    myToken.setTokenType(TOKEN_TYPE.CLIENT);
                    myToken.setUserPhone(localUser);

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(localUser)
                            .set(myToken)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                }
            }
        }
    }

    public static void showNotification(Context context, int notiId, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;

        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(
                    context,
                    notiId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }

        String NOTIFICATION_CHANEL_ID = "my_chanel_id_01";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANEL_ID, "My notification barber booking app", NotificationManager.IMPORTANCE_DEFAULT);

            //Config notification chanel
            notificationChannel.setDescription("Chanel description");
            notificationChannel.enableLights(true);
           // notificationChannel.setLightColor(Color.RED);
           // notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        Notification notification = builder.build();

        notificationManager.notify(notiId, notification);
    }

    public static void showRatingDialog(Context context, String ratingStateKey, String ratingSalonId, String ratingSalonName, String ratingBarberId) {

        ///AllSalon/Florida/Branch/0n7ikrtgQXW4EXhuJ0qy/Barbers/Nsa4hBFukd8UZYMiRe5y
        DocumentReference barberNeedRateRef = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(ratingStateKey)
                .collection("Branch")
                .document(ratingSalonId)
                .collection("Barbers")
                .document(ratingBarberId);

        Log.d("AAA", "showRatingDialog: " + ratingBarberId);


        barberNeedRateRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Barber barberRate = task.getResult().toObject(Barber.class);
                        barberRate.setBarberId(task.getResult().getId());

                        LayoutRatingDialogBinding binding = LayoutRatingDialogBinding.inflate(LayoutInflater.from(context), null, false);

                        binding.tvBarberName.setText(barberRate.getName());
                        binding.tvSalonName.setText(ratingSalonName);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setView(binding.getRoot())
                                .setCancelable(false)
                                .setNegativeButton("NOT NOW", (dialog, which) -> dialog.dismiss())
                                .setPositiveButton("OK", (dialog, which) -> {

                                    Double originRating = barberRate.getRating();
                                    Long ratingTimes = barberRate.getRatingTimes();
                                    Double userRating = (double) binding.ratingBar.getRating();

                                    Double finalRating = originRating + userRating;

                                    Map<String, Object> dataUpdate = new HashMap<>();
                                    dataUpdate.put("rating", finalRating);
                                    dataUpdate.put("ratingTimes", ++ratingTimes);

                                    barberNeedRateRef.update(dataUpdate)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(context, "Thank you for rating", Toast.LENGTH_SHORT).show();

                                                    Paper.init(context);
                                                    Paper.book().delete(Common.RATING_INFORMATION_KEY);

                                                    dialog.dismiss();
                                                }
                                            }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .setNeutralButton("SKIP", (dialog, which) -> {
                                    Paper.init(context);
                                    Paper.book().delete(Common.RATING_INFORMATION_KEY);
                                    dialog.dismiss();
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    public static String getFileName(ContentResolver contentResolver, Uri fileUri) {
        String result = null;
        if (fileUri.getScheme().equals("content")) {
            Cursor cursor = contentResolver.query(fileUri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }

        if (result == null) {
            result = fileUri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    public enum TOKEN_TYPE {
        CLIENT, BARBER, MANAGER
    }
}
