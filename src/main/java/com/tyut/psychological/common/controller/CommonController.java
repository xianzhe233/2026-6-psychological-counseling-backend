package com.tyut.psychological.common.controller;

import com.tyut.psychological.common.api.Result;
import com.tyut.psychological.common.entity.ProblemType;
import com.tyut.psychological.common.mapper.ProblemTypeMapper;
import com.tyut.psychological.common.vo.OptionVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公共接口控制器
 * 提供问题类型等公共字典选项
 */
@RestController
@RequestMapping("/api/common")
public class CommonController {

    private final ProblemTypeMapper problemTypeMapper;

    public CommonController(ProblemTypeMapper problemTypeMapper) {
        this.problemTypeMapper = problemTypeMapper;
    }

    /**
     * 获取问题类型选项列表
     * 用于首访结果、结案报告、统计筛选等页面的下拉选择
     */
    @GetMapping("/problem-types/options")
    public Result<List<OptionVO>> getProblemTypeOptions() {
        List<ProblemType> types = problemTypeMapper.selectAllEnabled();
        List<OptionVO> options = types.stream()
                .map(t -> new OptionVO(t.getTypeName(), t.getId()))
                .toList();
        return Result.success(options);
    }
}
