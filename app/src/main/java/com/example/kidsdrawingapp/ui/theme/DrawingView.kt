// DrawingView.kt
package com.example.kidsdrawingapp.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

  private var mDrawPath: CustomPath? = null
  private var mCanvasBitmap: Bitmap? = null
  private var mDrawPaint: Paint? = null
  private var mCanvasPaint: Paint? = null
  private var mBrushSize: Float = 0f
  private var color = Color.BLACK
  private var drawCanvas: Canvas? = null
  private val mPaths = ArrayList<CustomPath>()
  private val mUndoPaths = ArrayList<CustomPath>()

  init {
    setupDrawing()
  }

  private fun setupDrawing() {
    mDrawPaint = Paint()
    mDrawPath = CustomPath(color, mBrushSize)

    mDrawPaint?.color = color
    mDrawPaint?.style = Paint.Style.STROKE
    mDrawPaint?.strokeJoin = Paint.Join.ROUND
    mDrawPaint?.strokeCap = Paint.Cap.ROUND
    mCanvasPaint = Paint(Paint.DITHER_FLAG)
  }

  fun setSizeForBrush(newSize: Float) {
    mBrushSize = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics
    )
    mDrawPaint?.strokeWidth = mBrushSize
  }

  fun setColor(newColor: String) {
    color = Color.parseColor(newColor)
    mDrawPaint?.color = color
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    drawCanvas = Canvas(mCanvasBitmap!!)
  }

//  override fun onDraw(ca\]=nvas: Canvas) {
//    super.onDraw(canvas)
//    canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)
//
//    for (path in mPaths) {
//      mDrawPaint?.color = path.color
//      mDrawPaint?.strokeWidth = path.brushThickness
//      canvas.drawPath(path, mDrawPaint!!)
//    }
//
//    if (!mDrawPath!!.isEmpty) {
//      mDrawPaint?.color = mDrawPath!!.color
//      mDrawPaint?.strokeWidth = mDrawPath!!.brushThickness
//      canvas.drawPath(mDrawPath!!, mDrawPaint!!)
//    }
//  }
override fun onDraw(canvas: Canvas) {
  super.onDraw(canvas)
  mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//  drawCanvas = Canvas(mCanvasBitmap!!)
  canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

  // Draw all paths from mPaths
  for (path in mPaths) {
    mDrawPaint?.color = path.color
    mDrawPaint?.strokeWidth = path.brushThickness
    canvas.drawPath(path, mDrawPaint!!)
  }

  // Draw the current path being drawn
  if (!mDrawPath!!.isEmpty) {
    mDrawPaint?.color = mDrawPath!!.color
    mDrawPaint?.strokeWidth = mDrawPath!!.brushThickness
    canvas.drawPath(mDrawPath!!, mDrawPaint!!)
  }
}

  override fun onTouchEvent(event: MotionEvent): Boolean {
    val touchX = event.x
    val touchY = event.y

    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        mDrawPath?.color = color
        mDrawPath?.brushThickness = mBrushSize
        mDrawPath?.reset()
        mDrawPath?.moveTo(touchX, touchY)
      }
      MotionEvent.ACTION_MOVE -> {
        mDrawPath?.lineTo(touchX, touchY)
      }
      MotionEvent.ACTION_UP -> {
        mPaths.add(mDrawPath!!)
        drawCanvas?.drawPath(mDrawPath!!, mDrawPaint!!)
        mDrawPath = CustomPath(color, mBrushSize)
      }
      else -> return false
    }
    invalidate()
    return true
  }

  fun onClickUndo() {
    if (mPaths.isNotEmpty()) {
      mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
      Log.d("DrawingView", "Undo: Removed path. Remaining paths: ${mPaths.size}")
    invalidate()
    }
  }

  internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path()
}
