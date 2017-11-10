$.extend({ngoModule: function(module) {
	$(function() {
		if (module.init) {	module.init();	}
	});
	return module;}
});


var LIBRARY = (function() {
	var my = {};
	
	var verpfix = window.location.hostname.substring(0,1);
	
	/**
	 * load single script without version 
	 */
	my.loadScript = function (url, ok, fail){
		if (AppSettings.debug == true){
			my.loadScriptDebug(url,ok,fail);
			return;
		}
		
		//enable caching
		$.ajaxSetup({ cache : true});
		$.getScript(url, ok, fail )
		  .done(function( script, textStatus ) {
		    if (ok) ok();
		  })
		  .fail(function( jqxhr, settings, exception ) {
		    console.log( exception + " script ["+url+"]");
		    if (fail) fail(url);
		});
	};
	
	/**
	 * load single script without version in DEBUG mode, script will show up in Source panel  
	 */
	my.loadScriptDebug = function (url, ok, fail){
		var head = document.getElementsByTagName('head')[0];
		var script = document.createElement('script');
		script.type = 'text/javascript';
		script.src = url;
		script.async = false;
		script.onload = function() {
			if (ok) ok();
		};
		script.onerror = function() {
		  if (fail) fail();
		};
		head.appendChild(script);
	};
	
	/**
	 * load multiple scripts in bulk 
	 */
	my.loadScripts = function(urls, ok, fail) {
	    var i = 0;
	    $.ajaxSetup({ cache : true});
	    (function loadNextScript() {
	         if (i < urls.length) {
	              $.getScript(urls[i].url + "?"+verpfix+urls[i].ver)
	              .done(function() {
	                  ++i;
	                  loadNextScript();
	              })
	              .fail(function( jqxhr, settings, exception ) {
					    console.log( exception );
					    if (fail) fail(urls[i].url);
					    return;
	              });
	         }
	         else 
	        	 if (ok) ok();
	    })();
	};
	
	/**
	 * do formalization for URLs Json object as below
	 * 1. inject "retry" to link objects, in case CDN or retry URL exists, 
	 */
	function formalize(links)
	{
		for (u in links) {
			if (links[u].url.indexOf("#") !== -1)
			{
				var retry = links[u].url.split("#");
				links[u].url = retry[0];
				links[u].retry = retry;
			}
		}
	}
	
	/**
	 * load multiple scripts in bulk, do retry in case CDN or retry URL exists 
	 */
	my.loadScriptsRetry = function(urls, ok, fail) {
		formalize(urls);
		var i = 0, r = 0;
		$.ajaxSetup({ cache : true});
	    (function loadNextScript() {
	         if (i < urls.length) {
	        	  var url = (r == 0 ?  urls[i].url : urls[i].retry[r]);
	              $.getScript(url + "?" +verpfix+urls[i].ver)
	              .done(function() {
	                  ++i;
	                  r = 0; //reset retry flag
	                  loadNextScript();
	              })
	              .fail(function( jqxhr, settings, exception ) {
					    console.log( exception + " script [" + url + "]");
					    if (urls[i].retry) {
					    	if (r < urls[i].retry.length)
					    	{	r++;
					    		console.log( "retry load script [" + urls[i].retry[r]+"]");
					    		loadNextScript();
					    	}
				    	}					    	
	              });
	         }
	         else 
	        	 if (ok) ok();
	    })();
	};
	
	/**
	 * load multiple NG contents (e.g. ngjs, ngpng) in bulk, do retry in case CDN or retry URL exists 
	 */
	my.loadRetry = function(urls, ok, fail, res) {
		if (AppSettings.debug == true){
			my.loadRetryDebug(urls,ok,fail,res);
			return;
		}
		//formalize urls json object	
		formalize(urls);
		var i = 0, r = 0;
		$.ajaxSetup({ cache : true});
	    (function loadNext() {
	         if (i < urls.length) {
	        	  var url = (r == 0 ?  urls[i].url : urls[i].retry[r]);	        	  
	        	  if ( urls[i].category=="J") {
	        		  //script start
		              $.getScript(url + "?" +verpfix+urls[i].ver)
		              .done(function() {
		                  ++i;
		                  r = 0; //reset retry flag
		                  loadNext();
		              })
		              .fail(function( jqxhr, settings, exception ) {
						    console.log( exception + " script [" + url + "]");
						    if (urls[i].retry) {
						    	if (r < urls[i].retry.length)
						    	{	r++;
						    		console.log( "retry load script [" + urls[i].retry[r]+"]");
						    		loadNext();
						    	}
					    	}					    	
		              });
		              //script end
    			  } else if (urls[i].category=="I")  {
    				  //png image start
    				  $.ajax({
    		      			type : "GET", 
    		      			async : false,	
    		      			cache : true,
    		      			dataType : "text",	
    		      			url : url+"?" +verpfix+urls[i].ver,
    		      			data : {token : "ses001"},
    		      			success : function(data) {		      				
    		      				AppCommon.toImage(data, function(image)	{
    		      					if (res)
    		      						res.put(urls[i].name, image);
        		      				++i;
        		      				loadNext();
		      					});
    		      			},
    		      			error : function(error) {
    		      				if (fail) fail();
    		      			}
    		      		});
    				  //png image end
				  }	        	  
	         }
	         else 
	        	 if (ok) ok();
	    })();
	};
	
	/**
	 * load multiple NG contents (e.g. ngjs, ngpng) in bulk in debug mode, do retry in case CDN or retry URL exists 
	 */
	my.loadRetryDebug = function(urls, ok, fail, res) {
		formalize(urls);
		var i = 0, r = 0;
		$.ajaxSetup({ cache : true});
	    (function loadNext() {
	         if (i < urls.length) {
	        	  var url = (r == 0 ?  urls[i].url : urls[i].retry[r]);	        	  
	        	  if (urls[i].category=="J") {
	        		  //script start
	        		  var head = document.getElementsByTagName('head')[0];
	        		  var script = document.createElement('script');
	        		  script.type = 'text/javascript';
	        		  script.src = url + "?" +verpfix+urls[i].ver;
	        		  script.async = false;
	        		  script.onload = function() {
	        			  ++i;
		      			  loadNext();
	        		  };
	        		  script.onerror = function() {
	        			  if (urls[i].retry) {
						    	if (r < urls[i].retry.length)
						    	{	r++;
						    		console.log( "retry load script [" + urls[i].retry[r]+"]");
						    		loadNext();
						    	}
					    	}	
	        		  };
	        		  head.appendChild(script);
	        		  //script end
    			  } else if (urls[i].category=="I") {
    				  //png image start
    				  $.ajax({
    		      			type : "GET", 
    		      			async : false,	
    		      			cache : true,
    		      			dataType : "text",	
    		      			url : url+"?" +verpfix+urls[i].ver,
    		      			data : {token : "ses001"},
    		      			success : function(data) {
    		      				AppCommon.toImage(data, function(image)	{
    		      					if (res)
    		      						res.put(urls[i].name, image);
        		      				++i;
        		      				loadNext();
		      					});
    		      			},
    		      			error : function(error) {
    		      				if (fail) fail();
    		      			}
    		      		});
    				  //png image end
				  }	else if (urls[i].category=="S") { 
					//css style start
				  }
	         }
	         else 
	        	 if (ok) ok();
	    })();
	};
	
	/**
	 * load required urls for designated module in synchronized mode
	 * TODO: resolve token here
	 */
	my.require = function (module, ok, fail){
		var data;
		$.ajax({
			type : "GET", 
			async : false,	
			cache : false,
			dataType : "json",	
			url : "/json/bom",
			data : {token : "ses001",module : module},
			success : function(json) {
				data = json;
			},
			error : function(error) {
				if (fail) fail();
			}
		});
		return data;
	};
	
	/**
	 * load required urls for designated module in asynchronized mode
	 * TODO: resolve token here
	 */
	my.requirea = function (module, ok) {
		var api = "/json/bom?token=t1&module="+module;
		$.ajaxSetup({ cache : false});
	    $.getJSON(api, {
	    	format: "json"
		}).done(function(data){
			  if (ok) ok(data);
		});
	};

	return my;
})();

var BOOTSTRAP = $.ngoModule(function() {
	function init() {
		LIBRARY.loadScript(AppSettings.main);
	};	
	return {
		init : init
	};
}());
