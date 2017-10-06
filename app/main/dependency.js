function initDependency()
{
	var dependency = {
				    "resource": [
				        //{"tid":"3", "load":"l|n",  "name":"media2.2","url":"media1.zip"},
				        {"tid":"1", "load":"l|c|n", "name":"threeminjs","url":"https://cdnjs.cloudflare.com/ajax/libs/three.js/87/three.min.js"},
				        {"tid":"1", "load":"l|n",  "name":"datgui","url":"/app/lib/dat.gui.min.ngjs"},
				        {"tid":"1", "load":"l|n",  "name":"common1", "url":"/app/lib/common.min.ngjs"},
				        {"tid":"1", "load":"l|n",  "name":"demo1","url":"demo1.min.ngjs"}
				    ]
				};

	var resource = dependency.resource;
	for(var i in resource)
	{
	     var loads = resource[i].load.split('|');
	     var ok = -1;
	     for (var l in loads)
	     {     	
	     	if (loads[l] == 'l' && ok == -1)
	     		ok = loadLocal(resource[i].name, resource[i].tid);
			if (loads[l] == 'c' && ok == -1)
	     		ok = loadScript(resource[i].name,resource[i].url, "");
	     	if (loads[l] == 'n' && ok == -1)
	     		ok = loadScript(resource[i].name,resource[i].url, "");
	     }
	}
}

/*
HTML5 local storage
https://stackoverflow.com/questions/5523140/html5-local-storage-vs-session-storage
*/
function loadLocal(rs, type)
{
	if (typeof(Storage) !== "undefined") {
	    var result = localStorage.getItem(rs);
	    if (result == null)
	    	return -1;
	    //parsing the content based on its type
	    if (type=="1") 
	    {
	    	var x = createScript(rs);
	    	x.text = result;
	    	// Fire the loading
		    var head = document.getElementsByTagName('head')[0];
		    head.appendChild(script);
		    return 0
	    } else if (type=="3") {

	    }
	    return 0;
	} 
	else
	{
		return -1;
	}
}