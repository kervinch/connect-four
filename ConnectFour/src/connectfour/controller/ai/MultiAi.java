package connectfour.controller.ai;

public interface MultiAi extends Ai {

	void setDeterministic(boolean deterministic);
	
	void setTimeLimited(boolean timeLimited);
	
	void setTimeLimit(long timeLimit);
	
	void setDepthLimit(int depthLimit);
	
}
