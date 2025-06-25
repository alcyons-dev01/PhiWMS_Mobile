package fr.alcyons.phiwms_mobile.PrisePhoto.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraXSource {
    private final Context context;
    private final CameraXSourcePreview previewView;
    private final LifecycleOwner lifecycleOwner;
    private ImageCapture imageCapture;
    private final ExecutorService cameraExecutor;
    private boolean negativeEffect = false;

    public interface PictureCallback {
        void onPictureTaken(byte[] data);
    }

    public CameraXSource(Context context, CameraXSourcePreview previewView, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.previewView = previewView;
        this.lifecycleOwner = lifecycleOwner;
        previewView.post(() -> {
            int rotation = Surface.ROTATION_0;
            if (previewView.getDisplay() != null) {
                rotation = previewView.getDisplay().getRotation();
            }

            this.imageCapture = new ImageCapture.Builder()
                    .setTargetRotation(rotation)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build();

            startCamera(); // méthode qui bind Preview + ImageCapture
        });

        this.cameraExecutor = Executors.newSingleThreadExecutor();
    }

    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder()
                        .build();

                //preview.setSurfaceProvider(previewView.getSurfaceProvider());
                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                );
            } catch (Exception e) {
                Log.e("CameraX", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    public void setNegativeEffect(boolean enabled) {
        this.negativeEffect = enabled;
    }

    public void takePicture(@NonNull PictureCallback callback) {
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(
                new ByteArrayOutputStream()
        ).build();

        imageCapture.takePicture(ContextCompat.getMainExecutor(context),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        byte[] data = imageProxyToByteArray(image);
                        if (negativeEffect) {
                            data = applyNegativeEffect(data);
                        }
                        callback.onPictureTaken(data);
                        image.close();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraX", "Photo capture failed: ${exception.getMessage()}", exception);
                    }
                });
    }

    private byte[] imageProxyToByteArray(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }

    private byte[] applyNegativeEffect(byte[] jpegData) {
        Bitmap original = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
        Bitmap negative = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);

        int width = original.getWidth();
        int height = original.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = original.getPixel(x, y);
                int red = 255 - ((pixel >> 16) & 0xFF);
                int green = 255 - ((pixel >> 8) & 0xFF);
                int blue = 255 - (pixel & 0xFF);
                int alpha = (pixel >> 24) & 0xFF;
                negative.setPixel(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        negative.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }
}
