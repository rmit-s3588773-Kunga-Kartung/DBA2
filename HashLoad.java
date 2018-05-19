import java.io.*;

public class HashLoad {
   
   static final int HASH_SIZE = 53;
   static final int RECORD_SIZE = 297;
   static final int BNAME_SIZE = 200;
   static final int OVERHEAD = 18;
   
   static int currentPosition = 0;
   static int collisionCounter = 0;
   static int insertionCounter = 0;
   
   public void initialiseHashFile(int pageSize) {
      File hashFile = new File("hash." + pageSize);
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
      
      boolean isNextPage = true;
      boolean isNextRecord = true;
      
      try {
         FileInputStream fis = new FileInputStream(heapFile);
         
         while(isNextPage) {
            byte[] page = new byte[pageSize];
            fis.read(page, 0, pageSize);
            
            String pageCheck = new String(page);
            if(pageCheck.trim().length() == 0) {
               isNextPage = false;
            }
            
            isNextRecord = true;
            while(isNextRecord) {
               for(int i=0; i<page.length; i+=297) {
                  byte[] name = new byte[BNAME_SIZE];
                  System.arraycopy(page, i+OVERHEAD, name, 0, 200);
                  String nam = new String(name);
                  if((i == 3861) && (nam.trim().length() == 0)) {
                     System.out.println("here");
                     isNextRecord = false;
                  }
//                  System.arraycopy(page, i+OVERHEAD, name, 0, 200);
//                  String nam = new String(name);
//                  
//                  if(nam.trim().length() == 0) {
//                     isNextRecord = false;
//                  }
                  System.out.println(nam);
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
