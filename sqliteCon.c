
#include "sqliteCon.h"
#include "protocol.h"



sqlite3* getDBInstance()
{
    static sqlite3 *db=NULL;
    if(db==NULL)
    {
        sqlite3_open("/home/eyalyehieli/Desktop/EyalJavaProgram/packetsNetFilterDB/netFilterDB.sqlite",&db);
    }
    return db;
}

/*hashtable_t* initializer_hash_from_db(hashtable_t *hash)
{
    sqlite3 *db;
    sqlite3_stmt *stmt;
    int* initial_data=(int*)malloc(sizeof(int));
    char *query="SELECT st.structName, COUNT(*) FROM StructFields st INNER JOIN applications app ON st.id=app.StructFieldsId INNER JOIN ConnectionToApplicationByPortAndIp conTAppBPortAIp on conTAppBPortAIp.id=app.ConnectionToApplicationByPortAndIpId Group by st.structName";
    int rc,count;
    db=getDBInstance();
    rc=sqlite3_prepare_v2(db,query,-1,&stmt,NULL);
    count=0;
    *initial_data=0;
    if (rc != SQLITE_OK)
    {
        fprintf(stderr, "Failed to prepare SQL: %s\n", sqlite3_errmsg(db));
    }

    while (sqlite3_step(stmt) != SQLITE_DONE)
    {
        ht_put(hash,(char*)sqlite3_column_text(stmt,0),*initial_data);
        printf("data from db %s \n",sqlite3_column_text(stmt,0));
        printf("struct name %s\n",ht_get(hash,(char*)sqlite3_column_text(stmt,0)));
    }
    sqlite3_finalize(stmt);
    return hash;
}*/


void readProtocolsFromDB(list** list_of_protocols){
    sqlite3 *db;
    sqlite3_stmt *stmt;
    int rc;
    char* query="SELECT type,minRange,maxRange,serialNumber,structName,applicationName,ip,port FROM StructFields st INNER JOIN applications app ON st.id=app.StructFieldsId INNER JOIN ConnectionToApplicationByPortAndIp conTAppBPortAIp on conTAppBPortAIp.id=app.ConnectionToApplicationByPortAndIpId";
    protocol* protocol_to_add;
    *list_of_protocols=list_create();
    //sqlite3_open("/home/eyalyehieli/Desktop/EyalJavaProgram/packetsNetFilterDB/netFilterDB.sqlite",&db);
    db=getDBInstance();
    if(db==NULL){printf("failed to open db");}
    rc=sqlite3_prepare_v2(db, query, -1, &stmt, NULL);

    if (rc != SQLITE_OK)
    {
        fprintf(stderr, "Failed to prepare SQL: %s\n", sqlite3_errmsg(db));
    }

    while (sqlite3_step(stmt) != SQLITE_DONE)
    {

        protocol_to_add=(protocol*)malloc(sizeof(protocol));
        setType(protocol_to_add,(char*)sqlite3_column_text(stmt,0));
        setRange(protocol_to_add,(char*)sqlite3_column_text(stmt,1),(char*)sqlite3_column_text(stmt,2));
        setSerialNumber(protocol_to_add,sqlite3_column_int(stmt,3));
        setStructName(protocol_to_add,(char*)sqlite3_column_text(stmt,4));
        setApplication(protocol_to_add,(char*)sqlite3_column_text(stmt,5));
        setIp(protocol_to_add,(char*)sqlite3_column_text(stmt,6));
        setPort(protocol_to_add,sqlite3_column_int(stmt,7));

        list_add(*list_of_protocols,protocol_to_add);
    }

    sqlite3_finalize(stmt);
	//sqlite3_close(db);
}

