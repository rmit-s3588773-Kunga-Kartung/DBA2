import java.io.*;

public class HashLoad {
   
   static final int HASH_SIZE = 3700000; // HashSize
   static final int HASH_MOD = 3581791; // mod
   static final int RECORD_SIZE = 297;
   static final int BNAME_SIZE = 200;
   static final int OVERHEAD = 18; // to handle the business name in front of each record
   static final int INTSIZE = 4;
   
   static int currentPositionInHeap = 0; // where the current pointer in the heap file is
   static int collisionCounter = 0;
   static int insertionCounter = 0;
   static boolean lastRecordFlag = false; // To flag when its the last record in the 
   static int trailingWhiteSpace; // used to store the trailing white space calculated
   
   // Initialize hash file by printing -1s for all the viable positions in the hash
   // to represent null. Will be checked later when inserting values in that position.
   // cannot be any positive numbers or 0 as on comparison there could be duplicates.
   public void initialiseHashFile(int pageSize) {
      File hashFile = new File("hash." + pageSize);
      try {
         RandomAccessFile raf = new RandomAccessFile(hashFile,"rw");
         int placeHolder = -1;
         for(int i=0; i<HASH_SIZE; i++) {
            raf.writeInt(placeHolder);
         }
         
         raf.close();
      } catch(IOException e) {
         System.out.println(hashFile + " not found.");
      }
      
   }
   
   // Function to read the each page into each record and extract the name portion of
   // the record. Hash that name obtained which will be used as the index to store 
   // the current position of the record in the hash file. 
   public void hashFunction(int pageSize) {
      
      File heapFile = new File("heap." + pageSize);
      File hashFile = new File("hash." + pageSize);
      
      // Used to store the empty space at the end of each page.
      trailingWhiteSpace = pageSize - (13 * RECORD_SIZE);
      
      boolean isNextPage = true;
      boolean isNextRecord = true;
      int counter = 1;
      
      try {
         FileInputStream fis = new FileInputStream(heapFile);
         RandomAccessFile hashRaf = new RandomAccessFile(hashFile, "rw");
         
         while(isNextPage) {
            byte[] page = new byte[pageSize];
            fis.read(page, 0, pageSize);
            
            // Check if the end of the file has been reached.
            // Done so by coverting byte to string of the page.
            // Trim that string and if the length is 0 then we 
            // know we are at the end.
            String pageCheck = new String(page);
            if(pageCheck.trim().length() == 0) {
               isNextPage = false;
               break;
            }
            
            isNextRecord = true;
            while(isNextRecord) {
               for(int i=0; i<page.length; i+=RECORD_SIZE) {
                  
                  // when i = 3564 then we know its the amout of max
                  // record that can be stored in that page and it is the last
                  // record.
                  if(i == 3564) {
                     lastRecordFlag = true;
                  }
                  
                  // when i = 3861 then we know no more records can be in this page
                  // thus it is the end of the page.
                  if(i == 3861) {
                     isNextRecord = false;
                     break;
                  }
                  byte[] bName = new byte[BNAME_SIZE];
                  System.arraycopy(page, i+OVERHEAD, bName, 0, 200);
                  String name = new String(bName).trim();
                  
                  // used to see how many records read
                  System.out.println(counter);
                  counter++; 
                  
                  int hashIndex = Math.abs(name.hashCode()) % HASH_MOD * INTSIZE;
                  
                  hashWrite(hashIndex, pageSize, hashRaf);
               }
               
               
            }
         }
         
         fis.close();
         hashRaf.close();
         
         System.out.println("Completed");
         System.out.println("Insertions: " + insertionCounter);
         System.out.println("Collisions: " + collisionCounter);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   // Function to write the current position in the heap file to the hash file with the
   // calculated hashname which will be the index in the hashfile. 
   public void hashWrite(int hashIndex, int pageSize, RandomAccessFile hashRaf) {
      
      while(true) {
         try {
            hashRaf.seek(hashIndex);
            int checkPosHash = hashRaf.readInt();
            hashRaf.seek(hashIndex);
            
            // If the hashIndex in the hashfile is equal to 0 (which was set) then
            // it is empty and can be written into. 
            // Otherwise increment collision counter and the hashindex by 4 (1 int = 4 bytes) 
            // which is to get to the next 0 in the hashfile.
            if(checkPosHash == -1) {
               hashRaf.writeInt(currentPositionInHeap);
               insertionCounter++;
               
               // If the lastRecordFlag has been changed then it is the last record
               // in that page thus add the trailing white space to increment to the 
               // position of the next record in the new page. 
               // Otherwise just increment by the record size as there are more records
               // in the current read page.
               if(lastRecordFlag == true) {
                  currentPositionInHeap += RECORD_SIZE + trailingWhiteSpace;
                  lastRecordFlag=false;
               } else {
                  currentPositionInHeap += RECORD_SIZE;
               }
               break;
            } else {
               collisionCounter++;
               hashIndex += 4;
               // If reached end of the hash size then go to the beginning and continue from there
               if (hashIndex >= HASH_SIZE * INTSIZE - 1) {
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
      HashLoad hashLoad = new HashLoad();
      try {
         String input = args[0];
         int pageSize = Integer.parseInt(input);
         
         long startTime = System.currentTimeMillis();
         
         hashLoad.initialiseHashFile(pageSize);
         hashLoad.hashFunction(pageSize);
         
         long stopTime = System.currentTimeMillis();
         
         System.out.println(stopTime - startTime + " ms");
         System.out.println((stopTime - startTime) * 0.001 + " sec");
         
      } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
         System.out.println("Invalid pagesize");
      }
   }
}
