package com.example.proyecto_semestral_checkpoint.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.models.Recipe;
import com.example.proyecto_semestral_checkpoint.models.User;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;
import com.example.proyecto_semestral_checkpoint.util.DoubleClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewRecipeFragment extends Fragment {

    private Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);
    private String[] categories = new String[]{"Soup", "Appetizer", "Salads", "Breads ", "Drinks", "Desserts", "Main Dish", "Side Dish"};

    private User user;

    private boolean isFavorite = false;


    private TextView Name, NameLabel, Ingredients, Description, Category;
    private EditText NameE, IngredientsE, DescriptionE;
    private ImageView RecipeImage, Edit, Cancel, Delete, Favorite;
    private Spinner Categories;
    private Button Save;
    private Recipe recipe;

    public ViewRecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewRecipeFragmentArgs args = ViewRecipeFragmentArgs.fromBundle(getArguments());
        recipe = args.getRecipe();
        user = args.getUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeControls();
        setRecipeInfo();
        toggleEditMode();
        add_remove_favorite();
        validateInputs();
        deleteRecipe();

        SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        String author = settings.getString("_id", "null");

        //Enable edit mode for owner
        if (!recipe.getAuthor().equals(author)) {
            Edit.setVisibility(View.GONE);
            Delete.setVisibility(View.GONE);
        }

        //IsFavorites
        for(int i = 0; i < user.getFavorites().size(); i++){
            if(user.getFavorites().get(i).get("recipe").equals(recipe.get_id())) {
                isFavorite = true;
                Favorite.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initializeControls() {
        Name = getView().findViewById(R.id.name);
        Ingredients = getView().findViewById(R.id.ingredients);
        Description = getView().findViewById(R.id.description);
        Category = getView().findViewById(R.id.category);

        NameLabel = getView().findViewById(R.id.name_edit_label);

        NameE = getView().findViewById(R.id.name_edit);
        IngredientsE = getView().findViewById(R.id.ingredients_edit);
        DescriptionE = getView().findViewById(R.id.description_edit);
        Categories = getView().findViewById(R.id.category_edit);


        RecipeImage = getView().findViewById(R.id.recipe_image);
        Edit = getView().findViewById(R.id.edit);
        Cancel = getView().findViewById(R.id.cancel);
        Favorite = getView().findViewById(R.id.favorite);

        Save = getView().findViewById(R.id.btnSave);
        Delete = getView().findViewById(R.id.btnDelete);

        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, categories);
        Categories.setAdapter(adapter);
    }

    private void toggleEditMode() {
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name.setText("Edit Recipe");

                Ingredients.setVisibility(View.GONE);
                Description.setVisibility(View.GONE);
                Category.setVisibility(View.GONE);

                NameLabel.setVisibility(View.VISIBLE);

                NameE.setVisibility(View.VISIBLE);
                IngredientsE.setVisibility(View.VISIBLE);
                DescriptionE.setVisibility(View.VISIBLE);
                Categories.setVisibility(View.VISIBLE);

                Edit.setVisibility(View.GONE);
                Cancel.setVisibility(View.VISIBLE);

                Save.setVisibility(View.VISIBLE);
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name.setText(recipe.getName());

                Ingredients.setVisibility(View.VISIBLE);
                Description.setVisibility(View.VISIBLE);
                Category.setVisibility(View.VISIBLE);

                NameLabel.setVisibility(View.GONE);

                NameE.setVisibility(View.GONE);
                IngredientsE.setVisibility(View.GONE);
                DescriptionE.setVisibility(View.GONE);
                Categories.setVisibility(View.GONE);

                Edit.setVisibility(View.VISIBLE);
                Cancel.setVisibility(View.GONE);

                Save.setVisibility(View.GONE);
            }
        });
    }

    private void setRecipeInfo() {
        String category = "";
        ArrayList<HashMap<String, String>> lstingredients = recipe.getIngredients();
        String ingredients = "";

        //Convert category id to String
        for (int i = 0; i < categories.length; i++) {
            if (i + 1 == recipe.getCategory())
                category = categories[i];
        }

        //Convert list of ingredients to a String
        for (int j = 0; j < lstingredients.size(); j++) {
            if (j + 1 == lstingredients.size())
                ingredients += lstingredients.get(j).get("ingredient");
            else
                ingredients += lstingredients.get(j).get("ingredient") + ", ";
        }

        Name.setText(recipe.getName());
        Ingredients.setText(ingredients);
        Description.setText(recipe.getDescription());
        Category.setText(category);

        NameE.setText(recipe.getName());
        IngredientsE.setText(ingredients);
        DescriptionE.setText(recipe.getDescription());

    }

    private void validateInputs() {
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidName = !NameE.getText().toString().isEmpty();
                boolean isValidDescription = !DescriptionE.getText().toString().isEmpty();
                boolean isValidIngredients = !IngredientsE.getText().toString().isEmpty();

                ArrayList<HashMap<String, String>> ingredients = new ArrayList<>();

                //Check if their'r empty
                if (!isValidName) {
                    NameE.requestFocus();
                    NameE.setError("El nombre no puede estar vacío.");
                }
                if (!isValidDescription) {
                    DescriptionE.requestFocus();
                    DescriptionE.setError("La descripcion no puede estar vacío.");
                }
                if (!isValidIngredients) {
                    IngredientsE.requestFocus();
                    IngredientsE.setError("Los ingredientes no puede estar vacío.");
                } else {

                    //Check ingredients
                    String[] newIngredients = IngredientsE.getText().toString().split(",");

                    if (newIngredients.length > 5) {
                        isValidIngredients = false;
                        IngredientsE.requestFocus();
                        IngredientsE.setError("No puede agregar mas de 5 ingredientes");
                    }

                    //Add ingredients on array list
                    for (int j = 0; j < newIngredients.length; j++) {
                        HashMap<String, String> index = new HashMap<>();
                        index.put("ingredient", newIngredients[j]);
                        ingredients.add(index);
                    }

                    Log.d("INGREDIENTS", "onClick: " + ingredients);
                }

                //Convert category
                int category = 0;
                for (int i = 0; i < categories.length; i++) {
                    if (Categories.getSelectedItem().equals(categories[i])) {
                        category = i + 1;
                    }
                }

                if (isValidName && isValidDescription && isValidIngredients) {
                    Save.setEnabled(false);
                    updateRecipeRequest(ingredients, category);
                }

            }
        });
    }

    private void updateRecipeRequest(ArrayList<HashMap<String, String>> ingredients, int category) {
        SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        String author = settings.getString("_id", "null");
        String token = settings.getString("token", "");

        Recipe new_recipe = new Recipe(
                NameE.getText().toString(),
                category,
                DescriptionE.getText().toString(),
                null,
                ingredients
        );

        Call<Recipe> call = recipe_app_api.updateRecipe(recipe.get_id().toString(), "Bearer " + token, new_recipe);

        call.enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if (!response.isSuccessful()) {
                    Save.setEnabled(true);
                    Toast.makeText(getActivity(), "Update error, check your inputs", Toast.LENGTH_SHORT).show();
                    return;
                }

                recipe = response.body();

                refresh();
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                Save.setEnabled(true);
                Toast.makeText(getActivity(), "Something went wrong connecting to the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteRecipe() {
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
                String token = settings.getString("token", "");

                Call<Recipe> call = recipe_app_api.deleteRecipe(recipe.get_id().toString(), "Bearer " + token);

                call.enqueue(new Callback<Recipe>() {
                    @Override
                    public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(getActivity(), "Something went wrong while deleting the recipe", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.recipeFragment);
                    }

                    @Override
                    public void onFailure(Call<Recipe> call, Throwable t) {
                        Toast.makeText(getActivity(), "Something went wrong connecting to the server", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void add_remove_favorite() {
        RecipeImage.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick() {
                SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
                String token = settings.getString("token", "");

                Call<User> add = recipe_app_api.addFavorites(recipe.get_id(), token);
                Call<User> remove = recipe_app_api.removeFavorites(recipe.get_id(), token);

                if(!isFavorite){
                    add.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getActivity(), "Ups!, something went wrong", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            user = response.body();
                            Favorite.setVisibility(View.VISIBLE);
                            isFavorite = true;
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(getActivity(), "Something went wrong connecting to the server", Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
                    remove.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getActivity(), "Ups!, something went wrong", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            user = response.body();
                            Favorite.setVisibility(View.GONE);
                            isFavorite = false;
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(getActivity(), "Something went wrong connecting to the server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void refresh() {
        Name.setVisibility(View.VISIBLE);
        Ingredients.setVisibility(View.VISIBLE);
        Description.setVisibility(View.VISIBLE);
        Category.setVisibility(View.VISIBLE);

        NameE.setVisibility(View.GONE);
        IngredientsE.setVisibility(View.GONE);
        DescriptionE.setVisibility(View.GONE);
        Categories.setVisibility(View.GONE);

        Edit.setVisibility(View.VISIBLE);
        Cancel.setVisibility(View.GONE);

        Save.setEnabled(true);
        Save.setVisibility(View.GONE);

        onStart();
    }


}
