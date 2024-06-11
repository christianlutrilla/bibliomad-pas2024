package com.clamor.decibelmeter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.clamor.bibliomad.DecibelsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DecibelMeter {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    private static final double REFERENCE_SOUND_PRESSURE = 20.0; // μPa
    private AudioRecord audioRecord;
    private final DatabaseReference decibelRef;
    private final List<Double> decibelReadings = new ArrayList<>();
    private long lastSavedTime = 0;
    private boolean isAudioRecordReleased = false;


    public DecibelMeter() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        decibelRef = database.getReference("decibel_readings");
    }

    public void startRecording(Activity activity) {
        isAudioRecordReleased = false;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    BUFFER_SIZE
            );

            audioRecord.startRecording();

            new Thread(() -> {
                short[] buffer = new short[BUFFER_SIZE];

                while (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {

                    audioRecord.read(buffer, 0, BUFFER_SIZE);
                    double decibels = calculateDecibels(buffer);
                    decibelReadings.add(decibels);

                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastSavedTime >= 10000) { // 10 seconds have passed
                        double averageDecibels = calculateAverage(decibelReadings);
                        Log.d("Firebase", "Attempting to send data: " + averageDecibels);

                        pushToDatabase(averageDecibels, currentTime);
                        decibelReadings.clear();
                        lastSavedTime = currentTime;
                    }
                    activity.runOnUiThread(() -> ((DecibelsActivity) activity).updateDecibelDisplay(decibels));
                }
            }).start();
        }
    }

    public void pushToDatabase(double averageDecibels, long currentTime) {
        HashMap<String, Object> dataToSend = new HashMap<>();
        dataToSend.put("timestamp", currentTime);
        dataToSend.put("decibels", averageDecibels);

        DatabaseReference newReadingRef = decibelRef.push();
        newReadingRef.setValue(dataToSend, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                Log.e("Firebase", "Data could not be saved: " + databaseError.getMessage());
            } else {
                Log.d("Firebase", "Data saved successfully.");
            }
        });
    }
    public void stopRecording() {
        if (!isAudioRecordReleased && audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            isAudioRecordReleased = true; // Set to true after releasing
        }
    }

    private double calculateDecibels(short[] buffer) {
        double sum = 0.0;
        for (short value : buffer) {
            sum += value * value;
        }
        double rms = Math.sqrt(sum / buffer.length);
        return 20 * Math.log10(rms / REFERENCE_SOUND_PRESSURE);
    }

    private double calculateAverage(List<Double> readings) {
        if (readings.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (double reading : readings) {
            sum += reading;
        }
        return sum / readings.size();
    }

    public boolean isAudioRecordInitialized() {
        return audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED;
    }

}
