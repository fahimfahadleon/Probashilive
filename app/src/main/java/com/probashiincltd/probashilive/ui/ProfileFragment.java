package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel.PROFILE_ACTION_EDIT;
import static com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel.PROFILE_ACTION_FOLLOWERS;
import static com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel.PROFILE_ACTION_FOLLOWING;
import static com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel.PROFILE_ACTION_SAVE;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.FollowerOrFollowingAdapter;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.connectionutils.RosterHandler;
import com.probashiincltd.probashilive.databinding.FragmentProfileBinding;
import com.probashiincltd.probashilive.databinding.ShowFollowerOrFollowingBinding;
import com.probashiincltd.probashilive.databinding.SingleFollowerOrFollowingBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel;

import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;

import java.util.ArrayList;
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
    public static ProfileFragment getInstance() {
       if(profileFragment == null){
           profileFragment = new ProfileFragment();
       }
       return profileFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        model = new ViewModelProvider(this).get(ProfileFragmentViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(binding.getLifecycleOwner());
        model.init();
        initViewModelObserver();
        Glide.with(binding.profile).load(CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE)).into(binding.profile);
        binding.userName.setText(CM.getProfile().getContent().get(ProfileItem.NAME));
        binding.coin.setText(CM.getProfile().getContent().get(ProfileItem.COIN));
        binding.phone.setText(CM.getProfile().getContent().get(ProfileItem.PHONE));
        binding.email.setText(CM.getProfile().getContent().get(ProfileItem.EMAIL));
        binding.status.setText(CM.getProfile().getContent().get(ProfileItem.VIP));

        return binding.getRoot();
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
                }
            }
        });
    }

    private void saveData() {
        String name = binding.userName.getText().toString();
        String email = binding.email.getText().toString();
        String phone = binding.phone.getText().toString();

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




    void setUpEdit(){
        if(isEditEnabled){
            binding.profileEdit.setVisibility(View.VISIBLE);
            binding.userName.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.edit),null);
            binding.email.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.edit),null);
            binding.phone.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.edit),null);
            binding.coin.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.edit),null);
            binding.status.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.edit),null);
//            model.setInsetDrawable(R.drawable.edit);
            model.setIsEnabled(true);
        }else {
            binding.profileEdit.setVisibility(View.GONE);
            binding.email.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
            binding.userName.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
            binding.phone.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
            binding.coin.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
            binding.status.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
//            model.setInsetDrawable(null);
            model.setIsEnabled(false);

        }
    }
}