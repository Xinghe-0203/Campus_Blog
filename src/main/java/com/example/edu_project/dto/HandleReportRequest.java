package com.example.edu_project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 处理举报请求 DTO
 */
@Data
@Schema(description = "处理举报请求")
public class HandleReportRequest {

    @Schema(description = "处理状态：1=已驳回，2=已核实", required = true)
    @NotNull(message = "处理状态不能为空")
    private Integer status;

    @Schema(description = "处理结果说明")
    @Size(max = 500, message = "处理结果说明不能超过500字符")
    private String handlerResult;
}