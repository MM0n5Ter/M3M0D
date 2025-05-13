package com.testmod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.testmod.ExampleMod;
import com.testmod.event.CommandWheelHandler;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import com.testmod.client.input.KeyBindings;

import java.util.ArrayList;
import java.util.List;

public class CommandWheelScreen extends Screen {

    private static class WheelSegment {
        final String id;
        final Component label;
        // final Supplier<ItemStack> iconSupplier; // 暂不实现图标
        final Runnable action;

        // 几何属性 (在init中计算)
        float startAngle; // 弧度
        float endAngle;   // 弧度
        List<Vec2> vertices = new ArrayList<>(); // 用于绘制和命中检测的多边形顶点

        WheelSegment(String id, Component label, Runnable action) {
            this.id = id;
            this.label = label;
            this.action = action;
        }
    }

    private final List<WheelSegment> segments = new ArrayList<>();
    private int wheelCenterX, wheelCenterY;
    private final int innerRadius = 30; // 轮盘内圈半径
    private final int outerRadius = 100; // 轮盘外圈半径
    private int hoveredSegmentIndex = -1; // 当前悬停的扇区索引

    public CommandWheelScreen(Component title) {
        super(title);
        segments.add(new WheelSegment("follow", Component.literal("跟随"), () -> ExampleMod.LOGGER.info("Command: Follow")));
        segments.add(new WheelSegment("stay", Component.literal("待命"), () -> ExampleMod.LOGGER.info("Command: Stay")));
        segments.add(new WheelSegment("attack", Component.literal("攻击"), () -> ExampleMod.LOGGER.info("Command: Attack")));
        segments.add(new WheelSegment("skill1", Component.literal("技能1"), () -> ExampleMod.LOGGER.info("Command: Skill 1")));
        segments.add(new WheelSegment("skill2", Component.literal("技能2"), () -> ExampleMod.LOGGER.info("Command: Skill 2")));
        segments.add(new WheelSegment("skill3", Component.literal("技能3"), () -> ExampleMod.LOGGER.info("Command: Skill 3")));
    }

    @Override
    protected void init() {
        super.init();
        ExampleMod.LOGGER.info("Registering command wheel screen..."); // 这条日志应该在构造函数或者更早的静态初始化中，init可能会被多次调用
        this.wheelCenterX = this.width / 2;
        this.wheelCenterY = this.height / 2;

        int numSegments = segments.size();
        if (numSegments == 0) return;

        float angleStep = (float) (2 * Math.PI / numSegments);
        float currentAngle = (float) (-Math.PI / 2 - angleStep / 2); // 从正上方开始，使第一个扇区中心向上

        for (WheelSegment segment : segments) {
            segment.startAngle = currentAngle;
            segment.endAngle = currentAngle + angleStep;
            segment.vertices.clear();

            int arcResolution = 20; // 弧线细分度
            float anglePerSlice = (segment.endAngle - segment.startAngle) / arcResolution; // 注意是 (end-start)/res

            // 按照 TRIANGLE_STRIP 的要求，交替添加内外弧顶点
            for (int j = 0; j <= arcResolution; j++) {
                float angle = segment.startAngle + j * anglePerSlice;

                // 外弧点
                segment.vertices.add(new Vec2(
                        (float) (wheelCenterX + outerRadius * Math.cos(angle)),
                        (float) (wheelCenterY + outerRadius * Math.sin(angle))
                ));
                // 内弧点
                segment.vertices.add(new Vec2(
                        (float) (wheelCenterX + innerRadius * Math.cos(angle)),
                        (float) (wheelCenterY + innerRadius * Math.sin(angle))
                ));
            }
            currentAngle += angleStep;
        }
        }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        this.hoveredSegmentIndex = -1; // 重置悬停

        // --- 第一遍：绘制所有扇区的填充颜色 ---
        for (int i = 0; i < segments.size(); i++) {
            WheelSegment segment = segments.get(i);
            if (segment.vertices.isEmpty()) continue;

            boolean isMouseOver = isMouseInSegment(mouseX, mouseY, segment);
            if (isMouseOver) {
                this.hoveredSegmentIndex = i;
            }
            int color = isMouseOver ? 0xAA888888 : 0xAA555555; // 半透明灰
            drawSegmentFill(guiGraphics, segment, color); // 改个名，表示画填充
        }

        // --- 第二遍：绘制所有扇区的分界线和标签 ---
        int lineColor = 0xFFFFFFFF; // 白色分界线，不透明
        float lineWidth = 1.0f;    // 线宽 (BufferBuilder画线时，线宽是固定的，但可以通过画细长的矩形模拟)
        // 或者我们可以直接画构成扇区边界的线段

        for (WheelSegment segment : segments) {
            if (segment.vertices.isEmpty()) continue;

            drawSegmentOutline(guiGraphics, segment, lineColor, lineWidth); // 新的绘制边界方法

            // 绘制标签 (在边界之上)
            float midAngle = segment.startAngle + (segment.endAngle - segment.startAngle) / 2;
            float textRadius = innerRadius + (float) (outerRadius - innerRadius) / 2;
            int textX = (int) (wheelCenterX + textRadius * Math.cos(midAngle));
            int textY = (int) (wheelCenterY + textRadius * Math.sin(midAngle));
            guiGraphics.drawCenteredString(this.font, segment.label, textX, textY - this.font.lineHeight / 2, 0xFFFFFF);
        }

        // 绘制轮盘中心点（在所有扇区和边界线之上）
        int centerCircleColor = 0xAA333333; // 暗灰色半透明
        int centerCircleRadius = innerRadius - 5; // 比内圆半径小一点
        if (centerCircleRadius > 0) {
            // 你可以用画很多小三角形的方式画一个实心圆，或者直接画一个有颜色的方块/用其他方法画圆
            // 这里我们先画一个简单的中心小方块作为示意
            guiGraphics.fill(wheelCenterX - 3, wheelCenterY - 3, wheelCenterX + 3, wheelCenterY + 3, 0xFF808080);
        }

        int centerFillColor = 0xFF404040; // 例如，不透明的深灰色
        int centerFillRadius = this.innerRadius - 2; // 确保在扇区内边界之内
        if (centerFillRadius > 0) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            Matrix4f matrix = guiGraphics.pose().last().pose();

            bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            // 中心点
            bufferBuilder.vertex(matrix, wheelCenterX, wheelCenterY, 0).color(centerFillColor).endVertex();
            // 圆周上的点
            int circleSegments = 30; // 圆的平滑度
            for (int k = 0; k <= circleSegments; k++) {
                float angle = (float) (k * 2 * Math.PI / circleSegments);
                bufferBuilder.vertex(matrix,
                                (float) (wheelCenterX + centerFillRadius * Math.cos(angle)),
                                (float) (wheelCenterY + centerFillRadius * Math.sin(angle)),
                                0)
                        .color(centerFillColor).endVertex();
            }
            tesselator.end();
            RenderSystem.disableBlend();
        }

    }

    private void drawSegmentFill(GuiGraphics guiGraphics, WheelSegment segment, int color) {
        if (segment.vertices.size() < 4) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        Matrix4f matrix = guiGraphics.pose().last().pose();

        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (Vec2 vertex : segment.vertices) {
            bufferBuilder.vertex(matrix, vertex.x, vertex.y, 0).color(color).endVertex();
        }
        tesselator.end();
        RenderSystem.disableBlend();
    }

    // 新增：绘制扇区边界线的方法
    private void drawSegmentOutline(GuiGraphics guiGraphics, WheelSegment segment, int color, float lineWidth) {
        if (segment.vertices.size() < 4) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // 使用 getPositionColorShader()，它适用于绘制带颜色的顶点位置数据
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        // RenderSystem.lineWidth(lineWidth); 设置线宽 (注意: 这个在核心OpenGL中通常只对GL_LINES有效，对于strip可能无效或表现不一)

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        Matrix4f matrix = guiGraphics.pose().last().pose();

        // --- 绘制两条径向边线 ---
        // 从 segment.vertices 获取正确的点。由于顶点是 外0, 内0, 外1, 内1 ...
        // 第一条径向线是 外0 和 内0
        Vec2 outerStart = segment.vertices.get(0);
        Vec2 innerStart = segment.vertices.get(1);
        // 最后一条径向线是 外N 和 内N
        Vec2 outerEnd = segment.vertices.get(segment.vertices.size() - 2); // 倒数第二个点是最后一个外弧点
        Vec2 innerEnd = segment.vertices.get(segment.vertices.size() - 1); // 最后一个点是最后一个内弧点

        bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        // 第一条径向线 (segment的起始边)
        bufferBuilder.vertex(matrix, outerStart.x, outerStart.y, 0).color(color).endVertex();
        bufferBuilder.vertex(matrix, innerStart.x, innerStart.y, 0).color(color).endVertex();
        // 第二条径向线 (segment的结束边)
        bufferBuilder.vertex(matrix, outerEnd.x, outerEnd.y, 0).color(color).endVertex();
        bufferBuilder.vertex(matrix, innerEnd.x, innerEnd.y, 0).color(color).endVertex();
        tesselator.end();


        // --- 绘制内外弧线 ---
        // 外弧线
        bufferBuilder.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int j = 0; j < segment.vertices.size(); j += 2) { // 取所有外弧点 (索引 0, 2, 4...)
            Vec2 v = segment.vertices.get(j);
            bufferBuilder.vertex(matrix, v.x, v.y, 0).color(color).endVertex();
        }
        tesselator.end();

        // 内弧线
        bufferBuilder.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        for (int j = 1; j < segment.vertices.size(); j += 2) { // 取所有内弧点 (索引 1, 3, 5...)
            Vec2 v = segment.vertices.get(j);
            bufferBuilder.vertex(matrix, v.x, v.y, 0).color(color).endVertex();
        }
        tesselator.end();

        // RenderSystem.lineWidth(1.0f); // 恢复默认线宽 (如果之前设置了且有效)
        RenderSystem.disableBlend();
    }

    private boolean isMouseInSegment(int mouseX, int mouseY, WheelSegment segment) {
        if (segment.vertices.isEmpty()) return false;

        // 将鼠标坐标转换为相对于轮盘中心
        float dx = mouseX - wheelCenterX;
        float dy = mouseY - wheelCenterY;

        // 1. 检查距离是否在内外半径之间
        float distSq = dx * dx + dy * dy;
        if (distSq < innerRadius * innerRadius || distSq > outerRadius * outerRadius) {
            return false;
        }

        // 2. 检查角度是否在扇区角度范围内
        // Math.atan2(y, x) 返回的是从x轴正方向到点(x,y)的弧度，范围是 -PI 到 PI
        // 我们的角度是从正上方（-PI/2）开始，顺时针增加
        float mouseAngle = (float) Math.atan2(dy, dx);

        // 将所有角度归一化到 0 到 2PI 或 -PI 到 PI 范围以方便比较
        // 这是一个比较复杂的点，因为角度的周期性。
        // 简单的方法是检查点是否在由两条射线和两条弧线构成的多边形内部。
        // 我们可以使用射线法或环绕数法进行点在多边形内的判断。

        // 简化的角度检查 (可能需要根据你的角度定义方式调整)
        // 需要将 segment.startAngle 和 segment.endAngle 以及 mouseAngle 调整到一致的比较区间
        // 例如都调整到 [0, 2PI)
        float normalizedMouseAngle = normalizeAngle(mouseAngle);
        float normalizedStartAngle = normalizeAngle(segment.startAngle);
        float normalizedEndAngle = normalizeAngle(segment.endAngle);

        if (normalizedStartAngle <= normalizedEndAngle) {
            return normalizedMouseAngle >= normalizedStartAngle && normalizedMouseAngle <= normalizedEndAngle;
        } else { // 跨越了0点的情况 (例如 start=330度, end=30度)
            return normalizedMouseAngle >= normalizedStartAngle || normalizedMouseAngle <= normalizedEndAngle;
        }
    }

    // 将角度归一化到 [0, 2PI)
    private float normalizeAngle(float angle) {
        return (float) (angle % (2 * Math.PI) + (angle < 0 ? (2 * Math.PI) : 0));
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // 左键
            if (this.hoveredSegmentIndex != -1 && this.hoveredSegmentIndex < segments.size()) {
                WheelSegment clickedSegment = segments.get(this.hoveredSegmentIndex);
                clickedSegment.action.run(); // 执行扇区关联的动作
                this.onClose(); // 关闭轮盘
                return true;
            }
            // 如果点击在轮盘背景（非扇区部分），也关闭轮盘
            this.onClose();
            return true; // 消耗点击
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 指挥轮盘打开时不暂停游戏，以便观察战场
    }

    @Override
    public void onClose() {
        CommandWheelHandler.isCommandWheelOpen = false;
        super.onClose();
        ExampleMod.LOGGER.info("CommandWheelScreen closed and global state updated.");
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        if(KeyBindings.OPEN_COMMAND_WHEEL_KEY != null && KeyBindings.OPEN_COMMAND_WHEEL_KEY.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
