package com.example.studybuddy

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class GradeCalculatorActivity : Activity() {

    private lateinit var activityContainer: LinearLayout
    private lateinit var quizContainer: LinearLayout

    private var activityCount = 0
    private var quizCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_grade_calculator)

        activityContainer =
            findViewById(R.id.activityContainer)

        quizContainer =
            findViewById(R.id.quizContainer)

        val btnAddActivity =
            findViewById<Button>(R.id.btnAddActivity)

        val btnAddQuiz =
            findViewById<Button>(R.id.btnAddQuiz)

        val examScore =
            findViewById<EditText>(R.id.etExamScore)

        val examTotal =
            findViewById<EditText>(R.id.etExamTotal)

        val btnCalculate =
            findViewById<Button>(R.id.btnCalculate)

        val resultText =
            findViewById<TextView>(R.id.tvResult)


        // Create the first Activity and Quiz automatically
        addActivityField()
        addQuizField()


        // =========================
        // ADD ACTIVITY
        // =========================

        btnAddActivity.setOnClickListener {

            addActivityField()
        }


        // =========================
        // ADD QUIZ
        // =========================

        btnAddQuiz.setOnClickListener {

            addQuizField()
        }


        // =========================
        // CALCULATE
        // =========================

        btnCalculate.setOnClickListener {

            val activityPercentages =
                getPercentages(activityContainer)

            val quizPercentages =
                getPercentages(quizContainer)

            val examScoreValue =
                examScore.text
                    .toString()
                    .toDoubleOrNull()

            val examTotalValue =
                examTotal.text
                    .toString()
                    .toDoubleOrNull()


            // Check if Activities or Quizzes are incomplete
            if (
                activityPercentages == null ||
                quizPercentages == null
            ) {

                resultText.text =
                    "Please complete all Activity and Quiz fields."

                return@setOnClickListener
            }


            // Check Exam fields
            if (
                examScoreValue == null ||
                examTotalValue == null
            ) {

                resultText.text =
                    "Please enter the Exam score and total score."

                return@setOnClickListener
            }


            // Total score cannot be zero or negative
            if (examTotalValue <= 0) {

                resultText.text =
                    "Exam total score must be greater than 0."

                return@setOnClickListener
            }


            // Score cannot be negative
            if (examScoreValue < 0) {

                resultText.text =
                    "Exam score cannot be negative."

                return@setOnClickListener
            }


            // Score cannot be higher than total
            if (examScoreValue > examTotalValue) {

                resultText.text =
                    "Exam score cannot be higher than the total score."

                return@setOnClickListener
            }


            // =========================
            // CATEGORY AVERAGES
            // =========================

            val activityAverage =
                activityPercentages.average()

            val quizAverage =
                quizPercentages.average()

            val examPercentage =
                (examScoreValue / examTotalValue) * 100


            // =========================
            // FINAL GRADE
            // =========================

            val finalPercentage =
                (
                        activityAverage +
                                quizAverage +
                                examPercentage
                        ) / 3


            // Philippine-style equivalent grade
            val equivalentGrade =
                getEquivalentGrade(finalPercentage)


            val remarks =
                if (finalPercentage >= 75) {
                    "PASSED"
                } else {
                    "FAILED"
                }


            // =========================
            // DISPLAY RESULT
            // =========================

            resultText.text =
                """
                GRADE RESULT
                
                Activity Average: %.2f%%
                Quiz Average: %.2f%%
                Exam: %.2f%%
                
                Final Percentage: %.2f%%
                Equivalent Grade: %s
                Remarks: %s
                """.trimIndent().format(
                    activityAverage,
                    quizAverage,
                    examPercentage,
                    finalPercentage,
                    equivalentGrade,
                    remarks
                )
        }
    }


    // ==========================================================
    // ADD ACTIVITY FIELD
    // ==========================================================

    private fun addActivityField() {

        activityCount++

        val group =
            createScoreField(
                "Activity $activityCount"
            )

        activityContainer.addView(group)
    }


    // ==========================================================
    // ADD QUIZ FIELD
    // ==========================================================

    private fun addQuizField() {

        quizCount++

        val group =
            createScoreField(
                "Quiz $quizCount"
            )

        quizContainer.addView(group)
    }


    // ==========================================================
    // CREATE SCORE / TOTAL SCORE FIELD
    // ==========================================================

    private fun createScoreField(
        title: String
    ): LinearLayout {

        // Main group
        val group =
            LinearLayout(this)

        group.orientation =
            LinearLayout.VERTICAL

        group.setPadding(
            dp(15),
            dp(12),
            dp(15),
            dp(12)
        )


        // Title
        val titleText =
            TextView(this)

        titleText.text =
            title

        titleText.textSize =
            17f

        titleText.setTextColor(
            Color.BLACK
        )

        titleText.setTypeface(
            null,
            Typeface.BOLD
        )


        group.addView(titleText)


        // Horizontal row
        val scoreRow =
            LinearLayout(this)

        scoreRow.orientation =
            LinearLayout.HORIZONTAL

        scoreRow.gravity =
            Gravity.CENTER_VERTICAL


        // =========================
        // SCORE INPUT
        // =========================

        val scoreInput =
            EditText(this)

        scoreInput.hint =
            "Score"

        scoreInput.inputType =
            InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL

        scoreInput.layoutParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        scoreInput.setPadding(
            dp(10),
            dp(10),
            dp(10),
            dp(10)
        )


        // =========================
        // TOTAL SCORE INPUT
        // =========================

        val totalInput =
            EditText(this)

        totalInput.hint =
            "Total Score"

        totalInput.inputType =
            InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL

        totalInput.layoutParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        totalInput.setPadding(
            dp(10),
            dp(10),
            dp(10),
            dp(10)
        )


        scoreRow.addView(scoreInput)
        scoreRow.addView(totalInput)

        group.addView(scoreRow)

        return group
    }


    // ==========================================================
    // GET PERCENTAGES
    // ===   z=======================================================

    private fun getPercentages(
        container: LinearLayout
    ): List<Double>? {

        val percentages =
            mutableListOf<Double>()


        for (
        i in 0 until container.childCount
        ) {

            val group =
                container.getChildAt(i)
                        as LinearLayout


            val scoreRow =
                group.getChildAt(1)
                        as LinearLayout


            val scoreInput =
                scoreRow.getChildAt(0)
                        as EditText


            val totalInput =
                scoreRow.getChildAt(1)
                        as EditText


            val score =
                scoreInput.text
                    .toString()
                    .toDoubleOrNull()


            val total =
                totalInput.text
                    .toString()
                    .toDoubleOrNull()


            // Missing values
            if (
                score == null ||
                total == null
            ) {

                return null
            }


            // Invalid total
            if (total <= 0) {

                return null
            }


            // Negative score
            if (score < 0) {

                return null
            }


            // Score cannot exceed total
            if (score > total) {

                return null
            }


            // Convert to percentage
            val percentage =
                (score / total) * 100


            percentages.add(
                percentage
            )
        }


        return percentages
    }


    // ==========================================================
    // PHILIPPINE-STYLE COLLEGE GRADE
    // ==========================================================

    private fun getEquivalentGrade(
        percentage: Double
    ): String {

        return when {

            percentage >= 97 ->
                "1.00"

            percentage >= 94 ->
                "1.25"

            percentage >= 91 ->
                "1.50"

            percentage >= 88 ->
                "1.75"

            percentage >= 85 ->
                "2.00"

            percentage >= 82 ->
                "2.25"

            percentage >= 79 ->
                "2.50"

            percentage >= 76 ->
                "2.75"

            percentage >= 75 ->
                "3.00"

            else ->
                "5.00"
        }
    }


    // ==========================================================
    // DP CONVERTER
    // ==========================================================

    private fun dp(
        value: Int
    ): Int {

        return (
                value *
                        resources.displayMetrics.density
                ).toInt()
    }
}