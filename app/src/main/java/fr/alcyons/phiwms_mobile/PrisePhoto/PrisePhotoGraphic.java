package fr.alcyons.phiwms_mobile.PrisePhoto;

import android.graphics.Canvas;

import com.google.android.gms.vision.barcode.Barcode;

import fr.alcyons.phiwms_mobile.PrisePhoto.camera.GraphicOverlay;

/**
 * Created by jessica on 08/11/2017.
 */

public class PrisePhotoGraphic extends GraphicOverlay.Graphic {

    private int mId;
    private volatile Barcode mBarcode;

    PrisePhotoGraphic(GraphicOverlay overlay) {
        super(overlay);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public Barcode getBarcode() {
        return mBarcode;
    }

    /**
     * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateItem(Barcode barcode) {
        mBarcode = barcode;
        postInvalidate();
    }

    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;
        if (barcode == null) {
            return;
        }
    }
}
