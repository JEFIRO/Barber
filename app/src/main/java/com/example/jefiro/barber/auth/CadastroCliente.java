package com.example.jefiro.barber.auth;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CadastroCliente extends AppCompatActivity {
    private EditText nomeCliente;
    private EditText emailCliente;
    private EditText senhaCliente;
    private EditText telefoneCliente;
    private ImageView imageView;
    private final String SUPABASE_URL = "https://iuzrpmyfklrvfpndmwbk.supabase.co";
    private final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml1enJwbXlma2xydmZwbmRtd2JrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM4MzMyNzgsImV4cCI6MjA3OTQwOTI3OH0.EiSfjlbSL881vznX193OCrG9ouI_2BdaBslYh2CM9GY";
    private final String BUCKET_NAME = "ClientesProfileImage";

    private String urlImage;

    private Uri uri;
    private TextView infoView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;

    private ActivityResultLauncher<String> selecionarImagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.CadastroCliente), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nomeCliente = findViewById(R.id.inputNome);
        emailCliente = findViewById(R.id.inputEmail);
        senhaCliente = findViewById(R.id.inputSenha);
        telefoneCliente = findViewById(R.id.inputTelefone);
        imageView = findViewById(R.id.imageView);
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        selecionarImagem = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        this.uri = uri;
                        imageView.setImageURI(uri);
                    }
                }
        );

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void criarAuthCliente(Cliente cliente) {
        mAuth.createUserWithEmailAndPassword(cliente.getEmail(), cliente.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            db.collection("Clientes")
                                    .document(user.getUid())
                                    .set(cliente)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(CadastroCliente.this, "Cliente salvo e autenticado!", LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        deleteImageFromSupabase();
                                        Toast.makeText(CadastroCliente.this, "Erro ao salvar no Firestore: " + e.getMessage(), LENGTH_SHORT).show();
                                    });
                            updateUI(user);
                        } else {

                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(CadastroCliente.this, "Authentication failed.",
                                    LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void escolherFoto(View v) {
        selecionarImagem.launch("image/*");
    }

    public void salvarCliente(View v) {

        String nome = nomeCliente.getText().toString();
        String email = emailCliente.getText().toString();
        String senha = senhaCliente.getText().toString();
        String telefone = telefoneCliente.getText().toString();

        if (nome.isEmpty()) {
            nomeCliente.setHintTextColor(Color.RED);
            nomeCliente.setHint("Nome não pode se vazio.");
        }
        if (email.isEmpty()) {
            emailCliente.setHintTextColor(Color.RED);
            emailCliente.setHint("Email não pode se vazio.");
        }
        if (senha.isEmpty()) {
            senhaCliente.setHintTextColor(Color.RED);
            senhaCliente.setHint("Senha não pode se vazio.");
        }
        if (senha.length() <= 7) {
            senhaCliente.setHintTextColor(Color.RED);
            senhaCliente.setHint("sua senha deve ter 8 ou mais caracteres");
        }
        if (telefone.isEmpty()) {
            telefoneCliente.setHintTextColor(Color.RED);
            telefoneCliente.setHint("Telefone não pode se vazio.");
        }
        if (telefone.length() <= 10) {
            telefoneCliente.setHintTextColor(Color.RED);
            telefoneCliente.setHint("o Telefone tem que conter 11 digitos (xx)xxxxx-xxxx");
        }

        if (uri == null) {
            Toast.makeText(getApplicationContext(), "Selecione uma imagem", LENGTH_SHORT).show();
            return;
        } else {
            urlImage = uploadImageToSupabase();
        }


        Cliente cliente = new Cliente(nome, telefone, email, senha, urlImage);

        criarAuthCliente(cliente);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            Toast.makeText(this, "Bem-vindo, " + user.getEmail(), LENGTH_SHORT).show();


            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
            finish();
        } else {

            Toast.makeText(this, "Falha na autenticação!", LENGTH_SHORT).show();
        }
    }

    private String uploadImageToSupabase() {

        Log.d("SUPABASE", "Iniciando upload...");

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] imageBytes = toBytes(inputStream);

            inputStream.read(imageBytes);

            Log.d("SUPABASE", "Bytes da imagem lidos: " + imageBytes.length);


            String url = "https://iuzrpmyfklrvfpndmwbk.supabase.co/storage/v1/object/ClientesProfileImage/"
                    + System.currentTimeMillis() + ".jpg";

            Log.d("SUPABASE", "URL destino: " + url);


            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml1enJwbXlma2xydmZwbmRtd2JrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM4MzMyNzgsImV4cCI6MjA3OTQwOTI3OH0.EiSfjlbSL881vznX193OCrG9ouI_2BdaBslYh2CM9GY")
                    .addHeader("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml1enJwbXlma2xydmZwbmRtd2JrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM4MzMyNzgsImV4cCI6MjA3OTQwOTI3OH0.EiSfjlbSL881vznX193OCrG9ouI_2BdaBslYh2CM9GY")
                    .put(body)
                    .build();

            Log.d("SUPABASE", "Enviando requisição...");

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("SUPABASE", "Erro no upload: " + e.getMessage(), e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("SUPABASE", "Código de resposta: " + response.code());
                    Log.d("SUPABASE", "Resposta completa: " + response.body().string());
                }

            });

            return url;
        } catch (Exception e) {
            Log.e("SUPABASE", "Erro ao preparar imagem: " + e.getMessage(), e);
        }
        return null;
    }


    private byte[] toBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }


    public void deleteImageFromSupabase() {

        new Thread(() -> {
            try {
                URL url = new URL(urlImage);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("apikey", SUPABASE_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);

                int responseCode = conn.getResponseCode();

                Log.d("SUPABASE_DELETE", "Código de resposta: " + responseCode);

                if (responseCode == 200 || responseCode == 204) {
                    Log.d("SUPABASE_DELETE", "Imagem deletada com sucesso!");
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) sb.append(line);

                    Log.e("SUPABASE_DELETE", "Erro ao deletar: ");
                }

                conn.disconnect();

            } catch (Exception e) {
                Log.e("SUPABASE_DELETE", "Exceção: " + e.getMessage());
            }
        }).start();
    }


}