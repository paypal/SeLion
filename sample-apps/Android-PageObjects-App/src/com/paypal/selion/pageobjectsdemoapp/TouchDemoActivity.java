/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * <code>TouchDemoActivity</code> contains logic for touch functionality testing on seek bars and check boxes.
 */
public class TouchDemoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_demo);
        setUpAndroidCheckBox();
        setUpIosCheckBox();
        setUpSeekBar();
    }

    private void setUpSeekBar() {

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView seekBar_textview = (TextView) findViewById(R.id.seekBar_textview);
                seekBar_textview.setText("Value: " + progress);
            }
        });

    }

    private void setUpAndroidCheckBox() {
        final CheckBox androidCheckBox = (CheckBox) findViewById(R.id.android_checkbox);
        androidCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (androidCheckBox.isChecked()) {
                    TextView checkBox_textview = (TextView) findViewById(R.id.checkbox_textview);
                    checkBox_textview.setText("Android");
                } else {
                    TextView checkBox_textview = (TextView) findViewById(R.id.checkbox_textview);
                    checkBox_textview.setText("");
                }
            }
        });
    }

    private void setUpIosCheckBox() {
        final CheckBox iOSCheckBox = (CheckBox) findViewById(R.id.ios_checkbox);
        iOSCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iOSCheckBox.isChecked()) {
                    TextView checkBox_textview = (TextView) findViewById(R.id.checkbox_textview);
                    checkBox_textview.setText("iOS");
                } else {
                    TextView checkBox_textview = (TextView) findViewById(R.id.checkbox_textview);
                    checkBox_textview.setText("");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.page_objects_home, menu);
        invalidateOptionsMenu();
        menu.findItem(R.id.action_button).setTitle("Scroll");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_button) {
            Intent intent = new Intent(this, ScrollActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
