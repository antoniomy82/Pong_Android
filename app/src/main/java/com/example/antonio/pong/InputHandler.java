package com.example.antonio.pong;

import android.os.Build;
import android.view.MotionEvent;

/**
 * Created by antonio on 22/06/16.
 */

/**
 * Clase abstracta para manejar multitouch (en opción touch)
 *
 * NOTA: El area touch está comprendida entre : (para pasar el dedo o ratón)
 *      Jugador Down (verde): la pala y la parte inferior
 *      Jugador Top (azul): la pala y la parte superior
 */
public abstract class InputHandler {
	
	public static InputHandler getInstance() {
		if(Integer.parseInt(Build.VERSION.SDK) < 5) {
			return SingleInput.Holder.sInstance;
		}
		else {
			return MultiInput.Holder.sInstance;
		}
	}
	
	public abstract int getTouchCount(MotionEvent e);
	public abstract float getX(MotionEvent e, int i);
	public abstract float getY(MotionEvent e, int i);

	//Cuanto usamos varios touch
	private static class MultiInput extends InputHandler {
		private static class Holder {
			private static final MultiInput sInstance = new MultiInput();
		}

		@Override
		public int getTouchCount(MotionEvent e) {
			return e.getPointerCount();
		}

		@Override
		public float getX(MotionEvent e, int i) {
			return e.getX(i);
		}

		@Override
		public float getY(MotionEvent e, int i) {
			return e.getY(i);
		}
	}

	//con un solo touch
	private static class SingleInput extends InputHandler {
		private static class Holder {
			private static final SingleInput sInstance = new SingleInput();
		}

		@Override
		public int getTouchCount(MotionEvent e) {
			return 1;
		}

		@Override
		public float getX(MotionEvent e, int i) {
			return e.getX();
		}

		@Override
		public float getY(MotionEvent e, int i) {
			return e.getY();
		}
	}
}
