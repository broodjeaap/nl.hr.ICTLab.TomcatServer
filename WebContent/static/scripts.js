var timechangetimout;
var collectionKeys = {};
var collectionControls = {};
var collectionSelectionValues = {};

function refresh(collection){
	keys = collectionKeys[collection];
	var data = "collection="+collection+"&";
	for(var a = 0;a < keys.length;++a){
		if(keys[a].type == "range"){
			sliderValues = $("#"+collection+"_"+keys[a].name+"_range").slider("values");
			data += collection+"_"+keys[a].name+"_from="+sliderValues[0]+"&";
			data += collection+"_"+keys[a].name+"_to="+sliderValues[1]+"&";
		} else if (keys[a].type == "selection"){
			var selectedValues = "";
			values = collectionSelectionValues[collection+"_"+keys[a].name];
			for(var b = 0;b < values.length;++b){
				checkbox = $("#"+collection+"_"+keys[a].name+"_"+values[b]);
				if(checkbox != null){
					if(checkbox.prop("checked")){
						selectedValues += values[b]+",";
					}
				}
			}
			if(selectedValues.length > 0){
				selectedValues = selectedValues.slice(0,selectedValues.length-1);
				data += collection+"_"+keys[a].name+"_values="+selectedValues+"&";
			}
		}
	}
	data = data.slice(0,data.length-1);
	$.getJSON('AjaxGet?'+data,function(data){
		currentMarkers = markers[data.name];
		if(currentMarkers != null){
		  	for(var a =0;a < currentMarkers.length;++a){
		  		currentMarkers[a].setMap(null);
		  	}
		}
	  	currentMarkers = [];
	  	coordinates = data.data;
	  	for(var a = 0;a < coordinates.length;++a){
	  		marker = new google.maps.Marker({position: new google.maps.LatLng(coordinates[a].y,coordinates[a].x), icon: collectionIcon[collection]}); 
	  		currentMarkers.push(marker); 
	  		marker.setMap(map);
	  	}
	  	markers[data.name] = currentMarkers;
	});
}

function changeColor(collection, dropdown){
	collectionIcon[collection] = colorMarkers[dropdown.value];
	refresh(collection);
}

