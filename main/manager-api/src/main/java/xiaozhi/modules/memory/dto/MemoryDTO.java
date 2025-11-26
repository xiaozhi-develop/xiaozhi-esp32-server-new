package xiaozhi.modules.memory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "记忆体DTO")
public class MemoryDTO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "记忆体名称")
    private String name;

    @Schema(description = "记忆模型标识")
    private String memModelId;

    @Schema(description = "总结记忆")
    private String summaryMemory;

    @Schema(description = "排序")
    private Integer sort;
}
