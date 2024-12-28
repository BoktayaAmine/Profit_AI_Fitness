package ma.ensa.mobile.profit.ui.bmi;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import ma.ensa.mobile.profit.R;
import ma.ensa.mobile.profit.ui.MainActivity;
import ma.ensa.mobile.profit.ui.WelcomActivity;

public class BmicalcActivity extends AppCompatActivity {

    TextView mcurrentheight;
    TextView mcurrentweight, mcurrentage;
    ImageView mincrementage, mdecrementage, mincrementweight, mdecrementweight;
    SeekBar mseekbarforheight;
    Button mcalculatebmi;
    RelativeLayout mmale, mfemale;

    int intweight = 55;
    int intage = 22;
    int currentprogress;
    String mintprogress = "170";
    String typerofuser = "0";
    String weight2 = "55";
    String age2 = "22";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bmicalc);  // Ensure this points to the correct layout

        // Set up the default toolbar with a back arrow

        // Initialize views
        mcurrentage = findViewById(R.id.currentage);
        mcurrentweight = findViewById(R.id.currentweight);
        mcurrentheight = findViewById(R.id.currentheight);
        mincrementage = findViewById(R.id.incrementage);
        mdecrementage = findViewById(R.id.decrementage);
        mincrementweight = findViewById(R.id.incremetweight);
        mdecrementweight = findViewById(R.id.decrementweight);
        mcalculatebmi = findViewById(R.id.calculatebmi);
        mseekbarforheight = findViewById(R.id.seekbarforheight);
        mmale = findViewById(R.id.male);
        mfemale = findViewById(R.id.female);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }



        mmale.setOnClickListener(v -> {
            mmale.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.malefemalefocus));
            mfemale.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.malefemalenotfocus));
            typerofuser = "Male";
        });

        mfemale.setOnClickListener(v -> {
            mfemale.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.malefemalefocus));
            mmale.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.malefemalenotfocus));
            typerofuser = "Female";
        });

        mseekbarforheight.setMax(300);
        mseekbarforheight.setProgress(170);
        mseekbarforheight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentprogress = progress;
                mintprogress = String.valueOf(currentprogress);
                mcurrentheight.setText(mintprogress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mincrementweight.setOnClickListener(v -> {
            intweight++;
            weight2 = String.valueOf(intweight);
            mcurrentweight.setText(weight2);
        });

        mincrementage.setOnClickListener(v -> {
            intage++;
            age2 = String.valueOf(intage);
            mcurrentage.setText(age2);
        });

        mdecrementage.setOnClickListener(v -> {
            intage--;
            age2 = String.valueOf(intage);
            mcurrentage.setText(age2);
        });

        mdecrementweight.setOnClickListener(v -> {
            intweight--;
            weight2 = String.valueOf(intweight);
            mcurrentweight.setText(weight2);
        });

        mcalculatebmi.setOnClickListener(v -> {
            if (typerofuser.equals("0")) {
                Toast.makeText(getApplicationContext(), "Select Your Gender First", Toast.LENGTH_SHORT).show();
            } else if (mintprogress.equals("0")) {
                Toast.makeText(getApplicationContext(), "Select Your Height First", Toast.LENGTH_SHORT).show();
            } else if (intage == 0 || intage < 0) {
                Toast.makeText(getApplicationContext(), "Age is Incorrect", Toast.LENGTH_SHORT).show();
            } else if (intweight == 0 || intweight < 0) {
                Toast.makeText(getApplicationContext(), "Weight Is Incorrect", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(BmicalcActivity.this, BmiActivity.class);
                intent.putExtra("gender", typerofuser);
                intent.putExtra("height", mintprogress);
                intent.putExtra("weight", weight2);
                intent.putExtra("age", age2);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




}
