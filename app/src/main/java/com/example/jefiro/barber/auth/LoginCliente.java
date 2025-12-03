package com.example.jefiro.barber.auth;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jefiro.barber.MainActivity;
import com.example.jefiro.barber.R;
import com.example.jefiro.barber.home.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginCliente extends AppCompatActivity {
    private EditText email;
    private CheckBox exibirSenha;
    private EditText senha;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.inputEmail);
        senha = findViewById(R.id.inputSenha);
        exibirSenha = findViewById(R.id.checkExibirSenha);

        mAuth = FirebaseAuth.getInstance();

    }

    public void login(View v) {
        String emailTxt = email.getText().toString();
        String senhaTxt = senha.getText().toString();

        if (emailTxt.isEmpty()) {
            email.setHint("email não pode ser vazio");
            email.setHintTextColor(Color.RED);
        }
        if (senhaTxt.isEmpty()) {
            senha.setHintTextColor(Color.RED);
            senha.setHint("Senha não pode ser vazio");
        }
        buscarCliente(emailTxt.trim(), senhaTxt.trim());
    }

    private void buscarCliente(String email, String senha) {
        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginCliente.this, "Ben-vindo" + user.getEmail(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginCliente.this, HomePage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginCliente.this, "Falha no login: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void exibirSenha(View v) {
        if (exibirSenha.isChecked()) {
            senha.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        senha.setSelection(senha.getText().length());
    }

}