/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2016 PayPal                                                                                          |
|                                                                                                                     |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     |
|  with the License.                                                                                                  |
|                                                                                                                     |
|  You may obtain a copy of the License at                                                                            |
|                                                                                                                     |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                    |
|                                                                                                                     |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  |
|  the specific language governing permissions and limitations under the License.                                     |
\*-------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.pageobjectsdemoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class StateActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private TextView stateTextSlider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state);
        SeekBar seekBar = (SeekBar) findViewById(R.id.stateSeekBar);
        stateTextSlider = (TextView) findViewById(R.id.stateTextSlider);
        seekBar.setOnSeekBarChangeListener(this);
    }

    public void gotoPrevious(View view) {
        startActivity(new Intent(this, TouchDemoActivity.class));
        finish();
    }

    public void gotoNext(View view) {
        startActivity(new Intent(this, ScrollActivity.class));
        finish();
    }

    public void switchClicked(View view) {
        TextView textView = (TextView) findViewById(R.id.stateTxtSwitch);
        boolean checked = ((Switch) view).isChecked();
        String text = "Switch is " + (checked ? "ON" : "OFF");
        textView.setText(text);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        double pr = ((double) progress) / seekBar.getMax();
        stateTextSlider.setText(String.format("%1.6f", pr));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
