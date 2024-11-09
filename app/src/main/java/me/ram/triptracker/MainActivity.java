package me.ram.triptracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private ListView listView;
    private ArrayAdapter<Expense> adapter;
    private List<Expense> expenseList;



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
        expenseList = new ArrayList<>();
        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("expenses");
        listView = findViewById(R.id.listView); // Assuming you have a ListView in your XML layout
        adapter = new ExpenseAdapter(this, expenseList);
        listView.setAdapter(adapter);
        getData();
        FloatingActionButton addExpenseFab = findViewById(R.id.addExpenseFab);
        addExpenseFab.setOnClickListener(v -> showAddExpenseDialog());
        //
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
        String expenseId = databaseReference.push().getKey();
        Expense expense = new Expense(expenseId, name, amount, category, date);

        // Add the expense to Firebase using a unique key

        if (expenseId != null) {
            databaseReference.child(expenseId).setValue(expense)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Expense added", Toast.LENGTH_SHORT).show();
                            //getData();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to add expense", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void getData() {
        // Listen for changes to the 'expenses' node in Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the current list to prevent duplicates when updating the list
                expenseList.clear();

                // Loop through the children of the snapshot (each expense entry)
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    // Get each expense object
                    Expense expense = expenseSnapshot.getValue(Expense.class);

                    // Add the expense object to the list
                    if (expense != null) {
                        expenseList.add(expense);
                    }
                }

                // Sort expenses by date or any other field if necessary
                Collections.sort(expenseList, (e1, e2) -> e1.getDate().compareTo(e2.getDate()));

                // Update the ListView by notifying the adapter
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if the data retrieval fails
                Toast.makeText(MainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }




}
