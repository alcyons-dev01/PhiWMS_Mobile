package fr.alcyons.phiwms_mobile.Outils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Interfaces.ConfirmationServiceListener;
import fr.alcyons.phiwms_mobile.Interfaces.RetourServiceListener;
import fr.alcyons.phiwms_mobile.Interfaces.SaisieTextListener;
import fr.alcyons.phiwms_mobile.R;
public class Alerte {

    public static String texteARetourner = null;
    public static boolean mResult;
    public static List<Produit> produitsCorrespondants;

    public static AlertDialog alert;

    public static NumberPicker aNumberPicker;

    public static boolean afficherAlerte(Context context, String title, String message, String type) {
        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        // make a text input dialog and show it
        alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        alert.setCancelable(false);
        alert.setMessage(message);
        if (type.equals("alerte")) {
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mResult = true;
                            dialog.dismiss();
                        }
                    });
        } else if (type.equals("OuiNon")) {
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Non",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mResult = false;
                            dialog.dismiss();
                        }
                    });
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Oui",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mResult = true;
                            dialog.dismiss();
                        }
                    });
        }else if (type.equals("serialisation")) {
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Préparation",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mResult = false;
                            dialog.dismiss();
                        }
                    });
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Réception",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mResult = true;
                            dialog.dismiss();
                        }
                    });
        }
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.show();

        // loop till a runtime exception is triggered.
        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }

        return mResult;
    }
    public static String afficherAlerteEditText(final Context context, String title, String message) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        // make a text input dialog and show it
        alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        alert.setMessage(message);
        EditText editText = new EditText(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        alert.setView(editText);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                        dialog.cancel();
                        mResult = false;
                        handler.sendMessage(handler.obtainMessage());
                    }
                });
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                        mResult = true;
                        handler.sendMessage(handler.obtainMessage());
                    }
                });
        alert.setCancelable(false);
        alert.show();
        try {
            Looper.loop();
        } catch (RuntimeException e3) {
            e3.getMessage();
        }

        if (mResult) {
            texteARetourner = (editText.getText().toString().equals("") ? null : editText.getText().toString());

            if (texteARetourner == null) {
                texteARetourner = "";
            }
            // Tester si le nom contient autre chose que des espaces
            if (texteARetourner.trim().length() == 0) {
                texteARetourner = null;
            }

        } else {
            texteARetourner = null;
        }
        return texteARetourner;
    }
    public static String afficherAlerteInfoCommantaire(Context context, String commentaire) {
        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        // make a text input dialog and show it
        alert = new AlertDialog.Builder(context).create();
        alert.setTitle("Commentaire");
        // alert.setMessage(message);
        EditText editText = new EditText(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        editText.setText(commentaire);
        editText.setPadding(40, 40, 40, 40);

        alert.setView(editText);

        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mResult = false;
                        handler.sendMessage(handler.obtainMessage());
                    }
                });
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mResult = true;
                        handler.sendMessage(handler.obtainMessage());
                    }
                });
        alert.setCancelable(false);
        alert.show();
        try {
            Looper.loop();
        } catch (RuntimeException e3) {
            e3.getMessage();
        }

        if (mResult) {
            texteARetourner = (editText.getText().toString().equals("") ? null : editText.getText().toString());

            if (texteARetourner == null) {
                texteARetourner = "";
            }
            // Tester si le nom contient autre chose que des espaces
            if (texteARetourner.trim().length() == 0) {
                texteARetourner = "";
            }

        } else {
            texteARetourner = "";
        }
        return texteARetourner;
    }
    public static String afficherAlerteListView(Context context, String title, final List<String> liste) {
        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        // make a text input dialog and show it
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final String[] tableau = liste.toArray(new String[liste.size()]);

        builder.setItems(tableau, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                texteARetourner = tableau[which];
                mResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });


        alert = builder.create();
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Annuler",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mResult = false;
                        handler.sendMessage(handler.obtainMessage());
                    }
                });

        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Continuer",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mResult = true;
                        handler.sendMessage(handler.obtainMessage());
                    }
                });

        alert.show();
        try {
            Looper.loop();
        } catch (RuntimeException e3) {
        }

        if (mResult) {
            // Tester si le nom contient autre chose que des espaces
            if (texteARetourner.trim().length() == 0) {
                texteARetourner = null;
            }
        } else {
            texteARetourner = null;
        }
        return texteARetourner;
    }
    public static boolean afficherAlerteList(Context context, String title, String message, final List<String> liste, String type) {
        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        // make a text input dialog and show it
        alert = new AlertDialog.Builder(context).create();
        alert.setTitle(title);
        for (int i = 0; i < liste.size(); i++) {
            message = message + "\n\n" + liste.get(i);
        }
        alert.setMessage(message);

        if (type.equals("alerte")) {
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mResult = true;
                            dialog.dismiss();
                        }
                    });
        } else if (type.equals("OuiNon")) {
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Non",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mResult = false;
                            dialog.dismiss();
                        }
                    });
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Oui",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mResult = true;
                            dialog.dismiss();
                        }
                    });
        }
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.show();

        // loop till a runtime exception is triggered.
        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }

        return mResult;
    }

    public static void afficherAlerteNumberPicker(final Context context, String title, String message, int value, int maxValue, DialogInterface.OnClickListener onClickListener) {

        RelativeLayout linearLayout = new RelativeLayout(context);
        aNumberPicker = new NumberPicker(context);
        aNumberPicker.setMaxValue(maxValue);
        aNumberPicker.setValue(value);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker, numPicerParams);

        aNumberPicker.requestFocus();
        aNumberPicker.performClick();
        final InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok", onClickListener)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void afficherAlerteNumberPickerAvecPas(final Context context, String title, String message, int value, int maxValue, DialogInterface.OnClickListener onClickListener, final int pas) {

        RelativeLayout linearLayout = new RelativeLayout(context);
        aNumberPicker = new NumberPicker(context);
        //aNumberPicker.setMaxValue(maxValue);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker, numPicerParams);

        String[] values = new String[maxValue/pas+1];
        int positionValue = 0;

        for (int i = 0; i < values.length; i++) {
            int numberint = i*pas;
            String number = Integer.toString(numberint);
            values[i] = number;
            if(numberint == value)
            {
                positionValue = i;
            }
        }

        aNumberPicker.setMaxValue(values.length-1);
        aNumberPicker.setDisplayedValues(values);

        aNumberPicker.setMinValue(0);
        aNumberPicker.setValue(positionValue);

        aNumberPicker.requestFocus();
        aNumberPicker.performClick();

        ((EditText) aNumberPicker.getChildAt(0)).setInputType(InputType.TYPE_CLASS_PHONE);
        final InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok", onClickListener)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public static void afficherAlerteInformation(Context context, LayoutInflater inflater, String titre, String message, boolean retour, boolean navigation)
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_information, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        TextView messageTextView = (TextView) layout.findViewById(R.id.messageFin);
        TextView titreTextView = (TextView) layout.findViewById(R.id.titre);
        titreTextView.setText(titre);
        messageTextView.setText(message);
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            if(retour)
            {
                ((Activity) context).finish();
            }

            if(navigation)
            {
                if (((Activity) context) instanceof RetourServiceListener) {
                    ((RetourServiceListener) ((Activity) context)).retourNavigation();
                }
            }
        });
    }
    public static void afficherAlerteConfirmation(Context context, LayoutInflater inflater, final Bundle bundle, String message, boolean retourService, boolean confirmation, Activity activity)
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageTextView = (TextView) layout.findViewById(R.id.messageFin);
        messageTextView.setText(message);
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            if(retourService)
            {
                if (activity instanceof RetourServiceListener) {
                    ((RetourServiceListener) activity).retourService(bundle);
                }
            }

            if(confirmation)
            {
                if(activity instanceof ConfirmationServiceListener) {
                    ((ConfirmationServiceListener) activity).confirmationService();
                }
            }

        });

        buttonAnnuler.setOnClickListener(v -> alertDialog.dismiss());
    }

    public static void afficherAlerteTripleChoix(Context context, LayoutInflater inflater, final Bundle bundle, String titre, String message, String text1, String text2, String text3, Activity activity)
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_choix_triple, null);

        LinearLayout zone1_LL = (LinearLayout) layout.findViewById(R.id.zone1);
        LinearLayout zone2_LL = (LinearLayout) layout.findViewById(R.id.zone2);
        LinearLayout zone3_LL = (LinearLayout) layout.findViewById(R.id.zone3);
        TextView text1_TV = (TextView) layout.findViewById(R.id.text1);
        TextView text2_TV = (TextView) layout.findViewById(R.id.text2);
        TextView text3_TV = (TextView) layout.findViewById(R.id.text3);
        TextView messageTextView = (TextView) layout.findViewById(R.id.messageFin);
        messageTextView.setText(message);
        text1_TV.setText(text1);
        text2_TV.setText(text2);
        text3_TV.setText(text3);
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        zone1_LL.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        zone2_LL.setOnClickListener(v -> alertDialog.dismiss());

        zone3_LL.setOnClickListener(v -> alertDialog.dismiss());
    }

    public static void afficherAlerteChoixDate(Activity activity, LayoutInflater inflater) {
        Locale localeFr = new Locale("fr", "FR");
        Locale.setDefault(localeFr);

        Configuration config = new Configuration(activity.getResources().getConfiguration());
        config.setLocale(localeFr);
        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());

        View layout = LayoutInflater.from(activity).inflate(R.layout.alerte_choix_date, null);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);

        LinearLayout zoneOk = layout.findViewById(R.id.buttonOk);
        LinearLayout zoneCancel = layout.findViewById(R.id.buttonAnnuler);
        DatePicker datePicker = layout.findViewById(R.id.datepicker);

        datePicker.setCalendarViewShown(false);
        datePicker.setSpinnersShown(true);

        Calendar aujourdHui = Calendar.getInstance(localeFr);
        datePicker.init(aujourdHui.get(Calendar.YEAR),
                aujourdHui.get(Calendar.MONTH),
                aujourdHui.get(Calendar.DAY_OF_MONTH),
                null);

        builder.setView(layout);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        zoneOk.setOnClickListener(v -> {
            int jour = datePicker.getDayOfMonth();
            int mois = datePicker.getMonth() + 1;
            int annee = datePicker.getYear();
            String dateSelectionnee = String.format("%02d/%02d/%04d", jour, mois, annee);
            Toast.makeText(activity, "Date : " + dateSelectionnee, Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });

        zoneCancel.setOnClickListener(v -> alertDialog.dismiss());
    }

    public static void afficherAlertePlageDate(Activity activity, LayoutInflater inflater) {
        Locale localeFr = new Locale("fr", "FR");
        Locale.setDefault(localeFr);

        Configuration config = new Configuration(activity.getResources().getConfiguration());
        config.setLocale(localeFr);
        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());

        View layout = LayoutInflater.from(activity).inflate(R.layout.alerte_plage_date, null);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);

        LinearLayout zoneOk = layout.findViewById(R.id.buttonOk);
        LinearLayout zoneCancel = layout.findViewById(R.id.buttonAnnuler);
        EditText date1_ET = layout.findViewById(R.id.date1);
        EditText date2_ET = layout.findViewById(R.id.date2);

        //Mise en place des dates par défaut
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateAujourdhui = dateFormat.format(new Date());
        date1_ET.setText(dateAujourdhui);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        String dateN1 = dateFormat.format(cal.getTime());
        date2_ET.setText(dateN1);

        builder.setView(layout);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        //gestion des clics sur les edit text
        date1_ET.setOnClickListener(v -> {
            int annee = cal.get(Calendar.YEAR);
            int mois = cal.get(Calendar.MONTH);
            int jour = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    v.getContext(),
                    (view, anneeTemp, moisTemp, jourTemp) -> {
                        cal.set(anneeTemp, moisTemp, jourTemp);
                        date1_ET.setText(dateFormat.format(cal.getTime()));
                    }, annee, mois, jour);

            // Optionnel : forcer le style spinner en français
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getDatePicker().setSpinnersShown(true);

            datePickerDialog.show();
        });

        date2_ET.setOnClickListener(v -> {
            int annee = cal.get(Calendar.YEAR);
            int mois = cal.get(Calendar.MONTH);
            int jour = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    v.getContext(),
                    (view, anneeTemp, moisTemp, jourTemp) -> {
                        cal.set(anneeTemp, moisTemp, jourTemp);
                        date2_ET.setText(dateFormat.format(cal.getTime()));
                    }, annee, mois, jour);

            // Optionnel : forcer le style spinner en français
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.getDatePicker().setSpinnersShown(true);

            datePickerDialog.show();
        });

        //clic sur les bouton de validation
        zoneOk.setOnClickListener(v -> {
            String date1 = date1_ET.getText().toString();
            String date2 = date2_ET.getText().toString();
            alertDialog.dismiss();
        });

        zoneCancel.setOnClickListener(v -> alertDialog.dismiss());
    }

    public static void afficherAlerteSaisieText(Activity activity, LayoutInflater inflater, String titre, String message, String placeholder) {
        View layout = LayoutInflater.from(activity).inflate(R.layout.alerte_saisie_text, null);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        LinearLayout zoneOk = layout.findViewById(R.id.buttonOk);
        LinearLayout zoneCancel = layout.findViewById(R.id.buttonAnnuler);
        TextView message_TV = layout.findViewById(R.id.messageText);
        TextView titre_TV = layout.findViewById(R.id.titre);
        EditText text_ET = layout.findViewById(R.id.text);

        titre_TV.setText(titre);
        message_TV.setText(message);
        text_ET.setHint(placeholder);

        builder.setView(layout);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        //clic sur les bouton de validation
        zoneOk.setOnClickListener(v -> {
            String text = text_ET.getText().toString();
            if(activity instanceof SaisieTextListener) {
                ((SaisieTextListener) activity).retourSaisieText(text);
            }
            alertDialog.dismiss();
        });

        zoneCancel.setOnClickListener(v -> alertDialog.dismiss());
    }
}

