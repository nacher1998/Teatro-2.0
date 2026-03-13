package com.example.teatro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://mhofxrmxsegjzssutzru.supabase.co";
    private static final String SUPABASE_KEY = "sb_secret_Kvssja_b25MU2zlsUAumsg_sV8pqwPQ";

    private EditText etNombre, etApellido, etTelefono, etEmail, etPassword, etRol;
    private Button btnRegister;
    private TextView tvEstado;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etTelefono = findViewById(R.id.etTelefono);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRol = findViewById(R.id.etRol);
        btnRegister = findViewById(R.id.btnRegister);
        tvEstado = findViewById(R.id.tvEstado);

        client = new OkHttpClient();

        btnRegister.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String rol = etRol.getText().toString().trim();

            if(nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty() || rol.isEmpty()){
                tvEstado.setText("Completa todos los campos");
                return;
            }

            registrarUsuario(nombre, apellido, telefono, email, password, rol);
        });
    }

    private void registrarUsuario(String nombre, String apellido, String telefono,
                                  String email, String password, String rol){
        String url = SUPABASE_URL + "/rest/v1/usuarios";

        JSONObject json = new JSONObject();
        try {
            json.put("nombre", nombre);
            json.put("apellido", apellido);
            json.put("telefono", telefono);
            json.put("email", email);
            json.put("password", password);
            json.put("rol", "cliente");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(
                json.toString(),
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
                String bodyText = "";
                try {
                    bodyText = response.body() != null ? response.body().string() : "";
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String message = "Código: " + response.code() + "\nCuerpo: " + bodyText;

                // Show Toast on UI thread
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                    // Optional: also update the TextView
                    tvEstado.setText(message);
                });
            }

        });
    }

    private void actualizarTexto(String texto){
        runOnUiThread(() -> tvEstado.setText(texto));
    }
}
