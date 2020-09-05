package note;

import android.content.Intent;
import android.os.Bundle;

import com.example.firenotes.MainActivity;
import com.example.firenotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class addNotes extends AppCompatActivity {
    FirebaseFirestore fstore;
    EditText contenttitle,notetitle;
    ProgressBar progressBar;
    FirebaseAuth user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        contenttitle = (EditText) findViewById(R.id.writecontent);
        notetitle = (EditText) findViewById(R.id.notetitle);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String ntitle = notetitle.getText().toString();
                String  ncontent = contenttitle.getText().toString();
                if(ntitle.isEmpty() || ncontent.isEmpty())
                {
                    Toast.makeText(addNotes.this,"Can't Save Note with Empty Field",Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    progressBar.setVisibility(view.VISIBLE);
                    DocumentReference docref = fstore.collection("notes").document(user.getUid()).collection("myNotes").document();
                    Map<String,Object> note = new HashMap<>();
                    note.put("title",ntitle);
                    note.put("content",ncontent);
                    docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(addNotes.this,"Saved Succesfully!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(addNotes.this,MainActivity.class));
                            progressBar.setVisibility(view.INVISIBLE);
                            //startActivity( new Intent(addNotes.this,MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(addNotes.this,"Error! Try Again",Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.close_manu,menu);
    return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.closenote)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}
