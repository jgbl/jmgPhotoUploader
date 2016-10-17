package org.de.jmg.jmgphotouploader.Controls;

//import Android.Runtime.*;
import android.view.*;

public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener implements GestureDetector.OnGestureListener
{
	private View context;
	public float ScaleFactor = 1.0f;
	public float FocusX;
	public float FocusY;
	public ScaleListener(View context)
	{
		this.context = context;

	}
	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{
		ScaleFactor *= detector.getScaleFactor();

		// Don't let the object get too small or too large.
		ScaleFactor = Math.max(1.0f, Math.min(ScaleFactor, 10.0f));
		FocusX = detector.getFocusX();
		FocusY = detector.getFocusY();
		context.invalidate();
		//invalidate();
		return true;
	}

	@Override
	public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		//FooterView.Text = String.Format("Fling velocity: {0} x {1}", velocityX, velocityY);
		return false;
	}

	@Override
	public final boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		FocusX += distanceX;
		FocusY += distanceY;
		context.invalidate();
		return false;
	}
	
	@Override
	public final boolean onDown(MotionEvent e)
	{
		return false;
	}
	
	@Override
	public final void onLongPress(MotionEvent e)
	{
	}
	
	@Override
	public final void onShowPress(MotionEvent e)
	{
	}
	
	@Override
	public final boolean onSingleTapUp(MotionEvent e)
	{
		return false;
	}
}