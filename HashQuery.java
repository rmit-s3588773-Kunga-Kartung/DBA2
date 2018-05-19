import java.io.*;
import java.util.Arrays;

public class HashQuery {

   public static final int MOD = 3700001; // used to hash strings in HashLoad.java

   // Convert the input give in commandline into and array except for the last value of
   // the input given as that will be the pagesize. From the array create a String. This will
   // be the name of the business and hash that name the same way done int HashLoad.java
   public void convertInput(String[] args) throws IOException {
      String[] names = Arrays.copyOfRange(args, 0, args.length - 1);
      String spageSize = args[args.length - 1];

      String text = String.join(" ", names);
      int pageSize = Integer.parseInt(spageSize);

      File heapFile = new File("heap." + pageSize);
      File hashFile = new File("hash." + pageSize);

      int hashText = Math.abs(text.hashCode());
      int hashIndex = hashText % MOD;

      searchForInput(hashIndex, heapFile, hashFile);
   }

   // Read the hashFile with the given hashIndex to get the index of the record in the heap file.
   // Once content at hashIndex read use that to go to the index of the record and print those 
   // results out.
   public void searchForInput(int hashIndex, File heapFile, File hashFile) throws IOException {

      // Read hashfile using the hashIndex and get its contents.
      RandomAccessFile hashRaf = new RandomAccessFile(hashFile, "r");

      hashRaf.seek(hashIndex);
      int positionInHeap = hashRaf.readInt();

      // Use the read input from above to go to the location of the record and print those values.
      RandomAccessFile heapRaf = new RandomAccessFile(heapFile, "r");
      byte[] record = new byte[297];
      heapRaf.seek(positionInHeap);
      heapRaf.read(record, 0, positionInHeap + 297);

      // Copy portions of the byte array related to its corresponding key.
      byte[] name = Arrays.copyOfRange(record, 18, 218);
      byte[] status = Arrays.copyOfRange(record, 218, 230);
      byte[] dor = Arrays.copyOfRange(record, 230, 240);
      byte[] doc = Arrays.copyOfRange(record, 240, 250);
      byte[] rd = Arrays.copyOfRange(record, 250, 260);
      byte[] fsn = Arrays.copyOfRange(record, 260, 270);
      byte[] psor = Arrays.copyOfRange(record, 270, 273);
      byte[] abn = Arrays.copyOfRange(record, 273, 293);

      // Convert the byte arrays into Strings.
      String B_name = new String(name);
      String B_status = new String(status);
      String B_dor = new String(dor);
      String B_doc = new String(doc);
      String B_rd = new String(rd);
      String B_fsn = new String(fsn);
      String B_psor = new String(psor);
      String B_abn = new String(abn);

      // Print all the values.
      System.out.println("Business Name: " + B_name);
      System.out.println("Status: " + B_status);
      System.out.println("Date Of Registration: " + B_dor);
      System.out.println("Date Of Cancellation: " + B_doc);
      System.out.println("Renewal Date: " + B_rd);
      System.out.println("Former State Number: " + B_fsn);
      System.out.println("Previous State of Registration: " + B_psor);
      System.out.println("ABN: " + B_abn);

      hashRaf.close();
      heapRaf.close();

   }

   // Main Method
   public static void main(String[] args) {
      HashQuery hashQuery = new HashQuery();
      try {
         hashQuery.convertInput(args);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
