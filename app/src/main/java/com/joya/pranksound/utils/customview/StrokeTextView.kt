package com.joya.pranksound.utils.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.joya.pranksound.R

class StrokeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var strokeColor: Int = Color.RED
    private var strokeWidth: Float = 35f
    private val strokePaint = Paint()

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.StrokeTextView)
            strokeColor = typedArray.getColor(R.styleable.StrokeTextView_strokeColor, Color.RED)
            strokeWidth = typedArray.getDimension(R.styleable.StrokeTextView_strokeWidth, 35f)
            typedArray.recycle()
        }
        initStrokePaint()
    }

    private fun initStrokePaint() {
        strokePaint.isAntiAlias = true
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeJoin = Paint.Join.ROUND
        strokePaint.color = strokeColor
        strokePaint.strokeWidth = strokeWidth
        strokePaint.textSize = textSize
        strokePaint.typeface = typeface
    }

    override fun onDraw(canvas: Canvas) {
        // Lấy tối đa 12 ký tự từ văn bản
        val displayText = if (this.text.length > 12) this.text.substring(0, 12) else this.text.toString()

        val x = calculateXPosition(displayText)
        val y = calculateYPosition()

        // Vẽ stroke cho văn bản
        if (visibility == VISIBLE) {
            strokePaint.textSize = textSize
            strokePaint.typeface = typeface
            strokePaint.textAlign = paint.textAlign
            canvas.drawText(displayText, x, y, strokePaint)
        }

        // Vẽ văn bản chính với màu sắc được giữ nguyên
        paint.color = currentTextColor // Đảm bảo rằng màu text chính xác được sử dụng
        canvas.drawText(displayText, x, y, paint)
    }

    private fun calculateXPosition(text: String): Float {
        val textWidth = paint.measureText(text)
        return when (gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
            Gravity.CENTER_HORIZONTAL -> (width / 2f - textWidth / 2f)
            Gravity.END -> (width - textWidth - paddingRight.toFloat())
            else -> paddingLeft.toFloat()
        }
    }

    private fun calculateYPosition(): Float {
        val fontMetrics = paint.fontMetrics
        return when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.CENTER_VERTICAL -> (height / 2f - (fontMetrics.ascent + fontMetrics.descent) / 2f)
            Gravity.BOTTOM -> (height - paddingBottom.toFloat())
            else -> paddingTop - fontMetrics.ascent
        }
    }

    fun setStrokeColor(color: Int) {
        strokeColor = color
        strokePaint.color = color
        invalidate()
    }

    fun setStrokeWidth(width: Float) {
        strokeWidth = width
        strokePaint.strokeWidth = width
        invalidate()
    }
}
