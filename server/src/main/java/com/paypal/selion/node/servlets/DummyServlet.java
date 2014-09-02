/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
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

package com.paypal.selion.node.servlets;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @deprecated - This is a dummy servlet that is intended ONLY for testing purposes. Please do not hook this servlet.
 * 
 */
public class DummyServlet extends HttpServlet {

    private static final long serialVersionUID = 9187677490975386050L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.process(req, resp);

    }

    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {

        VeryLargeObject[] vla = new VeryLargeObject[1 << 12];
        for (int i = 0; i < Integer.MAX_VALUE; ++i) {
            vla[i] = new VeryLargeObject();
        }
    }

    public static class VeryLargeObject implements Serializable {
        private static final long serialVersionUID = 1L;

        public static final int SIZE = 1 << 12;

        public int[][] bigOne = new int[SIZE][SIZE];

        public VeryLargeObject() {
            for (int i = 0; i < SIZE; ++i) {
                for (int j = 0; j < SIZE; ++j) {
                    bigOne[i][j] = (int) (Math.random() * 100);
                }
            }
        }

    }

}
