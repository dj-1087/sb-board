package kr.co.promptech.sbboard.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum FileType {

    IMAGE("/images", new ArrayList<>(Arrays.asList("image/gif","image/jpeg","image/png"))),
    VIDEO("/videos", new ArrayList<>(Arrays.asList("video/mpeg","video/mp4"))),
    DOC("/docs", new ArrayList<>(Arrays.asList("application/msword", "application/pdf"))),
    ETC("/etc", new ArrayList<>());


    private final String path;
    private final ArrayList<String> contentTypeList;

    public static String classifyPath(String contentType) {
        for (FileType fileType :
                FileType.values()) {
            if (fileType.contentTypeList.contains(contentType)) {
                return fileType.path;
            }
        }
        return ETC.path;
    }
}
