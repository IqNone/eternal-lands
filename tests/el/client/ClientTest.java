package el.client;

import org.junit.Test;

import el.protocol.Message;
import el.utils.ByteUtils;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by ash on 19.06.16.
 */
public class ClientTest {

    @Test
    public void SimpleClient_ReturnsTrue() {
        //assertThat("true", is("true"));
        assertFalse(false);
        String cheese = "d";
        String smelly = "d";
        assertThat(cheese, equalTo(smelly));
    }

    // Gives error 0 and source 00020003

    @Test
    public void onGetActiveChannelsTest() {
        // 4712 0003 03000000 62000000 21020000 22020000
        // should be channel 3, 98, 545 and 546
        // 4712 0003 03000000 62000000 22020000 2b020000
        // should be channel 3, 98, 546 and 555
        // 4712 0000 03000000 62000000 22020000 00000000
        // should be channel 3, 98 and 546
        // 4712 0000 03000000 22020000 00000000 00000000
        // should be channel 3 and 546
        // 4712 0000 22020000 00000000 00000000 00000000
        // should be channel 546 and 88
        // 4712 0001 22020000 78030000 00000000 00000000
        // should be channel 88, 8888888
        // 4712 0002 22020000 78030000 38a28700 00000000

        String hex = "4712000303000000620000002102000022020000";
        System.out.println("bitmap Client");
        Message message = new Message(ByteUtils.hexStringToByteArray(hex));

        // TODO implement
        // Client.onMessageReceived(message);

        assertTrue(true);
    }
}
