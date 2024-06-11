package com.clamor.bibliomad;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu_activity);

        Button btn = findViewById(R.id.btn);
        Intent intent = new Intent(MenuActivity.this, ListActivity.class);
        btn.setOnClickListener(x -> startActivity(intent));

        Button btnDb = findViewById(R.id.btn_db);
        Intent intent_db = new Intent(MenuActivity.this, DecibelsActivity.class);
        btnDb.setOnClickListener(x -> startActivity(intent_db));
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().signOut();
    }
}
