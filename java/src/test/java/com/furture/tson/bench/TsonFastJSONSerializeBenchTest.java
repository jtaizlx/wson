package com.furture.tson.bench;

import com.alibaba.fastjson.JSON;
import com.efurture.tson.Tson;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/5.
 */
public class TsonFastJSONSerializeBenchTest extends TestCase {


    public void testMediaTson() throws IOException {
        benchBuild("/media.json", 10000, true);
    }

    public void testMediaJSON() throws IOException {
        benchBuild("/media.json", 1000, false);
    }

    public void testMedia2Tson() throws IOException {
        benchBuild("/media2.json", 1000, true);
    }

    public void testMedia2JSON() throws IOException {
        benchBuild("/media2.json", 1000, false);
    }

    public void testWeexTson() throws IOException {
        benchBuild("/weex.json", 1000, true);
    }

    public void testWeexJSON() throws IOException {
        benchBuild("/weex.json", 1000, false);
    }


    public void testMiddleTson() throws IOException {
        benchBuild("/middle.json", 1000, true);
    }

    public void testMiddleJSON() throws IOException {
        benchBuild("/middle.json", 1000, false);
    }





    /** 下面两个数据太大,单次存性能对比, 多次就是对比GC了 */
    public void testHomeTson() throws IOException {
        benchBuild("/home.json", 1, true);
    }

    public void testHomeJSON() throws IOException {
        benchBuild("/home.json", 1, false);
    }


    public void testDataTson() throws IOException {
        benchBuild("/data.json", 1, true);
    }

    public void testDataJSON() throws IOException {
        benchBuild("/data.json", 1, false);
    }


    private void benchBuild(String file, int count, boolean tson) throws IOException {
        String data = readFile(file);
        Object map = JSON.parse(data);
        long start = 0;
        long end = 0;

        if(tson) {
            start = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
               Tson.toTson(map);
            }
            end = System.currentTimeMillis();
            System.out.println("TSON toTSON used " + (end - start));
            System.out.println("tson size " + Tson.toTson(map).length);
        }else{
            start = System.currentTimeMillis();
            for(int i=0; i<count; i++) {
                JSON.toJSONString(map);
            }
            end = System.currentTimeMillis();
            System.out.println("FASTJSON toJSON used " + (end - start));
            System.out.println("json size " + JSON.toJSONString(map).getBytes("UTF-8").length);
        }
    }


    private String readFile(String file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream inputStream = this.getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  new String(outputStream.toByteArray());
    }
}
