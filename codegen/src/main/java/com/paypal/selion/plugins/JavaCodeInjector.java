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

package com.paypal.selion.plugins;

import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;

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

    private List<FieldDeclaration> fieldsFromExtendedFile;
    private List<MethodDeclaration> methodsFromExtendedFile;
    private List<ImportDeclaration> importsFromExtendedFile;
    private File baseFile;

    JavaCodeInjector(File extendedFile, File baseFile) throws IOException, ParseException {
        JavaCodeParser parser = new JavaCodeParser(extendedFile);
        this.fieldsFromExtendedFile = parser.getFields();
        this.methodsFromExtendedFile = parser.getMethods();
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

        ClassOrInterfaceDeclaration classTemp = getClassOrInterfaceDeclaration(cuResult);
        for (FieldDeclaration eachField : fieldsFromExtendedFile) {
            List<AnnotationExpr> annotExpList = eachField.getAnnotations();
            pruneAutoRefactor(annotExpList, classTemp, eachField);
        }

        for (MethodDeclaration eachMethod : methodsFromExtendedFile) {
            List<AnnotationExpr> annotExpList = eachMethod.getAnnotations();
            pruneAutoRefactor(annotExpList, classTemp, eachMethod);
        }

        String code = cuResult.toString();

        BufferedWriter b = new BufferedWriter(new FileWriter(baseFile));
        b.write(code);
        b.close();
    }

    private void pruneAutoRefactor(List<AnnotationExpr> annotations, ClassOrInterfaceDeclaration fromClass,
            BodyDeclaration from) {
        if (annotations == null) {
            return;
        }
        for (AnnotationExpr eachAnnotation : annotations) {
            if (eachAnnotation.getName().getName().equals("AutoRefactored")) {
                if (from instanceof MethodDeclaration) {
                    removeMethod(fromClass, (MethodDeclaration) from);
                }
                ASTHelper.addMember(fromClass, from);
            }
        }
    }

    private ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration(CompilationUnit cu) {

        List<TypeDeclaration> types = cu.getTypes();
        for (TypeDeclaration type : types) {
            if (type instanceof ClassOrInterfaceDeclaration) {
                return (ClassOrInterfaceDeclaration) type;
            }
        }
        return null;
    }

    private void removeMethod(ClassOrInterfaceDeclaration fromClass, MethodDeclaration methodToRemove) {

        List<BodyDeclaration> members = fromClass.getMembers();
        for (BodyDeclaration member : members) {
            boolean isMemberAMethod = (member instanceof MethodDeclaration);
            // We are interested in only methods
            if (!isMemberAMethod) {
                continue;
            }
            MethodDeclaration memberMethod = (MethodDeclaration) member;
            boolean matchingMethod = (memberMethod.getName().equals(methodToRemove.getName()));
            // If we are here it means we have found a method. But we need only methods that match
            // in names
            if (!matchingMethod) {
                continue;
            }
            // So we found a method that matches in name. Lets do a parameter comparison to decide
            // if it has to be removed or not.
            if (methodsWithSimilarParameters(memberMethod, methodToRemove)) {
                fromClass.getMembers().remove(methodToRemove);
            }
        }
    }

    private boolean methodsWithSimilarParameters(MethodDeclaration methodA, MethodDeclaration methodB) {
        List<Parameter> methodAParams = methodA.getParameters();
        List<Parameter> methodBParams = methodB.getParameters();
        boolean voidMethods = (methodAParams == null && methodBParams == null);
        if (voidMethods) {
            return true;
        }
        if (!voidMethods) {
            boolean sameParamCount = (methodAParams.size() == methodBParams.size());
            if (!sameParamCount) {
                // the parameter count didn't match. So they dont match.
                return false;
            }
            // Count matched which means we encountered 2 methods with the same list of parameters.
            // time to check if the parameters match as well.
            // TODO: Is there a bug lurking here which is still not come out ?
            // does list.containsAll() check for order also because the current logic
            // checks if the parameter types match, but doesn't check if the order match as well
            // The current logic will return true for : foo(int, String) and foo(String, int)
            // which is wrong.
            return methodAParams.containsAll(methodBParams);
        }
        return false;
    }
}
