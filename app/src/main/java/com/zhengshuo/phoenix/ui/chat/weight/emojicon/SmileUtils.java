/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhengshuo.phoenix.ui.chat.weight.emojicon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.zhengshuo.phoenix.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmileUtils {
    public static final String DELETE_KEY = "em_delete_delete_expression";
    

  	public static final String emoji_001 = "\uD83D\uDE03";
	public static final String emoji_002 = "\uD83D\uDE00";
	public static final String emoji_003 = "\uD83D\uDE0A";
	public static final String emoji_004 = "☺";
	public static final String emoji_005 = "\uD83D\uDE09";
	public static final String emoji_006 = "\uD83D\uDE0D";
	public static final String emoji_007 = "\uD83D\uDE18";
	public static final String emoji_008 = "\uD83D\uDE1A";
	public static final String emoji_009 = "\uD83D\uDE1C";
	public static final String emoji_010 = "\uD83D\uDE1D";
	public static final String emoji_11 = "\uD83D\uDE33";
	public static final String emoji_12 = "\uD83D\uDE01";
	public static final String emoji_13 = "\uD83D\uDE14";
	public static final String emoji_14 = "\uD83D\uDE0C";
	public static final String emoji_15 = "\uD83D\uDE12";
	public static final String emoji_16 = "\uD83D\uDE1F";
	public static final String emoji_17 = "\uD83D\uDE1E";
	public static final String emoji_18 = "\uD83D\uDE23";
	public static final String emoji_19 = "\uD83D\uDE22";
	public static final String emoji_20 = "\uD83D\uDE02";
	public static final String emoji_21 = "\uD83D\uDE2D";
	public static final String emoji_22 = "\uD83D\uDE2A";
	public static final String emoji_23 = "\uD83D\uDE30";
	public static final String emoji_24 = "\uD83D\uDE05";
	public static final String emoji_25 = "\uD83D\uDE13";
	public static final String emoji_26 = "\uD83D\uDE2B";
	public static final String emoji_27 = "\uD83D\uDE29";
	public static final String emoji_28 = "\uD83D\uDE28";
	public static final String emoji_29 = "\uD83D\uDE31";
	public static final String emoji_30 = "\uD83D\uDE21";
	public static final String emoji_31 = "\uD83D\uDE24";
	public static final String emoji_32 = "\uD83D\uDE16";
	public static final String emoji_33 = "\uD83D\uDE06";
	public static final String emoji_34 = "\uD83D\uDE0B";
	public static final String emoji_35 = "\uD83D\uDE37";
	public static final String emoji_36 = "\uD83D\uDE0E";
	public static final String emoji_37 = "\uD83D\uDE34";
	public static final String emoji_38 = "\uD83D\uDE32";
	public static final String emoji_39 = "\uD83D\uDE35";
	public static final String emoji_40 = "\uD83D\uDE08";
	public static final String emoji_41 = "\uD83D\uDC7F";
	public static final String emoji_42 = "\uD83D\uDE2F";
	public static final String emoji_43 = "\uD83D\uDE2C";

 	public static final String emoji_45 = "\uD83D\uDE15";
	public static final String emoji_46 = "\uD83D\uDE36";
	public static final String emoji_47 = "\uD83D\uDE07";
	public static final String emoji_48 = "\uD83D\uDE0F";
	public static final String emoji_49 = "\uD83D\uDE11";
	public static final String emoji_50 = "\uD83D\uDE48";
	public static final String emoji_51 = "\uD83D\uDE49";
	public static final String emoji_52 = "\uD83D\uDE4A";
	public static final String emoji_53 = "\uD83D\uDC7D";
	public static final String emoji_54 = "\uD83D\uDCA9";
	public static final String emoji_55 = "❤️";
	public static final String emoji_56 = "\uD83D\uDC94";
	public static final String emoji_57 = "\uD83D\uDD25";

	public static final String emoji_58 = "\uD83D\uDCA2";
	public static final String emoji_59 = "\uD83D\uDCA4";
	public static final String emoji_60 = "\uD83D\uDEAB";
	public static final String emoji_61 = "⭐";
	public static final String emoji_62 = "⚡";
	public static final String emoji_63 = "\uD83C\uDF19";
	public static final String emoji_64 = "☀";
	public static final String emoji_65 = "⛅";
	public static final String emoji_66 = "☁";
	public static final String emoji_67 = "❄";
	public static final String emoji_68 = "☔";
	public static final String emoji_69 = "⛄";
	public static final String emoji_70 = "\uD83D\uDC4D";
	public static final String emoji_71 = "\uD83D\uDC4E";

	public static final String emoji_72 = "\uD83E\uDD1D";
	public static final String emoji_73 = "\uD83D\uDC4C";
	public static final String emoji_74 = "\uD83D\uDC4A";
	public static final String emoji_75 = "✊";
	public static final String emoji_76 = "✌";
	public static final String emoji_77 = "✋";
	public static final String emoji_78 = "\uD83D\uDE4F";
	public static final String emoji_79 = "☝";
	public static final String emoji_80 = "\uD83D\uDC4F";
	public static final String emoji_81 = "\uD83D\uDCAA";
	public static final String emoji_82 = "\uD83D\uDC6A";
	public static final String emoji_83 = "\uD83D\uDC6B";
	public static final String emoji_84 = "\uD83D\uDC7C";
	public static final String emoji_85 = "\uD83D\uDC34";

	public static final String emoji_86 = "\uD83D\uDC36";
	public static final String emoji_87 = "\uD83D\uDC37";
	public static final String emoji_88 = "\uD83D\uDC7B";
	public static final String emoji_89 = "\uD83C\uDF39";
	public static final String emoji_90 = "\uD83C\uDF3B";
	public static final String emoji_91 = "\uD83C\uDF32";
	public static final String emoji_92 = "\uD83C\uDF84";
	public static final String emoji_93 = "\uD83C\uDF81";
	public static final String emoji_94 = "\uD83C\uDF89";
	public static final String emoji_95 = "\uD83D\uDCB0";
	public static final String emoji_96 = "\uD83C\uDF82";
	public static final String emoji_97 = "\uD83C\uDF56";
	public static final String emoji_98 = "\uD83C\uDF5A";
	public static final String emoji_99 = "\uD83C\uDF66";

	public static final String emoji_100 = "\uD83C\uDF6B";
	public static final String emoji_101 = "\uD83C\uDF49";
	public static final String emoji_102 = "\uD83C\uDF77";
	public static final String emoji_103 = "\uD83C\uDF7B";
	public static final String emoji_104 = "☕";
	public static final String emoji_105 = "\uD83C\uDFC0";
	public static final String emoji_106 = "⚽";
	public static final String emoji_107 = "\uD83C\uDFC2";
	public static final String emoji_108 = "\uD83C\uDFA4";
	public static final String emoji_109 = "\uD83C\uDFB5";
	public static final String emoji_110 = "\uD83C\uDFB2";
	public static final String emoji_111 = "\uD83C\uDC04";
	public static final String emoji_112 = "\uD83D\uDC51";
	public static final String emoji_113 = "\uD83D\uDC84";

	public static final String emoji_114 = "\uD83D\uDC8B";
	public static final String emoji_115 = "\uD83D\uDC8D";
	public static final String emoji_116 = "\uD83D\uDCDA";
	public static final String emoji_117 = "\uD83C\uDF93";
	public static final String emoji_118 = "✏";
	public static final String emoji_119 = "\uD83C\uDFE1";
	public static final String emoji_120 = "\uD83D\uDEBF";
	public static final String emoji_121 = "\uD83D\uDCA1";
	public static final String emoji_122 = "\uD83D\uDCDE";
	public static final String emoji_123 = "\uD83D\uDCE2";
	public static final String emoji_124 = "\uD83D\uDD56";
	public static final String emoji_125 = "⏰";
	public static final String emoji_126 = "⏳";
	public static final String emoji_127 = "\uD83D\uDCA3";
	public static final String emoji_128 = "\uD83D\uDD2B";
	public static final String emoji_129 = "\uD83D\uDC8A";
	public static final String emoji_130 = "\uD83D\uDE80";
	public static final String emoji_131 = "\uD83C\uDF0F";


	private static final Factory spannableFactory = Factory
	        .getInstance();
	
	private static final Map<Pattern, Object> emoticons = new HashMap<Pattern, Object>();


	static {
	    Emojicon[] emojicons = EmojiconDatas.getData();
	    for(int i = 0; i < emojicons.length; i++){
	        addPattern(emojicons[i].getEmojiText(), 1);
	    }


	}

	/**
	 * add text and icon to the map
	 * @param emojiText-- text of emoji
	 * @param icon -- resource id or local path
	 */
	public static void addPattern(String emojiText, Object icon){
	    emoticons.put(Pattern.compile(Pattern.quote(emojiText)), icon);
	}


	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                Object value = entry.getValue();
	                if(value instanceof String && !((String) value).startsWith("http")){
	                    File file = new File((String) value);
	                    if(!file.exists() || file.isDirectory()){
	                        return false;
	                    }
	                    ImageSpan imageSpan=new ImageSpan(context, Uri.fromFile(file));
						Drawable drawable=imageSpan.getDrawable();
						drawable.setBounds(0,0,25,25);
						imageSpan=new ImageSpan(drawable);
	                    spannable.setSpan(imageSpan,
	                            matcher.start(), matcher.end(),
	                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }
	            }
	        }
	    }

	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}
	
	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Object> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	public static int getSmilesSize(){
        return emoticons.size();
    }
    
	
}
