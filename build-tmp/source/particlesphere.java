import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.opengl.*; 
import heronarts.lx.*; 
import heronarts.lx.modulator.*; 
import toxi.physics.*; 
import toxi.physics.behaviors.*; 
import toxi.physics.constraints.*; 
import toxi.geom.*; 
import toxi.geom.mesh.*; 
import toxi.geom.Line3D; 
import toxi.math.*; 
import toxi.volume.*; 
import toxi.geom.*; 
import toxi.processing.*; 
import peasy.*; 
import controlP5.*; 
import java.util.Iterator; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class particlesphere extends PApplet {











//import toxi.geom.Vec3D.*; 


 


 
 

int NUM_PARTICLES = 1000;
int NUM_BOIDS = 500; 
int NUM_SPRINGS = 2000; 
int NUM_CONSTRAINTS = 7; 
int DEFAULT_COLOR = 0xff4400AA; 
int DEFAULT_STROKEWEIGHT=20; 
int NUM_COLORS=4; 
int BOID_SIZE = 4; 
int REST_LENGTH = 400; 
int DIM  = 1500; 
int sphereRadius = 600; 
float snapDist= 10*10; //minimum distance for dragging

//global booleans
boolean constraintsVisible = false; 
boolean boundsVisible = false; 
boolean particlesVisible = true; 
boolean framerateVisible = false; 
boolean flowFieldVisible = false; 

boolean displayOn = true; 
boolean constraintsOn = true; 
boolean attractorsOn = true; 


ToxiclibsSupport gfx; 
VerletPhysics physics, physics2; 
PeasyCam cam; 

//global arrays
ArrayList<AttractionBehavior> attractors = new ArrayList<AttractionBehavior>(); 
ArrayList<ParticleConstraint> constraints = new ArrayList<ParticleConstraint>(); 
ArrayList<ParticleConstraint> secondaryConstraints = new ArrayList<ParticleConstraint>(); 

ArrayList<ParticleConstraint> boundaryConstraints = new ArrayList<ParticleConstraint>(); 
ArrayList<Particle> particleList = new ArrayList<Particle>(); 
ArrayList<Vec3D> centers = new ArrayList<Vec3D>(); 
ArrayList<Boid> boids = new ArrayList<Boid>(); 

Matrix4x4 colorMatrix=new Matrix4x4().scale(255f/(DIM*2)).translate(DIM,DIM,DIM);

int[] particleColors = new int[NUM_COLORS]; 
Vec3D centerMove; 

VectorField flowField = new VectorField(30);
//HashMap<VerletParticle, Integer> particleColor = new HashMap<VerletParticle, Integer>(); 

SphereConstraint boundingSphere; 

GravityBehavior gravity; 

AttractionBehavior selected; 
AttractionBehavior repulse; 
AttractionBehavior attract0;
AttractionBehavior attract1; 
AttractionBehavior attract2;
AttractionBehavior attract3;
AttractionBehavior attract4;

//modulators
Click repulseClick;
SinLFO repulseRadius;

SinLFO[] centerMovements = new SinLFO[3];

SinLFO rgb; 




public void setup() { 
size(1500, 500, P3D);
//colorMode(HSB, 360, 100, 100, 100);	

cam = new PeasyCam(this, 400);

gfx = new ToxiclibsSupport(this);
physics = new VerletPhysics(); 
//physics2 = new VerletPhysics(); 
 

 
particleColors[0]=0xff0000FF; 
particleColors[1]=0xff1100FF;
particleColors[2]=0xff2200FF;

centerMovements[0]= new SinLFO(-300, 300, 10000); 
centerMovements[1]= new SinLFO(-300, 300, 7000); 
centerMovements[2]= new SinLFO(-300, 300, 6000); 

rgb = new SinLFO(0, 255, 2000);

initGUI();
initPhysics(); 



repulseClick = new Click(2000);
 repulseRadius = new SinLFO(0, 1000, 9000);
repulseClick.trigger();  
repulseRadius.trigger();
rgb.trigger(); 


 }

public void draw() {

background(0xff010101); 
gui(); 


 float x1off=0.f; 
 float y1off=0.f;
  float z1off=0.f;

if (repulseClick.click()) {
	println("repulseClick");
	repulse.setStrength(-50);
	repulse.setRadius(2000);
}

// float r =repulseRadius.getValuef();
// float s= -repulseRadius.getValuef();
// repulse.setStrength(s);
// repulse.setRadius(r);

// attract0.getAttractor().set(centers.get(0).x+centerMovements[0].getValuef(), centers.get(0).y+centerMovements[1].getValuef(), centers.get(0).z + centerMovements[2].getValuef());
// attract1.getAttractor().set(centers.get(1).x+centerMovements[0].getValuef(), centers.get(1).y+centerMovements[1].getValuef(), centers.get(1).z + centerMovements[2].getValuef());
// attract2.getAttractor().set(centers.get(2).x+centerMovements[0].getValuef(), centers.get(2).y+centerMovements[1].getValuef(), centers.get(2).z + centerMovements[2].getValuef());
// attract3.getAttractor().set(centers.get(3).x+centerMovements[0].getValuef(), centers.get(3).y+centerMovements[1].getValuef(), centers.get(3).z + centerMovements[2].getValuef());
// attract4.getAttractor().set(centers.get(4).x+centerMovements[0].getValuef(), centers.get(4).y+centerMovements[1].getValuef(), centers.get(4).z + centerMovements[2].getValuef());




//background(#0A0096);
physics.update(); 
updateParticles(); 
flowField.run(); 




if (boundsVisible) {
stroke(255, 120);
strokeWeight(1);
noFill();
box(physics.getWorldBounds().getExtent().x*2);
}

if (constraintsVisible) {
for (AttractionBehavior b : attractors)
      { 
strokeWeight(random(3,12));
stroke(0xffFFFFFF); 
gfx.point(b.getAttractor());
   }
  }  
 
   ambientLight(rgb.getValuef(), 0, rgb.getValuef()); 
   directionalLight(40, 0, 180, -1, -1, 1);
  spotLight(60, 0, 200, 1000, 500, 0, -1, -1, -1, 60, 2); 
 if (particlesVisible)
{   colorMode(RGB);
	for (Boid p : boids)  { 
    if (p.distanceToSquared(centers.get(0)) > 1000) {
      p.addForce(new Vec3D(-p.x, -p.y, -p.z).getNormalizedTo(2)); 
    }
   // flowField.applyForce(p);
	 p.run(boids); 
}
for (Particle particle : particleList) {
   strokeWeight(10);
  //float alphaP = map(particle.z, 0, 1500, 70, 100); 
	 // stroke(#0000FF, 100); 
   flowField.applyForce(particle); 
  //specular(50);
	// gfx.point((Vec3D)particle);
	

	 // if (abs(p.x)>2*width/3 || abs(p.y)> 2*height/3){
	 // 	p.isActive=false; 
	 // } 
	
	particle.display(); 
    }
}

if (flowFieldVisible){

   flowField.draw(); 

}



if  ( framerateVisible){
fill(255);
text("framerate:  " + frameRate, 20, 480);
println("framerate  : "  + frameRate); 
  }

}

public void keyPressed()  {

  if (key == CODED){
  int count = 0; 
  if (keyCode == UP) {  
      physics.removeConstraintFromAll(boundaryConstraints.get(count), physics.particles); 
      count++; 
    }
  if (key == DOWN){
       physics.addConstraintToAll(boundaryConstraints.get(count), physics.particles);
       count--; 
    }
  }
  
if (key == 'c')  {
      if (constraintsVisible) constraintsVisible = !constraintsVisible; 
      else if (!constraintsVisible) constraintsVisible = true; 
      ///println("constraintstoggle");
      }

if (key == 'w'){ 
     if (boundsVisible) boundsVisible = !boundsVisible;
      else if (!boundsVisible) boundsVisible = true; 
   	  }

if (key == 'p')  {
    if (particlesVisible) particlesVisible = !particlesVisible;
        else if (!particlesVisible) particlesVisible = true; 
      }

if (key == 'f')  {
    if (framerateVisible) framerateVisible = !framerateVisible;
        else if (!framerateVisible) framerateVisible = true; 
   }

if (key == 'v')   {
	if (flowFieldVisible) flowFieldVisible = !flowFieldVisible;
        else if (!flowFieldVisible) flowFieldVisible = true; 
   }

if (key == 'r')   {
	if (constraintsOn) constraintsOn = !constraintsOn;  //println("constraints:  ON"); 
		else if (!constraintsOn) constraintsOn = true; //println("constraints: OFF");  
   }


if (key == 'a')   {
	if (attractorsOn) {
		attractorsOn = !attractorsOn; 
		println("attractors: OFF");
	   for (ParticleBehavior p : physics.behaviors) { physics.removeBehavior(p); }
       }

      else if (!attractorsOn)  {
          attractorsOn = true;  
          println("attractors: ON");
         for (AttractionBehavior b: attractors) { physics.addBehavior(b);}    
    }
  }
}


public void mousePressed() {
  selected=null;
  Vec3D mousePos=new Vec3D(mouseX,mouseY, 0);
  // for(Iterator i=physics.behaviors.iterator(); i.hasNext();) {
  //   AttractionBehavior p= (AttractionBehavior)i.next();
 
  //   if (p.getAttractor().distanceToSquared(mousePos)<snapDist)  {
  //     selected=p;
    //   break;
    // }
 // }  //broken  because of multiple polymorphism on behaviors 
}

// only react to mouse dragging events if we have a selected attractor
public void mouseDragged() {
  if (selected!=null) {
    selected.setAttractor(new Vec3D((float)mouseX,(float)mouseY, (float)mouseX+mouseY)) ;
  }
}

// if we had a selected attractor unlock it again and kill reference
public void mouseReleased() {
  if (selected!=null) {
    selected=null;
  }
}



class Boid extends VerletParticle {

  Vec3D loc;
  Vec3D vel;
  Vec3D acc;
  float maxforce;
  float maxspeed;

  float neighborDist;
  float desiredSeparation;

  Boid(Vec3D l, float ms, float mf, float nd, float sep) {
    super(l);
    this.loc=l;
    this.acc = new Vec3D();
    this.vel = Vec3D.randomVector();
    this.maxspeed = ms;
    this.maxforce = mf;
    this.neighborDist=nd*nd;
    this.desiredSeparation=sep;
  }

  public void run(ArrayList boids) {
    flock(boids);
    update();
    borders();
    render();
  }

  // We accumulate a new acceleration each time based on three rules
  public void flock(ArrayList boids) {
    Vec3D sep = separate(boids);   // Separation
    Vec3D ali = align(boids);      // Alignment
    Vec3D coh = cohesion(boids);   // Cohesion
    // Arbitrarily weight these forces
    sep.scaleSelf(1.5f);
    ali.scaleSelf(1.0f);
    coh.scaleSelf(1.0f);
    // Add the force vectors to acceleration
    acc.addSelf(sep);
    acc.addSelf(ali);
    acc.addSelf(coh);
  }

  // Method to update location
  public void update() {
    // Update velocity
    vel.addSelf(acc);
    // Limit speed
    vel.limit(maxspeed);
    loc.addSelf(vel);
    // Reset accelertion to 0 each cycle
    acc.clear();
  }

  public void seek(Vec3D target) {
    acc.addSelf(steer(target,false));
  }

  public void arrive(Vec3D target) {
    acc.addSelf(steer(target,true));
  }


  // A method that calculates a steering vector towards a target
  // Takes a second argument, if true, it slows down as it approaches the target
  public Vec3D steer(Vec3D target, boolean slowdown) {
    Vec3D steer;  // The steering vector
    Vec3D desired = target.sub(loc);  // A vector pointing from the location to the target
    float d = desired.magnitude(); // Distance from the target is the magnitude of the vector
    // If the distance is greater than 0, calc steering (otherwise return zero vector)
    if (d > 0) {
      // Normalize desired
      desired.normalize();
      // Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
      if ((slowdown) && (d < 100.0f)) desired.scaleSelf(maxspeed*(d/100.0f)); // This damping is somewhat arbitrary
      else desired.scaleSelf(maxspeed);
      // Steering = Desired minus Velocity
      steer = desired.sub(vel).limit(maxforce);  // Limit to maximum steering force
    } 
    else {
      steer = new Vec3D();
    }
    return steer;
  }

  public void render() {
    // use the color matrix to transform position into RGB values 
    strokeWeight(10); 
    Vec3D col=colorMatrix.applyTo(loc);
    fill(col.x,col.y,col.z);
    stroke(0xff0000FF, 100); 
    gfx.point(loc); 
   // gfx.sphere(new Sphere(loc, BOID_SIZE), 5);
 //   gfx.cone(new Cone(loc,vel,0,BOID_SIZE,BOID_SIZE*4),5,false);
  }

  // Wraparound
  public void borders() {
    if (loc.x < -DIM) loc.x = DIM;
    if (loc.y < -DIM) loc.y = DIM;
    if (loc.z < -DIM) loc.z = DIM;
    if (loc.x > DIM) loc.x = -DIM;
    if (loc.y > DIM) loc.y = -DIM;
    if (loc.z > DIM) loc.z = -DIM;
  }

  // Separation
  // Method checks for nearby boids and steers away
  public Vec3D separate (ArrayList boids) {
    Vec3D steer = new Vec3D();
    int count = 0;
    // For every boid in the system, check if it's too close
    for (int i = boids.size()-1 ; i >= 0 ; i--) {
      Boid other = (Boid) boids.get(i);
      if (this != other) {
        float d = loc.distanceTo(other.loc);
        // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
        if (d < desiredSeparation) {
          // Calculate vector pointing away from neighbor
          Vec3D diff = loc.sub(other.loc);
          diff.normalizeTo(1.0f/d);
          steer.addSelf(diff);
          count++;
        }
      }
    }
    // Average -- divide by how many
    if (count > 0) {
      steer.scaleSelf(1.0f/count);
    }

    // As long as the vector is greater than 0
    if (steer.magSquared() > 0) {
      // Implement Reynolds: Steering = Desired - Velocity
      steer.normalizeTo(maxspeed);
      steer.subSelf(vel);
      steer.limit(maxforce);
    }
    return steer;
  }

  // Alignment
  // For every nearby boid in the system, calculate the average velocity
  public Vec3D align (ArrayList boids) {
    Vec3D steer = new Vec3D();
    int count = 0;
    for (int i = boids.size()-1 ; i >= 0 ; i--) {
      Boid other = (Boid) boids.get(i);
      if (this != other) {
        if (loc.distanceToSquared(other.loc) < neighborDist) {
          steer.addSelf(other.vel);
          count++;
        }
      }
    }
    if (count > 0) {
      steer.scaleSelf(1.0f/count);
    }

    // As long as the vector is greater than 0
    if (steer.magSquared() > 0) {
      // Implement Reynolds: Steering = Desired - Velocity
      steer.normalizeTo(maxspeed);
      steer.subSelf(vel);
      steer.limit(maxforce);
    }
    return steer;
  }

  // Cohesion
  // For the average location (i.e. center) of all nearby boids, calculate steering vector towards that location
  public Vec3D cohesion (ArrayList boids) {
    Vec3D sum = new Vec3D();   // Start with empty vector to accumulate all locations
    int count = 0;
    for (int i = boids.size()-1 ; i >= 0 ; i--) {
      Boid other = (Boid) boids.get(i);
      if (this != other) {
        if (loc.distanceToSquared(other.loc) < neighborDist) {
          sum.addSelf(other.loc); // Add location
          count++;
        }
      }
    }
    if (count > 0) {
      sum.scaleSelf(1.0f/count);
      return steer(sum,false);  // Steer towards the location
    }
    return sum;
  }
}

class Bolt {

//want to represent information / communication through some kind of electricity/ lightning effect between particles 


;}
// Flock class
// Does very little, simply manages the ArrayList of all the boids

class Flock {
  ArrayList boids; // An arraylist for all the boids

    Flock() {
    boids = new ArrayList(); // Initialize the arraylist
  }

  public void run() {
    for (int i = boids.size()-1 ; i >= 0 ; i--) {
      Boid b = (Boid) boids.get(i);  
      b.run(boids);  // Passing the entire list of boids to each boid individually
    }
  }

  public void addBoid(Boid b) {
    boids.add(b);
  }
}
class Node extends VerletParticle{

  Node(Vec3D pos) {
    super(pos);
  }

  // All we're doing really is adding a display() function to a VerletParticle
  public void display() {
    fill(0xff0000AA);
    strokeWeight(5);
    //lights; 
    //specular; 
    //stroke(0);
   //sphere(20);
    point(this.x, this.y, this.z);
  }
}
class Particle extends VerletParticle{
boolean trailsVisible = false; 
boolean isActive = true; 
float strokeWeightp;
int colorp;
int index; 
int alphap;
Vec3D previousPosition;
Line3D line; 

float prevMag; 
  Particle(Vec3D pos, int colorp, int alphap, float strokew, boolean trails) {
    super(pos);
     this.colorp=colorp; 
     this.alphap =alphap; 
     this.strokeWeightp=strokew;
     index=physics.particles.size(); 
     this.trailsVisible= trails; 
  }
  Particle(Vec3D pos, int colorp, int alphap, float strokew) {
    super(pos);
     this.colorp=colorp; 
     this.alphap =alphap; 
     this.strokeWeightp=strokew;
     index=physics.particles.size(); 
  }
   
  Particle(Vec3D pos) {
    super(pos);
     this.colorp=DEFAULT_COLOR; 
     this.strokeWeightp=DEFAULT_STROKEWEIGHT;
     this.alphap= 100; 
     index=physics.particles.size(); 
  }




   public void borders() {
     // if (this.x > DIM || this.x<-DIM || this.y>DIM  ||this.y< -DIM) {
     //  this.set(new Vec3D(random(100,100),random(100,100),random(100,100)))}


   }
   
  public void die() {this.isActive=false;}

  public void display() {
   if (isActive){
    
    if (!this.trailsVisible){
    borders(); 
    stroke(this.colorp,this.alphap);
    strokeWeight(this.strokeWeightp);
    gfx.point((Vec3D)this);
  }

  else   {  
      borders(); 


     this.previousPosition=this.getPreviousPosition();
     stroke(this.colorp, this.alphap);
     noStroke(); 
     strokeWeight(this.strokeWeightp);
     fill(this.colorp, this.alphap);
    //noStroke(); 
    smooth(); 
    pushMatrix(); 
    translate(this.x, this.y, this.z);
   //  gfx.sphere(new Sphere(strokeWeightp), 20);
     popMatrix();
     stroke(this.colorp, this.alphap);

     gfx.point((Vec3D)this);
     prevMag=this.previousPosition.magSquared();
     prevMag /= 10000;
     strokeWeight(prevMag*this.strokeWeightp/100);
     this.previousPosition.scale(prevMag/100);
     //line(this.x, this.y, this.z, this.previousPosition.x, this.previousPosition.y, this.previousPosition.z);
     //this.line= new Line3D((Vec3D) this, this.previousPosition);
      //gfx.line(this.line);
  //   gfx.point((Vec3D)this.sub(this.previousPosition).scale(.1) );

          }
        }
      else { noStroke(); }
      }
    }
ControlP5 ui;

public void initGUI() {

	fill(0xffFFFFFF); 
  ui = new ControlP5(this);
  ui.setAutoDraw(false);
  ui.addSlider("ConstraintSize",300,1200,sphereRadius,50,50,100,14).setLabel("ConstraintSize");
  ui.addSlider("Num particles",100,500,NUM_PARTICLES,50,150,100,14).setLabel("Num Particles");


   ui.addToggle("showPhysics",particlesVisible,20,60,14,14).setLabel("show particles");
  // ui.addToggle("isWireFrame",isWireFrame,20,100,14,14).setLabel("wireframe");
  // ui.addToggle("isClosed",isClosed,20,140,14,14).setLabel("closed mesh");
  // ui.addToggle("toggleBoundary",useBoundary,20,180,14,14).setLabel("use boundary");

  // ui.addBang("initPhysics",20,240,28,28).setLabel("restart");
}

public void gui() {
  hint(DISABLE_DEPTH_TEST);
  cam.beginHUD();
  ui.draw();
  cam.endHUD();
  hint(ENABLE_DEPTH_TEST);
}
class VectorField {

Vec3D[][][] vectors;
int resolution; 
int matrixSize;
private float xoff=0.f; 
private float yoff=0.f; 
private float zoff=0.f; 
private float theta; 
private float phi; 
private float magnitude; 

VectorField(int r){
	this.resolution = r; 
	matrixSize = DIM/resolution; 
	vectors = new Vec3D[matrixSize][matrixSize][matrixSize];
	run();
}

public void run() {  
	for (int i = 0; i < matrixSize; i++){
		xoff+=.01f;
	    for (int j=0; j<matrixSize; j++){
	    	yoff+=.01f;
	    	for (int k= 0; k<matrixSize; k++ ){
                  zoff+=.01f; 
                  // theta= map(noise(xoff,yoff, zoff),0,1, 0, TWO_PI) ; 
                  // magnitude=map(noise(xoff,yoff,zoff), 0,1, 0, .01);
                  // phi = map(noise(xoff, yoff, zoff), 0, 1, 0, PI); 
                  // vectors[i][j][k]= new Vec3D(magnitude*sin(phi)*cos(theta), magnitude*sin(phi)*cos(theta), magnitude*cos(phi) );
                   vectors[i][j][k]= new Vec3D(-j/(k+1), i/(k+1), 0).getNormalizedTo(.05f);
                  
	    	}
	    }
	}
}

public VerletParticle applyForce(VerletParticle p )  {
   int i = (int) constrain(p.x/resolution, 0, matrixSize-1 );
   int j = (int) constrain(p.y/resolution, 0, matrixSize-1 );
   int k = (int) constrain(p.z/resolution, 0, matrixSize-1 );

   return p.addForce(this.vectors[i][j][k]);
}




public void draw() {
 
for (int i = 1; i < matrixSize; i*=10){
	    for (int j=1; j<matrixSize; j*=10){
	    	for (int k= 1; k<matrixSize; k*=10 ){
	    		strokeWeight(3);
	    		stroke(0xffFFFFFF);
	    		float mag = vectors[i][j][k].magnitude();
                drawArrow(vectors[i][j][k], 10*mag, atan2(vectors[i][j][k].y , vectors[i][j][k].x) , acos(vectors[i][j][k].x/ mag) );
        }
      } 
    }  
  }
public void drawArrow(Vec3D fieldLine, float len, float theta, float phi)  {
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


public void initPhysics()  {


//physics.setWorldBounds(new AABB(new Vec3D(),new Vec3D(DIM,DIM,DIM)));
//physics2.setWorldBounds(new AABB(new Vec3D(), new Vec3D(DIM, DIM, DIM))); 
gravity = new GravityBehavior(new Vec3D(0, 1, 0)); 

centerMovements[0].trigger();
centerMovements[1].trigger();
centerMovements[2].trigger();
centerMove = new Vec3D(centerMovements[0].getValuef(), centerMovements[1].getValuef(), centerMovements[2].getValuef());
centers.add(new Vec3D(0, 0, 0)); 
centers.add(new Vec3D(-1500, -500 , -50));
centers.add(new Vec3D(1500, 500, -300));
centers.add(new Vec3D(600, 400 , -1200));
centers.add(new Vec3D(2000, 700, -600));
centers.add(new Vec3D(-2000, -700,  -500));
centers.add(new Vec3D(-600, 700, -800));

repulse = new AttractionBehavior(new Vec3D(0,0,0), 300,  -.1f, .2f);
// attract0 = new AttractionBehavior(centers.get(0).add(centerMove), 1000, .1);
// attract1 = new AttractionBehavior(centers.get(1).add(centerMove), 1000, .05);
// attract2 = new AttractionBehavior(centers.get(2).add(centerMove), 1000, .1);
// attract3 = new AttractionBehavior(centers.get(3).add(centerMove), 1000, .1);
// attract4 = new AttractionBehavior(centers.get(4).add(centerMove), 300, .1);

physics.addBehavior(gravity); 
physics.addBehavior(repulse);
// physics.addBehavior(attract0);
// physics.addBehavior(attract1);
// physics.addBehavior(attract2);
// physics.addBehavior(attract3);
// physics.addBehavior(attract4);

for (int i = 0; i < centers.size(); i++){
if (i < 5){
  constraints.add(new SphereConstraint(centers.get(i), 100, SphereConstraint.OUTSIDE));

 }
 else {
   secondaryConstraints.add(new SphereConstraint(centers.get(i), 40, SphereConstraint.OUTSIDE));
   physics.addBehavior(new AttractionBehavior(centers.get(i), 300, .1f, .05f));
   physics.addBehavior(new AttractionBehavior(centers.get(i).add(centerMove) , 400, .1f, .05f ) );  
   physics.addBehavior(new AttractionBehavior( (centers.get(i).add(new Vec3D(random(-100,100), random(-100,100), random(-100,100)))).getNormalizedTo(random(100, 300) ), 300, .1f, .1f) );

     }
}

 // physics.addConstraintToAll(world, physics.particles);
//this is a bit hacky-- we really just want the attraction behaviors added to the arraylist
// for ( AttractionBehavior b : physics.behaviors){
//  attractors.add(b);
//        }  

particleList = new ArrayList<Particle>(); 
  float xoff = 0; float yoff = 0; float zoff = 0; 
for (int i = 0; i<NUM_PARTICLES; i++){
       if (i<NUM_PARTICLES/4) {
       particleList.add(new Particle(new Vec3D(centers.get(0).x+ random(0, 1000), centers.get(0).y + random(0,500), centers.get(0).z + random(0,-200) ),color(0xff0000FF),  (int)random(50,100), random(10, 11) ) ); 
        for (ParticleConstraint constraint : constraints) particleList.get(i).addConstraint(constraint);
       }
      else if  (i < NUM_PARTICLES/2) {
     particleList.add(new Particle( new Vec3D(centers.get(1).x+ random(-200,200 ), centers.get(1).y + random(-200,200), centers.get(1).z + random(-200,200) ))) ;
       for (ParticleConstraint constraint : constraints) particleList.get(i).addConstraint(constraint);
     }
      else if  (i < 2*NUM_PARTICLES/3) {
     particleList.add(new Particle( new Vec3D(centers.get(2).x+ random(-200,200 ), centers.get(2).y + random(-200,200), centers.get(2).z + random(-200,200) ))) ;
              for (ParticleConstraint constraint : constraints) particleList.get(i).addConstraint(constraint);
     }
      else if (i <4*NUM_PARTICLES/5) { 
     particleList.add(new Particle( new Vec3D(centers.get(5).x + random(-200,200), centers.get(5).y + random(-200,200), centers.get(5).z + random(-200,200)).getNormalizedTo(random(20, 80) )));
     particleList.get(i).addConstraint(secondaryConstraints.get(0));
   ;  }
     else { 
     particleList.add(new Particle( new Vec3D(centers.get(6).x + random(-200,200), centers.get(6).y + random(-200,200), centers.get(6).z + random(-200,200)).getNormalizedTo(random(20, 80) )));
     particleList.get(i).addConstraint(secondaryConstraints.get(1)); 
   ;  }
  


  };  

  for (int i =0; i < NUM_BOIDS ; i++ ) {
  // boids.add(new Boid(new Vec3D(random(-300,300), random(-300,300), random(-400, 0) ), 10, 5, 25, 50)); 
  }
// for (Boid b : boids) {
//   	physics.addParticle((VerletParticle) b); 
//   }



for ( Particle particle : particleList) {
physics.addParticle((VerletParticle) particle);
particle.trailsVisible= true; 
}

boundaryConstraints.add(new SphereConstraint(centers.get(0), sphereRadius, SphereConstraint.INSIDE));
boundaryConstraints.add(new SphereConstraint(centers.get(1), sphereRadius+100, SphereConstraint.INSIDE));
boundaryConstraints.add(new SphereConstraint(centers.get(2), sphereRadius+200, SphereConstraint.INSIDE));
boundaryConstraints.add(new SphereConstraint(centers.get(3), sphereRadius+300, SphereConstraint.INSIDE));
boundaryConstraints.add(new SphereConstraint(centers.get(4), sphereRadius , SphereConstraint.INSIDE));
boundaryConstraints.add(new SphereConstraint(centers.get(5), sphereRadius , SphereConstraint.INSIDE));
boundaryConstraints.add(new SphereConstraint(centers.get(6), sphereRadius-100, SphereConstraint.INSIDE));
//boundaryConstraints.add(new SphereConstraint(centers.get(7), sphereRadius, SphereConstraint.INSIDE));

//constraints.add(boundingSphere);
//physics.addConstraintToAll(boundaryConstraints.get(0), physics.particles); 

for (ParticleConstraint p : constraints) {
	physics.addConstraintToAll(p, physics.particles);
}

   }




 public void updateParticles() {
  Vec3D grav=Vec3D.Y_AXIS.copy();
  grav.rotateX(mouseY*0.01f);
  grav.rotateY(mouseX*0.01f);
  gravity.setForce(grav.scaleSelf(2));
  int numP=physics.particles.size();

  for (int j = 0 ; j<boundaryConstraints.size(); j++){
      
  if (random(1)<0.8f && numP<NUM_SPRINGS) {
     Particle p=new Particle(new Vec3D(centers.get(j).x*random(-1,1)*20,centers.get(j).y,centers.get(j).z*random(-1,1)*20));
     p.addConstraint(boundaryConstraints.get(j));
     physics.addParticle(p);
     particleList.add(p); 
  // }
  if (numP>10 && physics.springs.size()<1400) {
    for(int i=0; i<60; i++) {
      if (random(1)<0.04f) {
        Particle q= (Particle) physics.particles.get((int)random(numP));
        Particle r=q;
        while(q==r) {
          r=(Particle) physics.particles.get((int)random(numP));
        }
        physics.addSpring(new VerletSpring(q,r,REST_LENGTH, 0.0002f));
      }
    }
  }
  float len=(float)numP/NUM_PARTICLES*REST_LENGTH;
  for(Iterator i=physics.springs.iterator(); i.hasNext();) {
    VerletSpring s=(VerletSpring)i.next();
    s.setRestLength(random(0.9f,1.1f)*len);
          }
  //physics.update();
       }
      } 
    }
 
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "particlesphere" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
