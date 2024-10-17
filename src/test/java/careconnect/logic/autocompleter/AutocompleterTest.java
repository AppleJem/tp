package careconnect.logic.autocompleter;

import static careconnect.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import careconnect.logic.autocompleter.exceptions.AutocompleteException;
import careconnect.logic.commands.AddCommand;
import org.junit.jupiter.api.Test;

public class AutocompleterTest {

    @Test
    public void autocompleteWithLexicalPriority_noAvailableOptions_throwsAutocompleteException() {
        Autocompleter autocompleter = new Autocompleter();
        String prefix = "a";
        List<String> list = new ArrayList<>();
        assertThrows(AutocompleteException.class, () -> autocompleter.autocompleteWithLexicalPriority(
                prefix, list));
    }

    @Test
    public void autocompleteWithLexicalPriority_nullValues_throwsNullPointerException() {
        Autocompleter autocompleter = new Autocompleter();
        String prefix = "a";
        List<String> list = new ArrayList<>();
        assertThrows(NullPointerException.class, () -> new Autocompleter().autocompleteWithLexicalPriority(
                null, list));
        assertThrows(NullPointerException.class, () -> new Autocompleter().autocompleteWithLexicalPriority(
                prefix, null));
        assertThrows(NullPointerException.class, () -> new Autocompleter().autocompleteWithLexicalPriority(
                null, null));
    }

    @Test
    public void autocompleteWithLexicalPriority_validValues_correctResults() throws Exception {
        Autocompleter autocompleter = new Autocompleter();
        List<String> list = new ArrayList<>();
        list.add("add");
        list.add("adda");
        assertEquals("add", autocompleter.autocompleteWithLexicalPriority("ad", list));
    }
}
