package com.gal.marvelpedia.utilities

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.gal.marvelpedia.R

class SpinnerLoader(view: View, context: Context) {

    var spinnerImage: ImageView = view.findViewById(R.id.thor_spinner) //findViewById(R.id.account_img)
    var anim: Animation = AnimationUtils.loadAnimation(context, R.anim.rotate_infinite)

    fun startSpinner() {
        spinnerImage.visibility = View.VISIBLE
        spinnerImage.startAnimation(anim)
    }

    fun stopSpinner() {
        spinnerImage.clearAnimation()
        spinnerImage.visibility = View.GONE
    }


}

