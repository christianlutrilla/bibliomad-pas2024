package com.clamor.bibliomad;

import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clamor.client.RetrofitClient;
import com.clamor.library.DatosMadridResponse;
import com.clamor.client.DatosMadridService;
import com.clamor.library.LibraryDetail;
import com.clamor.library.LibraryDetailsResponse;
import com.clamor.library.RawLibrary;
import com.clamor.recyclerview.LibraryAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListActivity extends AppCompatActivity {
    private final List<RawLibrary> rawLibraries = new ArrayList<>();
    private LibraryAdapter adapter;
    private String selectedDistrict;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new LibraryAdapter(rawLibraries, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration
                (this, DividerItemDecoration.VERTICAL));

        initDistrictSpinner();
        initButton();
    }

    private void initButton() {
        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rawLibraries.clear();
                fetchLibraries(selectedDistrict);
            }
        });
    }

    private void initDistrictSpinner() {
        Spinner districtSpinner = findViewById(R.id.district_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.district_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectedDistrict = parent.getItemAtPosition(position).toString();
                        //.replace(" ", "%20");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        districtSpinner.setAdapter(adapter);
    }

    private void fetchLibraries(String selectedDistrict) {
        DatosMadridService service = RetrofitClient.getInstance().getDatosMadridService();

        service.getRawLibraries(selectedDistrict).enqueue(new Callback<DatosMadridResponse>() {
            @Override
            public void onResponse(@NonNull Call<DatosMadridResponse> call, @NonNull Response<DatosMadridResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getRawLibraries() != null) {
                        rawLibraries.addAll(response.body().getRawLibraries());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("API_CALL", "Response was not successful.");
                }
            }

            @Override
            public void onFailure(Call<DatosMadridResponse> call, Throwable t) {
                Log.d("API_CALL", "Failure when making the API call.");
            }
        });
    }

    public void fetchLibraryDetails(String libraryId) {
        DatosMadridService service = RetrofitClient.getInstance().getDatosMadridService();

        Log.d("API_CALL", "Fetching details for: " + libraryId);
        service.getLibraryDetails(libraryId).enqueue(new Callback<LibraryDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<LibraryDetailsResponse> call, @NonNull Response<LibraryDetailsResponse> response) {
                if (response.isSuccessful() && Objects.requireNonNull(response.body()).getDetailedLibrary() != null) {
                    List<LibraryDetail> detailedLibraries = new ArrayList<>();
                    detailedLibraries.addAll(response.body().getDetailedLibrary());
                    if (!detailedLibraries.isEmpty()) {
                        createAndShowPopup(detailedLibraries.get(0));
                    }
                } else {
                    Log.d("API_CALL", "Response was not successful.");
                }
            }

            @Override
            public void onFailure(Call<LibraryDetailsResponse> call, Throwable t) {
                Log.d("API_CALL", "Failure when making the API call.");
            }
        });
    }

    private void createAndShowPopup(LibraryDetail libraryDetail) {
        View popupView = getLayoutInflater().inflate(R.layout.detailed_library_popup, null);

        TextView title = popupView.findViewById(R.id.popup_library_title);
        TextView address = popupView.findViewById(R.id.popup_library_address);
        TextView schedule = popupView.findViewById(R.id.popup_library_schedule);
        TextView services = popupView.findViewById(R.id.popup_library_services);

        Button openMapsButton = popupView.findViewById(R.id.open_maps_button);

        String fullAddress = libraryDetail.getAddress().getStreetAddress() + ", "
                + libraryDetail.getAddress().getLocality() + ", "
                + libraryDetail.getAddress().getPostalCode();

        title.setText(libraryDetail.getTitle());
        address.setText(fullAddress);
        schedule.setText("HORARIO:\n" + libraryDetail.getOrganization().getSchedule());
        services.setText("SERVICIOS:\n" + libraryDetail.getOrganization().getServices());

        openMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(fullAddress));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(ListActivity.this, "Google Maps app not found!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().signOut();
    }
}
