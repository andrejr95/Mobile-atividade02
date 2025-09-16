package com.example.calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var tvDisplay: TextView
    private lateinit var tvHistory: TextView

    private var currentInput: String = ""
    private var operand: Double? = null
    private var pendingOp: String? = null
    private var historyText: String = ""
    private var isResultDisplayed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.txtResultado)
        tvHistory = findViewById(R.id.txtHistory)

        val digits = listOf(
            "0" to R.id.btn0,
            "1" to R.id.btn1,
            "2" to R.id.btn2,
            "3" to R.id.btn3,
            "4" to R.id.btn4,
            "5" to R.id.btn5,
            "6" to R.id.btn6,
            "7" to R.id.btn7,
            "8" to R.id.btn8,
            "9" to R.id.btn9,
            "." to R.id.btnPonto
        )
        digits.forEach { (digit, id) ->
            findViewById<Button>(id).setOnClickListener { appendDigit(digit) }
        }

        val ops = listOf(
            "+" to R.id.btnSomar,
            "-" to R.id.btnSubtrair,
            "×" to R.id.btnMultiplicar,
            "÷" to R.id.btnDividir
        )
        ops.forEach { (op, id) ->
            findViewById<Button>(id).setOnClickListener { onOperator(op) }
        }

        findViewById<Button>(R.id.btnIgual).setOnClickListener { onEquals() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { backspace() }

        updateDisplay()
        updateHistoryDisplay()
    }

    private fun appendDigit(d: String) {
        if (isResultDisplayed) {
            currentInput = ""
            operand = null
            pendingOp = null
            isResultDisplayed = false
        }
        if (d == "." && currentInput.contains(".")) return
        currentInput = if (currentInput == "0") d else currentInput + d
        updateDisplay()
    }

    private fun onOperator(op: String) {
        if (isResultDisplayed) {
            isResultDisplayed = false
        }
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                if (operand == null) {
                    operand = value
                } else {
                    val result = performOperation(operand!!, value, pendingOp)
                    val operation = "${operand} ${pendingOp} ${value} = ${result}\n"
                    historyText += operation
                    operand = result
                }
            }
            currentInput = ""
        }
        pendingOp = op
        updateDisplay()
        updateHistoryDisplay()
    }

    private fun onEquals() {
        if (operand != null && currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull() ?: return
            val result = performOperation(operand!!, value, pendingOp)

            val operation = "${operand} ${pendingOp} ${value} = ${result}\n"
            historyText += operation

            currentInput = result.toString()
            operand = null
            pendingOp = null
            isResultDisplayed = true

            updateDisplay()
            updateHistoryDisplay()
        }
    }

    private fun performOperation(a: Double, b: Double, op: String?): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) {
                Toast.makeText(this, "Divisão por zero", Toast.LENGTH_SHORT).show()
                a
            } else a / b
            else -> b
        }
    }

    private fun clearAll() {
        currentInput = ""
        operand = null
        pendingOp = null
        historyText = ""
        isResultDisplayed = false
        updateDisplay()
        updateHistoryDisplay()
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        val displayStr = StringBuilder()
        if (operand != null) {
            displayStr.append(operand)
            if (pendingOp != null) {
                displayStr.append(" ").append(pendingOp).append(" ")
            }
        }
        displayStr.append(currentInput)

        tvDisplay.text = if (displayStr.toString().isEmpty()) "0" else displayStr.toString()
    }

    private fun updateHistoryDisplay() {
        tvHistory.text = historyText
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentInput", currentInput)
        outState.putDouble("operand", operand ?: Double.NaN)
        outState.putString("pendingOp", pendingOp)
        outState.putString("historyText", historyText)
        outState.putBoolean("isResultDisplayed", isResultDisplayed)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentInput = savedInstanceState.getString("currentInput", "")
        val opnd = savedInstanceState.getDouble("operand", Double.NaN)
        operand = if (opnd.isNaN()) null else opnd
        pendingOp = savedInstanceState.getString("pendingOp")
        historyText = savedInstanceState.getString("historyText", "")
        isResultDisplayed = savedInstanceState.getBoolean("isResultDisplayed", false)
        updateDisplay()
        updateHistoryDisplay()
    }
}