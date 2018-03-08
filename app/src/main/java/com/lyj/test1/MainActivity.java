package com.lyj.test1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lyj.test1.CompilerInter.LyjFrame;
import com.lyj.test1.Tea.Tea;
import com.example.plugin.Annote.TeaType;

public class MainActivity extends AppCompatActivity {

    private TextView cenText;

    @TeaType(type = Tea.class, teaClassNmae = "TieGuanYin", id = "tieGuanYin", price = "20")
    public Tea tieguanyin;

    @TeaType(type = Tea.class, teaClassNmae = "RedTea", id = "redTea", price = "15")
    public Tea redTea;

    @TeaType(type = Tea.class, teaClassNmae = "GreenTea", id = "greenTea", price = "30")
    public Tea greenTea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cenText = (TextView) findViewById(R.id.cenText);
        LyjFrame.bind(this);
        initTea();
    }

    private void initTea() {
        cenText.setText(tieguanyin.getId() + " price is : " + tieguanyin.getPrice()
            +"\n" +
                redTea.getId() + " price is : " + redTea.getPrice()
                +"\n" +
                greenTea.getId() + " price is : " + greenTea.getPrice());
    }
}
