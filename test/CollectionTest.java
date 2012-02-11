/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author raygao2000
 */
import java.util.*;

public class CollectionTest {

    public static void main(String[] args) {
        System.out.println("Collection Example!\n");
        int size;

        // Create a collection  
        Hashtable<String, String> collection = new Hashtable<String, String>();
        String str1 = "Yellow", str2 = "White", str3 = "Green", str4 = "Blue", str5 = "Blue";
        Set<String> keys;
        //Adding data in the collection
        collection.put(str1, str1);
        collection.put(str2, str2);
        collection.put(str3, str3);
        collection.put(str4, str4);
        collection.put(str5, str5);
        System.out.print("Collection data: ");
        //Create a keys
        keys = collection.keySet();
        for (String key : keys) {
            System.out.print(key + " ");
        }
        // Get size of a collection
        size = keys.size();
        if (collection.isEmpty()) {
            System.out.println("Collection is empty");
        } else {
            System.out.println("Collection size: " + size);
        }
        System.out.println();
        // Remove specific data  
        collection.remove(str2);
        System.out.println("After removing [" + str2 + "]\n");
        System.out.print("Now collection data: ");
        keys = collection.keySet();
        for (String key : keys) {
            System.out.print(key + " ");
        }
        System.out.println();
        size = collection.size();
        System.out.println("Collection size: " + size + "\n");
        //Collection empty
        collection.clear();
        size = collection.size();
        if (collection.isEmpty()) {
            System.out.println("Collection is empty");
        } else {
            System.out.println("Collection size: " + size);
        }
    }
}