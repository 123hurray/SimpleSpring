package com.rayxxzhang.simplespring.test.core;

import java.io.PrintStream;

/**
 * Created by Ray on 3/23.
 */
public class TestBean3 implements ITestBean3 {
    private PrintStream ps;
    private TestBean2 testBean2;

    public TestBean3(PrintStream ps, TestBean2 testBean2) {
        this.ps = ps;
        this.testBean2 = testBean2;
    }

    public void testMethod3() {
        ps.println("I'm TestBean3");
    }

    public TestBean2 getTestBean2() {
        return testBean2;
    }
}
