package com.example.seproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import android.graphics.Color;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VisitorScannerActivity extends AppCompatActivity {

    private ImageView btnBack, ivQrPlaceholder;
    private EditText etVisitorCode;
    private AppCompatButton btnValidateCode;
    private PreviewView viewFinder;
    private ExecutorService cameraExecutor;
    private boolean isScanning = true;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) startCamera();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_scan_qr_guard);

        btnBack = findViewById(R.id.btn_back);
        etVisitorCode = findViewById(R.id.et_visitor_code);
        btnValidateCode = findViewById(R.id.btn_validate_code);
        ivQrPlaceholder = findViewById(R.id.iv_qr_placeholder);
        viewFinder = findViewById(R.id.view_finder);
        cameraExecutor = Executors.newSingleThreadExecutor();

        btnBack.setOnClickListener(v -> finish());
        btnValidateCode.setOnClickListener(v -> {
            String inputCode = etVisitorCode.getText().toString().trim();
            if (!inputCode.isEmpty()) validateVisitorCode(inputCode);
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                ProcessCameraProvider provider = ProcessCameraProvider.getInstance(this).get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
                analysis.setAnalyzer(cameraExecutor, this::scanBarcodes);

                provider.unbindAll();
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis);

                ivQrPlaceholder.setVisibility(android.view.View.GONE);
                viewFinder.setVisibility(android.view.View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, "Camera Error", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void scanBarcodes(ImageProxy imageProxy) {
        if (!isScanning || imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
        BarcodeScanning.getClient().process(image)
                .addOnSuccessListener(barcodes -> {
                    if (!barcodes.isEmpty() && isScanning) {
                        isScanning = false;
                        runOnUiThread(() -> validateVisitorCode(barcodes.get(0).getRawValue()));
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void validateVisitorCode(String code) {
        FirebaseFirestore.getInstance().collection("requests")
                .whereEqualTo("passId", code)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        com.google.firebase.firestore.DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String documentId = querySnapshot.getDocuments().get(0).getId();

                        String currentStatus = document.getString("requestStatus");
                        if ("Approved".equals(currentStatus)) {
                            Snackbar.make(viewFinder, "ENTRY ALREADY CHECKED IN!", 2000).setBackgroundTint(Color.RED).setTextColor(Color.WHITE).show();
                            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> isScanning = true, 3000);
//                            isScanning = true;
                            return;
                        }
                        FirebaseFirestore.getInstance()
                                .collection("requests")
                                .document(documentId)
                                .update("requestStatus", "Approved", "checkedInAtMillis", System.currentTimeMillis())
                                .addOnSuccessListener(unused -> {
                                    Snackbar.make(viewFinder, "Checked in Successfully!", 2000).setBackgroundTint(Color.GREEN).setTextColor(Color.WHITE).show();
                                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> isScanning = true, 3000);
//                                    isScanning = true;
                                })
                                .addOnFailureListener(e -> {
                                    Snackbar.make(viewFinder, "Failed to Check In!", 2000).setBackgroundTint(Color.RED).setTextColor(Color.WHITE).show();
                                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> isScanning = true, 3000);
//                                    isScanning = true;
                                });
                    } else {
                        Snackbar.make(viewFinder, "Invalid Code!", 2000).setBackgroundTint(Color.RED).setTextColor(Color.WHITE).show();
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> isScanning = true, 5000);
//                        isScanning = true;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}