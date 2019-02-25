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


    static final int DEFAULT_BITS_TOLERANCE = 20;
    // 20bits slack =>  ~32bits precision => within ~4billion => values near 1.0 must be equal to ~9 decimal places

    /** @return whether `a` and `b` are approximately equal.
     *  That is, whether they are the same up to the last `bitsTolerance` bits (default @value{DEFAULT_BITS_TOLERANCE}).
     *  So `bitsTolerance`=0 is exactly-equal (aka `Double.equals`).
     *  There are 52-bits of precision in a double, so `bitsTolerance`=52 always passes.
     *  DISCLAIMER: this function is NOT exhaustively tested, and I'd actually be mildly surprised
     *  if there were NOT weird cases where it fails.
     */
    public static boolean equalsApprox( double a, double b, int bitsTolerance ) { 
        return (a==b)    // hotpath; also handles infinities.
            || Math.abs(a-b)  <  Math.max( Math.ulp(a), Math.ulp(b) ) * (0b1L << bitsTolerance);
        }
    public static boolean equalsApprox( double a, double b ) { return equalsApprox(a,b,DEFAULT_BITS_TOLERANCE); }
    
    /* @Test */
    public static void testEqualsApprox() {
        double x1 = 3.0;
        double x2 = 3.000_000_000_000_001; // within ~1e15 -- close to max precision
        double y  = x1 * 1.4;
        double z  = x1 + 0.000_000_001;

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
        }

    public static void testEqualsApprox2(double x, double y) { testEqualsApprox2(x,y,true); }
    public static void testEqualsApprox2(double x, double y, boolean expectEqual) {
        assert equalsApprox(    x,    y) == expectEqual;
        assert equalsApprox(    y,    x) == expectEqual;
        // If x is nearly y, then is 1/x nearly 1/y (as a relative amount)?
        assert equalsApprox(1.0/x,1.0/y) == expectEqual;//                 
        assert equalsApprox(1.0/y,1.0/x) == expectEqual;//                 
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

  }
