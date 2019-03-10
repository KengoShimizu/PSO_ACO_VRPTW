/**
 * 
 */
/**
 * @author shimizukengo
 *
 */

public class Customer{
	int[] pos = new int[2];
	int demand;
	int ready_time;
	int due_time;
	int service_time;
	Customer(int x, int y){
		this.pos[0] = x;
		this.pos[1] = y;
	}
}
