package com.example.edu_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.edu_project.dto.HandleReportRequest;
import com.example.edu_project.dto.ReportRequest;
import com.example.edu_project.entity.BlogReport;
import com.example.edu_project.vo.ReportVO;

/**
 * 举报服务接口
 */
public interface ReportService extends IService<BlogReport> {

    /**
     * 创建举报
     * @param request 举报请求
     * @param reporterId 举报人ID
     * @return 举报ID
     */
    Long createReport(ReportRequest request, Long reporterId);

    /**
     * 获取当前用户的举报记录（分页）
     * @param page 页码
     * @param pageSize 每页大小
     * @param reporterId 举报人ID
     * @return 分页举报列表
     */
    IPage<ReportVO> getMyReports(Integer page, Integer pageSize, Long reporterId);

    /**
     * 获取待处理的举报列表（管理员）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 待处理举报分页列表
     */
    IPage<ReportVO> getPendingReports(Integer page, Integer pageSize);

    /**
     * 获取举报详情（管理员）
     * @param reportId 举报ID
     * @return 举报详情
     */
    ReportVO getReportDetail(Long reportId);

    /**
     * 处理举报（管理员）
     * @param reportId 举报ID
     * @param request 处理请求
     * @param handlerId 处理人ID
     */
    void handleReport(Long reportId, HandleReportRequest request, Long handlerId);
}