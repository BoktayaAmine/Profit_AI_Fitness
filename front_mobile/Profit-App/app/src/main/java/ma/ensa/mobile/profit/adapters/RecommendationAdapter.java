package ma.ensa.mobile.profit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.models.Exercise;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {
    private final List<Exercise> exercises;
    private Context context;

    public RecommendationAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.nameTextView.setText(exercise.getNom());
        holder.typeTextView.setText(exercise.getType());
        holder.levelTextView.setText(exercise.getNiveau());

        // You can add image loading here using Glide or Picasso
        // Example with Glide:
//         Glide.with(holder.itemView.getContext())
//             .load(exercise.getImage())
//             .into(holder.exerciseImageView);

        loadImage(exercise.getImage(), holder.exerciseImageView);
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView typeTextView;
        public TextView levelTextView;
        public ImageView exerciseImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.exercise_name);
            typeTextView = itemView.findViewById(R.id.exercise_type);
            levelTextView = itemView.findViewById(R.id.exercise_level);
            exerciseImageView = itemView.findViewById(R.id.exercise_image);
        }
    }

    private void loadImage(String imageName, ImageView imageView) {
        int imageResId = getDrawableResourceId(imageName);
        if (imageResId != 0) {
            // Load image from drawable resources
            Glide.with(context)
                    .load(imageResId)
                    .into(imageView);
        } else {
            // Load image from URL
            Glide.with(context)
                    .load(imageName) // Assuming imageName is a URL
                    .into(imageView);
        }
    }

    private int getDrawableResourceId(String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

}