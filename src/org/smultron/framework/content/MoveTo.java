package org.smultron.framework.content;

import org.rspeer.networking.dax.walker.models.WalkState;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.path.PredefinedPath;
import org.rspeer.runetek.api.scene.Players;
import org.smultron.framework.Location;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;

/**
 * Moves to a {@Link Location}.
 * TODO add walkcondition
 */
public class MoveTo extends Task
{

    private Location location;
    private int randomizeCenter;

    /**
     *
     * @param listener
     * @param location
     * @param randomizeCenter
     */
    public MoveTo(final TaskListener listener, final Location location, int randomizeCenter) {
	super(listener, "Moving to " + location.locationName());
	this.location = location;
	this.randomizeCenter = randomizeCenter;
    }

    /**
     *
     * @param location
     * @param randomizeCenter
     */
    public MoveTo(final Location location, int randomizeCenter) {
	super("Moving to " + location.locationName());
	this.location = location;
	this.randomizeCenter = randomizeCenter;
    }

    @Override public boolean validate() {
	return location.asArea().contains(Players.getLocal());
    }

    @Override public int execute() {
	WalkState state = Movement.getDaxWalker().walkTo(location.asPosition().randomize(randomizeCenter));
	Time.sleepUntil(() -> !state.name().isEmpty(), 2500, 5000);
	if(state.equals(WalkState.FAILED) || state.equals(WalkState.ERROR)) {
	    // The webwalker could not find a path.
	    PredefinedPath predefinePath = PredefinedPath.build(Players.getLocal().getPosition(), location.asPosition());
	    if(predefinePath.walk()) {
		// We did built the path ourselves
	    } else {
		// Move around and hope DaxWalker can find a path from where we end up.
		PredefinedPath.build( Players.getLocal().getPosition(),  Players.getLocal().getPosition().translate(20, 20)).walk();
	    }
	}
	return 0;
    }


}
