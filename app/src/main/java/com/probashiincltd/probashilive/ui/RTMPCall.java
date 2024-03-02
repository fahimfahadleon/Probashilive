package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMMENT;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.CommentAdapter;
import com.probashiincltd.probashilive.databinding.ActivityRtmpcallBinding;
import com.probashiincltd.probashilive.viewmodels.RTMPCallViewModel;

import java.util.List;

public class RTMPCall extends AppCompatActivity {
    ActivityRtmpcallBinding binding;
    RTMPCallViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rtmpcall);
        model = new ViewModelProvider(this).get(RTMPCallViewModel.class);
        binding.setViewModel(model);
        binding.setLifecycleOwner(this);

        PermissionX.init(this).permissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA", "android.permission.RECORD_AUDIO").request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                if (allGranted) {
                    CommentAdapter commentAdapter = new CommentAdapter();
                    model.setAdapter(commentAdapter);
                    model.initViewModel(RTMPCall.this, getIntent(), binding.cameraView);
                    observeViewModel();
                } else {
                    PermissionX.init(RTMPCall.this).permissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA", "android.permission.RECORD_AUDIO").explainReasonBeforeRequest().request(new RequestCallback() {
                        @Override
                        public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
                            if (allGranted) {
                                CommentAdapter commentAdapter = new CommentAdapter();
                                model.setAdapter(commentAdapter);
                                model.initViewModel(RTMPCall.this, getIntent(), binding.cameraView);
                                observeViewModel();
                            } else {
                                Toast.makeText(RTMPCall.this, "Permission is needed", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            }
        });


    }

    public static class WrapContentLinearLayoutManager extends LinearLayoutManager {

        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "meet a IOOBE in RecyclerView");
            }
        }
    }

    void observeViewModel() {
        model.getSendComment().observe(this, s -> {
            if (s.equals(SUBJECT_TYPE_COMMENT)) {
                String cmnt = binding.commentEDT.getText().toString();
                if (!cmnt.isEmpty()) {
                    model.sendComment(cmnt);
                    binding.commentEDT.setText(null);
                }
            }
        });
        model.getSelectedItem().observe(this, model -> {

        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.onDestroy();
    }
}

