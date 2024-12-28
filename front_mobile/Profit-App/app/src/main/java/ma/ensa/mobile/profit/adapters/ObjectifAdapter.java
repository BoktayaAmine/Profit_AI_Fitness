package ma.ensa.mobile.profit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.models.Objectif;
import ma.ensa.mobile.profit.retrofit.RetrofitClient;
import ma.ensa.mobile.profit.services.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjectifAdapter extends RecyclerView.Adapter<ObjectifAdapter.ViewHolder> {

    private Context context;
    private final List<Objectif> objectifs;

    public ObjectifAdapter(List<Objectif> objectifs) {
        this.objectifs = objectifs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_objectif, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Objectif objectif = objectifs.get(position);

        // Set name
        holder.nameTextView.setText(objectif.getNom());

        // Personalize value based on type
        String valueText;
        switch (objectif.getType()) {
            case "COUNT":
                valueText = String.format("%s: %d", objectif.getType(), (int) objectif.getValue());
                break;
            case "DISTANCE":
                valueText = String.format("%s: %.2f km", objectif.getType(), objectif.getValue());
                break;
            case "DURATION":
                valueText = String.format("%s: %.2f min", objectif.getType(), objectif.getValue());
                break;
            default:
                valueText = String.format("%s: %.2f", objectif.getType(), objectif.getValue());
        }
        holder.valueTextView.setText(valueText);

        // Load the image using Glide with local drawable
        int imageResId = getDrawableResourceId(objectif.getImage());
        if (imageResId != 0) {
            Glide.with(context)
                    .load(imageResId)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.circle_background);
        }

        // Set checkbox state
        holder.checkBox.setChecked(objectif.isDone());

        // Handle checkbox changes
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            objectif.setDone(isChecked);

            AuthService authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
            int color = isChecked ? R.color.green_light : R.color.white;
            holder.itemView.setBackgroundColor(context.getColor(color));

            authService.updateObjectifDone(objectif.getId(), isChecked).enqueue(new Callback<Objectif>() {
                @Override
                public void onResponse(Call<Objectif> call, Response<Objectif> response) {
                    if (!response.isSuccessful()) {
                        holder.checkBox.setChecked(!isChecked);
                        Toast.makeText(context, "Failed to update objectif", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Objectif> call, Throwable t) {
                    holder.checkBox.setChecked(!isChecked);
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Apply animation
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
    }

    // Helper method to get object at position for swipe functionality
    public Objectif getObjectifAt(int position) {
        return objectifs.get(position);
    }

    // Helper method to remove item after successful deletion
    public void removeItem(int position) {
        objectifs.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, objectifs.size());
    }

    private int getDrawableResourceId(String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

    @Override
    public int getItemCount() {
        return objectifs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, valueTextView;
        ImageView imageView;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_objectif_name);
            valueTextView = itemView.findViewById(R.id.item_objectif_value);
            imageView = itemView.findViewById(R.id.item_objectif_image);
            checkBox = itemView.findViewById(R.id.item_objectif_checkbox);
        }
    }
}