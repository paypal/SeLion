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
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaCodeParser class will read java file and gives import statements, fields and methods of that java file
 */
public class JavaCodeParser {

    private final List<FieldDeclaration> fields;

    private final List<MethodDeclaration> methods;
    private List<ImportDeclaration> imports;

    private final File file;

    /**
     * @param file
     *            - The java file that needs to be parsed
     */
    JavaCodeParser(File file) {
        this.file = file;
        fields = new ArrayList<FieldDeclaration>();
        methods = new ArrayList<MethodDeclaration>();
        imports = new ArrayList<ImportDeclaration>();

    }

    /**
     * This method get all the import statement from the java compilation unit and return as a List
     * 
     * @return List of {@link ImportDeclaration}
     * @throws IOException
     * @throws ParseException
     */
    public List<ImportDeclaration> getImports() throws IOException, ParseException {
        if (imports.size() == 0) {
            initialize();
        }
        return imports;
    }

    /**
     * This method get all the fields from the java compilation unit and return as a List
     * 
     * @return List of {@link FieldDeclaration}
     * @throws IOException
     * @throws ParseException
     */
    public List<FieldDeclaration> getFields() throws IOException, ParseException {
        if (fields.size() == 0) {
            initialize();
        }
        return fields;
    }

    /**
     * This method get all the method details from the java compilation unit and return as a List
     * 
     * @return List of {@link MethodDeclaration}
     * @throws IOException
     * @throws ParseException
     */
    public List<MethodDeclaration> getMethods() throws IOException, ParseException {
        if (methods.size() == 0) {
            initialize();
        }
        return methods;
    }

    private void initialize() throws IOException, ParseException {
        CompilationUnit cu = JavaParser.parse(file);
        if (cu.getImports() != null) {
            imports = cu.getImports();
        }
        initializeFieldDetails(cu);
        initializeMethodDetails(cu);
    }

    private void initializeMethodDetails(CompilationUnit cu) {
        String className = file.getName();
        className = className.substring(0, className.indexOf("."));
        List<TypeDeclaration> types = cu.getTypes();
        for (TypeDeclaration type : types) {
            List<BodyDeclaration> members = type.getMembers();
            for (BodyDeclaration member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    method.setJavaDoc(new JavadocComment("@deprecated This method is moved to the class {@link "
                            + className + "}"));
                    methods.add(method);
                }
            }
        }
    }

    private void initializeFieldDetails(CompilationUnit cu) {
        List<TypeDeclaration> types = cu.getTypes();
        for (TypeDeclaration type : types) {
            List<BodyDeclaration> members = type.getMembers();
            for (BodyDeclaration member : members) {
                if (member instanceof FieldDeclaration) {
                    FieldDeclaration field = (FieldDeclaration) member;
                    fields.add(field);
                }
            }
        }
    }

}
