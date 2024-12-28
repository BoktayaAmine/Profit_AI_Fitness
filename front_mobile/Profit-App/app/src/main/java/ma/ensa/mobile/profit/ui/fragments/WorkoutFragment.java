package ma.ensa.mobile.profit.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import ma.ensa.mobile.profit.R;
import android.content.Intent;


public class WorkoutFragment extends Fragment {

    CardView cd1, cd2, cd3, cd4, cd5, cd6, cd7, cd8;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Lier le fragment à son layout
        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);

        // Initialiser les CardViews
        cd1 = rootView.findViewById(R.id.pushcard);
        cd2 = rootView.findViewById(R.id.pullcard);
        cd3 = rootView.findViewById(R.id.squartcard);
        cd4 = rootView.findViewById(R.id.tplankcard);
        cd5 = rootView.findViewById(R.id.deadbugcard);
        cd6 = rootView.findViewById(R.id.skippingcard);
        cd7 = rootView.findViewById(R.id.heavysqcard);
        cd8 = rootView.findViewById(R.id.splitjumpcard);

        // Ajouter les listeners pour chaque CardView
        setCardClickListener(cd1, "https://www.youtube.com/watch?v=IODxDxX7oi4");
        setCardClickListener(cd2, "https://www.youtube.com/watch?v=eGo4IYlbE5g");
        setCardClickListener(cd3, "https://www.youtube.com/watch?v=YaXPRqUwItQ");
        setCardClickListener(cd4, "https://www.youtube.com/watch?v=rTY5mqJ1HNo");
        setCardClickListener(cd5, "https://www.youtube.com/watch?v=4XLEnwUr1d8");
        setCardClickListener(cd6, "https://www.youtube.com/watch?v=u3zgHI8QnqE");
        setCardClickListener(cd7, "https://www.youtube.com/watch?v=Uv_DKDl7EjA");
        setCardClickListener(cd8, "https://www.youtube.com/watch?v=qsF1gYTWTrQ");

        return rootView;
    }

    private void setCardClickListener(CardView cardView, String url) {
        cardView.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Opening Youtube!", Toast.LENGTH_SHORT).show();
            gotoUri(url);
        });
    }

    private void gotoUri(String url) {
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
