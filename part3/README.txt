Part 1 DERBY:
-------------

structure:
----------
BN_NAMES(_ID_, REGISTER_NAME, BN_NAME)
BN_REGISTRATIONS(ID*, BN_STATE_NUM, BN_STATE_OF_REG*, BN_ABN)
BN_STATES(_BN_STATE_OF_REG_, STATE)
BN_STATUS(ID*, BN_STATUS_ID*, BN_REG_DT, BN_CANCEL_DT, BN_RENEW_DT)
BN_STATUS_OPTIONS(_BN_STATUS_ID_, BN_STATUS)

steps:
------
bash part1.sh BUSINESS_NAMES_201803.csv

====================================================================

Part 2 MONGODB:
---------------

structure:
----------
{
        "_id" : ObjectId("5ad0c1d5b1fae3122bfbf540"),
        "REGISTER_NAME" : "BUSINESS NAMES",
        "BN_NAME" : "Warby Wares",
        "BN_STATUS" : "Deregistered",
        "Date" : {
                "BN_REG_DT" : "31/05/2015",
                "BN_CANCEL_DT" : "12/10/2017",
                "BN_RENEW_DT" : "31/05/2017"
        },
        "BN_STATE_NUM" : "",
        "BN_STATE_OF_REG" : "",
        "BN_ABN" : NumberLong("48697696446")
}

steps:
------
bash ./part2.sh BUSINESS_NAMES_201803.csv

====================================================================

Part 3 HEAP:
------------

structure:
----------
FIXED size fields:
   total RECORD_SIZE = 297

   RID_SIZE = 4
   REGISTER_NAME_SIZE = 14
   BN_NAME_SIZE = 200
   BN_STATUS_SIZE = 12
   BN_REG_DT_SIZE = 10
   BN_CANCEL_DT_SIZE = 10
   BN_RENEW_DT_SIZE = 10
   BN_STATE_NUM_SIZE = 10
   BN_STATE_OF_REG_SIZE = 3
   BN_ABN_SIZE = 20
   EOF_PAGENUM_SIZE = 4


steps:
------

bash ./part3.sh BUSINESS_NAMES_201803.csv

This will:   
remove header (first line in csv file)

tail -n +2 BUSINESS_NAMES_201803.csv > BUSINESS_NAMES_201803.csv.nohead

then compile:

javac *.java

then create heap file (eg 4096 byte pages):

java dbload -p 4096 BUSINESS_NAMES_201803.csv.nohead

then query:

java dbquery "mf engineering" 4096
