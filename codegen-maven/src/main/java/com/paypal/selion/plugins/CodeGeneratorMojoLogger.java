/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2017 PayPal                                                                                          |
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

package com.paypal.selion.plugins;

import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

public class CodeGeneratorMojoLogger implements CodeGeneratorLogger, Log {
    private final Log instance;

    public CodeGeneratorMojoLogger() {
        this.instance = new SystemStreamLog();
    }

    public CodeGeneratorMojoLogger(Log instance) {
        this.instance = instance;
    }

    @VisibleForTesting
    Log getInstance() {
        return instance;
    }

    @Override
    public void debug(String format) {
        instance.debug(format);
    }

    @Override
    public boolean isDebugEnabled() {
        return instance.isDebugEnabled();
    }

    @Override
    public void debug(CharSequence content) {
        instance.debug(content);
    }

    @Override
    public void debug(CharSequence content, Throwable error) {
        instance.debug(content, error);
    }

    @Override
    public void info(String string) {
        instance.info(string);
    }

    @Override
    public void debug(Throwable e) {
        instance.debug(e);
    }

    @Override
    public boolean isInfoEnabled() {
        return instance.isInfoEnabled();
    }

    @Override
    public void info(CharSequence content) {
        instance.info(content);
    }

    @Override
    public void info(CharSequence content, Throwable error) {
        instance.info(content, error);
    }

    @Override
    public void info(Throwable error) {
        instance.info(error);
    }

    @Override
    public boolean isWarnEnabled() {
        return instance.isWarnEnabled();
    }

    @Override
    public void warn(CharSequence content) {
        instance.warn(content);
    }

    @Override
    public void warn(CharSequence content, Throwable error) {
        instance.warn(content, error);
    }

    @Override
    public void warn(Throwable error) {
        instance.warn(error);
    }

    @Override
    public boolean isErrorEnabled() {
        return instance.isErrorEnabled();
    }

    @Override
    public void error(CharSequence content) {
        instance.error(content);
    }

    @Override
    public void error(CharSequence content, Throwable error) {
        instance.error(content, error);
    }

    @Override
    public void error(Throwable error) {
        instance.error(error);
    }
}
