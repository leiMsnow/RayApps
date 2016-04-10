package com.ray.apps.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gsty.corelibs.utils.ToastUtil;
import com.ray.apps.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 五子棋view
 * Created by zhangleilei on 4/10/16.
 */
public class GoBangView extends View {

    private static final int MAX_LINE = 10;
    private static final int MAX_PIECE = 5;

    private static final int DIRECTION_HORIZONTAL = 0;
    private static final int DIRECTION_VERTICAL = 1;
    private static final int DIRECTION_LEFT_ANGLE = 2;
    private static final int DIRECTION_RIGHT_ANGLE = 3;

    private Context mContext;

    private int mPanelSize;
    private float mLineSize;

    private Paint mPaint;

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private float pieceMargin = 1.0f * 3 / 4;

    private boolean mIsWhite = true;
    private List<Point> mWhitePath = new ArrayList<>();
    private List<Point> mBlackPath = new ArrayList<>();

    private boolean mIsGameOver = false;

    private int[] directions = new int[]{
            DIRECTION_HORIZONTAL,
            DIRECTION_VERTICAL,
            DIRECTION_LEFT_ANGLE,
            DIRECTION_RIGHT_ANGLE
    };

    public GoBangView(Context context) {
        this(context, null);
    }

    public GoBangView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoBangView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        setBackgroundColor(0x55e3aa84);
        initPaint();
        initPiece();

    }


    private void initPaint() {
        mPaint = new Paint();

        mPaint.setColor(mContext.getResources().getColor(R.color.normal_grey));
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

    }

    private void initPiece() {
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_white_piece);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_black_piece);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int measureSize = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            measureSize = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            measureSize = widthSize;
        }

        setMeasuredDimension(measureSize, measureSize);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mPanelSize = w;
        mLineSize = (int) (mPanelSize * 1.0f / MAX_LINE);

        int pieceSize = (int) (mLineSize * pieceMargin);

        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceSize, pieceSize, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceSize, pieceSize, false);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);
        drawPieces(canvas);

        checkWinner();

    }

    private void checkWinner() {
        boolean isWhiteWinner = detectingPieces(mWhitePath);
        boolean isBlackWinner = detectingPieces(mBlackPath);
        if (isWhiteWinner || isBlackWinner) {
            mIsGameOver = true;
            String content = (isWhiteWinner ? "白方" : "黑方") + " 胜利了！";
            ToastUtil.getInstance(mContext).showToast(content);
        }

    }

    private boolean detectingPieces(List<Point> pieces) {

        for (Point piece : pieces) {
            int x = piece.x;
            int y = piece.y;

            if (detectingDirection(x, y, pieces)) {
                return true;
            }
        }

        return false;
    }


    private boolean detectingDirection(int x, int y, List<Point> pieces) {

        for (int direction = 0; direction < directions.length; direction++) {

            int count = 1;
            for (int i = 1; i < MAX_PIECE; i++) {
                if (pieces.contains(getDirectionPoint(direction, x, y, i, true))) {
                    count++;
                } else {
                    break;
                }
            }
            if (count == MAX_PIECE) return true;
            for (int i = 1; i < MAX_PIECE; i++) {
                if (pieces.contains(getDirectionPoint(direction, x, y, i, false))) {
                    count++;
                } else {
                    break;
                }

            }
            if (count == MAX_PIECE) return true;

        }
        return false;
    }

    private Point getDirectionPoint(int direction, int x, int y, int i, boolean isFirst) {
        Point pointLeft = null;
        Point pointRight = null;
        switch (direction) {
            case DIRECTION_HORIZONTAL:
                pointLeft = new Point(x - i, y);
                pointRight = new Point(x + i, y);
                break;
            case DIRECTION_VERTICAL:
                pointLeft = new Point(x, y - i);
                pointRight = new Point(x, y + i);
                break;
            case DIRECTION_LEFT_ANGLE:
                pointLeft = new Point(x - i, y - i);
                pointRight = new Point(x + i, y + i);
                break;
            case DIRECTION_RIGHT_ANGLE:
                pointLeft = new Point(x + i, y + i);
                pointRight = new Point(x - i, y - i);
                break;
        }

        return isFirst ? pointLeft : pointRight;
    }

    private void drawBoard(Canvas canvas) {

        for (int i = 0; i < MAX_LINE; i++) {

            int start = (int) (mLineSize / 2);
            int stop = (int) (mPanelSize - mLineSize / 2);
            int move = (int) ((0.5 + i) * mLineSize);
            canvas.drawLine(start, move, stop, move, mPaint);
            canvas.drawLine(move, start, move, stop, mPaint);

        }

    }

    private void drawPieces(Canvas canvas) {

        for (Point point : mWhitePath) {

            float left = (point.x + (1 - pieceMargin) / 2) * mLineSize;
            float top = (point.y + (1 - pieceMargin) / 2) * mLineSize;
            canvas.drawBitmap(mWhitePiece, left, top, null);

        }

        for (Point point : mBlackPath) {

            float left = (point.x + (1 - pieceMargin) / 2) * mLineSize;
            float top = (point.y + (1 - pieceMargin) / 2) * mLineSize;
            canvas.drawBitmap(mBlackPiece, left, top, null);

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mIsGameOver) return super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                int y = (int) event.getY();
                Point touchPoint = getRulePoint(x, y);

                if (mWhitePath.contains(touchPoint) || mBlackPath.contains(touchPoint)) {
                    return false;
                }
                if (mIsWhite) {
                    mWhitePath.add(touchPoint);
                } else {
                    mBlackPath.add(touchPoint);
                }

                invalidate();
                mIsWhite = !mIsWhite;
                break;
        }
        return true;
    }

    private Point getRulePoint(int x, int y) {
        int ruleX = (int) (x / mLineSize);
        int ruleY = (int) (y / mLineSize);
        return new Point(ruleX, ruleY);
    }
}
