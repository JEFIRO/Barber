package com.example.jefiro.barber.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.jefiro.barber.R;
import com.example.jefiro.barber.auth.SelectOptionAuth;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser cliente;

    private ShapeableImageView img;
    private EditText nomeInput;
    private EditText emailInput;
    private EditText telefoneInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        cliente = mAuth.getCurrentUser();


        img = view.findViewById(R.id.imgProfile);
        nomeInput = view.findViewById(R.id.inputNome);
        emailInput = view.findViewById(R.id.inputEmail);
        telefoneInput = view.findViewById(R.id.inputTelefone);


        view.findViewById(R.id.bntAtualizar).setOnClickListener(this::atualizar);
        view.findViewById(R.id.bntSair).setOnClickListener(this::signOut);

        if (cliente != null) {
            buscarUser();
        } else {
            Log.d("PERFIL_LOG", "Usuário não logado. Redirecionamento deve ser feito na Activity hospedeira.");
        }
    }


    private void buscarUser() {
        if (cliente == null) return;

        Log.d("PERFIL_LOG", "Iniciando busca do usuário por UID: " + cliente.getUid());


        db.collection("Clientes").document(cliente.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("PERFIL_LOG", "Documento encontrado com sucesso. Setando campos.");


                        String nome = documentSnapshot.getString("nome");
                        String email = documentSnapshot.getString("email");
                        String telefone = documentSnapshot.getString("telefone");
                        String fotoUrl = documentSnapshot.getString("fotoUrl");


                        nomeInput.setText(nome);
                        emailInput.setText(email);
                        telefoneInput.setText(telefone);

                        setarFoto(fotoUrl);

                    } else {
                        Log.d("PERFIL_LOG", "Nenhum documento encontrado para este UID.");
                        setarFoto(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PERFIL_LOG", "Erro ao buscar documento", e);
                    Toast.makeText(getContext(), "Erro ao carregar perfil: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setarFoto(String fotoUrl) {

        if (fotoUrl == null || fotoUrl.isEmpty()) {
            fotoUrl = "https://media.istockphoto.com/id/1495088043/pt/vetorial/user-profile-icon-ava"
                    + "tar-ou-person-icon-profile-picture-portrait-symbol-default-portrait.jpg?s=612x612&w=0&k=20&c=S7d8ImMSfoLBMCaEJOffTVua003OAl2xUnzOsuKIwek=";
        }


        if (getContext() != null) {
            Glide.with(getContext())
                    .load(fotoUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(img);
        }
    }

    private void signOut(View v) {
        if (mAuth != null && getActivity() != null) {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Deslogado com sucesso!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), SelectOptionAuth.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

            getActivity().finish();
        }
    }

    public void atualizar(View v) {
        if (cliente == null || getContext() == null) return;


        String novoNome = nomeInput.getText().toString();
        String novoTelefone = telefoneInput.getText().toString();

        Map<String, Object> atualizacao = new HashMap<>();
        atualizacao.put("nome", novoNome);
        atualizacao.put("telefone", novoTelefone);

        db.collection("Clientes").document(cliente.getUid())
                .update(atualizacao)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao atualizar perfil: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}