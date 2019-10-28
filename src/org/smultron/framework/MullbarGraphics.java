package org.smultron.framework;

import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.smultron.framework.MullbarRand.DumbnessFactor;
import org.smultron.framework.content.banking.BankCache;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

public abstract class MullbarGraphics implements RenderListener
{
    private static MullbarGraphics instance = null;
    private Deque<String> tasks = new ArrayDeque<>();
    private String signature = "@smultron";

    private MullbarGraphics() {}

    public static MullbarGraphics getInstance() {
        if (instance == null)
            instance = new MullbarGraphics(){};
        return instance;
    }

    /**
     *
     * @param task
     */
    public void queue(String task) {
        tasks.addLast(task);
    }

    void reset() {
        tasks.clear();
    }


    public void notify(final RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();
        int x = 20;
        int y = 30;
        int yOffset = 30;
        int yIncrease = 30;

        /*
        Rectangle
         */
        // TODO refractoring
        int heightMultiplier = (int) tasks.stream().filter(s -> !s.isEmpty()).count();
        int widthMultiplier = tasks.stream().max((o1, o2) -> o2.length() < o1.length() ? 1 : -1).toString().length();
        if(widthMultiplier*6 < 220)
            widthMultiplier = 36;
        g.setColor(newColorWithAlpha(Color.BLACK, 180));
        g.fillRect(x - 10, y - 20, 6*widthMultiplier, yOffset + yIncrease*(heightMultiplier + 2));

        /*
        Info
         */
        g.setColor(Color.green);
        DumbnessFactor speed = MullbarRand.getDumbnessFactor();
        g.drawString("I am " + speed, x, yOffset);
        yOffset += yIncrease;
        if(!BankCache.getInstance().mustCheckBank()) {
            g.setColor(Color.green);
            g.drawString("I know how my bank looks like", x, yOffset);
        } else {
            g.setColor(Color.red);
            g.drawString("I do NOT know how my bank looks like", x, yOffset);
        }
        yOffset += yIncrease;

        /*
        Tasks
         */
        g.setColor(Color.LIGHT_GRAY);
        Object[] nonEmptyTasks = tasks.stream().filter(s -> !s.isEmpty()).toArray();
        for (Object task : nonEmptyTasks){
            g.drawString((String)task, x, yOffset);
            yOffset += yIncrease;
        }

        /*
        Signature
         */
        g.setColor(Color.CYAN);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10    ));
        g.drawString(signature, 10, 338);
    }

    public static Color newColorWithAlpha(Color original, int alpha) {
        return new Color(original.getRed(), original.getGreen(), original.getBlue(), alpha);
    }

}
