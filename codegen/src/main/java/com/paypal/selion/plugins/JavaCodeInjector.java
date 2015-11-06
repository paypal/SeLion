/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
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

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This Class will add additional methods, fields or import statement to existing java class file
 * 
 */
public class JavaCodeInjector {

    private final List<ImportDeclaration> importsFromExtendedFile;
    private final File baseFile;

    JavaCodeInjector(File extendedFile, File baseFile) throws IOException, ParseException {
        JavaCodeParser parser = new JavaCodeParser(extendedFile);
        this.importsFromExtendedFile = parser.getImports();
        this.baseFile = baseFile;
    }

    private boolean importAlreadyPresent(List<ImportDeclaration> importsList, ImportDeclaration imp) {
        for (ImportDeclaration eachImport : importsList) {
            if (eachImport.getName().toString().equals(imp.getName().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method will add methods, fields and import statement to existing java file
     * 
     * @throws IOException
     * @throws ParseException
     */
    public void insertCode() throws IOException, ParseException {
        CompilationUnit cuResult = JavaParser.parse(baseFile);
        if (cuResult.getImports() != null) {
            List<ImportDeclaration> importsFromBaseFile = cuResult.getImports();
            for (ImportDeclaration eachImport : importsFromExtendedFile) {
                if (!importAlreadyPresent(importsFromBaseFile, eachImport)) {
                    importsFromBaseFile.add(eachImport);
                }
            }
            cuResult.setImports(importsFromBaseFile);
        }

        String code = cuResult.toString();

        BufferedWriter b = new BufferedWriter(new FileWriter(baseFile));
        b.write(code);
        b.close();
    }

}
