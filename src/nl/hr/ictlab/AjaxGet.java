package nl.hr.ictlab;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Servlet implementation class AjaxGet
 */
@WebServlet("/AjaxGet")
public class AjaxGet extends HttpServlet {
	private static final long serialVersionUID = 1L; 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AjaxGet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String collection = request.getParameter("collection");
		BasicDBList collectionKeys = (BasicDBList) MongoDatabase.getCollectionKeys(collection).get("data");
		List<DBObject> andQuery = new ArrayList<>();
		for(int a = 0;a < collectionKeys.size();++a){
			BasicDBObject dbo = (BasicDBObject) collectionKeys.get(a);
			String keyName = dbo.getString("name");
			if(dbo.get("type").equals("selection")){
				BasicDBList values = (BasicDBList) MongoDatabase.getCollectionSelectionValues(collection, keyName).get("data");
				BasicDBList variables = (BasicDBList) MongoDatabase.getCollectionSelectionVariables(collection, keyName).get("data");
				Map<String,String> variableValueMap = getVariablesValuesMap(values,variables);
				String parameterValues = request.getParameter(collection+"_"+keyName+"_values");
				if(parameterValues != null){
					String[] selectedValues = parameterValues.split(",");
					DBObject[] querys = new BasicDBObject[selectedValues.length];
					for(int b = 0;b < selectedValues.length;++b){
						querys[b] = new BasicDBObject(keyName,variableValueMap.get(selectedValues[b]));
					}
					//query.put(keyName, selectedValuesQuery);
					BasicDBObject orQuery = new BasicDBObject();
					orQuery.put("$or", querys);
					andQuery.add(orQuery);
				}
			} else if (dbo.get("type").equals("range")){
				String parameterValue = request.getParameter(collection+"_"+keyName+"_from");
				if(parameterValue != null){
					int from = Integer.parseInt(parameterValue);
					int to = Integer.parseInt(request.getParameter(collection+"_"+keyName+"_to"));
					andQuery.add(new BasicDBObject(keyName, new BasicDBObject("$gte",from).append("$lte", to)));
				}
			}
		}
		DBCursor cursor;
		if(andQuery.size() > 0){
			DBObject query = new BasicDBObject("$and",andQuery.toArray(new BasicDBObject[0]));
			cursor = MongoDatabase.getQueryResult(collection, query);
		} else {
			cursor = MongoDatabase.getAll(collection);
		}
		//out.println(query);
		BasicDBList coordinates = new BasicDBList();
		coordinates.ensureCapacity(cursor.count());
		while(cursor.hasNext()){
			coordinates.add(cursor.next().get("coordinates"));
		}
		DBObject meta = new BasicDBObject();
		meta.put("name", collection);
		meta.put("data", coordinates);
		out.println(meta);
	}
	
	private Map<String, String> getVariablesValuesMap(BasicDBList values,BasicDBList variables){
		Map<String,String> map = new HashMap<>();
		for(int a = 0;a < values.size();++a){
			//BasicDBObject variable = (BasicDBObject)variables.get(a);
			//BasicDBObject value = (BasicDBObject)values.get(a);
			map.put((String)variables.get(a),(String)values.get(a));
		}
		return map;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
