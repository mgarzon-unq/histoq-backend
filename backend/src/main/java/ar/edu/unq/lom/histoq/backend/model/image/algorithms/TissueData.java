package ar.edu.unq.lom.histoq.backend.model.image.algorithms;

import lombok.Data;

public interface TissueData {
    public Double getTotalArea();
    public Double getTotalTissueArea();
    public Double getViableTissueArea();
    public Double getNecroticTissueArea();
}
