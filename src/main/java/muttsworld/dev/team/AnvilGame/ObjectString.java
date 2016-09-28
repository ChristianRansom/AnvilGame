/*
 * AnvilGame is a CraftBukkit plugin created by Jasper Holton
 * Do not redistribute this plugin.
 * 
 * com.wenikalla.AnvilGame
 */

package muttsworld.dev.team.AnvilGame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ObjectString {

    public static String objectToString(Object object) throws Exception 
    {

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(arrayOutputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream);

        objectOutputStream.writeObject(object);

        objectOutputStream.flush();
        gzipOutputStream.close();
        arrayOutputStream.close();
        objectOutputStream.close();
        String objectString = new String(Base64Coder.encode(arrayOutputStream.toByteArray()));

        return objectString;
    }

    public static Object objectFromString(String objectString) throws Exception 
    {

        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(Base64Coder.decode(objectString));
        GZIPInputStream gzipInputStream = new GZIPInputStream(arrayInputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream);

        Object object = objectInputStream.readObject();

        objectInputStream.close();
        gzipInputStream.close();
        arrayInputStream.close();

        return object;
    }
}
