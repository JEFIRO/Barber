package com.example.jefiro.barber;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Detalhes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        ImageView img = findViewById(R.id.imagem);
        TextView nome = findViewById(R.id.nome);
        TextView endereco = findViewById(R.id.endereco);
        TextView servicos = findViewById(R.id.servicos);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            nome.setText(extras.getString("nome"));
            endereco.setText("Endereço: " + extras.getString("endereco"));
            servicos.setText("Serviços: " + extras.getString("servicos"));

            int imagemId = extras.getInt("imagem", R.drawable.barbearia1);
            img.setImageResource(imagemId);
        }
    }
}

