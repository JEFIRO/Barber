package com.example.jefiro.barber;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class HorarioFuncionamento extends AppCompatActivity {

    // 1. Mapeamento de todos os 14 campos (apenas 2 para exemplo, mas você faria para todos)
    private TextView SegundaAbertura, SegundaFechamento, TercaAbertura,
            TercaFechamento, QuartaAbertura, QuartaFechamento,
            QuintaAbertura, QuintaFechamento, SextaAbertura, SextaFechamento,
            SabadoAbertura, SabadoFechamento, DomingoAbertura, DomingoFechamento;


    // Mapa para facilitar a validação: Chave é o ID do TextView, Valor é o nome do dia.
    private Map<Integer, String> todosOsCamposHorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_funcionamento);

        SegundaAbertura = findViewById(R.id.segunda_abertura);
        SegundaFechamento = findViewById(R.id.segunda_fechamento);

        TercaAbertura = findViewById(R.id.terca_abertura);
        TercaFechamento = findViewById(R.id.terca_fechamento);

        QuartaAbertura = findViewById(R.id.quarta_abertura);
        QuartaFechamento = findViewById(R.id.quarta_fechamento);

        QuintaAbertura = findViewById(R.id.quinta_abertura);
        QuintaFechamento = findViewById(R.id.quinta_fechamento);

        SextaAbertura = findViewById(R.id.sexta_abertura);
        SextaFechamento = findViewById(R.id.sexta_fechamento);

        SabadoAbertura = findViewById(R.id.sabado_abertura);
        SabadoFechamento = findViewById(R.id.sabado_fechamento);

        DomingoAbertura = findViewById(R.id.domingo_abertura);
        DomingoFechamento = findViewById(R.id.domingo_fechamento);

        // ... inicializar todos os outros 12 TextViews

        // Preenche o mapa com TODOS os campos que precisam ser validados (RN-B04)
        todosOsCamposHorario = new HashMap<>();
        todosOsCamposHorario.put(R.id.segunda_abertura, "Segunda (Abertura)");
        todosOsCamposHorario.put(R.id.segunda_fechamento, "Segunda (Fechamento)");

        todosOsCamposHorario.put(R.id.terca_abertura, "Terça (Abertura)");
        todosOsCamposHorario.put(R.id.terca_fechamento, "Terça (Fechamento)");

        todosOsCamposHorario.put(R.id.quarta_abertura, "Quarta (Abertura)");
        todosOsCamposHorario.put(R.id.quarta_fechamento, "Quarta (Fechamento)");

        todosOsCamposHorario.put(R.id.quinta_abertura, "Quinta (Abertura)");
        todosOsCamposHorario.put(R.id.quinta_fechamento, "Quinta (Fechamento)");

        todosOsCamposHorario.put(R.id.sexta_abertura, "Sexta (Abertura)");
        todosOsCamposHorario.put(R.id.sexta_fechamento, "Sexta (Fechamento)");

        todosOsCamposHorario.put(R.id.sabado_abertura, "Sabado (Abertura)");
        todosOsCamposHorario.put(R.id.sabado_fechamento, "Sabado (Fechamento)");

        todosOsCamposHorario.put(R.id.domingo_abertura, "Domingo (Abertura)");
        todosOsCamposHorario.put(R.id.domingo_fechamento, "Domingo (Fechamento)");
        // ... adicionar os IDs dos TextViews de Terça, Quarta, ..., Domingo


        findViewById(R.id.salvar_todos).setOnClickListener(v -> {
            if (validarRN_B04()) {
                salvarHorarios();
            }
        });
    }

    // 2. Função para exibir o seletor de tempo
    public void showTimePicker(View view) {
        TextView tv = (TextView) view;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (timePicker, hourOfDay, minute) -> {
                    // Formata a hora para HH:MM (Ex: 08:05)
                    String horaFormatada = String.format("%02d:%02d", hourOfDay, minute);
                    tv.setText(horaFormatada);
                }, 8, 0, true); // Valores iniciais: 08:00, formato 24h

        timePickerDialog.show();
    }

    // 3. Função de Validação (Implementação da RN-B04)
    private boolean validarRN_B04() {
        for (Map.Entry<Integer, String> entry : todosOsCamposHorario.entrySet()) {
            TextView textView = findViewById(entry.getKey());
            // Verifica se o texto do TextView está vazio ou contém apenas espaços
            if (textView.getText().toString().trim().isEmpty()) {
                // RN-B04 violada: Exibe mensagem de erro para o usuário
                Toast.makeText(this,
                        "ERRO (RN-B04): O campo de horário para " + entry.getValue() + " é obrigatório.",
                        Toast.LENGTH_LONG).show();
                textView.setError("Horário obrigatório"); // Sinaliza o campo visualmente
                return false; // Falha na validação
            }
        }

        return true; // Todos os 14 campos (Abertura/Fechamento de 7 dias) foram preenchidos
    }

    private void salvarHorarios() {
        // Lógica para coletar e salvar os 14 valores (ex: em um banco de dados ou API)
        Toast.makeText(this, "Horários salvos com sucesso!", Toast.LENGTH_SHORT).show();
        // Exemplo: String horaSegundaAbertura = tvSegundaAbertura.getText().toString();
    }
}
