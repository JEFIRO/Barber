package com.example.jefiro.barber.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.jefiro.barber.R;
import com.example.jefiro.barber.barbearia.Barbearia;
import com.example.jefiro.barber.model.Cliente;
import com.example.jefiro.barber.repository.FirestoreRepository;
import com.example.jefiro.barber.service.App;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private ShapeableImageView profileImage;
    private FirestoreRepository<Cliente> db;
    private FirestoreRepository<Barbearia> dbBarbearia;
    private LinearLayout containerBarbearias;
    private TextView tvStatus;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        profileImage = view.findViewById(R.id.profileImage);

        containerBarbearias = view.findViewById(R.id.containerServicos);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        db = new FirestoreRepository<Cliente>();
        setImgProfile();
        getLastLocation();
        return view;
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1001
            );
        }
        if (!isGPSEnabled()) {
            showGPSDisabledAlert();
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                Log.d("MAPS", String.valueOf(latitude));

                double longitude = location.getLongitude();
                Log.d("MAPS", String.valueOf(longitude));
            }
        });
    }


    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showGPSDisabledAlert() {
        new AlertDialog.Builder(requireContext())
                .setTitle("GPS desativado")
                .setMessage("Para continuar, ative o GPS do dispositivo.")
                .setCancelable(false)
                .setPositiveButton("Ativar", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setImgProfile() {
        String uid = App.getmAuth().getCurrentUser().getUid();
        if (!uid.isEmpty()) {
            db.getById("Clientes", uid, task -> {
                if (task.isSuccessful()) {

                    String fotoUrl = task.getResult().getString("fotoUrl");

                    if (fotoUrl != null && !fotoUrl.isEmpty()) {
                        Glide.with(this)
                                .load(fotoUrl)
                                .circleCrop()
                                .into(profileImage);
                    }
                }
            });
        }
    }

    private void construirLayout(String id) {
        containerBarbearias.removeAllViews();
        dbBarbearia.getAll("Barbearia", task -> {

            if (task.isSuccessful()) {

                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    View v = getLayoutInflater().inflate(R.layout.card_barbearia, containerBarbearias, false);

                    ImageView imgBarbearia = v.findViewById(R.id.imgBarbearia);
                    var nomeBarbearia = v.findViewById(R.id.tvNomeBarbearia);
                    var tvEndereco = v.findViewById(R.id.tvEndereco);
                    tvStatus = v.findViewById(R.id.tvStatus);
                    var tvDistancia = v.findViewById(R.id.tvDistancia);

                    setStatus(doc.getId());

                }

            }
        });
    }

    private void setStatus(String id) {
        dbBarbearia.getSubDocument("Barbearias", id, "Horarios_Funcionamento", task -> {

            List<DocumentSnapshot> docs = task.getResult().getDocuments();
            if (docs.isEmpty()) return;

            String diaAtual = LocalDate.now()
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

            LocalTime agora = LocalTime.now();

            boolean aberto = false;

            for (DocumentSnapshot doc : docs) {

                String dia = doc.getString("diaSemana");

                if (!dia.equalsIgnoreCase(diaAtual)) continue;

                List<Map<String, Object>> periods =
                        (List<Map<String, Object>>) doc.get("periods");

                for (Map<String, Object> p : periods) {

                    String open = (String) p.get("open");
                    String close = (String) p.get("close");

                    LocalTime inicio = LocalTime.parse(open);
                    LocalTime fim = LocalTime.parse(close);

                    if (agora.isAfter(inicio) && agora.isBefore(fim)) {
                        aberto = true;
                        break;
                    }
                }
            }

            if (aberto) {
                tvStatus.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.status_aberto));
                tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.blackPremium));
                tvStatus.setText("Aberto");
            } else {
                tvStatus.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.status_fechado));
                tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.blackPremium));
                tvStatus.setText("Fechado");
            }

        });
    }


}
