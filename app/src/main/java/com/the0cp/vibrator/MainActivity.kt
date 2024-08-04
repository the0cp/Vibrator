package com.the0cp.vibrator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codevblocks.android.knob.Knob

class MainActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val knobView = findViewById<Knob>(R.id.knob)
        val currentProgress = knobView.progress
        println(currentProgress)

        fun Knob.setOnProgressListener(listener: Knob.OnProgressListener) {
            progressListener = listener
        }

        /*
        fun getCurrentProg() : Float {
            return knobView.progress ;
        }
         */

        /*
        set vibrator
         */
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibratorManager.defaultVibrator

        val switchPulse = findViewById<Switch>(R.id.pulseSwitch)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

// 设置默认模式
        for (i in 0 until radioGroup.childCount) {
            (radioGroup.getChildAt(i) as? RadioButton)?.isEnabled = false
        }

        val progressListener = object : Knob.OnProgressListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(knob: Knob, progress: Float) {
                if (vibrator.hasAmplitudeControl()) {
                    vibrator.cancel()
                    val timings =longArrayOf(1000, 0)
                    val amplitude = intArrayOf((progress * 2.25).toInt(), 0)
                    val repeat = 0
                    val vibrationEffect = VibrationEffect.createWaveform(timings, amplitude, repeat)
                    vibrator.vibrate(vibrationEffect)
                }
                val textAmplitude = findViewById<TextView>(R.id.amplitude)
                textAmplitude.text = "Amplitude: ${progress.toInt()}"
            }
        }

        knobView.setOnProgressListener(progressListener)

        switchPulse.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                for (i in 0 until radioGroup.childCount) {
                    (radioGroup.getChildAt(i) as? RadioButton)?.isEnabled = true
                }
                radioGroup.setOnCheckedChangeListener { _, checkedId ->
                    var pulseTiming = longArrayOf(1000, 100)
                    when (checkedId) {
                        R.id.radio100 -> pulseTiming = longArrayOf(1000, 100)
                        R.id.radio200 -> pulseTiming = longArrayOf(1000, 200)
                        R.id.radio300 -> pulseTiming = longArrayOf(1000, 300)
                        R.id.radio500 -> pulseTiming = longArrayOf(1000, 500)
                        R.id.radio1000 -> pulseTiming = longArrayOf(1000, 1000)
                    }

                    if (vibrator.hasAmplitudeControl()) {
                        vibrator.cancel()
                        val amplitude = intArrayOf((knobView.progress * 2.25).toInt(), 0)
                        val repeat = 0
                        val vibrationEffect = VibrationEffect.createWaveform(pulseTiming, amplitude, repeat)
                        vibrator.vibrate(vibrationEffect)
                    }
                }
            } else {
                for (i in 0 until radioGroup.childCount) {
                    (radioGroup.getChildAt(i) as? RadioButton)?.isEnabled = false
                }
                radioGroup.clearCheck()
                vibrator.cancel()
            }
        }
    }
}