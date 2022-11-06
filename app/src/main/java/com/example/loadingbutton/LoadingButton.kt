package com.example.loadingbutton

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var txt = context.getString(R.string.download)
    private var isClick = false
    private var backgroundColorBtn =0
    private var btnLoadingColor =0
    private var tempColor =0
    private var animator: ValueAnimator? = null
    private var currentSweepAngle = 0
    private var textColor = 0
    private var textSize = 0
    private val rect: RectF = RectF(0f, 0f, 0f, 0f)

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if(buttonState == ButtonState.Loading){
            txt = context.getString(R.string.we_are_loading)
            isClick = true
            backgroundColorBtn = ContextCompat.getColor(context,R.color.purple_700)
            startAnimationCircle()
        }else if (buttonState == ButtonState.Completed) {
            backgroundColorBtn = tempColor
            txt = context.getString(R.string.download)
            isClick = false
        }
        invalidate()
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        isClick = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            backgroundColorBtn = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            btnLoadingColor = getColor(R.styleable.LoadingButton_buttonLoadingColor, 0)
            tempColor = backgroundColorBtn
            textSize = getDimensionPixelSize(R.styleable.LoadingButton_textSize, 0)
        }
    }

    private fun startAnimationCircle() {
        animator?.cancel()
        animator = ValueAnimator.ofInt(0, 360).apply {
            duration = 2000
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        animator?.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = backgroundColorBtn
        canvas?.drawRect(0.0F, 0.0F, widthSize.toFloat(), heightSize.toFloat(), paint)
        paint.color = textColor
        paint.textSize = textSize.toFloat()
        canvas?.drawText(txt, widthSize / 2f, heightSize / 2 * 1.2f, paint)
        // loading button
        paint.color = btnLoadingColor
        canvas?.drawRect(0f, 0f, widthSize * currentSweepAngle/360f, heightSize.toFloat(), paint)
        if (isClick) {
            paint.color = textColor
            rect.set(64f, heightSize / 3f, widthSize / 6f, heightSize / 2f)
            canvas?.drawArc(
                rect,
                225f,
                currentSweepAngle.toFloat(),
                true,
                paint
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
    fun setState(state: ButtonState) {
        buttonState = state
    }
}