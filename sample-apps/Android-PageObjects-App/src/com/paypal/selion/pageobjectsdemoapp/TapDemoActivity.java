/*----------------------------------------------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                                                                |
|                                                                                                                                                           |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance                                           |
|  with the License.                                                                                                                                        |
|                                                                                                                                                           |
|  You may obtain a copy of the License at                                                                                                                  |
|                                                                                                                                                           |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                                                          |
|                                                                                                                                                           |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed                                         |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for                                        |
|  the specific language governing permissions and limitations under the License.                                                                           |
\*----------------------------------------------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.pageobjectsdemoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * <code>TapDemoActivity</code> contains logic for button press and text view functionality testing.
 */
public class TapDemoActivity extends ActionBarActivity {

    private GestureDetector shortPressGestureDetector;

    private GestureDetector longPressGestureDetector;

    private Button shortPressButton;

    private Button longPressButton;

    private TextView shortPressTextView;

    private TextView longPressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_demo);
        shortPressGestureDetector = new GestureDetector(this, new ShortPressGestureListener());
        longPressGestureDetector = new GestureDetector(this, new LongPressGestureListener());
        shortPressButton = (Button) findViewById(R.id.short_press_button);
        longPressButton = (Button) findViewById(R.id.long_press_button);
        shortPressTextView = (TextView) findViewById(R.id.short_press_button_output);
        longPressTextView = (TextView) findViewById(R.id.long_press_button_output);
        setupSingleTapButton();
        setupDoubleTapButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shortPressTextView.setText("");
        longPressTextView.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.page_objects_home, menu);
        invalidateOptionsMenu();
        menu.findItem(R.id.action_button).setTitle("Touch");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_button) {
            Intent intent = new Intent(this, TouchDemoActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSingleTapButton() {
        shortPressButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return shortPressGestureDetector.onTouchEvent(event);
            }
        });
    }

    private void setupDoubleTapButton() {
        longPressButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return longPressGestureDetector.onTouchEvent(event);
            }
        });
    }

    public class LongPressGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            longPressTextView.setText("x= " + e.getRawX() + ",y= " + e.getRawY() + ", long press");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TapDemoActivity", "Double tap fired");
            return true;
        }

    }

    public class ShortPressGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            shortPressTextView.setText("x= " + e.getRawX() + ",y= " + e.getRawY() + ", short press");
            return true;
        }

    }
}
