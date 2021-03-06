package com.example.wgapp.ui.signIn;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wgapp.MainActivity;
import com.example.wgapp.R;
import com.example.wgapp.models.Roommate;
import com.example.wgapp.ui.start.StartScreenActivity;
import com.example.wgapp.util.Database;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


// https://firebase.google.com/docs/auth/android/google-signin
//https://developers.google.com/identity/sign-in/android/sign-in
//https://developers.google.com/identity/sign-in/android/start-integrating
//http://www.androiddocs.com/training/basics/firstapp/starting-activity.html

// AF:40:35:9D:71:10:ED:87:4D:13:0C:0B:DD:1B:11:57:30:0C:0F:6B

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FirebaseUIActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    Roommate rm;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);
    }

    public void createSignInIntent(View v) {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }



    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                MainActivity.initUserDataBase();
                mCheckInforInServer(MainActivity.getUserReadRef());
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
    // [END auth_fui_result]

    private void mCheckInforInServer(DatabaseReference ref) {
        final Context self = this;
        mReadDataOnce(ref, new MainActivity.OnGetDataListener() {
            @Override
            public void onStart() {
                //DO SOME THING WHEN START GET DATA HERE
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog( self);
                    mProgressDialog.setMessage("Loading User Data");
                    mProgressDialog.setIndeterminate(true);
                }

                mProgressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    Roommate mate = data.getValue(Roommate.class);
                    rm = mate;

                    if(rm == null || rm.getCommuneID().equals("None")){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Roommate rm = new Roommate(0, user);
                        MainActivity.getUserWriteRef().setValue(rm);
                    }else{
                        MainActivity.getUserWriteRef().setValue(rm);

                    }
                    Intent intent = new Intent(self, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(DatabaseError databaseError) {
            }
        });

    }
    public void mReadDataOnce(DatabaseReference ref, final MainActivity.OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError);
            }
        });
    }
    public static void signOut(Context con) {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(con)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_signout]
    }

    public void delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_delete]
    }

    public void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        // [START auth_fui_theme_logo]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_money_bag)      // Set logo drawable
                        .setTheme(R.style.Theme_AppCompat_DayNight)      // Set theme
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_theme_logo]
    }

    public void privacyAndTerms() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();
        // [START auth_fui_pp_tos]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_pp_tos]
    }
}