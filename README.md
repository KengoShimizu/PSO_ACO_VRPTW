# PSO_ACO_VRPTW
This code is written in JAVA, and this Particle Swarm Optimization (PSO) - Ant Colony Optimization (ACO) method (PSO_ACO) is written for solving VRPTW.  
To solve VRPTW, this code loads a txt file from [Solomon benchmark](https://www.sintef.no/projectweb/top/vrptw/solomon-benchmark/).

&emsp; Procedure of this PSO_ACO (**Ant_colony_opt.java**):  
&emsp; &emsp; construct initial solutions using Nearest Neighbor method (**Initial_pop.java**)  
&emsp; &emsp; for until meet stopping criteria:  
&emsp; &emsp; &emsp; construct solutions (**Build_tour.java**)  
&emsp; &emsp; &emsp; parameter update (**Particle_swarm.java**)  

In this method, beta (weight of heuristic information) and q0 (probabilith of choosing next customer) were picked to update in PSO.
