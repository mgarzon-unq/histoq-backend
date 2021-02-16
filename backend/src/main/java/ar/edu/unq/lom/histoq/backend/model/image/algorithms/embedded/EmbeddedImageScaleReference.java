package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded;

import ar.edu.unq.lom.histoq.backend.model.image.algorithms.ImageScaleReference;
import lombok.Data;
import org.opencv.core.Point;
import org.opencv.core.Rect;

@Data
public class EmbeddedImageScaleReference extends ImageScaleReference {

    private Rect textRectangle;

    public EmbeddedImageScaleReference(){}

    public EmbeddedImageScaleReference(String measurementUnit,
                                Integer scaleValue,
                                Integer scalePixels,
                                Rect textRectangle) {
        super(measurementUnit,scaleValue,scalePixels);
        this.setTextRectangle(textRectangle);
    }

    public Point getTextRectXY() {
        Rect rect = getTextRectangle();
        return ( rect!=null ?
                new Point(rect.x, rect.y)
                : new Point(0,0) );
    }
}
