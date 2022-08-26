package net.oneseventhree.game.graphics.ui;

import imgui.ImGui;

public class Debug extends ImGuiLayer
{
    public void draw()
    {
        ImGui.begin("Debug");

        ImGui.text("Drawing vertices: " + 0);

        ImGui.end();
    }
}
