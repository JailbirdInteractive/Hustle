package com.rebirth.hustle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.jksiezni.permissive.PermissionsGrantedListener;
import com.github.jksiezni.permissive.Permissive;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity Login";
    Context context;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 999;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (SDK_INT >= Build.VERSION_CODES.M) {

            new Permissive.Request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                    .whenPermissionsGranted(new PermissionsGrantedListener() {
                        @Override
                        public void onPermissionsGranted(String[] permissions) throws SecurityException {


                        }
                    }).execute(MainActivity.this);
        }

    }

    @Override
    protected void onResume() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            makeToast("Welcome back "+currentUser.getDisplayName());
            startActivity(new Intent(getApplicationContext(), AccountActivity.class).putExtra("ID",currentUser.getUid()));

        }
        super.onResume();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void makeToast(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onClick(View v) {
        signIn();
        //Toast.makeText(context,"Sign in",Toast.LENGTH_SHORT).show();

    }
private void signOut(){
    FirebaseAuth.getInstance().signOut();

}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            createUser(user.getUid(),user.getDisplayName(),user.getEmail());
                            //Toast.makeText(context,"Signed in as "+user.getDisplayName(),Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.main_layout), "Signed in as "+user.getDisplayName(), Snackbar.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    private void createUser(final String ID,String name,String email){
        Employee employee=new Employee(ID,name,email,null,"");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("empID").equalTo(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w(TAG, "User " + dataSnapshot.toString());
                if (dataSnapshot.getValue() == null) {

                    startActivity(new Intent(getApplicationContext(), TagActivity.class));
//finish();
                } else {
                    Log.w(TAG, "User exists");
                    //Log.d("LoginActivity", "Messing everything up");

                    startActivity(new Intent(getApplicationContext(), AccountActivity.class).putExtra("ID",ID));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

}
