package com.short_tank.services;

import com.short_tank.models.Player;

public class Util {
    public static boolean checkPlayerCollision(Player player, double x, double y) {
        double cx = x;
        double cy = y;
        double r = 5;

        double rx = player.location.x - player.size / 2;
        double ry = player.location.y - player.size / 2;
        double width = player.size;
        double height = player.size;
        double closestX = clamp(cx, rx, rx + width);
        double closestY = clamp(cy, ry, ry + height);

        double distanceX = cx - closestX;
        double distanceY = cy - closestY;
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        return distanceSquared < r * r;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
}
