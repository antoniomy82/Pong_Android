package com.example.antonio.pong;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by antonio on /06/16.
 */


public class Player {
    private Paint mPaint;
    protected int mColor;
    protected int mSpeed = 7;
    protected int mGoles = 0;
    protected int puntos = 0;
    protected Rect mRect;
    protected Rect mTouch;
    private int centroPantallaY;
    private int centroPantallaX;
    private int numPartidas; //Número de partidas (variable para marcador)
    private int y;
    public int destination;
    public boolean onePlayer;
    public static final int ALTO_PALA = 20;
    public static final int ANCHO_PALA = 140;
    public int finPartidas;//Variable para determinar el número máximo de partidas

    //Constructor por parámetros
    public Player(int color, int y, int centroPantallaX, int centroPantallaY, Paint paint,int partidas) {
        mColor = color;
        this.centroPantallaY = centroPantallaY;
        this.centroPantallaX = centroPantallaX;
        this.y = y;
        this.mPaint = paint;

        this.numPartidas = partidas; //Inicializamos el número de partidas
        this.finPartidas=partidas;
        mRect = new Rect(centroPantallaX - ANCHO_PALA, y, centroPantallaX + ANCHO_PALA, y + ALTO_PALA);
        destination = centroPantallaX;
    }

    //Funciones de calculo de movimiento del jugador
    public void move() {
        move(mSpeed);
    }

    public void move(int s) {
        int dx = (int) Math.abs(mRect.centerX() - destination);

        if (destination < mRect.centerX()) {
            mRect.offset((dx > s) ? -s : -dx, 0);
        } else if (destination > mRect.centerX()) {
            mRect.offset((dx > s) ? s : dx, 0);
        }
    }

    /**
     * Bloque de Getter de posición
     */
    public int getTop() {return mRect.top;}

    public int getBottom() {return mRect.bottom;}

    public int getWidth() {return Player.ANCHO_PALA;}

    public int getLeft() {return mRect.left;}

    public int getRight() {return mRect.right;}

    public int centerX() {return mRect.centerX();} //Devuelve el centro de la raqueta


    /**
     * Bloque de funciones de pintado (draw)
     */
    //Dibuja la pala
    public void draw(Canvas canvas) {
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);

        //mPaint2.setColor(colorV);
        canvas.drawRect(mRect, mPaint);
        drawTouchbox(canvas);
        drawMarker(canvas);
    }

    //Dibuja la zona de control
    public void drawTouchbox(Canvas canvas) {
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);

        // Heuristic for deciding which line to paint:
        // draw the one closest to middle
        int mid = centroPantallaY / 2;
        int top = Math.abs(mTouch.top - mid);
        int bot = Math.abs(mTouch.bottom - mid);
        float y = (top < bot) ? mTouch.top : mTouch.bottom;
        canvas.drawLine(mTouch.left, y, mTouch.right, y, mPaint);
    }

    //Pintamos el marcador
    public void drawMarker(Canvas canvas) {
        //Modo 2 Jugadores
        if (!onePlayer) {
            Paint paintFont = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintFont.setTextSize(60);
            paintFont.setColor(mColor);
            paintFont.setTextAlign(Paint.Align.CENTER);
            if (y < centroPantallaY) {
                canvas.drawText("GOL:" + String.valueOf(mGoles), 160, 50, paintFont);
            } else {
                canvas.drawText("GOL:" + String.valueOf(mGoles), 160, (centroPantallaY * 2) - 5, paintFont);
            }
        } else if (mColor == Color.GREEN) { //Modo 1 Jugador
            Paint paintFont = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintFont.setTextSize(60);
            paintFont.setColor(mColor);
            paintFont.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Puntos:" + String.valueOf(puntos), 160, (centroPantallaY * 2) - 15, paintFont);
            canvas.drawText("Vidas:" + String.valueOf(numPartidas), 600, (centroPantallaY * 2) - 15, paintFont);
        }

    }

    /**
     * Bloque de funciones de lógica del jugador
     */
    //pone a modo un jugador
    public void totalWall() {mRect = new Rect(0, y, centroPantallaX * 2, y + ALTO_PALA);}

    //Comprobación si se ha pulsado dentro de la zona de control del  jugador
    public boolean inTouchbox(int x, int y) {return mTouch.contains(x, y);}

    //Pierde una vida (
    public void pierdeUnaVida() {numPartidas--;}

    //Da un toque
    public void tocaLaPelota() {puntos++;}

    //Marca un gol)
    public void marcaGol() {mGoles++;}

    //Ponemos a 3 Goles para 2 Jugadores
    public boolean haGanado() {return mGoles == finPartidas;}

    public void setTouchbox(Rect r) {mTouch = r;}

}
