package com.example.zxingwithroundedscanner

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.zxingwithroundedscanner.databinding.LayoutIosAlertBinding

class AppAlert(fragmentActivity: FragmentActivity) : Dialog(fragmentActivity) {

    class Builder(private val activity: FragmentActivity) : DefaultLifecycleObserver {

        private var dialog: Dialog? = null
        private var title: String? = null
        private var message: String? = null
        private var cancelable: Boolean = true
        private var positiveButtonText: String? = null
        private var negativeButtonText: String? = null
        private var neutralButtonText: String? = null
        private var positiveClick: (() -> Unit)? = null
        private var negativeClick: (() -> Unit)? = null
        private var neutralClick: (() -> Unit)? = null

        private lateinit var binding: LayoutIosAlertBinding

        private val context: Context
            get() = activity.applicationContext
        private val hasTwoButtons: Boolean
            get() = !positiveButtonText.isNullOrBlank() && !negativeButtonText.isNullOrBlank()
        private val hasThreeButtons: Boolean
            get() = !positiveButtonText.isNullOrBlank() &&
                    !negativeButtonText.isNullOrBlank() &&
                    !neutralButtonText.isNullOrBlank()

        init {
            activity.lifecycle.addObserver(this)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            dialog?.dismiss()
            super.onDestroy(owner)
        }

        fun title(@StringRes titleId: Int?): Builder {
            title = titleId?.let { context.getString(it) }
            return this
        }

        fun message(@StringRes messageId: Int?): Builder {
            message = messageId?.let { context.getString(it) }
            return this
        }

        fun title(title: String?): Builder {
            this.title = title
            return this
        }

        fun message(message: String?): Builder {
            this.message = message
            return this
        }

        fun positiveButton(
            @StringRes titleId: Int?,
            callback: (() -> Unit)? = null
        ): Builder {
            if (titleId != null) {
                positiveButtonText = context.getString(titleId)
                positiveClick = callback
            }
            return this
        }

        fun negativeButton(
            @StringRes titleId: Int?,
            callback: (() -> Unit)? = null
        ): Builder {
            if (titleId != null) {
                negativeButtonText = context.getString(titleId)
                negativeClick = callback
            }
            return this
        }

        fun neutralButton(
            @StringRes titleId: Int?,
            callback: (() -> Unit)? = null
        ): Builder {
            if (titleId != null) {
                neutralButtonText = context.getString(titleId)
                neutralClick = callback
            }
            return this
        }

        fun cancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        private fun build(): AppAlert {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_ios_alert,
                null,
                false
            )
            binding.title = title
            binding.message = message
            setupButtonsContainer()
            val dialogParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return AppAlert(activity).apply {
                setCancelable(cancelable)
                setContentView(binding.root, dialogParams)
                window?.apply {
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setDimAmount(0.4f)
                    attributes.windowAnimations = R.style.DialogAnimation
                }
                dialog = this
            }
        }

        fun show(): AppAlert {
            return build().apply { show() }
        }

        private fun setupButtonsContainer() {
            with(binding.layoutButtonsContainer) {
                orientation = if (hasThreeButtons) {
                    LinearLayout.VERTICAL
                } else {
                    LinearLayout.HORIZONTAL
                }
                when {
                    hasThreeButtons -> {
                        addView(setupPositiveButton())
                        addView(setupHorizontalDivider())
                        addView(setupNeutralButton())
                        addView(setupHorizontalDivider())
                        addView(setupNegativeButton())
                    }
                    hasTwoButtons -> {
                        addView(setupNegativeButton())
                        addView(setupVerticalDivider())
                        addView(setupPositiveButton())
                    }
                    else -> {
                        addView(setupPositiveButton())
                    }
                }
            }
        }

        private fun setupPositiveButton(): TextView {
            return TextView(context).apply {
                text = positiveButtonText
                setTextColor(ContextCompat.getColor(context, R.color.dark_dodger_blue))
                gravity = Gravity.CENTER
                layoutParams = getButtonLayoutParams()
                setOnClickListener {
                    positiveClick?.invoke()
                    dialog?.dismiss()
                }
            }
        }

        private fun setupNegativeButton(): TextView {
            return TextView(context).apply {
                text = negativeButtonText
                setTextColor(ContextCompat.getColor(context, R.color.dark_dodger_blue))
                gravity = Gravity.CENTER
                layoutParams = getButtonLayoutParams()
                setTypeface(typeface, Typeface.BOLD)
                setOnClickListener {
                    negativeClick?.invoke()
                    dialog?.dismiss()
                }
            }
        }

        private fun setupNeutralButton(): TextView {
            return TextView(context).apply {
                text = neutralButtonText
                setTextColor(ContextCompat.getColor(context, R.color.red_orange))
                gravity = Gravity.CENTER
                layoutParams = getButtonLayoutParams()
                setOnClickListener {
                    neutralClick?.invoke()
                    dialog?.dismiss()
                }
            }
        }

        private fun setupVerticalDivider(): View {
            return View(context).apply {
                background = ColorDrawable(ContextCompat.getColor(context, R.color.black_10))
                layoutParams = LinearLayout.LayoutParams(
                    context.resources.getDimensionPixelSize(R.dimen.dp_1),
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            }
        }

        private fun setupHorizontalDivider(): View {
            return View(context).apply {
                background = ColorDrawable(ContextCompat.getColor(context, R.color.black_10))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    context.resources.getDimensionPixelSize(R.dimen.dp_1)
                )
            }
        }

        private fun getButtonLayoutParams(): LinearLayout.LayoutParams {
            return if (hasThreeButtons) {
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    context.resources.getDimensionPixelSize(R.dimen.dp_48)
                )
            } else {
                LinearLayout.LayoutParams(
                    0,
                    context.resources.getDimensionPixelSize(R.dimen.dp_48),
                    1f
                )
            }
        }
    }
}
