package hw3;

public enum Act {
	INSERT, SEARCH, DELETE, UNKNOWN;
	
	public static Act getAct(int idx) {
		if (idx == 0)
			return INSERT;
		else if (idx == 1)
			return SEARCH;
		else if (idx == 2)
			return DELETE;
		else
			return UNKNOWN;
	}
}
