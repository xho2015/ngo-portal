
var container;
var camera, scene, renderer;
var plane, cube;
var mouse, raycaster, isShiftDown = false;
var rollOverMesh, rollOverMaterial;
var cubeGeo, cubeMaterial;
var objects = [];

init();

animate();

function init() {
	container = document.createElement( 'div' );
	document.body.appendChild( container );
	
	//camera
	camera = new THREE.PerspectiveCamera( 50, window.innerWidth / window.innerHeight, 1, 12000 );
	camera.position.set( 0, 1000, 1000 );
	camera.lookAt( new THREE.Vector3() );
	
	//scene
	scene = new THREE.Scene();
	scene.background = new THREE.Color( 0x000000 );


	//skydome
	var skyGeo = new THREE.SphereGeometry(100000, 25, 25); 
	var loader  = new THREE.TextureLoader(), texturesky = loader.load( "skydome1.jpg" );
	var material = new THREE.MeshPhongMaterial({ 
	        map: texturesky,
	});
	var sky = new THREE.Mesh(skyGeo, material);
    sky.material.side = THREE.BackSide;
    //scene.add(sky);
	
    //Axes
    var axes = new THREE.AxisHelper(120);
    scene.add(axes);


	// roll-over helpers
	var rollOverGeo = new THREE.BoxGeometry( 50, 50, 50 );
	rollOverMaterial = new THREE.MeshBasicMaterial( { color: 0xFf0000, opacity: 0.3, transparent: true } );
	rollOverMesh = new THREE.Mesh( rollOverGeo, rollOverMaterial );
	scene.add( rollOverMesh );

	// cubes
	cubeGeo = new THREE.BoxGeometry( 50, 50, 50 );
	cubeMaterial = new THREE.MeshLambertMaterial( { color: 0xfeb74c, map: new THREE.TextureLoader().load( "https://threejs.org/examples/textures/square-outline-textured.png" ) } );
	
	// grid
	var gridHelper = new THREE.GridHelper( 1000, 20 );
	scene.add( gridHelper );
	
	//raycaster
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

	//render
	renderer = new THREE.WebGLRenderer( { antialias: true } );
	renderer.setPixelRatio( window.devicePixelRatio );
	renderer.setSize( window.innerWidth, window.innerHeight );
	container.appendChild( renderer.domElement );

	//event register
	document.addEventListener( 'mousemove', onDocumentMouseMove, false );
	document.addEventListener( 'mousedown', onDocumentMouseDown, false );
	document.addEventListener( 'keydown', onDocumentKeyDown, false );
	document.addEventListener( 'keyup', onDocumentKeyUp, false );
	window.addEventListener( 'resize', onWindowResize, false );
}

function onWindowResize() {
	camera.aspect = window.innerWidth / window.innerHeight;
	camera.updateProjectionMatrix();
	renderer.setSize( window.innerWidth, window.innerHeight );
}

function onDocumentMouseMove( event ) {
	event.preventDefault();
	mouse.set( ( event.clientX / window.innerWidth ) * 2 - 1, - ( event.clientY / window.innerHeight ) * 2 + 1 );
	raycaster.setFromCamera( mouse, camera );
	var intersects = raycaster.intersectObjects( objects );
	if ( intersects.length > 0 ) {
		var intersect = intersects[ 0 ];
		rollOverMesh.position.copy( intersect.point ).add( intersect.face.normal );
		rollOverMesh.position.divideScalar( 50 ).floor().multiplyScalar( 50 ).addScalar( 25 );
	}
	animate();
}

function onDocumentMouseDown( event ) {
	event.preventDefault();
	mouse.set( ( event.clientX / window.innerWidth ) * 2 - 1, - ( event.clientY / window.innerHeight ) * 2 + 1 );
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
}

function onDocumentKeyDown( event ) {
	switch( event.keyCode ) {
		case 16: isShiftDown = true; break;
	}
}

function onDocumentKeyUp( event ) {
	switch ( event.keyCode ) {
		case 16: isShiftDown = false; break;
	}
}

function animate() {
	//requestAnimationFrame( animate );
	renderer.render( scene, camera );
}
