/*!
 The MIT License (MIT) Copyright (c) 2015 popAD, LLC dba Rocket Wagon Labs <lukel99@gmail.com>
 */
;(function ($) {

	var COORDINATE_LENGTH = 5000;

	//CLASSES
	/**
	 * The star object we're going to create
	 * Star's coordinate system is 0 through COORDINATE_LENGTH, and then mapped onto the coordinate system of our canvas
	 * @param  {number} x
	 * @param  {number} y
	 * @param  {number} size
	 * @param  {string} color - color string
	 * @return {Star} a star object
	 */
	var Star = function (x, y, size, color) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = color;
	};

	/**
	 * Convert from star X/Y (0-COORDINATE_LENGTH) to canvas X/Y
	 * @param  {number} canvasWidth - the canvas width in pixels
	 * @param  {number} canvasHeight - the canvas height in pixels
	 * @return {Object} an object containing the coordinates on the canvas
	 */
	Star.prototype.mapXYToCanvasCoordinates = function (canvasWidth, canvasHeight) {
		var canvasX = Math.round((this.x / COORDINATE_LENGTH) * canvasWidth);
		var canvasY = Math.round((this.y / COORDINATE_LENGTH) * canvasHeight);
		return {
			x: canvasX,
			y: canvasY
		}
	};

	var StarFactory = {
		/**
		 * Generates all random values to create a random star
		 * @return {Star} a star with random X/Y, size and color
		 */
		getRandomStar: function () {
			var x = Math.floor(Math.random() * (COORDINATE_LENGTH + 1));
			var y = Math.floor(Math.random() * (COORDINATE_LENGTH + 1));
			var size = this._getWeightedRandomSize();
			var color = this._getWeightedRandomColor();
			var tintedColor = this._applyRandomShade(color);
			return new Star(x, y, size, this._getRGBColorString(tintedColor));
		},

		_getWeightedRandomSize: function () {
			var list = [1, 1.5, 2];
			var weight = [0.8, 0.15, 0.05];
			return this._getWeightedRandom(list, weight);
		},

		_getWeightedRandomColor: function () {
			var list = [
				{'r': 255, 'g': 189, 'b': 111},
				{'r': 255, 'g': 221, 'b': 180},
				{'r': 255, 'g': 244, 'b': 232},
				{'r': 251, 'g': 248, 'b': 255},
				{'r': 202, 'g': 216, 'b': 255},
				{'r': 170, 'g': 191, 'b': 255},
				{'r': 155, 'g': 176, 'b': 255}
			];
			var weight = [0.05, 0.05, 0.05, 0.7, 0.05, 0.05, 0.05];
			return this._getWeightedRandom(list, weight);
		},

		_getRandomShade: function () {
			var list = [0.4, 0.6, 1];
			var weight = [0.5, 0.3, 0.2];
			return this._getWeightedRandom(list, weight);
		},

		_applyRandomShade: function (color) {
			var shade = this._getRandomShade();
			if (shade !== 1) { // skip processing full brightness stars
				color['r'] = Math.floor(color['r'] * shade);
				color['g'] = Math.floor(color['g'] * shade);
				color['b'] = Math.floor(color['b'] * shade);
			}
			return color;
		},

		_getRGBColorString: function (color) {
			return 'rgb(' + color['r'] + ',' + color['g'] + ',' + color['b'] + ')';
		},

		// http://codetheory.in/weighted-biased-random-number-generation-with-javascript-based-on-probability/
		_getWeightedRandom: function (list, weight) {

			var rand = function (min, max) {
				return Math.random() * (max - min) + min;
			};

			var total_weight = weight.reduce(function (prev, cur) {
				return prev + cur;
			});

			var random_num = rand(0, total_weight);
			var weight_sum = 0;

			for (var i = 0; i < list.length; i++) {
				weight_sum += weight[i];
				weight_sum = +weight_sum.toFixed(2);

				if (random_num <= weight_sum) {
					return list[i];
				}
			}
		}
	};

	var Starfield = [];
	
	var lunched = false;

	$.fn.starfield = function (options, canvasId) {

		var settings = $.extend({
			framerate: 8,
			speedX: 4,
			starDensity: 1.0,
			mouseScale: 1.0,
			background: '#000000',
			seedMovement: true
		}, options);

		$this = $(this);

		var width = $this.width();
		var height = $this.height();

		var totalPixels = width * height;
		var starRatio = 0.002 * settings.starDensity;
		var numStars = Math.floor(totalPixels * starRatio);

		if(settings.seedMovement){
			var deltaX = settings.speedX;
			var deltaY = 0;
		} else {
			var deltaX = 0;
			var deltaY = 0;
		}		

		//clean up the starts
		Starfield = [];
		for (var i = 0; i < numStars; i++) {
			Starfield.push(StarFactory.getRandomStar());
		}

		// ANIMATION HANDLER
		var recalcMovement = function () {
			$.each(Starfield, function (key, star) {
				var newX = star.x - deltaX;
				var newY = star.y - deltaY;

				if (newX < 0) { newX += COORDINATE_LENGTH }
				if (newY < 0) { newY += COORDINATE_LENGTH }
				if (newX > COORDINATE_LENGTH) {newX -= COORDINATE_LENGTH}
				if (newY > COORDINATE_LENGTH) {newY -= COORDINATE_LENGTH}

				star.x = newX;
				star.y = newY;
			});
		};

		var draw = function () {
			//get raw DOM element
			var canvas = document.getElementById(canvasId);
			var width = canvas.width;
			var height = canvas.height;

			canvas.setAttribute("width", width.toString());
			canvas.setAttribute("height", height.toString());

			if (canvas.getContext) {
				var ctx = canvas.getContext('2d');

				// clear canvas
				ctx.clearRect(0, 0, width, height);
				ctx.fillStyle = settings.background;
				ctx.fillRect(0, 0, width, height);
				
				// randomly shine
				var rands = [];
				rands.push(getRandomInt(0, Starfield.length));
				rands.push(getRandomInt(Math.floor(Starfield.length / 2) , Starfield.length));

				var idxr = 0, shine = 0, soffset = 0;
				// iterate stars and draw them
				$.each(Starfield, function (index, star) {
					var coords = star.mapXYToCanvasCoordinates(width, height);
					shine = (rands.indexOf(idxr++) > 0) ? 2 : 0;
					soffset = shine > 0 ? -1 : 0;
					ctx.fillStyle = star.color;
					ctx.fillRect(coords.x+soffset, coords.y+soffset, star.size+shine, star.size+shine);
				});
			}
		};
		
		/**
		 * Returns a random integer between min (inclusive) and max (inclusive)
		 * Using Math.round() will give you a non-uniform distribution!
		 */
		function getRandomInt(min, max) {
		    return Math.floor(Math.random() * (max - min + 1)) + min;
		}

		function animate() {
			recalcMovement();
			draw();
		}

		if (lunched == false) {
			AppCommon.limitLoop(animate, settings.framerate);
			lunched = true;
		}

		return this;
	};

}(jQuery));