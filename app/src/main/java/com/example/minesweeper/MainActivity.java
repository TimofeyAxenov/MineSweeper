package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

import com.example.minesweeper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int width;
    private int height;
    private int mineCount;

    private Bundle values;

    private String gameState;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        values = getIntent().getExtras();
        this.width = values.getInt("width");
        this.height = values.getInt("height");
        this.mineCount = values.getInt("mineCount");
        GameViewModel model = new ViewModelProvider(this).get(GameViewModel.class);
        model.startGame(width, height, mineCount);
        model.getUIState().observe(this, state -> {
            if (state == null) return;
            binding.textGameStateView.setText(state.getGameState().toString());
            binding.minesweeperView.drawField(state.getField());
            gameState = state.getGameState().toString();
            state.getField();
        });
        binding.minesweeperView.setListener(new MinesweeperView.OnCellClickListener() {
            @Override
            public void onClick(int x, int y) {

                model.onCellClick(x, y);
                binding.minesweeperView.setGameState(gameState);
            }

            @Override
            public void onLongClick(int x, int y) {

                model.onCellLongClick(x, y);
                binding.minesweeperView.setGameState(gameState);
            }
        });



        binding.buttonRestart.setOnClickListener(v -> {
            model.startGame(width, height, mineCount);
            model.updateState();
            binding.minesweeperView.setGameState(gameState);
            binding.minesweeperView.invalidate();
        });

    }
}