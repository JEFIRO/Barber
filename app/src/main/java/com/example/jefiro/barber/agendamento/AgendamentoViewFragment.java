package com.example.jefiro.barber.agendamento;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.jefiro.barber.R;
import com.example.jefiro.barber.barbearia.Barbearia;
import com.example.jefiro.barber.barbearia.Barbeiro;
import com.example.jefiro.barber.barbearia.Endereco;
import com.example.jefiro.barber.servicoPrestado.Servicos;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AgendamentoViewFragment extends Fragment {

    private FirebaseFirestore db;
    private LinearLayout containerAgendamentos;
    private String idCliente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_agendamento_view, container, false);

        db = FirebaseFirestore.getInstance();
        idCliente = FirebaseAuth.getInstance().getCurrentUser().getUid();
        containerAgendamentos = v.findViewById(R.id.containerAgendamentos);

        getAgendamentos();

        return v;
    }

    private void getAgendamentos() {
        db.collection("Agendamentos")
                .whereEqualTo("idCliente", idCliente)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful() || task.getResult().isEmpty()) return;

                    containerAgendamentos.removeAllViews();

                    for (DocumentSnapshot doc : task.getResult()) {

                        Agendamento ag = doc.toObject(Agendamento.class);

                        View item = getLayoutInflater().inflate(R.layout.item_agendamento, containerAgendamentos, false);

                        TextView tvNomeBarbearia = item.findViewById(R.id.tvNomeBarbearia);
                        TextView tvEndereco = item.findViewById(R.id.tvEndereco);
                        TextView tvStatus = item.findViewById(R.id.tvStatus);
                        TextView tvDataHora = item.findViewById(R.id.tvDataHora);
                        TextView tvServico = item.findViewById(R.id.tvServico);
                        TextView tvBarbeiro = item.findViewById(R.id.tvBarbeiro);
                        ShapeableImageView img = item.findViewById(R.id.imgBarbeariaAgendamento);

                        Button btnCancelar = item.findViewById(R.id.btnCancelar);
                        Button btnDetalhes = item.findViewById(R.id.btnDetalhes);


                        LocalDateTime data = LocalDateTime.ofInstant(ag.getData_agendada().toDate().toInstant(), ZoneId.systemDefault());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy 'às' HH:mm", new Locale("pt", "BR"));
                        String dataFormatada = data.format(formatter);

                        tvDataHora.setText(dataFormatada);

                        getBarbearia(ag.getIdBarbearia(), b -> {
                            tvNomeBarbearia.setText(b.getNome());

                            getEndereco(ag.getIdBarbearia(), e -> {
                                tvEndereco.setText(e.getRua() + ", " + e.getNumero());
                            });

                            Glide.with(this)
                                    .load(b.getFotoUrl())
                                    .circleCrop()
                                    .centerCrop()
                                    .into(img);

                        });

                        getServico(ag.getIdBarbearia(), ag.getIdServico(), s -> tvServico.setText(s.getNome()));

                        getBarbeiro(ag.getIdBarbearia(), ag.getIdBarbeiro(), br ->{
                            Log.d("DEBUG_SPINNER", "Barbeiro selecionado: " + ag.getIdBarbeiro());
                            Log.d("DEBUG_SPINNER", "Barbeiro selecionado: " + br);
                            tvBarbeiro.setText(br.getNome());
                        } );

                        btnCancelar.setOnClickListener(x -> cancelarAgendamento(doc.getId()));

                        btnDetalhes.setOnClickListener(x -> {
                        });

                        containerAgendamentos.addView(item);
                    }
                });
    }

    private void cancelarAgendamento(String id) {
        db.collection("Agendamentos").document(id)
                .update("status", "Cancelado")
                .addOnSuccessListener(x -> getAgendamentos());
    }

    private void getBarbearia(String id, OnResult<Barbearia> callback) {
        db.collection("Barbearias").document(id).get()
                .addOnSuccessListener(d -> callback.ok(d.toObject(Barbearia.class)));
    }

    private void getBarbeiro(String id, String idBarbeiro, OnResult<Barbeiro> callback) {
        db.collection("Barbearias")
                .document(id)
                .collection("Barbeiros")
                .document(idBarbeiro)
                .get()
                .addOnSuccessListener(d -> callback.ok(d.toObject(Barbeiro.class)));
    }

    private void getServico(String id, String idSevico, OnResult<Servicos> callback) {
        db.collection("Barbearias")
                .document(id)
                .collection("Servicos")
                .document(idSevico)
                .get()
                .addOnSuccessListener(d -> callback.ok(d.toObject(Servicos.class)));
    }

    private void getEndereco(String id, OnResult<Endereco> callback) {
        db.collection("Barbearias")
                .document(id)
                .collection("Enderecos")
                .get()
                .addOnSuccessListener(d -> callback.ok(d.getDocuments().get(0).toObject(Endereco.class)));
    }

    interface OnResult<T> {
        void ok(T t);
    }
}
