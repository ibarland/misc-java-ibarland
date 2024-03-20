// part of repo:  https://github.com/ibarland/misc-java-ibarland

import java.util.*;

class UtilsIan {
  //----------------------------------------------------------------
  // static helper functions:
  // Mostly, type-conversion functions with names I can remember.
  // (Include checking for null and good error message, for CS1 students.)
  
  /*
  int debugOutThreshold = 0;
  // methods assert, debugPrint
  static void assertIan( boolean cndn, String msg ) {
      if (!cndn) System.err.println( msg );
    }
  */
  
  
  // todo: cast thiz,that using reflection.  -- no, not needed(?).
  static boolean equals( Object thiz, Object that ) { return thiz.equals(that); }
  // todo: catch nullPointerExceptions
  //public static boolean aString_equals( String a, String b ) { return a.equals(b); }
  static String  intToString( int n ) { return Integer.toString(n); }
  static String  doubleToString( double x ) { return Double.toString(x); }
  static double  intToDouble( int n ) { return (double)n; }
  static int     doubleToInt( double x ) { return (int)Math.round(x); }
  static String  charToString( Character c ) {
      verifyNonNull(c,"charToString","char or Character");
      return Character.toString(c); }
  static char    stringToChar( String s ) {
    verifyNonNull(s, "stringToChar", "String");
    if (s.length() != 1) throw new IllegalArgumentException( "stringToChar: Can only convert strings of length 1; got: \"" + s + "\"." );
    return s.charAt(0);
    }
  
  private static void verifyNonNull( Object obj, String funcName, String expectedType ) {
    if (obj==null) throw new NullPointerException( funcName + ": Was passed null, instead of a " + expectedType + "."
                                                   + "(Did you forget to initialize a variable?)" );
    }


    static final int DEFAULT_BITS_TOLERANCE = 20; // very generous?; 5 is more resonable?
    // 10bits slack =>  ~42bits precision => within ~4billion => values near 1.0 must be equal to ~12 decimal places

    /** @return whether `a` and `b` are approximately equal.
     *  That is, whether they are the same up to the last `bitsTolerance` bits (default @value{DEFAULT_BITS_TOLERANCE}).
     *  This scales nicely with the size of the input, without specifying an absolute tolerance,
     *  and if one argument is 0 then it also works (unlike specifying a relative tolerance).
     *
     *  SURPRISING BEHAVIOR: even if equalsApprox(a,b), it is UNLIKELY that equalsApprox(0,a-b).
     *  That's because of underflow/denormalizing:
     *          around 0, doubles can      distinguish differences of ±1e-300, 
     *  whereas around 1, doubles can only distinguish differences of ±1e-016.
     *  (Hmm: maybe we should use one comparison if max(a,b)>1e-12 say, and only consider Math.ulp below that?)
     *
     *  So `bitsTolerance`= 0  behaves as exactly-equal (aka `Double.equals`).
     *     `bitsTolerance`=64 always passes (since 64b in a double).
     *     `bitsTolerance`=52 passes within a factor of 2  I think (since 52 mantissa-bits);
     *     `bitsTolerance`=53 passes within a factor of 4 (uses 1 bit of the exponent).
     *
     *  DISCLAIMER: this function is NOT exhaustively tested, and I'd actually be mildly surprised
     *  if there were NOT weird cases where it fails.
     */
    public static boolean equalsApprox( double a, double b, int bitsTolerance ) { 
        return  a==b // hotpath; also handles infinities and ±0s (but not NaNs) ?
            || (Math.abs(a-b)  <  Math.min( Math.ulp(a), Math.ulp(b) ) * (0b1L << bitsTolerance))
            || (Double.isNaN(a) && Double.isNaN(b));
        /* other implementations, ranked by least-to-most failed tests. */
        //return  a==b // hotpath; also handles infinities and NaNs.
        //    || Math.abs(Double.doubleToLongBits(a) - Double.doubleToLongBits(b)) < (long)(0b1L << bitsTolerance);
        //return  a==b // hotpath; also handles infinities and NaNs.
        //    || Math.abs(Double.doubleToLongBits(a-b)) < (long)(0b1L << bitsTolerance);
        }
    public static boolean equalsApprox( double a, double b ) { return equalsApprox(a,b,DEFAULT_BITS_TOLERANCE); }
    
    /* @Test */
    public static void testEqualsApprox() {
        double x1 = 3.0;
        double x2 = 3.000_000_000_000_001; // within ~1e15 -- close to max precision
        double y  = x1 * 1.4;
        double z  = x1 + 0.000_000_001;
        double pos0 = 1.0/Double.POSITIVE_INFINITY;
        double neg0 = 1.0/Double.NEGATIVE_INFINITY;

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
        assertTrue(  equalsApprox(  0.0,  0.0, 0 ));
        assertTrue(  equalsApprox(  0.0, neg0, 0 ));
        assertTrue(  equalsApprox( pos0,  0.0, 0 ));
        assertTrue(  equalsApprox( pos0, neg0, 0 ));
        assertTrue(  equalsApprox( +Double.MIN_VALUE, 0, 1 ));
        assertTrue(  equalsApprox( -Double.MIN_VALUE, 0, 1 ));
        assertFalse( equalsApprox( +Double.MIN_VALUE, -Double.MIN_VALUE, 0 ));   // FAILS v1
        assertFalse( equalsApprox( +Double.MIN_VALUE*2, -Double.MIN_VALUE*2, 2 ));   // FAILS v1
        assertTrue(  equalsApprox( +Double.MIN_VALUE*2, -Double.MIN_VALUE*2, 3 ));
        
        assertTrue(  equalsApprox(7.0,  7.0/25.0*25.0) );
        assertFalse( equalsApprox(0.0,  7.0 - (7.0/25.0*25.0)) );  // SURPRISING compared to previous?
        assertFalse( equalsApprox(0.0,  1e-17) );                  // FAILS v1-v3
        assertFalse( equalsApprox(0.0,  1e-87) );                  // FAILS v1-v3
        assertFalse( equalsApprox(0.0,  1e-307) );                 // FAILS v1-v3

        assertTrue(  equalsApprox(1.000000000002e-17,  1e-17) ); 
        assertTrue(  equalsApprox(1.000000000002e-00,  1e-00) );
        assertTrue(  equalsApprox(1.000000000002e+17,  1e+17) );

        assertFalse( equalsApprox(1.000000200002e-17,  1e-17) ); 
        assertFalse( equalsApprox(1.000000200002e-00,  1e-00) ); 
        assertFalse( equalsApprox(1.000000200002e+17,  1e+17) ); 

        assertTrue(  equalsApprox(0.0,  1e-320, 11) ); // passes
        assertTrue(  equalsApprox(0.0,  1e-321, 10) ); // passes
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
        confirmAssertionsEnabled();
        testEqualsApprox();  // test a helper I wrote.

        System.out.println("tests finished.");
      }

    public static void confirmAssertionsEnabled() {
        boolean assertionsEnabled = false;
        try {
            assert( "Just confirming that assertions are enabled." == "Yep, everything is fine." );
            }
        catch (AssertionError e) {
            assertionsEnabled = true;
            }
        if (!assertionsEnabled) {
            System.err.println("Uh-oh, assertions aren't enabled!   -- re-run with `java --enableAssertions UtilsIan`.");
            }
        }

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
        System.out.printf("assert failed: %s %s %s\n", a,  expectEqual?"==":"!=", b);
        }

    public static void logPassedAssertEquals(Object a, Object b, boolean expectEqual) {
        System.out.printf("." );
        }

    public static void assertEqualsApprox(   double a, double b) { assertEqualsApprox(   a,b,true); }
    public static void logFailedAssertEquals(Object a, Object b) { logFailedAssertEquals(a,b,true); }
    public static void logPassedAssertEquals(Object a, Object b) { logPassedAssertEquals(a,b,true); }
  }
