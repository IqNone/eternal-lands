package el.android.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import el.actor.Actor;
import el.actor.Attribute;
import el.android.R;

public class PlayerStatsDialog extends Dialog implements Invalidateable{
    private TextView temp_TextView;
    private ELProgressBar researchBar;
    private Actor actor;
    String[] book_list;
    View stats_layout;

    public PlayerStatsDialog(Context context) {
        super(context);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        buildContent(context);
    }

    private void buildContent(Context context) {
        setTitle("Player Statistics");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        stats_layout = inflater.inflate(R.layout.statistics, null);
        book_list = context.getResources().getStringArray(R.array.books);

    }

    @Override
    public void invalidate() {
        if(isShowing()) {
            // Invalidate layout view here
            setContent();
            stats_layout.invalidate();
            stats_layout.requestLayout();
        }
    }


    public void setContent() {
        // Base attribute information
        temp_TextView = (TextView) stats_layout.findViewById(R.id.phy_value);
        setAttributeText(temp_TextView, actor.phy);

        temp_TextView = (TextView) stats_layout.findViewById(R.id.coord_value);
        setAttributeText(temp_TextView, actor.coo);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.rea_value);
        setAttributeText(temp_TextView, actor.rea);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.wil_value);
        setAttributeText(temp_TextView, actor.wil);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.ins_value);
        setAttributeText(temp_TextView, actor.ins);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.vit_value);
        setAttributeText(temp_TextView, actor.vit);


        // Cross attributes
        temp_TextView = (TextView) stats_layout.findViewById(R.id.might_value);
        setAttributeText(temp_TextView, actor.might);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.matter_value);
        setAttributeText(temp_TextView, actor.matter);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.toughness_value);
        setAttributeText(temp_TextView, actor.toughness);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.charm_value);
        setAttributeText(temp_TextView, actor.charm);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.reaction_value);
        setAttributeText(temp_TextView, actor.reaction);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.perception_value);
        setAttributeText(temp_TextView, actor.perception);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.rationality_value);
        setAttributeText(temp_TextView, actor.rationality);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.dexterity_value);
        setAttributeText(temp_TextView, actor.dexterity);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.ethereality_value);
        setAttributeText(temp_TextView, actor.ethereality);

        // Nexus information
        temp_TextView = (TextView) stats_layout.findViewById(R.id.human_value);
        setAttributeText(temp_TextView, actor.human_nex);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.animal_value);
        setAttributeText(temp_TextView, actor.animal_nex);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.vegetal_value);
        setAttributeText(temp_TextView, actor.vegetal_nex);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.inorganic_value);
        setAttributeText(temp_TextView, actor.inorganic_nex);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.artificial_value);
        setAttributeText(temp_TextView, actor.artificial_nex);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.magic_nex_value);
        setAttributeText(temp_TextView, actor.magic_nex);

        // Skill information
        temp_TextView = (TextView) stats_layout.findViewById(R.id.attack_value);
        setExperienceText(temp_TextView, actor.attack_skill, actor.statistics.attack_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.defense_value);
        setExperienceText(temp_TextView, actor.defense_skill, actor.statistics.defense_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.harvest_value);
        setExperienceText(temp_TextView, actor.harvesting_skill, actor.statistics.harvesting_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.alchemy_value);
        setExperienceText(temp_TextView, actor.alchemy_skill, actor.statistics.alchemy_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.magic_value);
        setExperienceText(temp_TextView, actor.magic_skill, actor.statistics.magic_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.potion_value);
        setExperienceText(temp_TextView, actor.potion_skill, actor.statistics.potion_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.summoning_value);
        setExperienceText(temp_TextView, actor.summoning_skill, actor.statistics.summoning_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.manufacturing_value);
        setExperienceText(temp_TextView, actor.manufacturing_skill, actor.statistics.manufacturing_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.crafting_value);
        setExperienceText(temp_TextView, actor.crafting_skill, actor.statistics.crafting_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.engineering_value);
        setExperienceText(temp_TextView, actor.engineering_skill, actor.statistics.engineering_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.tailoring_value);
        setExperienceText(temp_TextView, actor.tailoring_skill, actor.statistics.tailoring_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.ranging_value);
        setExperienceText(temp_TextView, actor.ranging_skill, actor.statistics.ranging_exp);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.overall_value);
        setExperienceText(temp_TextView, actor.overall_skill, actor.statistics.overall_exp);

        // Additional information
        temp_TextView = (TextView) stats_layout.findViewById(R.id.food_value);
        temp_TextView.setText(String.valueOf(actor.food.current));
        temp_TextView = (TextView) stats_layout.findViewById(R.id.material_points_value);
        setAttributeText(temp_TextView, actor.materialPoints);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.ethereal_points_value);
        setAttributeText(temp_TextView, actor.etherealPoints);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.action_points_value);
        setAttributeText(temp_TextView, actor.action_points);
        temp_TextView = (TextView) stats_layout.findViewById(R.id.pickpoints_value);
        temp_TextView.setText(String.valueOf(actor.overall_skill.base - actor.overall_skill.current));
        researchBar = (ELProgressBar) stats_layout.findViewById(R.id.researchbar);
        researchBar.setColor(0xFF0000FF); researchBar.setShowText(true);



        if(actor.researching > book_list.length) {
            actor.is_researching = 0;
        }
        else {
            actor.is_researching = 1;
        }

        temp_TextView = (TextView) stats_layout.findViewById(R.id.researching);

        if ( actor.is_researching != 0) {
            temp_TextView.setText("Currently researching " + book_list[actor.researching]);
            researchBar.setTotal(actor.research_total);
            researchBar.setCurrent(actor.research_completed);
        } else {
            temp_TextView.setText("Researching nothing");
            researchBar.setShowText(false);
            researchBar.setTotal(1);
            researchBar.setCurrent(0);
        }
        this.setContentView(stats_layout);

    }

    public void setActor(Actor actor) {this.actor = actor;}

    public void setAttributeText(TextView tv, Attribute attrb) {
        tv.setText(String.valueOf(attrb.current) + "/" + String.valueOf(attrb.base));
    }

    public void setExperienceText(TextView tv, Attribute skill, Attribute experience) {
        tv.setText(String.valueOf(skill.current) + "/" + String.valueOf(skill.base)
                + "  ["  + String.valueOf(experience.current)
                +  "/" + String.valueOf(experience.base) + "]");

    }


}
