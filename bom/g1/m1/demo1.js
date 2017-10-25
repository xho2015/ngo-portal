var AppG1M1Demo1 = $.ngoModule(function() {
	
	var gui, params;
	var pannel;
	var offsetX = 20;
	var offsetY = 20;
	var container3d;
	var camera, scene, renderer;
	var plane, cube;
	var mouse, raycaster, isShiftDown = false;
	var rollOverMesh, rollOverMaterial;
	var cubeGeo, cubeMaterial;
	var objects = [];
	var WIDTH_3D, HEIGHT_3D;
	
	
	function init()
	{	
		if (!AppCommon.Detector.webgl) {
		    var warning = AppCommon.Detector.getWebGLErrorMessage();
		    alert(warning);
		    return;
		}
				
		initDatUI();
		init3d();
		
		onWindowResize();
		animate();
	};
	
	
	// gui sample https://github.com/dataarts/dat.gui/blob/master/example.html
	function initDatUI() {
		params = {
	    	width:600,
	    	height:500, 
	    	fullScreen:function() {
				fullscreen1(document.documentElement); 
	    	},
	    	exitfullScreen:function() {
				exitFullscreen(document.documentElement); 
	    	},
	    	hide3d:function() {
				container3d.style.visibility = 'hidden';      // Hide
				// element.style.visibility = 'visible'; // Show
	    	},
	    	show3d:function() {
				container3d.style.visibility = 'visible';     // Show
	    	},
	    	resize:function() {
	    		onResize();
	    	}
		};
		gui = new dat.gui.GUI({ autoPlace: false });
		gui.add(params, 'width').min(500).max(1902).step(10).listen();
		gui.add(params, 'height').min(400).max(1080).step(10).listen();
		gui.add(params, 'fullScreen');
		gui.add(params, 'exitfullScreen');
		gui.add(params, 'hide3d');
		gui.add(params, 'show3d');
		gui.add(params, 'resize');
		gui.domElement.id = 'gui';
	    //var gui_container = document.getElementById("gui_screen");
		//gui_container.appendChild(gui.domElement);
	};
	
	function fullscreen1(element) {
		if(element.requestFullscreen) {
		  element.requestFullscreen();
		 } else if(element.mozRequestFullScreen) {
		  element.mozRequestFullScreen();
		 } else if(element.webkitRequestFullscreen) {
		  element.webkitRequestFullscreen();
		 } else if(element.msRequestFullscreen) {
		  element.msRequestFullscreen();
		 }
	};
	
	function exitFullscreen() {
		 if(document.exitFullscreen) {
		  document.exitFullscreen();
		 } else if(document.mozCancelFullScreen) {
		  document.mozCancelFullScreen();
		 } else if(document.webkitExitFullscreen) {
		  document.webkitExitFullscreen();
		 }
	};

	
	
	function OnDown(e) {
		// tell the browser we're handling this mouse event
	        // e.preventDefault();
	        // e.stopPropagation();
	
	        clickOk = 1;
	
	        // get the current mouse position
	        var mx = parseInt(e.clientX - offsetX);
	        var my = parseInt(e.clientY - offsetY);
	
		// console.log("onDown x="+mx+",y="+my);
	};
	
	
	function OnUp(e) {
		// tell the browser we're handling this mouse event
	        // e.preventDefault();
	        // e.stopPropagation();
	
	        clickOk = 1;
	
	        // get the current mouse position
	        var mx = parseInt(e.clientX - offsetX);
	        var my = parseInt(e.clientY - offsetY);
	
		// console.log("OnUp x="+mx+",y="+my);
	};
	
	
	function OnMove(e) {
		// tell the browser we're handling this mouse event
	        // e.preventDefault();
	       // e.stopPropagation();
	
	        clickOk = 1;
	
	        // get the current mouse position
	        var mx = parseInt(e.clientX - offsetX);
	        var my = parseInt(e.clientY - offsetY);
	
		
	};
	
	function init3d() {
		container3d = document.getElementById( 'three_screen' );
		WIDTH_3D = container3d.clientWidth;
		HEIGHT_3D = container3d.clientHeight;
		// camera
		camera = new THREE.PerspectiveCamera( 50, WIDTH_3D / HEIGHT_3D, 1, 12000 );
		camera.position.set( 0, 1000, 1000 );
		camera.lookAt( new THREE.Vector3() );
		
		// scene
		scene = new THREE.Scene();
		// scene.background = new THREE.Color( 0x000000 );
	
		
	    // Axes
	    var axes = new THREE.AxisHelper(120);
	    scene.add(axes);
	
	
		// roll-over helpers
		var rollOverGeo = new THREE.BoxGeometry( 50, 50, 50 );
		rollOverMaterial = new THREE.MeshBasicMaterial( { color: 0xFf0000, opacity: 0.3, transparent: true } );
		rollOverMesh = new THREE.Mesh( rollOverGeo, rollOverMaterial );
		scene.add( rollOverMesh );
	
		// cubes
		cubeGeo = new THREE.BoxGeometry( 50, 50, 50 );
		cubeMaterial = new THREE.MeshLambertMaterial( { color: 0xfeb74c, map: new THREE.TextureLoader().load( "/app/res/square-outline-textured.ngpng" ) } );
		
		// grid
		var gridHelper = new THREE.GridHelper( 1000, 20 );
		scene.add( gridHelper );
		
		// raycaster
		raycaster = new THREE.Raycaster();
		mouse = new THREE.Vector2();
		var geometry = new THREE.PlaneBufferGeometry( 1000, 1000 );
		geometry.rotateX( - Math.PI / 2 );
		plane = new THREE.Mesh( geometry, new THREE.MeshBasicMaterial( { visible: false } ) );
		scene.add( plane );
		objects.push( plane );
	
		// Lights
		var ambientLight = new THREE.AmbientLight( 0xF0F0F0 );
		scene.add( ambientLight );
		var directionalLight = new THREE.DirectionalLight( 0xffffff );
		directionalLight.position.set( 1, 0.75, 0.5 ).normalize();
		scene.add( directionalLight );
	
		// render
		renderer = new THREE.WebGLRenderer( { antialias: true, alpha: true  } );
		renderer.setClearColor( 0x000000, 0 );
		renderer.setPixelRatio( window.devicePixelRatio );
		renderer.setSize(WIDTH_3D, HEIGHT_3D);
		container3d.appendChild( renderer.domElement );
	
		// event register
		container3d.addEventListener( 'mousemove', onDocumentMouseMove, false );
		container3d.addEventListener( 'mousedown', onDocumentMouseDown, false );
		container3d.addEventListener( 'keydown', onDocumentKeyDown, false );
		container3d.addEventListener( 'keyup', onDocumentKeyUp, false );
		
		window.addEventListener( 'resize', onWindowResize, false );
	};
	
	
	function onWindowResize() {
	
		params.width = window.innerWidth;;
		params.height = window.innerHeight;
	
		container3d = document.getElementById( 'three_screen' );
		WIDTH_3D = container3d.clientWidth;
		HEIGHT_3D = container3d.clientHeight;
	
		camera.aspect = WIDTH_3D / HEIGHT_3D;
		camera.updateProjectionMatrix();
		renderer.setSize( WIDTH_3D, HEIGHT_3D );
		animate();
	};
	
	
	function onDocumentMouseMove( event ) {
		event.preventDefault();
		mouse.set( ( (event.clientX - offsetX) / WIDTH_3D ) * 2 - 1, - ( (event.clientY - offsetY) / HEIGHT_3D ) * 2 + 1 );
		raycaster.setFromCamera( mouse, camera );
		var intersects = raycaster.intersectObjects( objects );
		if ( intersects.length > 0 ) {
			var intersect = intersects[ 0 ];
			rollOverMesh.position.copy( intersect.point ).add( intersect.face.normal );
			rollOverMesh.position.divideScalar( 50 ).floor().multiplyScalar( 50 ).addScalar( 25 );
		}
		animate();
	};
	
	function onDocumentMouseDown( event ) {
		event.preventDefault();
		mouse.set( ( (event.clientX - offsetX) / WIDTH_3D ) * 2 - 1, - ( (event.clientY - offsetY) / HEIGHT_3D ) * 2 + 1 );
		raycaster.setFromCamera( mouse, camera );
		var intersects = raycaster.intersectObjects( objects );
		if ( intersects.length > 0 ) {
			var intersect = intersects[ 0 ];
			// delete cube
			if ( isShiftDown ) {
				if ( intersect.object != plane ) {
					scene.remove( intersect.object );
					objects.splice( objects.indexOf( intersect.object ), 1 );
				}
			// create cube
			} else {
				var voxel = new THREE.Mesh( cubeGeo, cubeMaterial );
				voxel.position.copy( intersect.point ).add( intersect.face.normal );
				voxel.position.divideScalar( 50 ).floor().multiplyScalar( 50 ).addScalar( 25 );
				scene.add( voxel );
				objects.push( voxel );
			}
			animate();
		}
	};
	
	
	function onDocumentKeyDown( event ) {
		switch( event.keyCode ) {
			case 16: isShiftDown = true; break;
		}
	};
	
	
	function onDocumentKeyUp( event ) {
		switch ( event.keyCode ) {
			case 16: isShiftDown = false; break;
		}
	};
	
	
	function animate() {
		// requestAnimationFrame( animate );
		renderer.render( scene, camera );
	};
	
	
	return {
		init : init
	};

}());
