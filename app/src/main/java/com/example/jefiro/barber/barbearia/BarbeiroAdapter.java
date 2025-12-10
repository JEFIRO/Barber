package com.example.jefiro.barber.barbearia;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.jefiro.barber.R;


import java.util.List;

public class BarbeiroAdapter extends ArrayAdapter<Barbeiro> {

    public BarbeiroAdapter(@NonNull Context context, List<Barbeiro> barbeiros) {
        super(context, 0, barbeiros);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return criarView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return criarView(position, convertView, parent);
    }

    private View criarView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_barbeiro_spinner, parent, false);
        }

        Barbeiro b = getItem(position);

        ImageView img = convertView.findViewById(R.id.cardImage);
        TextView txtNome = convertView.findViewById(R.id.cardTitle);

        txtNome.setText(b.getNome());


        if (b.getFotoBarbeiro() != null && !b.getFotoBarbeiro().isEmpty()) {
                Glide.with(img.getContext())
                        .load(b.getFotoBarbeiro())
                        .centerCrop()
                        .circleCrop()
                        .into(img);

        }

        return convertView;
    }
}