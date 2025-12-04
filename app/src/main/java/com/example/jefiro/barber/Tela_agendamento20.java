package com.example.jefiro.barber;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Tela_agendamento20 extends AppCompatActivity {

    private Spinner barbeiro, servico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_agendamento20);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        servico = findViewById(R.id.spinner_servico);
        // configurção do spinner dos sevicos
        //Necessario para funcionar o Spinner
        servico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(Tela_agendamento20.this, "Item selecionado" + item , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
        ArrayList<String> ArrayServico = new ArrayList<>();
        // adicionar os servicos
        ArrayServico.add("Cabelo");
        ArrayServico.add("Barba");
        ArrayServico.add("Cabelo e barba");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ArrayServico);

        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        servico.setAdapter(adapter);
        barbeiro = findViewById(R.id.spinner_barbeiro);
        // configurção do spinner dos barbeiros
        //Necessario para funcionar o Spinner
        barbeiro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(Tela_agendamento20.this, "Item selecionado" + item , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
        ArrayList<String> ArrayBarbeiro = new ArrayList<>();
        // adicionar os nomes dos barbeiros
        ArrayBarbeiro.add("Pedro");
        ArrayBarbeiro.add("joao");
        ArrayBarbeiro.add("Altino");
        ArrayAdapter<String> sadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ArrayBarbeiro);

        sadapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        barbeiro.setAdapter(sadapter);
    }

}