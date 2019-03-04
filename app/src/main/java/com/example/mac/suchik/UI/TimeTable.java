package com.example.mac.suchik.UI;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac.suchik.ClothesData;
import com.example.mac.suchik.DBOperations;
import com.example.mac.suchik.R;
import com.example.mac.suchik.UI.main_window.RecomendationListAdapter;
import com.example.mac.suchik.UI.settings_page.TimesListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeTable extends Fragment implements AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener,
        View.OnClickListener, ItemAdapter.ItemClickListener {
    private String name, color;
    private int category = -1;
    private int minT = 100;
    private int maxT = 100;
    private int rain = 100;
    private int wind = 100;
    private int cloud = 100;

    private RadioGroup radioGroupRain;
    private RadioGroup radioGroupWind;
    private RadioGroup radioGroupCloud;

    private EditText editTextName;
    private EditText editTextColor;

    private Button add;

    private ClothesData item;

    private ItemAdapter adapter;

    DBOperations dbOperations;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbOperations = new DBOperations(getContext());
        return inflater.inflate(R.layout.add_item_fragment, container, false);
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        editTextColor = view.findViewById(R.id.color);
        editTextName = view.findViewById(R.id.name);

        add = view.findViewById(R.id.add_item);
        add.setOnClickListener(this);


        radioGroupRain = (RadioGroup) view.findViewById(R.id.rain);
        radioGroupRain.setOnCheckedChangeListener(this);

        radioGroupWind = (RadioGroup) view.findViewById(R.id.wind);
        radioGroupWind.setOnCheckedChangeListener(this);

        radioGroupCloud = (RadioGroup) view.findViewById(R.id.cloud);
        radioGroupCloud.setOnCheckedChangeListener(this);

        // Настраиваем адаптер
        ArrayAdapter<?> adapterCategories =
                ArrayAdapter.createFromResource(getContext(), R.array.categories, android.R.layout.simple_spinner_item);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinnerCategories = (Spinner) view.findViewById(R.id.spinner_category);
        spinnerCategories.setAdapter(adapterCategories);
        spinnerCategories.setOnItemSelectedListener(this);


        // Настраиваем адаптер
        ArrayAdapter<?> adapterTemp =
                ArrayAdapter.createFromResource(getContext(), R.array.temperature, android.R.layout.simple_spinner_item);
        adapterTemp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinnerTemp = (Spinner) view.findViewById(R.id.spinner_temp);
        spinnerTemp.setAdapter(adapterTemp);
        spinnerTemp.setOnItemSelectedListener(this);


        ArrayList<String> items = dbOperations.getData("clothes", new String[]{"name"}, null, null, null, null, null);


        RecyclerView recyclerView = view.findViewById(R.id.show_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemAdapter(getContext(), items);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_temp:
                switch (position){
                    case 1:
                        minT = -30; maxT = -15;
                        break;
                    case 2:
                        minT = -15; maxT = 0;
                        break;
                    case 3:
                        minT = 0; maxT = 15;
                        break;
                    case 4:
                        minT = 15; maxT = 30;
                        break;
                }
                String item = (String) parent.getItemAtPosition(position);
                //Toast.makeText(getBaseContext(), "item = " + item, Toast.LENGTH_SHORT).show();
                break;
            case R.id.spinner_category:
                category = position;
                //Toast.makeText(getBaseContext(), "item = " + position, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case -1:
                rain = 100; wind = 100; cloud = 100;
                break;
            case R.id.yes_rain:
                rain = 2;
                break;
            case R.id.no_rain:
                rain = 0;
                break;
            case R.id.do_not_matter_rain:
                rain = 1;
                break;
            case R.id.yes_wind:
                wind = 2;
                break;
            case R.id.no_wind:
                wind = 0;
                break;
            case R.id.do_not_matter_wind:
                wind = 1;
                break;
            case R.id.yes_cloud:
                cloud = 2;
                break;
            case R.id.no_cloud:
                cloud = 0;
                break;
            case R.id.do_not_matter_cloud:
                cloud = 1;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (category == -1){
            Toast.makeText(getContext(), "Выберете категорию", Toast.LENGTH_SHORT).show();
            return;
        }
        name = editTextName.getText().toString();
        if (name.isEmpty()){
            Toast.makeText(getContext(), "Введите название", Toast.LENGTH_SHORT).show();
            return;
        }
        if (minT == 100){
            Toast.makeText(getContext(), "Введите температурный режим", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rain == 100){
            Toast.makeText(getContext(), "Выберете водонепроницаемость", Toast.LENGTH_SHORT).show();
            return;
        }
        if (wind == 100){
            Toast.makeText(getContext(), "Выберете продуваемость", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cloud == 100){
            Toast.makeText(getContext(), "Выберете защиту от солнца", Toast.LENGTH_SHORT).show();
            return;
        }
        color = editTextColor.getText().toString();
//        if (color.isEmpty()){
//            Toast.makeText(getBaseContext(), "Введите цвет", Toast.LENGTH_SHORT).show();
//            return;
//        }
        Log.d("Clothes", "Category = " + category + " name = " + name + " rain = " + rain + " wind = " + wind + " cloud = " +
                " color = " + color);

        item = new ClothesData();

        item.category = category - 1;
        item.name = name;
        item.minTemp = minT;
        item.maxTemp = maxT;
        item.rain = rain;
        item.wind = wind;
        item.cloud = cloud;
        item.color = color;



        dbOperations.addItem(item);

        Toast.makeText(getContext(), "Добавлен элемент: " + item.name, Toast.LENGTH_SHORT).show();
        adapter.setList(dbOperations.getData("clothes", new String[]{"name"}, null, null, null, null, null));
    }

    @Override
    public void onItemClick(View view, int position) {
        buildAlertMessage(adapter.getItem(position));
        //Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    protected void buildAlertMessage(final String name) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Вы уверены, что хотите удалить этот элемент?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dbOperations.deleteItem(name);
                        adapter.setList(dbOperations.getData("clothes", new String[]{"name"}, null, null, null, null, null));
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
