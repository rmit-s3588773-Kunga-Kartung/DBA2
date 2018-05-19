import java.io.*;

public class HashLoad {
   
   static final int HASH_SIZE = 53;
   static final int RECORD_SIZE = 297;
   static final int BNAME_SIZE = 200;
   static final int OVERHEAD = 18;
   
   static int currentPositionInHeap = 0;
   static int collisionCounter = 0;
   static int insertionCounter = 0;
   static boolean lastRecordFlag = false;
   static int trailingWhiteSpace;
   
   public void initialiseHashFile(int pageSize) {
      File hashFile = new File("testhash." + pageSize);
      try {
         RandomAccessFile raf = new RandomAccessFile(hashFile,"rw");
         
         for(int i=0; i<HASH_SIZE; i++) {
            raf.writeInt(0);
         }
      } catch(IOException e) {
         System.out.println(hashFile + " not found.");
      }
   }
   
   public void hashFunction(int pageSize) {
      
      File heapFile = new File("test." + pageSize);
      File hashFile = new File("testhash." + pageSize);
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
            
            String pageCheck = new String(page);
            if(pageCheck.trim().length() == 0) {
               System.out.println("Should break page");
               isNextPage = false;
               break;
            }
            
            isNextRecord = true;
            while(isNextRecord) {
               for(int i=0; i<page.length; i+=RECORD_SIZE) {
                  if(i == 3564) { //lastrecord
                     lastRecordFlag = true;
                  }
                  if(i == 3861) {
                     System.out.println("Last record reached in current page");
                     isNextRecord = false;
                     break;
                  }
                  
                  byte[] bName = new byte[BNAME_SIZE];
                  System.arraycopy(page, i+OVERHEAD, bName, 0, 200);

                  String name = new String(bName).trim();
                  
                  System.out.println(counter + " " + name);
                  counter++;
                  
                  int hash_Name = Math.abs(name.hashCode());
                  
                  int hashIndex = hash_Name % HASH_SIZE;
                  
                  hashWrite(hashIndex, pageSize, hashRaf);
                  
               }
            }
         }
      } catch (FileNotFoundException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public void hashWrite(int hashIndex, int pageSize, RandomAccessFile hashRaf) {
      
      while(true) {
         try {
            hashRaf.seek(hashIndex);
            int checkPosHash = hashRaf.read();
            hashRaf.seek(hashIndex);
            
            if(checkPosHash == 0) {
               hashRaf.write(currentPositionInHeap);
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
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   public static void main(String[] args) {
      
      HashLoad hashLoad = new HashLoad();
   
      try {
         String input = args[0];
         int pageSize = Integer.parseInt(input);
         
         hashLoad.initialiseHashFile(pageSize);
         hashLoad.hashFunction(pageSize);
         
      } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
         System.out.println("Invalid pagesize");
      }
   }

}
