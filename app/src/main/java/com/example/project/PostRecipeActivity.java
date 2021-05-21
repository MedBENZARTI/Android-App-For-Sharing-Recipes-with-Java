package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import model.Recipe;
import util.RecipeApi;

public class PostRecipeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_CODE = 1;
    private static final String TAG = "PostRecipeActivity";
    private Button addBtn;
    private Button saveBtn;
    private ImageView addPhotoBtn;
    private ImageView imageView;
    private ProgressBar progressBar;
    private EditText recipeName;
    private EditText newIngredient;
    private ListView show;
    private final ArrayList<String> addArray = new ArrayList<>();

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to FireStore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private final CollectionReference collectionReference = db.collection("Recipe");
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_recipe);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        recipeName = findViewById(R.id.recipeName);
        newIngredient = findViewById(R.id.newIngredient);

        addBtn = findViewById(R.id.addButton);
        addBtn.setOnClickListener(this);
        saveBtn = findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(this);
        addPhotoBtn = findViewById(R.id.postCameraButton);
        addPhotoBtn.setOnClickListener(this);

        imageView = findViewById(R.id.recipe_imageView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        show = findViewById(R.id.ingredientList);

        if (RecipeApi.getInstance() != null){
            currentUserId = RecipeApi.getInstance().getUserId();
            currentUserName = RecipeApi.getInstance().getUsername();
        }else{
            currentUserId = "Guest";
            currentUserName = "Guest";
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                } else {
                }
            }
        };


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton:
                //we add an ingredient to our list
                String input = newIngredient.getText().toString();
                if (addArray.contains(input))
                    Toast.makeText(getBaseContext(), "Ingredient Already Added To The List", Toast.LENGTH_LONG).show();
                else if (input == null || input.trim().equals(""))
                    Toast.makeText(getBaseContext(), "Input Field Is Empty", Toast.LENGTH_LONG).show();
                else {
                    addArray.add(input);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PostRecipeActivity.this, android.R.layout.simple_expandable_list_item_1, addArray);
                    show.setAdapter(adapter);
                    ((EditText) findViewById(R.id.newIngredient)).setText("");
                }
                break;
            case R.id.saveButton:
                //save the recipe object
                saveRecipe();
                break;
            case R.id.postCameraButton:
                //get a photo from gallery
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;

        }

    }

    private void saveRecipe() {
        String recipeNameString = recipeName.getText().toString().trim();
        progressBar.setVisibility(View.INVISIBLE);
        if (!TextUtils.isEmpty(recipeNameString) && addArray.size() > 0 && imageUri != null) {
            StorageReference filePath = storageReference
                    .child("Recipe_Images")
                    .child("image-" + Timestamp.now().getSeconds());
            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //create a Recipe object
                                    String imageUrl = uri.toString();
                                    Recipe recipe = new Recipe();
                                    recipe.setRecipeName(recipeNameString);
                                    recipe.setUserId(currentUserId);
                                    recipe.setUserName(currentUserName);
                                    recipe.setImageUrl(imageUrl);
                                    recipe.setTimeAdded(new Timestamp(new Date()));
                                    recipe.setIngredients(addArray);
                                    //invoke collectionReference
                                    collectionReference.add(recipe)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    startActivity(new Intent(PostRecipeActivity.this, RecipeListActivity.class));
                                                    addArray.clear();
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                                }
                                            });

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}