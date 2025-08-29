package com.shot_tank.services;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Util {
    public static boolean checkCircleRectangleCollision(Circle circle, Rectangle rect) {
        double cx = circle.getTranslateX();
        double cy = circle.getTranslateY();
        double r = circle.getRadius();

        double rx = rect.getTranslateX();
        double ry = rect.getTranslateY();
        double width = rect.getWidth();
        double height = rect.getHeight();

        double closestX = clamp(cx, rx, rx + width);
        double closestY = clamp(cy, ry, ry + height);

        // Tính khoảng cách từ tâm circle đến điểm gần nhất
        double distanceX = cx - closestX;
        double distanceY = cy - closestY;
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        return distanceSquared < r * r;
    }
    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
}
