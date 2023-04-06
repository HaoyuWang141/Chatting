package StreamTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class InputStreamReaderTest {

    /*
     * 得到字符流前需先有字节流
     */
    private String getStream(String url){
        try {
            //得到字节流
            InputStream in = new URL(url).openStream();
            //将字节流转化成字符流，并指定字符集
            InputStreamReader isr = new InputStreamReader(in,"UTF-8");
            String results = "";
            int tmp;
            while((tmp = isr.read()) != -1){
                results += (char)tmp;
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
        InputStreamReaderTest test = new InputStreamReaderTest();
        System.out.println(test.getStream(URL));
    }

}
