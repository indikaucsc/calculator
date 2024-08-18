package com.example.mit.calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView txtResult;
    private boolean lastNumeric;
    private boolean stateError;
    private boolean lastDot;

    private static final String TAG = "CalcDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResult = findViewById(R.id.txt_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnClear = findViewById(R.id.btn_clear);
        Button btnBackspace = findViewById(R.id.btn_backspace);
        Button btnPercentage = findViewById(R.id.btn_percentage);
        Button btnDivide = findViewById(R.id.btn_divide);
        Button btnMultiply = findViewById(R.id.btn_multiply);
        Button btnSubtract = findViewById(R.id.btn_subtract);
        Button btnAdd = findViewById(R.id.btn_add);
        Button btnEquals = findViewById(R.id.btn_equals);
        Button btnDot = findViewById(R.id.btn_dot);
        Button btnZero = findViewById(R.id.btn_zero);
        Button btnOne = findViewById(R.id.btn_one);
        Button btnTwo = findViewById(R.id.btn_two);
        Button btnThree = findViewById(R.id.btn_three);
        Button btnFour = findViewById(R.id.btn_four);
        Button btnFive = findViewById(R.id.btn_five);
        Button btnSix = findViewById(R.id.btn_six);
        Button btnSeven = findViewById(R.id.btn_seven);
        Button btnEight = findViewById(R.id.btn_eight);
        Button btnNine = findViewById(R.id.btn_nine);

        View.OnClickListener numberClickListener = v -> {
            if (stateError) {
                txtResult.setText(((Button) v).getText().toString());
                stateError = false;
            } else {
                String currentText = txtResult.getText().toString();
                if (currentText.equals("0")) {
                    txtResult.setText(((Button) v).getText().toString());
                } else {
                    txtResult.append(((Button) v).getText().toString());
                }
            }
            lastNumeric = true;
            Log.d(TAG, "Number clicked: " + ((Button) v).getText().toString());
        };

        btnZero.setOnClickListener(numberClickListener);
        btnOne.setOnClickListener(numberClickListener);
        btnTwo.setOnClickListener(numberClickListener);
        btnThree.setOnClickListener(numberClickListener);
        btnFour.setOnClickListener(numberClickListener);
        btnFive.setOnClickListener(numberClickListener);
        btnSix.setOnClickListener(numberClickListener);
        btnSeven.setOnClickListener(numberClickListener);
        btnEight.setOnClickListener(numberClickListener);
        btnNine.setOnClickListener(numberClickListener);

        View.OnClickListener operatorClickListener = v -> {
            if (lastNumeric && !stateError) {
                txtResult.append(" " + ((Button) v).getText().toString() + " ");
                lastNumeric = false;
                lastDot = false;
                Log.d(TAG, "Operator clicked: " + ((Button) v).getText().toString());
            }
        };

        btnAdd.setOnClickListener(operatorClickListener);
        btnSubtract.setOnClickListener(operatorClickListener);
        btnMultiply.setOnClickListener(operatorClickListener);
        btnDivide.setOnClickListener(operatorClickListener);
        btnPercentage.setOnClickListener(operatorClickListener);

        btnDot.setOnClickListener(v -> {
            if (lastNumeric && !stateError && !lastDot) {
                txtResult.append(".");
                lastNumeric = false;
                lastDot = true;
                Log.d(TAG, "Dot clicked");
            }
        });

        btnClear.setOnClickListener(v -> {
            txtResult.setText("0");
            lastNumeric = false;
            stateError = false;
            lastDot = false;
            Log.d(TAG, "Clear clicked");
        });

        btnBackspace.setOnClickListener(v -> {
            String text = txtResult.getText().toString();
            if (text.length() > 0) {
                txtResult.setText(text.substring(0, text.length() - 1));
            }
            if (txtResult.getText().toString().isEmpty()) {
                txtResult.setText("0");
            }
            Log.d(TAG, "Backspace clicked");
        });

        btnEquals.setOnClickListener(v -> {
            if (lastNumeric && !stateError) {
                try {
                    String expression = txtResult.getText().toString();
                    Log.d(TAG, "Evaluating expression: " + expression);
                    double result = evaluateExpression(expression);
                    txtResult.setText(String.valueOf(result));
                    lastDot = true;
                } catch (Exception e) {
                    Log.e(TAG, "Error evaluating expression", e);
                    txtResult.setText("Error");
                    stateError = true;
                    lastNumeric = false;
                }
            }
        });
    }

    private double evaluateExpression(String expression) {
        try {
            Log.d(TAG, "Start evaluating expression: " + expression);

            Stack<Double> numbers = new Stack<>();
            Stack<Character> operators = new Stack<>();

            String[] tokens = expression.split(" ");
            for (String token : tokens) {
                if (token.isEmpty()) continue;

                if (token.matches("-?\\d+(\\.\\d+)?")) {
                    numbers.push(Double.parseDouble(token));
                    Log.d(TAG, "Number pushed: " + token);
                } else if (token.length() == 1 && "+-*/%".contains(token)) {
                    char operator = token.charAt(0);
                    Log.d(TAG, "Operator found: " + operator);
                    while (!operators.isEmpty() && precedence(operator) <= precedence(operators.peek())) {
                        processOperator(numbers, operators.pop());
                    }
                    operators.push(operator);
                }
            }

            while (!operators.isEmpty()) {
                processOperator(numbers, operators.pop());
            }

            double result = numbers.pop();
            Log.d(TAG, "Expression result: " + result);
            return result;

        } catch (Exception e) {
            Log.e(TAG, "Error evaluating expression", e);
            throw new IllegalArgumentException("Invalid expression");
        }
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
                return 2;
            default:
                return -1;
        }
    }

    private void processOperator(Stack<Double> numbers, char operator) {
        double b = numbers.pop();
        double a = numbers.pop();
        double result = 0;

        switch (operator) {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                if (b != 0) {
                    result = a / b;
                } else {
                    throw new IllegalArgumentException("Division by zero");
                }
                break;
            case '%':
                result = a % b;
                break;
        }

        numbers.push(result);
        Log.d(TAG, "Processed operator: " + operator + ", result: " + result);
    }
}
