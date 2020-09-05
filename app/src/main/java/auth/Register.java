package auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {
    EditText name,email,password,confirmPassword;
    Button sync;
    TextView login;
    ProgressBar sycing;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Create New Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.userName);
        email = findViewById(R.id.userEmail);
        password = findViewById(R.id.password);
        sycing = findViewById(R.id.progressBar4);
        confirmPassword = findViewById(R.id.passwordConfirm);
        sync = findViewById(R.id.createAccount);
        login = findViewById(R.id.login);
        firebaseAuth = FirebaseAuth.getInstance();

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = name.getText().toString();
                String useremail = email.getText().toString();
                String userpassword = password.getText().toString();
                String usercmpassword = confirmPassword.getText().toString();
                if (username.isEmpty() || useremail.isEmpty() || userpassword.isEmpty() || usercmpassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All Fields Are Required", Toast.LENGTH_SHORT).show();
                }
                if (!userpassword.equals(usercmpassword)) {
                    confirmPassword.setError("Password Do not match");
                }
                else if(userpassword.equals(usercmpassword)){
                    sycing.setVisibility(View.VISIBLE);
                    AuthCredential authCredential = EmailAuthProvider.getCredential(useremail, userpassword);
                    firebaseAuth.getCurrentUser().linkWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(), "NOtes are Synced!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            FirebaseUser usr = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest uPCR = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            usr.updateProfile(uPCR);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Network Issue\n Try Again!", Toast.LENGTH_SHORT).show();
                            sycing.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }

        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
