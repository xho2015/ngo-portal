/**
 * NGO UI library - H5 canvas based interface 
 */


var AppMainUI = (function() {	
	
	var animloop = [];
	
	function joinloop(action)
	{
		animloop.push(action);
		console.log("animation loop joined");
	}
	
	function init(canvasId)
	{
		canvas = document.getElementById(canvasId);
		ctx = canvas.getContext("2d");
		
		console.log("render App Main UI, canvas id="+canvas.id);

		BB = canvas.getBoundingClientRect();
		offsetX = BB.left;
		offsetY = BB.top;
		WIDTH = canvas.width;
		HEIGHT = canvas.height;
		
		// listen for mouse events
		canvas.onmousedown = myDown;
		canvas.onmouseup = myUp;
		canvas.onmousemove = myMove;

		animloop.push(drawMainUI);
		
		AppCommon.limitLoop(animation, 40);
	};
	
	
	var canvas;
	var ctx ;
	var BB;
	var offsetX;
	var offsetY;
	var WIDTH;
	var HEIGHT;

	// drag related variables
	var dragok = false;
	var startX;
	var startY;

	// an array of objects that define different rectangles
	var rects = [];
	rects.push({
	    x: 475,
	    y: 50 - 15,
	    width: 120,
	    height: 60,
	    fill: "#040F44",
	    isDragging: false
	});
	
	function animation()
	{
		clear();
		for (var i = 0; i < animloop.length; i++) {
	        animloop[i]();      
	    }
	}

	// draw a single rect
	function rect(x, y, w, h) {
	    ctx.beginPath();
	    ctx.rect(x, y, w, h);
	    ctx.closePath();
	    ctx.fill();
	}

	// clear the canvas
	function clear() {
	    ctx.clearRect(0, 0, WIDTH, HEIGHT);
	}

	// redraw the scene
	function drawMainUI() {
	    // redraw each rect in the rects[] array
	    for (var i = 0; i < rects.length; i++) {
	        var r = rects[i];
	        ctx.fillStyle = r.fill;
	        rect(r.x, r.y, r.width, r.height);
	    }
	}


	// handle mousedown events
	function myDown(e) {

	    // tell the browser we're handling this mouse event
	    e.preventDefault();
	    e.stopPropagation();

	    // get the current mouse position
	    var mx = parseInt(e.clientX - offsetX);
	    var my = parseInt(e.clientY - offsetY);
	    
	    console.log("main.ui myDown  x="+mx+",y="+my);

	    // test each rect to see if mouse is inside
	    dragok = false;
	    for (var i = 0; i < rects.length; i++) {
	        var r = rects[i];
	        if (mx > r.x && mx < r.x + r.width && my > r.y && my < r.y + r.height) {
	            // if yes, set that rects isDragging=true
	            dragok = true;
	            r.isDragging = true;
	        }
	    }
	    // save the current mouse position
	    startX = mx;
	    startY = my;
	}


	// handle mouseup events
	function myUp(e) {  
	    // tell the browser we're handling this mouse event
	    e.preventDefault();
	    e.stopPropagation();

	    // clear all the dragging flags
	    dragok = false;
	    for (var i = 0; i < rects.length; i++) {
	        rects[i].isDragging = false;
	    }
	}


	// handle mouse moves
	function myMove(e) {
	    // if we're dragging anything...
	    if (dragok) {

	        // tell the browser we're handling this mouse event
	        e.preventDefault();
	        e.stopPropagation();

	        // get the current mouse position
	        var mx = parseInt(e.clientX - offsetX);
	        var my = parseInt(e.clientY - offsetY);
	        
	        console.log("main.ui myMove  x="+mx+",y="+my);

	        // calculate the distance the mouse has moved
	        // since the last mousemove
	        var dx = mx - startX;
	        var dy = my - startY;

	        // move each rect that isDragging 
	        // by the distance the mouse has moved
	        // since the last mousemove
	        for (var i = 0; i < rects.length; i++) {
	            var r = rects[i];
	            if (r.isDragging) {
	                r.x += dx;
	                r.y += dy;
	            }
	        }

	        // redraw the scene with the new rect positions
	        //drawUI();

	        // reset the starting mouse position for the next mousemove
	        startX = mx;
	        startY = my;

	    }
	}
	
	
	return {
		init : init,
		joinloop : joinloop
	};
	
})();