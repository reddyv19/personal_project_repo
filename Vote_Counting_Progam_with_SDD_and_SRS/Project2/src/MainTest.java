import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MainTest {
    @Test
    public void testOpenFile(){
        assertNull(Main.openFile("DoesntExist"));

        assertNull(Main.openFile("Doesn'tExist.csv"));

        assertNull(Main.openFile("csv"));

        assertNotEquals(Main.openFile("testing/testCSVs/smallIR.csv"),null);

    }
}
