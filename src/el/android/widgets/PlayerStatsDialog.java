package el.android.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import el.actor.Actor;
import el.actor.Attributes;
import el.android.R;

public class PlayerStatsDialog extends Dialog implements Invalidateable{
    private ELProgressBar researchBar;
    private Actor actor;
    String[] book_list;
    View stats_layout;

    public PlayerStatsDialog(Context context) {
        super(context);

        buildContent(context);
    }

    private void buildContent(Context context) {
        setTitle("Player Statistics");

        book_list = context.getResources().getStringArray(R.array.books);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stats_layout = inflater.inflate(R.layout.statistics, null);

        researchBar = (ELProgressBar) stats_layout.findViewById(R.id.researchbar);
        researchBar.setColor(0xFF0000FF);
        researchBar.setShowText(true);

        //lets set the layout only one time
        setContentView(stats_layout);
    }

    @Override
    public void invalidate() {
        if(isShowing()) {
            // Invalidate layout view here
            updateStats();
            stats_layout.invalidate();
            stats_layout.requestLayout();
        }
    }

    private void updateStats() {
        updateBaseAttributes();
        updateCrossAttributes();
        updateNexusAttributes();
        updateSkillAttributes();
        updateAdditionalInfo();
        updateResearch();
    }

    private void updateAdditionalInfo() {
        setAttribute(R.id.material_points_value, actor.materialPoints);
        setAttribute(R.id.ethereal_points_value, actor.etherealPoints);
        setAttribute(R.id.action_points_value, actor.actionPoints);
        getTExtView(R.id.food_value).setText(String.valueOf(actor.food.current));
        getTExtView(R.id.pickpoints_value).setText(String.valueOf(actor.skills.overall.base - actor.skills.overall.current));
    }

    private void updateSkillAttributes() {
        setExperienceText(R.id.attack_value, actor.skills.attack, actor.statistics.attack_exp);
        setExperienceText(R.id.defense_value, actor.skills.defense, actor.statistics.defense_exp);
        setExperienceText(R.id.harvest_value, actor.skills.harvesting, actor.statistics.harvesting_exp);
        setExperienceText(R.id.alchemy_value, actor.skills.alchemy, actor.statistics.alchemy_exp);
        setExperienceText(R.id.magic_value, actor.skills.magic, actor.statistics.magic_exp);
        setExperienceText(R.id.potion_value, actor.skills.potion, actor.statistics.potion_exp);
        setExperienceText(R.id.summoning_value, actor.skills.summoning, actor.statistics.summoning_exp);
        setExperienceText(R.id.manufacturing_value, actor.skills.manufacturing, actor.statistics.manufacturing_exp);
        setExperienceText(R.id.crafting_value, actor.skills.crafting, actor.statistics.crafting_exp);
        setExperienceText(R.id.engineering_value, actor.skills.engineering, actor.statistics.engineering_exp);
        setExperienceText(R.id.tailoring_value, actor.skills.tailoring, actor.statistics.tailoring_exp);
        setExperienceText(R.id.ranging_value, actor.skills.ranging, actor.statistics.ranging_exp);
        setExperienceText(R.id.overall_value, actor.skills.overall, actor.statistics.overall_exp);
    }

    private void updateNexusAttributes() {
        setAttribute(R.id.human_value, actor.nexuses.human);
        setAttribute(R.id.animal_value, actor.nexuses.animal);
        setAttribute(R.id.vegetal_value, actor.nexuses.vegetal);
        setAttribute(R.id.inorganic_value, actor.nexuses.inorganic);
        setAttribute(R.id.artificial_value, actor.nexuses.artificial);
        setAttribute(R.id.magic_nex_value, actor.nexuses.magic);
    }

    private void updateCrossAttributes() {
        setAttribute(R.id.might_value, actor.cross.might);
        setAttribute(R.id.matter_value, actor.cross.matter);
        setAttribute(R.id.toughness_value, actor.cross.toughness);
        setAttribute(R.id.charm_value, actor.cross.charm);
        setAttribute(R.id.reaction_value, actor.cross.reaction);
        setAttribute(R.id.perception_value, actor.cross.perception);
        setAttribute(R.id.rationality_value, actor.cross.rationality);
        setAttribute(R.id.dexterity_value, actor.cross.dexterity);
        setAttribute(R.id.ethereality_value, actor.cross.ethereality);
    }

    private void updateBaseAttributes() {
        setAttribute(R.id.phy_value, actor.base.phy);
        setAttribute(R.id.coord_value, actor.base.coo);
        setAttribute(R.id.rea_value, actor.base.rea);
        setAttribute(R.id.wil_value, actor.base.wil);
        setAttribute(R.id.ins_value, actor.base.ins);
        setAttribute(R.id.vit_value, actor.base.vit);
    }

    private void updateResearch() {
        TextView researchView = getTExtView(R.id.researching);

        if (isResearching()) {
            researchView.setText("Researching nothing");
            researchBar.setShowText(false);
            researchBar.setTotal(1);
            researchBar.setCurrent(0);
        } else {
            researchView.setText("Currently researching " + book_list[actor.researching]);
            researchBar.setTotal(actor.research_total);
            researchBar.setCurrent(actor.research_completed);
        }
    }

    private boolean isResearching() {
        return actor.researching > book_list.length;
    }

    private void setAttribute(int resId, Attributes.Attribute value) {
        TextView textView = getTExtView(resId);
        textView.setText(String.format("%s/%s", value.current, value.base));
    }

    private void setExperienceText(int resId, Attributes.Attribute skill, Attributes.Attribute exp) {
        TextView textView = getTExtView(resId);
        textView.setText(String.format("%s/%s [%s/%s]", skill.current, skill.base, exp.current, exp.base));
    }

    private TextView getTExtView(int resId) {
        return (TextView) stats_layout.findViewById(resId);
    }

    public void setActor(Actor actor) {
        this.actor = actor;
        if(actor != null) {
            updateStats();
        }
    }
}
