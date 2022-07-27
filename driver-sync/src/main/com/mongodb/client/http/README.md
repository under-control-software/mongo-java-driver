# Mongo HTTP
This API is for using a mongoDB instance through an http server which understands the curl similar to following
```
curl 'https://http-server-url/' \
  -H 'x-requested-with: XMLHttpRequest' \
  --data-raw 'Service=MONGO&partnerId=0&serverType=GLOBAL&collectionName=account&queryType=general&limit=1&sortDirection=ASC&sortField=&includeFields=&query='{"_id" : "50003"}' \
  --compressed
```

## Useage
To use this API just change imports to this
```
import com.mongodb.client.http.MongoClient;
import com.mongodb.client.http.MongoClients;
import com.mongodb.client.http.MongoCollection;
import com.mongodb.client.http.MongoDatabase;
```


## Specifying the url
To specify the url use similar API pattern as connection string.
```
MongoClient mongoClient = MongoClients.create("https://myurl/api/mongo");
```
You can give parameters in the similar way as in connection string using ? and &.
```
MongoClient mongoClient = MongoClients.create("https://myurl/api/mongo?serverType=GLOBAL&partnerId=0");
```

### Creating MongoDatabase and MongoCollection
To create a MongoDatabase and MongoCollection just change the imports and use in the same way as with default driver 
usages

All CRUD operations are not supported yet.
only Read operations like findOne and into are supported.
