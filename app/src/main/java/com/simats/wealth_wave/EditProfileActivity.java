package com.simats.wealth_wave;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.simats.wealth_wave.retrofit.ApiService;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etMobile;
    private TextView tvEmail;
    private AppCompatButton btnSave;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private de.hdodenhof.circleimageview.CircleImageView profileImage;
    private ActivityResultLauncher<String> pickImageLauncher;

    private SharedPreferences sharedPreferences;
    private String currentEmail;

    // Backend base URL (ensure trailing slash)
    private static final String BASE_URL = "http://10.24.93.232/app_database/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        tvEmail = findViewById(R.id.tvEmail);
        btnSave = findViewById(R.id.btnSave);
        profileImage = findViewById(R.id.profileImage);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Load current data from SharedPreferences
        currentEmail = sharedPreferences.getString("email", "");
        String currentName = sharedPreferences.getString("name", "");
        String currentMobile = sharedPreferences.getString("mobile", "");

        tvEmail.setText(currentEmail);
        etName.setText(currentName);
        etMobile.setText(currentMobile);

        // Load saved server imageUrl if exists (works across devices)
        int userId = sharedPreferences.getInt("user_id", 0);
        String userProfileKey = "profileImageUrl_" + userId;
        String activeProfileUrl = sharedPreferences.getString(userProfileKey, "");

        if (activeProfileUrl != null && !activeProfileUrl.isEmpty()) {
            Glide.with(this)
                    .load(activeProfileUrl.startsWith("http") ? activeProfileUrl : BASE_URL + activeProfileUrl)
                    .placeholder(R.drawable.user_grad)
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.user_grad);
        }



        // pick image from gallery (content URI)
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // instead of uploading directly, launch crop
                        startCrop(uri);
                    }
                }
        );

        // open gallery on tap
        profileImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newMobile = etMobile.getText().toString().trim();
            if (newName.isEmpty() || newMobile.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // If you have a server API to update name/mobile, call it here.
            // (You previously had updateProfileOnServer(...) — ensure API + endpoint exists).
            updateProfileOnServer(newName, newMobile);
        });

        // UI niceties
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile_bot);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home_bot) startActivity(new Intent(this, HomeActivity.class));
            if (id == R.id.nav_savings_plan_bot) startActivity(new Intent(this, SavingsActivity.class));
            else if (id == R.id.nav_investment_bot) startActivity(new Intent(this, ProgressTrackingActivity.class));
            else if (id == R.id.nav_profile_bot) startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
            return true;
        });

        AppCompatButton btnRemovePhoto = findViewById(R.id.btnRemovePhoto);

        btnRemovePhoto.setOnClickListener(v -> {
            // Reset locally
            profileImage.setImageResource(R.drawable.user_grad);
            sharedPreferences.edit()
                    .remove(userProfileKey)
                    .remove("activeProfileUrl")
                    .apply();

            // Call backend to remove (optional, depends if you want to clear from server too)
            removeProfileImageFromServer();
        });

        // status/nav bar color
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }

    private void removeProfileImageFromServer() {
        int userId = sharedPreferences.getInt("user_id", 0);
        if (userId == 0) return;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<JsonObject> call = apiService.removeProfileImage(userId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditProfileActivity.this, "Profile photo removed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri croppedUri = UCrop.getOutput(data);
            if (croppedUri != null) {
                // show preview
                profileImage.setImageURI(croppedUri);
                // upload cropped image
                uploadProfileImage(croppedUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "Crop error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startCrop(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));

        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(80);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false); // lock crop
        options.setCircleDimmedLayer(true);     // 🔵 show circular crop overlay
        options.setShowCropFrame(false);        // hide rectangle frame
        options.setShowCropGrid(false);         // hide grid
        options.setToolbarTitle("Crop Profile Photo");

        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1) // enforce square
                .withMaxResultSize(800, 800)
                .withOptions(options)
                .start(EditProfileActivity.this);
    }



    // Convert a content:// Uri to a real File in cache (works on all Android versions)
    private File uriToFile(Uri uri) {
        try {
            String fileName = queryName(this, uri);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File tempFile = new File(getCacheDir(), "upload_" + System.currentTimeMillis() + "_" + (fileName != null ? fileName : "img.jpg"));
            try (OutputStream out = new FileOutputStream(tempFile)) {
                byte[] buff = new byte[1024];
                int len;
                while ((len = inputStream.read(buff)) > 0) {
                    out.write(buff, 0, len);
                }
            }
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper to find filename from Uri
    private static String queryName(Context context, Uri uri) {
        String displayName = null;
        try (android.database.Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                displayName = returnCursor.getString(nameIndex);
            }
        } catch (Exception ignored) {}
        return displayName;
    }

    private void uploadProfileImage(Uri imageUri) {
        File file = uriToFile(imageUri);
        if (file == null || !file.exists()) {
            Toast.makeText(this, "File not found or cannot read the selected image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare multipart
        MediaType mediaType = MediaType.parse("image/*");
        RequestBody reqFile = RequestBody.create(mediaType, file); // common signature
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_image", file.getName(), reqFile);

        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(sharedPreferences.getInt("user_id", 0)));

        // Retrofit client (you can reuse a singleton client if you have one)
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<JsonObject> call = apiService.uploadProfileImage(userIdPart, body);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject res = response.body();
                    if (res.has("status") && res.get("status").getAsBoolean()) {
                        // prefer full URL if backend returns image_url
                        String imageUrl = "";
                        if (res.has("image_url")) imageUrl = res.get("image_url").getAsString();
                        else if (res.has("image_path")) {
                            // always make full URL
                            String path = res.get("image_path").getAsString();
                            if (!path.startsWith("http")) imageUrl = BASE_URL + path;
                            else imageUrl = path;
                        }

                        if (!imageUrl.isEmpty()) {
                            // save and show
                            int userId = sharedPreferences.getInt("user_id", 0);
                            String userProfileKey = "profileImageUrl_" + userId; // unique key per user
                            sharedPreferences.edit()
                                    .putString(userProfileKey, imageUrl)      // save per user
                                    .putString("activeProfileUrl", imageUrl)  // save currently logged-in user's image
                                    .apply();

                            Glide.with(EditProfileActivity.this).load(imageUrl).placeholder(R.drawable.user_grad).into(profileImage);

                        }

                        Toast.makeText(EditProfileActivity.this, "Profile image uploaded", Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = res.has("message") ? res.get("message").getAsString() : "Upload failed";
                        Toast.makeText(EditProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Upload failed: server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Upload failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Your existing updateProfileOnServer (ensure server endpoint exists if you want to update name/mobile remotely)
    private void updateProfileOnServer(String name, String mobile) {
        String email = currentEmail; // from SharedPreferences

        // Prepare JSON body
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("fullName", name);
        body.addProperty("mobile", mobile);

        // Retrofit client
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<JsonObject> call = apiService.updateProfile(body);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject res = response.body();
                    boolean status = res.has("status") && res.get("status").getAsBoolean();

                    if (status) {
                        // ✅ Save updated values locally
                        sharedPreferences.edit()
                                .putString("name", name)
                                .putString("mobile", mobile)
                                .apply();

                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = res.has("message") ? res.get("message").getAsString() : "Update failed";
                        Toast.makeText(EditProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Update failed: server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
