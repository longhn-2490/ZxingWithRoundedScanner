package com.example.zxingwithroundedscanner

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ViewfinderView

class CustomViewfinderView(context: Context, attrs: AttributeSet?) :
    ViewfinderView(context, attrs) {

    private val scrimPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.black_80)
    }

    private val curveRadius: Float =
        context.resources.getDimensionPixelOffset(R.dimen.dp_20).toFloat()

    private val cornerLength: Float =
        context.resources.getDimensionPixelOffset(R.dimen.dp_10).toFloat()

    private val curveMargin: Float =
        context.resources.getDimensionPixelOffset(R.dimen.dp_2).toFloat()

    private val curveStrokeWidth =
        context.resources.getDimensionPixelOffset(R.dimen.dp_4).toFloat()

    override fun onDraw(canvas: Canvas?) {
        refreshSizes()
        if (framingRect == null || canvas == null) {
            return
        }

        val frame = framingRect
        val frameTop = frame.top.toFloat()
        val frameBottom = frame.bottom.toFloat()
        val frameLeft = frame.left.toFloat()
        val frameRight = frame.right.toFloat()
        drawScrimRect(canvas, frameLeft, frameTop, frameRight, frameBottom)
        drawRemainingScrimArc(canvas, frameLeft, frameTop, frameRight, frameBottom)
        drawFrameBounds(canvas, frame)
    }

    private fun drawScrimRect(
        canvas: Canvas,
        frameLeft: Float,
        frameTop: Float,
        frameRight: Float,
        frameBottom: Float
    ) {
        canvas.drawRect(0f, 0f, width.toFloat(), frameTop, scrimPaint)
        canvas.drawRect(0f, frameTop, frameLeft, frameBottom, scrimPaint)
        canvas.drawRect(frameRight, frameTop, width.toFloat(), frameBottom, scrimPaint)
        canvas.drawRect(0f, frameBottom, width.toFloat(), height.toFloat(), scrimPaint)
    }

    private fun drawRemainingScrimArc(
        canvas: Canvas,
        frameLeft: Float,
        frameTop: Float,
        frameRight: Float,
        frameBottom: Float
    ) {
        val path = Path()
        path.moveTo(frameLeft, frameTop)
        path.lineTo(frameLeft + curveRadius / 2f, frameTop)
        path.cubicTo(
            frameLeft + curveRadius / 2f,
            frameTop,
            frameLeft + curveMargin,
            frameTop + curveMargin,
            frameLeft,
            frameTop + curveRadius / 2f
        )
        path.moveTo(frameRight, frameTop)
        path.lineTo(frameRight - curveRadius / 2f, frameTop)
        path.cubicTo(
            frameRight - curveRadius / 2f,
            frameTop,
            frameRight - curveMargin,
            frameTop + curveMargin,
            frameRight,
            frameTop + curveRadius / 2f
        )
        path.moveTo(frameLeft, frameBottom)
        path.lineTo(frameLeft + curveRadius / 2f, frameBottom)
        path.cubicTo(
            frameLeft + curveRadius / 2f,
            frameBottom,
            frameLeft + curveMargin,
            frameBottom - curveMargin,
            frameLeft,
            frameBottom - curveRadius / 2f
        )
        path.moveTo(frameRight, frameBottom)
        path.lineTo(frameRight - curveRadius / 2f, frameBottom)
        path.cubicTo(
            frameRight - curveRadius / 2f,
            frameBottom,
            frameRight - curveMargin,
            frameBottom - curveMargin,
            frameRight,
            frameBottom - curveRadius / 2f
        )
        path.close()
        canvas.drawPath(path, scrimPaint)
    }

    private fun drawFrameBounds(canvas: Canvas, frame: Rect) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = curveStrokeWidth
        paint.color = Color.WHITE
        val frameTop = frame.top.toFloat()
        val frameBottom = frame.bottom.toFloat()
        val frameLeft = frame.left.toFloat()
        val frameRight = frame.right.toFloat()

        canvas.drawPath(
            createCornersPath(
                frameLeft + curveMargin,
                frameTop + curveMargin,
                frameRight - curveMargin,
                frameBottom - curveMargin,
                curveRadius,
                cornerLength
            ), paint
        )
    }

    private fun createCornersPath(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        cornerRadius: Float,
        cornerLength: Float
    ): Path {
        val path = Path()
        // top left
        path.moveTo(left, (top + cornerRadius))
        path.arcTo(
            RectF(left, top, left + cornerRadius, top + cornerRadius),
            180f,
            90f,
            true
        )
        path.moveTo(left + (cornerRadius / 2f), top)
        path.lineTo(left + (cornerRadius / 2f) + cornerLength, top)
        path.moveTo(left, top + (cornerRadius / 2f))
        path.lineTo(left, top + (cornerRadius / 2f) + cornerLength)
        // top right
        path.moveTo(right - cornerRadius, top)
        path.arcTo(
            RectF(right - cornerRadius, top, right, top + cornerRadius),
            270f,
            90f,
            true
        )
        path.moveTo(right - (cornerRadius / 2f), top)
        path.lineTo(right - (cornerRadius / 2f) - cornerLength, top)
        path.moveTo(right, top + (cornerRadius / 2f))
        path.lineTo(right, top + (cornerRadius / 2f) + cornerLength)
        // bottom left
        path.moveTo(left, bottom - cornerRadius)
        path.arcTo(
            RectF(left, bottom - cornerRadius, left + cornerRadius, bottom),
            90f,
            90f,
            true
        )
        path.moveTo(left + (cornerRadius / 2f), bottom)
        path.lineTo(left + (cornerRadius / 2f) + cornerLength, bottom)
        path.moveTo(left, bottom - (cornerRadius / 2f))
        path.lineTo(left, bottom - (cornerRadius / 2f) - cornerLength)
        // bottom right
        path.moveTo(left, bottom - cornerRadius)
        path.arcTo(
            RectF(right - cornerRadius, bottom - cornerRadius, right, bottom),
            0f,
            90f,
            true
        )
        path.moveTo(right - (cornerRadius / 2f), bottom)
        path.lineTo(right - (cornerRadius / 2f) - cornerLength, bottom)
        path.moveTo(right, bottom - (cornerRadius / 2f))
        path.lineTo(right, bottom - (cornerRadius / 2f) - cornerLength)
        return path
    }
}
