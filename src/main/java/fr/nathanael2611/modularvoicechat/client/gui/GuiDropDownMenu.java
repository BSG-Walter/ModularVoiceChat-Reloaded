package fr.nathanael2611.modularvoicechat.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiDropDownMenu extends GuiButton
{
    private String[] array;
    private boolean[] mouseOn;
    private final int prevHeight;
    private int amountOfItems;
    public boolean dropDownMenu = false;
    public int selectedInteger;

    public GuiDropDownMenu(int id, int x, int y, int width, int height, String par6Str, String[] array)
    {
        super(id, x, y, width, height, par6Str);
        this.prevHeight = super.height;
        this.array = array;
        this.amountOfItems = array.length;
        this.mouseOn = new boolean[this.amountOfItems];
        if (this.displayString == null) this.displayString = "";
    }

    public GuiDropDownMenu(int id, int x, int y, String par4Str, String[] array)
    {
        super(id, x, y, par4Str);
        this.prevHeight = super.height;
        this.array = array;
        this.amountOfItems = array.length;
        this.mouseOn = new boolean[this.amountOfItems];
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y, float partialTicks)
    {
        if (super.visible)
        {
            if (this.dropDownMenu && this.array.length != 0) super.height = this.prevHeight * (this.amountOfItems + 1);
            else super.height = this.prevHeight;

            FontRenderer fontrenderer = mc.fontRenderer;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            super.hovered = x >= super.x && y >= super.y && x < super.x + super.width && y < super.y + super.height;
            this.getHoverState(super.hovered);
            this.mouseDragged(mc, x, y);
            int l = 14737632;
            drawRect(super.x - 1, super.y - 1, super.x + super.width + 1, super.y + super.height + 1, -6250336);
            drawRect(super.x, super.y, super.x + super.width, super.y + super.height, -16777216);
            drawRect(super.x - 1, super.y + this.prevHeight, super.x + super.width + 1, super.y + this.prevHeight + 1, -6250336);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean u = true;
            short var9;

            if (this.dropDownMenu && this.array.length != 0) var9 = 228;
            else var9 = 242;

            if (!this.enabled) l = -6250336;

            String normalName = normalText(this.displayString.substring(0, Math.min(this.displayString.length(), 23)));
            this.drawCenteredString(fontrenderer, normalName, super.x + super.width / 2, super.y + (this.prevHeight - 8) / 2, l);
            GL11.glPushMatrix();

            if (this.dropDownMenu && this.array.length != 0)
            {
                for (int i = 0; i < this.amountOfItems; ++i)
                {
                    this.mouseOn[i] = this.inBounds(x, y, super.x, super.y + this.prevHeight * (i + 1), super.width, this.prevHeight);
                    String s = normalText(this.array[i].substring(0, Math.min(this.array[i].length(), 26)) + "..");
                    this.drawCenteredString(fontrenderer, s, super.x + super.width / 2, super.y + this.prevHeight * (i + 1) + 7, this.mouseOn[i] ? 16777120 : 14737632);
                }
            }
            else
            {
                for (int i = 0; i < this.amountOfItems; ++i)
                {
                    this.mouseOn[i] =  false;
                }
            }
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            //IndependentGUITexture.TEXTURES.bindTexture(Minecraft.getMinecraft());
            //this.drawTexturedModalRect(super.x + super.width - 15, super.y + 2, var9, 0, 14, 14);
        }
    }

    private String normalText(String text)
    {
        return text;
    }

    public int getMouseOverInteger()
    {
        for (int i = 0; i < this.mouseOn.length; ++i)
        {
            if (this.mouseOn[i]) return i;
        }
        return -1;
    }

    public boolean isMouseOver()
    {
        return this.getMouseOverInteger() != -1;
    }

    public String getSelectedText()
    {
        int selected = getMouseOverInteger();
        if (selected >= 0 && selected < amountOfItems)
        {
            return array[selected];
        } else
        {
            return "";
        }
    }

    private boolean inBounds(int x, int y, int posX, int posY, int width, int height)
    {
        return this.enabled && super.visible && x >= posX && y >= posY && x < posX + width && y < posY + height;
    }
}