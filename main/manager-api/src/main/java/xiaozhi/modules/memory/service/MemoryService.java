package xiaozhi.modules.memory.service;

import java.util.List;

import xiaozhi.common.service.BaseService;
import xiaozhi.modules.memory.dto.MemoryDTO;
import xiaozhi.modules.memory.entity.MemoryEntity;
import xiaozhi.modules.memory.vo.MemoryVO;

public interface MemoryService extends BaseService<MemoryEntity> {
    
    /**
     * 保存记忆体
     * @param dto
     */
    void save(MemoryDTO dto);

    /**
     * 更新记忆体
     * @param dto
     */
    void update(MemoryDTO dto);

    /**
     * 删除记忆体
     * @param ids
     */
    void delete(String[] ids);

    /**
     * 获取记忆体详情
     * @param id
     * @return
     */
    MemoryVO get(String id);

    /**
     * 获取用户记忆体列表
     * @param userId
     * @return
     */
    List<MemoryVO> getUserMemories(Long userId);

    /**
     * 更新记忆体摘要
     * @param id
     * @param summary
     */
    void updateSummary(String id, String summary);
}
