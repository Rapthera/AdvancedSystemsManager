package advancedsystemsmanager.gui;

import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.reference.Textures;
import advancedsystemsmanager.helpers.Settings;
import advancedsystemsmanager.tileentities.TileEntityClusterElement;
import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = Mods.NEI)
public abstract class GuiBase extends GuiContainer implements INEIGuiHandler
{
    private static final ResourceLocation TERRAIN = new ResourceLocation("textures/atlas/blocks.png");
    protected static final float SCALING = 0.00390625F;
    protected ContainerBase container;
    protected boolean cached;
    protected float scale;

    public GuiBase(ContainerBase container)
    {
        super(container);
        this.container = container;
    }

    public static ResourceLocation registerTexture(String name)
    {
        return new ResourceLocation(Reference.RESOURCE_LOCATION, Textures.GUI + name + ".png");
    }

    public float getZLevel()
    {
        return zLevel;
    }

    public void setZLevel(float val)
    {
        this.zLevel = val;
    }

    public void drawTexture(int x, int y, int srcX, int srcY, int w, int h)
    {

        drawScaledTexture(
                x,  y,
                srcX, srcY,
                w, h,
                w, h
        );
    }

    public void drawColouredTexture(int x, int y, int srcX, int srcY, int w, int h, int[] colour)
    {
        drawScaledColouredTexture(
                x, y,
                w, h, srcX, srcY,
                w, h, colour
        );
    }

    public void drawColouredTexture(int x, int y, int srcX, int srcY, int w, int h, float mult, int[] colour)
    {
        drawScaledColouredTexture(
                x, y,
                srcX, srcY,
                w, h,
                w, h, mult, colour
        );
    }

    public void drawRectangle(int x, int y, int x2, int y2, int[] colour)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque(colour[0], colour[1], colour[2]);
        tessellator.addVertex(x, y2, 0.0D);
        tessellator.addVertex(x2, y2, 0.0D);
        tessellator.addVertex(x2, y, 0.0D);
        tessellator.addVertex(x, y, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawGradientRectangle(int x, int y, int x2, int y2, int[] colourXY, int[] colourX2Y, int[] colourX2Y2, int[] colourXY2)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        tessellator.setColorOpaque(colourXY2[0], colourXY2[1], colourXY2[2]);
        tessellator.addVertex(x, y2, 0.0D);
        tessellator.setColorOpaque(colourX2Y2[0], colourX2Y2[1], colourX2Y2[2]);
        tessellator.addVertex(x2, y2, 0.0D);
        tessellator.setColorOpaque(colourX2Y[0], colourX2Y[1], colourX2Y[2]);
        tessellator.addVertex(x2, y, 0.0D);
        tessellator.setColorOpaque(colourXY[0], colourXY[1], colourXY[2]);
        tessellator.addVertex(x, y, 0.0D);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawRainbowRectangle(int x, int y, int x2, int y2, int[][]colours)
    {
        drawScaledRainbowRectangle(
                x, y,  x + x2, y + y2, colours);
    }

    public void drawScaledRainbowRectangle(double x, double y, double x2, double y2, int[][]colours)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_QUAD_STRIP);
        double dy = (y2 - y)/(colours.length - 1);
        for (int i = 0; i < colours.length; i++, y += dy)
        {
            tessellator.setColorOpaque(colours[i][0], colours[i][1], colours[i][2]);
            tessellator.addVertex(x2, y, 0.0D);
            tessellator.addVertex(x, y, 0.0D);
        }
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void drawScaledTexture(double x, double y, int srcX, int srcY, double w, double h, int u, int v)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        drawTexture(tessellator, x, y, w, h, srcX, srcY, u, v);
    }

    private void drawScaledColouredTexture(double x, double y, int srcX, int srcY, double w, double h, int u, int v, double mult, int[] colour)
    {
        drawScaledColouredTexture(x, y, w * mult, h * mult, srcX, srcY, u, v, colour);
    }

    private void drawScaledColouredTexture(double x, double y, double w, double h, int srcX, int srcY, int u, int v, int[] colour)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque(colour[0], colour[1], colour[2]);
        drawTexture(tessellator, x, y, w, h, srcX, srcY, u, v);
    }

    private void drawTexture(Tessellator tessellator, double x, double y, double w, double h, int srcX, int srcY, int u, int v)
    {
        tessellator.addVertexWithUV(x, y + h, (double)this.zLevel, srcX * SCALING, (srcY + v) * SCALING);
        tessellator.addVertexWithUV(x + w, y + h, (double)this.zLevel, (srcX + u) * SCALING, (srcY + v) * SCALING);
        tessellator.addVertexWithUV(x + w, y, (double)this.zLevel, (srcX + u) * SCALING, srcY * SCALING);
        tessellator.addVertexWithUV(x, y, (double)this.zLevel, srcX * SCALING, srcY * SCALING);
        tessellator.draw();
    }

    public void drawPolygon(int[] colours, double... points)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_POLYGON);
        drawAbstractPoints(tessellator, colours, points);
    }

    public void drawTriangleFan(int[] colours, double... points)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        drawAbstractPoints(tessellator, colours, points);
    }

    private void drawAbstractPoints(Tessellator tessellator, int[] colours, double[] points)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        for (int i = 0, j = 0; i < colours.length;)
        {
            tessellator.setColorOpaque(colours[i++], colours[i++], colours[i++]);
            tessellator.addVertex(points[j++], points[j++], 0.0D);
        }
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    protected float getScale()
    {
        if (cached)
        {
            return scale;
        } else
        {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            float w = scaledresolution.getScaledWidth() * 0.9F;
            float h = scaledresolution.getScaledHeight() * 0.9F;
            float multX = w / xSize;
            float multY = h / ySize;
            float mult = Math.min(multX, multY);
            if (mult > 1F && !Settings.isEnlargeInterfaces())
            {
                mult = 1F;
            }
            scale = (float)(Math.floor(mult * 1000)) / 1000F;
            cached = true;
            return scale;
        }
    }

    public void drawStringFormatted(String str, int x, int y, float mult, int color, Object... args)
    {
        drawLocalizedString(StatCollector.translateToLocalFormatted(str, args), x, y, mult, color);
    }

    public void drawLocalizedString(String str, int x, int y, float mult, int color)
    {
        GL11.glPushMatrix();
        GL11.glScalef(mult, mult, 1F);
        fontRendererObj.drawString(str, (int)((x) / mult), (int)((y) / mult), color);
        bindTexture(getComponentResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public abstract ResourceLocation getComponentResource();

    public static void bindTexture(ResourceLocation resource)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public void drawSplitString(String str, int x, int y, int w, float mult, int color)
    {
        drawSplitStringLocalized(StatCollector.translateToLocal(str), x, y, w, mult, color);
    }

    public void drawSplitStringLocalized(String str, int x, int y, int w, float mult, int color)
    {
        GL11.glPushMatrix();
        GL11.glScalef(mult, mult, 1F);
        fontRendererObj.drawSplitString(str, (int)(x / mult), (int)(y / mult), (int)(w / mult), color);
        bindTexture(getComponentResource());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
    }

    public void drawSplitStringFormatted(String str, int x, int y, int w, float mult, int color, Object... args)
    {
        drawSplitStringLocalized(StatCollector.translateToLocalFormatted(str, args), x, y, w, mult, color);
    }

    public void drawString(String str, int x, int y, int color)
    {
        drawString(str, x, y, 1F, color);
    }

    public void drawString(String str, int x, int y, float mult, int color)
    {
        drawLocalizedString(StatCollector.translateToLocal(str), x, y, mult, color);
    }

    public void drawMouseOver(String str, int x, int y, int width)
    {
        drawMouseOver(getLinesFromText(str, width), x, y);
    }

    public List<String> getLinesFromText(String str, int width)
    {
        List<String> lst = new ArrayList<String>();
        String[] words = str.split(" ");
        String line = "";
        for (String word : words)
        {
            String newLine;
            if (line.equals(""))
            {
                newLine = StatCollector.translateToLocal(word);
            } else
            {
                newLine = line + " " + StatCollector.translateToLocal(word);
            }
            if (getStringWidth(newLine) < width)
            {
                line = newLine;
            } else
            {
                lst.add(line);
                line = word;
            }
        }
        lst.add(line);

        return lst;
    }

    public int getStringWidth(String str)
    {
        return fontRendererObj.getStringWidth(str);
    }

    public void drawMouseOver(List lst, int x, int y)
    {
        if (lst != null)
        {
            drawHoveringText(lst, x, y, fontRendererObj);
        }
    }

    public void drawMouseOver(IAdvancedTooltip tooltip, int mX, int mY)
    {
        drawMouseOver(tooltip, mX, mY, mX, mY);
    }

    public void drawMouseOver(IAdvancedTooltip tooltip, int x, int y, int mX, int mY)
    {
        if (tooltip != null)
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);


            List<String> prefix = tooltip.getPrefix(this);
            List<String> suffix = tooltip.getSuffix(this);
            int prefixLength = prefix != null ? prefix.size() : 0;
            int suffixLength = suffix != null ? suffix.size() : 0;
            int extraHeight = tooltip.getExtraHeight(this);

            int width = Math.max(tooltip.getMinWidth(this), Math.max(getStringMaxWidth(prefix), getStringMaxWidth(suffix)));
            int height = extraHeight + (prefixLength + suffixLength) * 10 - 2;


            x += 12;
            y += 12;
            if (x + width > this.width)
            {
                x -= 28 + width;
            }
            if (y + height + 6 > this.height)
            {
                y = this.height - height - 6;
            }


            this.zLevel = 500.0F;
            itemRender.zLevel = 500.0F;
            int j1 = -267386864;
            this.drawGradientRect(x - 3, y - 4, x + width + 3, y - 3, j1, j1);
            this.drawGradientRect(x - 3, y + height + 3, x + width + 3, y + height + 4, j1, j1);
            this.drawGradientRect(x - 3, y - 3, x + width + 3, y + height + 3, j1, j1);
            this.drawGradientRect(x - 4, y - 3, x - 3, y + height + 3, j1, j1);
            this.drawGradientRect(x + width + 3, y - 3, x + width + 4, y + height + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            this.drawGradientRect(x - 3, y - 2, x - 3 + 1, y + height + 2, k1, l1);
            this.drawGradientRect(x + width + 2, y - 2, x + width + 3, y + height + 2, k1, l1);
            this.drawGradientRect(x - 3, y - 3, x + width + 3, y - 2, k1, k1);
            this.drawGradientRect(x - 3, y + height + 2, x + width + 3, y + height + 3, l1, l1);

            y = renderLines(prefix, x, y, true);
            tooltip.drawContent(this, x, y, mX, mY);
            y += extraHeight;
            renderLines(suffix, x, y, false);

            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    private int getStringMaxWidth(List<String> lines)
    {
        int width = 0;

        if (lines != null)
        {
            for (String line : lines)
            {
                int w = fontRendererObj.getStringWidth(line);

                if (w > width)
                {
                    width = w;
                }
            }
        }

        return width;
    }

    private int renderLines(List<String> lines, int mX, int mY, boolean first)
    {
        return renderLines(lines, mX, mY, first, false);
    }

    private int renderLines(List<String> lines, int mX, int mY, boolean first, boolean fake)
    {
        if (lines != null)
        {
            for (String line : lines)
            {
                if (!fake)
                {
                    fontRendererObj.drawStringWithShadow(line, mX, mY, -1);
                }

                if (first)
                {
                    mY += 2;
                    first = false;
                }

                mY += 10;
            }
        }

        return mY;
    }

    public int getAdvancedToolTipContentStartX(IAdvancedTooltip tooltip)
    {
        return 12;
    }

    public int getAdvancedToolTipContentStartY(IAdvancedTooltip tooltip)
    {
        if (tooltip != null)
        {
            return renderLines(tooltip.getPrefix(this), 0, 0, true, true) - 12;
        }

        return 0;
    }

    public void drawMouseOver(String str, int x, int y)
    {
        drawMouseOver(getLocalStrings(str.split("\n")), x, y);
    }

    public List<String> getLocalStrings(String[] strings)
    {
        ArrayList<String> results = new ArrayList<String>();
        for (String string : strings) results.add(StatCollector.translateToLocal(string));
        return results;
    }

    public void drawBlock(TileEntity te, int x, int y)
    {
        ItemStack item = getItemStackFromBlock(te);
        if (item != null)
        {
            drawItemStack(item, x, y);
        }
    }

    public String getBlockName(TileEntity te)
    {
        ItemStack item = getItemStackFromBlock(te);

        if (item != null)
        {
            return getItemName(item);
        }

        return "Unknown";
    }

    public String getItemName(ItemStack item)
    {
        try
        {
            List str = item.getTooltip(Minecraft.getMinecraft().thePlayer, false);
            if (str != null && str.size() > 0)
            {
                return (String)str.get(0);
            }
        } catch (Throwable ignored)
        {
        }

        return "Unknown";
    }

    private ItemStack getItemStackFromBlock(TileEntity te)
    {
        if (te != null)
        {
            if (te instanceof TileEntityClusterElement)
            {
                return ((TileEntityClusterElement)te).getItemStackFromBlock();
            }

            World world = te.getWorldObj();
            Block block = te.getBlockType();
            if (world != null && block != null)
            {
                int x = te.xCoord;
                int y = te.yCoord;
                int z = te.zCoord;

                return getItemStackFromBlock(world, x, y, z, block, world.getBlockMetadata(x, y, z));
            }
        }

        return null;
    }

    private ItemStack getItemStackFromBlock(World world, int x, int y, int z, Block block, int meta)
    {
        try
        {
            //try to get it by picking the block
            ItemStack item = block.getPickBlock(new MovingObjectPosition(x, y, z, 1, Vec3.createVectorHelper(x, y, z)), world, x, y, z, Minecraft.getMinecraft().thePlayer);
            if (item != null)
            {
                return item;
            }
        } catch (Throwable ignored)
        {
        }


        try
        {
            //try to get it from dropped items
            List<ItemStack> items = block.getDrops(world, x, y, z, meta, 0);
            if (items != null && items.size() > 0 && items.get(0) != null)
            {
                return items.get(0);
            }
        } catch (Throwable ignored)
        {
        }


        //get it from its id and meta
        return new ItemStack(block, 1, meta);
    }

    public ItemStack getItemStackFromBlock(World world, int x, int y, int z)
    {
        if (world != null)
        {
            Block block = world.getBlock(x, y, z);
            if (block != null)
            {
                return getItemStackFromBlock(world, x, y, z, block, world.getBlockMetadata(x, y, z));
            }
        }

        return null;
    }

    public void drawItemStack(ItemStack itemstack, int x, int y)
    {
        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glEnable(2896);
        this.zLevel++;
        itemRender.zLevel = zLevel;

        try
        {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack, x, y);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack, x, y, "");
        } catch (Exception var9)
        {
            if (itemstack != null && itemstack.getItem() != null && itemstack.getItemDamage() != 0)
            {
                ItemStack newStack = itemstack.copy();
                newStack.setItemDamage(0);
                this.drawItemStack(newStack, x, y);
            }
        } finally
        {
            this.zLevel--;
            itemRender.zLevel = zLevel;
            bindTexture(this.getComponentResource());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(2896);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3008);
            GL11.glPopMatrix();
        }
    }

    public void drawCenteredString(String str, int x, int y, float mult, int width, int color)
    {
        str = StatCollector.translateToLocal(str);
        drawString(str, x + (width - (int)(getStringWidth(str) * mult)) / 2, y, mult, color);
    }

    public void drawCursor(int x, int y, int z, int color)
    {
        drawCursor(x, y, z, 1F, color);
    }

    public void drawCursor(int x, int y, int z, float size, int color)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, z);
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(size, size, 0);
        GL11.glTranslatef(-x, -y, 0);
        Gui.drawRect(x, y + 1, x + 1, y + 10, color);
        GL11.glPopMatrix();
    }

    public void drawLines(int[] points, int[] colour)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_LINE_STRIP);
        GL11.glLineWidth(5 * getScale());
        tessellator.setColorOpaque(colour[0], colour[1], colour[2]);
        for (int i = 0; i< points.length; )
            tessellator.addVertex(points[i++], points[i++], 0);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @Override
    public final void drawDefaultBackground()
    {
        super.drawDefaultBackground();

        startScaling();
    }

    private void startScaling()
    {
        //start scale
        GL11.glPushMatrix();

        cached = false;
        float scale = getScale();

        GL11.glScalef(scale, scale, 1);
//        GL11.glTranslatef(guiLeft, guiTop, 0.0F);
        GL11.glTranslatef((this.width - this.xSize * scale) / (2 * scale), (this.height - this.ySize * scale) / (2 * scale), 0.0F);
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        super.drawScreen(scaleX(x), scaleY(y), f);

        stopScaling();
    }

    private void stopScaling()
    {
        //stop scale
        GL11.glPopMatrix();
    }

    protected int scaleX(float x)
    {
        float scale = getScale();
        x /= scale;
        x += guiLeft;
        x -= (this.width - this.xSize * scale) / (2 * scale);
        return (int)x;
    }

    protected int scaleY(float y)
    {
        float scale = getScale();
        y /= scale;
        y += guiTop;
        y -= (this.height - this.ySize * scale) / (2 * scale);
        return (int)y;
    }

    public void drawFluid(Fluid fluid, int x, int y)
    {
        IIcon icon = fluid.getIcon();

        if (icon == null)
        {
            if (FluidRegistry.WATER.equals(fluid))
            {
                icon = Blocks.water.getIcon(0, 0);
            } else if (FluidRegistry.LAVA.equals(fluid))
            {
                icon = Blocks.lava.getIcon(0, 0);
            }
        }

        if (icon != null)
        {
            bindTexture(TERRAIN);
            setColor(fluid.getColor());

            drawIcon(icon, x, y);

            GL11.glColor4f(1F, 1F, 1F, 1F);
            bindTexture(getComponentResource());
        }
    }

    public void drawIcon(IIcon icon, int x, int y)
    {
        drawTexturedModelRectFromIcon(x, y, icon, 16, 16);
    }

    private void setColor(int color)
    {
        float[] colorComponents = new float[3];
        for (int i = 0; i < colorComponents.length; i++)
        {
            colorComponents[i] = ((color >> (i * 8)) & 255) / 255F;
        }
        GL11.glColor4f(colorComponents[2], colorComponents[1], colorComponents[0], 1F);
    }

    public int getFontHeight()
    {
        return fontRendererObj.FONT_HEIGHT;
    }

    @Override
    @Optional.Method(modid = Mods.NEI)
    public VisiblityData modifyVisiblity(GuiContainer guiContainer, VisiblityData visiblityData)
    {
        visiblityData.showStateButtons = false;
        return visiblityData;
    }

    @Override
    @Optional.Method(modid = Mods.NEI)
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item)
    {
        return null;
    }

    @Override
    @Optional.Method(modid = Mods.NEI)
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui)
    {
        return null;
    }

    @Override
    @Optional.Method(modid = Mods.NEI)
    public boolean handleDragNDrop(GuiContainer gui, int mouseX, int mouseY, ItemStack draggedStack, int button)
    {
        return false;
    }

    @Override
    @Optional.Method(modid = Mods.NEI)
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h)
    {
        return !(x + w < this.guiLeft || x > this.guiLeft + this.width || y + h < this.guiTop || y > this.guiTop + this.height);
    }
}
