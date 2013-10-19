package el.android.widgets;

public class ActorView {}

//public class ActorView extends View implements Commander{
//    private static final int NPC_COLOR = 2;
//
//    private static final int MIN_ZOOM = 6;
//    private static final int MAX_ZOOM = 60;
//
//    private static final int TILE_SIZE_PX = 48;
//    private static final int CELL_SIZE_PX = 8;
//    private static final int ACTOR_SIZE = 10;
//
//    private int zoom = 10;
//
//    private Actor actor;
//
//    private int mapWidth;
//    private int mapHeight;
//
//    private int leftCell;
//    private int topCell;
//    private double cellSize;
//    private int cellsOnX;
//    private int cellsOnY;
//
//    private Paint[] actorsPaint;
//    private Paint movePaint;
//    private Paint namePaint;
//
//    private DynamicLayout textLayout;
//    private SpannableStringBuilder textBuffer;
//    private int lastTextIndex = 0;
//
//    private CommandListener commandListener;
//
//    public ActorView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        actorsPaint = new Paint[6];
//        actorsPaint[0] = createActorPaint(GREEN);
//        actorsPaint[1] = createActorPaint(COLORS[GREY1]);
//        actorsPaint[2] = createActorPaint(COLORS[BLUE2]);
//        actorsPaint[3] = createActorPaint(COLORS[GREY1]);
//        actorsPaint[4] = createActorPaint(COLORS[RED3]);
//        actorsPaint[5] = createActorPaint(COLORS[RED3]);
//
//        movePaint = createActorPaint(Color.RED);
//
//        namePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        namePaint.setTextSize(15);
//
//        setOnTouchListener(new TouchListenerImpl());
//    }
//
//    private Paint createActorPaint(int color) {
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(color);
//        return paint;
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        if(textLayout == null && getWidth() > 0) {
//            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//            paint.setTextSize(20);
//            textBuffer = new SpannableStringBuilder();
//            textLayout = new DynamicLayout(textBuffer, paint, getWidth(), Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
//        }
//    }
//
//    @Override
//    public void setCommandListener(CommandListener listener) {
//        commandListener = listener;
//    }
//
//    public void setActor(Actor actor) {
//        this.actor = actor;
//    }
//
//    @Override
//    public void draw(Canvas canvas) {
//        if(!isShown() || actor == null || actor.map == null) {
//            return;
//        }
//
//        updateMapStats();
//
//        drawGround(canvas);
//        shadeUnWalkableCells(canvas);
//        drawHarvestables(canvas);
//        drawEntrables(canvas);
//        drawActors(canvas);
//        drawText(canvas);
//    }
//
//    private void drawHarvestables(Canvas canvas) {
//        int rightCell = leftCell + cellsOnX - 1;
//        int bottomCell = topCell + cellsOnY - 1;
//
//        for (MapObject harvestable : actor.map.harvestables) {
//            if(harvestable.x >= leftCell && harvestable.x <= rightCell && harvestable.y >= topCell &&  harvestable.y <= bottomCell) {
//                if(harvestable.imgId != 0){
//                    Assets.IconBitmap image = Assets.getItemImage(harvestable.imgId);
//                    canvas.drawBitmap(image.bitmap,
//                            src(image.x, image.y, image.x + image.size, image.y + image.size),
//                            dst(toScreenX(harvestable.x), toScreenY(harvestable.y + 1), toScreenX(harvestable.x + 1), toScreenY(harvestable.y)),
//                            null);
//                }
//            }
//        }
//    }
//
//
//    private void drawEntrables(Canvas canvas) {
//        int rightCell = leftCell + cellsOnX - 1;
//        int bottomCell = topCell + cellsOnY - 1;
//
//        for (MapObject entrable : actor.map.entrables) {
//            if(entrable.x >= leftCell && entrable.x <= rightCell && entrable.y >= topCell &&  entrable.y <= bottomCell) {
//                if(entrable.imgId != 0){
//                    Bitmap bitmap = Assets.getEntrable(entrable.imgId);
//                    canvas.drawBitmap(bitmap,
//                            src(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1),
//                            dst(toScreenX(entrable.x), toScreenY(entrable.y + 1), toScreenX(entrable.x + 1), toScreenY(entrable.y)),
//                            null);
//                }
//            }
//        }
//    }
//
//    private void drawGround(Canvas canvas) {
//        int leftTile = leftCell / 6;
//        int rightTile = (leftCell + cellsOnX - 1) / 6;
//
//        int topTile = topCell / 6;
//        int bottomTile = (topCell + cellsOnY - 1) / 6;
//
//        Bitmap tileBitmap = getTileBitmap(leftTile, rightTile, topTile, bottomTile);
//
//        canvas.drawBitmap(tileBitmap,
//                src((leftCell % 6) * CELL_SIZE_PX, ((bottomTile + 1) * 6 - topCell - cellsOnY) * CELL_SIZE_PX, (leftCell % 6 + cellsOnX) * CELL_SIZE_PX - 1, tileBitmap.getHeight() - (topCell - topTile * 6)* CELL_SIZE_PX),
//                dst(0, 0, getWidth() - 1, getHeight() - 1),
//                null
//        );
//
//        tileBitmap.recycle();
//    }
//
//    private Bitmap getTileBitmap(int leftTile, int rightTile, int topTile, int bottomTile) {
//        int tilesX = rightTile - leftTile + 1;
//        int tilesY = bottomTile - topTile + 1;
//
//        Bitmap groundBitmap = Bitmap.createBitmap(tilesX * TILE_SIZE_PX, tilesY * TILE_SIZE_PX, Bitmap.Config.RGB_565);
//        Canvas groundCanvas = new Canvas(groundBitmap);
//
//        for(int i = 0; i < tilesX && i < actor.map.width - leftTile; ++i) {
//            for(int j = 0; j < tilesY && j < actor.map.height - topTile; ++j) {
//                int tileId = unsigned(actor.map.tileMap[topTile + j][leftTile + i]);
//                if(tileId != 255) { //taken from title_map.c
//                    Bitmap tile = Assets.getTile(tileId);
//                    groundCanvas.drawBitmap(tile,
//                        src(0, 0, TILE_SIZE_PX, TILE_SIZE_PX),
//                        dst(i * TILE_SIZE_PX, (tilesY - j - 1) * TILE_SIZE_PX, (i + 1) * TILE_SIZE_PX, (tilesY - j) * TILE_SIZE_PX),
//                        null);
//                }
//            }
//        }
//        return groundBitmap;
//    }
//
//    private void drawActors(Canvas canvas) {
//        int delta = (int) (cellSize - ACTOR_SIZE) / 2;
//
//        if(actor.moveToX != -1 && actor.moveToY != -1) {
//            int x = toScreenX(actor.moveToX) + delta;
//            int y = toScreenY(actor.moveToY) - delta;
//            if(x >= 0 && x < getWidth() - ACTOR_SIZE && y >=0 && y < getHeight() - ACTOR_SIZE) {
//                canvas.drawCircle(x, y, ACTOR_SIZE, movePaint);
//            }
//        }
//
//        for (BaseActor baseActor : actor.actors.values()) {
//            Paint paint = baseActor.id == actor.id ? actorsPaint[0] : actorsPaint[baseActor.nameColor];
//            canvas.drawCircle(toScreenX(baseActor.x) + delta, toScreenY(baseActor.y) - delta, ACTOR_SIZE, paint);
//            if(zoom < 12) { //2 tiles
//                drawName(canvas, baseActor);
//            }
//        }
//    }
//
//    private void drawName(Canvas canvas, BaseActor baseActor) {
//        int size = 0;
//        for (Span span : baseActor.name) {
//            size += namePaint.measureText(span.text);
//        }
//        float pos = (float) ((cellSize - size) / 2);
//        for (Span span : baseActor.name) {
//            namePaint.setColor(span.color == -1 ? actorsPaint[baseActor.nameColor].getColor() : span.color);
//            canvas.drawText(span.text, toScreenX(baseActor.x) + pos, (float) (toScreenY(baseActor.y) - cellSize), namePaint);
//            pos += namePaint.measureText(span.text);
//        }
//    }
//
//    private void shadeUnWalkableCells(Canvas canvas) {
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(0x66000000);
//        for(int i = 0; i < cellsOnX && i < actor.map.width * 6 - leftCell; ++i) {
//            for(int j = 0; j < cellsOnY && j < actor.map.height * 6 - topCell; ++j) {
//                if(actor.map.heightMap[topCell + j][leftCell + i] == 0) {
//                    canvas.drawRect(toScreenX(i + leftCell), toScreenY(j + topCell + 1), toScreenX(i + leftCell + 1), toScreenY(j + topCell), paint);
//                }
//            }
//        }
//    }
//
//    private void drawText(Canvas canvas) {
//        if(actor.texts.isEmpty() || textLayout == null) {
//            return;
//        }
//
//        updateText();
//        textLayout.draw(canvas);
//    }
//
//    private void updateText() {
//        if(lastTextIndex == actor.texts.size() - 1) {
//            return;
//        }
//
//        lastTextIndex = actor.texts.size() - 1;
//        textBuffer.clear();
//        for (Span span : actor.texts.get(lastTextIndex).spans) {
//            textBuffer.append(span.text);
//            textBuffer.setSpan(new ForegroundColorSpan(span.color), textBuffer.length() - span.text.length(), textBuffer.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//    }
//
//    @SuppressWarnings("SynchronizeOnNonFinalField")
//    private void updateMapStats() {
//        synchronized (actor) {
//            cellSize = ((double)getWidth()) / zoom;
//
//            cellsOnX = zoom;
//            cellsOnY = (int) (getHeight() / cellSize);
//
//            mapWidth = actor.map.width * 6;
//            mapHeight = actor.map.height * 6;
//
//            leftCell = findLeft(cellsOnX);
//            topCell = findTop(cellsOnY);
//        }
//    }
//
//    private int findLeft(int cellsOnX) {
//        int left = actor.x - (cellsOnX  - 1) / 2;
//        if(left < 0) { //bound the left edge of the map with the left edge of the screen
//            return 0;
//        }
//        if(left + cellsOnX - 1 >= mapWidth) { //bound the right edge of the map with the right edge of the screen
//            return mapWidth - cellsOnX;
//        }
//        return left;
//    }
//
//    private int findTop(int cellsOnY) {
//        int top = actor.y - (cellsOnY - 1) / 2;
//        if(top < 0) {//bound the top edge of the map with the top edge of the screen
//            return  0;
//        }
//        if(top + cellsOnY - 1 >= mapHeight) { //bound the bottom edge of the map with the bottom edge of the screen
//            return mapHeight - cellsOnY;
//        }
//        return top;
//    }
//
//    private int toScreenX(int cellX) {
//        return (int) ((cellX - leftCell) * cellSize);
//    }
//
//    private int toScreenY(int cellY) {
//        return (int) ((cellsOnY - cellY + topCell) * cellSize);
//    }
//
//    private int toCellX(float screenX) {
//        return (int) (screenX / cellSize + leftCell);
//    }
//
//    private int toCellY(float screenY) {
//        return (int) (cellsOnY + topCell - screenY / cellSize);
//    }
//
//    //use only 2 instances
//    private Rect src = new Rect();
//    private Rect dst = new Rect();
//
//    private Rect src(int left, int top, int right, int bottom) {
//        src.set(left, top, right, bottom);
//        return src;
//    }
//
//    private Rect dst(int left, int top, int right, int bottom) {
//        dst.set(left, top, right, bottom);
//        return dst;
//    }
//
//    private class TouchListenerImpl implements OnTouchListener {
//        private boolean zooming = false;
//        private float startDistance;
//        private int startZoom;
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_DOWN : {
//                    zooming = false;
//                } break;
//                case MotionEvent.ACTION_POINTER_DOWN: {
//                    zooming = true;
//                    startDistance = distance(event);
//                    startZoom = zoom;
//                } break;
//                case MotionEvent.ACTION_MOVE: {
//                    if(zooming && startDistance != -1) {
//                        float newDistance = distance(event);
//                        int  d = (int) (startDistance - newDistance) / 2;
//                        zoom = min(max(startZoom + d, MIN_ZOOM), MAX_ZOOM);
//                    }
//                } break;
//                case MotionEvent.ACTION_POINTER_UP: {
//                    startDistance = -1;
//                } break;
//                case MotionEvent.ACTION_UP : {
//                    if(!zooming && commandListener != null) {
//                        processOnClick(toCellX(event.getX()), toCellY(event.getY()));
//                    }
//                }
//            }
//
//            return true;
//        }
//
//        public float distance(MotionEvent event) {
//            float x = event.getX(0) - event.getX(1);
//            float y = event.getY(0) - event.getY(1);
//            return FloatMath.sqrt(x * x + y * y);
//        }
//
//        private void processOnClick(int x, int y) {
//            if (tryHarvest(x, y)) return;
//            if (tryEnter(x, y)) return;
//            if (tryInteractWithActors(x, y)) return;
//
//            commandListener.walkTo(x, y);
//        }
//
//        private boolean tryInteractWithActors(int x, int y) {
//            int actorId = getActor(x, y);
//            if (actorId == -1) {
//                return false;
//            }
//
//            if(isNPC(actorId)) {
//                commandListener.touchActor(actorId);
//            } else {
//                commandListener.tradeWith(actorId);
//            }
//            return true;
//        }
//
//        private boolean tryEnter(int x, int y) {
//            int entrableId = getEntrable(x, y);
//            if (entrableId == -1) {
//                return false;
//            }
//
//            commandListener.enter(entrableId);
//            return true;
//        }
//
//        private boolean tryHarvest(int x, int y) {
//            int harvestItemId = getHarvestable(x, y);
//            if (harvestItemId == -1) {
//                return false;
//            }
//
//            commandListener.harvest(harvestItemId);
//            return true;
//        }
//
//        private int getHarvestable(int x, int y) {
//            return findObjectWithCoordinates(actor.map.harvestables, x, y);
//        }
//
//        private int getEntrable(int x, int y) {
//            return findObjectWithCoordinates(actor.map.entrables, x, y);
//        }
//
//        private int getActor(int x, int y) {
//            for (BaseActor baseActor : actor.actors.values()) {
//                if(baseActor.x == x && baseActor.y == y) {
//                    return baseActor.id;
//                }
//            }
//
//            return -1;
//        }
//
//        private int findObjectWithCoordinates(MapObject[] objects, int x, int y) {
//            for (MapObject object : objects) {
//                if(object.x == x && object.y == y) {
//                    return object.objId;
//                }
//            }
//            return -1;
//        }
//
//        private boolean isNPC(int actorId) {
//            return actor.actors.get(actorId).nameColor == NPC_COLOR;
//        }
//    }
//}
