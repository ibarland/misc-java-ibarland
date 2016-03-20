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
  static String  intToString( int n ) { return (new Integer(n)).toString(); }
  static String  doubleToString( double x ) { return (new Double(x)).toString(); }
  static double  intToDouble( int n ) { return (double)n; }
  static int     doubleToInt( double x ) { return (int)Math.round(x); }
  static String  charToString( Character c ) {
      verifyNonNull(c,"charToString","char or Character");
      return new Character(c).toString(); }
  static char    stringToChar( String s ) {
    verifyNonNull(s, "stringToChar", "String");
    if (s.length() != 1) throw new IllegalArgumentException( "stringToChar: Can only convert strings of length 1; got: \"" + s + "\"." );
    return s.charAt(0);
    }
  
  private static void verifyNonNull( Object obj, String funcName, String expectedType ) {
    if (obj==null) throw new NullPointerException( funcName + ": Was passed null, instead of a " + expectedType + "."
                                                   + "(Did you forget to initialize a variable?)" );
    }

  }
