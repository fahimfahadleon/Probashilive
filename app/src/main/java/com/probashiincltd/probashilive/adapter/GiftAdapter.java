package com.probashiincltd.probashilive.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.probashiincltd.probashilive.databinding.SingleGiftItemBinding;
import com.probashiincltd.probashilive.models.GiftItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {

    public static final int GIFT_TYPE_IMAGE = 0;
    public static final int GIFT_TYPE_SVGA = 1;
    public static final int GIFT_TYPE_LOTTIE = 2;


    int type;
    OnItemClickListener listener;
    Context context;
    HashMap<String,Drawable>imageDrawableMap;
    ArrayList<GiftItem>imageMap;
    ArrayList<GiftItem>svgList;
    ArrayList<GiftItem>lottie;

    SVGAParser parser;
    public GiftAdapter(Context context,int giftType,OnItemClickListener listener){
        this.context = context;
        this.type = giftType;
        this.listener = listener;
        parser = new SVGAParser(context);
        svgList = getListOfJsonFiles("svga");
        lottie = getListOfJsonFiles("json");
        imageMap =new ArrayList<>();
        imageDrawableMap = new HashMap<>();
        loadImagesFromAssets();

for(GiftItem g: svgList){
    Log.e("svga",g.getGiftName());
}
for(GiftItem g: lottie){
    Log.e("lottie",g.getGiftName());
}
for(GiftItem g: imageMap){
    Log.e("imagemap",g.getGiftName());
}

    }

    private ArrayList<GiftItem> getListOfJsonFiles(String title) {
        ArrayList<String> jsonFiles = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list("");
            if (files != null) {
                for (String file : files) {
                    if (file.endsWith("."+title)) {
                        jsonFiles.add(file);
                    }
                }
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
        ArrayList<GiftItem>items = new ArrayList<>();
        for(String s: jsonFiles){
            GiftItem giftItem = new GiftItem();
            giftItem.setGiftName(s);
            giftItem.setGiftReference("asdfasdf");
            giftItem.setGiftPrice("4500");
            items.add(giftItem);
        }


        return items;
    }
    private void loadImagesFromAssets() {
        try {
            AssetManager assetManager = context.getAssets();
            String[] files = assetManager.list("important/images");
            if(files == null){
                Log.w("critical Warning","files null");
                return;
            }
            for (String file : files) {
                InputStream inputStream = assetManager.open("important/images/" + file);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                imageDrawableMap.put(file,drawable);

                GiftItem giftItem = new GiftItem();
                giftItem.setGiftName(file);
                giftItem.setGiftReference("");
                giftItem.setGiftPrice("3500");
                imageMap.add(giftItem);

            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }


    public interface OnItemClickListener{
        void onItemClick(GiftItem giftItem);
    }

    @NonNull
    @Override
    public GiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GiftViewHolder(SingleGiftItemBinding.inflate((LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GiftViewHolder holder, int position) {
        switch (type){
            case GIFT_TYPE_LOTTIE:{
                holder.setUpData(lottie.get(position));
                break;
            }case GIFT_TYPE_SVGA:{
                holder.setUpData(svgList.get(position));
                break;
            } default:{
                holder.setUpData(imageMap.get(position));
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
//        switch (type){
//            case GIFT_TYPE_IMAGE:{
//               return imageMap.size();
//            }case GIFT_TYPE_LOTTIE:{
//                return lottie.size();
//            }case GIFT_TYPE_SVGA:{
//                return svgList.size();
//            } default:return imageMap.size();
//        }
        return 1;
    }

    public class GiftViewHolder extends RecyclerView.ViewHolder {
        SingleGiftItemBinding binding;
        GiftItem giftItem;
        private void loadLottieAnimation(String name){
            binding.lottie.setAnimation(name); // Replace "your_animation.json" with your animation file
            binding.lottie.playAnimation();
        }

        private void loadSVGAAnimation(String fileName) {
            try {
                // Load animation from assets
                parser.parse(fileName, new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NonNull SVGAVideoEntity videoItem) {
                        // Set the animation to SVGAPlayer
//                       binding.svga.setVideoItem(videoItem);
//                        binding.svga.startAnimation();
                        binding.imageview.setImageBitmap(extractFirstFrame(videoItem));
//                        extractFirstFrame(videoItem);
                    }

                    @Override
                    public void onError() {
                        Log.e("error","error loading animation");
                    }
                });
            } catch (Exception e) {
                Log.e("1fileName",fileName);
                Log.e("1error",e.toString());
            }
        }


        private Bitmap extractFirstFrame(SVGAVideoEntity videoEntity) {
            Bitmap firstFrameBitmap = Bitmap.createBitmap((int)videoEntity.getVideoSize().getWidth(),
                    (int)videoEntity.getVideoSize().getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(firstFrameBitmap);
            SVGADrawable drawable = new SVGADrawable(videoEntity);
            drawable.draw(canvas); // Draw the animation onto the canvas
            return firstFrameBitmap;



//            Set<String>keys = videoEntity.getImageMap$com_opensource_svgaplayer().keySet();
//            for(String s: keys){
//                Log.e("keys",s);
//            }
        }
        public void setUpData(GiftItem item) {
            this.giftItem = item;
            switch (type){
                case GIFT_TYPE_IMAGE:{
                    binding.lottie.setVisibility(View.GONE);
                    binding.svga.setVisibility(View.GONE);
                    binding.imageview.setVisibility(View.VISIBLE);

                    try {
                        InputStream inputStream = context.getAssets().open("important/images/"+item.getGiftName()); // Replace "image.jpg" with your image file in assets
                        Drawable drawable = Drawable.createFromStream(inputStream, null);
                        binding.imageview.setImageDrawable(drawable);
                    } catch (IOException e) {
                        Log.e("2fileName",item.getGiftName());
                        Log.e("2error",e.toString());
                    }
//                    binding.imageview.setImageDrawable(imageDrawableMap.get(item.getGiftName()));

                    break;
                }case GIFT_TYPE_LOTTIE:{
                    binding.lottie.setVisibility(View.VISIBLE);
                    binding.svga.setVisibility(View.GONE);
                    binding.imageview.setVisibility(View.GONE);
                    loadLottieAnimation(giftItem.getGiftName());

                    break;
                }case GIFT_TYPE_SVGA:{
                    binding.lottie.setVisibility(View.GONE);
                    binding.svga.setVisibility(View.GONE);
                    binding.imageview.setVisibility(View.VISIBLE);
                    loadSVGAAnimation(giftItem.getGiftName());
                    break;
                }
            }
            binding.giftname.setText(giftItem.getGiftName());
        }

        public GiftViewHolder(@NonNull SingleGiftItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
