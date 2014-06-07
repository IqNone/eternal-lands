package el.android.widgets.mapview;

import android.view.MotionEvent;
import el.actor.Actor;
import el.actor.BaseActor;
import el.android.widgets.Commander;
import el.map.MapObject;

import java.util.Date;

import static java.lang.Math.abs;

public class TouchListener{
    private static final int NPC_COLOR = 2;
    private static final int CREATURE_COLOR = 6;
    private static final int LONG_PRESS_MILLIS = 350;
    private static final int IGNORE_MOVE = 30;

    private Actor actor;
    private Commander.CommandListener commandListener;

    private boolean dragging = false;
    private boolean moving = false;
    private long startTime = 0;
    private float startX;
    private float startY;

    private boolean longPress;

    public void enableLongPress(boolean moveOnLongPress) {
        this.longPress = moveOnLongPress;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public void setCommandListener(Commander.CommandListener commandListener) {
        this.commandListener = commandListener;
    }

    public boolean onTouch(MotionEvent event, int x, int y) {
        if(longPress) {
            return onTouchWithLongPress(event, x, y);
        } else {
            return onTouchNoLongPress(event, x, y);
        }
    }

    //todo refactor to remove copy paste
    private boolean onTouchWithLongPress(MotionEvent event, int x, int y) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                dragging = false;
                moving = false;
                startTime = new Date().getTime();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                dragging = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(!dragging && (abs(event.getX() - startX) > IGNORE_MOVE || abs(event.getY() - startY) > IGNORE_MOVE)) {
                    dragging = true;
                }
                if(!dragging && !moving && new Date().getTime() - startTime > LONG_PRESS_MILLIS) {
                    sendTouchEvent(x, y);
                    moving = true;
                }
                break;
            case MotionEvent.ACTION_UP:{
                if(!dragging && !moving) {
                    processMapClick(x, y);
                }
            }
        }
        return true;
    }

    public boolean onTouchNoLongPress(MotionEvent event, int x, int y) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                dragging = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                dragging = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if(!dragging && (abs(event.getX() - startX) > IGNORE_MOVE || abs(event.getY() - startY) > IGNORE_MOVE)) {
                    dragging = true;
                }
                break;
            case MotionEvent.ACTION_UP:{
                if(!dragging) {
                    if (tryHarvest(x, y)) return true;
                    if (tryEnter(x, y)) return true;
                    if(tryInteractWithActors(x, y)) return true;

                    sendTouchEvent(x, y);
                }
            }
        }
        return true;
    }

    private void sendTouchEvent(int x, int y) {
        if(commandListener != null && x >= 0 && x < actor.map.width && y >= 0 && y < actor.map.height){
            commandListener.walkTo(x, y);
        }
    }

    private void processMapClick(int x, int y) {
        if (tryHarvest(x, y)) return;
        if (tryEnter(x, y)) return;

        tryInteractWithActors(x, y);
    }

    private boolean tryHarvest(int x, int y) {
        int harvestItemId = getHarvestable(x, y);
        if (harvestItemId == -1) {
            return false;
        }

        commandListener.harvest(harvestItemId);
        return true;
    }

    private int getHarvestable(int x, int y) {
        return findObjectWithCoordinates(actor.map.harvestables, x, y, MapView.HARVESTABLE_SIZE);
    }

    private boolean tryEnter(int x, int y) {
        int entrableId = getEntrable(x, y);
        if (entrableId == -1) {
            return false;
        }

        commandListener.enter(entrableId);
        return true;
    }

    private int getEntrable(int x, int y) {
        return findObjectWithCoordinates(actor.map.entrables, x, y, MapView.ENTRABLE_SIZE);
    }

    private int findObjectWithCoordinates(MapObject[] objects, int x, int y, int r) {
        for (MapObject object : objects) {
            if(object.x >= x - r / 2 && object.x <= x + r / 2 &&
                    object.y >= y - r / 2 && object.y <= y + r / 2) {
                return object.objId;
            }
        }
        return -1;
    }

    private boolean tryInteractWithActors(int x, int y) {
        int actorId = getActor(x, y);
        if (actorId == -1) {
            return false;
        }

        if(isNPC(actorId)) {
            commandListener.touchActor(actorId);
        } else if(!isCreature(actorId)){
            commandListener.tradeWith(actorId);
        }
        return true;
    }

    private int getActor(int x, int y) {
        for (BaseActor baseActor : actor.actors.values()) {
            if(baseActor.x == x && baseActor.y == y) {
                return baseActor.id;
            }
        }

        return -1;
    }

    private boolean isNPC(int actorId) {
        return actor.actors.get(actorId).nameColor == NPC_COLOR;
    }

    private boolean isCreature(int actorId) {
        return actor.actors.get(actorId).nameColor == CREATURE_COLOR;
    }
}
