package ma.ensa.mobile.profit.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.models.Goal;
import ma.ensa.mobile.profit.services.GoalService;
import ma.ensa.mobile.profit.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private final Context context;
    private final List<Goal> goals;

    public GoalAdapter(Context context, List<Goal> goals) {
        this.context = context;
        this.goals = goals;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.titleTextView.setText(goal.getTitle());
        holder.descriptionTextView.setText(goal.getDescription());
        holder.checkbox.setChecked(goal.isCompleted());

        // Écoutez les changements de la CheckBox
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            goal.setCompleted(isChecked);
            if (isChecked) {
                completeGoal(goal);
            } else {
                incompleteGoal(goal);
            }
            // Mettre à jour l'affichage du texte barré
            updateTextStrikeThrough(holder.titleTextView, isChecked);
        });

        // Mettre à jour l'affichage du texte barré
        updateTextStrikeThrough(holder.titleTextView, goal.isCompleted());
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public void removeItem(int position) {
        goals.remove(position);
        notifyItemRemoved(position);
    }

    private void completeGoal(Goal goal) {
        GoalService goalService = RetrofitClient.getRetrofitInstance().create(GoalService.class);
        Call<Goal> call = goalService.completeGoal(goal.getId());
        call.enqueue(new Callback<Goal>() {
            @Override
            public void onResponse(Call<Goal> call, Response<Goal> response) {
                if (response.isSuccessful()) {
                    goal.setCompleted(true);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Failed to mark goal as complete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Goal> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void incompleteGoal(Goal goal) {
        GoalService goalService = RetrofitClient.getRetrofitInstance().create(GoalService.class);
        Call<Goal> call = goalService.completeGoal(goal.getId());
        call.enqueue(new Callback<Goal>() {
            @Override
            public void onResponse(Call<Goal> call, Response<Goal> response) {
                if (response.isSuccessful()) {
                    goal.setCompleted(false);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Failed to mark goal as complete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Goal> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTextStrikeThrough(TextView textView, boolean isCompleted) {
        if (isCompleted) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        CheckBox checkbox;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.goal_title);
            descriptionTextView = itemView.findViewById(R.id.goal_description);
            checkbox = itemView.findViewById(R.id.goal_checkbox); // Assurez-vous que l'ID correspond à votre layout
        }
    }
}