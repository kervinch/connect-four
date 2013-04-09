package connectfour.controller.ai.abstrakt;

import connectfour.model.Model;

public abstract class AbstractLimitedAi extends AbstractAi {

	protected double discountFactor = 0.95;
	
	public AbstractLimitedAi(Model board) {
		super(board);
	}
	
	public abstract void setLimit(Object limit);

}
