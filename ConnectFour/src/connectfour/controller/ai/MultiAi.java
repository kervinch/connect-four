package connectfour.controller.ai;

import java.util.HashMap;
import java.util.Map;

import connectfour.controller.ai.abstr.AbstractAi;
import connectfour.controller.ai.sub.DetDepthLimitedAi;
import connectfour.controller.ai.sub.DetTimeLimitedAi;
import connectfour.controller.ai.sub.RandDepthLimitedAi;
import connectfour.controller.ai.sub.RandTimeLimitedAi;
import connectfour.controller.ai.util.Ai;
import connectfour.model.GameBoard;

public class MultiAi implements Ai {
	
	private enum AiType {
		DET_TIME, DET_DEPTH, RAND_TIME, RAND_DEPTH
	}
	
	private AbstractAi detDepthLimitedAi;
	private AbstractAi detTimeLimitedAi;
	private AbstractAi randDepthLimitedAi;
	private AbstractAi randTimeLimitedAi;
	
	private boolean deterministic;
	private boolean timeLimited;
	private Map<AiType, AbstractAi> aiTypeToInstanceMap = new HashMap<AiType, AbstractAi>();
	
	public MultiAi(GameBoard board, boolean INIT_DET_AI, boolean INIT_TIME_LIMITED,
			long INIT_TIME_LIMIT, int INIT_DEPTH_LIMIT) {
		this.detDepthLimitedAi = new DetDepthLimitedAi(board);
		this.detTimeLimitedAi = new DetTimeLimitedAi(board);
		this.randDepthLimitedAi = new RandDepthLimitedAi(board);
		this.randTimeLimitedAi = new RandTimeLimitedAi(board);
		aiTypeToInstanceMap.put(AiType.DET_DEPTH, detDepthLimitedAi);
		aiTypeToInstanceMap.put(AiType.DET_TIME, detTimeLimitedAi);
		aiTypeToInstanceMap.put(AiType.RAND_DEPTH, randDepthLimitedAi);
		aiTypeToInstanceMap.put(AiType.RAND_TIME, randTimeLimitedAi);
		this.setDeterministic(INIT_DET_AI);
		this.setTimeLimited(INIT_TIME_LIMITED);
		this.setTimeLimit(INIT_TIME_LIMIT);
		this.setDepthLimit(INIT_DEPTH_LIMIT);
	}
	
	public int move() {
		return resolveAi().move();
	}
	
	private AbstractAi resolveAi() {
		return aiTypeToInstanceMap.get(resolveAiType());
	}
	
	private AiType resolveAiType() {
		if (!deterministic && !timeLimited) {
			return AiType.RAND_DEPTH;
		} else if (!deterministic && timeLimited) {
			return AiType.RAND_TIME;
		} else if (deterministic && !timeLimited) {
			return AiType.DET_DEPTH;
		} else {
			return AiType.DET_TIME;
		}
	}
	
	public void setDeterministic(boolean deterministic) {
		this.deterministic = deterministic;
	}
	
	public void setTimeLimited(boolean timeLimited) {
		this.timeLimited = timeLimited;
	}
	
	public void setDepthLimit(int depthLimit) {
		this.detDepthLimitedAi.setLimit(depthLimit);
		this.randDepthLimitedAi.setLimit(depthLimit);
	}
	
	public void setTimeLimit(long timeLimit) {
		this.detTimeLimitedAi.setLimit(timeLimit);
		this.randTimeLimitedAi.setLimit(timeLimit);
	}
	
	public void stop() {
		resolveAi().stop();
	}

}
