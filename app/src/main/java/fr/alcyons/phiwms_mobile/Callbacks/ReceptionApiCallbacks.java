package fr.alcyons.phiwms_mobile.Callbacks;

/**
 * Callbacks partagés par ReceptionPuiApiRequest et ReceptionPadApiRequest.
 *
 * Extrait ici pour éviter que ReceptionPadApiRequest ne dépende de
 * ReceptionPuiApiRequest juste pour accéder à ses interfaces internes.
 */
public interface ReceptionApiCallbacks {

    interface OnSuccessCallback {
        void onTerminee();
    }

    interface OnErreurCallback {
        void onErreur(String messageUtilisateur);
    }
}