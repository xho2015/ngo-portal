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
	
	return my;
})();

var LIBRARY = (function() {
	var my = {};	
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
