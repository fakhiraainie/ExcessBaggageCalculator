package com.example.excessbaggagecalculator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var etBaggageWeight: EditText
    private lateinit var etExcessRate: EditText
    private lateinit var rgTicketClass: RadioGroup
    private lateinit var rbEconomy: RadioButton
    private lateinit var rbBusiness: RadioButton
    private lateinit var btnCalculate: Button
    private lateinit var btnClear: Button
    private lateinit var tvExcessWeight: TextView
    private lateinit var tvExcessCharge: TextView
    private lateinit var tvTotalPayable: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind UI Elements
        etBaggageWeight = findViewById(R.id.etBaggageWeight)
        etExcessRate = findViewById(R.id.etExcessRate)
        rgTicketClass = findViewById(R.id.rgTicketClass)
        rbEconomy = findViewById(R.id.rbEconomy)
        rbBusiness = findViewById(R.id.rbBusiness)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnClear = findViewById(R.id.btnClear)
        tvExcessWeight = findViewById(R.id.tvExcessWeight)
        tvExcessCharge = findViewById(R.id.tvExcessCharge)
        tvTotalPayable = findViewById(R.id.tvTotalPayable)

        btnCalculate.setOnClickListener {
            calculateExcessFee()
        }

        btnClear.setOnClickListener {
            resetFields()
        }
    }

    private fun calculateExcessFee() {
        val weightInput = etBaggageWeight.text.toString().trim()
        val rateInput = etExcessRate.text.toString().trim()

        // Validation for empty inputs
        if (weightInput.isEmpty()) {
            etBaggageWeight.error = getString(R.string.err_empty_weight)
            etBaggageWeight.requestFocus()
            return
        }

        if (rateInput.isEmpty()) {
            etExcessRate.error = getString(R.string.err_empty_rate)
            etExcessRate.requestFocus()
            return
        }

        val weight = weightInput.toDoubleOrNull()
        val rate = rateInput.toDoubleOrNull()

        if (weight == null || rate == null || weight < 0 || rate < 0) {
            Toast.makeText(this, getString(R.string.err_invalid_input), Toast.LENGTH_SHORT).show()
            return
        }

        // Determine free allowance base on selected class
        val allowance = if (rbEconomy.isChecked) 20.0 else 40.0

        // Calculate Excess Weight (Min 0 kg)
        val excessWeight = if (weight > allowance) weight - allowance else 0.0

        // Calculate Charges
        val excessCharge = excessWeight * rate
        val serviceTax = excessCharge * 0.08
        val totalPayable = excessCharge + serviceTax

        // Update Output UI
        tvExcessWeight.text = String.format(Locale.getDefault(), "Excess Weight: %.2f kg", excessWeight)
        tvExcessCharge.text = String.format(Locale.getDefault(), "Excess Charge: RM %.2f", excessCharge)
        tvTotalPayable.text = String.format(Locale.getDefault(), "Total Payable (incl. 8%% tax): RM %.2f", totalPayable)
    }

    private fun resetFields() {
        etBaggageWeight.text.clear()
        etExcessRate.text.clear()
        rbEconomy.isChecked = true
        tvExcessWeight.text = getString(R.string.label_excess_weight)
        tvExcessCharge.text = getString(R.string.label_excess_charge)
        tvTotalPayable.text = getString(R.string.label_total_payable)
        etBaggageWeight.requestFocus()
    }

    // Inflate toolbar options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Handle toolbar button clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Excess Baggage Fee Calculator")
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out this Baggage Fee Calculator app repository: https://github.com/yourusername/ExcessBaggageCalculator"
                    )
                }
                startActivity(Intent.createChooser(shareIntent, "Share App Link via"))
                true
            }
            R.id.action_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}