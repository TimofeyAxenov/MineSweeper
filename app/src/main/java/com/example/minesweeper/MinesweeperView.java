package com.example.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

public class MinesweeperView extends View {

    private static final int PADDING = 50;
    private static final long LONG_CLICK_TIME = 500;

    private Paint numberPaint;
    private Paint rectPaint;
    private Paint closedCellPaint;
    private Paint openedCellPaint;
    private Drawable flagDrawable;
    private Drawable mineDrawable;
    private Drawable balloonDrawable;
    private OnCellClickListener listener;
    private Cell[][] field;
    private final Rect textBounds = new Rect();

    private String gameState;

    public MinesweeperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        numberPaint = new Paint();
        numberPaint.setColor(Color.BLACK);
        numberPaint.setTextSize(100f);

        rectPaint = new Paint();
        rectPaint.setColor(getResources().getColor(R.color.gridColor, null));
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(5f);

        closedCellPaint = new Paint();
        closedCellPaint.setColor(getResources().getColor(R.color.closedCell, null));

        openedCellPaint = new Paint();
        openedCellPaint.setColor(getResources().getColor(R.color.openedCell, null));

        flagDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.img_flag, null);
        mineDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.img_mine, null);
        balloonDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.img_normal_balloon, null);

    }

    public void drawField(Cell[][] field) {
        this.field = field;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas){
        if (field == null) return;


        int w = getWidth();
        int h = getHeight();

        int GameW = field[0].length;
        int GameH = field.length;

        float cellSizeByW = (w - PADDING - PADDING) / GameW;
        float cellSizeByH = (h - PADDING - PADDING) / GameH;
        float cellSize = Math.min(cellSizeByH, cellSizeByW);

        float topPadding = (h-GameH*cellSize)/2;
        float leftPadding = (w-GameW*cellSize)/2;

        if (this.gameState != "WIN") {
            for (int y = 0; y < GameH; y++) {
                for (int x = 0; x < GameW; x++) {
                    Cell cell = field[y][x];
                    drawCell(x, y, canvas, cell, cellSize, topPadding, leftPadding);
                }
            }
        } else {
            for (int y = 0; y < GameH; y++) {
                for (int x = 0; x < GameW; x++) {
                    Cell cell = field[y][x];
                    drawWinCell(x, y, canvas, cell, cellSize, topPadding, leftPadding);
                }
            }
        }


    }

    private void drawCell(int x, int y, Canvas canvas, Cell cell,  float cellSize, float topPadding, float leftPadding) {
        float left = leftPadding + cellSize * x;
        float top = topPadding + cellSize * y;
        float right = leftPadding + cellSize * (x+1);
        float bottom = topPadding + cellSize * (y+1);
        if(cell.getState() == CellState.NONE) {
            canvas.drawRect(left, top, right, bottom, closedCellPaint);
        } else if (cell.getState() == CellState.FLAG) {
            canvas.drawRect(left, top, right, bottom, closedCellPaint);
            flagDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
            flagDrawable.draw(canvas);
        } else {
            if (cell.getMine() != null && cell.getMine()) {
                canvas.drawRect(left, top, right, bottom, openedCellPaint);
                mineDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
                mineDrawable.draw(canvas);
            } else if (cell.getNumber() > 0) {
                canvas.drawRect(left, top, right, bottom, openedCellPaint);
                String numberString = cell.getNumber().toString();
                setTextSizeForWidth(numberPaint, cellSize*0.6f, numberString);
                numberPaint.getTextBounds(numberString, 0, numberString.length(), textBounds);
                float cx = (left+right)/2;
                float cy = (top+bottom)/2;
                canvas.drawText(numberString, cx-textBounds.exactCenterX(), cy - textBounds.exactCenterY(), numberPaint);

            } else if (cell.getNumber() == 0) {
                canvas.drawRect(left, top, right, bottom, openedCellPaint);
            }
        }
        canvas.drawRect(left,top,right,bottom,rectPaint);


    }

    private void drawWinCell(int x,
                             int y,
                             Canvas canvas,
                             Cell cell,
                             float cellSize,
                             float topPadding,
                             float leftPadding){
        float left = leftPadding + cellSize * x;
        float top = topPadding + cellSize * y;
        float right = leftPadding + cellSize * (x+1);
        float bottom = topPadding + cellSize * (y+1);
        canvas.drawRect(left, top, right, bottom, openedCellPaint);
        balloonDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
        balloonDrawable.draw(canvas);
    }

    private void setTextSizeForWidth(Paint paint, float desiredSize, String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredSize / textBounds.height();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }

    private long actionDownTime;
    private Runnable onLongClick;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDownTime = System.currentTimeMillis();
                float x = event.getX();
                float y = event.getY();
                onLongClick = () -> {
                    clickOnCell(x, y, true);
                };

                postDelayed(onLongClick, LONG_CLICK_TIME);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.removeCallbacks(onLongClick);
                return true;

            case MotionEvent.ACTION_UP:
                long clickTime = System.currentTimeMillis() - actionDownTime;

                if (clickTime>LONG_CLICK_TIME){
                    performLongClick();

                } else {
                    performClick();
                    clickOnCell(event.getX(), event.getY(), false);
                }


                return true;
        }
        return false;
    }

    public void clickOnCell(float x, float y, boolean isLong) {

        int w = getWidth();
        int h = getHeight();

        int GameW = field[0].length;
        int GameH = field.length;

        float cellSizeByW = (w - PADDING - PADDING) / GameW;
        float cellSizeByH = (h - PADDING - PADDING) / GameH;
        float cellSize = Math.min(cellSizeByH, cellSizeByW);

        float top = (h-GameH*cellSize)/2;
        float left = (w-GameW*cellSize)/2;
        float right = left + GameW*cellSize;
        float bottom = top + GameH*cellSize;

        if (x<left || x>right) return;
        if (y<top || y>bottom) return;

        x -= left;
        y -= top;

        int ix = (int) (x/cellSize);
        int iy = (int) (y/cellSize);

        if (listener != null) {
            if (isLong) {
                listener.onLongClick(ix, iy);
            } else {
                listener.onClick(ix, iy);
            }
        }
    }

    public void setListener(OnCellClickListener listener) {
        this.listener = listener;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public interface OnCellClickListener {
        void onClick(int x, int y);

        void onLongClick(int x, int y);
    }


}
