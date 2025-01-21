// part of repo:  https://github.com/ibarland/misc-java-ibarland

import java.util.*;

class NearlyEqualsCompareAlgs {
  /*
  int debugOutThreshold = 0;
  // methods assert, debugPrint
  static void assertIan( boolean cndn, String msg ) {
      if (!cndn) System.err.println( msg );
    }
  */
  
  
  // todo: cast thiz,that using reflection.  -- no, not needed(?).
  static boolean equals( Object thiz, Object that ) { return thiz.equals(that); }


    static final int DEFAULT_BITS_TOLERANCE = 20; // very generous?; 5 is more resonable?
    // 10bits slack =>  ~42bits precision => within ~4billion => values near 1.0 must be equal to ~12 decimal places

    /** @return whether `a` and `b` are approximately equal.
     *  That is, whether they are the same up to the last `bitsTolerance` bits (default @value{DEFAULT_BITS_TOLERANCE}).
     *  This scales nicely with the size of the input, without specifying an absolute tolerance,
     *  and if one argument is 0 then it also works (unlike specifying a relative tolerance).
     *
     *  SURPRISING BEHAVIOR: even if equalsApprox(a,b), it is UNLIKELY that equalsApprox(0,a-b).
     *  That's because:
     *          around 0, doubles can      distinguish differences of ±1e-300, 
     *  whereas around 1, doubles can only distinguish differences of ±1e-016.
     *  (Hmm: maybe we should use one comparison if max(a,b)>1e-12 say, and only consider Math.ulp below that?)
     *
     *  So `bitsTolerance`= 0  behaves as exactly-equal (aka `Double.equals`).
     *     `bitsTolerance`=64 always passes (since 64b in a double).
     *     `bitsTolerance`=52 passes within a factor of 2 I think (since 52 mantissa-bits can differ);
     *     `bitsTolerance`=53 passes within a factor of 4 (entire mantissa + 1bit of exponent can differ).
     *     `bitsTolerance`=51 passes within a factor of 0.5 (entire mantissa + 1bit of exponent can differ).
     *     `bitsTolerance`=22 passes within a factor of 1/2^30 (that is, 1e-9), I guess.
     *
     *  DISCLAIMER: this function is NOT exhaustively tested, and I'd actually be mildly surprised
     *  if there were NOT weird cases where it fails.
     *
     *  See unit-tests at   https://github.com/ibarland/misc-java-ibarland/blob/main/UtilsIan.java
     *  @license CC-BY
     */
    public static boolean equalsApprox( double a, double b, int bitsTolerance ) { 
        return equalsApprox_v1(a,b,bitsTolerance);
        }
    public static boolean equalsApprox( double a, double b ) { return equalsApprox(a,b,DEFAULT_BITS_TOLERANCE); }

    /** UPSHOT OF COMPARING ALGS:
     * v1,v2 only report two errors, on my test suite.
     * v3 reports a lot.
     * v4 reports six.
     * CAVEAT: my test suite is not necessarily any good!
     *	 
     * v1,v2,v3 are all very similar -- in fact I repeated the nearly-same lines, just commenting out a copy of each.
     */





    public static boolean equalsApprox_v1( double a, double b, int bitsTolerance ) { 
        return  a==b // hotpath; also handles infinities and ±0s (but not NaNs) ?
            || Math.abs(a-b)  <  (Math.min(Math.ulp(a), Math.ulp(b)) * (0b1L << bitsTolerance))
        /* other implementations, ranked by least-to-most failed tests (keep the a==b and NaN above, though): */
        //  || Math.abs(Double.doubleToLongBits(a) - Double.doubleToLongBits(b)) < (0b1L << bitsTolerance)
        //  || Math.abs(Double.doubleToLongBits(a-b)) < (0b1L << bitsTolerance)
            || (Double.isNaN(a) && Double.isNaN(b))
            ;
        }
    public static boolean equalsApprox_v1( double a, double b ) { return equalsApprox_v1(a,b,DEFAULT_BITS_TOLERANCE); }
    public static boolean equalsApprox_v2( double a, double b, int bitsTolerance ) { 
        return  a==b // hotpath; also handles infinities and ±0s (but not NaNs) ?
        //   || Math.abs(a-b)  <  (Math.min(Math.ulp(a), Math.ulp(b)) * (0b1L << bitsTolerance))
        /* other implementations, ranked by least-to-most failed tests (keep the a==b and NaN above, though): */
            || Math.abs(Double.doubleToLongBits(a) - Double.doubleToLongBits(b)) < (0b1L << bitsTolerance)
        //  || Math.abs(Double.doubleToLongBits(a-b)) < (0b1L << bitsTolerance)
            || (Double.isNaN(a) && Double.isNaN(b))
            ;
        }
    public static boolean equalsApprox_v2( double a, double b ) { return equalsApprox_v2(a,b,DEFAULT_BITS_TOLERANCE); }
    public static boolean equalsApprox_v3( double a, double b, int bitsTolerance ) { 
        return  a==b // hotpath; also handles infinities and ±0s (but not NaNs) ?
        //  || Math.abs(a-b)  <  (Math.min(Math.ulp(a), Math.ulp(b)) * (0b1L << bitsTolerance))
        /* other implementations, ranked by least-to-most failed tests (keep the a==b and NaN above, though): */
        //  || Math.abs(Double.doubleToLongBits(a) - Double.doubleToLongBits(b)) < (0b1L << bitsTolerance)
            || Math.abs(Double.doubleToLongBits(a-b)) < (0b1L << bitsTolerance)
            || (Double.isNaN(a) && Double.isNaN(b))
            ;
        }
    public static boolean equalsApprox_v3( double a, double b ) { return equalsApprox_v3(a,b,DEFAULT_BITS_TOLERANCE); }
    public static boolean equalsApprox_v4( double a, double b, int bitsTolerance ) { 
        if (a==b || (Double.isNaN(a) && Double.isNaN(b))) return true;
        if (Math.signum(a)==Math.signum(b)) {
            long MASK = Long.MAX_VALUE << bitsTolerance;
            return (Double.doubleToLongBits(a)&MASK) == (Double.doubleToLongBits(b)&MASK);
            }
        else { // opposite signs: they're only close if both are 0, up to bitsTolerance-1
            long MASK = Long.MAX_VALUE << (bitsTolerance-1);
            return (Double.doubleToLongBits(Math.abs(a))&MASK) == 0
                && (Double.doubleToLongBits(Math.abs(b))&MASK) == 0;
            }
        }
    public static boolean equalsApprox_v4( double a, double b ) { return equalsApprox_v4(a,b,DEFAULT_BITS_TOLERANCE); }
    
    // @Test
    public static void testEqualsApprox() {
        log("A few basic tests\n");
        assertTrue(  equalsApprox( 1.23, 1.23, 0 ));
        assertTrue(  equalsApprox( 1.23, 1.23, 1 ));
        assertTrue(  equalsApprox( 1.23, 1.23, 25 ));
        assertFalse( equalsApprox( 1.23, 1.24, 25 ));
        assertTrue(  equalsApprox( 1.23, 1.23000001, 32 ));
        assertTrue(  equalsApprox( 1.23, 1.23000011, 32 ));
        assertFalse( equalsApprox( 1.23, 1.23000111, 32 ));

        double x1 = 3.0;
        double x2 = 3.000_000_000_000_001; // within ~1e15 -- close to max precision
        double y  = x1 * 1.4;
        double z  = x1 + 0.000_000_001;
        double pos0 = 1.0/Double.POSITIVE_INFINITY;
        double neg0 = 1.0/Double.NEGATIVE_INFINITY;

        log("Check various scales\n");
        /* DISCLAIMER: this testing is not as thorough as it should be, but I'm too lazy to
         * think about IEEE double format to look for odd corner-cases.
         */
        for ( double scale : List.of( 1e0, 1e1, 1e2, 1e3, 1e8, 1e15, 1e100, 1e307 ) ) {
            testEqualsApprox2( x1*scale, x1*scale );
            testEqualsApprox2( x1*scale, x2*scale );

            testEqualsApprox2( x1*scale, y *scale, false );
            testEqualsApprox2( x1*scale, z *scale, false );
            testEqualsApprox2( x2*scale, y *scale, false );
            testEqualsApprox2( x2*scale, z *scale, false );
            testEqualsApprox2( y *scale, z *scale, false );
            }

        log("pos/neg zeroes, and near-zeroes\n");
        assertTrue(  equalsApprox(  0.0,  0.0, 0 ));
        assertTrue(  equalsApprox(  0.0, neg0, 0 ));
        assertTrue(  equalsApprox( pos0,  0.0, 0 ));
        assertTrue(  equalsApprox( pos0, neg0, 0 ));

        assertTrue(  equalsApprox( +Double.MIN_VALUE, 0, 1 ));
        assertTrue(  equalsApprox( -Double.MIN_VALUE, 0, 1 ));
        assertFalse( equalsApprox( +Double.MIN_VALUE  , -Double.MIN_VALUE  , 0 ));   // FAILS v1
        assertTrue(  equalsApprox( +Double.MIN_VALUE  , -Double.MIN_VALUE  , 1 ));   // FAILS v1
        assertTrue( equalsApprox( +Double.MIN_VALUE*2, -Double.MIN_VALUE*2, 2 ));   // FAILS v1
        assertTrue(  equalsApprox( +Double.MIN_VALUE*2, -Double.MIN_VALUE*2, 3 ));

        assertTrue(  equalsApprox(0.0,  1e-320, 11) ); // passes
        assertTrue(  equalsApprox(0.0,  1e-321, 10) ); // passes
        

        log("using default precision\n");
        assertTrue(  equalsApprox(7.0,  7.0/25.0*25.0) );
        assertFalse( equalsApprox(0.0,  7.0 - (7.0/25.0*25.0)) );  // SURPRISING compared to previous?
        assertFalse( equalsApprox(0.0,  1e-17) );                  // FAILS v1-v3
        assertFalse( equalsApprox(0.0,  1e-87) );                  // FAILS v1-v3
        assertFalse( equalsApprox(0.0,  1e-307) );                 // FAILS v1-v3

        log("using default precision, near 1.0\n");
        assertTrue(  equalsApprox(1.000000000002e-17,  1e-17) ); 
        assertTrue(  equalsApprox(1.000000000002e-00,  1e-00) );
        assertTrue(  equalsApprox(1.000000000002e+17,  1e+17) );

        assertFalse( equalsApprox(1.000000200002e-17,  1e-17) ); 
        assertFalse( equalsApprox(1.000000200002e-00,  1e-00) ); 
        assertFalse( equalsApprox(1.000000200002e+17,  1e+17) ); 

        }

    /** test equalsApprox(x,y) as well as (y,x) and (1/x,1/y) and (1/y,1/x). */
    public static void testEqualsApprox2(double x, double y) { testEqualsApprox2(x,y,true); }
    public static void testEqualsApprox2(double x, double y, boolean expectEqual) {
        assertEqualsApprox(    x,    y, expectEqual);
        assertEqualsApprox(    y,    x, expectEqual);
        // If x is nearly y, then is 1/x nearly 1/y (as a relative amount)?
        assertEqualsApprox(1.0/x,1.0/y, expectEqual);
        assertEqualsApprox(1.0/y,1.0/x, expectEqual);
        }

    /** Just run our unit tests. Be sure to run `java` with `--assertionsEnabled` (or, `-ea`).
     */
    public static void main( /* @NonNull */ String... __ ) {
        assertAssertionsEnabled();
        testEqualsApprox();  // test a helper I wrote.

        System.out.println("tests finished.");
      }

    /** Make sure that assertions are enable (else we throw an exception).
     */
    public static void assertAssertionsEnabled() {
        boolean assertionsDefinitelyEnabled = false;
        try {
            assert( "Just confirming that assertions are enabled." == "Yep, everything is fine." );
            }
        catch (AssertionError e) {
            assertionsDefinitelyEnabled = true;
            }
        if (!assertionsDefinitelyEnabled) {
            System.err.println("Uh-oh, assertions aren't enabled!\nRe-run with `java -enableassertions`.");
            }
        }

    /** Make sure that assertions are enable (else we throw an exception).
     */
    public static void assertEquals( Object a, Object b ) {
	if (!(a.equals(b))) {
            logFailedAssertEquals(a,b);
            }
        else {
            logPassedAssertEquals(a,b);
            }
        }

    public static void assertTrue(  boolean a ) { assertEquals(a,true ); }
    public static void assertFalse( boolean a ) { assertEquals(a,false); }
    
    /* Like assertEquals, but call nearlyEquals. */
    public static void assertEqualsApprox( double a, double b, boolean expectEqual ) {
        if (equalsApprox(a,b) != expectEqual) {
            logFailedAssertEquals(a,b,expectEqual);
            }
        else {
            logPassedAssertEquals(a,b,expectEqual);
            }
        }
    public static void logFailedAssertEquals(Object a, Object b, boolean expectEqual) {
        log("assert failed: %s %s %s\n", a,  expectEqual?"==":"!=", b);
        }

    public static void logPassedAssertEquals(Object a, Object b, boolean expectEqual) {
        log(".");
        }
    public static void log(String fmt, Object... vals) {
        System.out.printf(fmt, vals);
        }

    public static void assertEqualsApprox(   double a, double b) { assertEqualsApprox(   a,b,true); }
    public static void logFailedAssertEquals(Object a, Object b) { logFailedAssertEquals(a,b,true); }
    public static void logPassedAssertEquals(Object a, Object b) { logPassedAssertEquals(a,b,true); }
  }

/*
@author ibarland
@version 2024-Mar-20

@license CC-BY -- share/adapt this file freely, but include attribution, thx.
    https://creativecommons.org/licenses/by/4.0/ 
    https://creativecommons.org/licenses/by/4.0/legalcode 
Including a link to the github repo satisifies "appropriate attribution":
    https://github.com/ibarland/misc-java-ibarland/blob/main/UtilsIan.java

*/
