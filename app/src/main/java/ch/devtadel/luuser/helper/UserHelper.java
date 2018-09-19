package ch.devtadel.luuser.helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import ch.devtadel.luuser.R;

public abstract class UserHelper {

    public static final String DEFAULT_CANTON = "Bern";

    public static void signOutDialog(final FirebaseAuth auth, final AppCompatActivity activity, final Intent intent){

        LayoutInflater li = LayoutInflater.from(activity);
        View promptView = li.inflate(R.layout.prompt_sign_out, null);

        //Promptbuilder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        //View dem Dialog zuweisen.
        alertDialogBuilder.setView(promptView);

        // Dialognachricht setzen
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Ja",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                auth.signOut();
                                activity.finish();
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(intent);
                            }
                        })
                .setNegativeButton("Nein",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // Dialog erstellen
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Dialog zeigen
        alertDialog.show();


    }
}
