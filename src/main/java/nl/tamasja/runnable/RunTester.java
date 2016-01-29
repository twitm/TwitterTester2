package nl.tamasja.runnable;

import nl.tamasja.tester.ITester;

/**
 * TIS 28-8-2014.15:42
 */
public class RunTester implements Runnable {

    protected ITester tester;
    protected String label;

    public RunTester(ITester tester, String label) {
        this.tester = tester;
        this.label = label;
    }

    @Override
    public void run() {
        this.tester.runTest(this.label);
    }
}
