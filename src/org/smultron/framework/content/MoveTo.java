package org.smultron.framework.content;

import org.rspeer.networking.dax.walker.engine.definitions.WalkCondition;
import org.rspeer.networking.dax.walker.models.WalkState;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.PredefinedPath;
import org.rspeer.runetek.api.scene.Players;
import org.smultron.framework.Location;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;

import java.lang.reflect.Array;

/**
 * Moves to a {@Link Location}.
 * TODO add walkcondition
 */
public class MoveTo extends Task {

	private Location location;
	private int randomizeCenter;
	private WalkCondition walkCondition;

	public MoveTo(final Location location, int randomizeCenter, WalkCondition walkCondition) {
		super("Moving to " + location.locationName());
		this.walkCondition = walkCondition;
		this.location = location;
		this.randomizeCenter = randomizeCenter;
	}

	public MoveTo(final Location location, int randomizeCenter) {
		super("Moving to " + location.locationName());
		walkCondition = () -> false;
		this.location = location;
		this.randomizeCenter = randomizeCenter;
	}

	@Override
	public boolean validate() {
		return location.asArea().contains(Players.getLocal());
	}

	@Override
	public int execute() {

		WalkState state;
		if(randomizeCenter == 0) {
			// It seems randomize(0) still has like a one tile offset.
			// Not good for areas with one tile.
			state = Movement.getDaxWalker().walkTo(location.asPosition(), walkCondition);
		} else{
			state = Movement.getDaxWalker().walkTo(location.asPosition().randomize(randomizeCenter), walkCondition);
		}

		Time.sleepUntil(() -> !state.name().isEmpty(), 2500, 5000);
		if (state.equals(WalkState.FAILED) || state.equals(WalkState.ERROR)) {
			PredefinedPath predefinePath = PredefinedPath.build(Players.getLocal().getPosition(), location.asPosition());
			if (!predefinePath.walk()) {
				// Move around and hope DaxWalker can find a path from where we end up.
				PredefinedPath.build(Players.getLocal().getPosition(), Players.getLocal().getPosition().translate(20, 20)).walk();
			}
		}
		return 0;
	}


}
