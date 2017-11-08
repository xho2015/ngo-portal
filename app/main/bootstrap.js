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
	 * load single script without cache versioning 
	 */
	my.loadScript = function (url, ok, fail){
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
	 * inject "retry" to link objects, in case CDN or retry URL exists, 
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
