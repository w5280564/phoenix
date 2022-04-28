package com.zhengshuo.phoenix.ui.chat.weight.emojicon;


import com.zhengshuo.phoenix.R;

public class EmojiconDatas {

    private static String[] emojis = new String[]{

            SmileUtils.emoji_001,
            SmileUtils.emoji_002,
            SmileUtils.emoji_003,
            SmileUtils.emoji_004,
            SmileUtils.emoji_005,
            SmileUtils.emoji_006,
            SmileUtils.emoji_007,
            SmileUtils.emoji_008,
            SmileUtils.emoji_009,
            SmileUtils.emoji_010,
            SmileUtils.emoji_11,
            SmileUtils.emoji_12,
            SmileUtils.emoji_13,
            SmileUtils.emoji_14,
            SmileUtils.emoji_15,
            SmileUtils.emoji_16,
            SmileUtils.emoji_17,
            SmileUtils.emoji_18,
            SmileUtils.emoji_19,
            SmileUtils.emoji_20,
            SmileUtils.emoji_21,
            SmileUtils.emoji_22,
            SmileUtils.emoji_23,
            SmileUtils.emoji_24,
            SmileUtils.emoji_25,
            SmileUtils.emoji_26,
            SmileUtils.emoji_27,
            SmileUtils.emoji_28,
            SmileUtils.emoji_29,
            SmileUtils.emoji_30,
            SmileUtils.emoji_31,
            SmileUtils.emoji_32,
            SmileUtils.emoji_33,
            SmileUtils.emoji_34,
            SmileUtils.emoji_35,
            SmileUtils.emoji_36,
            SmileUtils.emoji_37,
            SmileUtils.emoji_38,
            SmileUtils.emoji_39,
            SmileUtils.emoji_40,
            SmileUtils.emoji_41,
            SmileUtils.emoji_42,
            SmileUtils.emoji_43,
            SmileUtils.emoji_45,
            SmileUtils.emoji_46,
            SmileUtils.emoji_47,
            SmileUtils.emoji_48,
            SmileUtils.emoji_49,
            SmileUtils.emoji_50,
            SmileUtils.emoji_51,
            SmileUtils.emoji_52,
            SmileUtils.emoji_53,
            SmileUtils.emoji_54,
            SmileUtils.emoji_55,
            SmileUtils.emoji_56,
            SmileUtils.emoji_57,
            SmileUtils.emoji_58,
            SmileUtils.emoji_59,
            SmileUtils.emoji_60,
            SmileUtils.emoji_61,
            SmileUtils.emoji_62,
            SmileUtils.emoji_63,
            SmileUtils.emoji_64,
            SmileUtils.emoji_65,
            SmileUtils.emoji_66,
            SmileUtils.emoji_67,
            SmileUtils.emoji_68,
            SmileUtils.emoji_69,
            SmileUtils.emoji_70,
            SmileUtils.emoji_71,
            SmileUtils.emoji_72,
            SmileUtils.emoji_73,
            SmileUtils.emoji_74,
            SmileUtils.emoji_75,
            SmileUtils.emoji_76,
            SmileUtils.emoji_77,
            SmileUtils.emoji_78,
            SmileUtils.emoji_79,
            SmileUtils.emoji_80,
            SmileUtils.emoji_81,
            SmileUtils.emoji_82,
            SmileUtils.emoji_83,
            SmileUtils.emoji_84,
            SmileUtils.emoji_85,
            SmileUtils.emoji_86,
            SmileUtils.emoji_87,
            SmileUtils.emoji_88,
            SmileUtils.emoji_89,
            SmileUtils.emoji_90,
            SmileUtils.emoji_91,
            SmileUtils.emoji_92,
            SmileUtils.emoji_93,
            SmileUtils.emoji_94,
            SmileUtils.emoji_95,
            SmileUtils.emoji_96,
            SmileUtils.emoji_97,
            SmileUtils.emoji_98,
            SmileUtils.emoji_99,
            SmileUtils.emoji_100,
            SmileUtils.emoji_101,
            SmileUtils.emoji_102,
            SmileUtils.emoji_103,
            SmileUtils.emoji_104,
            SmileUtils.emoji_105,
            SmileUtils.emoji_106,
            SmileUtils.emoji_107,

            SmileUtils.emoji_108,
            SmileUtils.emoji_109,
            SmileUtils.emoji_110,
            SmileUtils.emoji_111,
            SmileUtils.emoji_112,
            SmileUtils.emoji_113,
            SmileUtils.emoji_114,
            SmileUtils.emoji_115,
            SmileUtils.emoji_116,
            SmileUtils.emoji_117,
            SmileUtils.emoji_118,
            SmileUtils.emoji_119,
            SmileUtils.emoji_120,
            SmileUtils.emoji_121,
            SmileUtils.emoji_122,
            SmileUtils.emoji_123,
            SmileUtils.emoji_124,
            SmileUtils.emoji_125,
            SmileUtils.emoji_126,
            SmileUtils.emoji_127,
            SmileUtils.emoji_128,
            SmileUtils.emoji_129,
            SmileUtils.emoji_130,

            SmileUtils.emoji_131,


    };




    private static final Emojicon[] DATA = createData();

    private static Emojicon[] createData() {
        Emojicon[] datas = new Emojicon[emojis.length];
        for (int i = 0; i < emojis.length; i++) {
            datas[i] = new Emojicon(emojis[i], Emojicon.Type.NORMAL);
        }
        return datas;
    }





    public static Emojicon[] getData() {
        return DATA;
    }

}
