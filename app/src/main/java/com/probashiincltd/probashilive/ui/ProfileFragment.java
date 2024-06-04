package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.functions.Functions.loadImage;
import static com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel.PROFILE_ACTION_EDIT;
import static com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel.PROFILE_ACTION_FOLLOWERS;
import static com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel.PROFILE_ACTION_FOLLOWING;
import static com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel.PROFILE_ACTION_SAVE;
import static com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel.PROFILE_ACTION_UPLOAD_IMAGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.FollowerOrFollowingAdapter;
import com.probashiincltd.probashilive.callbacks.ImageUploadCallback;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.connectionutils.RosterHandler;
import com.probashiincltd.probashilive.databinding.FragmentProfileBinding;
import com.probashiincltd.probashilive.databinding.ShowFollowerOrFollowingBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel;

import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smackx.httpfileupload.element.Slot;
import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }
    static ProfileFragment profileFragment;
    FragmentProfileBinding binding;
    ProfileFragmentViewModel model;
    String profileSlotAddress;
    public static ProfileFragment getInstance() {
       if(profileFragment == null){
           profileFragment = new ProfileFragment();
       }
       return profileFragment;
    }
    private File saveImageToCache(String name,Uri imageUri) throws IOException {
        InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
        if (inputStream == null) {
            return null;
        }

        File cacheDir = getContext().getCacheDir();
        File cacheFile = new File(cacheDir, name);

        FileOutputStream outputStream = new FileOutputStream(cacheFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();

        // Image is now saved in the cache directory as "cached_image.jpg"

        return cacheFile;
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if(data != null){

                        Uri imageUri = data.getData();
                        String name = getFileName(imageUri);

                        try {
                            Functions.uploadProtectedFile(CM.getConnection(), new ArrayList<>(Collections.singletonList(saveImageToCache(name,imageUri))), getContext(), new ImageUploadCallback() {
                                @Override
                                public void onFailed(String reason) {
                                    Log.e("reason",reason);
                                }

                                @Override
                                public void onSuccess(ArrayList<Slot> originalSlots, ArrayList<Slot> thumbnailSlots, String type) {
                                    Log.e("originalSlots",originalSlots.get(0).getGetUrl().toString());
//                                    Log.e("thumbnailSlots",thumbnailSlots.get(0).getGetUrl().toString());
                                    Log.e("type",type);
                                    profileSlotAddress = originalSlots.get(0).getGetUrl().toString();
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else {
                        Toast.makeText(getContext(), "file empty!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        model = new ViewModelProvider(this).get(ProfileFragmentViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(binding.getLifecycleOwner());
        model.init();
        initViewModelObserver();

        updateProfile();


        return binding.getRoot();
    }
    void updateProfile(){
        loadImage(getContext(),binding.profile,CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE));
        binding.userName.setText(CM.getProfile().getContent().get(ProfileItem.NAME));
        binding.coin.setText(CM.getProfile().getContent().get(ProfileItem.COIN));
        binding.phone.setText(CM.getProfile().getContent().get(ProfileItem.PHONE));
        binding.email.setText(CM.getProfile().getContent().get(ProfileItem.EMAIL));
        binding.status.setText(CM.getProfile().getContent().get(ProfileItem.VIP));

        profileSlotAddress = CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE);
    }





    boolean isEditEnabled = false;

    private void initViewModelObserver() {
        model.getOnButtonClick().observe(getViewLifecycleOwner(),i->{
            switch (i){
                case PROFILE_ACTION_EDIT:{
                    isEditEnabled = !isEditEnabled;
                    setUpEdit();
                    break;
                }case PROFILE_ACTION_SAVE:{
                    saveData();
                    break;
                }case PROFILE_ACTION_FOLLOWERS:{
                    try {
                        openFollowers();
                    } catch (JSONException e) {
                        //ignored
                    }
                    break;
                }case PROFILE_ACTION_FOLLOWING:{
                    try {
                        openFollowing();
                    } catch (JSONException e) {
                        //ignored
                    }
                    break;
                } case PROFILE_ACTION_UPLOAD_IMAGE:{
                    Intent intent = new Intent ();
                    intent.setAction ( Intent.ACTION_PICK );
                    intent.setDataAndType ( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*" );
                    someActivityResultLauncher.launch(intent);
                }
            }
        });
    }

    private void saveData() {
        String name = binding.userName.getText().toString();
        String phone = binding.phone.getText().toString();
        String profilePic = profileSlotAddress;
        String vip = CM.getProfile().getContent().get(ProfileItem.VIP);
        String email = CM.getProfile().getContent().get(ProfileItem.EMAIL);
        String coin = CM.getProfile().getContent().get(ProfileItem.COIN);
        String landingAnimation = CM.getProfile().getContent().get(ProfileItem.LANDING_ANIMATION);

        HashMap<String, String> profileMap = new HashMap<>();
        profileMap.put(ProfileItem.NAME, name);
        profileMap.put(ProfileItem.PROFILE_PICTURE, profilePic);
        profileMap.put(ProfileItem.EMAIL,email );
        profileMap.put(ProfileItem.PHONE, phone);
        profileMap.put(ProfileItem.COIN, coin);
        profileMap.put(ProfileItem.VIP, vip);
        profileMap.put(ProfileItem.LANDING_ANIMATION,landingAnimation);

        ProfileItem profileItem = new ProfileItem(profileMap);
        ProfileItem.updateProfileItem(profileItem);

        CM.setProfile(profileItem);
        updateProfile();

    }

    private void openFollowing() throws JSONException {
        Log.e("clicked","following");
        Set<RosterEntry> entries = RosterHandler.getRosterHandler().roster.getEntries();
        ArrayList<String> following = new ArrayList<>();
        ArrayList<ProfileItem>profileItems = new ArrayList<>();
        for(RosterEntry entry:entries){
            List<RosterGroup> groups = entry.getGroups();
            if(!groups.isEmpty()){
                following.add(entry.getJid().toString().split("@")[0]);
            }
        }
        if(!following.isEmpty()){
            List<Item>items = Functions.getMultipleItemOfNode(CM.NODE_USERS,following);
            for(Item i: items){
                profileItems.add(ProfileItem.parseProfileItem(i));
            }
        }
        openFollowingOrFollower(profileItems,true);


    }

    private void openFollowers() throws JSONException {
        Log.e("clicked","followers");
        Set<RosterEntry> entries = RosterHandler.getRosterHandler().roster.getEntries();
        ArrayList<String> followers = new ArrayList<>();
        ArrayList<ProfileItem>profileItems = new ArrayList<>();
        for(RosterEntry entry:entries){
            List<RosterGroup> groups = entry.getGroups();
            if(groups.isEmpty()){
                followers.add(entry.getJid().toString().split("@")[0]);
            }
        }

        if(!followers.isEmpty()){
            List<Item>items = Functions.getMultipleItemOfNode(CM.NODE_USERS,followers);
            for(Item i: items){
                profileItems.add(ProfileItem.parseProfileItem(i));
            }
        }
        openFollowingOrFollower(profileItems,false);
    }
    AlertDialog sd;
    void openFollowingOrFollower(ArrayList<ProfileItem> items,boolean isFollowing){
        FollowerOrFollowingAdapter adapter = new FollowerOrFollowingAdapter(isFollowing, items, new FollowerOrFollowingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProfileItem profileItem) {
                //open profile
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(),R.style.Base_Theme_ProbashiLive);
        ShowFollowerOrFollowingBinding binding1 = ShowFollowerOrFollowingBinding.inflate(getLayoutInflater());
        binding1.title.setText(isFollowing?"Following":"Followers");
        binding1.rv.setAdapter(adapter);
        builder.setView(binding1.getRoot());

        sd = builder.create();
        sd.show();
        Objects.requireNonNull(sd.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sd.getWindow().setGravity(Gravity.CENTER);
        sd.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        sd.getWindow().setDimAmount(0.5f);



    }



    void setUpDrawable(EditText view, Drawable d){
        view.setCompoundDrawablesWithIntrinsicBounds(null,null,d,null);
    }

    void setUpEdit(){
        if(isEditEnabled){
            binding.profileEdit.setVisibility(View.VISIBLE);
            Drawable d= ContextCompat.getDrawable(requireContext(),R.drawable.edit);
            setUpDrawable(binding.userName,d);
            //setUpDrawable(binding.email,d);
            setUpDrawable(binding.phone,d);
            //setUpDrawable(binding.coin,d);
            setUpDrawable(binding.status,d);
            model.setIsEnabled(true);
            binding.save.setEnabled(true);

        }else {
            binding.profileEdit.setVisibility(View.GONE);
            setUpDrawable(binding.userName, null);
            //setUpDrawable(binding.email, null);
            setUpDrawable(binding.phone, null);
           // setUpDrawable(binding.coin, null);
            setUpDrawable(binding.status, null);
            model.setIsEnabled(false);
            binding.save.setEnabled(false);
        }
    }
}