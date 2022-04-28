package com.zhengshuo.phoenix.ui.chat.weight.emojicon;

import android.os.Parcel;
import android.os.Parcelable;

public class Emojicon implements Parcelable{
    public Emojicon(){
    }
    


    public Emojicon(String emojiText, Type type){
        this.emojiText = emojiText;
        this.type = type;
    }
    
    
    /**
     * identity code
     */
    private String identityCode;

    /**
     * text of emoji, could be null for big icon
     */
    private String emojiText;
    
    /**
     * name of emoji icon
     */
    private String name;
    
    /**
     * normal or big
     */
    private Type type;
    
    /**
     * path of icon
     */
    private String iconPath;
    
    /**
     * path of big icon
     */
    private String bigIconPath;


    protected Emojicon(Parcel in) {
        identityCode = in.readString();
        emojiText = in.readString();
        name = in.readString();
        iconPath = in.readString();
        bigIconPath = in.readString();
    }

    public static final Creator<Emojicon> CREATOR = new Creator<Emojicon>() {
        @Override
        public Emojicon createFromParcel(Parcel in) {
            return new Emojicon(in);
        }

        @Override
        public Emojicon[] newArray(int size) {
            return new Emojicon[size];
        }
    };




    /**
     * get text of emoji icon
     * @return
     */
    public String getEmojiText() {
        return emojiText;
    }


    /**
     * set text of emoji icon
     * @param emojiText
     */
    public void setEmojiText(String emojiText) {
        this.emojiText = emojiText;
    }

    /**
     * get name of emoji icon
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * set name of emoji icon
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get type
     * @return
     */
    public Type getType() {
        return type;
    }


    /**
     * set type
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }


    /**
     * get icon path
     * @return
     */
    public String getIconPath() {
        return iconPath;
    }


    /**
     * set icon path
     * @param iconPath
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }


    /**
     * get path of big icon
     * @return
     */
    public String getBigIconPath() {
        return bigIconPath;
    }


    /**
     * set path of big icon
     * @param bigIconPath
     */
    public void setBigIconPath(String bigIconPath) {
        this.bigIconPath = bigIconPath;
    }

    /**
     * get identity code
     * @return
     */
    public String getIdentityCode() {
        return identityCode;
    }
    
    /**
     * set identity code
     * @param
     */
    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }

    public static final String newEmojiText(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identityCode);
        dest.writeString(emojiText);
        dest.writeString(name);
        dest.writeString(iconPath);
        dest.writeString(bigIconPath);
    }


    public enum Type{
        /**
         * normal icon, can be input one or more in edit view
         */
        NORMAL,
        /**
         * big icon, send out directly when your press it
         */
        BIG_EXPRESSION
    }
}
