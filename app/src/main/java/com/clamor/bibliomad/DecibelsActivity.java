package com.clamor.bibliomad;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.clamor.decibelmeter.DecibelMeter;
import com.google.firebase.auth.FirebaseAuth;

public class DecibelsActivity extends AppCompatActivity {
    private TextView decibelTextView;
    private final DecibelMeter dbmeter = new DecibelMeter();
    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decibels_activity);

        decibelTextView = findViewById(R.id.decibel_text_view);
        decibelTextView.setTextColor(getResources().getColor(R.color.black));
        isRecording = false;

        Button btn = findViewById(R.id.record_btn);
        ColorStateList originalBtnColor = btn.getBackgroundTintList();
        ColorStateList redTint = ColorStateList.valueOf(getResources().getColor(R.color.red));

        btn.setOnClickListener(x -> {
            if(!isRecording) {
                btn.setBackgroundTintList(redTint);
                btn.setText("Terminar grabación");
                dbmeter.startRecording(this);
            }
            else {
                dbmeter.stopRecording();
                btn.setText("Empezar grabación");
                btn.setBackgroundTintList(originalBtnColor);
            }
            isRecording = !isRecording;
        });
    }

    public void updateDecibelDisplay(double decibels) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                decibelTextView.setText(String.format("%.2f dB", decibels));

                // Change text color based on loudness
                if (decibels < 40) {
                    decibelTextView.setTextColor(getResources().getColor(R.color.green));
                } else if (decibels < 50) {
                    decibelTextView.setTextColor(getResources().getColor(R.color.yellow));
                } else if (decibels < 80) {
                    decibelTextView.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    decibelTextView.setTextColor(getResources().getColor(R.color.red));
                }

                if (!isRecording) {
                    decibelTextView.setTextColor(getResources().getColor(R.color.black));
                    decibelTextView.setText("(Esperando lectura)");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isRecording)
            dbmeter.stopRecording();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRecording && dbmeter.isAudioRecordInitialized()) {
            dbmeter.stopRecording();
        }
        FirebaseAuth.getInstance().signOut();
    }
}
