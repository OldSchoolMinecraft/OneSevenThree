package net.oneseventhree.game.graphics.render;

import net.oneseventhree.game.graphics.Camera;
import net.oneseventhree.game.graphics.utils.IndexedMesh;
import net.oneseventhree.game.graphics.utils.Shader;
import net.oneseventhree.game.graphics.utils.Vertex;
import static org.lwjgl.opengl.GL11.*;

import java.util.*;

public class CubeRenderer extends Renderer
{
    private float pitch = 0.0f;

    public CubeRenderer()
    {

    }

    @Override
    public void render()
    {
        game.setActiveShader(Shader.WORLD);
        game.getActiveShader().bind();
        game.getActiveShader().setUniform("worldMatrix", game.getWorldMatrix());
        game.getActiveShader().setUniform("projectionMatrix", Camera.projection);
        game.getActiveShader().setUniform("viewMatrix", Camera.view);

        float wid = 16, hei = 16;
        float len = 16;

        glRotatef(pitch, 0.1f, 0.0f, 0.0f);

        glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );

        glBegin(GL_QUADS);
        glColor3f(0,1,1);
        glVertex3f(-wid/2f, hei/2f, len/2f);
        glVertex3f(-wid/2f, hei/2f,-len/2f);
        glVertex3f( wid/2f, hei/2f,-len/2f);
        glVertex3f( wid/2f, hei/2f, len/2f);
        glColor3f(1,0,0);
        glVertex3f(-wid/2f,-hei/2f,-len/2f);
        glVertex3f( wid/2f,-hei/2f,-len/2f);
        glVertex3f( wid/2f, hei/2f,-len/2f);
        glVertex3f(-wid/2f, hei/2f,-len/2f);
        glColor3f(0,1,0);
        glVertex3f(-wid/2f,-hei/2f,-len/2f);
        glVertex3f(-wid/2f,-hei/2f, len/2f);
        glVertex3f(-wid/2f, hei/2f, len/2f);
        glVertex3f(-wid/2f, hei/2f,-len/2f);
        glColor3f(0,0,1);
        glVertex3f( wid/2f,-hei/2f, len/2f);
        glVertex3f( wid/2f,-hei/2f,-len/2f);
        glVertex3f( wid/2f, hei/2f,-len/2f);
        glVertex3f( wid/2f, hei/2f, len/2f);
        glColor3f(1,1,0);
        glVertex3f(-wid/2f,-hei/2f,-len/2f);
        glVertex3f( wid/2f,-hei/2f,-len/2f);
        glVertex3f( wid/2f,-hei/2f, len/2f);
        glVertex3f(-wid/2f,-hei/2f, len/2f);
        glColor3f(1,0,1);
        glVertex3f(-wid/2f,-hei/2f, len/2f);
        glVertex3f( wid/2f,-hei/2f, len/2f);
        glVertex3f( wid/2f, hei/2f, len/2f);
        glVertex3f(-wid/2f, hei/2f, len/2f);
        glEnd();

        game.getActiveShader().unbind();
    }
}
