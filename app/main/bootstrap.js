
var dependency1 = function() {
   	//TODO: exception handling
	initDependency();
};

function scriptExist(sid)
{
	//validate existence
	var x = document.getElementById(sid);
    if (x) return x;
}


function createScript(sid)
{
	if (scriptExist(sid))
		return;

	// Adding the script tag to the head as suggested before
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.id = sid;
    script.type = 'text/javascript';
    return script;
}

function loadScript(sid, url, callback)
{
    var script = createScript(sid);
    script.src = url;
    script.async = false;

    // Then bind the event to the callback function.
    // There are several events for cross browser compatibility.
    script.onreadystatechange = callback;
    script.onload = callback;
    script.onerror = callback;

    // Fire the loading
    var head = document.getElementsByTagName('head')[0];
    head.appendChild(script);

    //validate the js element
    if (scriptExist(sid))
    	return 0;
}

loadScript("resource", "dependency.js", dependency1);
