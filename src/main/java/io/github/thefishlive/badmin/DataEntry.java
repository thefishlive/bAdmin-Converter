package io.github.thefishlive.badmin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataEntry {

	private final String name;
	private final String reason;
	private final String admin;
	private final long time;
	private final long temptime;
	private final int type;
	
}
