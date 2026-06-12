package com.example.electricitybillcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity {

    private ListView listViewBills;
    private Button btnBack;
    private DatabaseHelper databaseHelper;
    private List<BillRecord> billList;
    private BillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        listViewBills = findViewById(R.id.listViewBills);
        btnBack = findViewById(R.id.btnBack);
        databaseHelper = new DatabaseHelper(this);

        loadBillList();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listViewBills.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BillRecord selectedBill = billList.get(position);
                Intent intent = new Intent(ListViewActivity.this, DetailActivity.class);
                intent.putExtra("BILL_ID", selectedBill.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBillList(); // Refresh list when coming back from edit/delete
    }

    private void loadBillList() {
        billList = databaseHelper.getAllBills();
        adapter = new BillAdapter(this, billList);
        listViewBills.setAdapter(adapter);
    }

    class BillAdapter extends ArrayAdapter<BillRecord> {
        private Context context;
        private List<BillRecord> bills;

        public BillAdapter(Context context, List<BillRecord> bills) {
            super(context, R.layout.list_item_bill, bills);
            this.context = context;
            this.bills = bills;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_bill, parent, false);
            }

            BillRecord bill = bills.get(position);

            TextView txtMonth = convertView.findViewById(R.id.txtMonth);
            TextView txtFinalCost = convertView.findViewById(R.id.txtFinalCost);

            txtMonth.setText(bill.getMonth());
            txtFinalCost.setText(String.format("RM %.2f", bill.getFinalCost()));

            return convertView;
        }
    }
}
