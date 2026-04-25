package com.example.edu_project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 收藏状态响应VO
 */
@Data
@Schema(description = "收藏状态响应")
public class CollectStatusVO {

    @Schema(description = "是否已收藏")
    private Boolean collected;
}
