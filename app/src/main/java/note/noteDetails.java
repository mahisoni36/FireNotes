package note;

import android.content.Intent;
import android.os.Bundle;

import com.example.firenotes.MainActivity;
import com.example.firenotes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class noteDetails extends AppCompatActivity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView content = findViewById(R.id.notedetailescontent);
        TextView title = findViewById(R.id.notedetailstitle);
        content.setMovementMethod(new ScrollingMovementMethod());
        intent = getIntent();
        final String titles = intent.getStringExtra("title");
        final String  contents = intent.getStringExtra("content");
        final String docId = intent.getStringExtra("noteId");
        content.setText(contents);
        title.setText(titles);
        content.setBackgroundColor(getResources().getColor(intent.getIntExtra("color",0)));




        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(view.getContext(), editNote.class);
                intent1.putExtra("title",titles);
                intent1.putExtra("content",contents);
                intent1.putExtra("noteId",docId);
                startActivity(intent1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
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
