/** An ersatz JUnit.
 * You should use real JUnit instead.
 * However, if you don't want to mess w/ downloading/installing/getting-IDE-to-find JUnit,
 * and just want to place this .java in your project's folder,
 * then these replace `assertEquals` etc are drop-in replacements (mostly).
 *
 * One handy/quick function if using `assert`:
 * `assertAssertionsEnabled` checks whether the jvm is currently IGNORING assertions
 * (which is the default ?!?!), and throws a helpful reminder-message if so.
 */
class IUnit {

    /** If assertions aren't enabled on this jvm run, throw an AssertionError :-)  */
    static boolean assertAssertionsEnabled() {
        assertionsEnabledConfirmed = false;

        try { assert( false ); }
        catch (AssertionError e) { assertionsEnabledConfirmed = true; }

        if (!assertionsEnabledConfirmed)
            throw new AssertionError("Assertions not enabled.  Re-run jvm with `-enableassertions` (or just `-ea`).");

        return assertionsEnabledConfirmed;
        }

    /** cache whether we've confirmed assertsionsEnabled (for efficiency of tests … lol) */
    private static boolean assertionsEnabledConfirmed = false;

        
    /** print "." to indicate/summarize a successful test. */
    static void printResultSummaryChar() { printResultSummaryChar("."); }

    /** print a v.short test summary (probably just ".", indicating success). */
    static void printResultSummaryChar(String summary) {
        if (VERBOSITY >= 3) {
            //if (dotsOnCurrentLine==0) LOG_FILE.printf("test#%3d: ", currTestCount);
            // Add a space if needed, to make groups of 5:
            if (dotsOnCurrentLine%5==0 && dotsOnCurrentLine!=0) LOG_FILE.print(" "); // groups of 5
            LOG_FILE.print(summary);
            ++dotsOnCurrentLine;  // 5 tests per grouping, even if summary is multiple chars.
            if (summary.indexOf('\n') >= 0) dotsOnCurrentLine = 0;
            }
        }

    /** Throw exception if expected ≠ actual.
     * COMMENT THIS METHOD OUT if using real JUnit.
     * @return whether or not the assertion passed.
     * @see org.junit.Assert#assertEquals
     */
    static boolean assertEquals( Object expected, Object actual ) {
        ++currTestCount;

        if (expected.equals(actual)) {
            printResultSummaryChar();
            return true;
            }
        else { // TEST FAILED:
            if (VERBOSITY==0) {
                if (!warned_of_verbosity_0) {
                    LOG_FILE.printf("A test failed!  VERBOSITY=0; no further messages(failures) printed.\n");
                    warned_of_verbosity_0 = true;
                    }
                }
            else {
                printResultSummaryChar("!\n");
                String errMsg = "Assertion failed:\n";
                errMsg += String.format("Expect: %s\nActual: %s\n", expected, actual);

                // Walk the stack to find to get past our own "internal" assert<Something> functions.
                StackTraceElement[] stack = (new Throwable()).getStackTrace();
                int i = 0;
                while (hasPrefix( stack[i].getMethodName(), "assert" )) { ++i; }
                StackTraceElement blame = stack[i];
                
                errMsg += String.format("%s: %s(…): line %s; test# %d\n", 
                                        blame.getFileName(),
                                        blame.getMethodName(),
                                        blame.getLineNumber(),
                                        currTestCount );
                
                LOG_FILE.printf( errMsg );
                // If we want to halt testing:  
                //   throw new AssertionError( errMsg );
                }
            return false;
            }
        }

    /** @see org.junit.Assert#assertTrue */
    static boolean assertTrue(  Object actual ) { return assertEquals(true,  actual); }
    /** @see org.junit.Assert#assertFalse */
    static boolean assertFalse( Object actual ) { return assertEquals(false, actual); }

    /** @see org.junit.Assert#assertThrows */
    static boolean assertThrows( Class expectedExnType, java.util.function.Supplier<?> actual ) {
        Exception actuallyThrown = null;
        try {
            actual.get();  // call the (0-argument) function passed to us; it *should* throw.
            }
        catch (Exception e) {
            actuallyThrown = e;
            }
        return assertTrue( actuallyThrown != null 
                        && actuallyThrown.getClass() == expectedExnType );

        // The param-type `Supplier` inspired by:  https://stackoverflow.com/a/40153253/320830  h/t @x1a0
        //   However, the docs suggest a `void` function can't be a Supplier.
        //   Yet, it actually does compile to pass in `() -> System.out.print("hmm")` (!?)
        //
        // A better sol'n might be:     https://stackoverflow.com/a/29946155/320830  h/t @Matt
        }

        
    /** Does `str` have `pref` as a prefix? */
    static boolean hasPrefix(String str, String pref) {
        int n = pref.length();
        return (str.length() >= n)
            && (pref.equals(str.substring(0,n)));
        }

    /** Does `str` have `suff` as a suffix? */
    static boolean hasSuffix(String str, String suff) {
        int n = str.length() - suff.length();
        return (n >= 0)
            && (suff.equals(str.substring(n)));
        }
    
    /** verbosity level: 
     * 0 suppresses all messages (incl. failed tests!)
     * 1 prints only failed-test messages.
     * 2 prints log-messages
     * 3 prints "." for each test passed.
     */
    static final int VERBOSITY = 3;
    static boolean warned_of_verbosity_0 = false;

    /** How many tests we've started (whether pass or fail). */
    private static int currTestCount = 0;
    /** How many tests we've failed. */
    private static int currFailCount = 0;
    /** How many "."s have been printed on current line (for indicating successful tests). */
    private static int dotsOnCurrentLine = 0;

    /** Where to send test-messages. */
    static final java.io.PrintStream LOG_FILE = System.out;

    /** Print a log-message for testing. */
    public static void printfTestMsg( String fmt, Object... args ) {
        // HACK: don't include fmt's trailing "…" if w're printing "."s for tests!
        if (VERBOSITY >= 3 && hasSuffix(fmt, "…")) { fmt = fmt.substring(0,fmt.length()-1); }

        if (VERBOSITY >= 2) LOG_FILE.printf( fmt, args );
        dotsOnCurrentLine = 0;
        }
    
    /** Print test summary. */
    static void printTestSummary() { printTestSummary(""); }
    /** Print test summary. */
    static void printfTestSummary() { printfTestSummary(""); }
    /** Print test summary, prefixed with `msgStart`. */

    /** Print test summary, prefixed with `msgStart`. */
    static void printTestSummary(String msgStart) { printfTestSummary(msgStart); }

    /** Print test summary, prefixed with `msgStart`. */
    static void printfTestSummary(String msgStart, Object... msgStartDetails) {
        if (currTestCount==0) {
            LOG_FILE.printf("%s\n%s%s.\n",
                            "Um, please write some unit-tests.",
                            "Think of calling small helper functions with small-but-valid inputs",
                            " (perhaps just size 1 or even 0, to start with)"
                            );
            }
        else {
            int currPassCount = (currTestCount - currFailCount);
            int passPercent = 100 * (int)((double)currPassCount / (double)Math.min(1,currTestCount));
            String encouragement = (passPercent==100  ?  "Rock solid."
                                  : passPercent > 90  ?  "So close!"
                                  : passPercent > 75  ?  "Almost there."
                                  : passPercent > 50  ?  "Over halfway there."
                                  : passPercent > 25  ?  "On our way."
                                  : "\nProceed by tracing the code for *simplest* test that's currently failing.");
            LOG_FILE.printf(msgStart, msgStartDetails);
            LOG_FILE.printf("%d/%d tests passed (%d%%); %d failed.  %s\n",
                            currPassCount, currTestCount, passPercent, 
                            currFailCount,
                            encouragement);
            if (currFailCount == 0 && currTestCount < 3) {
                LOG_FILE.printf("   (…though tbf, shouldn't you have more than just %d tests case%s?)\n",
                                currTestCount,
                                currTestCount==1 ? "" : "s" );
                }

            }
        }
    
    }



/**
 * @author ibarland
 * @version 2025-Mar-11
 * 
 * @license CC-BY -- share/adapt this file freely, but include attribution, thx.
 *     https://creativecommons.org/licenses/by/4.0/ 
 *     https://creativecommons.org/licenses/by/4.0/legalcode 
 * Including a link to the *original* file satisifies "appropriate attribution".
 */
