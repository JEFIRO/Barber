package com.example.jefiro.barber.auth;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jefiro.barber.MainActivity;
import com.example.jefiro.barber.R;
import com.example.jefiro.barber.home.HomePage;
import com.example.jefiro.barber.model.Cliente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class CadastroCliente extends AppCompatActivity {
    private EditText nomeCliente;
    private EditText emailCliente;
    private EditText senhaCliente;
    private EditText telefoneCliente;
    private TextView infoView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CadastroCliente), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nomeCliente = findViewById(R.id.inputNome);
        emailCliente = findViewById(R.id.inputEmail);
        senhaCliente = findViewById(R.id.inputSenha);
        telefoneCliente = findViewById(R.id.inputTelefone);
        infoView = findViewById(R.id.infoView);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

    }

    public void criarAuthCliente(Cliente cliente) {
        mAuth.createUserWithEmailAndPassword(cliente.getEmail(), cliente.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            db.collection("Clientes")
                                    .document(user.getUid())
                                    .set(cliente)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(CadastroCliente.this, "Cliente salvo e autenticado!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(CadastroCliente.this, "Erro ao salvar no Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                            updateUI(user);
                        } else {

                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(CadastroCliente.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }



    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void salvarCliente(View v) {

        String nome = nomeCliente.getText().toString();
        String email = emailCliente.getText().toString();
        String senha = senhaCliente.getText().toString();
        String telefone = telefoneCliente.getText().toString();

        if (nome.isEmpty()) {
            nomeCliente.setHintTextColor(Color.RED);
            nomeCliente.setHint("Nome não pode se vazio.");
        }
        if (email.isEmpty()) {
            emailCliente.setHintTextColor(Color.RED);
            emailCliente.setHint("Email não pode se vazio.");
        }
        if (senha.isEmpty()) {
            senhaCliente.setHintTextColor(Color.RED);
            senhaCliente.setHint("Senha não pode se vazio.");
        }
        if (senha.length() <= 7) {
            senhaCliente.setHintTextColor(Color.RED);
            senhaCliente.setHint("sua senha deve ter 8 ou mais caracteres");
        }
        if (telefone.isEmpty()) {
            telefoneCliente.setHintTextColor(Color.RED);
            telefoneCliente.setHint("Telefone não pode se vazio.");
        }

        Cliente cliente = new Cliente(nome, telefone, email, senha, null);

        criarAuthCliente(cliente);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            Toast.makeText(this, "Bem-vindo, " + user.getEmail(), Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
            finish();
        } else {

            Toast.makeText(this, "Falha na autenticação!", Toast.LENGTH_SHORT).show();
        }
    }


}