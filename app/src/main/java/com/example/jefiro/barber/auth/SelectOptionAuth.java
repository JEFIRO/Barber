package com.example.jefiro.barber.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

public class SelectOptionAuth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_option_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            Toast.makeText(this, "Bem-vindo de volta, " + user.getEmail(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomePage.class));
            finish();
        }
    }


    public void chamarCadastro(View v) {
        Intent intent = new Intent(this, CadastroCliente.class);
        startActivity(intent);
    }

    public void chamarLogin(View v){
        Intent intent = new Intent(this, LoginCliente.class);
        startActivity(intent);
    }


}