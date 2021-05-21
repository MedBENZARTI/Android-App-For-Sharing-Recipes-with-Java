package ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import model.Recipe;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerAdapter.ViewHolder> {
    public RecipeRecyclerAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    private Context context;
    private List<Recipe> recipeList;


    @NonNull
    @Override
    public RecipeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_row, parent, false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeRecyclerAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        String imageUrl ;
        holder.title.setText(recipe.getRecipeName());
        holder.ingredients.setText(listToOne(recipe.getIngredients()));
        holder.name.setText(recipe.getUserName());
        imageUrl = recipe.getImageUrl();
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(recipe.getTimeAdded().getSeconds() * 1000);
        holder.dateAdded.setText(timeAgo);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.new_recipe)
                .fit()
                .into(holder.image);

    }


    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView
                title,
                ingredients,
                dateAdded,
                name;
        public ImageView image ;
        String userId;
        String userName;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            title = itemView.findViewById(R.id.Recipe_title_list);
            ingredients = itemView.findViewById(R.id.ingredients_list);
            dateAdded = itemView.findViewById(R.id.recipe_timestamp_list);
            name = itemView.findViewById(R.id.recipe_row_username);
            image = itemView.findViewById(R.id.recipe_image_list);


        }

        }
        private String listToOne(ArrayList<String> ingredients){
            String ingredientsString = "Ingredients:";
            for (String ing: ingredients) {
                ingredientsString = ingredientsString + "\n - " +ing + ".";
            }
            return ingredientsString;
    }
}
