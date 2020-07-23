package com.example.proyecto_semestral_checkpoint.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.proyecto_semestral_checkpoint.R;
import com.example.proyecto_semestral_checkpoint.activities.LoginActivity;
import com.example.proyecto_semestral_checkpoint.activities.MainActivity;
import com.example.proyecto_semestral_checkpoint.models.User;
import com.example.proyecto_semestral_checkpoint.network.ApiClient;
import com.example.proyecto_semestral_checkpoint.network.Recipe_App_API;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

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
    private View headerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = (User) getArguments().getSerializable("user");

        NavigationView navigationView = getActivity().findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);
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

        UserNameT.setText(user.getName());
        UserNameE.setText(user.getName());

        EmailT.setText(user.getEmail());
        EmailE.setText(user.getEmail());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);

        Glide.with(this).load(ApiClient.getBaseUrl() + "users/" + user.getId() + "/avatar").apply(options).into(ImageT);
        Glide.with(this).load(ApiClient.getBaseUrl() + "users/" + user.getId() + "/avatar").apply(options).into(ImageE);
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

    private void updateUserInfo() {
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidName = !UserNameE.getText().toString().isEmpty();
                boolean isValidEmail = !EmailE.getText().toString().isEmpty();

                if(!isValidName) {
                    UserNameE.requestFocus();
                    UserNameE.setError("User name can't be empty");
                }
                if(!isValidEmail) {
                    EmailE.requestFocus();
                    EmailE.setError("Email can't be empty");
                }

                if(isValidName && isValidEmail) {
                    SharedPreferences settings = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
                    String token = settings.getString("token", "");

                    Save.setEnabled(false);

                    User update = new User();
                    update.setName(UserNameE.getText().toString());
                    update.setEmail(EmailE.getText().toString());

                    if(!PasswordE.getText().toString().equals("Password") && PasswordE.length() > 9 && !PasswordE.toString().isEmpty())
                        update.setPassword(PasswordE.getText().toString());

                    try {
                        Call<User> call = recipe_app_api.updateUser("Bearer " + token, update);

                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if(!response.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Update error, check your inputs", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                user = response.body();

                                if(imageData != null)
                                    uploadImage(token);
                                else
                                    refresh();

                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                refresh();
                                Toast.makeText(getActivity(), "Something went wrong connecting to the server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    private void refresh() {

        //Exit edit mode
        ImageT.setVisibility(View.VISIBLE);
        ImageE.setVisibility(View.GONE);

        UserNameT.setVisibility(View.VISIBLE);
        UserNameE.setVisibility(View.GONE);

        EmailT.setVisibility(View.VISIBLE);
        EmailE.setVisibility(View.GONE);

        PasswordT.setVisibility(View.VISIBLE);
        PasswordE.setVisibility(View.GONE);

        //Add.setVisibility(View.GONE);
        Save.setEnabled(true);
        Save.setVisibility(View.GONE);

        Cancel.setVisibility(View.GONE);
        Edit.setVisibility(View.VISIBLE);

        imageData = null;

        //Refresh Nav Header User info
        TextView User_name =  headerView.findViewById(R.id.user_name);
        TextView Email =  headerView.findViewById(R.id.email);
        ImageView Image =  headerView.findViewById(R.id.profile_image);


        User_name.setText(user.getName());
        Email.setText(user.getEmail());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(this).load(ApiClient.getBaseUrl() + "users/" + user.getId() + "/avatar").apply(options).into(Image);

        //Restart Fragment
        onStart();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageData = data.getData();
        }
    }

}


