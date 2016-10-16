package com.jmg.jmgphotouploader.Controls;

import android.graphics.*;
import android.content.*;
//import android.runtime.*;
import android.view.*;
import android.widget.*;
import android.util.*;

//[Register("com/jmgphotoprinterandroid/jmgphotoprinterandroid/controls/ZoomExpandableListview", DoNotGenerateAcw=true)]
public class ZoomExpandableListview extends ExpandableListView
{
	private ScaleGestureDetector mScaleDetector;
	private ScaleListener mScaleListener;
	private GestureDetector mGestureDetector;
	public TextView FooterView;

	public ZoomExpandableListview(Context context)
	{
		super(context);
		Initialize();
	}

	public ZoomExpandableListview(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		Initialize();
	}

	public ZoomExpandableListview(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		Initialize();
	}

	public boolean getIsScaled()
	{
		return (mScaleListener.ScaleFactor != 1.0f);
	}
	private void Initialize()
	{
		View view = this;
		mScaleListener = new ScaleListener(view);
		mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
		mGestureDetector = new GestureDetector(getContext(), mScaleListener);
		/*
		FooterView = new TextView (Context);
		this.AddFooterView (FooterView);
		FooterView.Text = "X: Y: ";
		*/
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		// Let the ScaleGestureDetector inspect all events.

		mScaleDetector.onTouchEvent(ev);
		mGestureDetector.onTouchEvent(ev);
		if (mScaleListener.ScaleFactor != 1.0f)
		{
			/*
			float OffsetX =  mScaleListener.FocusX; // / mScaleListener.ScaleFactor;
			float OffsetY = mScaleListener.FocusY; // / mScaleListener.ScaleFactor;
			float ScaledX = (ev.RawX / mScaleListener.ScaleFactor) + OffsetX;
			float ScaledY = (ev.RawY / mScaleListener.ScaleFactor) + OffsetY;
			Console.WriteLine( "X:" + ev.RawX + " Y:" + ev.RawY + " ScaledX:" + (ScaledX) + " ScaledY: " + (ScaledY));
			Console.WriteLine("FocusX: " + mScaleListener.FocusX + " FocusY: " + mScaleListener.FocusY + " Factor: " + mScaleListener.ScaleFactor);

			ev.SetLocation (ScaledX,ScaledY);
			*/
		}
		else
		{
			super.onTouchEvent(ev);
		}
		return true;
	}
	/*
	float saveScale = 1.0f;
	float maxScale = 10.0f;
	float minScale = 0.5f;
*/

	private float DefaultFocusX;
	private float DefaultFocusY;
	/*
	private Rect mSelRect;
	private Rect mTotalRect;
	*/
	@Override
	protected void onDraw(Canvas canvas)
	{

		super.onDraw(canvas);

		//canvas.Save();
		//canvas.Scale(mScaleListener.ScaleFactor, mScaleListener.ScaleFactor,mScaleListener.FocusX,mScaleListener.FocusY);
		
		Matrix m = canvas.getMatrix();
		m.reset();
		if (mScaleListener.ScaleFactor == 1.0f)
		{
			mScaleListener.FocusX = 0.0f;
			mScaleListener.FocusY = 0.0f;
			//DefaultFocusX = Resources.DisplayMetrics.WidthPixels / 2;
			//DefaultFocusY = (Resources.DisplayMetrics.HeightPixels) / 2;
			DefaultFocusX = canvas.getWidth() / 2;
			DefaultFocusY = canvas.getHeight() / 2;
		}
		else
		{
			m.postScale(mScaleListener.ScaleFactor, mScaleListener.ScaleFactor, mScaleListener.FocusX, mScaleListener.FocusY);
			//m.PostTranslate(mScaleListener.FocusX,mScaleListener.FocusY);
			//m.postTranslate(zoomCenter[0], zoomCenter[1]);
			//child.setScaleType(ScaleType.MATRIX);
			//child.setImageMatrix(m);
			canvas.concat(m);
			/*
			mTotalRect = new Rect (0, 0, canvas.Width, canvas.Height);
			mSelRect = new Rect ((int)(mScaleListener.FocusX ),
				(int)(mScaleListener.FocusY ),
				(int)(Resources.DisplayMetrics.WidthPixels / mScaleListener.ScaleFactor),
				(int)(Resources.DisplayMetrics.HeightPixels / mScaleListener.ScaleFactor));
			*/
			//Console.WriteLine ("L:" + mSelRect.Left + " R:" + mSelRect.Right + " W:" + mSelRect.Width() + " H:" + mSelRect.Height()
			//	+ " TotalW:" + canvas.Width + " TotalH:" + canvas.Height);
		}
		/*
		float mScaleFactor = mScaleListener.ScaleFactor;
		float origScale = saveScale;
		saveScale *= mScaleFactor;
		if (saveScale > maxScale) {
			saveScale = maxScale;
			mScaleFactor = maxScale / origScale;
		} else if (saveScale < minScale) {
			saveScale = minScale;
			mScaleFactor = minScale / origScale;
		} 

		if (OrigWidth * saveScale <= this.Width || OrigHeight * saveScale <= this.Height)
			Matrix.PostScale(mScaleFactor, mScaleFactor, this.Width / 2, this.Height / 2);
		else 
			Matrix.PostScale(mScaleListener.ScaleFactor, mScaleListener.ScaleFactor, mScaleListener.FocusX, mScaleListener.FocusY);
			*/

// onDraw() code goes here

		//canvas.Restore();

	}

}