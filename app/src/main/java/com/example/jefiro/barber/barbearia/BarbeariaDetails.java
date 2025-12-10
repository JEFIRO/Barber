package com.example.jefiro.barber.barbearia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.jefiro.barber.R;
import com.example.jefiro.barber.agendamento.AgendamentosPage;
import com.example.jefiro.barber.repository.FirestoreRepository;
import com.example.jefiro.barber.repository.OnCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BarbeariaDetails extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirestoreRepository<Barbearia> db;
    private ImageView imgBarbearia;
    private Double lat, lon;
    private TextView tvStatus, tv_Endereco, tvBarbeariaNome;
    private LinearLayout containerServicos, containerBarbeiro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barbearia_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String idBarbearia = getIntent().getStringExtra("idBarbearia");
        String status = getIntent().getStringExtra("status");
        Log.d("Barbearia", idBarbearia);

        db = new FirestoreRepository<Barbearia>();
        mAuth = FirebaseAuth.getInstance();

        imgBarbearia = findViewById(R.id.imgBarbearia);
        tvBarbeariaNome = findViewById(R.id.tvBarbeariaNome);

        tv_Endereco = findViewById(R.id.tv_Endereco);

        tvStatus = findViewById(R.id.tvStatus);

        containerServicos = findViewById(R.id.containerServicos);
        containerBarbeiro = findViewById(R.id.containerBarbeiro);

        setBarbearia(idBarbearia, new OnCallback<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot result) {
                Barbearia barbearia = result.toObject(Barbearia.class);

                Glide.with(getApplicationContext())
                        .load(barbearia.getFotoUrl())
                        .centerCrop()
                        .into(imgBarbearia);
                tvBarbeariaNome.setText(barbearia.getNome());

            }

            @Override
            public void onFailure(String e) {

            }
        });
        getEndereco(idBarbearia, new OnCallback<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot result) {
                if (result.getDocuments().size() == 1) {
                    var doc = result.getDocuments().get(0);

                    var rua = doc.getString("rua");
                    var bairro = doc.getString("bairro");
                    var cidade = doc.getString("cidade");
                    var numero = doc.getString("numero");

                    lat = doc.getDouble("lat");
                    lon = doc.getDouble("log");

                    String endereco = rua + ", " + numero + " - " + bairro + ", " + cidade;

                    tv_Endereco.setText(endereco);
                }
            }

            @Override
            public void onFailure(String e) {

            }
        });
        getServicos(idBarbearia, new OnCallback<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot result) {
                if (!result.isEmpty()) {
                    containerServicos.removeAllViews();

                    result.getDocuments().forEach(doc -> {

                        View v = getLayoutInflater().inflate(R.layout.item_servico, containerServicos, false);

                        var duracao = doc.getString("duracao");
                        var nome = doc.getString("nome");
                        var preco = doc.getDouble("preco");

                        ImageView cardImage = v.findViewById(R.id.cardImage);
                        TextView cardTitle = v.findViewById(R.id.cardTitle);
                        TextView cardPreco = v.findViewById(R.id.cardPreco);
                        TextView cardDuracao = v.findViewById(R.id.cardDuracao);

                        v.findViewById(R.id.btnAgendar).setOnClickListener(l -> {
                            Intent intent = new Intent(getApplicationContext(), AgendamentosPage.class);
                            intent.putExtra("idServico", doc.getId());
                            intent.putExtra("idBarbearia", idBarbearia);
                            startActivity(intent);
                        });

                        if (nome.toLowerCase().contains("cabelo e barba")) {
                            cardImage.setImageResource(R.drawable.ic_cabelo_barba);
                        } else if (nome.toLowerCase().contains("cabelo")) {
                            cardImage.setImageResource(R.drawable.ic_hair_cut);
                        } else if (nome.toLowerCase().contains("barba")) {
                            cardImage.setImageResource(R.drawable.ic_barba);
                        } else {
                            cardImage.setImageResource(R.drawable.ic_hair_cut);
                        }


                        String precoFormatado = String.format(Locale.getDefault(), "R$ %.2f", preco);
                        cardPreco.setText(precoFormatado);
                        cardTitle.setText(capitalizarTitulo(nome));
                        cardDuracao.setText(formatarDuracao(duracao));

                        containerServicos.addView(v);
                    });
                }
            }

            @Override
            public void onFailure(String e) {

            }
        });
        getBarbeiro(idBarbearia, new OnCallback<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot result) {
                if (!result.isEmpty()) {
                    containerBarbeiro.removeAllViews();
                    result.getDocuments().forEach(doc -> {
                        View v = getLayoutInflater().inflate(R.layout.item_barbeiro, containerBarbeiro, false);

                        var imgBarbeiro = doc.getString("fotoBarbeiro");
                        var nome = doc.getString("nome");

                        ImageView cardImg = v.findViewById(R.id.cardImage);
                        TextView cardTitle = v.findViewById(R.id.cardTitle);

                        cardTitle.setText(nome);

                        Glide.with(getApplicationContext())
                                .load(imgBarbeiro)
                                .circleCrop()
                                .into(cardImg);

                        containerBarbeiro.addView(v);

                    });
                }
            }

            @Override
            public void onFailure(String e) {

            }
        });

        if (!status.isEmpty() && status.toLowerCase().contains("aberto")) {
            tvStatus.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.status_aberto));
            tvStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blackPremium));
            tvStatus.setText("Aberto");
        } else {
            tvStatus.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.status_fechado));
            tvStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blackPremium));
            tvStatus.setText("Fechado");
        }
    }

    private void setBarbearia(String id, OnCallback<DocumentSnapshot> callback) {
        db.getById("Barbearias", id, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(task.getResult());
            } else {
                callback.onFailure("Erro ao buscar barbearia.");

            }
        });
    }

    private void getEndereco(String id, OnCallback<QuerySnapshot> callback) {
        db.getSubDocument("Barbearias", id, "Enderecos", task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(task.getResult());
                    } else {
                        callback.onFailure("Error");
                    }
                }
        );

    }

    private void getServicos(String id, OnCallback<QuerySnapshot> callback) {
        db.getSubDocument("Barbearias", id, "Servicos", task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(task.getResult());
                    } else {
                        callback.onFailure("Error");
                    }
                }
        );

    }

    private void getBarbeiro(String id, OnCallback<QuerySnapshot> callback) {
        db.getSubDocument("Barbearias", id, "Barbeiros", task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(task.getResult());
                    } else {
                        callback.onFailure("Error");
                    }
                }
        );
    }

    public static String formatarDuracao(String duracao) {
        String[] partes = duracao.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);

        if (horas > 0 && minutos > 0)
            return horas + "h " + minutos + "min";
        else if (horas > 0)
            return horas + "h";
        else
            return minutos + "min";
    }

    public static String capitalizarTitulo(String texto) {
        if (texto == null || texto.trim().isEmpty()) return "";

        List<String> ignorar = Arrays.asList("de", "da", "do", "das", "dos", "em", "para", "por", "e", "a", "o", "com");

        String[] palavras = texto.toLowerCase().split(" ");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < palavras.length; i++) {
            String p = palavras[i];
            if (i == 0 || !ignorar.contains(p)) {
                p = p.substring(0, 1).toUpperCase() + p.substring(1);
            }
            resultado.append(p).append(" ");
        }
        return resultado.toString().trim();
    }

    public void openInGoogleMaps(View v) {
        String label = "Barbearia";

        Uri uri = Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(" + label + ")");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

}