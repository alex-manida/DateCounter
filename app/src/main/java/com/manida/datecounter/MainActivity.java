package com.manida.datecounter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.manida.datecounter.databinding.ActivityMainBinding;

import java.time.LocalDate;
import java.time.Period;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    boolean isOnlyDayShown = true;
    private LocalDate selectedDate;

    private int heartIndex = 0;

    private final int[] heartImages = {
            R.drawable.heart_1,
            R.drawable.heart_2,
            R.drawable.heart_3,
            R.drawable.heart_4
    };

    // ❤️ Heartbeat animation (60 BPM)
    private final Handler heartHandler = new Handler();
    private Runnable heartBeatRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadDataFromSharedPreferences();
        initActions();

        // ❤️ start 60 BPM
        // heartbeat
        startHeartBeat60BPM();
    }

    private void loadDataFromSharedPreferences() {
        binding.tv1.setText(MySharedPreference.getString(this, MySharedPreference.TV1));
        binding.tv2.setText(MySharedPreference.getString(this, MySharedPreference.TV2));

        long epochDays = MySharedPreference.getLong(this, MySharedPreference.DATE);
        selectedDate = LocalDate.ofEpochDay(epochDays);
        updateDate();

        Uri iv1Uri = Uri.parse(MySharedPreference.getString(this, MySharedPreference.IV1));
        binding.iv1.setImageURI(iv1Uri);

        Uri iv2Uri = Uri.parse(MySharedPreference.getString(this, MySharedPreference.IV2));
        binding.iv2.setImageURI(iv2Uri);
    }

    private void updateDate() {
        if (isOnlyDayShown) {
            Period period = Period.between(selectedDate, LocalDate.now());
            binding.tvDateCounter.setText(period.getYears() + " years, " + period.getMonths() + " months, and " + period.getDays() + " days"
            );
        } else {
            int days = (int) (LocalDate.now().toEpochDay() - selectedDate.toEpochDay());
            binding.tvDateCounter.setText(days + " Days");
        }
        binding.btSelectDate.setText(selectedDate.toString());
    }

    private void initActions() {

        binding.tvDateCounter.setOnClickListener(v -> {
            updateDate();
            isOnlyDayShown = !isOnlyDayShown;
        });

        binding.btSelectDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    null,
                    selectedDate.getYear(),
                    selectedDate.getMonthValue() - 1,
                    selectedDate.getDayOfMonth()
            );
            dialog.setOnDateSetListener((d, y, m, day) -> {
                selectedDate = LocalDate.of(y, m + 1, day);
                MySharedPreference.saveLong(this, MySharedPreference.DATE, selectedDate.toEpochDay());
                updateDate();
            });
            dialog.show();
        });

        binding.tv1.setOnClickListener(v -> changeUsername(binding.tv1));
        binding.tv2.setOnClickListener(v -> changeUsername(binding.tv2));

        binding.ivHeart.setOnClickListener(v -> {

            binding.ivHeart.animate()
                    .alpha(0f)
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(200)
                    .withEndAction(() -> {

                        heartIndex = (heartIndex + 1) % heartImages.length;
                        binding.ivHeart.setImageResource(heartImages[heartIndex]);

                        binding.ivHeart.animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                                .start();
                    })
                    .start();
        });


        binding.iv1.setOnClickListener(v -> {
            Intent it = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            it.setType("image/*");
            startActivityForResult(it, 1);
        });

        binding.iv2.setOnClickListener(v -> {
            Intent it = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            it.setType("image/*");
            startActivityForResult(it, 2);
        });

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.mn_image) {
                Intent it = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                it.setType("image/*");
                startActivityForResult(it, 3);
                return true;
            } else if (item.getItemId() == R.id.mn_color) {
                new AmbilWarnaDialog(this, Color.BLACK,
                        new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            public void onCancel(AmbilWarnaDialog dialog) {
                            }

                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                binding.getRoot().setBackgroundColor(color);
                            }
                        }).show();
                return true;
            } else if (item.getItemId() == R.id.mn_about) {
                Intent intent = new Intent(this, AboutTheDeveloper.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    // ❤️ 60 BPM heartbeat animation
    private void startHeartBeat60BPM() {
        ScaleAnimation beat = new ScaleAnimation(
                1f, 1.2f,
                1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        beat.setDuration(300);
        beat.setRepeatMode(Animation.REVERSE);
        beat.setRepeatCount(1);

        heartBeatRunnable = new Runnable() {
            @Override
            public void run() {
                binding.ivHeart.startAnimation(beat);
                heartHandler.postDelayed(this, 1000); // 60 BPM
            }
        };

        heartHandler.post(heartBeatRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        heartHandler.removeCallbacks(heartBeatRunnable);
    }

    private void changeUsername(TextView tv) {
        EditText ed = new EditText(this);
        ed.setText(tv.getText());

        new AlertDialog.Builder(this)
                .setTitle(tv.getId() == R.id.tv_1
                        ? "Change name for first person"
                        : "Change name for second person")
                .setView(ed)
                .setPositiveButton("OK", (d, w) -> {
                    String text = ed.getText().toString();
                    if (!text.isEmpty()) {
                        tv.setText(text);
                        MySharedPreference.saveString(
                                this,
                                tv.getId() == R.id.tv_1
                                        ? MySharedPreference.TV1
                                        : MySharedPreference.TV2,
                                text
                        );
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (requestCode == 1) {
                binding.iv1.setImageURI(uri);
                MySharedPreference.saveString(this, MySharedPreference.IV1, uri.toString());
            } else if (requestCode == 2) {
                binding.iv2.setImageURI(uri);
                MySharedPreference.saveString(this, MySharedPreference.IV2, uri.toString());
            }
        }
    }
}
