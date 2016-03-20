class Functional120 {
  static int     stringToInt( String s ) { return Integer.parseInt(s); }
  static double  stringToDouble( String s ) { return Double.parseDouble(s); }

  static boolean equalsIgnoreCase( String a, String b ) { return a.equalsIgnoreCase(b); }
  static int     length( String _this ) { return _this.length(); }
  static String  substring( String _this, int start, int stop ) { return _this.substring(start,stop); }
  static String  substring( String _this, int start ) { return _this.substring(start); }
  static String  toLowerCase( String _this ) { return _this.toLowerCase(); }
  static String  toUpperCase( String self ) { return self.toUpperCase(); }
  static String  intToString( int n ) { return (new Integer(n)).toString(); }
  }
