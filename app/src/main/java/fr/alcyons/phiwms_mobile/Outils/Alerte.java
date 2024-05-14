package fr.alcyons.phiwms_mobile.Outils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
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

    public static String afficherAlerteEditTextNumber(final Context context, String title, String message) {
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
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
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

    public static boolean afficherAlerteListViewPreparation(Context context, String title, final List<String> liste) {
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

        return mResult;
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

    public static List<Produit> rechercheMedicamentParCode(final Context context, final ProduitOpenHelper gestionnaireBDD, final SQLiteDatabase db, String title, String message) {
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
        final EditText editText = new EditText(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);

        produitsCorrespondants = new ArrayList<>();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().endsWith("\n")) {
                    Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(editText.getText().toString());
                    if (gs1Decoupe.size() != 0) {
                        produitsCorrespondants = gestionnaireBDD.getMedicamentsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                    }
                    mResult = true;
                    handler.sendMessage(handler.obtainMessage());
                }
            }
        });

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

        alert.show();
        try {
            Looper.loop();
        } catch (RuntimeException e3) {
        }

        alert.cancel();
        return mResult ? produitsCorrespondants : null;
    }

    public static List<Produit> rechercheDispositifParCode(final Context context, final ProduitOpenHelper gestionnaireBDD, final SQLiteDatabase db, String title, String message) {
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
        final EditText editText = new EditText(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);

        produitsCorrespondants = new ArrayList<>();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().endsWith("\n")) {
                    Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(editText.getText().toString());
                    if (gs1Decoupe.size() != 0) {
                        produitsCorrespondants = gestionnaireBDD.getMedicamentsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                    }
                    mResult = true;
                    handler.sendMessage(handler.obtainMessage());
                }
            }
        });

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

        alert.show();
        try {
            Looper.loop();
        } catch (RuntimeException e3) {
        }

        alert.cancel();
        return mResult ? produitsCorrespondants : null;
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




    public static void afficherAlerteInfoDepot(Context context, LayoutInflater inflater, String identiteDepot, String adressedepot, String telephone, String fax) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_info_depot, null);

        TextView referenceDepot = (TextView) layout.findViewById(R.id.referenceDepot);
        referenceDepot.setText(identiteDepot);
        TextView adresseDepot = (TextView) layout.findViewById(R.id.adresseDepot);
        adresseDepot.setText(adressedepot);
        TextView telephonedepot = (TextView) layout.findViewById(R.id.telephoneDepot);
        telephonedepot.setText(telephone);
        TextView faxdepot = (TextView) layout.findViewById(R.id.faxDepot);
        faxdepot.setText(fax);
        ImageView iconFax = (ImageView) layout.findViewById(R.id.iconFax);
        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.zoneOK);
        builder.setView(layout);

        if (fax.equals("")) {
            faxdepot.setVisibility(View.INVISIBLE);
            iconFax.setVisibility(View.INVISIBLE);
        }

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public static HashMap<Boolean, Boolean> afficherAlerteBoolean(final Context context, String title, String message) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        HashMap<Boolean, Boolean> resultat = new HashMap<>();
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
        CheckBox checkBox = new CheckBox(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        checkBox.setLayoutParams(lp);
        checkBox.setHint("Avoir attendu");
        alert.setView(checkBox);
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
            if(checkBox.isChecked())
            {
                resultat.put(mResult, true);
            }
            else
            {
                resultat.put(mResult, false);
            }

        }
        return resultat;
    }
}
