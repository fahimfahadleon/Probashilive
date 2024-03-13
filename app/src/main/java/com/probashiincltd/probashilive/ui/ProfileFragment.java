package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel.PROFILE_ACTION_EDIT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.FragmentProfileBinding;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.viewmodels.ProfileFragmentViewModel;

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
                }
            }
        });
    }

    void setUpEdit(){
        if(isEditEnabled){
            binding.profileEdit.setVisibility(View.VISIBLE);
//            binding.userName.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.edit),null);
//            binding.email.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.edit),null);
//            binding.phone.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.edit),null);
//            binding.coin.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.edit),null);
//            binding.status.setCompoundDrawablesWithIntrinsicBounds(null,null, ContextCompat.getDrawable(requireContext(),R.drawable.edit),null);
            model.setInsetDrawable(R.drawable.edit);
            model.setIsEnabled(true);
        }else {
            binding.profileEdit.setVisibility(View.GONE);
//            binding.email.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
//            binding.userName.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
//            binding.phone.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
//            binding.coin.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
//            binding.status.setCompoundDrawablesWithIntrinsicBounds(null,null, null,null);
            model.setInsetDrawable(null);
            model.setIsEnabled(false);

        }
    }
}