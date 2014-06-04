class VectorField {

Vec3D[][][] vectors;
int resolution; 
int matrixSize;
private float xoff=0.; 
private float yoff=0.; 
private float zoff=0.; 
private float theta; 
private float phi; 
private float magnitude; 

VectorField(int r){
	this.resolution = r; 
	matrixSize = DIM/resolution; 
	vectors = new Vec3D[matrixSize][matrixSize][matrixSize];
	run();
}

void run() {  
	for (int i = 0; i < matrixSize; i++){
		xoff+=.01;
	    for (int j=0; j<matrixSize; j++){
	    	yoff+=.01;
	    	for (int k= 0; k<matrixSize; k++ ){
                  zoff+=.01; 
                  // theta= map(noise(xoff,yoff, zoff),0,1, 0, TWO_PI) ; 
                  // magnitude=map(noise(xoff,yoff,zoff), 0,1, 0, .01);
                  // phi = map(noise(xoff, yoff, zoff), 0, 1, 0, PI); 
                  // vectors[i][j][k]= new Vec3D(magnitude*sin(phi)*cos(theta), magnitude*sin(phi)*cos(theta), magnitude*cos(phi) );
                   vectors[i][j][k]= new Vec3D(-j/(k+1), i/(k+1), 0).getNormalizedTo(.05);
                  
	    	}
	    }
	}
}

VerletParticle applyForce(VerletParticle p )  {
   int i = (int) constrain(p.x/resolution, 0, matrixSize-1 );
   int j = (int) constrain(p.y/resolution, 0, matrixSize-1 );
   int k = (int) constrain(p.z/resolution, 0, matrixSize-1 );

   return p.addForce(this.vectors[i][j][k]);
}




void draw() {
 
for (int i = 1; i < matrixSize; i*=10){
	    for (int j=1; j<matrixSize; j*=10){
	    	for (int k= 1; k<matrixSize; k*=10 ){
	    		strokeWeight(3);
	    		stroke(#FFFFFF);
	    		float mag = vectors[i][j][k].magnitude();
                drawArrow(vectors[i][j][k], 10*mag, atan2(vectors[i][j][k].y , vectors[i][j][k].x) , acos(vectors[i][j][k].x/ mag) );
        }
      } 
    }  
  }
void drawArrow(Vec3D fieldLine, float len, float theta, float phi)  {
  pushMatrix();
  translate(fieldLine.x, fieldLine.y, fieldLine.z);
  rotateZ(theta);
  rotateX(phi);
  line(0,0,0, 0, len, 0);
  line(len, 0, 0, 0, len - 8, -8);
  line(len, 0,0, 0, len - 8, 8);
  popMatrix();
  }

}


