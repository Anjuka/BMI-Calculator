package com.angleone.bmical

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.angleone.bmical.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private val TAG = "ResultActivity"
    private lateinit var binding: ActivityResultBinding
    private var bmi = 0.0
    private var status = Pair("", "")
    private var details = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent = intent
        bmi = intent.getDoubleExtra("bmi", 0.0)
        details = intent.getStringExtra("details") ?: ""
        Log.d(TAG, "onCreate: BMI $bmi")
        binding.tvBmiVal.text = String.format("%.2f", bmi).toDouble().toString()
        getStatusFromBMI(bmi)
        binding.tvDetails.text = details

        binding.btnReCal.setOnClickListener {
            finish()
        }
    }

    private fun getStatusFromBMI(bmi: Double) {
        if (bmi < 18.5) {
            // Underweight
            binding.tvBmiStatus.text = "Under weight"
            binding.tvBmiStatus.setTextColor(Color.parseColor("#60ccf3"))
            binding.tvBmiText.text = "You have to give attention to your meals and activities..."
        } else if (bmi >= 18.5 && bmi < 24.9) {
            // Healthy Weight
            binding.tvBmiStatus.text = "Healthy weight"
            binding.tvBmiStatus.setTextColor(Color.parseColor("#63bc46"))
            binding.tvBmiText.text = "You done a great job, Keep it up..."
        } else if (bmi >= 25.0 && bmi < 29.9) {
            // Overweight
            binding.tvBmiStatus.text = "Over weight"
            binding.tvBmiStatus.setTextColor(Color.parseColor("#f78f2c"))
            binding.tvBmiText.text = "Control your meals and increase your workout plan..."
        }else {
            // Obesity
            binding.tvBmiStatus.text = "Obesity"
            binding.tvBmiStatus.setTextColor(Color.parseColor("#ee3928"))
            binding.tvBmiText.text = "You have to give immediate attention to your meals and activities..."
        }
    }
}