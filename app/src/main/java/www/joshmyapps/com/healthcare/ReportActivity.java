package www.joshmyapps.com.healthcare;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends LocationActivity {

    private TextView mTvLongitude;
    private TextView mTvLatitude;
    private TextView mTvDays;
    private Spinner mCaseSpinner;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mTvLongitude = findViewById(R.id.longitude_text_view);
        mTvLatitude = findViewById(R.id.latitude_text_view);
        mCaseSpinner = findViewById(R.id.case_spinner);
        mTvDays = findViewById(R.id.days_text_view);
        mRadioGroup = findViewById(R.id.medication_radio_group);
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        longitude = String.valueOf(location.getLongitude());
        latitude = String.valueOf(location.getLatitude());
        mTvLatitude.setText(latitude);
        mTvLongitude.setText(longitude);
    }

    public void reportCase(View view) {
        int selectedId = mRadioGroup.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        if (!(selectedId == -1)) {
            mRadioButton = findViewById(selectedId);
        }
        if (mRadioButton != null) {
            String currentCase = mCaseSpinner.getSelectedItem().toString();
            String tookMeds = mRadioButton.getText().toString();
            String days = mTvDays.getText().toString();
            Map<String, String> report = new HashMap<>();
            report.put("case", currentCase);
            report.put("tookMeds", tookMeds);
            report.put("days", days);
            report.put("latitude", latitude);
            report.put("longitude", longitude);
            sendToFirebase(report);
        }

    }

    private void sendToFirebase(Map<String, String> report) {
        FirebaseUser vFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore vFirebaseFirestore = FirebaseFirestore.getInstance();
        if (vFirebaseUser != null) {
            vFirebaseFirestore.collection("reports")
                    .document(vFirebaseUser.getUid())
                    .collection(report.get("case"))
                    .document(new Timestamp(new Date().getTime()).toString())
                    .set(report)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(DefaulterActivity.class.getSimpleName(), "Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(DefaulterActivity.class.getSimpleName(), e.toString());
                        }
                    });
        }
    }

    public void reduceDays(View view) {
        int days = Integer.parseInt(mTvDays.getText().toString());
        if (days > 0) {
            mTvDays.setText(String.valueOf(days - 1));
            return;
        }
        Toast.makeText(ReportActivity.this, "Invalid operation", Toast.LENGTH_LONG).show();
    }

    public void increaseDays(View view) {
        int days = Integer.parseInt(mTvDays.getText().toString()) + 1;
        mTvDays.setText(String.valueOf(days));
    }
}
