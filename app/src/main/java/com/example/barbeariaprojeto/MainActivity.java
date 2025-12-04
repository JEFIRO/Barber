package com.example.barbeariaprojeto;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private EditText procurar;
    private LinearLayout listaB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        procurar = findViewById(R.id.procurarB);
        listaB = findViewById(R.id.listaB);



        Button ver1 = findViewById(R.id.verD);     // botão do primeiro card
        Button ver2 = findViewById(R.id.ButaoD);   // botão do segundo card

        ver1.setOnClickListener(v -> abrirDetalhes("João Barbearia", "Av. Getúlio Vargas 171"));
        ver2.setOnClickListener(v -> abrirDetalhes("Zé Barbearia", "Av. Franco Fraga Maia"));


        //FILTRAGEM
        procurar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filtrarBarbearias(s.toString());
            }
        });
    }


     //Função que abre outra activity com os detalhes
    private void abrirDetalhes(String nome, String endereco) {
        Intent i = new Intent(MainActivity.this, Detalhes.class);
        i.putExtra("nome", nome);
        i.putExtra("endereco", endereco);
        startActivity(i);
    }


    //  Função de filtro dos cards

    private void filtrarBarbearias(String texto) {
        texto = texto.toLowerCase();

        for (int i = 0; i < listaB.getChildCount(); i++) {

            View item = listaB.getChildAt(i);

            if (item instanceof CardView) {

                LinearLayout layout = (LinearLayout) ((CardView) item).getChildAt(0);
                TextView nomeB = (TextView) layout.getChildAt(0);

                String nome = nomeB.getText().toString().toLowerCase();

                if (nome.contains(texto)) {
                    item.setVisibility(View.VISIBLE);
                } else {
                    item.setVisibility(View.GONE);
                }
            }
        }
    }
}
