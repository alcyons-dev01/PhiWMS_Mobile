package fr.alcyons.phiwms_mobile.Outils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;

import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Livraison.InformationLivraisonActivity;
import fr.alcyons.phiwms_mobile.R;

public class Dialogue {


    //Livraison
    public SignaturePad signaturePad;
    public SignaturePad signatureChauffeur;
    public TextView typeSignataire;
    public Dialog dialog;
    Context context;
    View.OnClickListener onClickListener;
    Utilisateur utilisateur;

    //Retour fournisseur
    public EditText edit_nom_chauffeur;
    public EditText edit_prenom_chauffeur;
    public EditText edit_transporteur;
    public EditText commentaireEditText;

    public Dialogue(Context context, View.OnClickListener onClickListener, Utilisateur utilisateur) {
        this.context = context;
        this.utilisateur = utilisateur;
        this.onClickListener = onClickListener;
    }

    public void signaturePadOpen(boolean chauffeur) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.signature_livraison);

        Window window = dialog.getWindow();

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(layoutParams);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        signaturePad = (SignaturePad) dialog.findViewById(R.id.signaturePad);
        typeSignataire = (TextView) dialog.findViewById(R.id.typeSignataire);

        Button boutonValider = (Button) dialog.findViewById(R.id.boutonValider);
        boutonValider.setOnClickListener(onClickListener);

        Button boutonEffacer = (Button) dialog.findViewById(R.id.boutonEffacer);
        boutonEffacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

        TextView layoutFermePAD = (TextView) dialog.findViewById(R.id.layoutFermePAD);
        layoutFermePAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void padCommentairePhotoLivraison() {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.commentaire_photo_livraison);

        Window window = dialog.getWindow();

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(layoutParams);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        commentaireEditText = (EditText) dialog.findViewById(R.id.commentaireEditText);

        Button boutonValider = (Button) dialog.findViewById(R.id.boutonValider);
        boutonValider.setOnClickListener(onClickListener);

        LinearLayout boutonPhoto = (LinearLayout) dialog.findViewById(R.id.boutonPhoto);
        boutonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activityName = context.getClass().getSimpleName();
                if(activityName.contentEquals("InformationLivraisonActivity"))
                {
                    ((InformationLivraisonActivity) context).onClickMenuPhoto();
                }
            }
        });

        TextView layoutFermePAD = (TextView) dialog.findViewById(R.id.layoutFermePAD);
        layoutFermePAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void signaturePadOpenFournisseur() {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.signature_retour_fournisseur);

        Window window = dialog.getWindow();

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(layoutParams);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        signaturePad = (SignaturePad) dialog.findViewById(R.id.signaturePad);
        edit_nom_chauffeur = (EditText) dialog.findViewById(R.id.edit_nom_chauffeur);
        edit_prenom_chauffeur = (EditText) dialog.findViewById(R.id.edit_prenom_chauffeur);
        edit_transporteur = (EditText) dialog.findViewById(R.id.edit_transporteur);

        Button boutonValider = (Button) dialog.findViewById(R.id.boutonValider);
        boutonValider.setOnClickListener(onClickListener);

        Button boutonEffacer = (Button) dialog.findViewById(R.id.boutonEffacer);
        boutonEffacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

        dialog.show();
    }
}
