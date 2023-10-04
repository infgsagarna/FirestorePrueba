package com.example.firestoreprueba;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class AuthActivity extends AppCompatActivity {

   // private FirebaseAuth mAuth;
   // private static final String TAG = "EmailPassword";
    private static final String TAG = "AuthActivity";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
       // FirebaseAuth.getInstance().signOut();
        //escribir();
        //leer();
        //borrarDocumento();
        //borrarCampo();
        //configurarDocumento();
        //actualizar();
        //guardarDatos();
        setup();
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        Toast.makeText(getApplicationContext(),"Now onStart() calls", Toast.LENGTH_LONG).show(); //onStart Called
        cargarPreferencias();
    }

    private void cargarPreferencias() {
        //Recogemos el email, pass y el checkbox (boolean)
        EditText e=findViewById(R.id.emailEditText);
        EditText p=findViewById(R.id.passwordEditText);
        CheckBox checkBox=findViewById(R.id.acuerdoCheckBox);

        /*
        SharedPreferences prefs = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        e.setText(prefs.getString("user",""));
        e.setText(prefs.getString("pass", "wifi.txt"));
        checkBox.setChecked(prefs.getBoolean("pantalla", true));*/

        //Instancioamos clase SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences
                ("Credenciales",Context.MODE_PRIVATE);


        //no lo vamos a editar, pero si recoger datos.
        String usuario = sharedPref.getString("user","No existe dicha información");
        Boolean check = sharedPref.getBoolean("check",false);


        e.setText(usuario);
        checkBox.setChecked(check);

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"Now onDestroyt() calls", Toast.LENGTH_LONG).show(); //onStart Called
        guardarPreferencias();
    }

    private void guardarPreferencias() {
        //Recogemos el email, pass y el checkbox (boolean)
        EditText e=findViewById(R.id.emailEditText);
        CheckBox checkBox=findViewById(R.id.acuerdoCheckBox);

        //Instancioamos clase SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences
                (getString(R.string.cred),Context.MODE_PRIVATE);

        String usuario=e.getText().toString();
        Boolean check=checkBox.isChecked();

        //Instancioamos su Editor
        SharedPreferences.Editor editor = sharedPref.edit();


        //Introducimos los pares stributo valor deseados
        editor.putString("user", usuario);
        editor.putBoolean("check",check);

        //Guardamos los datos con apply() o commit()
        //editor.apply();
        editor.commit();

        /*
        SharedPreferences prefs = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user", e.getText().toString());
        editor.putString("pass", p.getText().toString());
        editor.putBoolean("pantalla", checkBox.isChecked());
        editor.commit();*/
    }
    /*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }else{
            setup();
        }
    }*/

    public void setup(){
        String title="Autenticación";
        this.setTitle(title);

        Button botsup=findViewById(R.id.signUpButton);
        Button botsin=findViewById(R.id.signInButton);

        //  TextView text=findViewById(R.id.emailEditText);

        EditText e=findViewById(R.id.emailEditText);
        EditText p=findViewById(R.id.passwordEditText);

        CheckBox checkBox=findViewById(R.id.acuerdoCheckBox);

        String email=e.getText().toString();
        String pass=p.getText().toString();


        botsup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
              //  Log.d(TAG, "checkpoint_signup");

                signUp(email,pass);


                //  escribirDatos(email,pass);
            }

        });

        botsin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
              //  Log.d(TAG, "checkpoint_signin");
                String email=e.getText().toString();
                String pass=p.getText().toString();
                signIn(email,pass);
                //  escribirDatos(email,pass);
            }

        });

    }

    public void reload(){
        Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
        startActivity(intent);
    }
    private void updateUI(FirebaseUser user) {
       // if (user.isEmailVerified()) {

            Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
            startActivity(intent);
      //  }
    }
    public void showHome(String email,ProviderType provider){
        Intent homeIntent = new Intent(AuthActivity.this, HomeActivity.class);
        homeIntent.putExtra("email",email);
        homeIntent.putExtra("provider",provider.name());
        startActivity(homeIntent);
    }

    public void showAlert(){

        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 2. Chain together various setter methods to set the dialog characteristics
        /*builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });*/
        builder.setPositiveButton("Aceptar", null);
        builder.setMessage("Se ha producido un error autenticando al usuario")
                .setTitle("Error");
        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    public void signUp(String email, String pass){
        if(!email.isEmpty() && !pass.isEmpty()){
             // Log.d(TAG, "checkpoint_signup");
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                showHome(task.getResult().getUser().getEmail(),ProviderType.BASIC);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(AuthActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                                showAlert();
                            }
                        }
                    });

        }
    }
    public void signIn(String email, String pass){
        if(!email.isEmpty() && !pass.isEmpty()){
           // Log.d(TAG, "checkpoint_signin");
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                showHome(email,ProviderType.BASIC);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(AuthActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                                showAlert();
                            }
                        }
                    });

        }
    }



    public void guardarDatos(){
        Button boton=findViewById(R.id.signUpButton);

        boton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
              //  TextView text=findViewById(R.id.emailEditText);

                EditText e=findViewById(R.id.emailEditText);
                EditText p=findViewById(R.id.passwordEditText);

                String email=e.getText().toString();
                String pass=p.getText().toString();
                escribirDatos(email,pass);
            }

        });
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

    public void configurarDocumento(){
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Gontzal");
        user.put("middle", "Sagarna");
        user.put("last", "Martinez");
        user.put("born", 1987);
     //   user.put("capital", true);

        db.collection("users").document("nuevo")
                .set(user)
               // .set(user,SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }
    public void borrarCampo(){
        DocumentReference docRef = db.collection("users").document("31ClfAIeAITNNBq7aom8");

        // Remove the 'capital' field from the document
        Map<String,Object> updates = new HashMap<>();
        updates.put("middle", FieldValue.delete());

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            // [START_EXCLUDE]
            @Override
            public void onComplete(@NonNull Task<Void> task) {}
            // [START_EXCLUDE]
        });
    }
    public void borrardocumento(){
        db.collection("users").document("zQ6U84KTFnRjSpprjgdG")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
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
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void escribir(){


        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("middle", "Mathison");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
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


    public void escribirDatos(String email,String pass){


        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("pass", pass);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
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
    /*
    @Override
    public void onStart() {
        super.onStart();


    }*/
/*
    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }*/

}