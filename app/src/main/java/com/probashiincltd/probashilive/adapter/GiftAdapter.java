package com.probashiincltd.probashilive.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.probashiincltd.probashilive.databinding.SingleGiftItemBinding;
import com.probashiincltd.probashilive.models.GiftItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {

    public static final int GIFT_TYPE_IMAGE = 0;
    public static final int GIFT_TYPE_SVGA = 1;
    public static final int GIFT_TYPE_LOTTIE = 2;


    int type;
    OnItemClickListener listener;
    Context context;
    ArrayList<String> imageMap;
    ArrayList<String> fullImage;
    ArrayList<String> halfImage;
    ArrayList<String> lottieFiles;


    OnItemLongClickListener longClickListener;

    public GiftAdapter(Context context, int giftType, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
        this.context = context;
        this.type = giftType;
        this.listener = listener;
        this.longClickListener = longClickListener;
        imageMap = new ArrayList<>();
        lottieFiles = getListOfFiles("important/previews/lottie");
        fullImage = getListOfFiles("important/previews/full");
        halfImage = getListOfFiles("important/previews/half");
        imageMap = getListOfFiles("important/images");
    }
    private ArrayList<String> getListOfFiles(String dir) {
        ArrayList<String> jsonFiles = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list(dir);
            if (files != null) {
                for (String file : files) {
                    if (file.endsWith("." + "png")) {
                        jsonFiles.add(file);
                    }
                }
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        return jsonFiles;
    }



    public interface OnItemClickListener {
        void onItemClick(GiftItem giftItem);
    }

    public interface OnItemLongClickListener {
        void onLongClick(GiftItem giftItem);
    }

    @NonNull
    @Override
    public GiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GiftViewHolder(SingleGiftItemBinding.inflate((LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GiftViewHolder holder, int position) {
        switch (type) {
            case GIFT_TYPE_LOTTIE: {
                GiftItem giftItem = new GiftItem();
                giftItem.setGiftType(GiftItem.GIFT_TYPE_LOTTIE);
                giftItem.setGiftName(lottieFiles.get(position));
                giftItem.setGiftReference("");
                giftItem.setGiftPrice("4512");
                holder.setUpData(giftItem);
                break;
            }
            case GIFT_TYPE_SVGA: {
                GiftItem giftItem = new GiftItem();
                giftItem.setGiftType(GiftItem.GIFT_TYPE_SVGA_HALF);
                giftItem.setGiftName(halfImage.get(position));
                giftItem.setGiftReference("");
                giftItem.setGiftPrice("4512");
                holder.setUpData(giftItem);
                break;
            }
            default: {
                GiftItem giftItem = new GiftItem();
                giftItem.setGiftType(GiftItem.GIFT_TYPE_IMAGE);
                giftItem.setGiftName(imageMap.get(position));
                giftItem.setGiftReference("");
                giftItem.setGiftPrice("4512");
                holder.setUpData(giftItem);
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        switch (type) {
            case GIFT_TYPE_IMAGE: {
                return imageMap.size();
            }
            case GIFT_TYPE_LOTTIE: {
                return lottieFiles.size();
            }
            case GIFT_TYPE_SVGA: {
                return halfImage.size();
            }
            default:
                return imageMap.size();
        }
    }

    public class GiftViewHolder extends RecyclerView.ViewHolder {
        SingleGiftItemBinding binding;
        GiftItem giftItem;
        private void loadPreview(String dir, String name) {
            try {
                InputStream ims = context.getAssets().open(dir + name);
                Drawable d = Drawable.createFromStream(ims, null);
                binding.imageview.setImageDrawable(d);
                ims.close();
            } catch (IOException ex) {
                ex.fillInStackTrace();
            }
        }
        public void setUpData(GiftItem item) {
            this.giftItem = item;
            switch (type) {
                case GIFT_TYPE_IMAGE: {
                    loadPreview("important/images/", item.getGiftName());
                    break;
                }
                case GIFT_TYPE_LOTTIE: {
                    loadPreview("important/previews/lottie/", giftItem.getGiftName());
                    break;
                }
                case GIFT_TYPE_SVGA: {
                    loadPreview("important/previews/half/", giftItem.getGiftName());
                    break;
                }
            }
            binding.giftname.setText(giftItem.getGiftName().replace(".png", ""));
            binding.giftPrice.setText(giftItem.getGiftPrice());
            binding.getRoot().setOnClickListener(v -> listener.onItemClick(giftItem));
            binding.getRoot().setOnLongClickListener(v -> {
                longClickListener.onLongClick(giftItem);
                return true;
            });
        }

        public GiftViewHolder(@NonNull SingleGiftItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
