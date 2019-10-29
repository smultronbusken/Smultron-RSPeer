package org.smultron.framework;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;

public interface Location {
	public Position asPosition();

	public Area asArea();

	public String locationName();

	public static Location location(Area area, String name) {
		return new Location() {
			@Override
			public Position asPosition() {
				return area.getCenter();
			}

			@Override
			public Area asArea() {
				return area;
			}

			@Override
			public String locationName() {
				return name;
			}
		};
	}

	public static Location location(Area area) {
		return new Location() {
			@Override
			public Position asPosition() {
				return area.getCenter();
			}

			@Override
			public Area asArea() {
				return area;
			}

			@Override
			public String locationName() {
				return area.toString();
			}
		};
	}

	public static Location location(int minX, int minY, int maxX, int maxY, String name) {
		return new Location() {
			Area area = Area.rectangular(minX, minY, maxX, maxY);

			@Override
			public Position asPosition() {
				return area.getCenter();
			}

			@Override
			public Area asArea() {
				return area;
			}

			@Override
			public String locationName() {
				return name;
			}
		};
	}

	public static Location location(int minX, int minY, int maxX, int maxY) {
		return new Location() {
			Area area = Area.rectangular(minX, minY, maxX, maxY);

			@Override
			public Position asPosition() {
				return area.getCenter();
			}

			@Override
			public Area asArea() {
				return area;
			}

			@Override
			public String locationName() {
				return area.toString();
			}
		};
	}

	public static Location location(Position position) {
		return new Location() {
			@Override
			public Position asPosition() {
				return position;
			}

			@Override
			public Area asArea() {
				return Area.absolute(position);
			}

			@Override
			public String locationName() {
				return position.toString();
			}
		};
	}

	public static Location getHintArrow(final int xMinOffset, final int xMaxOffset,
										final int yMinOffset, final int yMaxOffset) {
		int xMin = Game.getClient().getHintArrowX() - xMinOffset;
		int xMax = Game.getClient().getHintArrowX() + xMaxOffset;
		int yMin = Game.getClient().getHintArrowY() - yMinOffset;
		int yMax = Game.getClient().getHintArrowY() + yMaxOffset;
		Area area = Area.rectangular(xMin, yMin, xMax, yMax);
		return Location.location(area, "the hint arrow");
	}

	public static Location getHintArrow() {
		int xMin = Game.getClient().getHintArrowX() - 2;
		int xMax = Game.getClient().getHintArrowX() + 2;
		int yMin = Game.getClient().getHintArrowY() - 2;
		int yMax = Game.getClient().getHintArrowY() + 2;
		Area area = Area.rectangular(xMin, yMin, xMax, yMax);
		return Location.location(area, "the hint arrow");
	}

}
