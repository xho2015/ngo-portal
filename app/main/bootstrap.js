$.extend({ngoModule: function(module) {
	$(function() {
		if (module.init) {	module.init();	}
	});
	return module;}
});

var JSLOADER = (function() {
	var my = {};	
	my.loadScript = function (url, ok, fail){
		$.getScript(url, ok, fail )
		  .done(function( script, textStatus ) {
		    console.log( textStatus );
		    if (ok) ok();
		  })
		  .fail(function( jqxhr, settings, exception ) {
		    console.log( exception );
		    if (fail) fail(url);
		});
	};
	
	my.loadScripts = function(urls, ok, fail) {
	    var i = 0;
	    (function loadNextScript() {
	         if (i < urls.length) {
	              $.getScript(urls[i].url)
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
	 * fomalize json response, if CDN or retry url existed, 
	 */
	function formalize(links)
	{
		for (u in links) {
			if (links[u].url.indexOf("|") !== -1)
			{
				var retry = links[u].url.split("|");
				links[u].url = retry[0];
				links[u].retry = retry;
			}
		}
	}
	
	my.loadScriptsRetry = function(urls, ok, fail) {
		formalize(urls);
	    var i = 0, r = 0;
	    (function loadNextScript() {
	         if (i < urls.length) {
	        	  var url = (r == 0 ?  urls[i].url : urls[i].retry[r]);
	              $.getScript(url)
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
	
	return my;
})();

var LIBRARY = (function() {
	var my = {};
	
	/**
	 * TODO: resolve token here
	 */
	my.load = function (module, ok, fail){
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
	my.loada = function (module, ok) {
		var api = "/json/bom?token=t1&module="+module;
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
		JSLOADER.loadScript(AppSettings.main);
	};	
	return {
		init : init
	};
}());
