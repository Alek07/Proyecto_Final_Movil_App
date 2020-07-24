package com.example.proyecto_semestral_checkpoint.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.models.Recipe;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.ViewHolder> {

    //variables
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private Context cContext;

    private onItemClickListener rListener;

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        rListener = listener;
    }

    public RecipesRecyclerViewAdapter(Context cContext, ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        this.cContext = cContext;
    }

    public void setRecipesList (ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView upvotes;

        public ViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.recipe_image);
            name = itemView.findViewById(R.id.recipe_name);
            upvotes = itemView.findViewById(R.id.recipe_upvotes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public RecipesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipes_listview, parent, false);
        return new RecipesRecyclerViewAdapter.ViewHolder(view, rListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesRecyclerViewAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.name.setText(recipe.getName());
        holder.upvotes.setText(Integer.toString(recipe.getUpvotes()));

        ArrayList<HashMap<String, ?>> images = recipe.getImages();

        if(images.size() > 0) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.food)
                    .error(R.drawable.food);

            Glide.with(cContext).load(ApiClient.getBaseUrl() + "recipes/" + recipe.get_id() + "/images/" + recipe.getImages().get(0).get("_id")).apply(options).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
