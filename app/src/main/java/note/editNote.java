package note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.firenotes.MainActivity;
import com.example.firenotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class editNote extends AppCompatActivity {
    Intent intent;
    EditText editnotetitle, editnotecontent;
    Toolbar toolbar;
    ProgressBar spinner;
    FirebaseFirestore fstore;
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        intent = getIntent();
        fstore = FirebaseFirestore.getInstance();

        FloatingActionButton fab = findViewById(R.id.saveedited);
         final FirebaseAuth user  = FirebaseAuth.getInstance();

        editnotecontent = findViewById(R.id.editnotecontent);
        editnotetitle = findViewById(R.id.editnotetitle);
        toolbar = findViewById(R.id.toolbar);
        spinner = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);

        String notetitle = intent.getStringExtra("title");
        String notecontent = intent.getStringExtra("content");
        final String docId = intent.getStringExtra("noteId");

        editnotetitle.setText(notetitle);
        editnotecontent.setText(notecontent);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String ntitle = editnotetitle.getText().toString();
                String ncontent = editnotecontent.getText().toString();
                if (ntitle.isEmpty() || ncontent.isEmpty()) {
                    Toast.makeText(editNote.this, "Can't Save Note with Empty Field", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    spinner.setVisibility(view.VISIBLE);
                    DocumentReference docref = fstore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", ntitle);
                    note.put("content", ncontent);
                    docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(editNote.this, "Note Saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            spinner.setVisibility(view.INVISIBLE);
                                                    }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(editNote.this, "Error! Try Again", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

    }
}