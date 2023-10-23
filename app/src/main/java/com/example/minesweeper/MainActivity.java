package com.example.minesweeper;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView timerTextView;
    private CountDownTimer timer;
    private long timeElapsed = 0; // Time in milliseconds
    GridView gridView;
    Game game;
    BaseAdapter adapter;

    private boolean flagMode = false;
    private Button flagModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerTextView = findViewById(R.id.timerTextView);
        gridView = findViewById(R.id.gridview);
        game = new Game(8, 8, 10);  // 8x8 grid with 10 mines

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {
            @Override
            public int getCount() {
                return 64;  // for an 8x8 grid
            }

            @Override
            public String getItem(int position) {
                int row = position / 8;
                int col = position % 8;
                Cell cell = game.getGrid().getCell(row, col);

                if (cell.isFlagged()) {
                    return "🚩";
                }
                if (!cell.isRevealed()) {
                    return "";
                }
                if (cell.isMine()) {
                    return "\uD83D\uDCA3";  // Use "M" for mines
                }
                if (cell.getNeighboringMines() > 0) {
                    return String.valueOf(cell.getNeighboringMines());
                }
                return "";
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView;
                if (convertView == null) {
                    textView = new TextView(MainActivity.this);
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextSize(18); // Set the size of the text
                } else {
                    textView = (TextView) convertView;
                }

                String item = getItem(position);

                if ("🚩".equals(item)) {
                    textView.setText("🚩");
                    textView.setTextColor(Color.RED);
                    textView.setBackgroundColor(getResources().getColor(R.color.orange_cell));
                }
                else if ("\uD83D\uDCA3".equals(item)) {
                    textView.setBackgroundColor(getResources().getColor(R.color.mine_cell));
                    textView.setTextColor(Color.WHITE);
                } else if (!item.isEmpty()) {
                    textView.setBackgroundColor(getResources().getColor(R.color.revealed_cell));
                    // Set text colors based on the number
                    if ("1".equals(item)) {
                        textView.setTextColor(Color.BLUE);
                    } else if ("2".equals(item)) {
                        textView.setTextColor(Color.GREEN);
                    } else if ("3".equals(item)) {
                        textView.setTextColor(Color.YELLOW);
                    } else if ("4".equals(item)) {
                        textView.setTextColor(Color.MAGENTA);
                    } else if ("".equals(item)) {
                        textView.setTextColor(getResources().getColor(R.color.revealed_cell));
                    }
                } else {
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setTextColor(Color.BLACK);
                }

                textView.setText(item);

                return textView;
            }
        };

        flagModeButton = findViewById(R.id.btn_flag_mode);
        flagModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagMode = true;
            }
        });

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int row = position / 8;
                int col = position % 8;

                Cell cell = game.getGrid().getCell(row, col);

                if (flagMode && !cell.isRevealed() && !cell.isFlagged()) {
                    cell.toggleFlag();
                    flagMode = false;  // Deactivate flag mode after placing a flag
                    adapter.notifyDataSetChanged();
                    return;
                }

                // If the cell is flagged and flag mode is active, unflag it
                if (flagMode && cell.isFlagged()) {
                    cell.toggleFlag();
                    flagMode = false;  // Deactivate flag mode after removing a flag
                    adapter.notifyDataSetChanged();
                    return;
                }

                game.reveal(row, col);
                if (game.isGameOver()) {
                    timer.cancel();
                    findViewById(R.id.btn_play_again).setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Game Over!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();  // Refresh the entire grid
                }


                adapter.notifyDataSetChanged();
            }
        });
    }

    private void startTimer() {
        timer = new CountDownTimer(Long.MAX_VALUE, 1000) { // Update every second
            @Override
            public void onTick(long millisUntilFinished) {
                timeElapsed += 1000;
                timerTextView.setText(formatTime(timeElapsed));
            }

            @Override
            public void onFinish() { }
        }.start();
    }
    private String formatTime(long timeInMilliseconds) {
        int seconds = (int) (timeInMilliseconds / 1000) % 60;
        int minutes = (int) ((timeInMilliseconds / (1000 * 60)) % 60);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
    public void startGame(View view) {
        game = new Game(8, 8, 10);  // reset the game
        gridView.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        startTimer();
    }
    public void playAgain(View view) {
        game = new Game(8, 8, 10);  // reset the game
        gridView.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        findViewById(R.id.btn_start).setVisibility(View.GONE);
        timerTextView.setText("00:00"); // Reset timer display
        timeElapsed = 0; // Reset timer count
        startTimer();
        adapter.notifyDataSetChanged();
    }
}