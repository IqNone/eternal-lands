package el.utils;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by ash on 19.06.16.
 */
public class StringUtilsTest {

    @Test
    public void leftPad() {
        assertThat(StringUtils.leftPad("33", 4, "0"), equalTo("0033"));
    }

    @Test
    public void leftPadSmaller() {
        assertThat(StringUtils.leftPad("33", 1, "0"), equalTo("33"));
    }

    @Test
    public void leftPadEqual() {
        assertThat(StringUtils.leftPad("33", 2, "0"), equalTo("33"));
    }

    @Test
    public void leftPadNeg() {
        assertThat(StringUtils.leftPad("33", -1, "0"), equalTo("33"));
    }

    @Test
    public void repeat() {
        assertThat(StringUtils.repeat("3", 2), equalTo("33"));
    }

    @Test(expected = java.lang.NegativeArraySizeException.class)
    public void repeatNeg() {
        StringUtils.repeat("3", -1);
    }

}
