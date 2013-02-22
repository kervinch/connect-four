package connectfour.common;

@Deprecated
public interface AsyncCallback<T> {

	void onSuccess(T result);
	
	void onFailure(Throwable caught);
	
}
