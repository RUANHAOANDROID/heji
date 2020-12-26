package com.rh.heji.utlis;

import android.graphics.Color;

import java.util.Random;

/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #
 */
public class ColorUtils {
    public static int randomColor() {
        //随机种子
        final Random mRandom = new Random(System.currentTimeMillis());

        final int baseRed = Color.red(randomBaseColor());
        final int baseGreen = Color.green(randomBaseColor());
        final int baseBlue = Color.blue(randomBaseColor());
        final int red = (baseRed + mRandom.nextInt(256)) / 2;
        final int green = (baseGreen + mRandom.nextInt(256)) / 2;
        final int blue = (baseBlue + mRandom.nextInt(256)) / 2;
        return Color.rgb(red, green, blue);
    }

    public static int randomBaseColor() {
        //随机种子
        final Random mRandom = new Random(System.currentTimeMillis());

        int[] baseColos = new int[]{Color.BLUE, Color.RED, Color.YELLOW, Color.YELLOW, Color.GREEN};
        // 基础色
        int baseColor = baseColos[mRandom.nextInt(baseColos.length)];
        return baseColor;
    }
}
