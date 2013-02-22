package connectfour.view;

public interface View {

	void onReset();
	
	void onUndo();
	
	void onCounterPlaced();
	
	void onComputerStartMove();
	
	void onComputerEndMove();
	
	void onHumanStartMove();
	
	void onHumanEndMove();
	
}
