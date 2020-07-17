package com.example.proyecto_semestral_checkpoint.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.adapter.CategoryMenuAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ArrayList<String> cNames = new ArrayList<>();
    private ArrayList<Integer> cImages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(!cNames.isEmpty()) {
            cNames.clear();
            cImages.clear();
        }

        initCategoryMenu();
    }

    private void initCategoryMenu() {
        String[] categories = new String[]{"Soup", "Appetizer", "Salads", "Breads ", "Drinks", "Desserts", "Main Dish", "Side Dish"};
        int[] categories_images = new int[]{R.drawable.soup, R.drawable.appetizer, R.drawable.salad, R.drawable.bread, R.drawable.drinks, R.drawable.dessert, R.drawable.main_dish, R.drawable.side_dish};

        for(int i = 0; i < categories.length; i++){
            cNames.add(categories[i]);
            cImages.add(categories_images[i]);
        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = getView().findViewById(R.id.category_view);
        recyclerView.setLayoutManager(layoutManager);
        CategoryMenuAdapter adapter = new CategoryMenuAdapter(getContext(), cNames, cImages);
        recyclerView.setAdapter(adapter);
    }
}
