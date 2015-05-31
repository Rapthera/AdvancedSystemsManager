package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Null;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ScrollVariable extends ScrollController<Variable> implements IPacketSync
{
    private FlowComponent command;
    private int id;
    public int selected = -1;

    public ScrollVariable(FlowComponent command)
    {
        this.command = command;
        command.registerSyncable(this);
        textBox = new TextBoxLogic(Null.NULL_PACKET, Integer.MAX_VALUE, TEXT_BOX_SIZE_W - TEXT_BOX_TEXT_X * 2)
        {
            @Override
            public void onUpdate()
            {
                if (getText().length() > 0)
                {
                    updateSearch();
                } else
                {
                    result.clear();
                    updateScrolling();
                }
            }
        };
        textBox.setTextAndCursor("");
        updateSearch();
    }

    @Override
    public void updateSearch()
    {
        if (hasSearchBox)
        {
            result = updateSearch(textBox.getText(), textBox.getText().isEmpty());
        } else
        {
            result = updateSearch("", false);
        }
        updateScrolling();
    }

    @Override
    public void onClick(Variable variable, int mX, int mY, int button)
    {
        int newSelected = variable.colour;
        setSelected(newSelected == selected ? -1 : newSelected);
        sendUpdate();
    }

    @Override
    public void draw(GuiManager gui, Variable variable, int x, int y, boolean hover)
    {
        int srcInventoryX = selected == variable.colour ? 1 : 0;
        int srcInventoryY = hover ? 1 : 0;

        gui.drawTexture(x, y, MenuContainer.INVENTORY_SRC_X + srcInventoryX * MenuContainer.INVENTORY_SIZE, MenuContainer.INVENTORY_SRC_Y + srcInventoryY * MenuContainer.INVENTORY_SIZE, MenuContainer.INVENTORY_SIZE, MenuContainer.INVENTORY_SIZE);
        variable.draw(gui, x, y);
    }

    @Override
    public List<Variable> updateSearch(String search, boolean all)
    {
        List<Variable> variables = new ArrayList<Variable>();
        if (all)
        {
            variables.addAll(command.getManager().getVariables());
        } else
        {
            Pattern pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
            for (Variable variable : command.getManager().getVariables())
            {
                if (pattern.matcher(variable.getNameFromColor()).find()) variables.add(variable);
            }
        }
        Collections.sort(variables);
        return variables;
    }

    @Override
    public void drawMouseOver(GuiManager gui, Variable variable, int mX, int mY)
    {
        gui.drawMouseOver(variable.getDescription(gui), mX, mY);
    }

    public void setSelected(int val)
    {
        selected = val;
    }

    public void sendUpdate()
    {
        ASMPacket packet = command.getSyncPacket();
        packet.writeByte(id);
        packet.writeMedium(selected);
        packet.sendServerPacket();
    }

    @Override
    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    public boolean readData(ASMPacket packet)
    {
        setSelected(packet.readUnsignedMedium());
        return false;
    }
}
