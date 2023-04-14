package Emoji;


import com.vdurmont.emoji.EmojiParser;
import org.json.JSONObject;

public class emojiTest {

    public static void test4() {
        String str = "An :grinning:awesome :smiley:string &#128516;with a few :wink:emojis!";
        String result = EmojiParser.parseToUnicode(str);
        System.out.println("转换结果：" + result);
    }

    public static void test5(){
        String str = "An 😀awesome 😃string with a few 😉emojis!";
        String result = EmojiParser.parseToAliases(str);
        System.out.println("转换结果："+result);
    }

    public static void main(String[] args) {
        test4();
        test5();
    }
}
