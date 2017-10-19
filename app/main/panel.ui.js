/**
 * NGO UI library - H5 canvas based interface
 */

var AppMainUI = (function() {

	var resizeEvents = [];

	function registeResize(resize) {
		resizeEvents.push(resize);
		console.log("resize Events registed");
	}

	/*
	 * the only sensor of window resize event
	 */
	function onWindowResize() {
		for (var i = 0; i < resizeEvents.length; i++) {
			var resize = resizeEvents[i];
			resize();
		}
	}
	;

	var canvas;
	var ctx;
	var BB;
	var offsetX;
	var offsetY;
	var WIDTH;
	var HEIGHT;

	function onResize() {
		var canvas = document.getElementById('panel_canvas');
		canvas.width = panelscreen.innerWidth();
		canvas.height = panelscreen.innerHeight();
		BB = canvas.getBoundingClientRect();
		offsetX = BB.left;
		offsetY = BB.top;
		WIDTH = canvas.width;
		HEIGHT = canvas.height;

		console.log("onResize, WIDTH=" + WIDTH + ", HEIGHT=" + HEIGHT);
		drawPanelUI();
	}
	;

	function initBackground() {
		// create background canvas
		var bgscreen = $('#background_screen');
		$('<canvas>').css({
			position : 'absolute',
			left : 0,
			top : 0,
			width : '100%',
			height : '100%'
		}).attr({
			id : 'backgound_canvas',
			width : bgscreen.width(),
			height : bgscreen.height()
		}).prependTo(bgscreen);

		// enable star field effect
		if (AppSettings.bganimate) {
			$(bgscreen).starfield({
				framerate : 20,
				speedX : 2,
				starDensity : 0.10,
				mouseScale : 0.01,
				background : '#000007',
				seedMovement : true
			}, "backgound_canvas");
		}

	}
	
	
	var tickEvents = [];
	
	function registTick(event)
	{
		tickEvents.push(event);
	}

	function tick(event)
	{
		for (t in tickEvents)
			tickEvents[t](event);
	}

	var stages = [];
	var moduleContainer;
	var moduleStage;

	function initPanel() {
		panelscreen = $('#panel_screen');
		$('<canvas>').css({
			position : 'absolute',
			left : 0,
			top : 0,
			width : '100%',
			height : '100%'
		}).attr({
			id : 'panel_canvas',
			width : panelscreen.width(),
			height : panelscreen.height()
		}).prependTo(panelscreen);

		// init module list
		var moduleNPCs = [];
		moduleContainer = new createjs.Container();

		moduleContainer.width = 200;
		moduleContainer.height = 300;
		moduleContainer.x = WIDTH - moduleContainer.width - 40;
		moduleContainer.y = HEIGHT - moduleContainer.height - 40;

		var spriteBMP = new createjs.Shape(); // new
												// createjs.Bitmap("/app/res/tile.png");
		spriteBMP.graphics.beginFill("#004").drawRect(5, 0, 110, 80);
		spriteBMP.y = 4;
		spriteBMP.x = 0;
		var spriteBMP2 = new createjs.Shape(); // new
												// createjs.Bitmap("/app/res/tile.png");
		spriteBMP2.graphics.beginFill("#004").drawRect(5, 0, 110, 80);
		spriteBMP2.y = 88;
		spriteBMP2.x = 0;
		var spriteBMP3 = new createjs.Shape(); // new
												// createjs.Bitmap("/app/res/tile.png");
		spriteBMP3.graphics.beginFill("#007").drawRect(5, 0, 110, 80);
		spriteBMP3.y = 172;
		spriteBMP3.x = 0;

		var squareUp = new createjs.Shape();
		squareUp.graphics.beginFill("green").drawRect(0, 0, 200, 10);
		squareUp.x = 0;
		squareUp.y = 0;

		var upPressed = false;
		squareUp.on("mousedown", function(evt) {
			upPressed = true;
		});
		squareUp.on("pressup", function(evt) {
			upPressed = false;
		});
		
		
		function arrowTick(event) {
			if (upPressed) {
				if (spriteBMP.y == 4)
					return;
				spriteBMP.y -= 8;
				spriteBMP2.y -= 8;
				spriteBMP3.y -= 8;
			} else if (downPressed) {
				if (spriteBMP.y == moduleContainer.height)
					return;
				spriteBMP.y += 8;
				spriteBMP2.y += 8;
				spriteBMP3.y += 8;
			}
			moduleStage.update(event);
		}
		
		registTick(arrowTick);

		var squareDown = new createjs.Shape();
		squareDown.graphics.beginFill("green").drawRect(0, 0, 200, 10);
		squareDown.x = 0;
		squareDown.y = moduleContainer.height - 10;

		var downPressed = false;	
		squareDown.on("mousedown", function(evt) {
			downPressed = true;
		});
		squareDown.on("pressup", function(evt) {
			downPressed = false;
		});


		moduleNPCs
				.push(spriteBMP, spriteBMP2, spriteBMP3, squareUp, squareDown);

		for (i in moduleNPCs) {
			moduleContainer.addChild(moduleNPCs[i]);
		}

		moduleStage = new createjs.Stage("panel_canvas");
		// stage.autoClear = false;
		moduleStage.enableMouseOver();
		var moduleRect = new createjs.Rectangle(moduleContainer.x,
				moduleContainer.y, moduleContainer.width,
				moduleContainer.height);
		moduleStage.drawRect = moduleRect;

		moduleStage.addChild(moduleContainer);
	}

	function drawPanelUI() {

		moduleContainer.width = 120;
		moduleContainer.height = 300;
		moduleContainer.x = WIDTH - moduleContainer.width - 40;
		moduleContainer.y = HEIGHT - moduleContainer.height - 40;

		var moduleRect = new createjs.Rectangle(moduleContainer.x,
				moduleContainer.y, moduleContainer.width,
				moduleContainer.height);
		moduleStage.drawRect = moduleRect;

		moduleStage.update();
	}

	function init() {
		initBackground();
		initPanel();
		window.addEventListener('resize', onWindowResize, false);
		registeResize(onResize);
		onWindowResize();
		
		//give panel heart to beat
		createjs.Ticker.on("tick", tick);
	};

	return {
		init : init
	};

})();