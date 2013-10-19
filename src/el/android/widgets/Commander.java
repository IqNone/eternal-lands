package el.android.widgets;

public interface Commander {
    public void setCommandListener(CommandListener listener);

    public static interface CommandListener {
        public void walkTo(int x, int y);
        public void harvest(int itemId);
        public void enter(int entrableId);
        public void touchActor(int actorId);
        public void tradeWith(int actorId);
    }
}
