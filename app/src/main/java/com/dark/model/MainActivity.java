package com.dark.model;

import android.animation.ObjectAnimator;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dark.model.databinding.ActivityMainBinding;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Node modelNode;
    ObjectAnimator animator;
    boolean start = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.transparentSceneView.setTransparent(true);
        loadModels();

        binding.startStop.setOnClickListener(view -> {
            if (start) {
                animator.pause();
                binding.startStop.setText("Start");
                binding.startStop.setIconResource(R.drawable.rounded_play_circle_24);
            } else {
                if (animator.isPaused()) animator.resume();
                else animator.start();
                binding.startStop.setText("Stop");
                binding.startStop.setIconResource(R.drawable.rounded_stop_circle_24);
            }
            start = !start;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            binding.transparentSceneView.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.transparentSceneView.pause();
    }

    public void loadModels() {
        CompletableFuture<ModelRenderable> backdrop = ModelRenderable.builder().setSource(this, Uri.parse("animCube.glb")).setIsFilamentGltf(true).setAsyncLoadEnabled(true).setAnimationFrameRate(60).build();

        CompletableFuture.allOf(backdrop).handle((ok, ex) -> {
            try {

                modelNode = new Node();
                modelNode.setRenderable(backdrop.get());
                modelNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
                modelNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 35));
                modelNode.setLocalPosition(new Vector3(0f, 0f, -1.0f));
                binding.transparentSceneView.getScene().addChild(modelNode);
                animator = modelNode.getRenderableInstance().animate(false);

            } catch (InterruptedException | ExecutionException ignore) {

            }
            return null;
        });
    }
}
