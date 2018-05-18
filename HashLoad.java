import java.io.*;

public class HashLoad {
   
   public static final int HASH_SIZE = 3700000; //Size of hash table 
   public static final int MOD = 3175399; //Mod to use for hashing
   public static final int RECORD_SIZE = 297; //Set size of each record in file
   public static final int BNAME_SIZE = 200; //Set size of the business name field in file
   public static final int HEADSIZE = 18; //To take into account for Business name in front of all the record names
   public static int currPosHeapFile = 0; //Varible to represent the byte location of the start of the record
   public static int insertionCount = 0; //Count of all the insertion
   public static int collisionCount = 0; //Count of all the collisions
   
   // For debugging, couting what record we are up to
   public static int counter = 0;

   // Method to initalize the new hashFile which will be named hash.(#{pageSize}).
   // To represent null all it is filled with -1 for all empty sspaces. 
   // There are HASH_SIZE empty spaces.
   // Will be used later to fill and when filled the -1 is replaced otherwise if not
   // -1 then know that space taken and linear probing done.
   public void initalizeHashFile(int pageSize) throws IOException {
      File hashFile = new File("hash." + pageSize);
      try {
         RandomAccessFile raf = new RandomAccessFile(hashFile, "rw");
         for(int i=0; i<HASH_SIZE; i++) {
            raf.writeInt(-1);
         }
         raf.close();
         System.out.println("File created and filled");
         
      } catch (Exception e) {
         System.out.println(hashFile + " does not exists");
         System.exit(0);
      }
   }

   //Reads the heap file in its page sizes. From the page sizes, interate through the pages
   //and get each business name. Hash that and send to hashFunction for writing into the hash
   //file
   public void hashFile(int pageSize) throws IOException {
      
      File heapFile = new File("heap." + pageSize); 
      File hashFile = new File("hash." + pageSize);
      int recordPerPage = pageSize / RECORD_SIZE; //How much records can be fitted into per page.
      
      boolean isNextPage = true;
      
      try {
         FileInputStream fis = new FileInputStream(heapFile);
         RandomAccessFile hashRaf = new RandomAccessFile(hashFile, "rw");
         
         while(isNextPage) {
            try {
               byte[] bPage = new byte[pageSize];
               //read a page at a time
               fis.read(bPage, 0, pageSize);
               //loop through the amount of records in the 1 page
               for(int i=0; i<recordPerPage; i++) {
                  byte[] record = new byte[RECORD_SIZE];
                  //copy the bytes containing businessname taking into account
                  //the Business name tag in front (hence HEADSIZE).
                  System.arraycopy(bPage, RECORD_SIZE * i + HEADSIZE, record, 0, BNAME_SIZE);
                  //Change the byte attained to string and trim the empty spaces
                  String b_Name = new String(record).trim();
                  
                  //Used for debug - count what record we are up to
                  System.out.println(b_Name);
                  counter++;
                  System.out.println(counter);
                  
                  //Hash the String obtained and make sure it is a positive int
                  int hash_bName = Math.abs(b_Name.hashCode());
                  //If the int is 0 then we know have reached the end of the file and exit loop
                  if(hash_bName == 0) {
                     isNextPage = false;
                  }
                  //Used to obtain the index to use for the hash table
                  int hashIndex = hash_bName % MOD * 4;
                  //Function to write the current pointer in the heapfile to the hash in the
                  //index obtained above
                  hashFunction(i, hashIndex, pageSize, recordPerPage, hashRaf);
               }
            } catch (ArrayIndexOutOfBoundsException e) {
               break;
            }
         }
         
         System.out.println("Finished");
         fis.close();
         hashRaf.close();
         System.out.println(hashFile + " created.\nInsertion: " + insertionCount + "\nCollisions: " + collisionCount);
      } catch (FileNotFoundException e) {
         System.out.println("Could not find " + heapFile);
         System.exit(0);
      }
   }

   //Function to write the current location in the heapfile into the hashIndex supplied from hashing the 
   //business name.
   public void hashFunction(int hashIndex, int pageSize, int recordPerPage, int currentRecord, RandomAccessFile hashRaf) throws FileNotFoundException {
   // To calculate the size of the trailing empty space at the end of the page.
      int emptySpacePerPage = pageSize - (recordPerPage * RECORD_SIZE);
      
      while(true) {
         try {
            //Go to the location of the hashIndex supplied
            hashRaf.seek(hashIndex);
            //Read what is at the position
            int checkPos = hashRaf.readInt();
            //Return seek to the start
            hashRaf.seek(hashIndex);
            
            //if the read returned -1 then it represents the position is empty
            if(checkPos == -1) {
               //write the position of the current location of the read in the heapFile
               hashRaf.writeInt(currPosHeapFile);
               //inserted a record so insertioncount++
               insertionCount++;
               //if the current record (i from the for loop in hashFile()) is the second
               //last one then take into account the empty space that follows after the last record
               //and increment the current location of the read accordingly otherwise just increment 
               //it by the record size.
               if(currentRecord == recordPerPage - 1) {
                  {
                     currPosHeapFile += RECORD_SIZE + emptySpacePerPage;
                  }
               } else {
                  currPosHeapFile += RECORD_SIZE;
               }
               break;
            } 
            //if the read did not return -1 then position is not empty thus increment by 4
            //as as in is 4 bytes thus to go to the position of the next int we must go 4 bytes.
            //increase the collision count
            else {
               collisionCount++;
               hashIndex = hashIndex + 4;
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      
   }

   public static void main(String[] args) {
      
      try {
         String input = args[0];
         int pageSize = Integer.parseInt(input);
         
         HashLoad hash = new HashLoad();
         hash.initalizeHashFile(pageSize);
         hash.hashFile(pageSize);
         
      } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
         System.out.println("Invalid or missing page size");
         System.exit(0);
      } catch (Exception e) {
         e.printStackTrace();
      }
         

   }

}