class Particle extends VerletParticle{
float strokeWeightp;
color colorp;
int index; 
int alphap;

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
     this.alphap= 50; 
     index=physics.particles.size(); 
  }



  void display() {
    strokeWeight(this.strokeWeightp);
    //lights; 
    //specular; 
    stroke(this.colorp,this.alphap);
 
    gfx.point((Vec3D)this);
  }
}