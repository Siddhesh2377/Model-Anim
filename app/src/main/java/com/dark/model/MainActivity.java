package com.dark.model;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.dark.model.databinding.ActivityMainBinding;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Node modelNode;
    ObjectAnimator silent, surprise;
    boolean start = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.transparentSceneView.setTransparent(true);
        loadModels();


        binding.slider.addOnChangeListener((slider, value, fromUser) -> {
            // Update position as the slider value changes
            modelNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0f, -(value), 0f), 35));
        });


        binding.Silent.setOnClickListener(view -> {
            if (silent.isPaused() || !silent.isRunning()) {
                // Ensure Surprise animation is stopped smoothly
                surprise.end();
                surprise.removeAllListeners(); // Remove lingering listeners for smoothness

                // Start Silent animation
                silent.start();
            }
        });

        binding.Surprise.setOnClickListener(view -> {
            if (surprise.isPaused() || !surprise.isRunning()) {
                // Ensure Silent animation is stopped smoothly
                silent.end();
                silent.removeAllListeners();

                // Start Surprise animation
                surprise.start();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            binding.transparentSceneView.resume();
        } catch (Exception e) {
            Log.e("MainActivity", Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.transparentSceneView.pause();
    }

    public void loadModels() {
        CompletableFuture<ModelRenderable> backdrop = ModelRenderable.builder().setSource(this, Uri.parse("face.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).setAnimationFrameRate(60).build();

        CompletableFuture.allOf(backdrop).handle((ok, ex) -> {
            try {

                modelNode = new Node();
                modelNode.setRenderable(backdrop.get());
                modelNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
                modelNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0f, -3.08f, 0f), 35));
                modelNode.setLocalPosition(new Vector3(0f, -1f, -3.5f));
                binding.transparentSceneView.getScene().addChild(modelNode);
                silent = modelNode.getRenderableInstance().animate("silent");
                silent.setRepeatMode(ValueAnimator.RESTART);

                surprise = modelNode.getRenderableInstance().animate("surprise");
                surprise.setRepeatMode(ValueAnimator.RESTART);

            } catch (InterruptedException | ExecutionException ignore) {

            }
            return null;
        });
    }
}
