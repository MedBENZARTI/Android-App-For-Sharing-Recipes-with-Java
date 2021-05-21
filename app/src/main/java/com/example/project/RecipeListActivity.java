package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.Recipe;
import ui.RecipeRecyclerAdapter;
import util.RecipeApi;

public class RecipeListActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private List<Recipe> recipeList;
    private RecyclerView recyclerView;
    private RecipeRecyclerAdapter recipeRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Recipe");
    private TextView noRecipeEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        setContentView(R.layout.activity_recipe_list);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        noRecipeEntry = findViewById(R.id.listNoRecipe);
        recipeList = new ArrayList<Recipe>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add :
                //go to addRecipe activity
                if (user != null && firebaseAuth != null){
                    startActivity(new Intent(RecipeListActivity.this, PostRecipeActivity.class));
                    recipeList.clear();
                    //finish();
                }
                break;
            case R.id.action_signout :
                //sign out
                if (user != null && firebaseAuth != null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(RecipeListActivity.this, MainActivity.class));
                    finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot recipes: queryDocumentSnapshots){
                                Recipe recipe = recipes.toObject(Recipe.class);
                                recipeList.add(recipe);
                            }
                            //invoke recyclerView
                            recipeRecyclerAdapter = new RecipeRecyclerAdapter(RecipeListActivity.this, recipeList);
                            recyclerView.setAdapter(recipeRecyclerAdapter);
                            recipeRecyclerAdapter.notifyDataSetChanged();

                        }else{
                            noRecipeEntry.setVisibility(View.VISIBLE);

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}