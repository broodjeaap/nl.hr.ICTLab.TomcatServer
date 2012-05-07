package nl.hr.ictlab;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.types.BasicBSONList;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDatabase {
	
	private static Mongo m;
	private static DB db;
	static{
		try {
			m = new Mongo();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		db = m.getDB("ICTLab"); 
	}
	
	public static BasicDBObject getCollectionKeys(String collectionName){
		DBCollection coll = db.getCollection("MetaData");
		DBCursor cursor = coll.find(new BasicDBObject("name",collectionName));
		BasicDBList ret = new BasicDBList();
		if(cursor.hasNext()){
			DBObject tmp = cursor.next();
			Set<String> keys = tmp.keySet();
			Iterator<String> it = keys.iterator();
			it.next(); //mongo generated ID
			it.next(); //name of the collection
			while(it.hasNext()){
				String name = it.next();
				BasicDBObject o = (BasicDBObject) tmp.get(name);
				if(!o.get("type").equals("none") && !o.get("type").equals("location")){
					BasicDBObject put = new BasicDBObject();
					put.put("name", name);
					put.put("type", o.get("type"));
					ret.add(put);
				}
			}
		}
		BasicDBObject dbo = new BasicDBObject();
		dbo.put("collectionname", collectionName);
		dbo.put("data", ret);
		return dbo;
	}
	
	public static DBObject getCollectionSelectionValues(String collectionName,String name){
		DBCollection coll = db.getCollection("MetaData");
		DBCursor cursor = coll.find(new BasicDBObject("name",collectionName));
		BasicDBList ret = new BasicDBList();
		if(cursor.hasNext()){
			DBObject tmp = cursor.next();
			tmp = (DBObject) tmp.get(name);
			if(tmp.get("type").equals("selection")){
				ret = (BasicDBList) tmp.get("values");
			}
		}
		BasicDBObject dbo = new BasicDBObject();
		dbo.put("collectionname", collectionName);
		dbo.put("keyname", name);
		dbo.put("data", ret);
		return dbo;
	}
	
	public static DBObject getCollectionSelectionVariables(String collectionName,String name){
		DBCollection coll = db.getCollection("MetaData");
		DBCursor cursor = coll.find(new BasicDBObject("name",collectionName));
		BasicDBList ret = new BasicDBList();
		if(cursor.hasNext()){
			DBObject tmp = cursor.next();
			tmp = (DBObject) tmp.get(name);
			if(tmp.get("type").equals("selection")){
				ret = (BasicDBList) tmp.get("variables");
			}
		}
		BasicDBObject dbo = new BasicDBObject();
		dbo.put("collectionname", collectionName);
		dbo.put("keyname", name);
		dbo.put("data", ret);
		return dbo;
	}
	
	public static DBObject getMetaData(String collectionName,String keyName){
		DBObject ret = null;
		DBCollection coll = db.getCollection("MetaData");
		BasicDBObject query = new BasicDBObject();
		query.put("name", collectionName);
		DBCursor metaData = coll.find(query);
		if(metaData.hasNext()){
			ret = metaData.next();
			ret = (DBObject) ret.get(keyName);
		}
		return ret;
	}
	
	public static BasicDBList getCollectionColumns(String collectionName){
		BasicDBList ret = new BasicDBList();
		DBCollection coll = db.getCollection("MetaData");
		BasicDBObject query = new BasicDBObject();
		query.put("name", collectionName);
		DBCursor metaData = coll.find(query);
		if(metaData.hasNext()){
			DBObject tmp = metaData.next();
			Set<String> keys = tmp.keySet();
			for(String key : keys){
				BasicDBObject o = new BasicDBObject();
				o.put("type", ((BasicDBObject) tmp.get(key)).get("type"));
				o.put("name", key.replaceAll(" ", ""));
				ret.put(key, o);
			}
		}
		return ret;
	}
	
	public static DBCursor getQueryResult(String collectionName, DBObject query){
		DBCollection coll = db.getCollection(collectionName);
		return coll.find(query);
	}

	public static DBCursor getAll(String collectionName) {
		DBCollection coll = db.getCollection(collectionName);
		return coll.find();
	}
	
	public static Set<String> getCollections(){
		return db.getCollectionNames();
	}
	
	public static String getCollectionsDiv(){
		StringBuilder sb = new StringBuilder();
		sb.append("<table id='collectionTable'>");
		Set<String> collections = getCollections();
		boolean flip = true;
		for(String collection : collections){
			if(!collection.equals("MetaData") && !collection.equals("system.indexes")){
				if(flip){
					sb.append("<tr class='collectionTableEven'><td><input type='checkbox' name='"+collection+"_checkbox' id='"+collection+"_checkbox' value='"+collection+"' /></td><td>"+collection+"</td></tr>");
				} else {
					sb.append("<tr class='collectionTableOdd'><td><input type='checkbox' name='"+collection+"_checkbox' id='"+collection+"_checkbox' value='"+collection+"' /></td><td>"+collection+"</td></tr>");
				}
				flip = !flip;
			}
		}
		sb.append("</table>");
		return sb.toString();
	}

	public static String getCollectionsDiv(List<String> selectedCollections) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table id='collectionTable'>");
		boolean flip = true;
		for(String collection : selectedCollections){
			if(!collection.equals("MetaData") && !collection.equals("system.indexes")){
				if(flip){
					sb.append("<tr class='collectionTableEven'><td><input type='checkbox' name='"+collection+"_checkbox' id='"+collection+"_checkbox' value='"+collection+"' /></td><td>"+collection+"</td></tr>");
				} else {
					sb.append("<tr class='collectionTableOdd'><td><input type='checkbox' name='"+collection+"_checkbox' id='"+collection+"_checkbox' value='"+collection+"' /></td><td>"+collection+"</td></tr>");
				}
				flip = !flip;
			}
		}
		sb.append("</table>");
		return sb.toString();
	}

	public static String getSelectedCollectionsDiv(List<String> selectedCollections) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table id='collectionTable'>");
		Set<String> collections = getCollections();
		boolean flip = true;
		for(String collection : collections){
			if(!collection.equals("MetaData") && !collection.equals("system.indexes")){
				String checked = "";
				if(selectedCollections.contains(collection)){
					checked = "checked='checked'";
				}
				if(flip){
					sb.append("<tr class='collectionTableEven'><td><input type='checkbox' name='"+collection+"_checkbox' id='"+collection+"_checkbox' value='"+collection+"' "+checked+"/></td><td>"+collection+"</td></tr>");
				} else {
					sb.append("<tr class='collectionTableOdd'><td><input type='checkbox' name='"+collection+"_checkbox' id='"+collection+"_checkbox' value='"+collection+"' "+checked+"/></td><td>"+collection+"</td></tr>");
				}
				flip = !flip;
			}
		}
		sb.append("</table>");
		return sb.toString();
	}
}
