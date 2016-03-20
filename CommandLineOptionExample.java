/* A file to demo the use of command-line-options's `allOptions`.
 * @author Ian Barland
 * @version 2016-mar-20
 */


/* The possible command-line options to a program. 
 */
public class CommandLineOptionExample {

    static CommandLineOption[] options = {
         new CommandLineOption( "file",  'f',  "foo.txt", "the file to blazblarg" )
        ,new CommandLineOption( "name",  'n',  "ibarland", "the primary blazlbarger"  )
        ,new CommandLineOption( "size",  's',  "98", "how many blazzes to blarg (in dozens)" )
        ,new CommandLineOption( "stuff", '\0', null, "what to call your stuff" )
        ,new CommandLineOption( "otherStuff", 'o', "blarg", "the help info for other stuff" )
        };

    /* After declaring the above, you can invoke the program with (say) 
     *   java CommandLineOptionExample --size 44 -f baz.txt
     * and then `allOptions` will return:
     *    { "baz.txt", "ibarland", "44", NULL, "blarg" }
     * Note that these values are in the order that you specify in your array-of-option_info.
     */
  
    public static void main( String[] args ) {
        String[] settings = CommandLineOption.allOptions( args, options );
        // Now, the array `settings` contains all the options, in order:
        // either taken from the command-line, or from the default given in `options[]`.
    
        for (int i=0;  i<settings.length; ++i) {
            System.out.printf("Option #%d (%s) is \"%s\".\n", i, options[i].longOption, settings[i] );
            }

        }
        
    }
