package ma.ensa.mobile.profit.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.models.Goal;
import ma.ensa.mobile.profit.services.GoalService;
import ma.ensa.mobile.profit.retrofit.RetrofitClient;
import ma.ensa.mobile.profit.adapters.GoalAdapter;
import ma.ensa.mobile.profit.ui.RecommendationActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalFragment extends Fragment {

    private Long userId;
    private List<Goal> goalList;
    private GoalAdapter goalAdapter;
    private RecyclerView recyclerView;

    private Button showRecommendationsButton;


    public GoalFragment(Long userId) {
        this.userId = userId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        recyclerView = view.findViewById(R.id.goal_list);
        goalList = new ArrayList<>();
        goalAdapter = new GoalAdapter(getContext(), goalList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(goalAdapter);

        showRecommendationsButton = view.findViewById(R.id.button_show_recommendations);
        showRecommendationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecommendationActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        Button addButton = view.findViewById(R.id.add_goal_button);
        addButton.setOnClickListener(v -> showAddGoalDialog());

        loadUserGoals();
        setupSwipeToDelete();

        return view;
    }

    private void showAddGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_goal, null);
        builder.setView(dialogView);

        EditText titleInput = dialogView.findViewById(R.id.goal_title);
        EditText descriptionInput = dialogView.findViewById(R.id.goal_description);
        Button addButton = dialogView.findViewById(R.id.add_goal_button);

        AlertDialog dialog = builder.create();

        addButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();
            if (!title.isEmpty() && !description.isEmpty()) {
                Goal newGoal = new Goal(title, description, userId);
                createGoal(newGoal);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void loadUserGoals() {
        GoalService goalService = RetrofitClient.getRetrofitInstance().create(GoalService.class);
        Call<List<Goal>> call = goalService.getUserGoals(userId);
        call.enqueue(new Callback<List<Goal>>() {
            @Override
            public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    goalList.clear();
                    goalList.addAll(response.body());
                    goalAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load goals", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Goal>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Goal goalToDelete = goalList.get(position);
                deleteGoal(goalToDelete, position);
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    private void deleteGoal(Goal goal, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Goal")
                .setMessage("Are you sure you want to delete this goal?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    GoalService goalService = RetrofitClient.getRetrofitInstance().create(GoalService.class);
                    Call<Void> call = goalService.deleteGoal(goal.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                goalAdapter.removeItem(position);
                                Toast.makeText(getContext(), "Goal deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to delete goal", Toast.LENGTH_SHORT).show();
                                goalAdapter.notifyItemChanged(position);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            goalAdapter.notifyItemChanged(position);
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> goalAdapter.notifyItemChanged(position))
                .show();
    }

    private void createGoal(Goal goal) {
        GoalService goalService = RetrofitClient.getRetrofitInstance().create(GoalService.class);
        Call<Goal> call = goalService.createGoal(userId, goal);
        call.enqueue(new Callback<Goal>() {
            @Override
            public void onResponse(Call<Goal> call, Response<Goal> response) {
                if (response.isSuccessful()) {
                    goalList.add(response.body());
                    goalAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Goal added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add goal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Goal> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}