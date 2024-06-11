package com.clamor.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clamor.bibliomad.R;
import com.clamor.library.RawLibrary;

public class LibraryViewholder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView location;

    public LibraryViewholder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.library_name);
        location = itemView.findViewById(R.id.location);
    }

    public void bind(RawLibrary library) {
        title.setText(library.getTitle());
        location.setText("> " + library.getLocation().getLatitude()
                + ", " + library.getLocation().getLongitude() + "\n");
    }
}
