package com.example.antonio.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by antonio on 16/06/16.
 */
public class PongView extends View implements View.OnTouchListener {

    Player playerDown;
    Player playerTop;

    Paint playerDownPaint;
    Paint playerTopPaint;
    Paint bolaPaint;

    Ball bola;

    boolean newBola = false;
    boolean continuar = true;
    boolean reiniciar;
    private boolean pongIniciado = false;

    private Rect nuevoJuego; //Pulsamos la pantalla para iniciar de nuevo el juego
    long lastUpdateTime = 0; //Variable de para actualizar el sensor

    public int numPlayers; //Variable elegir  numero de jugadores
    public int partidas;   //Numero de partidas, inicializamos a 2
    public int dificultad; ///Variable para seleccionar la dificultad del juego


     //Creamos el pool para nuestros efectos de sonido
    protected SoundPool mPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    protected int ganarSFX, bolaPerdidaSFX, toqueRaquetaSFX, toqueParedSFX;

    //Refrescamos la pantalla acorde a nuestros FPS
    private RefreshHandler mRedrawHandler = new RefreshHandler();

   //Constructor por Parámetros
    public PongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);

    }


    /**
     * Bloque de Funciones Setter
     */
    public void setNumeroJugadores(int numeroJugadores) {this.numPlayers = numeroJugadores;}

    public void setDificultad(int dificultad) {this.dificultad = dificultad;}

    public void setPartidas(int vidas){this.partidas=vidas;}


    public void logicaDeJuego() {
        //Mover jugadores
        float px = bola.x;
        float py = bola.y;
        bola.move();
        playerDown.move();
        playerTop.move();
        handleBounces(px, py);
        // Compruebo si ha perdido alguno
        if (bola.y >= getHeight()) {
            newBola = true;
            playerTop.marcaGol();
            playerDown.pierdeUnaVida();
            if (!playerTop.haGanado())
                playSound(bolaPerdidaSFX);
            else {
                playSound(ganarSFX);
                stop();
            }

        } else if (bola.y <= 0) {
            newBola = true;
            playerDown.marcaGol();
            if (!playerDown.haGanado())
                playSound(bolaPerdidaSFX);
            else {
                playSound(ganarSFX);
                stop();
            }

        }

    }

    /**
     * Bloque de Funciones Handlers
     */
    protected void handleBounces(float px, float py) {
        handleTopFastBounce(playerTop, px, py);
        handleBottomFastBounce(playerDown, px, py);

        if (bola.x <= Ball.RADIO || bola.x >= getWidth() - Ball.RADIO) {
            bola.rebotaEnPared();
            playSound(toqueParedSFX);
            if (bola.x == Ball.RADIO)
                bola.x++;
            else
                bola.x--;
        }

    }

    protected void handleTopFastBounce(Player jugador, float px, float py) {
        if (bola.goUP() == false)
            return;

        float tx = bola.x;
        float ty = bola.y - Ball.RADIO;
        float ptx = px;
        float pty = py - Ball.RADIO;
        float dyp = ty - jugador.getBottom();
        float xc = tx + (tx - ptx) * dyp / (ty - pty);

        if (ty < jugador.getBottom() && pty > jugador.getBottom()
                && xc > jugador.getLeft() && xc < jugador.getRight()) {

            bola.x = xc;
            bola.y = jugador.getBottom() + Ball.RADIO;
            bola.rebotaEnPlayer(jugador);
            playSound(toqueRaquetaSFX);

        }

    }

    protected void handleBottomFastBounce(Player jugador, float px, float py) {
        if (bola.goDown() == false)
            return;

        float bx = bola.x;
        float by = bola.y + Ball.RADIO;
        float pbx = px;
        float pby = py + Ball.RADIO;
        float dyp = by - jugador.getTop();
        float xc = bx + (bx - pbx) * dyp / (pby - by);

        if (by > jugador.getTop() && pby < jugador.getTop()
                && xc > jugador.getLeft() && xc < jugador.getRight()) {

            bola.x = xc;
            bola.y = jugador.getTop() - Ball.RADIO;
            bola.rebotaEnPlayer(jugador);
            playSound(toqueRaquetaSFX);
            if (playerDown.onePlayer)
                playerDown.tocaLaPelota();

        }
    }

    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            PongView.this.update();
            PongView.this.invalidate(); // Mark the view as 'dirty'
        }

        public void sleep(long delay) {
            this.removeMessages(0);
            this.sendMessageDelayed(obtainMessage(0), delay);
        }
    }


    /**
     * Bloque de funciones Draws
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Pinto jugadores
        if (null != playerDown) {
            if (continuar) {
                playerDown.draw(canvas);
                playerTop.draw(canvas);
                bola.draw(canvas);
            } else {

                drawReiniciarJuego(canvas);
            }
        }

    }

    private void drawReiniciarJuego(Canvas canvas) {
        reiniciar = true;
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        String gana;
        if (playerDown.haGanado()) {
            gana = "Ganador jugador de abajo";
        } else {
            gana = "Ganador jugador de arriba";
        }
        String texto = null;
        if (numPlayers != 1) {
            texto = "Juego terminado \n" + gana + "\n" + "Pulse para reiniciar";
        } else {
            texto = "Juego terminado \n" + "Has tocado " + String.valueOf(playerDown.puntos) + " veces la bola \n"
                    + "Pulsa para reiniciar";
        }
        int pausew = (int) paint.measureText(texto);
        paint.setStyle(Paint.Style.STROKE);
        int color = R.color.verdeConsola;
        paint.setColor(color);


        int y = getHeight() / 2 - 100;
        int x = getWidth() / 2 - pausew / 2 - 90;
        paint.setTextSize(50);
        paint.setColor(Color.GREEN);
        for (String line : texto.split("\n")) {
            canvas.drawText(line, x, y, paint);
            y += paint.descent() - paint.ascent();
        }

    }

    /**
     * Bloque de funciones de Inicialización
     */

    protected void cargarSonidos() {
        Context ctx = getContext();
        ganarSFX = mPool.load(ctx, R.raw.toquegana, 1);
        bolaPerdidaSFX = mPool.load(ctx, R.raw.bolaperdida, 1);
        toqueRaquetaSFX = mPool.load(ctx, R.raw.toqueraqueta, 1);
        toqueParedSFX = mPool.load(ctx, R.raw.toquepared, 1);
    }

    public void iniciarConToqueJuegoNuevo() {
        int min = Math.min(getWidth() / 4, getHeight() / 4);
        int xmid = getWidth() / 2;
        int ymid = getHeight() / 2;
        nuevoJuego = new Rect(xmid - min, ymid - min, xmid + min, ymid + min);
    }

    public void inicializarJuego() {
        cargarSonidos();
        inicalizarJugadores();
        inicializarBola();
        iniciarConToqueJuegoNuevo();
        pongIniciado = true;
        continuar = true;
        reiniciar = false;
    }

    public void inicializarBola() {
        bolaPaint = new Paint();
        bolaPaint.setColor(Color.GREEN);
        bola = new Ball(bolaPaint, getWidth());
        bolaInicio();
    }

    public void inicalizarJugadores() {
        Rect topTouch = new Rect(0, 0, getWidth(), getHeight() / 8);
        Rect downTouch = new Rect(0, 7 * getHeight() / 8, getWidth(), getHeight());

        playerDownPaint = new Paint();
        playerDownPaint.setColor(Color.GREEN);
        playerTopPaint = new Paint();
        playerTopPaint.setColor(Color.GREEN);
        
        //Pasamos los parámetros al constructor de la clase Player
        playerTop = new Player(Color.BLUE, topTouch.bottom + 3, getWidth() / 2, getHeight() / 2, playerDownPaint,partidas);
        playerDown = new Player(Color.GREEN, downTouch.top - 3 - Player.ALTO_PALA, getWidth() / 2, getHeight() / 2, playerTopPaint,partidas);
        playerTop.setTouchbox(topTouch);
        playerDown.setTouchbox(downTouch);

        ///Cambiar a solo un jugador//
        if (numPlayers == 1) {
            playerTop.totalWall(); //Cambia a modo un jugador
            playerDown.onePlayer = true;
            playerTop.onePlayer = true;
        } else {
            playerDown.onePlayer = false;
            playerTop.onePlayer = false;
        }
    }

    //Bola Inicio
     private void bolaInicio() {
        bola.x = getWidth() / 2;
        bola.y = getHeight() / 2;
        bola.velocidad = Ball.VELOCIDAD_BASE + (3 * dificultad);//añadir velocidad
        bola.randomAngle();
        bola.pause();
    }


     public boolean onTouch(View view, MotionEvent motionEvent) {
        //Creamos un objeto InputHandler para poder usar multitouch
        InputHandler handle = InputHandler.getInstance();
        for (int i = 0; i < handle.getTouchCount(motionEvent); i++) {
            int tx = (int) handle.getX(motionEvent, i);
            int ty = (int) handle.getY(motionEvent, i);
            if (null != playerDown) {
                if (playerDown.inTouchbox(tx, ty)) {
                    playerDown.destination = tx;
                } else if (playerTop.inTouchbox(tx, ty)) {
                    playerTop.destination = tx;
                }
            }
        }
        if (reiniciar) {
            reiniciar = false;
            inicializarJuego();
            resume();
        }
        return false;
    }

    //Reproducir Sonidos
    private void playSound(int rid) {
        mPool.play(rid, 0.2f, 0.2f, 1, 0, 1.0f);
    }

    /**
     * Bloque de Funciones de Cambio de Estado
     */

    public void update() {
        if (getHeight() == 0 || getWidth() == 0) {
            mRedrawHandler.sleep(1000 / 30);
            return;
        }
        if (!pongIniciado) {
            inicializarJuego();

        }
        long now = System.currentTimeMillis();
        if (pongIniciado) {
            if (now >= 1000 / 30) {
                if (newBola) {
                    bolaInicio();
                    newBola = false;
                }
                logicaDeJuego();
            }
        }
        if (continuar) {
            long diff = System.currentTimeMillis() - now;
            mRedrawHandler.sleep(Math.max(0, (1000 / 30) - diff));
        }
    }

    public void resume() {
        continuar = true;
        update();
    }

    public void stop() {
        continuar = false;
    }

    //Liberar sonidos
    public void release() {
        mPool.release();
    }

    //Actualizar al cambiar de sensor
    public void updateFromSensor(float gravityX) {
        // Primera vez
        if (lastUpdateTime == 0) {
            lastUpdateTime = System.currentTimeMillis();
            return;
        }
        if (gravityX > 0) {
            playerDown.destination = 0;
        } else {
            playerDown.destination = getWidth();
        }
    }

}
