package com.example.proyecto_semestral_checkpoint;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.proyecto_semestral_checkpoint.models.User;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 123;

    private Uri imageData;

    private Recipe_App_API recipe_app_api = ApiClient.getClient().create(Recipe_App_API.class);

    private TextView UserNameT, EmailT, PasswordT;
    private EditText UserNameE, EmailE, PasswordE;
    private ImageView ImageT, ImageE,  Edit, Cancel;
    private Button Add, Save;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initControllers();
        setUserProfile();
        toggleEditMode();
        addImage();
        updateUserInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(imageData != null)
            Glide.with(this).load(imageData).into(ImageE);
    }

    private void initControllers() {
        //This is insane T_T
        UserNameT = getView().findViewById(R.id.user_name_plh);
        EmailT = getView().findViewById(R.id.user_email_plh);
        PasswordT = getView().findViewById(R.id.user_password_plh);

        UserNameE = getView().findViewById(R.id.user_name_edit);
        EmailE = getView().findViewById(R.id.user_email_edit);
        PasswordE = getView().findViewById(R.id.user_password_edit);

        ImageT = getView().findViewById(R.id.user_profile_image_plh);
        ImageE = getView().findViewById(R.id.user_profile_image_edit);
        Edit = getView().findViewById(R.id.edit_user);
        Cancel = getView().findViewById(R.id.cancel);

        Add = getView().findViewById(R.id.add_user_image);
        Save = getView().findViewById(R.id.save_user_info);
    }

    private void setUserProfile() {

        SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        String userName = settings.getString("user_name", "User Name");
        String email = settings.getString("email", "User Name");
        String id = settings.getString("_id", "no id");

        UserNameT.setText(userName);
        UserNameE.setText(userName);

        EmailT.setText(email);
        EmailE.setText(email);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(this).load(ApiClient.getBaseUrl() + "users/" + id + "/avatar").apply(options).into(ImageT);
        Glide.with(this).load(ApiClient.getBaseUrl() + "users/" + id + "/avatar").apply(options).into(ImageE);
    }

    private void toggleEditMode() {
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageT.setVisibility(View.GONE);
                ImageE.setVisibility(View.VISIBLE);

                UserNameT.setVisibility(View.GONE);
                UserNameE.setVisibility(View.VISIBLE);

                EmailT.setVisibility(View.GONE);
                EmailE.setVisibility(View.VISIBLE);

                PasswordT.setVisibility(View.GONE);
                PasswordE.setVisibility(View.VISIBLE);

//                Add.setVisibility(View.VISIBLE);
                Save.setVisibility(View.VISIBLE);

                Edit.setVisibility(View.GONE);
                Cancel.setVisibility(View.VISIBLE);

                setUserProfile();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageT.setVisibility(View.VISIBLE);
                ImageE.setVisibility(View.GONE);

                UserNameT.setVisibility(View.VISIBLE);
                UserNameE.setVisibility(View.GONE);

                EmailT.setVisibility(View.VISIBLE);
                EmailE.setVisibility(View.GONE);

                PasswordT.setVisibility(View.VISIBLE);
                PasswordE.setVisibility(View.GONE);

//                Add.setVisibility(View.GONE);
                Save.setVisibility(View.GONE);

                Cancel.setVisibility(View.GONE);
                Edit.setVisibility(View.VISIBLE);

                imageData = null;
            }
        });
    }

    private void addImage() {
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageData = data.getData();
        }
    }

    private void updateUserInfo() {
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidName = !UserNameE.getText().toString().isEmpty();
                boolean isValidEmail = !EmailE.getText().toString().isEmpty();
                boolean isValidPass = !PasswordE.getText().toString().isEmpty();

                if(!isValidName) {
                    UserNameE.requestFocus();
                    UserNameE.setError("El nombre no puede estar vacío.");
                }
                if(!isValidEmail) {
                    EmailE.requestFocus();
                    EmailE.setError("El email no puede estar vacío.");
                }
                if(!isValidPass) {
                    PasswordE.requestFocus();
                    PasswordE.setError("El password no puede estar vacío.");
                }

                if(isValidName && isValidEmail && isValidPass) {
                    SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
                    String token = settings.getString("token", "");

                    User update = new User();
                    update.setName(UserNameE.getText().toString());
                    update.setEmail(EmailE.getText().toString());

                    if(!PasswordE.getText().toString().equals("Password"))
                        update.setPassword(PasswordE.getText().toString());

                    try {
                        Call<User> call = recipe_app_api.updateUser("Bearer " + token, update);

                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if(!response.isSuccessful()) {
                                    return;
                                }

                                user = response.body();
                                SharedPreferences.Editor edit = settings.edit();
                                edit.putString("user_name", user.getName());
                                edit.putString("email", user.getEmail());
                                edit.apply();

                                if(imageData != null)
                                    uploadImage(token);
                                else
                                    getActivity().recreate();

                                Log.d("USER INFO UPDATE", "onResponse: ");
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {

                            }
                        });
                    } catch (Exception e) {
                        Log.d("EXCEPTION", "onClick: " + e);
                    }
                }
            }
        });
    }

    private void uploadImage(String token) {
        try {
            Log.d("TOKEN", "uploadImage: " + token);
            File image = new File(imageData.getPath());
            Log.d("IMAGE DATA", "uploadImage: " + image);
            Call<ResponseBody> call = recipe_app_api.uploadUserImage("Bearer " + token, image);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(!response.isSuccessful()) {
                        Log.d("SOMETHING HAPPENPS", "onResponse: " + response.body());
                        return;
                    }
                    Log.d("IMAGE UPLOAD", "onResponse: " + response.body());
                    getActivity().recreate();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("UPLOAD FAIL", "onFailure: " + t);
                }
            });
        } catch (Exception e) {
            Log.d("EXCEPTION", "uploadImage: " + e);
        }
    }
}
