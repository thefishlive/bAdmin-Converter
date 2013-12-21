package io.github.thefishlive.badmin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BanTypeMapping {

	BAN(0, 0),
	TEMPBAN(0, 1), // same id but has a 'temptime'
	WARNING(2, 2),
	OFFLINE_WARNING(3, 2), // might as well resolve to the same
	UNKNOWN(-1, -1);
	
	@Getter private final int oldMapping;
	@Getter private final int newMapping;

	public static BanTypeMapping getTypeId(DataEntry entry) {
		switch (entry.getType()) {
			case 0:
				if (entry.getTemptime() != 0) {
					return TEMPBAN;
				} else {
					return BAN;
				}
			case 1:
				return UNKNOWN;
			case 2:
				return WARNING;
			case 3:
				return OFFLINE_WARNING;
			default:
				return UNKNOWN;
		}
	}
}
