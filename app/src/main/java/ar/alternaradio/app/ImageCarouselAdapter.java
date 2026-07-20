package ar.alternaradio.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import jp.wasabeef.glide.transformations.BlurTransformation;
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
        // Contenedor que muestra la imagen anterior (blureada), la principal y la siguiente (blureada)
        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        container.setOrientation(LinearLayout.VERTICAL);
        container.setBackgroundColor(0xFF000000);

        // ImageView para la imagen anterior (blureada, small)
        ImageView prevImage = new ImageView(context);
        prevImage.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                0.2f  // 20% del alto
        ));
        prevImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        prevImage.setAlpha(0.4f);
        container.addView(prevImage);

        // ImageView para la imagen principal (grande)
        ImageView mainImage = new ImageView(context);
        mainImage.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                0.6f  // 60% del alto
        ));
        mainImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        container.addView(mainImage);

        // ImageView para la imagen siguiente (blureada, small)
        ImageView nextImage = new ImageView(context);
        nextImage.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                0.2f  // 20% del alto
        ));
        nextImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        nextImage.setAlpha(0.4f);
        container.addView(nextImage);

        return new ImageViewHolder(container, mainImage, prevImage, nextImage);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String currentUrl = imageUrls.get(position);
        String prevUrl = position > 0 ? imageUrls.get(position - 1) : null;
        String nextUrl = position < imageUrls.size() - 1 ? imageUrls.get(position + 1) : null;

        // Cargar imagen actual (sin blur, tamaño completo)
        Glide.with(context)
                .load(currentUrl)
                .centerInside()
                .dontTransform()
                .into(holder.mainImageView);

        // Cargar imagen anterior (con blur)
        if (prevUrl != null) {
            Glide.with(context)
                    .load(prevUrl)
                    .transform(new MultiTransformation<>(
                            new CenterInside(),
                            new BlurTransformation(25)
                    ))
                    .into(holder.prevImageView);
        } else {
            holder.prevImageView.setImageDrawable(null);
        }

        // Cargar imagen siguiente (con blur)
        if (nextUrl != null) {
            Glide.with(context)
                    .load(nextUrl)
                    .transform(new MultiTransformation<>(
                            new CenterInside(),
                            new BlurTransformation(25)
                    ))
                    .into(holder.nextImageView);
        } else {
            holder.nextImageView.setImageDrawable(null);
        }
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
        ImageView mainImageView;
        ImageView prevImageView;
        ImageView nextImageView;

        ImageViewHolder(View container, ImageView mainImage, ImageView prevImage, ImageView nextImage) {
            super(container);
            this.mainImageView = mainImage;
            this.prevImageView = prevImage;
            this.nextImageView = nextImage;
        }
    }
}

