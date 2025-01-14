package com.marcuspereira.calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        var operator: String? = null
        var display: String = ""
        var statusResult: Boolean = false
        var resultGlobal: Double = 0.0

        val btnPoint = findViewById<Button>(R.id.btn_Ponto)
        val btnZero = findViewById<Button>(R.id.btn_Zero)
        val btnBackSpace = findViewById<ImageButton>(R.id.btn_BackSpace)
        val btnResult = findViewById<Button>(R.id.btn_Result)
        val btnOne = findViewById<Button>(R.id.btn_One)
        val btnTwo = findViewById<Button>(R.id.btn_Two)
        val btnThree = findViewById<Button>(R.id.btn_Three)
        val btnFour = findViewById<Button>(R.id.btn_Four)
        val btnFive = findViewById<Button>(R.id.btn_Five)
        val btnSix = findViewById<Button>(R.id.btn_Six)
        val btnSeven = findViewById<Button>(R.id.btn_Seven)
        val btnEight = findViewById<Button>(R.id.btn_Eight)
        val btnNine = findViewById<Button>(R.id.btn_Nine)
        val btnSum = findViewById<Button>(R.id.btn_Sum)
        val btnSubtraction = findViewById<Button>(R.id.btn_Subtraction)
        val btnMultiplication = findViewById<Button>(R.id.btn_Multiplication)
        val btnCancel = findViewById<Button>(R.id.btn_Cancel)
        val btnPlusMinus = findViewById<ImageButton>(R.id.btn_PlusMinus)
        val btnPercentage = findViewById<Button>(R.id.btn_Percentage)
        val btnDivision = findViewById<ImageButton>(R.id.btn_Division)

        val tvResult = findViewById<TextView>(R.id.tv_Result)
        val tvOperation = findViewById<TextView>(R.id.tv_Operation)

        btnZero.setOnClickListener { display = clickNumber("0", display, tvResult, tvOperation) }

        btnOne.setOnClickListener { display = clickNumber("1", display, tvResult, tvOperation) }

        btnTwo.setOnClickListener { display = clickNumber("2", display, tvResult, tvOperation) }

        btnThree.setOnClickListener { display = clickNumber("3", display, tvResult, tvOperation) }

        btnFour.setOnClickListener { display = clickNumber("4", display, tvResult, tvOperation) }

        btnFive.setOnClickListener { display = clickNumber("5", display, tvResult, tvOperation) }

        btnSix.setOnClickListener { display = clickNumber("6", display, tvResult, tvOperation) }

        btnSeven.setOnClickListener { display = clickNumber("7", display, tvResult, tvOperation) }

        btnEight.setOnClickListener { display = clickNumber("8", display, tvResult, tvOperation) }

        btnNine.setOnClickListener { display = clickNumber("9", display, tvResult, tvOperation) }

        btnPoint.setOnClickListener { display = clickNumber(".", display, tvResult, tvOperation) }

        btnSum.setOnClickListener {
            if (display.isNotEmpty()) {
                display += "+"
                tvResult.text = display
            }
        }

        btnSubtraction.setOnClickListener {
            if (display.isNotEmpty()) {
                display += "-"
                tvResult.text = display
            }
        }

        btnMultiplication.setOnClickListener {
            if (display.isNotEmpty()) {
                display += "x"
                tvResult.text = display
            }
        }

        btnDivision.setOnClickListener {
            if (display.isNotEmpty()) {
                display += "÷"
                tvResult.text = display
            }
        }

        btnPlusMinus.setOnClickListener {
            if (display.isEmpty()) {
                display = (-0).toString()
                tvResult.text = display
                return@setOnClickListener
            }
            try {
                val number = display.toDoubleOrNull() ?: return@setOnClickListener

                val numberInverted = if (!statusResult) {
                    invertedNumber(number).also { display = it.toString() }
                } else {
                    invertedNumber(resultGlobal).also { resultGlobal = it }
                }
                val invertedFormatado = formatoNumber(numberInverted)
                tvResult.text = invertedFormatado
            } catch (e: Exception) {
                tvResult.text = "Erro"
            }
        }

        btnPercentage.setOnClickListener {
            val number = display.toDoubleOrNull() ?: 0.0
            val numberCalculo = number / 100
            val numberformat = formatoNumber(numberCalculo)
            display = numberformat
            tvOperation.text = display

        }

        btnResult.setOnClickListener {

            if (display.isEmpty()) {
                tvResult.text = "Erro"
                return@setOnClickListener
            }

            try {
                if (!display.matches(Regex("^-?\\d+(\\.\\d+)?([-+x/÷]-?\\d+(\\.\\d+)?)*\$"))) {
                    tvResult.text = "Erro"
                    return@setOnClickListener
                }

                val result = CalculationExpression(display)
                val resultFormatado = formatoNumber(result)

                statusResult = true
                resultGlobal = result

                tvOperation.text = display
                tvResult.text = resultFormatado

                operator = null
                display = ""

            } catch (e: Exception) {
                tvResult.text = "Erro"
            }
        }

        btnCancel.setOnClickListener {
            display = ""
            operator = null
            tvOperation.text = ""
            tvResult.text = "0"
        }

        btnBackSpace.setOnClickListener {
            if (display.isNotEmpty()) {
                display = display.dropLast(1) //Remove o último caractere da string
            }
            if (display.isEmpty()) {
                display = "0"
            }
            tvResult.text = display
        }
    }

    fun clickNumber(
        input: String, display: String, tvResult: TextView, tvOperation: TextView
    ): String {
        var updateDisplay = display

        if (updateDisplay.isEmpty()) {
            tvOperation.text = ""
        }

        if (input == ".") {
            updateDisplay = if (updateDisplay.isEmpty()) {
                "0."
            } else if (!updateDisplay.endsWith(".") && !updateDisplay.contains(Regex("\\\\d+\\\\.\\\\d*\$"))) {
                updateDisplay + "."
            } else {
                updateDisplay
            }
        } else {
            updateDisplay += input
        }
        if (updateDisplay == "0") {
            updateDisplay = ""
        }

        tvResult.text = updateDisplay
        return updateDisplay
    }


    fun CalculationExpression(expressao: String): Double {
        val tokens = expressao.split("(?<=[-+x÷])|(?=[-+x÷])".toRegex())
        val numbers = mutableListOf<Double>()
        val operations = mutableListOf<Char>()
        tokens.forEach { token ->
            if (token.matches(Regex("-?\\d+(\\.\\d+)?"))) {
                numbers.add(token.toDouble())
            } else if (token.matches(Regex("[-+x÷]"))) {
                operations.add(token[0])
            }
        }

        var i = 0
        while (i < operations.size) {
            if (operations[i] == 'x' || operations[i] == '÷') {
                val result = if (operations[i] == 'x') {
                    numbers[i] * numbers[i + 1]
                } else {
                    if (numbers[i + 1] == 0.0) throw ArithmeticException("Erro")
                    numbers[i] / numbers[i + 1]
                }
                numbers[i] = result
                numbers.removeAt(i + 1)
                operations.removeAt(i)
            } else {
                i++
            }
        }

        i = 0
        while (i < operations.size) {
            val result = when (operations[i]) {
                '+' -> numbers[i] + numbers[i + 1]
                '-' -> numbers[i] - numbers[i + 1]
                else -> throw IllegalArgumentException("Operação desconhecida: ${operations[i]}")
            }
            numbers[i] = result
            numbers.removeAt(i + 1)
            operations.removeAt(i)
        }

        return numbers[0]
    }

    fun formatoNumber(number: Double): String {
        return if (number % 1 == 0.0) {
            number.toInt().toString()
        } else {
            "%.2f".format(number).replace(".", ",")
        }
    }

    fun invertedNumber(number: Double): Double {
        return -number
    }
}