package el.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;
import el.actor.Actor;
import el.actor.Item;
import el.actor.Span;
import el.android.assets.Assets;
import el.android.widgets.*;
import el.android.widgets.mapview.MapView;
import el.android.widgets.storage.StorageDialog;
import el.android.widgets.trade.TradeDialog;

import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.LayoutParams;
import static el.android.GameMetadata.CLIENT;
import static java.lang.String.format;

public class Game extends GameRunner {
    private TextView actorName;
    private TextView actorTime;
    private ELProgressBar healthBar;
    private ELProgressBar manaBar;
    private ELProgressBar foodBar;
    private ViewFlipper flipper;
    private MapView mapView;
    private ConsoleView consoleView;

    private Invalidateable invalidateableDialog;
    private Dialog lastOpenedDialog;
    private StorageDialog storageDialog;
    private TradeDialog tradeDialog;
    private Dialog confirmLogoutDialog;

    private Actor actor;
    private ItemView[] fastInventory = new ItemView[4];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Assets.setAssetManager(getAssets());

        actorName = (TextView) findViewById(R.id.actorName);
        actorTime = (TextView) findViewById(R.id.actorTime);
        healthBar = (ELProgressBar) findViewById(R.id.healthBar);
        manaBar = (ELProgressBar) findViewById(R.id.manaBar);
        foodBar = (ELProgressBar) findViewById(R.id.foodBar);
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        mapView = (MapView) findViewById(R.id.map);
        consoleView = (ConsoleView) findViewById(R.id.console);

        fastInventory[0] = (ItemView) findViewById(R.id.fastInventory1);
        fastInventory[1] = (ItemView) findViewById(R.id.fastInventory2);
        fastInventory[2] = (ItemView) findViewById(R.id.fastInventory3);
        fastInventory[3] = (ItemView) findViewById(R.id.fastInventory4);

        healthBar.setColor(0xFFFF0000); healthBar.setShowText(true);
        manaBar.setColor(0xFF0000FF);   manaBar.setShowText(true);
        foodBar.setColor(0xFFFFD800);   foodBar.setShowText(true);

        View openChatButton = findViewById(R.id.openChatInput);
        openChatButton.setOnClickListener(new OpenChatButtonClickListener());

        View viewSwitcher = findViewById(R.id.viewSwitcher);
        viewSwitcher.setOnClickListener(new SwitchViewListener());

        findViewById(R.id.inventory).setOnClickListener(new OnInventoryButtonClickListener());

        mapView.setCommandListener(commandListener);

        storageDialog = new StorageDialog(this);
        tradeDialog = new TradeDialog(this);
        confirmLogoutDialog = createConfirmLogoutDialog();

//        i'm not doing it right, screw this
//        CLIENT.sendOpeningScreen();

//        findViewById(R.id.stats).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LoggerFactory.logger(this.getClass()).error(new RuntimeException("Test email exception. You get it when you press the 'stats' button"));
//            }
//        });
    }

    private Dialog createConfirmLogoutDialog() {
        return new AlertDialog.Builder(Game.this)
                .setTitle(getString(R.string.confirm_logout))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Game.this.finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private long lastUpdate = new Date().getTime();

    @SuppressWarnings("SynchronizeOnNonFinalField")
    @Override
    protected void updateUI() {
        long currentUpdate = new Date().getTime();
        actor = CLIENT.getActor();

        if (actor == null) {
            return;
        }

        synchronized (actor) {
            updateInSync(currentUpdate - lastUpdate);
            lastUpdate = currentUpdate;
        }
    }

    private void updateInSync(long elapsed) {
        updateActorName();
        updateItemsCooldown(elapsed);
        actorTime.setText(format("%d:%02d", (actor.minutes / 60) % 6, actor.minutes % 60));
        healthBar.setTotal(actor.materialPoints.base);
        healthBar.setCurrent(actor.materialPoints.current);
        manaBar.setTotal(actor.etherealPoints.base);
        manaBar.setCurrent(actor.etherealPoints.current);
        foodBar.setTotal(actor.food.base);
        foodBar.setCurrent(actor.food.current);
        mapView.setActor(actor);
        consoleView.setActor(actor);
        updateOpenedDialog();
        updateStorageDialog();
        updateFastInventory();
        updateTradeDialog();
    }

    private void updateItemsCooldown(long elapsed) {
        if(actor.inventory == null) {
            return;
        }

        for (Item item : actor.inventory) {
            if(item != null &&  item.cooldownMax > 0) {
                item.cooldownLeft = (int) Math.max(0, item.cooldownLeft - elapsed);
            }
        }
    }

    private void updateStorageDialog() {
        if(actor.storage.openRequest && !storageDialog.isShowing()) {
            storageDialog.show();
            invalidateableDialog = storageDialog;
            lastOpenedDialog = storageDialog;
        }
        actor.storage.openRequest = false;
        storageDialog.setActor(actor);
    }

    private void updateTradeDialog() {
        if(actor.trade.isTrading && actor.trade.partnersName != null && !tradeDialog.isShowing()) {
            tradeDialog.setShowStorage(actor.trade.storageAvailable);
            tradeDialog.showAtLocation(mapView, Gravity.TOP, 0, 0);
            invalidateableDialog = tradeDialog;
        }
        if(!actor.trade.isTrading && tradeDialog.isShowing()) {
            tradeDialog.dismiss();
        }
        tradeDialog.setActor(actor);
    }

    private void updateActorName() {
        SpannableStringBuilder buffer = new SpannableStringBuilder();
        addSpans(buffer, actor.name);
        actorName.setText(buffer);
    }

    private void updateOpenedDialog() {
        if(invalidateableDialog != null) {
            invalidateableDialog.invalidate();
        }
    }

    private void addSpans(SpannableStringBuilder buffer, List<Span> spans) {
        for (Span span : spans) {
            buffer.append(span.text);
            buffer.setSpan(new ForegroundColorSpan(span.color), buffer.length() - span.text.length(), buffer.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void updateFastInventory() {
        for(int i = 0; i < 4; ++i) {
            fastInventory[i].setItem(actor.inventory[i]);
        }
    }

    private void sendText(String text) {
        CLIENT.sendText(text);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }

        if (tradeDialog.isShowing()) {
            CLIENT.exitTrade();
            return true;
        } else if(lastOpenedDialog != null && lastOpenedDialog.isShowing()){
            return super.onKeyDown(keyCode, event);//let android dismiss the popup
        } else {
            confirmLogoutDialog.show();
            lastOpenedDialog = confirmLogoutDialog;
            return true;
        }
    }

    private class OpenChatButtonClickListener implements View.OnClickListener {
        private EditText view = new EditText(Game.this);
        private AlertDialog dialog;

        public OpenChatButtonClickListener() {
            view.setMaxLines(5);
            view.setHeight(200);
            view.setFilters(new InputFilter[]{new InputFilter.LengthFilter(240)});
            view.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            view.setGravity(Gravity.TOP);
        }

        @Override
        public void onClick(View v) {
            if(dialog == null) {
                dialog = createDialog();
            }
            view.setText("");

            dialog.show();
            lastOpenedDialog = dialog;
        }

        private AlertDialog createDialog() {
            return new AlertDialog.Builder(Game.this)
                    .setView(view)
                    .setTitle(getString(R.string.send_text))
                    .setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Game.this.sendText(view.getText().toString());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
        }
    }

    private class OnInventoryButtonClickListener implements View.OnClickListener {
        private InventoryDialog dialog = new InventoryDialog(Game.this);

        @Override
        public void onClick(View v) {
            invalidateableDialog = dialog;
            lastOpenedDialog = dialog;
            dialog.setItems(actor.inventory);
            dialog.setCapacity(actor.capacity);
            dialog.show();
        }
    }

    private class SwitchViewListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (flipper.isFlipping()) {
                return;
            }

            int next = (flipper.getDisplayedChild() + 1) % 2;
            flipper.setDisplayedChild(next);
            v.setBackgroundResource(next == 0 ? R.drawable.map_view : R.drawable.console_view);
        }
    }

    private Commander.CommandListener commandListener = new Commander.CommandListener() {
        @Override
        public void walkTo(int x, int y) {
            mapView.setCanMovNotifier(CLIENT.walkTo(x, y));
        }

        @Override
        public void harvest(int itemId) {
            CLIENT.harvest(itemId);
        }

        @Override
        public void enter(int entrableId) {
            CLIENT.enter(entrableId);
        }

        @Override
        public void touchActor(int actorId) {
            CLIENT.touchActor(actorId);
        }

        @Override
        public void tradeWith(int actorId) {
            CLIENT.tradeWith(actorId);
        }
    };
}
