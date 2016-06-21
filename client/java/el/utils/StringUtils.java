package el.utils;

/**
 * Created by ash on 19.06.16.
 */
public class StringUtils {

    public static String leftPad(String str, int size, String delim) {
        size = (size - str.length()) / delim.length();
        if(size > 0) {
            str = repeat(delim, size) + str;
        }

        return str;
    }

    public static String repeat(String str, int repeat) {
        StringBuffer buffer = new StringBuffer(repeat * str.length());

        for(int i = 0; i < repeat; ++i) {
            buffer.append(str);
        }

        return buffer.toString();
    }

}
