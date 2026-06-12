package com.example.electricitybillcalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

public class DetailActivity extends AppCompatActivity {

    private TextView detailMonth, detailKWh, detailTotalCharges, detailRebate, detailFinalCost;
    private Button btnEdit, btnDelete, btnBack;
    private DatabaseHelper databaseHelper;
    private int billId;
    private BillRecord currentBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailMonth = findViewById(R.id.detailMonth);
        detailKWh = findViewById(R.id.detailKWh);
        detailTotalCharges = findViewById(R.id.detailTotalCharges);
        detailRebate = findViewById(R.id.detailRebate);
        detailFinalCost = findViewById(R.id.detailFinalCost);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        detailMonth.setTextColor(Color.BLACK);
        detailKWh.setTextColor(Color.BLACK);
        detailTotalCharges.setTextColor(Color.BLACK);
        detailRebate.setTextColor(Color.BLACK);

        databaseHelper = new DatabaseHelper(this);

        billId = getIntent().getIntExtra("BILL_ID", -1);

        if (billId != -1) {
            loadBillDetails();
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadBillDetails() {
        currentBill = databaseHelper.getBillById(billId);

        if (currentBill != null) {
            detailMonth.setText(currentBill.getMonth());
            detailKWh.setText(String.valueOf(currentBill.getKWh()) + " kWh");
            detailTotalCharges.setText(String.format("RM %.2f", currentBill.getTotalCharges()));
            detailRebate.setText(currentBill.getRebatePercent() + "%");
            detailFinalCost.setText(String.format("RM %.2f", currentBill.getFinalCost()));
        }
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Bill Record");

        final View view = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        final EditText editKWh = view.findViewById(R.id.editKWh);
        final SeekBar seekBarRebate = view.findViewById(R.id.seekBarRebate);
        final TextView txtRebateValue = view.findViewById(R.id.txtRebateValue);

        editKWh.setText(String.valueOf(currentBill.getKWh()));
        seekBarRebate.setProgress(currentBill.getRebatePercent());
        txtRebateValue.setText(currentBill.getRebatePercent() + "%");

        seekBarRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtRebateValue.setText(progress + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        builder.setView(view);

        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String kWhInput = editKWh.getText().toString().trim();
                int rebatePercent = seekBarRebate.getProgress();

                if (kWhInput.isEmpty()) {
                    Toast.makeText(DetailActivity.this, "Please enter kWh!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int kWh = Integer.parseInt(kWhInput);

                if (kWh < 1 || kWh > 1000) {
                    Toast.makeText(DetailActivity.this, "kWh must be 1-1000!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double totalCharges = calculateTotalCharges(kWh);
                double finalCost = totalCharges - (totalCharges * rebatePercent / 100);

                boolean isUpdated = databaseHelper.updateBill(
                        billId, currentBill.getMonth(), kWh, totalCharges, rebatePercent, finalCost
                );

                if (isUpdated) {
                    Toast.makeText(DetailActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    loadBillDetails(); // Refresh display
                } else {
                    Toast.makeText(DetailActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this record?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isDeleted = databaseHelper.deleteBill(billId);
                        if (isDeleted) {
                            Toast.makeText(DetailActivity.this, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to list
                        } else {
                            Toast.makeText(DetailActivity.this, "Delete failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private double calculateTotalCharges(int kWh) {
        double total = 0;
        if (kWh <= 200) {
            total = kWh * 0.218;
        } else if (kWh <= 300) {
            total = 200 * 0.218 + (kWh - 200) * 0.334;
        } else if (kWh <= 600) {
            total = 200 * 0.218 + 100 * 0.334 + (kWh - 300) * 0.516;
        } else {
            total = 200 * 0.218 + 100 * 0.334 + 300 * 0.516 + (kWh - 600) * 0.546;
        }
        return total;
    }
}