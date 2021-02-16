package ar.edu.unq.lom.histoq.backend.model.image.algorithms.scripted;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.TissueData;
import lombok.Data;

@Data
public class ScriptedTissueData implements TissueData {
    private Double totalArea;
    private Double totalTissueArea;
    private Double viableTissueArea;
    private Double necroticTissueArea;

    public ScriptedTissueData(){}

    public ScriptedTissueData(Double totalArea,
                      Double totalTissueArea,
                      Double viableTissueArea,
                      Double necroticTissueArea ) {
        this.setTotalArea(totalArea);
        this.setTotalTissueArea(totalTissueArea);
        this.setViableTissueArea(viableTissueArea);
        this.setNecroticTissueArea(necroticTissueArea);
    }
}

