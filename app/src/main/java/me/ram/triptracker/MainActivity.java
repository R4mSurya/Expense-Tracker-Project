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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    Expense expense;
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

        FloatingActionButton addExpenseFab = findViewById(R.id.addExpenseFab);
        addExpenseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddExpenseDialog();
            }
        });


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
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String expenseName = expenseNameInput.getText().toString();
                double amount = Double.parseDouble(amountInput.getText().toString().isEmpty() ? "0" : amountInput.getText().toString());
                String category = categorySpinner.getSelectedItem().toString();
                String date = dateInput.getText().toString();

                // Create a new expense object
                //Expense expense = new Expense(expenseName, amount, category, date);
                addDatatoFirebase(expenseName, amount, category, date);
                // Save expense to Firebase
                //String expenseId = databaseReference.push().getKey();


                // Dismiss dialog after saving
                dialog.dismiss();
            }
        });



        // Show the dialog
        dialog.show();
    }
    private void addDatatoFirebase(String name, Double amount, String category, String date) {
        // below 3 lines of code is used to set
        // data in our object class.
        expense.setName(name);
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(date);


        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                databaseReference.setValue(expense);

                // after adding this data we are showing toast message.
                Toast.makeText(MainActivity.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(MainActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}