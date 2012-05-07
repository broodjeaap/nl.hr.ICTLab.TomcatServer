package nl.hr.ictlab;

import java.net.UnknownHostException;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class HtmlBuilder {
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
	public static String getRangeDiv(String collectionName, String keyName){
		DBObject metaData = MongoDatabase.getMetaData(collectionName, keyName);
		String s = "";
		if(metaData != null){
			String type = metaData.get("type").toString();
			if(type.equals("range")){
				s = "<div class='range' id=\""+collectionName+"_"+keyName+"_range_div\">" +
						"<p id=\""+collectionName+"_"+keyName+"_range_span\">Van "+metaData.get("from")+" tot "+metaData.get("to")+"</p>"+
						"<div id=\""+collectionName+"_"+keyName+"_range\" class=\"ui-slider ui-slider-horizontal ui-widget ui-widget-content ui-corner-all\"></div>" +
					"</div>" +
					"<script>" +
					"$(function() {"+
						"$(\"#"+collectionName+"_"+keyName+"_range\").slider({"+
							"range: true,"+
							"min: "+metaData.get("from")+","+
							"max: "+metaData.get("to")+","+
							"values: [ "+metaData.get("from")+", "+metaData.get("to")+" ],"+
							"slide: function( event, ui ) {" +
								"collectionControls[\""+collectionName+"_"+keyName+"\"] = ui;"+
								"$(\"#"+collectionName+"_"+keyName+"_range_span\").html(\"Van \" + ui.values[ 0 ] + \" tot \" + ui.values[ 1 ] );"+
								"refresh(\""+collectionName+"\");"+
							"}" +
						"});"+
					"});" +
					"</script>";
			}
		}
		return s;
	}
	
	public static String getSelectionDiv(String collectionName, String keyName){
		DBObject metaData = MongoDatabase.getMetaData(collectionName, keyName);
		String s = "";
		if(metaData != null){
			s = "<div class='selection' id=\""+collectionName+"_"+keyName+"_selection_div\">" +
					"<p id=\""+collectionName+"_"+keyName+"_selection_span\">"+keyName+" Selection</p>" +
						"<table>" +
							"<tr>";
			BasicDBList values = (BasicDBList) metaData.get("values");
			BasicDBList variables = (BasicDBList) metaData.get("variables");
			int count = 0;
			for(int a = 0;a < values.size();++a){
				String value = values.get(a).toString();
				String variable = variables.get(a).toString();
				s += "<td><input id=\""+collectionName+"_"+keyName+"_"+variable+"\" type='checkbox' onclick=\"refresh('"+collectionName+"')\" value=\""+variable+"\" /></td><td>"+value+"</td>";
				if(count++ > 4){
					s += "</tr>";
					count = 0;
					if(a < values.size()-1){
						s+="<tr>";
					}
				}
			}
			s += "</table>" +
				"<script>" +
					"collectionSelectionValues['"+collectionName+"_"+keyName+"'] = "+MongoDatabase.getCollectionSelectionVariables(collectionName, keyName).get("data")+";"+
				"</script>"+
			"</div>";
		}
		return s;
	}
	
	public static String getCollectionControls(String collectionName){
		StringBuilder s = new StringBuilder();
		BasicDBList columns = (BasicDBList) MongoDatabase.getCollectionKeys(collectionName).get("data");
		for(Object column : columns){
			BasicDBObject bo = (BasicDBObject)column;
			String type = (String) bo.get("type");
			switch(type){
				case "selection":{
					s.append(getSelectionDiv(collectionName,bo.get("name").toString()));
					break;
				}
				case "range":{
					s.append(getRangeDiv(collectionName,bo.get("name").toString()));
					break;
				}
			}
		}
		s.append("<script>" +
					"collectionKeys['"+collectionName+"'] = " +MongoDatabase.getCollectionKeys(collectionName).get("data")+";"+
				"</script>");
		return s.toString();
	}

	public static String getAllCollectionControls(List<String> selectedCollections) {
		StringBuilder sb = new StringBuilder();
		for(String collection : selectedCollections){
			sb.append("<div class='collectionControlsWrapper'>");
			sb.append(	"<h2>" +
							collection+
						"<h2>" +
						"<select onchange=\"changeColor('"+collection+"',this)\">" +
							"<option value='red'>" +
								"Red" +
							"</option>" +
							"<option value='orange'>" +
								"Orange</option>" +
							"<option value='yellow'>" +
								"Yellow" +
							"</option>" +
							"<option value='blue'>" +
								"Blue" +
							"</option>" +
							"<option value='green'>" +
								"Green" +
							"</option>" +
						"</select>" +
						"<script>" +
						"collectionIcon['"+collection+"'] = colorMarkers[colors[currentColorMarker++]];"+
						"</script>");
			sb.append(getCollectionControls(collection));
			sb.append("</div>");
		}//red','orange','yellow','blue','green
		return sb.toString();
	}
}