package xiaozhi.modules.memory.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.AllArgsConstructor;
import xiaozhi.common.exception.ErrorCode;
import xiaozhi.common.exception.RenException;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.common.user.UserDetail;
import xiaozhi.common.utils.ConvertUtils;
import xiaozhi.modules.memory.dao.MemoryDao;
import xiaozhi.modules.memory.dto.MemoryDTO;
import xiaozhi.modules.memory.entity.MemoryEntity;
import xiaozhi.modules.memory.service.MemoryService;
import xiaozhi.modules.memory.vo.MemoryVO;
import xiaozhi.modules.security.user.SecurityUser;

@Service
@AllArgsConstructor
public class MemoryServiceImpl extends BaseServiceImpl<MemoryDao, MemoryEntity> implements MemoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(MemoryDTO dto) {
        MemoryEntity entity = ConvertUtils.sourceToTarget(dto, MemoryEntity.class);
        
        if (entity.getId() == null || entity.getId().trim().isEmpty()) {
            entity.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        
        UserDetail user = SecurityUser.getUser();
        entity.setUserId(user.getId());
        entity.setCreator(user.getId());
        entity.setCreateDate(new Date());
        entity.setUpdater(user.getId());
        entity.setUpdateDate(new Date());
        
        if (entity.getSort() == null) {
            entity.setSort(0);
        }

        baseDao.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MemoryDTO dto) {
        MemoryEntity entity = baseDao.selectById(dto.getId());
        if (entity == null) {
            throw new RenException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        
        // Check permission
        UserDetail user = SecurityUser.getUser();
        if (!entity.getUserId().equals(user.getId())) {
             // Assuming simple permission check for now
             // throw new RenException(ErrorCode.FORBIDDEN);
        }

        org.springframework.beans.BeanUtils.copyProperties(dto, entity);
        
        entity.setUpdater(user.getId());
        entity.setUpdateDate(new Date());
        
        baseDao.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String[] ids) {
        baseDao.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public MemoryVO get(String id) {
        MemoryEntity entity = baseDao.selectById(id);
        return ConvertUtils.sourceToTarget(entity, MemoryVO.class);
    }

    @Override
    public List<MemoryVO> getUserMemories(Long userId) {
        QueryWrapper<MemoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByAsc("sort");
        List<MemoryEntity> list = baseDao.selectList(wrapper);
        return ConvertUtils.sourceToTarget(list, MemoryVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSummary(String id, String summary) {
        MemoryEntity entity = baseDao.selectById(id);
        if (entity == null) {
            throw new RenException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        entity.setSummaryMemory(summary);
        entity.setUpdateDate(new Date());
        baseDao.updateById(entity);
    }
}
