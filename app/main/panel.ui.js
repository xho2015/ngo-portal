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


		//add tiles
		for (i = 0; i<3; i++)
		{
			var tile = new createjs.Shape(); 
			tile.graphics.beginFill("#10f").drawRect(5, 0, 110, 80);
			tile.y = i * 80 + 8;
			tile.x = 0;
			moduleNPCs.push(tile);
		}
		
		//up key
		var squareUp = new createjs.Shape();
		squareUp.graphics.beginFill("green").drawRect(0, 0, 200, 4);
		squareUp.x = 0;
		squareUp.y = 0;


		//populate childs
		for (j in moduleNPCs) {
			moduleContainer.addChild(moduleNPCs[j]);
		}
		moduleContainer.addChild(squareUp);

		//create stage
		moduleStage = new createjs.Stage("panel_canvas");
		moduleStage.addChild(moduleContainer);
		
		//inertia effects
		var m0 = md = 0, mu = 0, offset = 0; 
		moduleContainer.on("pressmove", function(evt) {
			m0 = m0 == 0 ? evt.stageY : m0;
			md = md == 0 ? evt.stageY : mu;
			mu = evt.stageY;	
			offset = mu - md;
			//console.log("md="+md+",mu="+mu+",offset="+offset)
			if ((moduleNPCs[0].y + offset) < 0 || (moduleNPCs[moduleNPCs.length - 1].y+offset + 20) > moduleContainer.height)
				{return;}
			
			for (i in moduleNPCs) {
				moduleNPCs[i].y += offset;
			}
			//console.log("evt.target.y="+evt.target.y+",evt.stageY="+evt.stageY)
			moduleStage.update();	
		});
		moduleContainer.on("pressup", function(evt) {
			var os = m0 > 0 ? evt.stageY - m0 : 0;
			console.log("up offset="+ os)
			m0 = 0;
			if (os > 0)
				inertiaEffect(os);
		});
		
		function inertiaEffect(os){
			var ab = Math.abs(os);
			nop = ab == os;
			strength = inertia.length - 1;		
		}
		
		var inertia = [16,16,15,14,12,8,4,4,2];
		var strength = 0;
		var nop = true;
		//createjs.Ticker.on("tick", tickInertia);
		createjs.Ticker.addEventListener("tick", inertiaTick)
		
		function inertiaTick(event)
		{		
			if (strength > 0){
				console.log("nop="+nop+",strength="+strength);
				if (nop===true){
					if (moduleNPCs[0].y + inertia[strength] + 20 > moduleContainer.height)
					{strength = 0;	return;}
					for (i in moduleNPCs) {
						moduleNPCs[i].y += inertia[strength];
					}
					moduleStage.update();
					strength--;
					//console.log("inertia down idx="+strength);
				} else {
					if (moduleNPCs[moduleNPCs.length-1].y - inertia[strength] < 0)
					{strength = 0;return;}
					for (i in moduleNPCs) {
						moduleNPCs[i].y -= inertia[strength];
					}
					moduleStage.update();
					strength--;
					//console.log("inertia up idx="+strength);
				}
			}
		}	
	}

	function drawPanelUI() {

		moduleContainer.width = 120;
		moduleContainer.height = 400;
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
		//createjs.Ticker.on("tick", tick);
	};

	return {
		init : init
	};

})();