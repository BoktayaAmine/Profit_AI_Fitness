/* package ma.ensa.mobile.profit.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.ui.ChatMainActivity;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Charger le layout du fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Exemple de configuration des TextViews pour un affichage statique
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        TextView tvWorkout = view.findViewById(R.id.tvWorkout);
        TextView tvTemp = view.findViewById(R.id.tvTemp);

        // Affecter des valeurs statiques pour l'affichage
        tvUserName.setText("Hello John Doe");
        tvWorkout.setText("Workouts Completed: 10");
        tvTemp.setText("75°F");

        View fabChatBot = view.findViewById(R.id.fabChatBot);
        fabChatBot.setOnClickListener(v -> {
            // Start ChatMainActivity when FAB is clicked
            Intent intent = new Intent(getActivity(), ChatMainActivity.class);
            startActivity(intent);
        });
    }
}
*/