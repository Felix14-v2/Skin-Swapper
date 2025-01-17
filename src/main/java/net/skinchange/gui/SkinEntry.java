package net.skinchange.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SkinEntry extends AlwaysSelectedEntryListWidget.Entry<SkinEntry>
{
    protected final MinecraftClient client;
    public String fname;
    public File skin_file;
    public boolean oldSkin;
    public String skinType;
    protected final SkinListWidget list;
    public Identifier rawSkin;
    public Identifier processedSkin;
    private static int identify = 0;

    public SkinEntry(String text, File file, SkinListWidget list, NativeImage rawNativeImage, boolean old)
    {
        identify++;
        this.fname = text;
        this.client = MinecraftClient.getInstance();
        this.skin_file = file;
        this.list = list;
        this.oldSkin = old;
        this.rawSkin = new Identifier("skinswapper_raw:"+identify); //raw skin file uploaded to minecraft.net
        this.processedSkin = new Identifier("skinswapper_processed:"+identify); //skin file to be rendered

        this.skinType = genSkinType();

        //registers the texture for the icon
        NativeImageBackedTexture rawImageBackedTexture = new NativeImageBackedTexture(rawNativeImage);
        this.client.getTextureManager().registerTexture(rawSkin, rawImageBackedTexture);

        //registers texture to be rendered in 3d
        NativeImage processedNativeImage = SkinUtils.remapTexture(rawNativeImage);
        NativeImageBackedTexture processedImageBackedTexture = new NativeImageBackedTexture(processedNativeImage);
        this.client.getTextureManager().registerTexture(processedSkin, processedImageBackedTexture);
    }

    public void render(MatrixStack matrices, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta)
    {
        TextRenderer font = this.client.textRenderer;
        renderIcon(x,y,matrices);
        int maxNameWidth = rowWidth - 32 - 3;
        if (font.getWidth(fname) > maxNameWidth)
        {
			fname = font.trimToWidth(fname, maxNameWidth - font.getWidth("...")) + "...";
		}
        font.draw(matrices, fname, x + 35, y + 5, 0xFFFFFF);

    }

    @Override
    public boolean mouseClicked(double v, double v1, int i)
    {
        list.select(this);
		return true;
    }

    @Override
    public Text method_37006() {
        return new LiteralText(fname);
    }
    
    private void renderIcon(int k, int j, MatrixStack matrices)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, rawSkin);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if(oldSkin)
        {
            //head icon
            DrawableHelper.drawTexture(matrices, k, j, 32.0F, 32.0F, 32, 32, 256, 128);
            if(!skin_file.getName().equals("Notch.png")) //Notch's skin uses black instead of transparency
            {
                DrawableHelper.drawTexture(matrices, k, j, 160.0F, 32.0F, 32, 32, 256, 128);
            }
        }
        else
        {
            //head icon
            DrawableHelper.drawTexture(matrices, k, j, 32.0F, 32.0F, 32, 32, 256, 256);
            DrawableHelper.drawTexture(matrices, k, j, 160.0F, 32.0F, 32, 32, 256, 256);
        }
    }

    public void toggleSkinType()
    {
        if(skinType.equals("classic"))
        {
            skinType = "slim";
        }
        else
        {
            skinType = "classic";
        }
    }

    public String genSkinType()
    {
        try{
            BufferedImage image = ImageIO.read(skin_file);
            int pixel = image.getRGB(50,19);

            if((pixel>>24) == 0x00)
            {
                return "slim";
            }
            return "classic";
        }catch(Exception e){
            return null;
        }
    }

    public void deleteSkin()
    {
        skin_file.delete();
    }


    public void drawSkin(int k, int j, MatrixStack matrices)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, rawSkin);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if(oldSkin)
        {
            //head icon
            DrawableHelper.drawTexture(matrices, k, j, 32.0F, 32.0F, 32, 32, 256, 128);
            if(!skin_file.getName().equals("Notch.png")) //Notch's skin uses black instead of transparency
            {
                DrawableHelper.drawTexture(matrices, k, j, 160.0F, 32.0F, 32, 32, 256, 128);
            }

            //body
            DrawableHelper.drawTexture(matrices, k, j+32, 80.0F, 80.0F, 32, 48, 256, 128);

            //arm

            //left
            if(skinType.equals("slim"))
            {
                DrawableHelper.drawTexture(matrices, k-12, j+32, 176.0F, 80.0F, 12, 48, 256, 128);
            }

            else
            {
                DrawableHelper.drawTexture(matrices, k-16, j+32, 176.0F, 80.0F, 16, 48, 256, 128);
            }

            //right(flips horizontal)
            if(skinType.equals("slim"))
            {
                DrawableHelper.drawTexture(matrices, k+32, j+32, 184F, 80.0F, 4, 48, 256, 128);
                DrawableHelper.drawTexture(matrices, k+36, j+32, 180F, 80.0F, 4, 48, 256, 128);
                DrawableHelper.drawTexture(matrices, k+40, j+32, 176F, 80.0F, 4, 48, 256, 128);
            }

            else
            {
                DrawableHelper.drawTexture(matrices, k+32, j+32, 188F, 80.0F, 4, 48, 256, 128);
                DrawableHelper.drawTexture(matrices, k+36, j+32, 184F, 80.0F, 4, 48, 256, 128);
                DrawableHelper.drawTexture(matrices, k+40, j+32, 180F, 80.0F, 4, 48, 256, 128);
                DrawableHelper.drawTexture(matrices, k+44, j+32, 176F, 80.0F, 4, 48, 256, 128);
            }


            //leg

            //left
            DrawableHelper.drawTexture(matrices, k, j+80, 16.0F, 80.0F, 16, 48, 256, 128);

            //right(flips horizontal)
            DrawableHelper.drawTexture(matrices, k+28, j+80, 16.0F, 80.0F, 4, 48, 256, 128);
            DrawableHelper.drawTexture(matrices, k+24, j+80, 20.0F, 80.0F, 4, 48, 256, 128);
            DrawableHelper.drawTexture(matrices, k+20, j+80, 24.0F, 80.0F, 4, 48, 256, 128);
            DrawableHelper.drawTexture(matrices, k+16, j+80, 28.0F, 80.0F, 4, 48, 256, 128);
        }

        else
        {
            //head icon
            DrawableHelper.drawTexture(matrices, k, j, 32.0F, 32.0F, 32, 32, 256, 256);
            DrawableHelper.drawTexture(matrices, k, j, 160.0F, 32.0F, 32, 32, 256, 256);

            //body
            DrawableHelper.drawTexture(matrices, k, j+32, 80.0F, 80.0F, 32, 48, 256, 256);
            DrawableHelper.drawTexture(matrices, k, j+32, 80.0F, 144.0F, 32, 48, 256, 256);

            //left arm
            if(skinType.equals("slim"))
            {
                DrawableHelper.drawTexture(matrices, k-12, j+32, 176.0F, 80.0F, 12, 48, 256, 256);
                DrawableHelper.drawTexture(matrices, k-12, j+32, 176.0F, 144.0F, 12, 48, 256, 256);
            }

            else
            {
                DrawableHelper.drawTexture(matrices, k-16, j+32, 176.0F, 80.0F, 16, 48, 256, 256);
                DrawableHelper.drawTexture(matrices, k-16, j+32, 176.0F, 144.0F, 16, 48, 256, 256);
            }

            //right arm
            if(skinType.equals("slim"))
            {
                DrawableHelper.drawTexture(matrices, k+32, j+32, 144.0F, 208.0F, 12, 48, 256, 256);
                DrawableHelper.drawTexture(matrices, k+32, j+32, 208.0F, 208.0F, 12, 48, 256, 256);
            }
            else
            {
                DrawableHelper.drawTexture(matrices, k+32, j+32, 144.0F, 208.0F, 16, 48, 256, 256);
                DrawableHelper.drawTexture(matrices, k+32, j+32, 208.0F, 208.0F, 16, 48, 256, 256);
            }

            //reght leg
            DrawableHelper.drawTexture(matrices, k+16, j+80, 16.0F, 80.0F, 16, 48, 256, 256);
            DrawableHelper.drawTexture(matrices, k+16, j+80, 16.0F, 144.0F, 16, 48, 256, 256);

            //left leg
            DrawableHelper.drawTexture(matrices, k, j+80, 80.0F, 208.0F, 16, 48, 256, 256);
            DrawableHelper.drawTexture(matrices, k, j+80, 16.0F, 208.0F, 16, 48, 256, 256);
        }
    }
}