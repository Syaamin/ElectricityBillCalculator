package com.example.electricitybillcalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.content.Intent;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private EditText editKWh;
    private SeekBar seekBarRebate;
    private TextView txtRebateValue, txtTotalCharges, txtFinalCost, txtSaveMessage;
    private Button btnCalculate, btnViewList, btnAbout;

    private DatabaseHelper databaseHelper;

    private String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);


        spinnerMonth = findViewById(R.id.spinnerMonth);
        editKWh = findViewById(R.id.editKWh);
        seekBarRebate = findViewById(R.id.seekBarRebate);
        txtRebateValue = findViewById(R.id.txtRebateValue);
        txtTotalCharges = findViewById(R.id.txtTotalCharges);
        txtFinalCost = findViewById(R.id.txtFinalCost);
        txtSaveMessage = findViewById(R.id.txtSaveMessage);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnViewList = findViewById(R.id.btnViewList);
        btnAbout = findViewById(R.id.btnAbout);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, months) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);


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


        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAndSave();
            }
        });

        btnViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                startActivity(intent);
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void calculateAndSave() {

        String month = spinnerMonth.getSelectedItem().toString();
        String kWhInput = editKWh.getText().toString().trim();
        int rebatePercent = seekBarRebate.getProgress();

        if (kWhInput.isEmpty()) {
            editKWh.setError("Please enter units used!");
            editKWh.requestFocus();
            return;
        }

        int kWh = Integer.parseInt(kWhInput);

        if (kWh < 1 || kWh > 1000) {
            editKWh.setError("Units must be between 1 and 1000 kWh!");
            editKWh.requestFocus();
            return;
        }

        double totalCharges = calculateTotalCharges(kWh);
        double finalCost = totalCharges - (totalCharges * rebatePercent / 100);

        txtTotalCharges.setText(String.format("Total Charges: RM %.2f", totalCharges));
        txtFinalCost.setText(String.format("Final Cost: RM %.2f", finalCost));

        boolean isSaved = databaseHelper.insertBill(month, kWh, totalCharges, rebatePercent, finalCost);

        if (isSaved) {
            txtSaveMessage.setText("Saved successfully!");
            txtSaveMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

            editKWh.setText("");
            spinnerMonth.setSelection(0);
            seekBarRebate.setProgress(0);

        } else {
            txtSaveMessage.setText("Failed to save!");
            txtSaveMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                txtSaveMessage.setText("");
            }
        }, 3000);
    }

    private double calculateTotalCharges(int kWh) {
        double total = 0;

        if (kWh <= 200) {
            total = kWh * 0.218;
        }
        else if (kWh <= 300) {
            total = 200 * 0.218;
            total += (kWh - 200) * 0.334;
        }
        else if (kWh <= 600) {
            total = 200 * 0.218;
            total += 100 * 0.334;
            total += (kWh - 300) * 0.516;
        }
        else {
            total = 200 * 0.218;
            total += 100 * 0.334;
            total += 300 * 0.516;
            total += (kWh - 600) * 0.546;
        }

        return total;
    }
}