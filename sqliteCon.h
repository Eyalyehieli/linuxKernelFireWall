#ifndef SQLITECON_H_INCLUDED
#define SQLITECON_H_INCLUDED
#include <stdio.h>
#include <stdlib.h>
#include <sqlite3.h>
#include <search.h>
#include "myStruct.h"
#include "list.h"
#include "protocol.h"
//#include "hashT.h"



//double atod(const char* s);
sqlite3* getDBInstance();
//hashtable_t* initializer_hash_from_db();
void readProtocolsFromDB(list** list_of_protocols);
//void getRange(protocol* protocol_to_add,sqlite3_stmt* stmt);


#endif // SQLITECON_H_INCLUDED
