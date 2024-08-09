package com.wa.pranksound.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "fav_prank_sound")
public class InsertPrankSound {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;
    @ColumnInfo(name = "folder_name")
    public String folder_name;
    @ColumnInfo(name = "sound_name")
    public String sound_name;

    @ColumnInfo(name = "image_path") // Tên cột cho đường dẫn ảnh
    public String image_path; // Trường lưu đường dẫn ảnh

    @ColumnInfo(name = "sound_path") // Tên cột cho đường dẫn ảnh
    public String sound_path; // Trường lưu đường dẫn ảnh




}
