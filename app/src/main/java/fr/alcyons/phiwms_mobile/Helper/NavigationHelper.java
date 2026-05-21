package fr.alcyons.phiwms_mobile.Helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;

public final class NavigationHelper {

    private NavigationHelper() {}

    public static void allerVersNavigation(Context context, int utilisateurId) {
        Intent intent = new Intent(context, NavigationActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurId);
        intent.putExtras(extras);
        context.startActivity(intent);
    }
}