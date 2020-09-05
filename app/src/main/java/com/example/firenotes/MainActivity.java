package com.example.firenotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import auth.Login;
import auth.Register;
import auth.Splash;
import note.Note;
import note.addNotes;
import note.editNote;
import note.noteDetails;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView nav_view;
    RecyclerView recyclerView;
    FirebaseFirestore fstore;
    FirebaseUser user;
    FirebaseAuth fauth;
    TextView email,name;
    FirestoreRecyclerAdapter<Note,NoteViewHolder> noteadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        nav_view = (NavigationView) findViewById(R.id.navview);
        nav_view.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        name = findViewById(R.id.username);
        email = findViewById(R.id.useremails);

        setSupportActionBar(toolbar);

        fstore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();

        Query query = fstore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<Note> allNote = new FirestoreRecyclerOptions.Builder<Note>()
                    .setQuery(query,Note.class).build();
        noteadapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNote) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull final Note note) {
                noteViewHolder.notetittle.setText(note.getTitle());
                noteViewHolder.notecontent.setText(note.getContent());
                final int col = getRandomColor();
                final String docId = noteadapter.getSnapshots().getSnapshot(i).getId();
                noteViewHolder.mcardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(col,null));
                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), noteDetails.class);
                        intent.putExtra("title",note.getTitle());
                        intent.putExtra("content",note.getContent());
                        intent.putExtra("color",col);
                        intent.putExtra("noteId",docId);
                        view.getContext().startActivity(intent);
                    }
                });
                ImageView menuicon = noteViewHolder.view.findViewById(R.id.menuIcon);
                menuicon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu menu = new PopupMenu(v.getContext(),v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent i = new Intent(MainActivity.this, editNote.class);
                                i.putExtra("title",note.getTitle());
                                i.putExtra("content",note.getContent());
                                //intent.putExtra("color",col);
                                i.putExtra("noteId",docId);
                                startActivity(i);
                                return false;
                            }
                        });
                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference docref = fstore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                                docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this,"Note Deleted!",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this,"Try Again!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        menu.show();
                    }
                });

            }
            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };
        recyclerView = findViewById(R.id.notelist);
        drawerToggle  = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        FloatingActionButton fab = findViewById(R.id.fab);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(noteadapter);

        View headerView = nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.username);
        TextView emails = headerView.findViewById(R.id.useremails);
        if(user.isAnonymous())
        {
            emails.setText("Phle Log in kr..");
            username.setText("Log in krle bhai");
        }
        else {
            emails.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(view.getContext(), addNotes.class));
            }
        });



    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId())
        {
            case R.id.notes:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.addnotes:
                startActivity(new Intent(this,addNotes.class));
                break;
            case R.id.logout:
                checkUser();
                break;
            case R.id.sync:
                if(user.isAnonymous())
                {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You Are Already Connected",Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                Toast.makeText(this,"Coming soon",Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void checkUser()
    {
        if(user.isAnonymous())
        {
            displayAlert ();
        }
        else
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setTitle("Are you sure?")
                    .setMessage("You Want To Log Out")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fauth.signOut();
                            startActivity(new Intent(getApplicationContext(), Splash.class));
                            finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            alert.show();

        }
    }

    private void displayAlert()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("You are logged in with Temporary Account. Logging out will delete all Notes")
                .setPositiveButton("Sync Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("LogOut", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(),Splash.class));
                                finish();
                            }
                        });

                    }
                });
        alert.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.setting)
            Toast.makeText(this,"on Setting",Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
    public class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView notetittle,notecontent;
        View view;
        CardView mcardView;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetittle = itemView.findViewById(R.id.titles);
            notecontent = itemView.findViewById(R.id.content);
            mcardView = itemView.findViewById(R.id.noteCard);
            view = itemView;

        }
    }
    private int getRandomColor() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.blue);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.lightPurple);
        colorcode.add(R.color.gray);
        colorcode.add(R.color.greenlight);
        colorcode.add(R.color.yellow);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.red);
        colorcode.add(R.color.notgreen);
        Random random = new Random();
        int num = random.nextInt(colorcode.size());
        return colorcode.get(num);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteadapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteadapter!=null)
        {
            noteadapter.stopListening();
        }
    }
}
