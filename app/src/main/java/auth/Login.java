package auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firenotes.MainActivity;
import com.example.firenotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText email,passs;
    Button login;
    TextView create,forget;
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    ProgressBar spinner;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login Account");

        email = findViewById(R.id.email);
        passs = findViewById(R.id.lPassword);
        login = findViewById(R.id.loginBtn);
        forget = findViewById(R.id.forgotPasword);
        create = findViewById(R.id.createAccount);
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        spinner = findViewById(R.id.progressBar3);
        user = fauth.getCurrentUser();
        showWaring();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String semail = email.getText().toString();
                final String spass = passs.getText().toString();
                if(semail.isEmpty() || spass.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Fields Are Required",Toast.LENGTH_SHORT).show();
                }
                spinner.setVisibility(View.VISIBLE);
            if(user.isAnonymous())
            {
                fstore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"All Temp Notes Deleted",Toast.LENGTH_SHORT).show();

                    }
                });
                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"temp User Deleted",Toast.LENGTH_SHORT).show();
                    }
                });
            }

                fauth.signInWithEmailAndPassword(semail,spass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getApplicationContext(),"Successfully \n Login",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            default:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
    private void showWaring()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("If You Are Existing User Then Login Will Delete Your All Current Saved Notes")
                .setPositiveButton("Save Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Its OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){

                    }
                });
        alert.show();
    }
}
