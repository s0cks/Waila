package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import codechicken.nei.forge.GuiContainerManager;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

import java.awt.Dimension;
import java.awt.Point;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorEntity;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraftforge.common.Configuration;
import static codechicken.core.gui.GuiDraw.*;

public class OverlayRenderer {

	protected static boolean hasBlending;
	protected static boolean hasLight;
	protected static int     boundTexIndex;   	
	
    public static void renderOverlay()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(!(mc.currentScreen == null &&
             mc.theWorld != null &&
             mc.isGuiEnabled() &&
             !mc.gameSettings.keyBindPlayerList.pressed &&
             ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true) &&
             RayTracing.instance().getTarget()      != null))
        	return;
    
        if (RayTracing.instance().getTarget().typeOfHit == EnumMovingObjectType.TILE && RayTracing.instance().getTargetStack() != null)
        {
            renderOverlay(RayTracing.instance().getTargetStack(), WailaTickHandler.instance().tooltip);
        }
        
        if (RayTracing.instance().getTarget().typeOfHit == EnumMovingObjectType.ENTITY)
        {
        	renderOverlay(DataAccessorEntity.instance.getEntity(), WailaTickHandler.instance().tooltip); // Might need change for the override       	
        }
    }		
    
    public static void renderOverlay(ItemStack stack, Tooltip tooltip)
    {
    	//TrueTypeFont font = (TrueTypeFont)mod_Waila.proxy.getFont();
    	
    	GL11.glPushMatrix();
    	
    	GL11.glScalef(OverlayConfig.scale, OverlayConfig.scale, 1.0f);
    	
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        drawTooltipBox(tooltip.x, tooltip.y, tooltip.w, tooltip.h, OverlayConfig.bgcolor, OverlayConfig.gradient1, OverlayConfig.gradient2);
        
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);     
        for (int i = 0; i < tooltip.textData.size(); i++)
        	//FontHelper.drawString(textData.get(i), x + 24, y + ty + 10*i, font, 1.0f, 1.0f, new float[] {1.0f, 1.0f, 1.0f});
            drawString(tooltip.textData.get(i), tooltip.x + 24, tooltip.y + tooltip.ty + 10*i, OverlayConfig.fontcolor, true);
        GL11.glDisable(GL11.GL_BLEND);
        
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
        if (stack.getItem() != null)
            GuiContainerManager.drawItem(tooltip.x+5, tooltip.y+tooltip.h/2-8, stack);
        
    	GL11.glPopMatrix();        
    }    
    
    public static void renderOverlay(Entity entity, Tooltip tooltip)
    {
    	GL11.glPushMatrix();    	
    	
    	GL11.glScalef(OverlayConfig.scale, OverlayConfig.scale, 1.0f);    	
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        drawTooltipBox(tooltip.x, tooltip.y, tooltip.w, tooltip.h, OverlayConfig.bgcolor, OverlayConfig.gradient1, OverlayConfig.gradient2);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);         
        for (int i = 0; i < tooltip.textData.size(); i++)
            drawString(tooltip.textData.get(i), tooltip.x + 6, tooltip.y + tooltip.ty + 10*i, OverlayConfig.fontcolor, true);
        GL11.glDisable(GL11.GL_BLEND);
        
        //RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
    	GL11.glPopMatrix();         
    }     

    public static void saveGLState(){
		hasBlending   = GL11.glGetBoolean(GL11.GL_BLEND);
		hasLight      = GL11.glGetBoolean(GL11.GL_LIGHTING);
    	boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);  
    	GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    }
    
    public static void loadGLState(){
    	if (hasBlending) GL11.glEnable(GL11.GL_BLEND); else GL11.glDisable(GL11.GL_BLEND);
    	if (hasLight) GL11.glEnable(GL11.GL_LIGHTING); else	GL11.glDisable(GL11.GL_LIGHTING);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);
    	GL11.glPopAttrib();
    	//GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }    
    
    public static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2)
    {
        //int bg = 0xf0100010;
        drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
        drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
        drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);//center
        drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
        drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
        //int grad1 = 0x505000ff;
        //int grad2 = 0x5028007F;
        drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
        drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);
        
        drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
        drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
    }    
    
}
