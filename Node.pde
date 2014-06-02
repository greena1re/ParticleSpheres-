class Node extends VerletParticle{

  Node(Vec3D pos) {
    super(pos);
  }

  // All we're doing really is adding a display() function to a VerletParticle
  void display() {
    fill(#0000AA);
    strokeWeight(5);
    //lights; 
    //specular; 
    //stroke(0);
   //sphere(20);
    point(this.x, this.y, this.z);
  }
}