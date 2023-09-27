package com.example.firestoreprueba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

enum ProviderType{
    BASIC
}
public class HomeActivity extends AppCompatActivity {


    private static final String TAG = "HomeActivity";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Bundle bundle=getIntent().getExtras();
        String email=bundle.getString("email");
        String provider=bundle.getString("provider");
        setup(email,provider);
    }
    /*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }*/

    public void setup(String email,String provider){

        String title="Home";
        this.setTitle(title);

        TextView e=findViewById(R.id.emailTextView);
        TextView p=findViewById(R.id.providerTextView);

        e.setText(email);
        p.setText(provider);

        Button botsave=findViewById(R.id.saveButton);
        Button botread=findViewById(R.id.readButton);
        Button botupdate=findViewById(R.id.updateButton);
        Button botlogout=findViewById(R.id.logOutButton);

        //  TextView text=findViewById(R.id.emailEditText);
/*
        EditText e=findViewById(R.id.emailEditText);
        EditText p=findViewById(R.id.passwordEditText);

        String email=e.getText().toString();
        String pass=p.getText().toString();
*/

        botsave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                escribirDatos();
                //  escribirDatos(email,pass);
            }

        });

        botread.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                leer();
                //signIn(email,pass);
                //  escribirDatos(email,pass);
            }

        });
        botupdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                actualizar();
            }

        });
        botlogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                cerrarSesion();
            }

        });
    }

    public void reload(){

    }
    public void cerrarSesion(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
        startActivity(intent);
    }
    public void actualizar(){
        DocumentReference washingtonRef = db.collection("users").document("nuevo");

// Set the "mujer" field of the user 'true'
        washingtonRef
                .update("mujer", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
    public void leer(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            mostrar(task);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void mostrar(Task<QuerySnapshot> task){
        TextView text=findViewById(R.id.showTextView);
        for (QueryDocumentSnapshot document : task.getResult()) {
            text.append(document.getData().toString() + "\n");
         //   Log.d(TAG, document.getId() + " => " + document.getData());
        }

    }
    public void escribirDatos(){

        EditText n=findViewById(R.id.nameEditText);
        EditText a=findViewById(R.id.ageEditText);
        String name=n.getText().toString();
        String ageString=a.getText().toString();
        int age=Integer.parseInt(ageString);
/*
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", name);
        user.put("edad", age);*/

        Persona persona=new Persona(name,age);


        // Agregar un documento con un ID autogenerado
        db.collection("users")
                .add(persona)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


    }
}