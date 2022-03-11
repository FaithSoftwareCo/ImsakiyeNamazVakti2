package com.faith.namazvakti.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faith.namazvakti.R;
import com.faith.namazvakti.activities.PlaceSelectionActivity;

/**
 * Created by akif on 08/05/16.
 */
public class NoPlacesFoundFragment extends Fragment implements View.OnClickListener {
    public NoPlacesFoundFragment() {}

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_noplacesfound, container, false);

        FloatingActionButton floatingActionButtonAddPlace = (FloatingActionButton) layout.findViewById(R.id.fab_addPlace);

        floatingActionButtonAddPlace.setOnClickListener(this);

        return layout;
    }

    @Override public void onClick(View v) {
        startActivity(new Intent(getActivity(), PlaceSelectionActivity.class));
    }
}
