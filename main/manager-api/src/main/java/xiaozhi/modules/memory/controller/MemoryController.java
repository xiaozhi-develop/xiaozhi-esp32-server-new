package xiaozhi.modules.memory.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.memory.dto.MemoryDTO;
import xiaozhi.modules.memory.service.MemoryService;
import xiaozhi.modules.memory.vo.MemoryVO;
import xiaozhi.modules.security.user.SecurityUser;

@RestController
@RequestMapping("memory")
@Tag(name = "记忆体管理")
@AllArgsConstructor
public class MemoryController {
    private final MemoryService memoryService;

    @GetMapping("list")
    @Operation(summary = "获取用户记忆体列表")
    public Result<List<MemoryVO>> list() {
        Long userId = SecurityUser.getUserId();
        List<MemoryVO> list = memoryService.getUserMemories(userId);
        return new Result<List<MemoryVO>>().ok(list);
    }

    @GetMapping("{id}")
    @Operation(summary = "获取记忆体信息")
    public Result<MemoryVO> get(@PathVariable("id") String id) {
        MemoryVO data = memoryService.get(id);
        return new Result<MemoryVO>().ok(data);
    }

    @PostMapping
    @Operation(summary = "保存记忆体")
    public Result save(@RequestBody MemoryDTO dto) {
        memoryService.save(dto);
        return new Result();
    }

    @PutMapping
    @Operation(summary = "修改记忆体")
    public Result update(@RequestBody MemoryDTO dto) {
        memoryService.update(dto);
        return new Result();
    }

    @PutMapping("content/{id}")
    @Operation(summary = "更新记忆体内容")
    public Result updateContent(@PathVariable("id") String id, @RequestBody MemoryDTO dto) {
        memoryService.updateSummary(id, dto.getSummaryMemory());
        return new Result();
    }

    @DeleteMapping
    @Operation(summary = "删除记忆体")
    public Result delete(@RequestBody String[] ids) {
        memoryService.delete(ids);
        return new Result();
    }
}
