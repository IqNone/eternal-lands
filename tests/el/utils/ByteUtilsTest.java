package el.utils;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by ash on 19.06.16.
 */
public class ByteUtilsTest {


    @Test
    public void toHexTestLongLeadingZero() {
        String hex = "000a0f000a001b00010903007f0000011f400901000e0100";
        byte[] bytes = new byte[]{0x00, 0x0a, 0x0f, 0x00, 0x0a, 0x00, 0x1b,
                0x00, 0x01, 0x09, 0x03, 0x00, 0x7f, 0x00, 0x00, 0x01, 0x1f, 0x40,
                0x09, 0x01, 0x00, 0x0e, 0x01, 0x00};

        String hexCalc = ByteUtils.byteArrayToHexString(bytes);
        assertThat(hexCalc, equalTo(hex));
    }

    @Test
    public void toHexTestLeadingZero() {
        String hex = "0a0f000a001b00010903007f0000011f400901000e0100";
        byte[] bytes = new byte[]{0x0a, 0x0f, 0x00, 0x0a, 0x00, 0x1b,
                0x00, 0x01, 0x09, 0x03, 0x00, 0x7f, 0x00, 0x00, 0x01, 0x1f, 0x40,
                0x09, 0x01, 0x00, 0x0e, 0x01, 0x00};

        String hexCalc = ByteUtils.byteArrayToHexString(bytes);
        assertThat(hexCalc, equalTo(hex));
    }

    @Test
    public void toHexTest() {
        String hex = "1a0f000a001b00010903007f0000011f400901000e0100";
        byte[] bytes = new byte[]{0x1a, 0x0f, 0x00, 0x0a, 0x00, 0x1b,
                0x00, 0x01, 0x09, 0x03, 0x00, 0x7f, 0x00, 0x00, 0x01, 0x1f, 0x40,
                0x09, 0x01, 0x00, 0x0e, 0x01, 0x00};

        String hexCalc = ByteUtils.byteArrayToHexString(bytes);
        assertThat(hexCalc, equalTo(hex));
    }

    @Test
    public void toByteTestLongLeadingZero() {
        String hex = "000a0f000a001b00010903007f0000011f400901000e0100";
        byte[] bytes = new byte[]{0x00, 0x0a, 0x0f, 0x00, 0x0a, 0x00, 0x1b,
                0x00, 0x01, 0x09, 0x03, 0x00, 0x7f, 0x00, 0x00, 0x01, 0x1f, 0x40,
                0x09, 0x01, 0x00, 0x0e, 0x01, 0x00};

        byte[] bytesCalc = ByteUtils.hexStringToByteArray(hex);
        assertThat(bytes, equalTo(bytesCalc));
    }

    @Test
    public void toByteTestLeadingZero1() {
        String hex = "0a0f000a001b00010903007f0000011f400901000e0100";
        byte[] bytes = new byte[]{0x0a, 0x0f, 0x00, 0x0a, 0x00, 0x1b,
                0x00, 0x01, 0x09, 0x03, 0x00, 0x7f, 0x00, 0x00, 0x01, 0x1f, 0x40,
                0x09, 0x01, 0x00, 0x0e, 0x01, 0x00};

        byte[] bytesCalc = ByteUtils.hexStringToByteArray(hex);
        assertThat(bytes, equalTo(bytesCalc));
    }

    @Test
    public void toByteTestLeadingZero2() {
        String hex = "a0f000a001b00010903007f0000011f400901000e0100";
        byte[] bytes = new byte[]{0x0a, 0x0f, 0x00, 0x0a, 0x00, 0x1b,
                0x00, 0x01, 0x09, 0x03, 0x00, 0x7f, 0x00, 0x00, 0x01, 0x1f, 0x40,
                0x09, 0x01, 0x00, 0x0e, 0x01, 0x00};

        byte[] bytesCalc = ByteUtils.hexStringToByteArray(hex);
        assertThat(bytes, equalTo(bytesCalc));
    }


    @Test
    public void toByteTest() {
        String hex = "1a0f000a001b00010903007f0000011f400901000e0100";
        byte[] bytes = new byte[]{0x1a, 0x0f, 0x00, 0x0a, 0x00, 0x1b,
                0x00, 0x01, 0x09, 0x03, 0x00, 0x7f, 0x00, 0x00, 0x01, 0x1f, 0x40,
                0x09, 0x01, 0x00, 0x0e, 0x01, 0x00};

        byte[] bytesCalc = ByteUtils.hexStringToByteArray(hex);
        assertThat(bytes, equalTo(bytesCalc));
    }





}
