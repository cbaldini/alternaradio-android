package ar.alternaradio.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder> {
    private final Context context;
    private final List<String> imageUrls;

    public ImageCarouselAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .dontTransform()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void updateImages(List<String> newUrls) {
        this.imageUrls.clear();
        this.imageUrls.addAll(newUrls);
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }
    }
}

