var AppCommon = (function() {
	
	/**
	 * Map define 
	 */
	function Map() {
	    this.container = {};
	    
	    Map.prototype.put = function(key, value) {
	        try {
	            if (key != null && key != "")
	                this.container[key] = value;
	        } catch (e) {
	            return e;
	        }
	    };
	    
	    Map.prototype.putArray = function(arrayObj) {
	        try {
	            if (arrayObj != null )
	            	for (e in arrayObj)	{
	            		this.put(arrayObj[e][0], arrayObj[e][1]);
	            	}
	            return this;
	        } catch (e) {
	            return e;
	        }
	    };
	    
	    Map.prototype.putJson = function(json, key, value) {
	        try {
	            if (json != null && key != "" && key !=null && value!="" & value!=null)
	            	for (e in json)	{
	            		this.put(json[e][key], json[e][value]);
	            	}
	            return this;
	        } catch (e) {
	            return e;
	        }
	    };
	    
	    Map.prototype.toString = function() {
	    	var out = '';
	    	var entries = this.entrySet();
	        try {
	        	for (e in entries) {
	        		out += entries[e] +",";
        		}
	        	return out;
	        } catch (e) {
	            return e;
	        }
	    };
	     
	    Map.prototype.get = function(key) {
	        try {
	            return this.container[key];
	        } catch (e) {
	            return e;
	        }
	    };
	     
	    Map.prototype.containsKey = function(key) {
	        try {
	            for ( var p in this.container) {
	                if (p == key)
	                    return true;
	            }
	            return false;
	     
	        } catch (e) {
	            return e;
	        }
	    }
	     
	    Map.prototype.containsValue = function(value) {
	        try {
	            for ( var p in this.container) {
	                if (this.container[p] === value)
	                    return true;
	            }
	            return false;
	     
	        } catch (e) {
	            return e;
	        }
	    };
	     
	    Map.prototype.remove = function(key) {
	        try {
	            delete this.container[key];
	        } catch (e) {
	            return e;
	        }
	    };
	     
	    Map.prototype.clear = function() {
	        try {
	            delete this.container;
	            this.container = {};
	     
	        } catch (e) {
	            return e;
	        }
	    };
	     
	    Map.prototype.isEmpty = function() {
	     
	        if (this.keySet().length == 0)
	            return true;
	        else
	            return false;
	    };
	     
	    Map.prototype.size = function() {
	     
	        return this.keySet().length;
	    }
	     
	    Map.prototype.keySet = function() {
	        var keys = new Array();
	        for ( var p in this.container) {
	            keys.push(p);
	        }	     
	        return keys;
	    }
	     
	    Map.prototype.values = function() {
	        var valuesArray = new Array();
	        var keys = this.keySet();
	        for (var i = 0; i < keys.length; i++) {
	            valuesArray.push(this.container[keys[i]]);
	        }
	        return valuesArray;
	    }
	     
	    Map.prototype.entrySet = function() {
	        var array = new Array();
	        var keys = this.keySet();
	        for (var i = 0; i < keys.length; i++) {
	            array.push(keys[i],this.container[keys[i]]);
	        }
	        return array;
	    }
	     
	    Map.prototype.sumValues = function() {
	        var values = this.values();
	        var result = 0;
	        for (var i = 0; i < values.length; i++) {
	            result += Number(values[i]);
	        }
	        return result;
	    }
	    return this;
	};
	
	/**
	 * convert BASE64 dataUrl to png Image, callbeck will be invoked once image.onload 
	 * @param dataUrl
	 * @param callback
	 * @returns
	 */
	function toImage(dataUrl, callback)	{
		var image = new Image();
		image.src = 'data:image/png;base64,'+dataUrl;
		image.onload = function (){ 
			if (callback)
				callback(this);
		} 	
	};
	
	/*
	 * Limit the frame rate https://gist.github.com/addyosmani/5434533
	 */
	var limitLoop = function(fn, fps) {
		// Use var then = Date.now(); if you
		// don't care about targetting < IE9
		var then = new Date().getTime();
		// custom fps, otherwise fallback to 60
		fps = fps || 60;
		var interval = 1000 / fps;
		return (function loop(time) {
			requestAnimationFrame(loop);
			// again, Date.now() if it's available
			var now = new Date().getTime();
			var delta = now - then;

			if (delta > interval) {
				// Update time
				// now - (delta % interval) is an improvement over just
				// using then = now, which can end up lowering overall fps
				then = now - (delta % interval);
				// call the fn
				fn();
			}
		}(0));
	};

	/**
	 * @author alteredq / http://alteredqualia.com/
	 * @author mr.doob / http://mrdoob.com/
	 */
	var Detector = {

		canvas : !!window.CanvasRenderingContext2D,
		webgl : (function() {
			try {

				var canvas = document.createElement('canvas');
				return !!(window.WebGLRenderingContext && (canvas
						.getContext('webgl') || canvas
						.getContext('experimental-webgl')));
			} catch (e) {
				return false;
			}
		})(),
		workers : !!window.Worker,
		fileapi : window.File && window.FileReader && window.FileList
				&& window.Blob,
		getWebGLErrorMessage : function() {
			var element = document.createElement('div');
			element.id = 'webgl-error-message';
			element.style.fontFamily = 'monospace';
			element.style.fontSize = '13px';
			element.style.fontWeight = 'normal';
			element.style.textAlign = 'center';
			element.style.background = '#fff';
			element.style.color = '#000';
			element.style.padding = '1.5em';
			element.style.width = '400px';
			element.style.margin = '5em auto 0';
			if (!this.webgl) {
				element.innerHTML = window.WebGLRenderingContext ? [
						'Your graphics card does not seem to support <a href="http://khronos.org/webgl/wiki/Getting_a_WebGL_Implementation" style="color:#000">WebGL</a>.<br />',
						'Find out how to get it <a href="http://get.webgl.org/" style="color:#000">here</a>.' ]
						.join('\n')
						: [
								'Your browser does not seem to support <a href="http://khronos.org/webgl/wiki/Getting_a_WebGL_Implementation" style="color:#000">WebGL</a>.<br/>',
								'Find out how to get it <a href="http://get.webgl.org/" style="color:#000">here</a>.' ]
								.join('\n');
			}
			return element;
		},

		addGetWebGLMessage : function(parameters) {
			var parent, id, element;
			parameters = parameters || {};
			parent = parameters.parent !== undefined ? parameters.parent
					: document.body;
			id = parameters.id !== undefined ? parameters.id : 'oldie';
			element = Detector.getWebGLErrorMessage();
			element.id = id;
			parent.appendChild(element);
		}
	};

	
	return {
		limitLoop : limitLoop,
		Detector : Detector,
		Map : Map,
		toImage: toImage
	};

})();