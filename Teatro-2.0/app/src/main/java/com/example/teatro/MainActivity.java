package com.example.teatro;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.mindrot.jbcrypt.BCrypt;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // Supabase credentials (academic project)
    private static final String SUPABASE_URL = "https://mhofxrmxsegjzssutzru.supabase.co";
    private static final String SUPABASE_KEY = "sb_secret_Kvssja_b25MU2zlsUAumsg_sV8pqwPQ";

    private TextView tvEstado;
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;

    private OkHttpClient client;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link XML views
        tvEstado = findViewById(R.id.tvEstado);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        client = new OkHttpClient();

        // Register button
        btnRegister.setOnClickListener(v -> {
            // Open the RegisterActivity page
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


        // Login button
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                tvEstado.setText("Completa todos los campos");
                return;
            }

            tvEstado.setText("Verificando...");
            loginUsuario(email, password);
        });
    }

    // ================= REGISTER =================

    private void registrarUsuario(String email, String password) {

        String url = SUPABASE_URL + "/rest/v1/usuarios";

        String hashedPassword=BCrypt.hashpw(password, BCrypt.gensalt());

        String json = "{ \"email\": \"" + email + "\", \"password\": \"" + hashedPassword + "\" }";

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                actualizarTexto("Error conexión: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    actualizarTexto("Usuario registrado correctamente");
                } else {
                    actualizarTexto("Error registro: " + response.code());
                }
            }
        });
    }

    // ================= LOGIN =================
    private void loginUsuario(String email, String password) {
        String url;
        try {
            String emailEncoded = URLEncoder.encode(email, "UTF-8");
            url = SUPABASE_URL + "/rest/v1/usuarios?email=eq." + emailEncoded;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            actualizarTexto("Error codificando email");
            return;
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LOGIN_RESPONSE", "Connection error: " + e.getMessage());
                actualizarTexto("Error conexión: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bodyText = response.body() != null ? response.body().string() : "";
                Log.d("LOGIN_RESPONSE", "Body: " + bodyText);

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            JSONArray array = new JSONArray(bodyText);
                            if (array.length() > 0) {
                                String storedPassword = array.getJSONObject(0).getString("password");
                                if (BCrypt.checkpw(password, storedPassword)) {
                                    tvEstado.setText("Login correcto ✅");
                                } else {
                                    tvEstado.setText("Contraseña incorrecta ❌");
                                }
                            } else {
                                tvEstado.setText("Usuario no encontrado ❌");
                            }
                        } catch (JSONException e) {
                            tvEstado.setText("Error procesando datos");
                            e.printStackTrace();
                        }
                    } else {
                        tvEstado.setText("Error login: " + response.code());
                    }
                });
            }
        });
    }


    // Update UI safely
    private void actualizarTexto(String texto) {
        runOnUiThread(() -> tvEstado.setText(texto));
    }
}
