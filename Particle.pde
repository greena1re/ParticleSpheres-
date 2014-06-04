class Particle extends VerletParticle{
boolean trailsVisible = false; 
boolean isActive = true; 
float strokeWeightp;
color colorp;
int index; 
int alphap;
Vec3D previousPosition;
Line3D line; 

float prevMag; 
  Particle(Vec3D pos, color colorp, int alphap, float strokew, boolean trails) {
    super(pos);
     this.colorp=colorp; 
     this.alphap =alphap; 
     this.strokeWeightp=strokew;
     index=physics.particles.size(); 
     this.trailsVisible= trails; 
  }
  Particle(Vec3D pos, color colorp, int alphap, float strokew) {
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




   void borders() {
     // if (this.x > DIM || this.x<-DIM || this.y>DIM  ||this.y< -DIM) {
     //  this.set(new Vec3D(random(100,100),random(100,100),random(100,100)))}


   }
   
  void die() {this.isActive=false;}

  void display() {
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