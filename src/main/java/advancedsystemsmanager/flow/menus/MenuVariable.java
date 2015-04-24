package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.flow.elements.VariableDisplay;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class MenuVariable extends Menu
{
    public MenuVariable(FlowComponent parent)
    {
        super(parent);

        int declarationCount = 0;
        int modificationCount = 0;

        radioButtons = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int selectedOption)
            {
                setSelectedOption(selectedOption);
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(true); //var || mode
                dw.writeBoolean(false); //mode
                dw.writeData(selectedOption, DataBitHelper.CONTAINER_MODE);
                PacketHandler.sendDataToServer(dw);
            }

            @Override
            public int getSelectedOption()
            {
                int id = super.getSelectedOption();
                VariableMode mode = VariableMode.values()[id];
                if (mode.declaration != isDeclaration())
                {
                    setSelectedOption(id = getDefaultId());
                }

                return id;
            }

            @Override
            public void setSelectedOption(int selectedOption)
            {
                super.setSelectedOption(selectedOption);

                if (isDeclaration())
                {
                    getParent().getManager().updateVariables();
                }
            }
        };

        for (int i = 0; i < VariableMode.values().length; i++)
        {
            final VariableMode mode = VariableMode.values()[i];
            int id = mode.declaration ? declarationCount++ : modificationCount++;

            radioButtons.add(new RadioButton(RADIO_BUTTON_X, RADIO_BUTTON_Y + id * RADIO_BUTTON_SPACING, mode.getName())
            {
                @Override
                public boolean isVisible()
                {
                    return mode.declaration == isDeclaration();
                }
            });
        }

        radioButtons.setSelectedOption(getDefaultId());

        varDisplay = new VariableDisplay(null, 5, 5)
        {
            @Override
            public int getValue()
            {
                return selectedVariable;
            }

            @Override
            public void setValue(int val)
            {
                setSelectedVariable(val);
            }

            @Override
            public void onUpdate()
            {
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(true); //var || mode
                dw.writeBoolean(true); //var
                dw.writeData(selectedVariable, DataBitHelper.VARIABLE_TYPE);
                PacketHandler.sendDataToServer(dw);
            }
        };

        checkBoxes = new CheckBoxList();
        checkBoxes.addCheckBox(new CheckBox(Localization.GLOBAL_VALUE_SET, CHECK_BOX_X, CHECK_BOX_Y)
        {
            @Override
            public void setValue(boolean val)
            {
                executed = val;
            }

            @Override
            public boolean getValue()
            {
                return executed;
            }

            @Override
            public void onUpdate()
            {
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(false); //executed
                dw.writeBoolean(executed);
                PacketHandler.sendDataToServer(dw);
            }

            @Override
            public boolean isVisible()
            {
                return getVariableMode() == VariableMode.GLOBAL;
            }
        });
    }

    @Override
    public String getName()
    {
        return Localization.VARIABLE_MENU.toString();
    }


    public void setSelectedVariable(int val)
    {
        selectedVariable = val;

        if (isDeclaration())
        {
            getParent().getManager().updateVariables();
        }
    }

    public Variable getVariable()
    {
        return getParent().getManager().getVariables()[getSelectedVariable()];
    }

    public static final int RADIO_BUTTON_X = 5;
    public static final int RADIO_BUTTON_Y = 28;
    public static final int RADIO_BUTTON_SPACING = 12;

    public static final int CHECK_BOX_X = 5;
    public static final int CHECK_BOX_Y = 52;


    public RadioButtonList radioButtons;
    public VariableDisplay varDisplay;
    public int selectedVariable = 0;
    public CheckBoxList checkBoxes;
    public boolean executed;


    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        radioButtons.draw(gui, mX, mY);
        varDisplay.draw(gui, mX, mY);
        checkBoxes.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        varDisplay.drawMouseOver(gui, mX, mY);
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        radioButtons.onClick(mX, mY, button);
        varDisplay.onClick(mX, mY);
        checkBoxes.onClick(mX, mY);
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeData(DataWriter dw)
    {
        dw.writeData(selectedVariable, DataBitHelper.VARIABLE_TYPE);
        dw.writeData(radioButtons.getSelectedOption(), DataBitHelper.CONTAINER_MODE);
        dw.writeBoolean(executed);
    }

    @Override
    public void readData(DataReader dr)
    {
        setSelectedVariable(dr.readData(DataBitHelper.VARIABLE_TYPE));
        radioButtons.setSelectedOption(dr.readData(DataBitHelper.CONTAINER_MODE));
        executed = dr.readBoolean();
    }

    @Override
    public void copyFrom(Menu menu)
    {
        setSelectedVariable(((MenuVariable)menu).selectedVariable);
        radioButtons.setSelectedOption(((MenuVariable)menu).radioButtons.getSelectedOption());
        executed = ((MenuVariable)menu).executed;
    }


    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuVariable newDataMode = (MenuVariable)newData;

        if (selectedVariable != newDataMode.selectedVariable)
        {
            setSelectedVariable(newDataMode.selectedVariable);

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(true); //var  || mode
            dw.writeBoolean(true); //var
            dw.writeData(selectedVariable, DataBitHelper.VARIABLE_TYPE);
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        if (radioButtons.getRawSelectedOption() != newDataMode.radioButtons.getRawSelectedOption())
        {
            radioButtons.setSelectedOption(newDataMode.radioButtons.getRawSelectedOption());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(true); //var  || mode
            dw.writeBoolean(false); //mode
            dw.writeData(radioButtons.getRawSelectedOption(), DataBitHelper.CONTAINER_MODE);
            PacketHandler.sendDataToListeningClients(container, dw);
        }


        if (executed != newDataMode.getVariable().hasBeenExecuted())
        {
            executed = newDataMode.getVariable().hasBeenExecuted();

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(false); //executed
            dw.writeBoolean(executed);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    public static final String NBT_VARIABLE = "Variable";
    public static final String NBT_MODE = "Mode";
    public static final String NBT_EXECUTED = "Executed";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        setSelectedVariable(nbtTagCompound.getByte(NBT_VARIABLE));
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_MODE));
        executed = nbtTagCompound.getBoolean(NBT_EXECUTED);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_VARIABLE, (byte)selectedVariable);
        nbtTagCompound.setByte(NBT_MODE, (byte)radioButtons.getSelectedOption());
        nbtTagCompound.setBoolean(NBT_EXECUTED, executed);
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        if (dr.readBoolean())
        {
            if (dr.readBoolean())
            {
                setSelectedVariable(dr.readData(DataBitHelper.VARIABLE_TYPE));
            } else
            {
                radioButtons.setSelectedOption(dr.readData(DataBitHelper.CONTAINER_MODE));
            }
        } else
        {
            executed = dr.readBoolean();
            if (!getParent().getManager().getWorldObj().isRemote)
            {
                getVariable().setExecuted(executed);
            }
        }
    }

    public int getSelectedVariable()
    {
        return selectedVariable;
    }

    public enum VariableMode
    {
        GLOBAL(Localization.GLOBAL, true),
        LOCAL(Localization.LOCAL, true),
        ADD(Localization.ADD, false),
        REMOVE(Localization.REMOVE, false),
        SET(Localization.SET, false);

        public boolean declaration;
        public Localization name;

        VariableMode(Localization name, boolean declaration)
        {
            this.name = name;
            this.declaration = declaration;
        }

        public Localization getName()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return super.toString().charAt(0) + super.toString().substring(1).toLowerCase();
        }
    }

    public boolean isDeclaration()
    {
        return getParent().getConnectionSet() == ConnectionSet.EMPTY;
    }

    public int getDefaultId()
    {
        return isDeclaration() ? 1 : 2;
    }

    public VariableMode getVariableMode()
    {
        return VariableMode.values()[radioButtons.getSelectedOption()];
    }


    @Override
    public void addErrors(List<String> errors)
    {
        Variable variable = getParent().getManager().getVariables()[selectedVariable];
        if (!variable.isValid())
        {
            errors.add(Localization.NOT_DECLARED_ERROR.toString());
        } else if (isDeclaration() && variable.getDeclaration().getId() != getParent().getId())
        {
            errors.add(Localization.ALREADY_DECLARED_ERROR.toString());
        }
    }
}