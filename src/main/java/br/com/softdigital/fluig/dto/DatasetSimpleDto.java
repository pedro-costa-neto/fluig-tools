package br.com.softdigital.fluig.dto;

import com.totvs.technology.ecm.dataservice.ws.ValuesDto;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

/**
 * Dataset Simple Dto
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0, 28/09/2020
 */
@Data
public class DatasetSimpleDto {
    //private ObservableList<TableColumn<ValuesDto, String>> columns;    
    private List<String> columns;
    private List<ValuesDto> items;
    
    public ObservableList<String> getObservableColumns() {
        return  FXCollections.observableArrayList(columns);
    }
    
    public ObservableList<ValuesDto> getObservableItems() {
        return  FXCollections.observableArrayList(items);
    }
}
