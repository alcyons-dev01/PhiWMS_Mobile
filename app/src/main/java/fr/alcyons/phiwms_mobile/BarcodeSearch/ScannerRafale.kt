package fr.alcyons.phiwms_mobile.BarcodeSearch

import android.app.Activity.RESULT_OK
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import fr.alcyons.phiwms_mobile.R
import fr.alcyons.phiwms_mobile.ServiceActivity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

abstract class ScannerRafale : ServiceActivity() {

    protected lateinit var previewView: PreviewView
    protected lateinit var btnFlash: ImageButton
    protected lateinit var btnExit: ImageButton
    protected lateinit var btnActiveSon: ImageButton
    protected lateinit var btnInactiveSon: ImageButton
    protected var isSoundOn = true

    private var flashOn = false
    private lateinit var cameraControl: CameraControl
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var scanExecutor: ExecutorService
    protected lateinit var switch : Switch

    protected abstract val layoutResId: Int

    private var part1: String? = null
    private var part1Time: Long = 0L
    private val windowMs = 1000L
    var type = ""

    protected var codeUnique = false
    val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)

        previewView = findViewById(R.id.previewView)
        btnFlash = findViewById(R.id.btnFlash)
        btnExit = findViewById(R.id.btnClose)
        btnActiveSon = findViewById(R.id.btnActiveSon)
        btnInactiveSon = findViewById(R.id.btnInactiveSon)
        switch = findViewById<Switch>(R.id.switchCodeUnique)

        scanExecutor = Executors.newFixedThreadPool(2)

        // Configuration ML Kit
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_DATA_MATRIX,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_QR_CODE
            )
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        if (!isSoundEnabled()) {
            isSoundOn = false
            btnActiveSon.visibility = View.GONE
            btnInactiveSon.visibility = View.VISIBLE
        }

        btnActiveSon.setOnClickListener {
            isSoundOn = true
            btnInactiveSon.visibility = View.VISIBLE
            btnActiveSon.visibility = View.GONE
        }

        btnInactiveSon.setOnClickListener {
            if (isSoundEnabled()) {
                isSoundOn = false
                btnInactiveSon.visibility = View.GONE
                btnActiveSon.visibility = View.VISIBLE
            }
        }

        btnFlash.setOnClickListener {
            flashOn = !flashOn
            cameraControl.enableTorch(flashOn)
        }

        btnExit.setOnClickListener {
            AlertDialog.Builder(this@ScannerRafale)
                .setTitle("Quitter ?")
                .setMessage("Voulez-vous vraiment quitter ?")
                .setPositiveButton("Oui") { _, _ -> retourNavigation() }
                .setNegativeButton("Non", null)
                .show()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@ScannerRafale)
                    .setTitle("Quitter ?")
                    .setMessage("Voulez-vous vraiment quitter ?")
                    .setPositiveButton("Oui") { _, _ -> retourNavigation() }
                    .setNegativeButton("Non", null)
                    .show()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        switch.trackDrawable?.mutate()?.setTint(Color.parseColor("#EC0200"))

        switch.setOnCheckedChangeListener { _, isChecked ->
            val trackDrawable = switch.trackDrawable?.mutate()

            if (isChecked) {
                codeUnique = true
                switch.trackTintList = ColorStateList.valueOf(Color.parseColor("#00CC00")) // vert vif
                switch.thumbTintList = ColorStateList.valueOf(Color.WHITE)
                trackDrawable?.setTint(Color.parseColor("#00CC00"))
            } else {
                codeUnique = false
                switch.trackTintList = ColorStateList.valueOf(Color.parseColor("#EC0200")) // rouge vif
                switch.thumbTintList = ColorStateList.valueOf(Color.WHITE)
                trackDrawable?.setTint(Color.parseColor("#EC0200"))
            }
            switch.trackDrawable = trackDrawable
        }

        startCamera()
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            previewView.scaleType = PreviewView.ScaleType.FILL_CENTER


            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .setTargetResolution(android.util.Size(1920, 1080))
                .build()
                .also {
                    it.setAnalyzer(scanExecutor) { imageProxy ->
                        analyzeImage(imageProxy)
                    }
                }

            val camera = cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalyzer
            )

            cameraControl = camera.cameraControl

        }, ContextCompat.getMainExecutor(this))
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun analyzeImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }

        val bitmap = imageProxy.toBitmap()
        val rotation = imageProxy.imageInfo.rotationDegrees

        val normalImage = InputImage.fromBitmap(bitmap, rotation)
        val invertedImage = InputImage.fromBitmap(invertBitmap(bitmap), rotation)

        var completed = 0
        var resultHandled = false

        val onComplete = {
            completed++
            if (completed == 2) imageProxy.close()
        }

        fun processImage(input: InputImage) {
            barcodeScanner.process(input)
                .addOnSuccessListener { barcodes ->
                    if (!resultHandled) {
                        barcodes.forEach { barcode ->
                            val raw = barcode.rawValue ?: return@forEach
                            val normalized = normalize(raw)
                            val assembled = if (normalized.uppercase().startsWith("PHITAG"))
                                normalized
                            else
                                handleSplitScan(raw) ?: return@forEach

                            resultHandled = true
                            runOnUiThread { onCodeScanned(assembled) }
                        }
                    }
                }
                .addOnCompleteListener { onComplete() }
        }

        processImage(normalImage)
        processImage(invertedImage)
    }

    private fun normalize(raw: String): String {
        return raw
            .replace("]C1", "")
            .replace("]e0", "")
            .replace("]d2", "")
            .replace("]Q3", "")
            .replace("}C1", "")
            .replace("}d2010", "")
            .replace("]d2010", "")
            .trim()
    }

    private fun handleSplitScan(raw: String): String? {
        val now = System.currentTimeMillis()
        val code = normalize(raw)

        if (isCompleteCode(code)) {
            part1 = null
            part1Time = 0L
            return code
        }

        if (part1 != null && (now - part1Time) > windowMs) {
            part1 = null
        }

        if (part1 == null || part1.contentEquals(code)) {
            if (!code.startsWith("01") && !code.startsWith("02") && !code.startsWith("+") && code.startsWith("+$")) return null
            type = when {
                code.startsWith("01") || code.startsWith("02") -> "GTIN"
                code.startsWith("+") -> "HIBC"
                else -> ""
            }
            part1 = code
            part1Time = now
            return null
        }

        var assembled = part1 + code
        if (type == "HIBC") {
            val p1 = part1 ?: return null
            assembled = p1.dropLast(1) + "/" + code.substring(1, code.length - 2)
        }

        part1 = null
        part1Time = 0L
        return assembled
    }

    private fun isCompleteCode(code: String): Boolean {
        if (code.length < 2) return false
        val firstChar = code[0]
        val secondChar = code[1]
        if (firstChar == '0' && (secondChar == '1' || secondChar == '2') && code.length > 16) return true
        if (firstChar == '+' && secondChar.isLetter() && code.contains('/')) return true
        return false
    }

    protected open fun onCodeScanned(code: String) {}

    override fun onDestroy() {
        super.onDestroy()
        scanExecutor.shutdown()
        toneGenerator.release()
        barcodeScanner.close()
    }

    override fun isSoundEnabled(): Boolean {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        return audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL
    }

    private fun invertBitmap(src: Bitmap): Bitmap {
        val inv = src.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(inv)
        val paint = Paint()
        val matrixColorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
            set(floatArrayOf(
                -1f,  0f,  0f, 0f, 255f,
                0f, -1f,  0f, 0f, 255f,
                0f,  0f, -1f, 0f, 255f,
                0f,  0f,  0f, 1f,   0f
            ))
        })
        paint.colorFilter = matrixColorFilter
        canvas.drawBitmap(src, 0f, 0f, paint)
        return inv
    }

    protected open fun getAlreadyScannedSet(): Set<String> = emptySet()
}