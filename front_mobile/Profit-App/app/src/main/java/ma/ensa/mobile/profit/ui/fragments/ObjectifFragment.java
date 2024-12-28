package ma.ensa.mobile.profit.ui.fragments;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.adapters.ObjectifAdapter;
import ma.ensa.mobile.profit.models.Objectif;
import ma.ensa.mobile.profit.retrofit.RetrofitClient;
import ma.ensa.mobile.profit.services.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjectifFragment extends Fragment {

    private RecyclerView recyclerView;
    private ObjectifAdapter adapter;
    private Long userId;
    private AuthService authService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_objectif, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_objectifs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);

        if (getActivity() != null) {
            userId = getActivity().getIntent().getLongExtra("USER_ID", -1);
            if (userId != -1) {
                fetchObjectifs();
            } else {
                Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            }
        }

        setupSwipeToDelete();
        return view;
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Objectif objectif = adapter.getObjectifAt(position);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Objective")
                        .setMessage("Are you sure you want to delete this objective?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteObjectif(objectif, position))
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Reset the item's position
                            adapter.notifyItemChanged(position);
                        })
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX * 0.5f, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);
    }

    private void deleteObjectif(Objectif objectif, int position) {
        authService.deleteObjectif(objectif.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.removeItem(position);
                    Toast.makeText(getContext(), "Objective deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to delete objective", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                adapter.notifyItemChanged(position);
            }
        });
    }

    private void fetchObjectifs() {
        authService.getObjectifs(userId).enqueue(new Callback<List<Objectif>>() {
            @Override
            public void onResponse(Call<List<Objectif>> call, Response<List<Objectif>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new ObjectifAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch objectifs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Objectif>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}