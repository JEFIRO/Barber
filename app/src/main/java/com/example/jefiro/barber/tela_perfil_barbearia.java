package com.example.jefiro.barber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class tela_perfil_barbearia extends AppCompatActivity {

    private EditText rua, cidade, cep, bairro, numero, estado;

    private TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_perfil_barbearia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //teste
        texto = findViewById(R.id.sucesso);
        //
        rua = findViewById(R.id.rua);
        cidade = findViewById(R.id.cidade);
        cep = findViewById(R.id.cep);
        bairro = findViewById(R.id.bairro);
        numero = findViewById(R.id.numero);
        estado = findViewById(R.id.Estado);





    }
    public void salvar(View v){
        //EditTexts
        String street = rua.getText().toString();
        String city = cidade.getText().toString();
        String cep1 = cep.getText().toString();
        String district = bairro.getText().toString();
        String number = numero.getText().toString();
        String state = estado.getText().toString();

        if (state.isEmpty() || number.isEmpty() || district.isEmpty()
                || cep1.isEmpty() || city.isEmpty() || street.isEmpty()){

            //popup com erro

            //teste
            texto.setText("Algo errado");

        }else {
            //teste
            texto.setText("Certo");
        }

    }
    public void horario(View v){
        AlertDialog popup = new AlertDialog.Builder(tela_perfil_barbearia.this).create();
        popup.setTitle("Voce irá para a tela de Horarios");
        popup.setButton(DialogInterface.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getApplicationContext(), HorarioFuncionamento.class);
                startActivity(i);
            }
        });
        popup.show();
    }
}