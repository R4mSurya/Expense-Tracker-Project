package me.ram.triptracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("expenses");

        FloatingActionButton addExpenseFab = findViewById(R.id.addExpenseFab);
        addExpenseFab.setOnClickListener(v -> showAddExpenseDialog());
    }

    private void showAddExpenseDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_expense, null);

        // Create AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Add Expense")
                .create();

        // Expense Name Input
        EditText expenseNameInput = dialogView.findViewById(R.id.expenseNameInput);

        // Amount Input
        EditText amountInput = dialogView.findViewById(R.id.amountInput);

        // Category Spinner
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        String[] categories = {"Accommodation", "Transportation", "Food", "Activities", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Date Input
        EditText dateInput = dialogView.findViewById(R.id.dateInput);

        // Save Button
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            // Get input values
            String expenseName = expenseNameInput.getText().toString();
            double amount = Double.parseDouble(amountInput.getText().toString().isEmpty() ? "0" : amountInput.getText().toString());
            String category = categorySpinner.getSelectedItem().toString();
            String date = dateInput.getText().toString();

            // Add data to Firebase
            addDatatoFirebase(expenseName, amount, category, date);

            // Dismiss dialog after saving
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

    private void addDatatoFirebase(String name, Double amount, String category, String date) {
        // Create a new Expense object
        Expense expense = new Expense(name, amount, category, date);

        // Add the expense to Firebase using a unique key
        String expenseId = databaseReference.push().getKey();
        if (expenseId != null) {
            databaseReference.child(expenseId).setValue(expense)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Expense added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to add expense", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
