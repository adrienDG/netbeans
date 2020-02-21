/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2015 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.repository.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import static junit.framework.TestCase.assertNotNull;
import org.netbeans.junit.Manager;
import org.netbeans.junit.RandomlyFails;
import org.netbeans.modules.cnd.api.model.CsmModelState;
import org.netbeans.modules.cnd.modelimpl.trace.TraceModel;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 */
@RandomlyFails
public class IncrementalParseRepositoryComposite extends RepositoryValidationBase {
    private static final RequestProcessor RP = new RequestProcessor("Sleep");
    private volatile boolean isShutdown = false;
    private volatile boolean dumpModel = true;
    private volatile long trueParsingTime = 0;

    public IncrementalParseRepositoryComposite(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        System.setProperty("cnd.repository.hardrefs", Boolean.TRUE.toString()); //NOI18N
        System.setProperty("org.netbeans.modules.cnd.apt.level","OFF"); // NOI18N
        System.setProperty("cnd.skip.err.check", Boolean.TRUE.toString()); //NOI18N
        System.setProperty("cnd.dump.skip.dummy.forward.classifier", Boolean.TRUE.toString()); //NOI18N
        super.setUp();
    }

    @Override
    protected void parsingTime(TraceModel.TestResult time) {
        trueParsingTime =time.getTime();
    }

    public void testRepository() throws Exception {
        File workDir = getWorkDir();
        setGoldenDirectory(workDir.getAbsolutePath());

        PrintStream streamOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(workDir+"/..", nimi + ".out"))));
        PrintStream streamErr = new FilteredPrintStream(new BufferedOutputStream(new FileOutputStream(new File(workDir+"/..", nimi + ".err"))));

        List<String> args = find();
        assert args.size() > 0;
        //args.add("-fq"); //NOI18N
        long currentTimeMillis = System.currentTimeMillis();
        doTest(args.toArray(new String[]{}), streamOut, streamErr);
        assertNoExceptions();
        System.err.println("IncrementalParseRepositoryComposite: pure parsing took "+trueParsingTime+ " ms.");
        System.err.println("IncrementalParseRepositoryComposite: first (golden) pass took "+(System.currentTimeMillis()-currentTimeMillis)+ " ms.");
        getTestModelHelper().shutdown(true);
        //
        setUp2();
        RP.post(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!isShutdown) {
                        isShutdown = true;
                        getTestModelHelper().shutdown(false);
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }, 500);
        dumpModel = false;
        currentTimeMillis = System.currentTimeMillis();
        performTest2(args.toArray(new String[]{}), nimi + ".out", nimi + ".err");
        assertNoExceptions();
        System.err.println("IncrementalParseRepositoryComposite: pure parsing took "+trueParsingTime+ " ms.");
        System.err.println("IncrementalParseRepositoryComposite: second (interrupted) pass took "+(System.currentTimeMillis()-currentTimeMillis)+ " ms.");
        if (!isShutdown) {
            isShutdown = true;
            getTestModelHelper().shutdown(false);
        }
        //
        dumpModel = true;
        setUp3();
        currentTimeMillis = System.currentTimeMillis();
        try {
        performTest(args.toArray(new String[]{}), nimi + ".out", nimi + ".err");
            assertNoExceptions();
        } finally {
            System.err.println("IncrementalParseRepositoryComposite: pure parsing took "+trueParsingTime+ " ms.");
            System.err.println("IncrementalParseRepositoryComposite: last (finishing interrupted parse) pass took "+(System.currentTimeMillis()-currentTimeMillis)+ " ms.");
        }
    }

    private void setUp2() throws Exception {
        System.setProperty("cnd.repository.hardrefs", Boolean.FALSE.toString()); //NOI18N
        System.setProperty("org.netbeans.modules.cnd.apt.level","OFF"); // NOI18N
        assertNotNull("This test can only be run from suite", RepositoryValidationBase.getGoldenDirectory()); //NOI18N
        System.setProperty(PROPERTY_GOLDEN_PATH, RepositoryValidationBase.getGoldenDirectory());
        super.setUp();
    }

    private void setUp3() throws Exception {
        System.setProperty("cnd.repository.hardrefs", Boolean.FALSE.toString()); //NOI18N
        System.setProperty("org.netbeans.modules.cnd.apt.level", "OFF"); // NOI18N
        System.setProperty(PROPERTY_GOLDEN_PATH, RepositoryValidationBase.getGoldenDirectory());
        cleanCache = false;
        super.setUp();
    }

    private void performTest2(String[] args, String goldenDataFileName, String goldenErrFileName, Object... params) throws Exception {
        File workDir = getWorkDir();

        File output = new File(workDir, goldenDataFileName);
        PrintStream streamOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(output)));
        File error = goldenErrFileName == null ? null : new File(workDir, goldenErrFileName);
        PrintStream streamErr = goldenErrFileName == null ? null : new FilteredPrintStream(new BufferedOutputStream(new FileOutputStream(error)));
        try {
            doTest(args, streamOut, streamErr, params);
        } finally {
            // restore err and out
            streamOut.close();
            if (streamErr != null) {
                streamErr.close();
            }
        }
    }

    @Override
    protected boolean returnOnShutdown() {
        if (!dumpModel) {
            if (CsmModelState.OFF == getTraceModel().getModel().getState()) {
                return true;
            }
            return false;
        } else {
            return super.returnOnShutdown();
        }
    }

    @Override
    protected boolean dumpModel() {
        return dumpModel;
    }

    @Override
    public File getGoldenFile(String filename) {
        String goldenDirPath = System.getProperty(PROPERTY_GOLDEN_PATH); // NOI18N
        if (goldenDirPath == null || goldenDirPath.length() == 0) {
            return super.getGoldenFile(filename);
        } else {
            return Manager.normalizeFile(new File(goldenDirPath+"/..", filename));
        }
    }
}
