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
import android.widget.TextView;

import java.util.Date;

public class TapDemoActivity extends Activity {
    private static final long MULTI_TAP_TIME_OUT = 500;
    private int multiTapCount = 0;
    private long lastMultiTapClicked = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_demo);
        findViewById(R.id.tapBtnLongTap).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((TextView)findViewById(R.id.tapTxtLongTap)).setText("Long Clicked");
                return true;
            }
        });
    }

    public void gotoPrevious(View view) {
        startActivity(new Intent(this, PageObjectsHomeActivity.class));
        finish();
    }

    public void gotoNext(View view) {
        startActivity(new Intent(this, TouchDemoActivity.class));
        finish();
    }

    public void SingleTapClicked(View view) {
        TextView textView = (TextView) findViewById(R.id.tapTxtSingleTap);
        textView.setText("Tap Count: 1");
    }

    public void multiTapClicked(View view) {
        long now = new Date().getTime();
        if (now > lastMultiTapClicked + MULTI_TAP_TIME_OUT) {
            multiTapCount = 0;
        }
        lastMultiTapClicked = now;
        multiTapCount ++;
        TextView textView = (TextView) findViewById(R.id.tapTxtMultiTap);
        if (multiTapCount > 1) {
            textView.setText("Tap Count: " + multiTapCount);
        } else {
            textView.setText("");
        }
    }
}
