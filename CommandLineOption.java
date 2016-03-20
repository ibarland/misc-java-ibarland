/** This file provides the implementation of the function `allOptions`.
 *
 * If callers provide named-arguments on the command-line in any order,
 * then your program can call `allOptions` to get back an array of ALL the option-values
 * (with predefined defaults used for those the caller didn't specify.)
 *
 *  Example: If you want the caller to be able to optionally provide any of the
 *  command-line options `--file`, `--name` and `--size`, then add the following
 *  to your program:
 *
 *    CommandLineOption[] options = {
 *        new CommandLineOption( "file", 'f', null, "the file containing the glubglub" ),
 *        new CommandLineOption( "name", 'n', "ibarland", "the name of the package-author" ),
 *        new CommandLineOption( "size", 's', "45", "the size of the frobzat, in meters." ),
 *        };
 *
 *  The four pieces of info you must provide for each option are:
 *    long-version (`--name`), short-version (`-n`), a default value if not provided ("ibarland"),
 *    and a string which might someday be used in a help-message (but is not currently used).
 *  
 * So if the caller invoked "java CommandLineOptionsExample --size 27 -f stuff.txt`,
 * then `allOPtions` would return the array { "stuff.txt", "ibarland", "27" }.
 * The items in the return-array are the same order as you list them in `options`.
 * 
 * See also: ...there must be other more robust libraries out there.
 *    I'm making this to be parallel to the C-language version I wrote, which 
 *    I wanted to be lighter-weight than the C libraries I found.
 *
 * Known bugs:
 * We assume every option is followed by a value!  (No 'binary' flag-options.)
 * If a value looks like an option, this code will get confused (e.g. if you
 * try to specify a --file whose name is "--size", or if the name of the
 * executable argv[0] is "-n", etc.)!
 */

class CommandLineOption /*extends ObjectIan*/ {

    String longOption;
    char shortOption;
    String defaultValue;
    String helpString;

    //CommandLineOption( Object... args ) { super(args); } // ObjectIan provides boilerplate constructor, equals, hashcode.
    CommandLineOption( String _longOption, char _shortOption, String _defaultValue, String _helpString ) {
        this.longOption = _longOption;
        this.shortOption = _shortOption;
        this.defaultValue = _defaultValue;
        this.helpString = _helpString;
        }
    
    /* Given a string of form "--otherStuff", return the otherStuff.
     * If it wasn't of that form, return null.
     * (The result is a pointer into the provided argument.)
     */
    /*private*/ static String extractLongOptionName( String arg ) {
        if (arg==null || arg.length() <= 2 || !arg.substring(0,2).equals("--")) 
            return null;
        else
            return arg.substring(2);
        }
 
                          
    /* Given a string of form "-someChar", return the char.
     * If it wasn't of that form, return '\0'.
     */
    /*private*/ static char extractShortOptionName( String arg ) {
        if (arg==null || arg.length() != 2 || arg.charAt(0) != '-' || arg.equals("--")) {
            return '\0';
            }
        else {
            return arg.charAt(1);
            }
        }
                           
                           
    /* Search haystack[] for the indicated option, and return the *next* item of haystack as its value.
     * If not located, return the option's default value.
     * If multiple occurrences of the option, we take the last one ('overwriting' previous ones),
     * except that a "--" will halt the option-searching.
     */
    /*private*/ static String findOption( final CommandLineOption target, final String haystack[] ) {
        String answerSoFar = target.defaultValue;
        for (int i=0;  i < haystack.length-1;  ++i) {
            if (haystack[i].equals("--")) break;  /* "--" stops option-processing */
            String asLongOption  = extractLongOptionName( haystack[i]);
            char  asShortOption = extractShortOptionName(haystack[i]);
            if (   (asLongOption  != null  && asLongOption.equals(target.longOption))
                || (asShortOption != '\0' && asShortOption == target.shortOption)) {
                answerSoFar = haystack[i+1];
                ++i;  // skip over haystack[i+1] as a potential next-arg.
                }
            }
        return answerSoFar;
        }


    /* If `arg` looks like it's an option, is it one of the allowed ones in `options`?
     */
    /*private*/ static boolean apparentOptionIsLegal( CommandLineOption[] options, String arg ) {
        String asLongOption  = extractLongOptionName(  arg );
        char  asShortOption = extractShortOptionName( arg );
        if (asLongOption==null && asShortOption=='\0') return true; 
        /* Doesn't look like it's trying to be an option, so no problem. */
    
        for (int i=0;  i< options.length;  ++i) {
            if (asLongOption!=null) {
                if (asLongOption.equals( options[i].longOption )) return true;
                }
            else if (asShortOption!='\0') {
                if (asShortOption==options[i].shortOption) return true;
                }
            else {
                throw new RuntimeException( "Whoopsie, this line should be unreachable!" );
                }
            }
        return false;
        }




    /* Given command-line arguments,
     * return an array with the values for ALL possible options.
     * The strings are taken from the command-line if provided, else from `options[i].default`
     * So if argv = { "--size", "27", "-f", "foo.txt" }
     * and options was an array with the options
     *    { {"file","f","-"}, {"name",'n',"ibarland"}, {"size",'s',null} }
     * then we'd return {"foo.txt", "ibarland", "27"}.
     */
    public static String[] allOptions( String[] argv, CommandLineOption[] options ) {
        String[] allOpts = new String[ options.length ];
        for (int i=0;  i<options.length;  ++i) {
            allOpts[i] = findOption( options[i], argv );
            }

        // Also: make sure that everything that LOOKS like an arg is valid:
        for (int i=0;  i<argv.length;  ++i) {
            if (argv[i].equals("--")) break;
            if (!apparentOptionIsLegal(options, argv[i])) {
                System.err.printf("Warning: argument #%d, \"%s\", is not a known option.\n", i, argv[i]);
                }
            }
  
        return allOpts;
        }
  

    }
