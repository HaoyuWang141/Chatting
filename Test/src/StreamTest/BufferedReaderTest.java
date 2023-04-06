package StreamTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class BufferedReaderTest {

    /*
     * 字节流——字符流——缓存输出的字符流
     */
    private String getStream(String url) {
        try {
            //得到字节流
            InputStream in = new URL(url).openStream();
            //将字节流转化成字符流，并指定字符集
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            //将字符流以缓存的形式一行一行输出
            BufferedReader bf = new BufferedReader(isr);
            String results = "";
            String newLine = "";
            while ((newLine = bf.readLine()) != null) {
                results += newLine + "\n";
            }
            return results;

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String URL = "http://www.baidu.com";
        BufferedReaderTest test = new BufferedReaderTest();
        System.out.println(test.getStream(URL));
    }

}
