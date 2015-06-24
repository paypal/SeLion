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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * <code>ScrollActivity</code> contains logic for table scroll functionality testing.
 */
public class ScrollActivity extends ActionBarActivity {

    /*
     * TextView references for table cells
     */
    private TextView[] textViews = null;

    /*
     * Number of cells
     */
    private static final int cellsCount = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_demo);
        setUpTableCells();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpTableCells() {
        textViews = new TextView[cellsCount];
        textViews[0] = (TextView) findViewById(R.id.TextView1);
        textViews[1] = (TextView) findViewById(R.id.TextView2);
        textViews[2] = (TextView) findViewById(R.id.TextView3);
        textViews[3] = (TextView) findViewById(R.id.TextView4);
        textViews[4] = (TextView) findViewById(R.id.TextView5);
        textViews[5] = (TextView) findViewById(R.id.TextView6);
        textViews[6] = (TextView) findViewById(R.id.TextView7);
        textViews[7] = (TextView) findViewById(R.id.TextView8);
        textViews[8] = (TextView) findViewById(R.id.TextView9);
        textViews[9] = (TextView) findViewById(R.id.TextView10);
        textViews[10] = (TextView) findViewById(R.id.TextView11);
        textViews[11] = (TextView) findViewById(R.id.TextView12);
        textViews[12] = (TextView) findViewById(R.id.TextView13);
        textViews[13] = (TextView) findViewById(R.id.TextView14);
        final Context context = this;
        for (int i = 0; i < cellsCount; i++) {
            textViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView) v;
                    Log.i("MainActivity", textView.getText().toString());
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage(textView.getText());
                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.page_objects_home, menu);
        invalidateOptionsMenu();
        menu.findItem(R.id.action_button).setTitle("End");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_button) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
