package ec.fin.be.sigered.dto;

import org.apache.poi.ss.usermodel.CellStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CellBackupDto {
    int row;
    int col;
    String value;
    CellStyle style;
}

