package com.example.edu_project.dto;

import lombok.Data;
import java.util.List;

@Data
public class SaveDraftRequest {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private List<Long> tagIds;
}