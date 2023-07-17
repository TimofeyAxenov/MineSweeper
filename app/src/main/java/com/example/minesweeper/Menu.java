package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minesweeper.databinding.ActivityMenuBinding;

public class Menu extends AppCompatActivity {

    private ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        EditText width = binding.editWidthText;
        EditText height = binding.editHeightText;
        EditText mineCount = binding.editMineCountText;
        Button startButton = binding.button;

        //String strwidth = width.getText().toString();
        //String strheight = height.getText().toString();
        //String strmineCount = mineCount.getText().toString();

        //Integer intwidth = Integer.parseInt(strwidth);
        //Integer intheight = Integer.parseInt(strheight);
        //Integer intmineCount = Integer.parseInt(strmineCount);

        startButton.setOnClickListener(v -> {
            String strwidth = width.getText().toString();
            String strheight = height.getText().toString();
            String strmineCount = mineCount.getText().toString();

            Integer intwidth = Integer.parseInt(strwidth);
            Integer intheight = Integer.parseInt(strheight);
            Integer intmineCount = Integer.parseInt(strmineCount);

            Intent main = new Intent(this, MainActivity.class);
            main.putExtra("width", intwidth);
            main.putExtra("height", intheight);
            main.putExtra("mineCount", intmineCount);
            startActivity(main);
        });


    }
}