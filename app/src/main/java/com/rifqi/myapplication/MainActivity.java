package com.rifqi.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private RoomDB database;
    private List<TaskData> taskData = new ArrayList<>();
    private ListTaskAdapter listTaskAdapter;
    private Dialog dialog;
    private String date;

    EditText etTask, etDate;
    ImageView dtPick;
    Button addTask;

    @BindView(R.id.count_task)
    TextView tvCount;
    @BindView(R.id.rv_task)
    RecyclerView rvTask;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.btn_select)
    Button btnSelect;
    @BindView(R.id.add_item)
    ImageView addItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        database = RoomDB.getInstance(getApplicationContext());

        fetchData();
        showRecycleView();
    }

    private void showRecycleView() {
        rvTask.setLayoutManager(new LinearLayoutManager(this));
        listTaskAdapter = new ListTaskAdapter(this, taskData);
        rvTask.setAdapter(listTaskAdapter);
    }

    void fetchData() {
        taskData = database.taskDao().getAllData();
        database.taskDao().getAllLiveData().observe(this, taskData ->{
            listTaskAdapter.updateData(taskData);
        });
        database.taskDao().getCountData().observe(this, integer ->{
            if (integer == 0){
                tvCount.setText("Tidak ada tugas");
            }else{
                tvCount.setText(integer + " Tugas");
            }
        });
        database.taskDao().getCountDoneData().observe(this, integer ->{
            if (integer == 3){
                btnDelete.setText("Delete All");
            }else{
                btnDelete.setText("Hapus " + integer);
            }
        });
    }

    @OnClick(R.id.add_item)
    public void popUpAddTask(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.pop_up_add_item);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();

        etTask = dialog.findViewById(R.id.et_task);
        etDate = dialog.findViewById(R.id.et_tanggal);
        dtPick = dialog.findViewById(R.id.date_picker_img);
        addTask = dialog.findViewById(R.id.add_task);

        dtPick.setOnClickListener(view -> {
            showDatePickerDialog();
        });
        etDate.setFocusable(false);
        etDate.setClickable(true);

        addTask.setOnClickListener(view -> {
            String isiTask, tanggal;
            isiTask = etTask.getText().toString().trim();
            tanggal = etDate.getText().toString().trim();
            if (isiTask.isEmpty() || tanggal.isEmpty()){
                Toast.makeText(this, "Tugas dan Tanggal Harus Terisi", Toast.LENGTH_SHORT).show();
                return;
            }else {
                TaskData data = new TaskData();
                data.setText(isiTask);
                data.setDate(tanggal);
                data.setStatus(0);
                database.taskDao().insert(data);
                dialog.dismiss();
            }
        });

    }
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePickerDialog.OnDateSetListener) this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dateOfDay) {
        Date d = new Date();
        int cDate  = Integer.parseInt((String) DateFormat.format("d", d.getTime()));
        int cMonth  = Integer.parseInt((String) DateFormat.format("M", d.getTime()));
        int cYear  = Integer.parseInt((String) DateFormat.format("yyyy", d.getTime()));
        if (year > cYear || year < 2011){
            Toast.makeText(this, "Tahun tidak valid", Toast.LENGTH_SHORT).show();
        }else if (year == cYear && (month+1) > cMonth || year == 2011 && (month+1) < 7){
            Toast.makeText(this, "Tahun tidak valid", Toast.LENGTH_SHORT).show();
        }else if (year == cYear && (month+1) == cMonth && dateOfDay > cDate || year == 2011 && (month+1) == 7 && dateOfDay < 16){
            Toast.makeText(this, "Tahun tidak valid", Toast.LENGTH_SHORT).show();
        }else {
            date = dateOfDay + " - " + (month+1) + " - " + year;
            etDate.setText(date);
        }

//        if (year > cYear || (month+1) > cMonth){
//            Toast.makeText(this, "Tahun tidak valid", Toast.LENGTH_SHORT).show();
//        }else {
//            date = dateOfDay + " - " + (month+1) + " - " + year;
//            etDate.setText(date);
//        }
    }
    @OnClick(R.id.btn_select)
    public void selectAll(){
        database.taskDao().updateSelectAll();
    }
    @OnClick(R.id.btn_delete)
    public void deleteAll(){
        database.taskDao().deleteAllDone();
    }
}