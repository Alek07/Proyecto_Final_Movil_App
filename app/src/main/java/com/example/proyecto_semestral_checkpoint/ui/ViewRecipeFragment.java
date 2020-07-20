package com.example.proyecto_semestral_checkpoint.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_semestral_checkpoint.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewRecipeFragment extends Fragment {

    public ViewRecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_recipe, container, false);
    }
}
