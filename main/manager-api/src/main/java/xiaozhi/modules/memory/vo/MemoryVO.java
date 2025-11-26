package xiaozhi.modules.memory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xiaozhi.modules.memory.entity.MemoryEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "记忆体VO")
public class MemoryVO extends MemoryEntity {
}
