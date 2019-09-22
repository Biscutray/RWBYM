package be.bluexin.rwbym.entity.renderer;

import be.bluexin.rwbym.RWBYModels;
import be.bluexin.rwbym.entity.EntityRen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenRender extends RenderBiped<EntityRen>
{

    public static RenRender.Factory FACTORY = new RenRender.Factory();

    public RenRender(RenderManager renderManagerIn, ModelBiped modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    protected boolean canRenderName(EntityRen entity) {
        return false;
    }

    protected void preRenderCallback(EntityRen entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(1F, 1F, 1F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityRen entity) {
        return new ResourceLocation(RWBYModels.MODID,"textures/entity/ren.png");
    }

    public static class Factory implements IRenderFactory<EntityRen> {

        @Override
        public Render<? super EntityRen> createRenderFor(RenderManager manager) {
            return new RenRender(manager, new ModelBiped(), 0);
        }

    }

}