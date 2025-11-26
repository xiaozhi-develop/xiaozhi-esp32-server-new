package xiaozhi.modules.memory.dao;

import org.apache.ibatis.annotations.Mapper;
import xiaozhi.common.dao.BaseDao;
import xiaozhi.modules.memory.entity.MemoryEntity;

@Mapper
public interface MemoryDao extends BaseDao<MemoryEntity> {
}
