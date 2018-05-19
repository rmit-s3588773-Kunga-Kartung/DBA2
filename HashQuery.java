import java.io.*;
import java.util.Arrays;

public class HashQuery {

   public static final int HASH_SIZE = 3581791; // used to hash strings in HashLoad.java
   public static final int RECORD_SIZE = 297; // size of each record;

   // Convert the input give in commandline into and array except for the last
   // value of the input given as that will be the pagesize. From the array create a String.
   // This will be the name of the business and hash that name the same way done inHashLoad.java
   // Calls functions searchForInput which searches the heap file.
   public void convertInput(String[] args) throws IOException {
      String[] names = Arrays.copyOfRange(args, 0, args.length - 1);
      String spageSize = args[args.length - 1];
      String name;
      
      // If argument given is only 1 then first element in array is the name
      // otherwise join all the elements with space as a delimiter
      if(names.length == 1) {
         name = names[0];
      } else {
         name = String.join(" ", names);
      }
      
      int pageSize = Integer.parseInt(spageSize);

      File heapFile = new File("heap." + pageSize);
      File hashFile = new File("hash." + pageSize);

      int hashIndex = Math.abs(name.hashCode()) % HASH_SIZE * 4;

      searchForInput(hashIndex, name, heapFile, hashFile);
   }

   // Read the hashFile with the given hashIndex to get value that  points
   // to the index of the record in the heap file.
   // Once value at hashIndex read use that to go to the location of the record in heapFile and
   // check its value. Convert that to a string and check if it matches the name provided
   // If not then it means that either the input was wrong or more likely that the location 
   // of that hash was taken by another value so with linear probing it has been places further
   // down the list. To check we increment by 4 (size of int in bytes) to get to the next int.
   // Compare that and if no match keep looping this process until found. If not found then the input
   // is wrong.
   public void searchForInput(int hashIndex, String name, File heapFile, File hashFile) throws FileNotFoundException {

      RandomAccessFile hashRaf = new RandomAccessFile(hashFile, "r");
      RandomAccessFile heapRaf = new RandomAccessFile(heapFile, "r");

      int positionInHeap;
      boolean flagFound = false;

      while (true) {
         try {
            while (true) {
               hashRaf.seek(hashIndex);
               positionInHeap = hashRaf.readInt();

               if (positionInHeap == -1) {
                  break;
               } else {
                  byte[] record = new byte[RECORD_SIZE];
                  heapRaf.seek(positionInHeap);
                  heapRaf.read(record, 0, RECORD_SIZE);

                  byte[] bname = Arrays.copyOfRange(record, 18, 218);
                  String nameCheck = new String(bname).trim();

                  if (nameCheck.equals(name)) {
                     // Copy bytes from the record into their respective fields.
                     byte[] status = Arrays.copyOfRange(record, 218, 230);
                     byte[] dor = Arrays.copyOfRange(record, 230, 240);
                     byte[] doc = Arrays.copyOfRange(record, 240, 250);
                     byte[] rd = Arrays.copyOfRange(record, 250, 260);
                     byte[] fsn = Arrays.copyOfRange(record, 260, 270);
                     byte[] psor = Arrays.copyOfRange(record, 270, 273);
                     byte[] abn = Arrays.copyOfRange(record, 273, 293);

                     // Convert the byte arrays into Strings.
                     String B_name = new String(bname);
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
                     flagFound = true;
                     break;
                  } else {
                     break;
                  }
               }
            }
            if(flagFound == true) {
               break;
            } else {
               hashIndex += 4;
               // If reached end of the hash size then go to the beginning and continue from there
               if (hashIndex >= HASH_SIZE * 4 - 1) {
                  hashIndex = 0;
               }
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

   }

   // Main Method
   public static void main(String[] args) {
      HashQuery hashQuery = new HashQuery();
      try {
         long startTime = System.currentTimeMillis();
         
         hashQuery.convertInput(args);
         
         long stopTime = System.currentTimeMillis();
         System.out.println(stopTime - startTime + " ms");
      } catch (IOException e) {
         System.out.println("Invalid or missing pageSize");
      }
   }
}
