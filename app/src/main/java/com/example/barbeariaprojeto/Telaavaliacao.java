package com.example.barbeariaprojeto;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Telaavaliacao extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText edtComentario;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telaavaliacao);

        ratingBar = findViewById(R.id.ratingBar);
        edtComentario = findViewById(R.id.edtComentario);
        btnEnviar = findViewById(R.id.btnEnviar);

        int agendamentoId = getIntent().getIntExtra("agendamentoId", -1);
        int clienteId = getIntent().getIntExtra("clienteId", -1);

        btnEnviar.setOnClickListener(v -> {

            float estrelas = ratingBar.getRating();
            String comentario = edtComentario.getText().toString();

            Toast.makeText(this,
                    "Avaliação enviada!\nEstrelas: " + estrelas +
                            "\nComentário: " + comentario,
                    Toast.LENGTH_LONG).show();

            finish();
        });
    }
}
