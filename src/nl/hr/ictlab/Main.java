package nl.hr.ictlab;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBList;
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;

/**
 * Servlet implementation class Main
 */
@WebServlet("/Main")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Mongo mongo;
	private DB db;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Main() {
        super();
		try {
			mongo = new Mongo( "localhost" , 27017 );
		} catch (UnknownHostException | MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db = mongo.getDB("ICTLab");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
        out.println("<html>" +
        				"<head>" +
        					"<script type=\"text/javascript\" src=\"static/jquery-1.7.1.min.js\"></script>"+
        					"<script type=\"text/javascript\" src=\"static/jquery-ui-1.8.18.custom.min.js\"></script>"+
        					"<script type=\"text/javascript\" src=\"static/scripts.js\"></script>"+
        					"<link rel=\"stylesheet\" type=\"text/css\" href=\"static/style.css\">"+
        					"<link rel=\"stylesheet\" type=\"text/css\" href=\"static/jquery-ui-1.8.18.custom.css\">"+
        					"<script type=\"text/javascript\""+
        						"src=\"http://maps.googleapis.com/maps/api/js?sensor=false\">"+
        					"</script>"+
        					"<script type=\"text/javascript\">"+
        						"var markers = [];" +
        						"var colorMarkers = {};" +
        						"colorMarkers['red'] = 'http://www.orlandoedc.com/core/fileparse.php/104845/urlt/redMarker.png';"+
        						"colorMarkers['orange'] = 'http://www.portlandbolt.com/image/misc/map-marker-orange.png';"+
        						"colorMarkers['yellow'] = 'http://www.sp-ec.com/images/ico_YellowMarker.gif';"+
        						"colorMarkers['blue'] = 'http://www.orlandoedc.com/core/fileparse.php/104845/urlt/blueMarker.png';"+
        						"colorMarkers['green'] = 'http://www.orlandoedc.com/core/fileparse.php/104845/urlt/greenMarker.png';" +
        						"colors = ['red','orange','yellow','blue','green'];" +
        						"var currentColorMarker = 0;" +
        						"collectionIcon = {};"+
        					"</script>"+
        					"<script type=\"text/javascript\">"+
        						"var map;" +
        						"function initialize() {" +
        							"var myOptions = {" +
        								"center: new google.maps.LatLng(51.87116861834397, 4.470255727786548)," +
        								"zoom: 11," +
        								"mapTypeId: google.maps.MapTypeId.ROADMAP" +
        							"};" +
        							"map = new google.maps.Map(document.getElementById(\"map_canvas\"),myOptions);" +
        							"text =\"Hello\"" +
        						"}" +
        					"</script>"+
        				"</head>"+
	        			"<body onload=\"initialize()\">"+
	        				"<div id=\"wrapper\">"+
		        				"<div id=\"map_wrap\">"+
		        					"<div id=\"map_canvas\"></div>"+
		        				"</div>" +
		        				"<div id=\"collections\">" +
			        				"<h2>" +
			        					"Collections: " +
			        				"</h2>" +
			        				"<form name='collections' action='Main' method='post'>" +
			        					MongoDatabase.getCollectionsDiv()+
			        					"<input type='submit' value='Ok' />"+
			        				"</form>"+
		        				"</div>"+
		        				"<div id=\"controls\">"+
        							//HtmlBuilder.getCollectionControls("speeltoestellen")+
        						"</div>"+
		        			"</div>"+
		        		"</body>" +
		        	"</html>");
	}
	

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Set<String> allCollections = MongoDatabase.getCollections();
		List<String> selectedCollections = new ArrayList<>();
		for(String collection : allCollections){
			String parameter = request.getParameter(collection+"_checkbox");
			if(parameter != null){
				selectedCollections.add(collection);
			}
		}
		BasicDBList list = new BasicDBList();
		for(String collection : selectedCollections){
			list.add(collection);
		}
		PrintWriter out = response.getWriter();
        out.println("<html>" +
        				"<head>" +
        					"<script type=\"text/javascript\" src=\"static/jquery-1.7.1.min.js\"></script>"+
        					"<script type=\"text/javascript\" src=\"static/jquery-ui-1.8.18.custom.min.js\"></script>"+
        					"<script type=\"text/javascript\" src=\"static/scripts.js\"></script>"+
        					"<link rel=\"stylesheet\" type=\"text/css\" href=\"static/style.css\">"+
        					"<link rel=\"stylesheet\" type=\"text/css\" href=\"static/jquery-ui-1.8.18.custom.css\">"+
        					"<script type=\"text/javascript\""+
        						"src=\"http://maps.googleapis.com/maps/api/js?key=AIzaSyBUHKNVPkzNbOjejdzPKIVIMK12IU7w4Vg&sensor=false\">"+
        					"</script>"+
        					"<script type=\"text/javascript\">"+
	    						"var markers = [];" +
	    						"var colorMarkers = {};" +
	    						"colorMarkers['red'] = 'http://www.orlandoedc.com/core/fileparse.php/104845/urlt/redMarker.png';"+
	    						"colorMarkers['orange'] = 'http://www.portlandbolt.com/image/misc/map-marker-orange.png';"+
	    						"colorMarkers['yellow'] = 'http://www.sp-ec.com/images/ico_YellowMarker.gif';"+
	    						"colorMarkers['blue'] = 'http://www.orlandoedc.com/core/fileparse.php/104845/urlt/blueMarker.png';"+
	    						"colorMarkers['green'] = 'http://www.orlandoedc.com/core/fileparse.php/104845/urlt/greenMarker.png';" +
	    						"colors = ['red','orange','yellow','blue','green'];" +
	    						"var currentColorMarker = 0;" +
	    						"collectionIcon = {};"+
	    					"</script>"+
        					"<script type=\"text/javascript\">"+
        						"var map;" +
        						"function initialize() {" +
        							"var myOptions = {" +
        								"center: new google.maps.LatLng(51.87116861834397, 4.470255727786548)," +
        								"zoom: 11," +
        								"mapTypeId: google.maps.MapTypeId.ROADMAP" +
        							"};" +
        							"map = new google.maps.Map(document.getElementById(\"map_canvas\"),myOptions);" +
        							"text =\"Hello\"" +
        						"}" +
        					"</script>"+
        				"</head>"+
	        			"<body onload=\"initialize()\">"+
	        				"<div id=\"wrapper\">"+
		        				"<div id=\"map_wrap\">"+
		        					"<div id=\"map_canvas\"></div>"+
		        				"</div>" +
		        				"<div id=\"collections\">" +
			        				"<h2>" +
			        					"Collections: " +
			        				"</h2>" +
			        				"<form name='collections' action='Main' method='post'>" +
			        					MongoDatabase.getSelectedCollectionsDiv(selectedCollections)+
			        					"<input type='submit' value='Ok' />"+
			        				"</form>"+
		        				"</div>"+
		        				"<div id=\"controls\">"+
        							HtmlBuilder.getAllCollectionControls(selectedCollections)+
        						"</div>"+
		        			"</div>"+
		        		"</body>" +
		        	"</html>");
	}

}
