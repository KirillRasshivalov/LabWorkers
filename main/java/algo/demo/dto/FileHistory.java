package algo.demo.dto;

import lombok.Builder;

@Builder
public record FileHistory(String status, String user, Long numOfAdds, String fileName, String fileObjectName) {}
