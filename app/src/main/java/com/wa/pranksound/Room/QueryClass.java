package com.wa.pranksound.Room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QueryClass {

    @Insert
    void insertPrankSound(InsertPrankSound... createDevices);

    @Query("SELECT * FROM fav_prank_sound")
    List<InsertPrankSound> getAllFavSound();

    @Query("SELECT * FROM fav_prank_sound WHERE folder_name=:folder_name1 AND sound_name=:sound_name1")
    InsertPrankSound getFavSound(String folder_name1,String sound_name1);

    @Query("DELETE FROM fav_prank_sound WHERE folder_name=:folder_name1 AND sound_name=:sound_name1")
    void getUnFavSound(String folder_name1,String sound_name1);

    @Query("SELECT folder_name FROM fav_prank_sound")
    List<String> getAllFolderNames();

    @Query("SELECT * FROM fav_prank_sound WHERE folder_name=:folder_name1 AND sound_name=:sound_name1 AND image_path=:image_path1")
    InsertPrankSound getFavSound1(String folder_name1,String sound_name1,String image_path1);

   /* @Query("SELECT * FROM question WHERE topic_details_id=:topic_details_id AND chapter_id=:chapter_id")
    CreateQuestion getAllQuestion(int topic_details_id, int chapter_id);*/


}
