package com.clamor.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.clamor.bibliomad.ListActivity;
import com.clamor.bibliomad.R;
import com.clamor.library.RawLibrary;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryViewholder> {

    private List<RawLibrary> libraries;
    private Context context;

    public LibraryAdapter(List<RawLibrary> libraries, Context context) {
        this.libraries = libraries;
        this.context = context;
    }

    @NonNull
    @Override
    public LibraryViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.library_layout, parent, false);
        return new LibraryViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewholder holder, int position) {
        RawLibrary library = libraries.get(position);
        holder.bind(library);

        holder.itemView.setOnClickListener(v -> {
            ((ListActivity) context).fetchLibraryDetails(library.getId());
        });
    }

    @Override
    public int getItemCount() {
        return libraries.size();
    }
}