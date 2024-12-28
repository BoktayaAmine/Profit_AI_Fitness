package ma.ensa.mobile.profit.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.models.Exercise;
import ma.ensa.mobile.profit.ui.ExerciseActivity;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private Context context;
    private List<Exercise> exercises;
    private String userNiveau; // User's level

    public ExerciseAdapter(Context context, List<Exercise> exercises, String userNiveau) {
        this.context = context;
        this.exercises = exercises;
        this.userNiveau = userNiveau;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.nameTextView.setText(exercise.getNom());

        // Set background color based on exercise level
        FrameLayout frameLayout = holder.itemView.findViewById(R.id.frame_layout);
        switch (exercise.getNiveau().toLowerCase()) {
            case "beginner":
                frameLayout.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.beginner_green)));
                break;
            case "intermediate":
                frameLayout.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.intermediate_yellow)));
                break;
            case "advanced":
                frameLayout.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.advanced_red)));
                break;
        }

        // Load the exercise image using Glide
        int imageResId = getDrawableResourceId(exercise.getImage());
        if (imageResId != 0) {
            Glide.with(context)
                    .load(imageResId)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.circle_background); // Default image
        }

        // Determine if the item is clickable based on user level
        boolean isClickable = isExerciseClickable(exercise.getNiveau());
        holder.itemView.setAlpha(isClickable ? 1.0f : 0.5f); // Dim non-clickable items
        holder.itemView.setClickable(isClickable);

        if (isClickable) {
            holder.itemView.setOnClickListener(v -> {
                if (context instanceof ExerciseActivity) {
                    ((ExerciseActivity) context).showExerciseDialog(exercise);
                }
            });
        } else {
            // Ensure no click listener is set for non-clickable items
            holder.itemView.setOnClickListener(null);
        }
    }

    private int getDrawableResourceId(String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }

    private boolean isExerciseClickable(String exerciseNiveau) {
        if (userNiveau == null) return true; // If no user level is available, all items are clickable

        switch (userNiveau.toLowerCase()) {
            case "beginner":
                return exerciseNiveau.toLowerCase().equals("beginner") ||
                        exerciseNiveau.toLowerCase().equals("intermediate");
            case "intermediate":
                return true; // All exercises are clickable for intermediate users
            case "advanced":
                return true; // All exercises are clickable for advanced users
            default:
                return true;
        }
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView imageView;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.exercise_name);
            imageView = itemView.findViewById(R.id.exercise_image);
        }
    }
}
