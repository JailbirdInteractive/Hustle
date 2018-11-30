package com.rebirth.hustle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adroitandroid.chipcloud.ChipCloud;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.pyplcheckout.Environment;
import com.paypal.pyplcheckout.PYPLCheckout;
import com.paypal.pyplcheckout.PYPLCheckoutEnvironment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;

import java.io.File;
import java.math.BigDecimal;
import java.text.BreakIterator;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Account Activity";
    String ID;
    TextView nameText;
    Employee currEmployee;
    ChipCloud specChips;
    private TextView userName;
    private String myName;
Button signOutButton;
FloatingActionButton fab,specFab,addFab;

    private Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        context=this;
        nameText = findViewById(R.id.user_name);
        fab=findViewById(R.id.fab);
        fab.setOnClickListener(this);
        addFab=findViewById(R.id.add_job_button);
        addFab.setOnClickListener(this);
        specFab=findViewById(R.id.spec_button);
        specFab.setOnClickListener(this);
        ID = getIntent().getStringExtra("ID");
        specChips = findViewById(R.id.chip_cloud);
        specChips.setMode(ChipCloud.Mode.NONE);
        getEmployee();
        signOutButton=findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(this);
        userName = findViewById(R.id.user_name);
        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            actionBar.setTitle(myName);

            //actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left_black_48dp);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

    }
    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId("Ac3x1cQFV6364VunezqOA4A9vxRtyofb4zRpeGSqsztP1z26efP4Lbz7vjHSyEroSFxofWCLKhZlHNLf");

    private void getEmployee() {
        DatabaseReference myRef = database.getReference();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.orderByChild("empID").equalTo(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w(TAG, "User " + dataSnapshot.toString());
                if (dataSnapshot.getValue() == null) {

                } else {
                    Log.w(TAG, "User exists");
                    currEmployee = dataSnapshot.child(ID).getValue(Employee.class);
                    myName = currEmployee.getEmpName();
                    userName.setText(myName);
                    getSupportActionBar().setTitle(myName);
                    nameText.setText(currEmployee.empName);
                    List<String> tags = currEmployee.empTags;
                    String[] taglist = tags.toArray(new String[tags.size()]);
getPhotos(ID);
                    specChips.addChips(taglist);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

    @Override
    protected void onResume() {
        //getPhotos(ID);

        super.onResume();
    }
    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
    public void onBuyPressed(View pressed) {

        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.

        PayPalPayment payment = new PayPalPayment(new BigDecimal("100.00"), "USD", "Sample Job",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));

    }
    public class SampleWebViewIntercept extends WebViewClient {
        //include this for integrating with Checkout.js
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            PYPLCheckout.getInstance().loadScript(view);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            return PYPLCheckout.getInstance().shouldOverrideUrlLoading(view, url);
        }
    }
    private void payment(){
     /*   final PYPLCheckoutEnvironment pyplEnvironment = PYPLCheckoutEnvironment.getInstance();
        pyplEnvironment.setkPYPLEnvironment(Environment.SANDBOX);
        pyplEnvironment.setkPYPLUrlScheme("foobarstore");
//set the redurect uri, that has the assetLinks.json.
        pyplEnvironment.setkPYPLRedirectURL("https://scorchlive.com");
//set the client ID for the merchant
        pyplEnvironment.setClientId(getString(R.string.paypal_client_id));
//set the user context. 'this' should be the activity from which the experience is being called.
        pyplEnvironment.setkPYPLUserContext(this);
        WebView webView = new WebView(this);
//SampleWebViewIntercept is your webViewClient.
        webView.setWebViewClient(new SampleWebViewIntercept());

//TestWebViewIntercept.class
                PYPLCheckout.getInstance().interceptWebView(webView, this);*/


        final PYPLCheckoutEnvironment pyplEnvironment = PYPLCheckoutEnvironment.getInstance();
        pyplEnvironment.setkPYPLWebBrowserOnly(true);
        PYPLCheckout.getInstance().startCheckoutWithECToken(AccountActivity.this, "EC-1FP91222RL3429812");
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.done_button:
                signOut();
                break;
            case R.id.fab:
                addPhoto();
                break;
            case R.id.spec_button:
                startActivity(new Intent(context,TagActivity.class));
                break;
            case R.id.add_job_button:
onBuyPressed(v);
break;
        }
    }
    private void addPhoto() {
        final UCrop.Options options = new UCrop.Options();
        options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setMaxBitmapSize(1024);

        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCircleDimmedLayer(true);


        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(AccountActivity.this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {

                        File newFile = new File(getExternalCacheDir() + File.separator + "avatar.jpg");
                        Uri tmp = Uri.fromFile(newFile);
                        UCrop.of(uri, tmp)
                                .withOptions(options)
                                .withMaxResultSize(512, 512)
                                .start((Activity) context);


                        // here is selected uri
                    }
                })
                .showCameraTile(false)
                .setOnErrorListener(new TedBottomPicker.OnErrorListener() {
                    @Override
                    public void onError(String message) {
                        Log.e("Photo ", "Photo error: " + message);

                    }
                })
                .create();

        tedBottomPicker.show(getSupportFragmentManager());

    }
    private void getPhotos(String id) {

        DatabaseReference ref = database.getReference().child("Users").child(id).child("photoUrl");

        ref.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.e("Post ", "Picture: " + dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null) {
                  /*
                    String img = (String) dataSnapshot.getValue();
                    byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    profilePic.setImageBitmap(decodedByte);
                    */
                    String myAvatarUrl = (String) dataSnapshot.getValue();
                    final ImageView profilePic=findViewById(R.id.profile_image);

                    final StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.photo_bucket));

storageRef.child(myAvatarUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
    @Override
    public void onSuccess(Uri uri) {
        Picasso.with(AccountActivity.this).load(uri).placeholder(R.drawable.h_logo).into(profilePic);

    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        Log.e("Photo",e.getMessage());
    }
});
                    //Glide.with(PlaceInfoActivity.this).load(postsnap.getValue()).into(frissonView);


                    Log.e("Photo ", "Photo Url: " + dataSnapshot.getValue());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            FirebaseStorage storage=FirebaseStorage.getInstance();
            final StorageReference storageRef = storage.getReferenceFromUrl(getString(R.string.photo_bucket)).child("Profile Images/" + ID);

            final StorageReference photoRef = storageRef.child("images/" + resultUri.getLastPathSegment());
            UploadTask uploadTask = photoRef.putFile(resultUri);

// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //Snackbar.make(coordinatorLayout, " Photo Added! ", Snackbar.LENGTH_SHORT).show();
                    Uri downloadUri = Uri.parse(photoRef.getPath());

                    //DatabaseReference myRef= mDatabase.child("trails").push();
                    //myRef.setValue(img);
                    database.getReference().child("Users").child(ID).child("photoUrl").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getPhotos(ID);
                        }
                    });

                }
            });


            /*
            Bitmap bmp = BitmapFactory.decodeFile(resultUri.getPath());//your image
            ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bYtE);
            bmp.recycle();
            byte[] byteArray = bYtE.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            mDatabase.child("Users").child(myId).child("Profile Pic").setValue(encodedImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(coordinatorLayout, " Photo Added! ", Snackbar.LENGTH_SHORT).show();
                    getPhotos(myId);
                }
            });
            */

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            //Snackbar.make(coordinatorLayout, " Something went wrong! ", Snackbar.LENGTH_SHORT).show();

        }else {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("paymentExample", confirm.toJSONObject().toString(4));

                        // TODO: send 'confirm' to your server for verification.
                        // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                        // for more details.

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
