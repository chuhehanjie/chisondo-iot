package com.chisondo.iot.common.constant;

public class Constant {
    public interface DevStatus {
        /**
         * 心跳
         */
        int HEART_BEAT = 1;
        /**
         * 电源按键
         */
        int POWER_BUTTON = 2;
        /**
         * 浓度按键
         */
        int  CONCENTRATION_BUTTON = 3;
        /**
         * 启动按键
         */
        int ENABLE_BUTTON = 4;
        /**
         * 保温按键
          */
        int KEEP_WARM_BUTTON = 5;
        /**
         * 确认按键
         */
        int CONFIRM_BUTTON = 6;
        /**
         * 沏茶自动结束（倒计时到0秒）
         */
        int TEA_AUTO_FINISH = 7;
        /**
         * 缺水状态
         */
        int LACK_WATER = 8;
        /**
         * 缺茶状态
         */
        int LACK_TEA = 9;
    }

    public interface WorkStatus {
        // 空闲
        int IDLE = 0;
        // 沏茶中
        int MAKING_TEA = 1;
        // 洗茶中
        int CLEANING_TEA = 2;
        // 烧水中
        int BOILING_WATER = 3;
    }

    public interface WarmStatus {
        // 未保温
        int NOT_KEEP_WARM = 0;
        // 保温中
        int KEEPING_WARM = 1;
    }

    /**
     * 浓度状态
     */
    public interface ConcentrationStatus {
        int HIGH = 1; // 浓

        int MIDDLE = 2; // 中

        int LOW = 3; // 淡
    }

    /**
     * 异常状态
     */
    public interface ErrorStatus {
        int NORMAL = 0; // 正常
        int LACK_WATER = 1; // 缺水
        int LACK_TEA = 2; // 缺茶
        int LACK_WATER_TEA = 3; // 缺水缺茶
        int NOT_CONNECTED_WIFI = 4; // 未连接Wi-Fi
    }


}
