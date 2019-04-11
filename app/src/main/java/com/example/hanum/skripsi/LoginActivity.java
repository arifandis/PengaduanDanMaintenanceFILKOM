package com.example.hanum.skripsi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button btnLogin;

    private String PREF_NAME = "SkripsiPrefs";

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mProgressView = findViewById(R.id.login_progress);
        btnLogin = findViewById(R.id.email_sign_in_button);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login(){
        String role = getIntent().getStringExtra("role");
        mProgressView.setVisibility(View.VISIBLE);
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        if (email.isEmpty() || password.isEmpty()){
            mProgressView.setVisibility(View.GONE);
            Toast.makeText(this, "NIP atau NIM dan password harus di isi!", Toast.LENGTH_SHORT).show();
        }else {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo !=null && networkInfo.isConnected()){
                mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean condition = false;
                        String nama;

                        if (role.equals("pengadu")){
                            for (DataSnapshot data: dataSnapshot.getChildren()){
                                String key = data.getKey();
                                if (key.equals("dosen")){
                                    for (DataSnapshot value: data.getChildren()){
                                        String id = value.getKey();
                                        String pass = String.valueOf(value.child("password").getValue());
                                        if (email.equals(id) && password.equals(pass)){
                                            condition = true;
                                            nama = value.child("nama").getValue(String.class);
                                            Log.d("Nama",nama);
                                        }
                                    }
                                }else if(key.equals("mahasiswa")){
                                    for (DataSnapshot value: data.getChildren()){
                                        String id = value.getKey();
                                        String pass = String.valueOf(value.child("password").getValue());
                                        if (email.equals(id) && password.equals(pass)){
                                            condition = true;
                                            nama = value.child("nama").getValue(String.class);
                                            Log.d("Nama",nama);
                                        }
                                    }
                                }
                            }
                        }else if (role.equals("pegawai")){
                            for (DataSnapshot data: dataSnapshot.child("pegawaiPerkap").getChildren()){
                                String id = data.getKey();
                                String pass = String.valueOf(data.child("password").getValue());
                                if (email.equals(id) && password.equals(pass)){
                                    condition = true;
                                    nama = data.child("nama").getValue(String.class);
                                    Log.d("Nama",nama);
                                }
                            }
                        }else if (role.equals("kasubag")){
                            String id = dataSnapshot.child("kasubag").child("nik").getValue(String.class);
                            String pass = dataSnapshot.child("kasubag").child("password").getValue(String.class);
                            if (email.equals(id) && password.equals(pass)){
                                condition = true;
                                nama = dataSnapshot.child("kasubag").child("nama").getValue(String.class);
                                Log.d("Nama",nama);
                            }
                        }

                        if (condition){
                            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString("id",email);
                            editor.putString("role",role);
                            editor.apply();

                            mProgressView.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "Data yang dimasukkan salah!", Toast.LENGTH_SHORT).show();
                            mProgressView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressView.setVisibility(View.GONE);
                        Log.d("DatabaseError",databaseError.getMessage());
                    }
                });
            }else{
                mProgressView.setVisibility(View.GONE);
                Log.d("Connectivity","No network connection");
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,PilihRoleActivity.class));
        finish();
    }
}

