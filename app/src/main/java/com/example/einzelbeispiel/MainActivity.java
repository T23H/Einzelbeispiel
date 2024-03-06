package com.example.einzelbeispiel;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText matNr;
    TextView server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        matNr = (EditText)findViewById(R.id.editText);
        server = (TextView)findViewById(R.id.textView3);
        Button button = findViewById(R.id.buttonSend);
        Button button2 = findViewById(R.id.buttonCalc);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matrikelnummer = matNr.getText().toString().trim();
                contactServer(matrikelnummer);
            }
        });
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String matrikelnummer = matNr.getText().toString().trim();
                server.setText(sortWithoutPrimes(matrikelnummer));
            }
        });

    }

    private void contactServer(String matrikelnummer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //Verbindungsaufbau
                    Socket socket = new Socket("se2-submission.aau.at", 20080);

                    //Datenübertragung
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.write(matrikelnummer);

                    //Antwort
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String messageReceived = in.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            server.setText(messageReceived);
                        }
                    });

                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private String sortWithoutPrimes(String matrikelnummer){
        String input = matrikelnummer;
        List<Integer> number = new ArrayList<>();
        for (int i = 0; i < input.length(); i++){
            if(!isPrime(Character.getNumericValue(matrikelnummer.charAt(i)))) {
                number.add(Character.getNumericValue(matrikelnummer.charAt(i)));
            }
        }
        Collections.sort(number);
        return number.toString();
    }
    private boolean isPrime(int num){
        if (num <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
}