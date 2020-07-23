package com.example.proyecto_semestral_checkpoint.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_semestral_checkpoint.R;

import java.util.ArrayList;

public class CategoryMenuAdapter extends RecyclerView.Adapter<CategoryMenuAdapter.ViewHolder> {

    //variables
    private ArrayList<String> cNames = new ArrayList<>();
    private ArrayList<Integer> cImages = new ArrayList<>();
    private Context cContext;

    private onItemClickListener cListener;


    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        cListener = listener;
    }


    public CategoryMenuAdapter(Context cContext, ArrayList<String> cNames, ArrayList<Integer> cImages) {
        this.cNames = cNames;
        this.cImages = cImages;
        this.cContext = cContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.category_image);
            name = itemView.findViewById(R.id.category_name);

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_menu_listview, parent, false);
        return new ViewHolder(view, cListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(cNames.get(position));
        holder.image.setImageResource(cImages.get(position));
    }

    @Override
    public int getItemCount() {
        return cNames.size();
    }

}
