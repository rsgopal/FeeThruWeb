package com.janakan.feethru;

import java.util.Date;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class UpdateTransDate {
	public static void main(String[] args) {
		MongoClient client = new MongoClient(new MongoClientURI(
				"mongodb://admin:Janakan!23@cluster0-shard-00-00-nlaxy.mongodb.net:27017,cluster0-shard-00-01-nlaxy.mongodb.net:27017,cluster0-shard-00-02-nlaxy.mongodb.net:27017/feethru?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true"));
		try {
			MongoDatabase database = client.getDatabase("feethru");
			MongoCollection<Document> transColl = database.getCollection("transaction");
			transColl.updateMany(Filters.eq("date", null), new Document("$set", new Document("date", new Date())));
		} finally {
			client.close();
		}
	}
}
