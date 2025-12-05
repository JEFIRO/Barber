package com.example.jefiro.barber.auth;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import static com.example.jefiro.barber.service.SupaBase.deleteImg;
import static com.example.jefiro.barber.service.SupaBase.uploadImageToSupabase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jefiro.barber.R;
import com.example.jefiro.barber.home.HomePage;
import com.example.jefiro.barber.model.Cliente;
import com.example.jefiro.barber.repository.FirestoreRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class CadastroCliente extends AppCompatActivity {
    private TextInputEditText nomeCliente;
    private TextInputEditText emailCliente;
    private TextInputEditText senhaCliente;
    private TextInputEditText telefoneCliente;
    private TextInputEditText senhaConfirmaCliente;
    private ImageView imageView;
    private final String CLIENTES = "Clientes";
    private String urlImage;
    private Uri uri;
    private FirebaseAuth mAuth;
    private FirestoreRepository<Cliente> repository;
    private ActivityResultLauncher<String> selecionarImagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_cliente);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CadastroCliente), (v, insets) -> {
            selecionarImagem = registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            this.uri = uri;
                            imageView.setImageURI(uri);
                        }
                    }
            );
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nomeCliente = findViewById(R.id.edtClienteNome);
        emailCliente = findViewById(R.id.edtClienteEmail);
        senhaCliente = findViewById(R.id.edtClienteSenha);
        senhaConfirmaCliente = findViewById(R.id.edtClienteConfirmaSenha);
        telefoneCliente = findViewById(R.id.edtClienteTelefone);
        imageView = findViewById(R.id.edtClienteImg);

        repository = new FirestoreRepository<Cliente>();

        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    public void criarAuthCliente(Cliente cliente) {

        mAuth.createUserWithEmailAndPassword(cliente.getEmail(), cliente.getSenha())
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        repository.create(CLIENTES, uid, cliente, response -> {

                            if (response.isSuccessful()) {
                                Toast.makeText(this, "Cliente cadastrado com sucesso!", LENGTH_SHORT).show();
                                updateUI();
                            } else {
                                deleteImg(urlImage);
                                Toast.makeText(this, "Erro ao salvar no banco!", LENGTH_SHORT).show();
                                Log.e(TAG, "Erro Firestore: " + response.getException());
                            }
                        });

                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            makeToast("Senha fraca! Use no mínimo 6 caracteres.");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            makeToast("E-mail inválido! Corrija e tente novamente.");
                        } catch (FirebaseAuthUserCollisionException e) {
                            makeToast("Este e-mail já está cadastrado.");
                        } catch (FirebaseNetworkException e) {
                            makeToast("Sem conexão com a internet.");
                        } catch (Exception e) {
                            makeToast("Erro ao criar usuário");
                        }
                    }
                });
    }

    private void makeToast(String texto) {
        Toast.makeText(getApplicationContext(), texto, LENGTH_SHORT).show();
    }

    public void escolherFoto(View v) {
        selecionarImagem.launch("image/*");
    }

    public void salvarCliente(View v) {

        String nome = nomeCliente.getText().toString();
        String email = emailCliente.getText().toString();
        String senha = senhaCliente.getText().toString();
        String senhaConfirma = senhaConfirmaCliente.getText().toString();
        String telefone = telefoneCliente.getText().toString();
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || senhaConfirma.isEmpty() || telefone.isEmpty()) {
            makeToast("Preencha todos os campos");
        }

        if (senha.equals(senhaConfirma)) {
            makeToast("As senhas não conferem");
        }

        if (uri == null) {
            Toast.makeText(getApplicationContext(), "Selecione uma imagem", LENGTH_SHORT).show();
            return;
        } else {
            urlImage = uploadImageToSupabase(getApplicationContext(), uri);
        }

        Cliente cliente = new Cliente(nome, telefone, email, senha, urlImage);

        criarAuthCliente(cliente);
    }

    private void updateUI() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            Toast.makeText(this, "Bem-vindo, " + user.getEmail(), LENGTH_SHORT).show();

            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
            finish();
        } else {

            Toast.makeText(this, "Falha na autenticação!", LENGTH_SHORT).show();
        }
    }
}