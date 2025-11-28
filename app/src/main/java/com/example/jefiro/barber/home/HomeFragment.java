package com.example.jefiro.barber.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.jefiro.barber.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ShapeableImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profileImage = view.findViewById(R.id.profileImage);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();

            db.collection("Clientes").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fotoUrl = documentSnapshot.getString("fotoUrl");

                            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(fotoUrl)
                                        .circleCrop()
                                        .into(profileImage);
                            }
                        }else {
                            // adicionar exessao
                            return;
                        }
                    });
        }
    }
}
