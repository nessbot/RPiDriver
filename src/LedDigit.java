/*
		  ----3----
		 /2/    /5/
		/ /    / /
	   ----6----
	  /1/   /4/
	 / /   / /
	----0----

 */
public interface LedDigit {

	public void set(int n) throws InterruptedException;

	public void set(char c) throws InterruptedException;

	public void spin(int n) throws InterruptedException;

	public void spin(int n, int t) throws InterruptedException;

	public void blink(int n, int t) throws InterruptedException;

	public void blink(int n) throws InterruptedException;

	public void clear() throws InterruptedException;

	public void write(String s) throws InterruptedException;

}

