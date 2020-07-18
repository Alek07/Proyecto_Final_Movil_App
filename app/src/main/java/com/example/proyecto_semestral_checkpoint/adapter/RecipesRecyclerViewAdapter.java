package com.example.proyecto_semestral_checkpoint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.models.Recipe;

import java.util.ArrayList;

public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.ViewHolder> {

    //variables
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private Context cContext;

    public RecipesRecyclerViewAdapter(Context cContext, ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        this.cContext = cContext;
    }

    public void setRecipesList (ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView upvotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recipe_image);
            name = itemView.findViewById(R.id.recipe_name);
            upvotes = itemView.findViewById(R.id.recipe_upvotes);
        }
    }

    @NonNull
    @Override
    public RecipesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipes_listview, parent, false);
        return new RecipesRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesRecyclerViewAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.name.setText(recipe.getName());
        holder.upvotes.setText(Integer.toString(recipe.getUpvotes()));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
