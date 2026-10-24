package ec.fin.be.sigered.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RowBackupDto {
    int rowIndex;
    Float height;
    List<CellBackupDto> cells = new ArrayList<>();
}