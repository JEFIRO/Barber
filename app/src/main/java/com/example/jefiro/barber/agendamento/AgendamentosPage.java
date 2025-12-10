package com.example.jefiro.barber.agendamento;

import static com.example.jefiro.barber.barbearia.BarbeariaDetails.capitalizarTitulo;
import static com.example.jefiro.barber.barbearia.BarbeariaDetails.formatarDuracao;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jefiro.barber.R;
import com.example.jefiro.barber.barbearia.Barbearia;
import com.example.jefiro.barber.barbearia.Barbeiro;
import com.example.jefiro.barber.barbearia.BarbeiroAdapter;
import com.example.jefiro.barber.horario.Horario;
import com.example.jefiro.barber.horario.Periods;
import com.example.jefiro.barber.repository.FirestoreRepository;
import com.example.jefiro.barber.repository.OnCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AgendamentosPage extends AppCompatActivity {

    private FirestoreRepository<Barbearia> dbBarbearia;
    private FirestoreRepository<Agendamento> dbAgendamento;
    private FirebaseFirestore db;
    private LinearLayout containerSevicoAgendamento;
    private Button btnAgendarHorario;
    private LinearLayout containerHorarios;
    private DatePicker datePicker;
    private Spinner spinnerBarbeiros;
    private LocalTime horarioSelecionado = null;
    private LocalDate dataSelecionadaGlobal = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agendamentos_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbBarbearia = new FirestoreRepository<Barbearia>();
        dbAgendamento = new FirestoreRepository<Agendamento>();
        db = FirebaseFirestore.getInstance();

        containerSevicoAgendamento = findViewById(R.id.containerSevicoAgendamento);
        containerHorarios = findViewById(R.id.containerHorarios);
        datePicker = findViewById(R.id.dataPicke);
        spinnerBarbeiros = findViewById(R.id.spinnerBarbeiros);
        btnAgendarHorario = findViewById(R.id.btnAgendarHorario);
        String idServico = getIntent().getStringExtra("idServico");
        String idBarbearia = getIntent().getStringExtra("idBarbearia");

        getServico(idBarbearia, idServico);
        setValuesOnSpiner(idBarbearia);

        final Barbeiro[] barbeiroSelecionado = {null};

        spinnerBarbeiros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                barbeiroSelecionado[0] = (Barbeiro) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, 1);
        datePicker.setMaxDate(lastDate.getTimeInMillis());

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), (view, year, month, day) -> {
            containerHorarios.removeAllViews();
            if (barbeiroSelecionado[0] == null) {
                Toast.makeText(this, "Selecione um barbeiro", Toast.LENGTH_SHORT).show();
                return;
            }

            int mes = month + 1;
            String dataStr = day + "/" + mes + "/" + year;

            LocalDate dataSelecionada = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            DayOfWeek diaSemana = dataSelecionada.getDayOfWeek();
            String dia = removeAcento(diaSemana.getDisplayName(TextStyle.FULL, new Locale("pt", "BR"))).toUpperCase().replaceAll("-FEIRA", "");

            getHorario(idBarbearia, new OnCallback() {
                @Override
                public void onSuccess(Object result) {
                    List<Horario> horarios = (List<Horario>) result;

                    List<Horario> diaSelecionado = new ArrayList<>();

                    for (Horario h : horarios) {
                        if (h.getDiaSemana().toString().equalsIgnoreCase(dia)) {
                            diaSelecionado.add(h);
                        }
                    }


                    if (diaSelecionado.isEmpty() || diaSelecionado.get(0).getClosed()) {
                        containerHorarios.removeAllViews();
                        Toast.makeText(getApplicationContext(), "Barbearia fechada neste dia!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int duracao = 30;
                    List<LocalTime> horariosGerados = gerarHorariosDoDia(diaSelecionado, duracao);

                    getAgenda(idBarbearia, barbeiroSelecionado[0].getId(), new OnCallback() {
                        @Override
                        public void onSuccess(Object result) {

                            List<Agendamento> agenda = (List<Agendamento>) result;

                            List<Agendamento> agendaDia = new ArrayList<>();

                            for (Agendamento a : agenda) {
                                LocalDate dataAgendada = a.getData_agendada()
                                        .toDate()
                                        .toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();

                                if (dataAgendada.equals(dataSelecionada)) {
                                    agendaDia.add(a);
                                }
                            }

                            List<LocalTime> livres = filtrarOcupados(horariosGerados, agendaDia);

                            exibirGrade(livres, dataSelecionada);


                        }

                        @Override
                        public void onFailure(String e) {
                            Toast.makeText(getApplicationContext(), "Erro ao buscar agenda", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(String e) {
                    Toast.makeText(getApplicationContext(), "Erro ao buscar horários", Toast.LENGTH_SHORT).show();
                }
            });

        });


        btnAgendarHorario.setOnClickListener(c -> {
            LocalDateTime dataHora = LocalDateTime.of(dataSelecionadaGlobal, horarioSelecionado);

            long millis = dataHora.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            Timestamp agendamentoTimeStamp = new Timestamp(new Date(millis));

            Agendamento agendamento = new Agendamento(idBarbearia, barbeiroSelecionado[0].getId(), idServico, FirebaseAuth.getInstance().getUid(), agendamentoTimeStamp);

            agendarHorario(agendamento);
        });
    }

    private void getServico(String idBarbearia, String id) {
        dbBarbearia.getSubDocument("Barbearias", idBarbearia, "Servicos", id, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    containerSevicoAgendamento.removeAllViews();
                    View v = getLayoutInflater().inflate(R.layout.item_servico, containerSevicoAgendamento, false);

                    var duracao = doc.getString("duracao");
                    var nome = doc.getString("nome");
                    var preco = doc.getDouble("preco");

                    ImageView cardImage = v.findViewById(R.id.cardImage);
                    TextView cardTitle = v.findViewById(R.id.cardTitle);
                    TextView cardPreco = v.findViewById(R.id.cardPreco);
                    TextView cardDuracao = v.findViewById(R.id.cardDuracao);

                    v.findViewById(R.id.btnAgendar).setOnClickListener(l -> {
                        Intent intent = new Intent(getApplicationContext(), AgendamentosPage.class);
                        intent.putExtra("idServico", doc.getId());
                        intent.putExtra("idBarbearia", idBarbearia);
                        startActivity(intent);
                    });

                    if (nome.toLowerCase().contains("cabelo e barba")) {
                        cardImage.setImageResource(R.drawable.ic_cabelo_barba);
                    } else if (nome.toLowerCase().contains("cabelo")) {
                        cardImage.setImageResource(R.drawable.ic_hair_cut);
                    } else if (nome.toLowerCase().contains("barba")) {
                        cardImage.setImageResource(R.drawable.ic_barba);
                    } else {
                        cardImage.setImageResource(R.drawable.ic_hair_cut);
                    }


                    String precoFormatado = String.format(Locale.getDefault(), "R$ %.2f", preco);
                    cardPreco.setText(precoFormatado);
                    cardTitle.setText(capitalizarTitulo(nome));
                    cardDuracao.setText(formatarDuracao(duracao));

                    containerSevicoAgendamento.addView(v);
                }
            } else {
                return;
            }
        });
    }

    private void setValuesOnSpiner(String idBarbearia) {
        dbBarbearia.getSubDocument("Barbearias", idBarbearia, "Barbeiros",
                task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        if (!docs.isEmpty()) {
                            List<Barbeiro> lista = new ArrayList<>();

                            for (DocumentSnapshot doc : docs) {
                                Barbeiro b = doc.toObject(Barbeiro.class);
                                lista.add(b);
                            }
                            BarbeiroAdapter adapter = new BarbeiroAdapter(getApplicationContext(), lista);
                            spinnerBarbeiros.setAdapter(adapter);
                        }
                    } else return;
                });
    }

    private void getAgenda(String idBarbearia, String idBarbeiro,
                           OnCallback<List<Agendamento>> callback) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Barbearias")
                .document(idBarbearia)
                .collection("Barbeiros")
                .document(idBarbeiro)
                .collection("Agenda")
                .get()
                .addOnSuccessListener(result -> {
                    List<Agendamento> lista = new ArrayList<>();

                    for (DocumentSnapshot snap : result.getDocuments()) {
                        Agendamento a = snap.toObject(Agendamento.class);
                        lista.add(a);
                    }

                    callback.onSuccess(lista);
                })
                .addOnFailureListener(e -> callback.onFailure("Erro ao buscar agenda: " + e.getMessage()));
    }


    private void getHorario(String barbearia, OnCallback callback) {
        dbBarbearia.getSubDocument("Barbearias", barbearia, "Horarios_Funcionamento",
                task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        if (!docs.isEmpty()) {
                            List<Horario> horarioFuncionamentoList = new ArrayList<>();
                            docs.forEach(doc -> {
                                if (doc.exists()) {
                                    horarioFuncionamentoList.add(doc.toObject(Horario.class));
                                }
                            });
                            callback.onSuccess(horarioFuncionamentoList);
                        } else callback.onFailure("Sem horarios");
                    } else callback.onFailure("Erro na task");
                });
    }

    private String removeAcento(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    private List<LocalTime> gerarHorariosDoDia(List<Horario> horariosFun, int duracaoMinutos) {
        List<LocalTime> lista = new ArrayList<>();

        for (Horario h : horariosFun) {
            for (Periods p : h.getPeriods()) {
                LocalTime inicio = LocalTime.parse(p.getOpen());
                LocalTime fim = LocalTime.parse(p.getClose());

                LocalTime atual = inicio;

                while (!atual.plusMinutes(duracaoMinutos).isAfter(fim)) {
                    lista.add(atual);
                    atual = atual.plusMinutes(duracaoMinutos);
                }
            }
        }
        return lista;
    }

    private List<LocalTime> filtrarOcupados(List<LocalTime> gerados, List<Agendamento> agendaDia) {
        List<LocalTime> livres = new ArrayList<>();

        for (LocalTime h : gerados) {
            boolean ocupado = agendaDia.stream().anyMatch(a -> {
                LocalDateTime dt = a.getData_agendada().toDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();
                return dt.toLocalTime().equals(h);
            });

            if (!ocupado) livres.add(h);
        }
        return livres;
    }

    private void exibirGrade(List<LocalTime> horariosLivre, LocalDate dataSelecionada) {
        containerHorarios.removeAllViews();

        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        for (LocalTime h : horariosLivre) {

            if (datePicker.getDayOfMonth() == hoje.getDayOfMonth() &&
                    datePicker.getMonth() == hoje.getMonthValue() - 1 &&
                    datePicker.getYear() == hoje.getYear() &&
                    h.isBefore(agora))
                continue;

            TextView item = new TextView(this);
            item.setText(h.format(DateTimeFormatter.ofPattern("HH:mm")));
            item.setPadding(26, 22, 26, 22);
            item.setTextSize(18f);
            item.setBackgroundResource(R.drawable.bg_horario_button);

            item.setOnClickListener(v -> {
                horarioSelecionado = h;
                dataSelecionadaGlobal = dataSelecionada;
                Toast.makeText(this, "Selecionado: " + h, Toast.LENGTH_SHORT).show();
            });

            containerHorarios.addView(item);
        }
    }

    private void agendarHorario(Agendamento agendamento) {
        DocumentReference refGlobal = db.collection("Agendamentos").document();
        agendamento.setId(refGlobal.getId());

        refGlobal.set(agendamento)
                .addOnSuccessListener(v -> {
                    db.collection("Barbearias")
                            .document(agendamento.getIdBarbearia())
                            .collection("Barbeiros")
                            .document(agendamento.getIdBarbeiro())
                            .collection("Agenda")
                            .document(refGlobal.getId())
                            .set(agendamento)
                            .addOnSuccessListener(e -> {
                                Toast.makeText(getApplicationContext(),
                                        "Agendamento confirmado!",
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(err ->
                        Toast.makeText(getApplicationContext(),
                                "Erro ao salvar: " + err.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

}