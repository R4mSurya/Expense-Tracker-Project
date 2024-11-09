package me.ram.triptracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ExpenseAdapter extends ArrayAdapter<Expense> {
    public ExpenseAdapter(@NonNull Context context, @NonNull List<Expense> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_expense, parent, false);
        }

        // Get the current expense object
        Expense expense = getItem(position);

        // Bind data to the TextViews
        TextView expenseName = convertView.findViewById(R.id.expenseName);
        TextView amount = convertView.findViewById(R.id.amount);
        TextView category = convertView.findViewById(R.id.category);
        TextView date = convertView.findViewById(R.id.date);

        // Set values to the corresponding views
        expenseName.setText(expense.getExpenseName());
        amount.setText(String.format("$%.2f", expense.getAmount()));
        category.setText(expense.getCategory());
        date.setText(expense.getDate());

        return convertView;
    }
}
