package com.joya.pranksound.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageLoader {
    public static List<String> getImageListFromAssets(Context context, String folderName) {
        List<String> imageList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            // Liệt kê tất cả các tệp trong thư mục assets/prank_image/test_sound
            String[] files = assetManager.list(folderName);
            if (files != null) {
                for (String file : files) {
                    // Thêm đường dẫn của tệp vào danh sách
                    imageList.add(folderName + "/" + file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageList;
    }
}