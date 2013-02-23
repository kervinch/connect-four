package connectfour.controller.ai.abstrakt;

import connectfour.model.Model;

public abstract class AbstractLimitedAi extends AbstractAi {

	public AbstractLimitedAi(Model board) {
		super(board);
	}
	
	public abstract void setLimit(Object limit);

}
