package connectfour.controller.ai;

import connectfour.controller.ai.abstrakt.AbstractLimitedAi;
import connectfour.controller.ai.sub.DetDepthLimitedAi;
import connectfour.controller.ai.sub.DetTimeLimitedAi;
import connectfour.controller.ai.sub.RandDepthLimitedAi;
import connectfour.controller.ai.sub.RandTimeLimitedAi;
import connectfour.model.Model;

public class MultiAiImpl implements MultiAi {

	private AbstractLimitedAi detDepthLimitedAi;
	private AbstractLimitedAi detTimeLimitedAi;
	private AbstractLimitedAi randDepthLimitedAi;
	private AbstractLimitedAi randTimeLimitedAi;

	private Ai resolvedAi;

	private boolean deterministic;
	private boolean timeLimited;

	public MultiAiImpl(Model board, boolean INIT_DET_AI,
			boolean INIT_TIME_LIMITED, long INIT_TIME_LIMIT,
			int INIT_DEPTH_LIMIT) {
		this.detDepthLimitedAi = new DetDepthLimitedAi(board);
		this.detTimeLimitedAi = new DetTimeLimitedAi(board);
		this.randDepthLimitedAi = new RandDepthLimitedAi(board);
		this.randTimeLimitedAi = new RandTimeLimitedAi(board);
		this.setDeterministic(INIT_DET_AI);
		this.setTimeLimited(INIT_TIME_LIMITED);
		this.setTimeLimit(INIT_TIME_LIMIT);
		this.setDepthLimit(INIT_DEPTH_LIMIT);
	}

	public int move() {
		resolvedAi = resolveAi();
		return resolvedAi.move();
	}

	private Ai resolveAi() {
		if (!deterministic && !timeLimited) {
			return randDepthLimitedAi;
		} else if (!deterministic && timeLimited) {
			return randTimeLimitedAi;
		} else if (deterministic && !timeLimited) {
			return detDepthLimitedAi;
		} else {
			return detTimeLimitedAi;
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
		resolvedAi.stop();
	}

}
