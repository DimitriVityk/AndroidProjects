package com.example.homeworkone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private TextView labelOne;
    private TextView labelTwo;
    private TextView result;
    private TextView history;
    private double convFact = 1.60934;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.userInput);
        labelOne = findViewById(R.id.labelTwo);
        labelTwo = findViewById(R.id.labelThree);
        result = findViewById(R.id.result);
        history = findViewById(R.id.history);
        history.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString("History", history.getText().toString());


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        history.setText(savedInstanceState.getString("History"));
    }

    public void doPress(View v) {
        String valIn = inputText.getText().toString();
        Double value = Double.parseDouble(valIn);

        double solution = value * convFact;

        inputText.setText("");
        result.setText(String.format("%.1f", solution));

        String hist = history.getText().toString();
        if (convFact == 1.60934)
        {
            history.setText(String.format("Mi to Km: %.1f ==> %.1f \n" + hist, value, solution));
        }
        else if(convFact == .621371)
        {
            history.setText(String.format("Km to Mi: %.1f ==> %.1f \n" + hist, value, solution));
        }
    }

    public void radioOnClick(View v) {
        switch (v.getId()) {
            case R.id.MtoK:
                convFact = 1.60934;
                result.setText("");
                labelOne.setText("Miles Value: ");
                labelTwo.setText("Kilometers Value: ");
                break;
            case R.id.KtoM:
                convFact = .621371;
                result.setText("");
                labelTwo.setText("Miles Value: ");
                labelOne.setText("Kilometers Value: ");
                break;

        }
    }

    public void clearOnClick(View v)
    {
        history.setText("");
        result.setText("");
    }


}