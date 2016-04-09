import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommandLineOptionTest {

    @Test
    public void testExtractLongOptionName(){
        assertEquals( "hello", CommandLineOption.extractLongOptionName("--hello"));
        assertEquals( null, CommandLineOption.extractLongOptionName("noLeadingDashes"));
        assertEquals( null, CommandLineOption.extractLongOptionName("-hello"));
        assertEquals( null, CommandLineOption.extractLongOptionName("-h"));
        assertEquals( null, CommandLineOption.extractLongOptionName("--"));
        assertEquals( null, CommandLineOption.extractLongOptionName(""));
        assertEquals( null, CommandLineOption.extractLongOptionName(null));
        }

    @Test
    public void testExtractShortOptionName(){
        assertEquals( new Character('h'), CommandLineOption.extractShortOptionName("-h"));
        assertEquals( null, CommandLineOption.extractShortOptionName("h"));
        assertEquals( null, CommandLineOption.extractShortOptionName("hello"));
        assertEquals( null, CommandLineOption.extractShortOptionName("--hello"));
        assertEquals( null, CommandLineOption.extractShortOptionName("h-"));
        assertEquals( null, CommandLineOption.extractShortOptionName("-"));
        assertEquals( null, CommandLineOption.extractShortOptionName("--"));
        assertEquals( null, CommandLineOption.extractShortOptionName(""));
        assertEquals( null, CommandLineOption.extractShortOptionName(null));
        }



    @Test
    public void testfindOption() {
        String[] sample1 = { "--hello","tag", "-b","99", "--", "--hello", "tag2" } ;
        String[] sample2 = { "--hello","tag", "-b","99", "--hello", "tag2" } ;
        CommandLineOption[] options = {
            new CommandLineOption( "hello", 'h', "ibarland", "the name of the package-author" ),
            new CommandLineOption( "bye", 'b', "99", "the size of the frobzat, in meters." ),
            };
        assertEquals( "tag", CommandLineOption.findOption( options[0], sample1 ));
        assertEquals( "99", CommandLineOption.findOption( options[1], sample1 ));
        assertEquals( "tag2", CommandLineOption.findOption( options[0], sample2 ));
        }


    @Test
    public void testApparentOptionIsLegal() {
        CommandLineOption[] options = {
            new CommandLineOption( "name", 'n', "ibarland", "the name of the package-author" ),
            new CommandLineOption( "size", 's', "45", "the size of the frobzat, in meters." ),
            };
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "--name" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "--size" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "-s" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "-n" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "blah" ));
        assertEquals(  false, CommandLineOption.apparentOptionIsLegal( options, "--zasd" ));
        assertEquals(  false, CommandLineOption.apparentOptionIsLegal( options, "-z" ));
        assertEquals(  true, CommandLineOption.apparentOptionIsLegal( options, "--" ));
        }



    }
