package com.example.jefiro.barber.home;

import static com.example.jefiro.barber.service.DistanciaCalc.getDistance;

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
import com.example.jefiro.barber.barbearia.BarbeariaComDistancia;
import com.example.jefiro.barber.barbearia.BarbeariaDetails;
import com.example.jefiro.barber.model.Cliente;
import com.example.jefiro.barber.repository.FirestoreRepository;
import com.example.jefiro.barber.service.OnDistanceCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class HomeFragment extends Fragment {

    private ShapeableImageView profileImage;
    private FirestoreRepository<Cliente> dbCliente;
    private FirestoreRepository<Barbearia> dbBarbearia;
    private FirebaseAuth mAuth;
    private LinearLayout containerBarbearias;
    private Double usuarioLat, usuarioLon;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        profileImage = view.findViewById(R.id.profileImage);
        containerBarbearias = view.findViewById(R.id.containerServicos);

        mAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        dbCliente = new FirestoreRepository<>();
        dbBarbearia = new FirestoreRepository<>();

        setImgProfile();
        getLastLocation();
        return view;
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1001
            );
            return;
        }

        if (!isGPSEnabled()) {
            showGPSDisabledAlert();
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                usuarioLat = location.getLatitude();
                usuarioLon = location.getLongitude();
                Log.d("MAPS", "Lat: " + usuarioLat + " Lon: " + usuarioLon);

                carregarBarbearias(usuarioLat, usuarioLon, null);
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
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setImgProfile() {
        String uid = mAuth.getUid();
        if (uid == null || uid.isEmpty()) return;

        dbCliente.getById("Clientes", uid, task -> {
            if (task.isSuccessful()) {
                String fotoUrl = task.getResult().getString("fotoUrl");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(this).load(fotoUrl).circleCrop().into(profileImage);
                }
            }
        });
    }

    private void setStatus(String id, TextView statusView) {
        dbBarbearia.getSubDocument("Barbearias", id, "Horarios_Funcionamento", task -> {
            List<DocumentSnapshot> docs = task.getResult().getDocuments();

            if (docs.isEmpty()) {
                statusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.status_fechado));
                statusView.setTextColor(ContextCompat.getColor(getContext(), R.color.blackPremium));
                statusView.setText("Fechado");
                return;
            }

            String diaAtual = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            LocalTime agora = LocalTime.now();
            boolean aberto = false;

            for (DocumentSnapshot doc : docs) {
                String dia = doc.getString("diaSemana");
                if (!dia.equalsIgnoreCase(diaAtual)) continue;

                List<Map<String, Object>> periods = (List<Map<String, Object>>) doc.get("periods");
                for (Map<String, Object> p : periods) {
                    LocalTime inicio = LocalTime.parse((String) p.get("open"));
                    LocalTime fim = LocalTime.parse((String) p.get("close"));
                    if (agora.isAfter(inicio) && agora.isBefore(fim)) {
                        aberto = true;
                        break;
                    }
                }
            }

            if (aberto) {
                statusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.status_aberto));
                statusView.setTextColor(ContextCompat.getColor(getContext(), R.color.blackPremium));
                statusView.setText("Aberto");
            } else {
                statusView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.status_fechado));
                statusView.setTextColor(ContextCompat.getColor(getContext(), R.color.blackPremium));
                statusView.setText("Fechado");
            }
        });
    }

    private void distance(double lat1, double lon1, double lat2, double lon2, OnDistanceCallback callback) {
        getDistance(lat1, lon1, lat2, lon2, callback);
    }

    private void carregarBarbearias(double usuarioLat, double usuarioLon, String pesquisaNome) {
        dbBarbearia.getAll("Barbearias", task -> {
            if (!task.isSuccessful()) return;

            List<DocumentSnapshot> docs = task.getResult().getDocuments();
            List<BarbeariaComDistancia> lista = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(docs.size());

            for (DocumentSnapshot doc : docs) {
                Log.d("DEBUG_BARBEARIA", "🟡 BARBEARIA LIDA DO FIREBASE → " + doc.getString("nome"));

                dbBarbearia.getSubDocument("Barbearias", doc.getId(), "Enderecos", task1 -> {

                    if (!task1.isSuccessful() || task1.getResult().isEmpty()) {
                        Log.e("DEBUG_BARBEARIA", "❌ SEM ENDEREÇO - NÃO ENTRA NA LISTA → " + doc.getString("nome"));
                        latch.countDown();
                        return;
                    }

                    DocumentSnapshot enderecoDoc = task1.getResult().getDocuments().get(0);

                    Double lat = enderecoDoc.getDouble("lat");
                    Double lon = enderecoDoc.getDouble("log");

                    if (lat == null || lon == null) {
                        Log.e("DEBUG_BARBEARIA", "❌ LAT/LON NULA - DESCARTADA → " + doc.getString("nome"));
                        latch.countDown();
                        return;
                    }

                    distance(usuarioLat, usuarioLon, lat, lon, new OnDistanceCallback() {
                        @Override
                        public void onSucefull(double distanceKm) {
                            Log.d("DEBUG_BARBEARIA", "🟢 ADICIONADA NA LISTA → " + doc.getString("nome") + " | " + distanceKm + "km");
                            lista.add(new BarbeariaComDistancia(doc, distanceKm));
                            latch.countDown();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("DEBUG_BARBEARIA", "⚠ ERRO DISTÂNCIA: " + doc.getString("nome") + " | " + error);
                            latch.countDown();
                        }
                    });
                });
            }


            new Thread(() -> {
                try {
                    latch.await();
                    ordenarEExibir(lista);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }


    private void ordenarEExibir(List<BarbeariaComDistancia> lista) {
        Collections.sort(lista, Comparator.comparingDouble(BarbeariaComDistancia::getDistanciaMetros));

        getActivity().runOnUiThread(() -> {
            containerBarbearias.removeAllViews();

            for (BarbeariaComDistancia b : lista) {
                DocumentSnapshot doc = b.getDoc();

                View v = getLayoutInflater().inflate(R.layout.card_barbearia, containerBarbearias, false);

                ImageView imgBarbearia = v.findViewById(R.id.imgBarbearia);
                TextView nomeBarbearia = v.findViewById(R.id.tvNomeBarbearia);
                TextView tvEndereco = v.findViewById(R.id.tvEndereco);
                TextView tvStatus = v.findViewById(R.id.tvStatus);
                TextView tvDistancia = v.findViewById(R.id.tvDistancia);
                LinearLayout tvDetails = v.findViewById(R.id.tvDetails);

                tvDetails.setOnClickListener(l -> {
                    Intent intent = new Intent(getContext(), BarbeariaDetails.class);
                    intent.putExtra("idBarbearia", doc.getId());
                    intent.putExtra("status", tvStatus.getText());
                    startActivity(intent);
                });


                Glide.with(this)
                        .load(doc.getString("fotoUrl"))
                        .circleCrop()
                        .into(imgBarbearia);

                nomeBarbearia.setText(doc.getString("nome"));
                tvDistancia.setText(String.format("%.2f km", b.getDistanciaMetros() / 1000));

                setStatus(doc.getId(), tvStatus);

                dbBarbearia.getSubDocument("Barbearias", doc.getId(), "Enderecos", t -> {
                    List<DocumentSnapshot> endDocs = t.getResult().getDocuments();
                    if (!endDocs.isEmpty()) {
                        DocumentSnapshot end = endDocs.get(0);
                        tvEndereco.setText(end.getString("rua") + ", " + end.getString("numero"));
                    }
                });

                containerBarbearias.addView(v);
            }
        });
    }
}
